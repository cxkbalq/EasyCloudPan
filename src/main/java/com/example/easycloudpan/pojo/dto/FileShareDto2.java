package com.example.easycloudpan.pojo.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
public class FileShareDto2 implements Serializable {

    private String fileId;
    private String userId;
    private String fileMd5;
    private String filePid;
    private String fileSize;
    private String fileName;
    private String fileCover;
    private String filePath;
    private String folderType;
    private String fileCategory;
    private String fileType;
    private int status;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String recoveryTime;
    private int delFlag;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdateTime;
    private boolean showOp;
    private int validType;
    private int codeType;
    private String code;


}
