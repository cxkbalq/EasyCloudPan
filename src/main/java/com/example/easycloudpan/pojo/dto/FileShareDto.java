package com.example.easycloudpan.pojo.dto;

import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.pojo.FileShare;
import com.example.easycloudpan.pojo.vo.FileShareVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class FileShareDto implements Serializable {
    private Long totalCount;
    private Long pageSize;
    private Long pageNo;
    private Long pageTotal;
    private List<FileShareVo> list;
}
