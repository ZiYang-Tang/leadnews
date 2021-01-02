package com.heima.artcle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.ResponseResult;

public interface AuthorService extends IService<ApAuthor> {
    /**
     * 根据用户id查询作者信息
     *
     * @param userId
     * @return
     */
    public ResponseResult findByUserId(Integer userId);

    /**
     * 保存作者
     *
     * @param apAuthor
     * @return
     */
    public ResponseResult insert(ApAuthor apAuthor);
}
