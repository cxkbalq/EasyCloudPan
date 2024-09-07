package com.example.easycloudpan.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SessionWebUserVO implements Serializable {
    private String nickname;
    private String userId;
    private Boolean isAdmin;
    private String avatar;
    private String jwt;
}
