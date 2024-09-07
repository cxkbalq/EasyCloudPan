package com.example.easycloudpan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.easycloudpan.mapper.SaveInfoMapper;
import com.example.easycloudpan.pojo.SaveInfo;
import com.example.easycloudpan.service.SaveInfoService;
import org.springframework.stereotype.Service;

@Service
public class SaveInfoServiceImpl extends ServiceImpl<SaveInfoMapper, SaveInfo> implements SaveInfoService {
}
