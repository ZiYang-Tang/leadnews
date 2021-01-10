package com.heima.artcle.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.artcle.mapper.ApArticleConfigMapper;
import com.heima.artcle.mapper.ApArticleContentMapper;
import com.heima.artcle.service.ArticleInfoService;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ArticleInfoServiceImpl implements ArticleInfoService {

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Override
    public ResponseResult loadArticleInfo(ArticleInfoDto dto) {
        HashMap<String, Object> resuletMap = new HashMap<>();
        //1.检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2。根据id查询文章配置信息
        ApArticleConfig apArticleConfig = apArticleConfigMapper.selectOne(Wrappers.<ApArticleConfig>lambdaQuery().eq(ApArticleConfig::getArticleId, dto.getArticleId()));
        if (apArticleConfig == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //3.根据文章id查询文章内容,判断文章的状态，如果已被删除或者下架，那么不查询
        if (!apArticleConfig.getIsDelete() && !apArticleConfig.getIsDown()) {
            ApArticleContent articleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getArticleId()));
            resuletMap.put("content", articleContent);
        }
        resuletMap.put("config", apArticleConfig);

        //返回结果
        return ResponseResult.okResult(resuletMap);
    }
}
