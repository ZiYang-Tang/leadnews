package com.heima.common.aliyun;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun")
@PropertySource("classpath:aliyun.properties")
public class AliyunConfiguration {

    private String accessKeyId;
    private String secret;
    private String scenes;
}
