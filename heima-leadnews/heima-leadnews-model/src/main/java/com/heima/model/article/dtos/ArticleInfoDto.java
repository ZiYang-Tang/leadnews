package com.heima.model.article.dtos;

import com.heima.model.common.annotation.IdEncrypt;
import lombok.Data;

@Data
public class ArticleInfoDto {
    /**
     * 文章ID
     */
    @IdEncrypt
    Long articleId;
}