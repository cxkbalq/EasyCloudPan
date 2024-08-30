package com.example.easycloudpan.utils;

import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.util.Random;

@Component
@Slf4j
public class StringUtil {
    //生成随机字符串
    public String generateRandomString(int length) {
        // 定义随机字符串的字符集
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();

        // 从字符集中随机选择字符，构建字符串
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }
        return sb.toString();
    }

//    public Integer typeInt(String type){
//        String [] typelist={"all","video","image","doc","others"};
//        int i = Arrays.asList(typelist).indexOf(type);
//        return i;
//    }

    //获得文件的分类
    public Integer getfolderType(String s) {
        String[] videoExtensions = {"mp4", "avi", "mkv"};
        String[] audioExtensions = {"mp3", "wav", "ogg"};
        String[] imageExtensions = {"jpg", "jpeg", "png", "gif"};
        String[] documentExtensions = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"};
        String fileExtension = getFileExtension(s);
        if (isInArray(fileExtension, videoExtensions)) {
            return 1;
        } else if (isInArray(fileExtension, audioExtensions)) {
            return 2;//"音频"
        } else if (isInArray(fileExtension, imageExtensions)) {
            return 3;  //"图片";
        } else if (isInArray(fileExtension, documentExtensions)) {
            return 4;//"文档";
        } else {
            return 5;//其他
        }
    }


    //获得文件具体分类
    public Integer getfileType(String s){
         String fileExtension = getFileExtension(s);
        switch (fileExtension) {
            case "mp4":
            case "avi":
            case "mkv":
                return 1; // 视频
            case "mp3":
            case "wav":
            case "ogg":
                return 2; // 音频
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                return 3; // 图片
            case "pdf":
                return 4; // PDF
            case "doc":
            case "docx":
                return 5; // Word文档
            case "xls":
            case "xlsx":
                return 6; // Excel表格
            case "txt":
                return 7; // 文本文件
            case "zip":
            case "rar":
                return 9; // 压缩包
            default:
                return 10; // 其他
        }
    }


    //获得文件后缀
    private static String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filePath.substring(lastDotIndex + 1).toLowerCase();
    }

    //存在判断
    private static boolean isInArray(String value, String[] array) {
        for (String element : array) {
            if (element.equals(value)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isEmpty(String str) {

        if (null == str || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }
}
