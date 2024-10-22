package com.example.easycloudpan.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

@Component
@Slf4j
public class InitChecker implements CommandLineRunner {
    @Value("${easycloudpan.path.temppath}")
    private String temppath;
    @Value("${easycloudpan.path.filepath}")
    private String filepath;
    @Value("${easycloudpan.path.imgepath}")
    private String imgepath;
    @Value("${easycloudpan.path.croveImage}")
    private String imgepcrove;
    @Override
    public void run(String... args) throws Exception {
        log.info("开始进行目录完整性检查");
        List<String> list=new ArrayList<>();
        list.add(temppath);
        list.add(filepath);
        list.add(imgepath);
        list.add(imgepcrove);
        // 获取路径映射
        for (String path: list) {
            File directory = new File(path);
            if (!directory.exists()) {
                boolean created = directory.mkdirs(); // 创建目录，包括任何必要但不存在的父目录
                if (created) {
                    log.info("目录创建成功: " + path);
                } else {
                    log.info("目录创建失败: " + path);
                }
            } else {
                log.info("目录已存在: " + path);
            }
        }

        //开始检测服务器处理性能

    }
}
