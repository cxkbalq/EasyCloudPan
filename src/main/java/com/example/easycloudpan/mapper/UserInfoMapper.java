package com.example.easycloudpan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.easycloudpan.pojo.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户信息 Mapper 接口
 * </p>
 *
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
