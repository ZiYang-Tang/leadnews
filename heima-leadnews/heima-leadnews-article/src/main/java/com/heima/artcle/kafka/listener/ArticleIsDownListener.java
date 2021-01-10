package com.heima.artcle.kafka.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.artcle.service.ApArticleConfigService;
import com.heima.common.constants.message.WmNewsMessageConstants;
import com.heima.model.article.pojos.ApArticleConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * app端的文章是否下架的状态监听
 */
@Component
public class ArticleIsDownListener {

    @Autowired
    private ApArticleConfigService apArticleConfigService;

    //监听方法
    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void getMessage(ConsumerRecord record) {
        Optional<ConsumerRecord> optional = Optional.ofNullable(record);
        if (optional.isPresent()) {
            String value = (String) record.value();
            Map map = JSON.parseObject(value, Map.class);
            Long articleId = (Long) map.get("articleId");
            Integer enable = (Integer) map.get("enable");
            System.err.println("articleId = " + articleId);
            System.err.println("enable = " + enable);
            LambdaUpdateWrapper<ApArticleConfig> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.eq(ApArticleConfig::getArticleId, articleId);
            //如果enable=1表示wm_news表上架状态
            if(enable==1){
                updateWrapper.set(ApArticleConfig::getIsDown,0);//修改isdown=0表示下架状态
            }else{ //如果enable=0表示wm_news表下架状态
                updateWrapper.set(ApArticleConfig::getIsDown,1);//修改isdown=1表示上架架状态
            }
            apArticleConfigService.update(updateWrapper);
        }
    }
}
