package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.user.UserConstants;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.media.pojos.WmUser;
import com.heima.model.user.dtos.AuthDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserRealname;
import com.heima.user.feign.ArticleFeign;
import com.heima.user.feign.WemediaFeign;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.mapper.ApUserRealnameMapper;
import com.heima.user.service.ApUserRealnameService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@SuppressWarnings("all")
@Service
@Transactional
public class ApUserRealnameServiceImpl extends ServiceImpl<ApUserRealnameMapper, ApUserRealname> implements ApUserRealnameService {

    @Autowired
    private ArticleFeign articleFeign;

    @Autowired
    private WemediaFeign wemediaFeign;

    @Autowired
    private ApUserMapper apUserMapper;

    @Override
    public PageResponseResult loadListByStatus(AuthDto dto) {
        //1.判断参数
        if (dto == null) {
            return (PageResponseResult) ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.检查参数
        dto.checkParam();

        //3.构建条件,查询数据
        QueryWrapper<ApUserRealname> wrapper = new QueryWrapper<>();
        if (dto.getStatus() != null) {
            wrapper.lambda().eq(ApUserRealname::getStatus, dto.getStatus());
        }
        IPage pageParam = new Page<>(dto.getPage(), dto.getSize());
        IPage page = page(pageParam, wrapper);
        // 对结果进行封装
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setCode(200);
        responseResult.setData(page.getRecords());

        //5.返回封装后的结果
        return responseResult;
    }

    @Override
    @GlobalTransactional
    public ResponseResult updateStatusById(AuthDto dto, Short status) {
        if (dto == null || dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if (statusCheck(status)) {
            //说明账号账号不正常
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //修改状态
        ApUserRealname apUserRealname = new ApUserRealname();
        apUserRealname.setId(dto.getId());
        apUserRealname.setStatus(status);
        if (dto.getMsg() != null) {
            apUserRealname.setReason(dto.getMsg());
        }
        //根据状态修改
        updateById(apUserRealname);

        //3认证通过之后创建作者账号 和自媒体账号
        if (status.equals(UserConstants.PASS_AUTH)) {
//            int x = 1/0;
            ResponseResult createResult = createWmUserAndAuthor(dto);
            if (createResult != null) {
                return createResult;
            }
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 创建自媒体账号 以及作者账号
     *
     * @param dto
     * @return
     */
    private ResponseResult createWmUserAndAuthor(AuthDto dto) {
        //添加自媒体账号 查询ap_user信息再转到wm_user中
        ApUserRealname apUserRealname = getById(dto.getId());
        ApUser apUser = apUserMapper.selectById(apUserRealname.getId());
        if (apUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //判断自媒体账户是否存在。检查是否重复申请自媒体账号
        WmUser wmUser = null;
        if (apUser.getName() != null && apUser.getName().length() > 0) {
            ResponseResult<WmUser> responseResult = wemediaFeign.findByName(apUser.getName());
            wmUser = responseResult.getData();
        }

        if (wmUser == null || wmUser.getId() == null) {
            wmUser = new WmUser();

            //设置ApUserId
            wmUser.setApUserId(apUser.getId());
            wmUser.setCreatedTime(apUser.getCreatedTime());
            wmUser.setSalt(apUser.getSalt());
            wmUser.setName(apUser.getName());
            wmUser.setPassword(apUser.getPassword());
            wmUser.setStatus(9);
            wmUser.setPhone(apUser.getPhone());

            //保存这个自媒体人到自媒体表
            wemediaFeign.save(wmUser);
        }

        createAuthor(wmUser);
        //修改ap_user标记
        apUser.setFlag((short) 1);
        apUserMapper.updateById(apUser);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 创建作者账号
     *
     * @param wmUser
     */
    private void createAuthor(WmUser wmUser) {
        Integer apUserId = wmUser.getApUserId();
        ResponseResult<ApAuthor> responseResult = articleFeign.findByUserId(apUserId);
        ApAuthor apAuthor = responseResult.getData();

        //判断之前是否有创建作者账号
        if (apAuthor == null) {
            apAuthor = new ApAuthor();
            apAuthor.setId(wmUser.getId());
            apAuthor.setName(wmUser.getName());
            apAuthor.setType(UserConstants.AUTH_TYPE);
            apAuthor.setCreatedTime(new Date());

            //保存作者账号到作者表中
            articleFeign.save(apAuthor);
        }
    }

    /**
     * 检查状态
     *
     * @param status
     * @return
     */
    private boolean statusCheck(Short status) {

        if (status == null || (!status.equals(UserConstants.FAIL_AUTH) && !status.equals(UserConstants.PASS_AUTH))) {
            //账号状态不能正常使用
            return true;
        }
        return false;
    }
}
