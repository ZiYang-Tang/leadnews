package com.heima.api.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;

/**
 * 自媒体文章接口
 */
public interface WmNewsControllerApi {

    /**
     * 分页带条件查询自媒体文章列表
     *
     * @param wmNewsPageReqDto
     * @return
     */
    public ResponseResult findAll(WmNewsPageReqDto wmNewsPageReqDto);

    /**
     * 提交文章或保存草稿
     *
     * @param wmNews
     * @return
     */
    ResponseResult summitNews(WmNewsDto wmNews);

    /**
     * 根据id获取文章信息
     *
     * @param id
     * @return
     */
    ResponseResult findWmNewsById(Integer id);

    /**
     * 根据id删除文章
     *
     * @param id
     * @return
     */
    ResponseResult delNews(Integer id);

    /**
     * 上下架
     *
     * @param dto
     * @return
     */
    ResponseResult downOrUp(WmNewsDto dto);
}