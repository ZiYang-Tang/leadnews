package com.heima.api.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * 素材管理控制层接口
 */
@Api(value = "自媒体用户管理", tags = "WmUser", description = "自媒体用户管理API")
public interface WmMaterialControllerApi {

    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    @ApiOperation("上传图片")
    ResponseResult uploadPicture(MultipartFile multipartFile);


    /**
     * 素材列表
     * @param dto
     * @return
     */
    ResponseResult findList(WmMaterialDto dto);

    /**
     * 删除图片
     * @param id
     * @return
     */
    ResponseResult delPicture(Integer id);

    /**
     * 取消收藏
     * @param id
     * @return
     */
    ResponseResult cancleCollectionMaterial(Integer id);

    /**
     * 收藏图片
     * @param id
     * @return
     */
    ResponseResult collectionMaterial(Integer id);
}