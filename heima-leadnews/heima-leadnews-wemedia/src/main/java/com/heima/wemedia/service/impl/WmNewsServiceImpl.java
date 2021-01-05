package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.wemedia.WemediaContans;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.threadlocal.WmThreadLocalUtils;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings(value = "all")
@Service
@Transactional
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Value("${fdfs.url}")
    private String fileServerUrl;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Override
    public ResponseResult findAll(WmNewsPageReqDto dto) {
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        dto.checkParam();

        //分页查询构造
        IPage pageParam = new Page<>(dto.getPage(), dto.getSize());

        //查询条件构造
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //状态查询
        if (dto.getStatus() != null) {
            lambdaQueryWrapper.eq(WmNews::getStatus, dto.getStatus());
        }
        //频道查询
        if (dto.getChannelId() != null) {
            lambdaQueryWrapper.eq(WmNews::getChannelId, dto.getChannelId());
        }

        //时间范围查询
        if (dto.getBeginPubDate() != null && dto.getEndPubDate() != null) {
            lambdaQueryWrapper.between(WmNews::getPublishTime, dto.getBeginPubDate(), dto.getEndPubDate());
        }

        //关键字模糊查询
        if (dto.getKeyword() != null) {
            lambdaQueryWrapper.like(WmNews::getTitle, dto.getKeyword());
        }

        WmUser user = WmThreadLocalUtils.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);
        IPage pageResult = page(pageParam, lambdaQueryWrapper);

        //将结果进行封装
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) pageParam.getTotal());
        //赋值数据
        responseResult.setData(pageResult.getRecords());
        responseResult.setHost(fileServerUrl);
        return responseResult;
    }

    /**
     * 保存文章
     *
     * @param dto
     * @param isSubmit 是否为提交 1 为提交 0为草稿
     * @return
     */
    @Override
    public ResponseResult saveNews(WmNewsDto dto, Short isSubmit) {
        //参数校验
        if (dto == null || StringUtils.isBlank(dto.getContent())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //1,保存或修改文章
        WmNews wmNews = new WmNews();
        // 将dto里的属性，复制到wmNews对象里
        BeanUtils.copyProperties(dto, wmNews);
        //判断文章的封面类型是否为自动
        if (WemediaContans.WM_NEWS_TYPE_AUTO.equals(dto.getType())) {
            wmNews.setType(null);
        }
        // 判断封面图片是否为空
        if (dto.getImages() != null && dto.getImages().size() > 0) {
            //[dfjksdjfdfj.jpg,sdlkjfskld.jpg]
            //设置图片的路径
            wmNews.setImages(dto.getImages().toString().replace("[", "")
                    .replace("]", "")
                    .replace(fileServerUrl, "")
                    .replace(" ", ""));
        }
        // 保存或修改文章
        saveWmNews(wmNews, isSubmit);

        //2.关联文章和素材之间的关系
        String content = dto.getContent();
        List<Map> list = JSON.parseArray(content, Map.class);
        List<String> materials = ectractUrlInfo(list);

        //2.1 关联内容中的图片和素材的关系
        // 先判断提交是否是 提交审核 ，在判断提交的图片和设置的图片数量是否一致
        if (isSubmit == WmNews.Status.SUBMIT.getCode() && materials.size() != 0) {
            ResponseResult responseResult = saveRelativeInfoForContent(materials, wmNews.getId());
            if (responseResult != null) {
                return responseResult;
            }
        }
        //2.2关联封面中的图片和文章的关联关系，将wm_type设置为自动
        // 是否是提交审核,并且是无图模式
        if (isSubmit == WmNews.Status.SUBMIT.getCode()) {
            ResponseResult responseResult = saveRelativeInfoForCover(dto, materials, wmNews);
            if (responseResult != null) {
                return responseResult;
            }
        }
        return null;
    }


    /**
     * 保存封面和文章关系的 前置操作
     *
     * @param dto
     * @param materials
     * @param wmNews
     * @return
     */
    private ResponseResult saveRelativeInfoForCover(WmNewsDto dto, List<String> materials, WmNews wmNews) {
        List<String> images = dto.getImages();
        //如果自媒体人选择的自动模式，那么根据文章中的图片自动匹配封面
        if (dto.getType().equals(WemediaContans.WM_NEWS_TYPE_AUTO)) {
            //内容中的图片数量小于等于2  设置为单图
            if (materials.size() > 0 && materials.size() <= 2) {
                wmNews.setType(WemediaContans.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            } else if (materials.size() > 2) {
                //如果内容中的图片大于2 设置为多图
                wmNews.setType(WemediaContans.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            } else {
                //没有图片的，则是无图
                wmNews.setType(WemediaContans.WM_NEWS_NONE_IMAGE);
            }
            //修改文章信息
            if (images != null && images.size() > 0) {
                //修改之后的图片路径只剩下保存在 fastDFS的路径
                wmNews.setImages(images.toString().replace("[", "")
                        .replace("]", "").replace(fileServerUrl, "")
                        .replace(" ", ""));
            }
            //再根据id进行修改
            updateById(wmNews);
        }

        //封面图片选择完之后，保存封面与素材的关联关系
        if (images != null && images.size() > 0) {
            ResponseResult responseResult = saveRelativeInfoForImage(images, wmNews.getId());
            if (responseResult != null) {
                return responseResult;
            }
        }
        return null;
    }

    /**
     * 保存封面和文章关系的 实际操作
     *
     * @param images
     * @param newsId
     * @return
     */
    private ResponseResult saveRelativeInfoForImage(List<String> images, Integer newsId) {
        ArrayList<String> materials = new ArrayList<>();
        for (String image : images) {
            materials.add(image.replace(fileServerUrl, ""));
        }
        return saveRelativeInfo(materials, newsId, WemediaContans.WM_NEWS_COVER_REFERENCE);
    }

    /**
     * 关联图片和素材的关系
     *
     * @param materials
     * @param id
     * @return
     */
    private ResponseResult saveRelativeInfoForContent(List<String> materials, Integer id) {
        return saveRelativeInfo(materials, id, WemediaContans.WM_NEWS_CONTENT_REFERENCE);
    }


    /**
     * 保存文章和素材的关系
     *
     * @param materials
     * @param newsId
     * @param type
     * @return
     */
    private ResponseResult saveRelativeInfo(List<String> materials, Integer newsId, Short type) {
        // 去重
        materials = materials.stream().distinct().collect(Collectors.toList());

        // 先根据url 和用户id查询素材信息
        Integer userId = WmThreadLocalUtils.getUser().getId();
        List<WmMaterial> materialList = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery()
                .in(WmMaterial::getUrl, materials)
                .eq(WmMaterial::getUserId, userId));

        // 如果两个集合的长度不相等，则表示
        if (materials.size() != materialList.size()) {
            throw new RuntimeException("文章素材数据不匹配");
        }
        List<Integer> materialIds = materialList.stream().map(material -> material.getId()).collect(Collectors.toList());

        wmNewsMaterialMapper.saveRelationsByContent(materialIds, newsId, type);
        return null;
    }

    /**
     * 提取图片信息
     *
     * @param list
     * @return
     */
    private List<String> ectractUrlInfo(List<Map> list) {
        ArrayList<String> materials = new ArrayList<>();
        //提取前端内容 类型是image的数据
        for (Map map : list) {
            if (map.get("type").equals(WemediaContans.WM_NEWS_TYPE_IMAGE)) {
                String imgUrl = (String) map.get("value");
                // 将路由路径改为空，留下图片在 fastDFS的路径
                imgUrl = imgUrl.replace(fileServerUrl, "");
                //再存到集合
                materials.add(imgUrl);
            }
        }
        return materials;
    }

    /**
     * 保存或修改文章
     *
     * @param wmNews
     * @param isSubmit
     */
    private void saveWmNews(WmNews wmNews, Short isSubmit) {
        // 修改状态
        wmNews.setStatus(isSubmit);
        // 设置用户id
        wmNews.setUserId(WmThreadLocalUtils.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short) 1);
        // 判断是保存还是修改
        if (wmNews.getId() == null) {
            save(wmNews);
        } else {
            //如果 是修改 那么先删除之前的关系
            LambdaQueryWrapper<WmNewsMaterial> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WmNewsMaterial::getId, wmNews.getId());
            //删除 文章&素材的中间表
            wmNewsMaterialMapper.delete(queryWrapper);
            updateById(wmNews);
        }
    }

    /**
     * 根据id查询文章信息
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult findWmNewsById(Integer id) {
        //1.校验参数
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章Id不可缺少");
        }
        //2。根据id查一把
        WmNews wmNews = getById(id);
        //判断能否查询出文章
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章不存在");
        }
        //3.将结果封装，并加上图片的访问url
        ResponseResult responseResult = ResponseResult.okResult(wmNews);
        responseResult.setHost(fileServerUrl);
        return responseResult;
    }

    /**
     * 根据id删除文章
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult delNews(Integer id) {
        //1.校验参数
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章Id不可缺少");
        }
        //2.获取数据
        WmNews wmNews = getById(id);
        //判断能否查询出文章
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章不存在");
        }
        //3.判断当前文章的状态 上架状态和发布（9）不能删除
        if (wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode()) && wmNews.getEnable().equals(WemediaContans.WM_NEWS_ENABLE_UP)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "文章已发布，不能删除！");
        }
        //去除文章和素材的关联关系
        wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, wmNews.getId()));
        //删除文章
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {
        //1.校验参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章Id不可缺少");
        }
        //2.获取数据
        WmNews wmNews = getById(dto.getId());
        //判断能否查询出文章
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章不存在");
        }
        //3.判断当前文章的状态 上架状态和发布（9）不能删除
        if (wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "当前文章不是发布状态，不能上下架");
        }
        //4.修改文章的状态 将范围缩小之 0-1
        if (dto.getEnable() != null && dto.getEnable() > -1 && dto.getEnable() < 2) {
            //根据id修改上架状态，将数据库中的enable修改为dto传递过来enable，
            update(Wrappers.<WmNews>lambdaUpdate().eq(WmNews::getId, wmNews.getId()).set(WmNews::getEnable, dto.getEnable()));
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


}
