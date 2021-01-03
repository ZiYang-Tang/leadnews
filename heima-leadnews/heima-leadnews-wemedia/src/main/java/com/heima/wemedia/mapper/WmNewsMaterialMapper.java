package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {
    /**
     * 批量添加数据，用来绑定图片素材和文章的关联关系
     * @param materialIds
     * @param newId
     * @param type
     */
    void saveRelationsByContent(@Param("materialIds") List<Integer> materialIds, @Param("newsId") Integer newId, @Param("type") int type);

}