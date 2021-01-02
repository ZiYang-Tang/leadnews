package com.heima.api.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmUserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "登录", tags = "wemedia", description = "登陆管理")
public interface LoginControllerApi {

    /**
     * 自媒体登录
     * @param dto
     * @return
     */
    @ApiOperation("自媒体人登录功能")
    public ResponseResult login(WmUserDto dto);
}