package com.heima.admin.controller.v1;

import com.heima.admin.service.AdChannelService;
import com.heima.api.admin.AdChannelControllerApi;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/channel")
public class AdChannelController  implements AdChannelControllerApi {

    @Autowired
    private AdChannelService channelService;

    @PostMapping("/list")
    @Override
    public ResponseResult findByNameAndPage(@RequestBody ChannelDto dto){
        return channelService.findByNameAndPage(dto);
    }

    @PostMapping("/save")
    @Override
    public ResponseResult insertChannel(@RequestBody AdChannel adChannel) {
        return channelService.insertChannel(adChannel);
    }

    @PostMapping("/update")
    @Override
    public ResponseResult updateChannel(@RequestBody AdChannel adChannel) {
        return channelService.updateChannel(adChannel);
    }

    @GetMapping("/del/{id}")
    @Override
    public ResponseResult deleteChannelById(@PathVariable Integer id) {
        return channelService.deleteChannelById(id);
    }

    @GetMapping("/channels")
    @Override
    public ResponseResult findAll() {
        List<AdChannel> list = channelService.list();
        return ResponseResult.okResult(list);
    }
}