package com.example.easycloudpan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.easycloudpan.pojo.EmailCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  邮箱验证码Mapper 接口
 * </p>

 */
@Mapper
public interface EmailCodeMapper extends BaseMapper<EmailCode> {

}
