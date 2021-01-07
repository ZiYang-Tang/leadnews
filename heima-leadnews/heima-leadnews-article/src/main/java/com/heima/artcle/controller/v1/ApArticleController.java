package com.heima.artcle.controller.v1;

import com.heima.api.article.ApArticleControllerApi;
import com.heima.artcle.service.ApArticleService;
import com.heima.model.article.pojos.ApArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/article")
public class ApArticleController implements ApArticleControllerApi {

    @Autowired
    private ApArticleService apArticleService;

    @PostMapping("/save")
    @Override
    public ApArticle saveArticle(@RequestBody ApArticle apArticle) {
        apArticleService.save(apArticle);
        return apArticle;
    }
}
