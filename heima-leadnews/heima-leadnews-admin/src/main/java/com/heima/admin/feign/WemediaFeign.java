package com.heima.admin.feign;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("leadnews-wemedia")
public interface WemediaFeign {

    @GetMapping("/api/v1/news/findOne/{id}")
    public WmNews findById(@PathVariable("id") Integer id);

    @PostMapping("/api/v1/news/update")
    public ResponseResult updateWmNews(WmNews wmNews);

    @GetMapping("/api/v1/user/findOne/{id}")
    public WmUser findWmUserById(@PathVariable("id")  Long id);
}
