package com.heima.wemedia.controller.v1;

import com.heima.api.wemedia.WmUserControllerApi;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.model.wemedia.dtos.WmUserDto;
import com.heima.wemedia.service.WmUserService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class WmUserController implements WmUserControllerApi {
    @Autowired
    private WmUserService wmUserService;

    @PostMapping("/save")
    @Override
    public ResponseResult save(@RequestBody WmUser wmUser){
        return wmUserService.insert(wmUser);
    }

    @GetMapping("/findByName/{name}")
    @Override
    public ResponseResult findByName(String name) {
        return wmUserService.findByName(name);
    }

    @GetMapping("/findOne/{id}")
    @Override
    public WmUser findWmUserById(@PathVariable("id")  Long id) {
        return wmUserService.getById(id);
    }


}
