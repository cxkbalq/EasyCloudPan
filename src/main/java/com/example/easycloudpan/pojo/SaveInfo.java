package com.example.easycloudpan.pojo;

import lombok.Data;

import java.io.Serializable;


/***
 * 这个类的创建用户解决，保存文件所出现的问题，在原项目中并没有对每个用户文件进行特定划归
 * 我对文件进行了分类处理，所以需要一个中间类，获得路径的构建，这样就不要复制文件进行保存
 * 使空间的利用最大化
 */

@Data
public class SaveInfo  implements Serializable {
    //兼容
     private static final long serialVersionUID = 1L;

    private String saveId;
    private String xianUserId;
    private String yuanUserId;
    private String xianFileId;
    private String yuanFileId;
    private String shareId;
}
