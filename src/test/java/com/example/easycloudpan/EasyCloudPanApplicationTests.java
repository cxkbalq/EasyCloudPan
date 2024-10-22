//package com.example.easycloudpan;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//
//@SpringBootTest
//@Slf4j
//class FfmpegProcessingTime {
//    @Value("${easycloudpan.path.temppath}")
//    private String temppath;
//    @Value("${easycloudpan.path.filepath}")
//    private String filepath;
//    @Value("${easycloudpan.path.imgepath}")
//    private String imgepath;
//    @Value("${easycloudpan.path.croveImage}")
//    private String imgepcrove;
//    public static void main(String[] args) {
//        String inputFilePath = "path/to/your/input/file.mp4"; // 输入文件路径
//        String outputFilePath = "path/to/your/output/file.mp4"; // 输出文件路径
//
//        try {
//            long startTime = System.currentTimeMillis(); // 开始时间
//
//            // 构建 FFmpeg 命令
//            ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-i", inputFilePath, outputFilePath);
//            processBuilder.redirectErrorStream(true); // 合并标准输出和错误输出
//
//            // 启动进程并读取输出
//            Process process = processBuilder.start();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line); // 打印 FFmpeg 输出
//            }
//
//            int exitCode = process.waitFor(); // 等待进程结束
//            long endTime = System.currentTimeMillis(); // 结束时间
//
//            // 处理时间
//            if (exitCode == 0) {
//                long processingTime = endTime - startTime; // 计算处理时间
//                System.out.printf("Processing time: %.2f seconds%n", processingTime / 1000.0);
//            } else {
//                System.err.println("FFmpeg process failed with exit code: " + exitCode);
//            }
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void test() {
//        String diskFreeSpace="C";
//
//        File diskPartition = new File( diskFreeSpace+ ":"); // 根目录，或指定其他目录
//        long freeSpace = diskPartition.getUsableSpace(); // 获取剩余空间
//        System.out.println(freeSpace);
//    }
//    @Test
//    public void run() {
//        log.info("开始进行目录完整性检查");
//        List<String> list=new ArrayList<>();
//        list.add(temppath);
//        list.add(filepath);
//        list.add(imgepath);
//        list.add(imgepcrove);
//        // 获取路径映射
//        for (String path: list) {
//            File directory = new File(path);
//            if (!directory.exists()) {
//                boolean created = directory.mkdirs(); // 创建目录，包括任何必要但不存在的父目录
//                if (created) {
//                    log.info("目录创建成功: " + path);
//                } else {
//                    log.info("目录创建失败: " + path);
//                }
//            } else {
//                log.info("目录已存在: " + path);
//            }
//        }
//    }
//    }
//
//
