package com.heima.artcle.controller.v1;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.api.article.AuthorControllerApi;
import com.heima.artcle.service.AuthorService;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/author")
public class AuthorController implements AuthorControllerApi {
    @Autowired
    private AuthorService authorService;

    @GetMapping("/findByUserId/{id}")
    @Override
    public ResponseResult findByUserId(@PathVariable("id") Integer userId) {
        return authorService.findByUserId(userId);
    }

    @PostMapping("/save")
    @Override
    public ResponseResult save(@RequestBody ApAuthor apAuthor) {
        return authorService.insert(apAuthor);
    }


    @GetMapping("/findByName/{name}")
    @Override
    public ApAuthor findByName(@PathVariable("name") String name) {
        return authorService.getOne(Wrappers.<ApAuthor>lambdaQuery().eq(ApAuthor::getName, name));
    }

    @GetMapping("/one/{id}")
    @Override
    public ApAuthor findById(@PathVariable("id") Integer id) {
        return authorService.getById(id);
    }
}
