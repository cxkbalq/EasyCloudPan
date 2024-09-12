package com.example.easycloudpan.pojo.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
public class FileShareVo implements Serializable {

    private String shareId;
    private String fileId;
    private String userId;
    private int validType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shareTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdateTime;
    private String code;
    private int browseCount;
    private String fileName;
    private String folderType;
    private String fileCategory;
    private String fileType;
    private String fileCover;
    private Long fileSize;

}
