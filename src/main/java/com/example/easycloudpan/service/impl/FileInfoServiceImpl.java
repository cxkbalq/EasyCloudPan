package com.example.easycloudpan.service.impl;

import com.example.easycloudpan.mapper.FileInfoMapper;
import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.service.FileInfoService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {

}
