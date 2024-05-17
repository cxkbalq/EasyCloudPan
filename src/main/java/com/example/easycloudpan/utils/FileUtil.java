package com.example.easycloudpan.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.service.FileInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class FileUtil {
    @Autowired
    FileInfoService fileInfoService;

    //秒传判断
    public Boolean isExist(String md5 ,FileInfo one) {
        //如果存在这个文件
        if (one.equals(md5)) {
            return true;
        } else {
            return false;
        }
    }
    //秒传
    public Boolean miaoChan(FileInfo one){
        //创建新的文件对象
        FileInfo fileInfo =new FileInfo();
        fileInfo.setFileMd5(one.getFileMd5());
        fileInfo.setFilePid(one.getId());
        fileInfo.setCreateTime(LocalDateTime.now());
        fileInfo.setUpdateTime(LocalDateTime.now());
        //id
        String s = new StringUtil().generateRandomString(10);
        fileInfo.setId(s);
        fileInfo.setFilePath(one.getFilePath());
        return true;
    }
    //文件类型判断

}
