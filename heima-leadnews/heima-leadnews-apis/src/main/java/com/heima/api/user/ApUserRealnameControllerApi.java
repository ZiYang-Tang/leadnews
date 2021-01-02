package com.heima.api.user;

import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.AuthDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "用户管理", tags = "UserRealname", description = "用户管理API")
public interface ApUserRealnameControllerApi {
    /**
     * 按照状态查询认证列表
     * @param dto
     * @return
     */
    @ApiOperation("按照状态查询认证列表")
    public PageResponseResult loadListByStatus(AuthDto dto);

    /**
     * 审核通过
     * @param dto
     * @return
     */
    public ResponseResult authPass(AuthDto dto) ;

    /**
     * 审核失败
     * @param dto
     * @return
     */
    public ResponseResult authFail(AuthDto dto);
}
