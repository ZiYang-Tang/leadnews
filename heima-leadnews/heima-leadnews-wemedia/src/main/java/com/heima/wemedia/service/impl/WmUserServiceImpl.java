package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.model.wemedia.dtos.WmUserDto;
import com.heima.utils.common.AppJwtUtil;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmUserService;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
@Service
@Transactional
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ResponseResult insert(WmUser wmUser) {
        //1.验证参数
        if (wmUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.保存
        save(wmUser);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult findByName(String name) {
        //1.验证参数
        if (name == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.查找
        WmUser wmUser = getOne(new QueryWrapper<WmUser>().eq("name", name));
        return ResponseResult.okResult(wmUser);
    }

    public ResponseResult login(WmUserDto dto) {

        //校验参数
        if (StringUtils.isEmpty(dto.getName()) || StringUtils.isEmpty(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"用户名或密码错误");
        }

        //查询数据库中的用户信息
        List<WmUser> list = list(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getName,dto.getName()));
        if (list != null || list.size()==1) {

            WmUser wmUser = list.get(0);

            //比对密码
            String pwd = DigestUtils.md5DigestAsHex((dto.getPassword() + wmUser.getSalt()).getBytes());

            if (wmUser.getPassword().equals(pwd)){
                //返回数据jwt
                Map<String, Object> map = new HashMap<>();

                //获取jti
                String token = AppJwtUtil.getToken(wmUser.getId().longValue());
                Claims claims = AppJwtUtil.getClaimsBody(token);
                Object jti = claims.get("jti");

                //将jti保存到redis
                redisTemplate.boundValueOps(jti).set(token,7, TimeUnit.DAYS);

                //添加token到集合
                //map.put("token", AppJwtUtil.getToken(wmUser.getId().longValue()));
                map.put("token", jti);
                wmUser.setPassword("");
                wmUser.setSalt("");
                map.put("user",wmUser);

                return ResponseResult.okResult(map);
            }else {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }

        }else {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR,"用户不存在");
        }

    }


}
