package com.example.easycloudpan.common;

import lombok.Data;

@Data
public class R<T> {
    private String status;
    private int code;
    private String info;
    private T data;

    // 省略构造函数、getter 和 setter 方法

    // 示例静态方法，用于创建成功的响应
    public static <T> R<T> success(T data) {
        R<T> response = new R<>();
        response.setStatus("success");
        response.setCode(200);
        response.setInfo("请求成功");
        response.setData(data);
        return response;
    }

    // 示例静态方法，用于创建失败的响应
    public static <T> R<T> error(String info) {
        R<T> response = new R<>();
        response.setStatus("error");
        response.setCode(1001);
        response.setInfo(info);
//        response.setData(info);
        return response;
    }
}