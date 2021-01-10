package com.heima.artcle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

public interface ApArticleService extends IService<ApArticle> {
    /**
     * 根据参数加载文章列表
     * @param loadType 1加载更多  2 加载最新
     * @param dto
     * @return
     */
    public ResponseResult load(ArticleHomeDto dto, Short loadType);
}
