package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.media.pojos.WmUser;
import com.heima.model.wemedia.dtos.WmUserDto;
import com.heima.utils.common.AppJwtUtil;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;

@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {


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

    @Override
    public ResponseResult login(WmUserDto dto) {
        //1. 参数校验
        if (StringUtils.isEmpty(dto.getName()) || StringUtils.isEmpty(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "用户名或密码不匹配！");
        }
        //2.查询数据库中中的数据库信息
        WmUser wmUser = getOne(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getName, dto.getName()));
        if (wmUser != null) {
            //将dto传过来的数据，加盐加密，在和数据库的密码对比
            String pswd = DigestUtils.md5DigestAsHex((dto.getPassword() + wmUser.getSalt()).getBytes());
            if (pswd.equals(wmUser.getPassword())){
                //说明密码正确，生成token，再获得jwt并返回
                HashMap<String, Object> map = new HashMap<>();
                map.put("token", AppJwtUtil.getToken(wmUser.getId().longValue()));
                //将敏感信息置为空，
                wmUser.setPassword("");
                wmUser.setSalt("");
                map.put("name",wmUser.getName());
                // 返回jwt
                return ResponseResult.okResult(map);
            }else {
                //说明密码错误
                return  ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
        }else {
            //说明 数据库中查询不到这个用户
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户不存在！");
        }
    }
}
