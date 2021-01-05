package com.heima.api.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.model.wemedia.dtos.WmUserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;

@Api(value = "自媒体用户管理", tags = "WmUser", description = "自媒体用户管理API")
public interface WmUserControllerApi {

    /**
     * 保存自媒体用户
     * @param wmUser
     * @return
     */
    @ApiOperation("保存自媒体用户")
    public ResponseResult save(WmUser wmUser);

    /**
     * 按照名称查询用户
     * @param name
     * @return
     */
    @ApiOperation("按照名称查询用户")
    public ResponseResult findByName(String name);

    /**
     * 根据id查询自媒体用户
     * @param id
     * @return
     */
    WmUser findWmUserById(Long id);
}