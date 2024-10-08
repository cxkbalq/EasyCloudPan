package com.example.easycloudpan.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.easycloudpan.pojo.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 文件信息表 Mapper 接口
 * </p>
 *

 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

}
