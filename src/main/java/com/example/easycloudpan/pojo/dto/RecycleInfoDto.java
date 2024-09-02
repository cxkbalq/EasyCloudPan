package com.example.easycloudpan.pojo.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
@Data
public class RecycleInfoDto {
    private String fileId;
    private String filePid;
    private int fileSize;
    private String fileName;
    private String fileCover;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdateTime; // 注意日期格式可能需要转换
    private int folderType;
    private int fileCategory;
    private int fileType;
    private int status;
}
