package com.example.easycloudpan.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetFolderInfoVo implements Serializable {
    private String fileName;
    private String fileId;
}
