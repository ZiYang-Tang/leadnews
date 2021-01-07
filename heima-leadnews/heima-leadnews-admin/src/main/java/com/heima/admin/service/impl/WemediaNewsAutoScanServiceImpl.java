package com.heima.admin.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.heima.admin.feign.ArticleFeign;
import com.heima.admin.feign.WemediaFeign;
import com.heima.admin.mapper.AdChannelMapper;
import com.heima.admin.mapper.AdSensitiveMapper;
import com.heima.admin.service.WemediaNewsAutoScanService;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.fastdfs.FastDFSClient;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.SensitiveWordUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log4j2
public class WemediaNewsAutoScanServiceImpl implements WemediaNewsAutoScanService {

    @Autowired
    private WemediaFeign wemediaFeign;


    @Override
    @GlobalTransactional
    public void autoScanByMediaNewsId(Integer id) {
        if (id == null) {
            log.error("当前审核id空");
            return;
        }
        //1. 根据id查询自媒体文章
        WmNews wmNews = wemediaFeign.findById(id);
        if (wmNews == null) {
            log.error("审核的自媒体文章不存在，自媒体的id:{}", id);
            return;
        }
        //2.判断文章状态，为4（人工通过），就直接保存数据和创建索引
        if (wmNews.getStatus() == 4) {
            // 保存数据
            saveAppArticle(wmNews);
            return;
        }

        //3.判断文章状态为8  发布时间>当前时间 直接保存数据
        if (wmNews.getStatus() == 8 && wmNews.getPublishTime().getTime() <= System.currentTimeMillis()) {
            //保存数据
            saveAppArticle(wmNews);
            return;
        }

        //4.文章状态为1 那么开启自动审核
        if (wmNews.getStatus() == 1) {
            //抽取文章文本和图片
            Map<String, Object> contentAndImagesResult = handleTextAndImages(wmNews);
            //4.1文本审核
            boolean textScanBoolean = handleTextScan((String) contentAndImagesResult.get("content"), wmNews);
            if (!textScanBoolean) {
                return;
            }
            //4.2图片审核
            boolean imagesScanBoolean = handleImagesScan((List<String>) contentAndImagesResult.get("images"), wmNews);
            if (!imagesScanBoolean) {
                return;
            }
            //4.3自管理的敏感词汇
            boolean sensitiveScanBoolean = handleSensitive((String) contentAndImagesResult.get("content"), wmNews);
            if (!sensitiveScanBoolean) {
                return;
            }
            //4.4判断发布时间是否大于当前时间
            if (wmNews.getPublishTime().getTime() > System.currentTimeMillis()) {
                updateWmNews(wmNews, (short) 8, "审核通过，待发布");
                return;
            }
            //5.审核通过 修改状态 保存数据
            saveAppArticle(wmNews);
        }


    }


    @Autowired
    private AdSensitiveMapper adSensitiveMapper;

    /**
     * 检查自管理的敏感字
     *
     * @param content
     * @param wmNews
     * @return
     */
    private boolean handleSensitive(String content, WmNews wmNews) {
        List<String> allSensitive = adSensitiveMapper.findAllSensitive();
        //初始化敏感词
        SensitiveWordUtil.initMap(allSensitive);
        //文章内容自管理过滤
        Map<String, Integer> resultMap = SensitiveWordUtil.matchWords(content);

        //判断是否有敏感词
        if (resultMap.size() > 0) {
            log.error("敏感词过滤没有通过，包含了敏感词:{}", resultMap);
            //找到了敏感词，审核不通过
            updateWmNews(wmNews, (short) 2, "文章中包含了敏感词");
            return false;
        }
        return true;
    }


    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Value("${fdfs.url}")
    private String fileServerUrl;

    /**
     * 图片审核
     *
     * @param images
     * @param wmNews
     * @return
     */
    private boolean handleImagesScan(List<String> images, WmNews wmNews) {
        if (images == null) {
            return true;
        }
        List<byte[]> imageList = new ArrayList<>();
        try {
            for (String image : images) {
                //处理路径
                String imageName = image.replace(fileServerUrl, "");
                int index = imageName.indexOf("/");
                String groupName = imageName.substring(0, index);
                String imagePath = imageName.substring(index + 1);
                byte[] imageByte = fastDFSClient.download(groupName, imagePath);
                imageList.add(imageByte);
            }
            // 阿里云图片审核
            Map map = greenImageScan.imageScan(imageList);
            if (!map.get("suggestion").equals("pass")) {
                //审核不通过
                if (map.get("suggestion").equals("block")) {
                    //修改文章状态， 告知违规原因
                    updateWmNews(wmNews, (short) 2, "违规图片！");
                    return false;
                }
                //不确定，转人工
                if (map.get("suggestion").equals("review")) {
                    //修改文章状态
                    updateWmNews(wmNews, (short) 3, "文章中图片有不确定元素！");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("图片审核出现错误");
            return false;
        }
        return true;
    }

    @Autowired
    private GreenTextScan greenTextScan;


    /**
     * 文本审核
     *
     * @param content
     * @param wmNews
     * @return
     */
    private boolean handleTextScan(String content, WmNews wmNews) {
        try {
            Map map = greenTextScan.greeTextScan(content);
            if (!map.get("suggestion").equals("pass")) {
                //审核不通过
                if (map.get("suggestion").equals("block")) {
                    //
                    updateWmNews(wmNews, (short) 2, "文章内容中有敏感词汇");
                    return false;
                }
                //人工审核
                if (map.get("suggestion").equals("review")) {
                    //修改自媒体文章的状态，并告知审核失败原因
                    updateWmNews(wmNews, (short) 3, "文章内容中有不确定词汇");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("文本内容审核出现异常");
            return false;
        }
        return true;
    }

    /**
     * 修改文章状态
     *
     * @param wmNews
     * @param status
     * @param msg
     */
    private void updateWmNews(WmNews wmNews, short status, String msg) {
        wmNews.setStatus(status);
        wmNews.setReason(msg);
        wemediaFeign.updateWmNews(wmNews);
    }

    /**
     * 提取文字和图像
     *
     * @param wmNews
     * @return
     */
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {
        //文章的内容
        String content = wmNews.getContent();

        //存储纯文本
        StringBuilder sb = new StringBuilder();
        //存储图片
        List<String> images = new ArrayList<>();

        //提取文字和图片
        List<Map> contenList = JSONArray.parseArray(content, Map.class);
        for (Map map : contenList) {
            if (map.get("type").equals("text")) {
                sb.append(map.get("value"));
            }
            if (map.get("type").equals("image")) {
                sb.append((String) map.get("value"));
            }
        }
        //判断是否是无图
        if (wmNews.getImages() != null && wmNews.getType() != 0) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", sb.toString());
        resultMap.put("images", images);
        return resultMap;
    }

    /**
     * 保存文章相关数据
     *
     * @param wmNews
     */
    private void saveAppArticle(WmNews wmNews) {
        // 保存app文章
        ApArticle apArticle = saveArticle(wmNews);
        //保存 文章配置
        saveArticleConfig(apArticle);
        //保存文章内容
        saveArticleContent(apArticle, wmNews);

        //修改自媒体文章的状态为9
        updateWmNews(wmNews, (short) 9, "审核通过");

        // TODO es创建索引
    }

    @Autowired
    private ArticleFeign articleFeign;

    /**
     * 创建文章内容
     *
     * @param apArticle
     * @param wmNews
     */
    private void saveArticleContent(ApArticle apArticle, WmNews wmNews) {
        ApArticleContent apArticleContent = new ApArticleContent();
        apArticleContent.setArticleId(apArticle.getId());
        apArticleContent.setContent(wmNews.getContent());
        articleFeign.saveArticleContent(apArticleContent);
    }

    /**
     * 保存文章配置
     *
     * @param apArticle
     */
    private void saveArticleConfig(ApArticle apArticle) {
        ApArticleConfig apArticleConfig = new ApArticleConfig();
        apArticleConfig.setArticleId(apArticle.getId());
        apArticleConfig.setIsForward(true);
        apArticleConfig.setIsDelete(false);
        apArticleConfig.setIsDown(true);
        apArticleConfig.setIsComment(true);
        // 保存文章配置
        articleFeign.saveArticleConfig(apArticleConfig);
    }

    @Autowired
    private AdChannelMapper adChannelMapper;

    /**
     * 保存app文章
     *
     * @param wmNews
     * @return
     */
    private ApArticle saveArticle(WmNews wmNews) {
        ApArticle apArticle = new ApArticle();
        apArticle.setTitle(wmNews.getTitle());
        apArticle.setLayout(wmNews.getType());
        apArticle.setImages(wmNews.getImages());
        apArticle.setCreatedTime(new Date());

        //获取作者相关信息
        Integer wmUserId = wmNews.getUserId();
        WmUser wmUser = wemediaFeign.findWmUserById(wmUserId);
        if (wmUser == null) {
            //获得文章自媒体信息
            String wmUserName = wmUser.getName();
            //根据自媒体人的名称找到作者
            ApAuthor apAuthor = articleFeign.findByName(wmUserName);
            if (apAuthor != null) {
                //设置文章的作者信息
                apArticle.setAuthorId(apAuthor.getId().longValue());
                apArticle.setAuthorName(apAuthor.getName());
            }
        }
        // 获取频道相关信息
        Integer channelId = wmNews.getChannelId();
        // 根据id获取频道
        AdChannel channel = adChannelMapper.selectById(channelId);
        if (channel!=null){
            // 设置文章的频道属性
            apArticle.setChannelId(channel.getId());
            apArticle.setChannelName(channel.getName());
        }

        // 保存文章
        return articleFeign.saveArticle(apArticle);
    }
}
