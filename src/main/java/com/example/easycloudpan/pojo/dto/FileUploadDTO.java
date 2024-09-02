package com.example.easycloudpan.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class FileUploadDTO implements Serializable {

    private String id;

    @NotEmpty
    private String filename;

    private String filePid;

    @NotEmpty
    private String fileMd5;

    @NotNull
    private Integer chunkIndex;

    @NotNull
    private Integer chunks;
}
