package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.media.pojos.WmUser;
import com.heima.model.wemedia.dtos.WmUserDto;

public interface WmUserService extends IService<WmUser> {
    /**
     * 保存自媒体用户
     *
     * @param wmUser
     * @return
     */
    public ResponseResult insert(WmUser wmUser);

    /**
     * 按照名称查询用户
     *
     * @param name
     * @return
     */
    public ResponseResult findByName(String name);

    /**
     * 登录
     * @param dto
     * @return
     */
    public ResponseResult login(WmUserDto dto);
}
