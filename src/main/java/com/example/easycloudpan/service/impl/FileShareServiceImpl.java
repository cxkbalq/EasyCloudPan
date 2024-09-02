package com.example.easycloudpan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.easycloudpan.mapper.FileShareMapper;
import com.example.easycloudpan.pojo.FileShare;
import com.example.easycloudpan.service.FileShareService;
import org.springframework.stereotype.Service;

@Service
public class FileShareServiceImpl extends ServiceImpl<FileShareMapper, FileShare> implements FileShareService {
}
