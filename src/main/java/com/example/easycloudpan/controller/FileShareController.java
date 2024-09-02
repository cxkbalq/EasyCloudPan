package com.example.easycloudpan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.easycloudpan.common.R;
import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.pojo.FileShare;
import com.example.easycloudpan.pojo.dto.FileShareDto;
import com.example.easycloudpan.pojo.dto.FileShareDto2;
import com.example.easycloudpan.pojo.vo.FileShareVo;
import com.example.easycloudpan.service.FileInfoService;
import com.example.easycloudpan.service.FileShareService;
import com.example.easycloudpan.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/share")
//确保其符合指定的验证规则
@Slf4j
public class FileShareController {
    @Autowired
    private FileShareService fileShareService;
    @Autowired
    private FileInfoService fileInfoService;

    //@ModelAttribute这个注解可以接收非json类型的表单数据
    @PostMapping("/shareFile")
    public R<FileShare> shareFile(HttpSession session,
                                  @ModelAttribute FileShareDto2 fileShareVo) {
        String userid = session.getAttribute("userid").toString();
        FileShare fileShare = new FileShare();
        //生成随机id
        String sId = StringUtil.generateRandomString(20);
        fileShare.setFileId(fileShareVo.getFileId());
        fileShare.setShareId(sId);
        fileShare.setSaveCount(0);
        fileShare.setCreateTime(LocalDateTime.now());
        fileShare.setValidType(fileShareVo.getValidType());
        /**
         * 有效期类型 0:1天 1:7天 2:30天 3:永久有效
         */
        int validType = fileShareVo.getValidType();
        LocalDateTime now = LocalDateTime.now();
        // 根据有效期类型设置过期时间
        LocalDateTime expireTime;
        switch (validType) {
            case 0:
                // 1天有效期
                expireTime = now.plusDays(1);
                break;
            case 1:
                // 7天有效期
                expireTime = now.plusDays(7);
                break;
            case 2:
                // 30天有效期
                expireTime = now.plusDays(30);
                break;
            case 3:
                // 永久有效，设置为 null 或者不设置过期时间
                expireTime = null;  // 根据需要选择如何处理永久有效的情况
                break;
            default:
                // 默认情况，处理无效类型
                throw new IllegalArgumentException("无效的有效期类型: " + validType);
        }
        // 设置到 fileShare 对象中
        fileShare.setExpireTime(expireTime);
        if (fileShareVo.getCodeType() == 1) {
            String sCode = StringUtil.generateRandomString(5);
            fileShare.setCode(sCode);
        } else {
            fileShare.setCode(fileShareVo.getCode());
        }
        fileShare.setUpdateTime(LocalDateTime.now());
        fileShare.setUserId(userid);
        fileShare.setFileId(fileShareVo.getFileId());
        fileShare.setSaveCount(0);
        fileShare.setBrowseCount(0);
        fileShare.setVersion(1);
        fileShare.setDownloadCount(0);
        fileShare.setDeleted(0);
        boolean save = fileShareService.save(fileShare);
        if (save) {
            return R.success(fileShare);
        }
        return R.error("发生未知错误，请重新尝试！");
    }

    /***
     * 获取分享文件列表
     * @param session
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping("/loadShareList")
    public R<FileShareDto> loadShareList(HttpSession session,
                                         @RequestParam("pageNo") String pageNo,
                                         @RequestParam("pageSize") String pageSize) {
        String userid = session.getAttribute("userid").toString();
        Page<FileShare> page = new Page<>(Long.valueOf(pageNo), Long.valueOf(pageSize));
        //构建分页
        LambdaQueryWrapper<FileShare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileShare::getUserId, userid).eq(FileShare::getDeleted,0);
        Page<FileShare> page1 = fileShareService.page(page, lambdaQueryWrapper);
        //构建返回对象
        FileShareDto FileShareDto = new FileShareDto();

        FileShareDto.setPageSize(Long.valueOf(pageSize));
        FileShareDto.setPageNo(Long.valueOf(pageNo));
        FileShareDto.setPageTotal(page1.getTotal());
        FileShareDto.setTotalCount(page1.getCurrent());
        List<FileShare> records = page1.getRecords();
        List<FileShareVo> list = new ArrayList<>();
        for (FileShare fileShare : records) {
            //在使用循环时，LambdaQueryWrapper构建条件时，需要每次重新生成，不然会产生叠加，查不出数据
            LambdaQueryWrapper<FileInfo> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            FileShareVo fileShareVo = new FileShareVo();
            lambdaQueryWrapper1.eq(FileInfo::getUserId, userid).eq(FileInfo::getFileId, fileShare.getFileId());
            List<FileInfo> list1 = fileInfoService.list(lambdaQueryWrapper1);
            for (FileInfo one : list1) {
                BeanUtils.copyProperties(fileShare, fileShareVo);
                BeanUtils.copyProperties(one, fileShareVo);
                fileShareVo.setFileName(one.getFileName());
                fileShareVo.setShareTime(fileShare.getCreateTime());
                fileShareVo.setFileCover(one.getFileCover());
                list.add(fileShareVo);
            }

        }
        FileShareDto.setList(list);
        return R.success(FileShareDto);
    }

    @PostMapping("/cancelShare")
    public R<String> cancelShare(HttpSession session, @RequestParam("shareIds") String shareIds) {
        String userid = session.getAttribute("userid").toString();
        String[] split = shareIds.split(",");
        for (String shareId : split) {
            LambdaUpdateWrapper<FileShare> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(FileShare::getShareId, shareId).eq(FileShare::getUserId, userid);
            lambdaUpdateWrapper.set(FileShare::getDeleted, 1).set(FileShare::getUpdateTime, LocalDateTime.now());
            boolean update = fileShareService.update(lambdaUpdateWrapper);
        }
        return R.success("更新成功！");
    }

}
