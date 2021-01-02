package com.heima.wemedia.config;

import com.heima.model.config.SwaggerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SwaggerConfiguration.class)
public class SwaggerConfig {
}