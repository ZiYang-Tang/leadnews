package com.heima.api.admin;


import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 登录控制层接口
 * @author ziyang
 */
public interface LoginControllerapi {
    /**
     * admin登录程序
     * @param dto
     * @return
     */
    public ResponseResult login(@RequestBody AdUserDto dto);

}