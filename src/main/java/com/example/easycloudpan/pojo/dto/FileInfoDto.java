package com.example.easycloudpan.pojo.dto;

import com.example.easycloudpan.pojo.FileInfo;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
//用于返回分页查询数据
@Data
public class FileInfoDto implements Serializable {
    private Long totalCount;
    private Long pageSize;
    private Long pageNo;
    private Long pageTotal;
    private List<FileInfo> list;
}
