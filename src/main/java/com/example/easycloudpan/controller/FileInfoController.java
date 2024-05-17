package com.example.easycloudpan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.pojo.dto.FileInfoDto;
import com.example.easycloudpan.service.FileInfoService;
import com.example.easycloudpan.service.UserInfoServise;
import lombok.extern.slf4j.Slf4j;
import com.example.easycloudpan.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/file")
//确保其符合指定的验证规则
@Validated
@Slf4j
public class FileInfoController {
    @Autowired
    private FileInfoService fileInfoService;
    @PostMapping("loadDataList")
    public R<FileInfoDto> loadDataList(@RequestParam String category,
                                       @RequestParam String filePid,
                                       @RequestParam(required = false) String fileNameFuzzy,
                                       @RequestParam(required = false) String pageNo,
                                       @RequestParam(required = false) String pageSize) {
        //设置分页参数
        pageNo="1";
        pageSize="15";
        Page<FileInfo> page=new Page<>(Long.valueOf(pageNo),Long.valueOf(pageSize));
/*        Page<FileInfo> page=new Page<>(1,15);*/
        //构建查询条件
        LambdaQueryWrapper<FileInfo>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getFileCategory,category).eq(FileInfo::getFilePid,filePid);
        Page<FileInfo> page1 = fileInfoService.page(page, lambdaQueryWrapper);
        //构建返回对象
        FileInfoDto fileInfoDto=new FileInfoDto();
        fileInfoDto.setList(page1.getRecords());
        fileInfoDto.setPageSize(Long.valueOf(pageSize));
        fileInfoDto.setPageNo(Long.valueOf(pageNo));
        fileInfoDto.setPageTotal(page1.getTotal());
        fileInfoDto.setTotalCount(page1.getCurrent());
        return R.success(new  FileInfoDto());
    }
}