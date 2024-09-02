package com.example.easycloudpan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.easycloudpan.common.R;
import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.pojo.dto.FileInfoDto;
import com.example.easycloudpan.service.FileInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@RestController()
@RequestMapping("/recycle")
public class RecycleController {

    @Autowired
    private FileInfoService fileInfoService;

    @PostMapping("/loadRecycleList")
    public R<FileInfoDto> delFile(HttpSession session , @RequestParam(required = false) String pageNo,
                                  @RequestParam(required = false) String pageSize) {
        String userid = session.getAttribute("userid").toString();
                Page<FileInfo> page = new Page<>(Long.valueOf(pageNo), Long.valueOf(pageSize));
        /*        Page<FileInfo> page=new Page<>(1,15);*/
        //构建查询条件
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if (filePid == null) {
//            filePid = String.valueOf(0);
//        }
//        lambdaQueryWrapper.eq(FileInfo::getFilePid, filePid);
//        if (fileNameFuzzy != null) {
//            lambdaQueryWrapper.like(FileInfo::getFileName, fileNameFuzzy);
//        }
//
//        if (type != 0) {
//            lambdaQueryWrapper.eq(FileInfo::getFileCategory, type);
//        }
        lambdaQueryWrapper.eq(FileInfo::getDelFlag,1).eq(FileInfo::getUserId,userid);
        Page<FileInfo> page1 = fileInfoService.page(page, lambdaQueryWrapper);
        //构建返回对象
        FileInfoDto fileInfoDto = new FileInfoDto();
        fileInfoDto.setList(page1.getRecords());
        fileInfoDto.setPageSize(Long.valueOf(pageSize));
        fileInfoDto.setPageNo(Long.valueOf(pageNo));
        fileInfoDto.setPageTotal(page1.getTotal());
        fileInfoDto.setTotalCount(page1.getCurrent());

        return R.success(fileInfoDto);
    }

    /***
     * 恢复文件
     * @param session
     * @param fileIds
     * @return
     */
    @PostMapping("/recoverFile")
    public R<String> recoverFile(HttpSession session, @RequestParam("fileIds") String fileIds) {
        String userid = session.getAttribute("userid").toString();
        String[] split = fileIds.split(",");
        for (String fileId : split) {
            LambdaUpdateWrapper<FileInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(FileInfo::getFileId, fileId).eq(FileInfo::getUserId, userid);
            lambdaUpdateWrapper.set(FileInfo::getDelFlag, 0).set(FileInfo::getLastUpdateTime, LocalDateTime.now());
            boolean update = fileInfoService.update(lambdaUpdateWrapper);
        }
        return R.success("更新成功！");
    }

    @PostMapping("/delFile")
    public R<String> delFile(HttpSession session, @RequestParam("fileIds") String fileIds) {
        String userid = session.getAttribute("userid").toString();
        String[] split = fileIds.split(",");
        for (String fileId : split) {
            LambdaUpdateWrapper<FileInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(FileInfo::getFileId, fileId).eq(FileInfo::getUserId, userid);
            lambdaUpdateWrapper.set(FileInfo::getDelFlag, 2).set(FileInfo::getLastUpdateTime, LocalDateTime.now());
            boolean update = fileInfoService.update(lambdaUpdateWrapper);
        }
        return R.success("更新成功！");
    }

}
