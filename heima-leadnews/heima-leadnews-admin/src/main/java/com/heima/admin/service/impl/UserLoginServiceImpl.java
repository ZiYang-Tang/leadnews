package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.UserMapper;
import com.heima.admin.service.UserLoginService;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserLoginServiceImpl extends ServiceImpl<UserMapper, AdUser> implements UserLoginService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ResponseResult login(AdUserDto dto) {
        //1.校验参数
        if (StringUtils.isEmpty(dto.getName()) || StringUtils.isEmpty(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "用户名或密码不能为空");
        }

        //2.根据用户名在数据库中找到对象
//       获得一个集合  List<AdUser> list = list(new QueryWrapper<AdUser>().eq("name", dto.getName()));

//       获得一个对象 QueryWrapper<AdUser> wrapper = new QueryWrapper<AdUser>();
//        wrapper.eq("name",dto.getName());
        //获得对象
        AdUser adUser = getOne(new QueryWrapper<AdUser>().eq("name", dto.getName()));
        if (adUser != null) {
            //说明账号正确
            //3.对比加密后的密码
            String pswd = DigestUtils.md5DigestAsHex((dto.getPassword() + adUser.getSalt()).getBytes());
            if (adUser.getPassword().equals(pswd)) {
                //登陆成功,生成token
                adUser.setPassword("");
                adUser.setSalt("");

                // 获取jti :token 短标识
                String token = AppJwtUtil.getToken(adUser.getId().longValue());
                Claims claims = AppJwtUtil.getClaimsBody(token);
                String jti = (String) claims.get("jti");
                //将token保存到redis中
                redisTemplate.boundValueOps(jti).set(token, 7, TimeUnit.DAYS);
                //将jti返回给用户
                HashMap<String, Object> map = new HashMap<>();
                map.put("token", jti);
                map.put("user", adUser);

                //4.返回结果
                return ResponseResult.okResult(map);
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
        } else {
            //说明没找到这个对象
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
        }

    }
}
