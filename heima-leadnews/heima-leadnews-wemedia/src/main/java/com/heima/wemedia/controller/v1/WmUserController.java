package com.heima.wemedia.controller.v1;

import com.heima.api.wemedia.WmUserControllerApi;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.media.pojos.WmUser;
import com.heima.model.wemedia.dtos.WmUserDto;
import com.heima.wemedia.service.WmUserService;
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

    @PostMapping("/in")
    @Override
    public ResponseResult login(WmUserDto dto) {
        return wmUserService.login(dto);
    }


}
