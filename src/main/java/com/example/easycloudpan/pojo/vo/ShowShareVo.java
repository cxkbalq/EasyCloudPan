package com.example.easycloudpan.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ShowShareVo {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shareTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
    private String nickName;
    private String fileName;
    private Boolean currentUser;
    private String fileId;
    private String avatar;
    private String userId;
}
