package com.example.easycloudpan.pojo.dto;

import com.example.easycloudpan.pojo.UserInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class UserInfoDto implements Serializable {
    private Long totalCount;
    private Long pageSize;
    private Long pageNo;
    private Long pageTotal;
    private List<UserInfo> list;
}
