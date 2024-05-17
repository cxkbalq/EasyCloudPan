package com.example.easycloudpan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.easycloudpan.mapper.UserInfoMapper;
import com.example.easycloudpan.pojo.UserInfo;
import com.example.easycloudpan.service.UserInfoServise;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiseImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoServise{
}
