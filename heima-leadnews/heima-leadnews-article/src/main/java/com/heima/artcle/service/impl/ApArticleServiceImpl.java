package com.heima.artcle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.artcle.mapper.ApArticleMapper;
import com.heima.artcle.service.ApArticleService;
import com.heima.model.article.pojos.ApArticle;
import org.springframework.stereotype.Service;

@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
}
