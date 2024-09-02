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
    private String code;
    private int showCount;
    private String fileName;
    private int folderType;
    private int fileCategory;
    private int fileType;
    private String fileCover;

}
