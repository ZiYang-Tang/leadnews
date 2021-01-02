package com.heima.api.admin;

import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
/**
 * 频道控制层接口
 * @author ziyang
 */
@Api(value = "频道管理", tags = "channel", description = "频道管理API")
public interface AdChannelControllerApi {

    /**
     * 根据名称分页查询频道列表
     * @param dto
     * @return
     */
    @ApiOperation("频道分页列表查询")
    public ResponseResult findByNameAndPage(ChannelDto dto);

    /**
     * 新增频道
     * @param adChannel
     * @return
     */
    @ApiOperation("新增频道")
    public ResponseResult insertChannel(AdChannel adChannel);

    /**
     * 修改频道
     * @param adChannel
     * @return
     */
    @ApiOperation("修改频道")
    public ResponseResult updateChannel(AdChannel adChannel);

    /**
     * 根据id修改删除频道
     * @param id
     * @return
     */
    @ApiOperation("删除频道")
    public ResponseResult deleteChannelById(Integer id);
}