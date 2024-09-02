package com.example.easycloudpan.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.easycloudpan.common.R;
import com.example.easycloudpan.pojo.UserInfo;
import com.example.easycloudpan.pojo.UserInfo;
import com.example.easycloudpan.pojo.dto.UserInfoDto;
import com.example.easycloudpan.service.UserInfoServise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

//3200839842@qq.com
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserInfoServise userInfoServise;
    @Value("${easycloudpan.rootuser}")
    private String root;
    @PostMapping("/loadUserList")
    public R<UserInfoDto> loadUserList(HttpSession session,
                                       @RequestParam(value = "status",required = false) String status,
                                       @RequestParam(value = "nickNameFuzzy",required = false) String nickNameFuzzy,
                                       @RequestParam("pageNo") String pageNo,
                                       @RequestParam("pageSize") String pageSize) {

        //String userid = session.getAttribute("userid").toString();
        String rootzh = session.getAttribute("root").toString();
        //验证是否为管理用户
        if (!rootzh.equals(root)) {
            return R.error("非法操作！");
        }

        Page<UserInfo> page = new Page<>(Long.valueOf(pageNo), Long.valueOf(pageSize));
        //构建查询条件
        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            lambdaQueryWrapper.eq(UserInfo::getStatus, Integer.valueOf(status));
        }
        if (nickNameFuzzy != null) {
            lambdaQueryWrapper.like(UserInfo::getNickName, nickNameFuzzy);
        }

        Page<UserInfo> page1 = userInfoServise.page(page, lambdaQueryWrapper);
        //构建返回对象
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setList(page1.getRecords());
        userInfoDto.setPageSize(Long.valueOf(pageSize));
        userInfoDto.setPageNo(Long.valueOf(pageNo));
        userInfoDto.setPageTotal(page1.getTotal());
        userInfoDto.setTotalCount(page1.getCurrent());
        return R.success(userInfoDto);
    }


}
