package com.heima.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.admin.feign.ArticleFeign;
import com.heima.admin.feign.WemediaFeign;
import com.heima.admin.mapper.AdChannelMapper;
import com.heima.admin.mapper.AdSensitiveMapper;
import com.heima.admin.service.WemediaNewsAutoScanService;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.fastdfs.FastDFSClient;
import com.heima.model.admin.dtos.NewsAuthDto;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.model.wemedia.vo.WmNewsVo;
import com.heima.utils.common.SensitiveWordUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Log4j2
@Service
@Transactional
public class WemediaNewsAutoScanServiceImpl implements WemediaNewsAutoScanService {

//     0 草稿
//     1 提交（待审核）
//     2 审核失败
//     3 人工审核
//     4 人工审核通过
//     8 审核通过（待发布）
//     9 已发布

    @Value("fdfs.url")
    private String url;
    @Autowired
    private WemediaFeign wemediaFeign;

    //根据文章的状态对文章进行审核等操作
    @Override
    @GlobalTransactional
    public void autoScanByMediaNewsId(Integer id) {
        //首先检查参数
        if (id == null) {
            log.error("参数缺失");
            return;
        }

        //首先根据 id 获取到相应的文章
        WmNews wmNews = wemediaFeign.findById(id);
        if (wmNews == null) {
            log.error("文章不存在，自媒体id:{}", id);
            return;
        }

        if (wmNews.getStatus() == 0 || wmNews.getStatus() == 3) {
            return;
        }

        //审核失败的情况
        if (wmNews.getStatus() == 2) {
            log.error("文章审核未通过，自媒体id:{}", id);
            return;
        }

        //人工审核通过的情况,文章已自动审核通过判断是否到达发布时间并保存数据
        if (wmNews.getStatus() == 4 || wmNews.getStatus() == 8) {
            if (wmNews.getPublishTime().getTime() <= System.currentTimeMillis()) {
                wmNews.setStatus((short) 9);
                wmNews.setReason("审核通过");
                wmNews.setEnable((short) 1);
                saveAppArticle(wmNews);
                wemediaFeign.updateWmNews(wmNews);
            }
            return;
        }

        //文章待审核则自动进行审核
        if (wmNews.getStatus() == 1) {
            //首先对文章里的数据进行处理，将其处理成为图片和文本两部分
            Map<String, Collection<String>> map = handerWmNews(wmNews);

            //进行安全校验
            boolean flag = saveScan(map, wmNews);

            //判断如果校验通过，判断是否要进行发布
            if (flag) {
                if (wmNews.getPublishTime().getTime() > System.currentTimeMillis()) {
                    wmNews.setStatus((short) 8);
                    wmNews.setReason("审核通过，待发布");
                    wemediaFeign.updateWmNews(wmNews);
                    return;
                } else {
                    wmNews.setStatus((short) 9);
                    wmNews.setReason("审核通过");
                    wmNews.setEnable((short) 1);
                    saveAppArticle(wmNews);
                    wemediaFeign.updateWmNews(wmNews);
                    return;
                }
            }
        }
    }

    @Override
    public PageResponseResult findNews(NewsAuthDto dto) {
        PageResponseResult pageResponseResult = wemediaFeign.findList(dto);
        pageResponseResult.setHost(url);
        return pageResponseResult;
    }

    @Override
    public ResponseResult findOne(Integer id) {
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmNewsVo wmNewsVo = wemediaFeign.findWmNewsVo(id);

        ResponseResult responseResult = ResponseResult.okResult(wmNewsVo);
        responseResult.setHost(url);
        return responseResult;
    }

    @Override
    public ResponseResult updateStatus(Integer type, NewsAuthDto dto) {
        if (type == null || dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmNews wmNews = wemediaFeign.findById(dto.getId());

        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        if (type.equals(0)) {
            wmNews.setStatus((short) 2);
            wmNews.setReason(dto.getMsg());
            wemediaFeign.updateWmNews(wmNews);
        } else {
            wmNews.setStatus((short) 4);
            wmNews.setReason("人工审核通过");
            wemediaFeign.updateWmNews(wmNews);
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Autowired
    private GreenTextScan greeTextScan;
    @Autowired
    private GreenImageScan greenImageScan;
    @Autowired
    private FastDFSClient fastDFSClientUtil;
    @Autowired
    private AdSensitiveMapper adSensitiveMapper;
    @Autowired
    private ArticleFeign articleFeign;
    @Autowired
    private AdChannelMapper adChannelMapper;

    //进行安全校验
    private boolean saveScan(Map<String, Collection<String>> map, WmNews wmNews) {
        try {
            //先验证文本
            Collection<String> texts = map.get("texts");
            String replace = JSON.toJSONString(texts).replace("[", "")
                    .replace("]", "")
                    .replace(" ", "");
            //审核文本及敏感词
            if (replace != null && replace.length() > 0) {

                //审核敏感词
                //首先获取敏感词列表并创建敏感词词典
                SensitiveWordUtil.initMap(adSensitiveMapper.findAllSensitive());
                //验证敏感词
                Map<String, Integer> matchWords = SensitiveWordUtil.matchWords(replace);
                if (matchWords == null && matchWords.size() > 0) {
                    wmNews.setStatus((short) 2);
                    wmNews.setReason("内容中有敏感词");
                    wemediaFeign.updateWmNews(wmNews);
                    return false;
                }

                //审核文本
                Map map1 = greeTextScan.greeTextScan(replace);
                //判断是否审核通过
                if (!imageAndTextScan(map1, wmNews)) {
                    return false;
                }
            }


            //判断图片是否合法
            Collection<String> images = map.get("images");
            List<byte[]> list = new ArrayList<>();
            //将图片下载转为二进制数组
            if (images != null && images.size() > 0) {
                for (String image : images) {
                    int index = image.indexOf("/");
                    String group = image.substring(0, index);
                    String path = image.substring(index + 1);
                    byte[] download = fastDFSClientUtil.download(group, path);
                    list.add(download);
                }

                Map map2 = greenImageScan.imageScan(list);
                //判断是否审核通过
                if (!imageAndTextScan(map2, wmNews)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //判断是否审核通过
    public boolean imageAndTextScan(Map map, WmNews wmNews) {
        if (!map.get("suggestion").equals("pass")) {
            //审核失败
            if (map.get("suggestion").equals("block")) {
                //修改自媒体文章的状态，并告知审核失败原因
                wmNews.setStatus((short) 2);
                wmNews.setReason("内容中有违法内容");
                wemediaFeign.updateWmNews(wmNews);
                return false;
            }

            //人需要工审核
            if (map.get("suggestion").equals("review")) {
                //修改自媒体文章的状态，并告知审核失败原因
                wmNews.setStatus((short) 3);
                wmNews.setReason("无法判定，需要人工审核");
                wemediaFeign.updateWmNews(wmNews);
                return false;
            }
        }

        return true;
    }

    //保存文章及其相关配置
    private void saveAppArticle(WmNews wmNews) {
        //保存app文章
        ApArticle apArticle = saveArticle(wmNews);
        apArticle.setPublishTime(wmNews.getPublishTime());
        if (apArticle != null && apArticle.getId() != null) {
            wmNews.setArticleId(apArticle.getId());
        }
        //保存app文章配置
        saveArticleConfig(apArticle);
        //保存app文章内容
        saveArticleContent(apArticle, wmNews);
    }

    private void saveArticleContent(ApArticle apArticle, WmNews wmNews) {
        ApArticleContent apArticleContent = new ApArticleContent();
        apArticleContent.setArticleId(apArticle.getId());
        apArticleContent.setContent(wmNews.getContent());
        articleFeign.saveArticleContent(apArticleContent);
    }

    private void saveArticleConfig(ApArticle apArticle) {
        ApArticleConfig apArticleConfig = new ApArticleConfig();
        apArticleConfig.setArticleId(apArticle.getId());
        apArticleConfig.setIsForward(true);
        apArticleConfig.setIsDelete(false);
        apArticleConfig.setIsDown(true);
        apArticleConfig.setIsComment(true);

        articleFeign.saveArticleConfig(apArticleConfig);
    }

    private ApArticle saveArticle(WmNews wmNews) {
        ApArticle apArticle = new ApArticle();
        apArticle.setTitle(wmNews.getTitle());
        apArticle.setLayout(wmNews.getType());
        apArticle.setImages(wmNews.getImages());
        apArticle.setCreatedTime(new Date());

        //获取作者相关信息
        Integer wmUserId = wmNews.getUserId();
        WmUser wmUser = wemediaFeign.findWmUserById(wmUserId);
        if (wmUser != null) {
            String wmUserName = wmUser.getName();
            ApAuthor apAuthor = articleFeign.selectAuthorByName(wmUserName);
            if (apAuthor != null) {
                apArticle.setAuthorId(apAuthor.getId().longValue());
                apArticle.setAuthorName(apAuthor.getName());
            }

        }

        //获取频道相关信息
        Integer channelId = wmNews.getChannelId();
        AdChannel channel = adChannelMapper.selectById(channelId);
        if (channel != null) {
            apArticle.setChannelId(channel.getId());
            apArticle.setChannelName(channel.getName());
        }

        return articleFeign.saveArticle(apArticle);
    }

    //处理文章并返回里面的文本信息和图片信息
    private Map<String, Collection<String>> handerWmNews(WmNews wmNews) {

        //首先将文章对象的文本内容进行解析
        String content = wmNews.getContent();
        List<Map<String, String>> list = JSON.parseObject(content, List.class);
        List<String> images = new ArrayList<>();
        //使用set集合防止出现重复图片
        Set<String> texts = new TreeSet<>();
        if (list != null && list.size() > 0) {
            for (Map<String, String> map : list) {
                if ("image".equals(map.get("type"))) {
                    images.add(map.get("value").replace(url, ""));
                } else {
                    texts.add(map.get("value"));
                }
            }
        }

        //将封面一同添加
        if (wmNews.getImages() != null && wmNews.getImages().length() > 0) {
            String[] strings = wmNews.getImages().split(",");
            for (String string : strings) {
                images.add(string);
            }
        }

        //创建一个用于返回数据的map集合
        HashMap<String, Collection<String>> map = new HashMap<>();
        if (images != null && images.size() != 0) {
            map.put("images", images);
        }
        if (texts != null && texts.size() != 0) {
            map.put("texts", texts);
        }

        return map;
    }
}

