package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.common.dtos.ResponseResult;

public interface AdChannelService extends IService<AdChannel> {
    /**
     * 根据名称分页查询频道列表
     * @param dto
     * @return
     */
    public ResponseResult findByNameAndPage(ChannelDto dto);

    /**
     * 新增频道
     * @param adChannel
     * @return
     */
    public ResponseResult insertChannel(AdChannel adChannel);

    /**
     * 修改频道
     * @param adChannel
     * @return
     */
    public ResponseResult updateChannel(AdChannel adChannel);

    /**
     * 根据id删除频道
     * @param id
     * @return
     */
    public ResponseResult deleteChannelById(Integer id);
}
