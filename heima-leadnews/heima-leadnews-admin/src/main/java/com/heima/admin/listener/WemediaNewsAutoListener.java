package com.heima.admin.listener;

import com.heima.admin.service.WemediaNewsAutoScanService;
import com.heima.common.constants.message.NewsAutoScanConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WemediaNewsAutoListener {
    @Autowired
    WemediaNewsAutoScanService wemediaNewsAutoScanService;

    @KafkaListener(topics = NewsAutoScanConstants.WM_NEWS_AUTO_SCAN_TOPIC)
    public void recevieMessage(ConsumerRecord<?,?> record){
        Optional<? extends ConsumerRecord<?, ?>> optional = Optional.ofNullable(record);
        // 判断消息是否存在
        if (optional.isPresent()){
            //获得消息
            Object value = record.value();
            //类型转化Integer类型
            wemediaNewsAutoScanService.autoScanByMediaNewsId(Integer.parseInt((String)value));
        }
    }
}
