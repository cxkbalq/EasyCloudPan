package com.example.easycloudpan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.easycloudpan.common.R;
import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.pojo.dto.FileInfoDto;
import com.example.easycloudpan.service.FileInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController()
@RequestMapping("/recycle")
public class RecycleController {

    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/loadRecycleList")
    public R<FileInfoDto> delFile(HttpSession session, @RequestParam(required = false) String pageNo,
                                  @RequestParam(required = false) String pageSize) {
        String userid = session.getAttribute("userid").toString();
        String cacheKey = String.format("fileInfo:user:%s:pageNo:%s:pageSize:%s:delFlag:1", userid, pageNo, pageSize);
        String cachekey1 = String.format("recycle:user:%s:loadRecycleList", userid);

        //判断数据是否以及被修改
        if (redisTemplate.opsForValue().get(cachekey1) == null) {
            //为空已经被修改,删除原数据
            redisTemplate.delete(cacheKey);
            redisTemplate.opsForValue().set(cachekey1, 1, 10, TimeUnit.MINUTES);
        }

        // 从 Redis 获取缓存数据
        FileInfoDto cachedDto = (FileInfoDto) redisTemplate.opsForValue().get(cacheKey);
        if (cachedDto != null) {
            return R.success(cachedDto);
        }

        Page<FileInfo> page = new Page<>(Long.valueOf(pageNo), Long.valueOf(pageSize));
        /*        Page<FileInfo> page=new Page<>(1,15);*/
        //构建查询条件
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getDelFlag, 1).eq(FileInfo::getUserId, userid);
        Page<FileInfo> page1 = fileInfoService.page(page, lambdaQueryWrapper);
        //构建返回对象
        FileInfoDto fileInfoDto = new FileInfoDto();
        fileInfoDto.setList(page1.getRecords());
        fileInfoDto.setPageSize(Long.valueOf(pageSize));
        fileInfoDto.setPageNo(Long.valueOf(pageNo));
        fileInfoDto.setPageTotal(page1.getTotal());
        fileInfoDto.setTotalCount(page1.getCurrent());
        // 更新缓存
        redisTemplate.opsForValue().set(cacheKey, fileInfoDto, 10, TimeUnit.MINUTES);
        return R.success(fileInfoDto);
    }

    /***
     * 恢复文件
     * @param session
     * @param fileIds
     * @return
     */
    @PostMapping("/recoverFile")
    public R<String> recoverFile(HttpSession session, @RequestParam("fileIds") String fileIds) {
        String userid = session.getAttribute("userid").toString();
        //更新数据
        String cachekey1 = String.format("recycle:user:%s:loadRecycleList", userid);
        redisTemplate.delete(cachekey1);
        String cachekey2 = String.format("fileInfo:user:%s:loadDataList", userid);
        //输出发生变化,删除键值
        redisTemplate.delete(cachekey2);

        String[] split = fileIds.split(",");
        for (String fileId : split) {
            LambdaUpdateWrapper<FileInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(FileInfo::getFileId, fileId).eq(FileInfo::getUserId, userid);
            lambdaUpdateWrapper.set(FileInfo::getDelFlag, 0).set(FileInfo::getLastUpdateTime, LocalDateTime.now());
            boolean update = fileInfoService.update(lambdaUpdateWrapper);
        }
        return R.success("更新成功！");
    }

    @PostMapping("/delFile")
    public R<String> delFile(HttpSession session, @RequestParam("fileIds") String fileIds) {
        String userid = session.getAttribute("userid").toString();
        //更新数据
        String cachekey1 = String.format("recycle:user:%s:loadRecycleList", userid);
        redisTemplate.delete(cachekey1);
        String[] split = fileIds.split(",");
        for (String fileId : split) {
            LambdaUpdateWrapper<FileInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(FileInfo::getFileId, fileId).eq(FileInfo::getUserId, userid);
            lambdaUpdateWrapper.set(FileInfo::getDelFlag, 2).set(FileInfo::getLastUpdateTime, LocalDateTime.now());
            boolean update = fileInfoService.update(lambdaUpdateWrapper);
        }
        return R.success("更新成功！");
    }

}
