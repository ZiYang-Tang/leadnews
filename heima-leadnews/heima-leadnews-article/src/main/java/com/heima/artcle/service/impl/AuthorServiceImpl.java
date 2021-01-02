package com.heima.artcle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.SelectById;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.artcle.mapper.ApAuthorMapper;
import com.heima.artcle.service.AuthorService;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 作者业务层
 */
@Service
public class AuthorServiceImpl extends ServiceImpl<ApAuthorMapper, ApAuthor> implements AuthorService {
    @Override
    public ResponseResult findByUserId(Integer userId) {
        //检查参数
        if (userId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //根据id查询作者信息
        ApAuthor author = getOne(new QueryWrapper<ApAuthor>().lambda().eq(ApAuthor::getId, userId));

        return ResponseResult.okResult(author);
    }

    @Override
    public ResponseResult insert(ApAuthor apAuthor) {
        if (apAuthor == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //保存作者信息
        apAuthor.setCreatedTime(new Date());
        save(apAuthor);
        return ResponseResult.errorResult(AppHttpCodeEnum.SUCCESS);
    }
}
