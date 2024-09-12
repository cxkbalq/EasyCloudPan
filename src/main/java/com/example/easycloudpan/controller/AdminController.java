package com.example.easycloudpan.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.easycloudpan.common.R;
import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.pojo.UserInfo;
import com.example.easycloudpan.pojo.UserInfo;
import com.example.easycloudpan.pojo.dto.FileInfoDto;
import com.example.easycloudpan.pojo.dto.UserInfoDto;
import com.example.easycloudpan.service.FileInfoService;
import com.example.easycloudpan.service.UserInfoServise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.easycloudpan.utils.FileUtil;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//3200839842@qq.com
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @Autowired
    private UserInfoServise userInfoServise;
    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private FileInfoService fileInfoService;
    @Value("${easycloudpan.rootuser}")
    private String root;
    @Value("${easycloudpan.sys_diskspace}")
    private String diskFreeSpace;
    @Value("${easycloudpan.sys_space}")
    private String sysSpace;
    @Autowired
    private RedisTemplate redisTemplate;
    // 定义常量，1 MB = 1024 * 1024 字节
    //文件路径
    @Value("${easycloudpan.filepath}")
    private String filepath;
    private static final long MEGABYTE_TO_BYTES = 1024L * 1024L;

    /***
     * 管理员获取用户列表
     * @param session
     * @param status
     * @param nickNameFuzzy
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping("/loadUserList")
    public R<UserInfoDto> loadUserList(HttpSession session,
                                       @RequestParam(value = "status", required = false) String status,
                                       @RequestParam(value = "nickNameFuzzy", required = false) String nickNameFuzzy,
                                       @RequestParam("pageNo") String pageNo,
                                       @RequestParam("pageSize") String pageSize) {

        //String userid = session.getAttribute("userid").toString();
        String rootzh = session.getAttribute("root").toString();

        //验证是否为管理用户
        if (!rootzh.equals(root)) {
            return R.error("非法操作！");
        }

        // 构建缓存键
        String cacheKey = String.format("userList:root:%s:status:%s:nickNameFuzzy:%s:pageNo:%s:pageSize:%s",
                rootzh, status, nickNameFuzzy, pageNo, pageSize);
        String cachekey1 = String.format("admin:user:%s:loadUserList", rootzh);

        //判断数据是否以及被修改
        if (redisTemplate.opsForValue().get(cachekey1) == null) {
            //为空已经被修改,删除原数据
            redisTemplate.delete(cacheKey);
            redisTemplate.opsForValue().set(cachekey1, 1, 10, TimeUnit.MINUTES);
        }

        // 从 Redis 获取缓存数据
        UserInfoDto cachedDto = (UserInfoDto) redisTemplate.opsForValue().get(cacheKey);
        if (cachedDto != null) {
            return R.success(cachedDto);
        }

        Page<UserInfo> page = new Page<>(Long.valueOf(pageNo), Long.valueOf(pageSize));
        //构建查询条件
        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //去除管理用户
        lambdaQueryWrapper.notIn(UserInfo::getEmail, rootzh);
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
        // 更新缓存
        redisTemplate.opsForValue().set(cacheKey, userInfoDto, 10, TimeUnit.MINUTES);
        return R.success(userInfoDto);
    }

    /***
     * 更新用户状态
     * @param session
     * @param status
     * @param userid
     * @return
     */
    @PostMapping("/updateUserStatus")
    public R<String> updateUserStatus(HttpSession session,
                                      @RequestParam(value = "status", required = false) String status,
                                      @RequestParam(value = "userId", required = false) String userid) {

        String rootzh = session.getAttribute("root").toString();
        //验证是否为管理用户
        if (!rootzh.equals(root)) {
            return R.error("非法操作！");
        }


        LambdaUpdateWrapper<UserInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(UserInfo::getUserId, userid);
        lambdaUpdateWrapper.set(UserInfo::getStatus, status).set(UserInfo::getUpdateTime, LocalDateTime.now());
        if (userInfoServise.update(lambdaUpdateWrapper)) {
            //数据发生更新
            String cachekey1 = String.format("admin:user:%s:loadUserList", rootzh);
            redisTemplate.delete(cachekey1);
            return R.success("更新成功!");
        }
        return R.error("更新失败");
    }

    /***
     * 更新用户空间
     * @param session
     * @param totalSpace
     * @param useSpace
     * @param userid
     * @return
     */
    @PostMapping("/updateUserSpace")
    public R<String> updateUserSpace(HttpSession session,
                                     @RequestParam(value = "changeSpace", required = false) String totalSpace,
                                     @RequestParam(value = "useSpace", required = false) String useSpace,
                                     @RequestParam(value = "userId", required = false) String userid) {

        String rootzh = session.getAttribute("root").toString();
        //验证是否为管理用户
        if (!rootzh.equals(root)) {
            return R.error("非法操作！");
        }

        // 将兆字节转换为字节
        long bytes = Long.valueOf(totalSpace) * MEGABYTE_TO_BYTES;
        //获取文件盘符所剩余空间
        File diskPartition = new File(diskFreeSpace + ":"); // 根目录，或指定其他目录
        long freeSpace = diskPartition.getUsableSpace(); // 获取剩余空间

        if (bytes > freeSpace - Long.valueOf(sysSpace) * MEGABYTE_TO_BYTES) {
            return R.error("磁盘可用空间不足，请扩充磁盘！");
        }

        if (bytes < Long.valueOf(useSpace)) {
            return R.error("修改数据必须大于已使用空间");
        }
        LambdaUpdateWrapper<UserInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(UserInfo::getUserId, userid);
        lambdaUpdateWrapper.set(UserInfo::getTotalSpace, bytes).set(UserInfo::getUpdateTime, LocalDateTime.now());
        if (userInfoServise.update(lambdaUpdateWrapper)) {
            //数据发生更新
            String cachekey1 = String.format("admin:user:%s:loadUserList", rootzh);
            redisTemplate.delete(cachekey1);
            return R.success("更新成功!");
        }
        return R.error("更新失败");
    }

    /***
     * 管理端加载所有文件列表
     * @param session
     * @param fileNameFuzzy
     * @param pageNo
     * @param pageSize
     * @param filePid
     * @return
     */

    @PostMapping("/loadFileList")
    public R<FileInfoDto> loadFileList(HttpSession session,
                                       @RequestParam(value = "fileNameFuzzy", required = false) String fileNameFuzzy,
                                       @RequestParam("pageNo") String pageNo,
                                       @RequestParam("pageSize") String pageSize,
                                       @RequestParam("filePid") String filePid) {


        String rootzh = session.getAttribute("root").toString();
        //验证是否为管理用户
        if (!rootzh.equals(root)) {
            return R.error("非法操作！");
        }
        // 构建缓存键
        String cacheKey = String.format("fileList:root:%s:fileNameFuzzy:%s:pageNo:%s:pageSize:%s:filePid:%s",
                rootzh, fileNameFuzzy, pageNo, pageSize, filePid);
        String cachekey1 = String.format("admin:user:%s:loadFileList", rootzh);

        //判断数据是否以及被修改
        if (redisTemplate.opsForValue().get(cachekey1) == null) {
            //为空已经被修改,删除原数据
            redisTemplate.delete(cacheKey);
            redisTemplate.opsForValue().set(cachekey1, 1, 10, TimeUnit.MINUTES);
        }
        // 尝试从 Redis 获取缓存数据
        FileInfoDto cachedDto = (FileInfoDto) redisTemplate.opsForValue().get(cacheKey);
        if (cachedDto != null) {
            return R.success(cachedDto);
        }

        Page<FileInfo> page = new Page<>(Long.valueOf(pageNo), Long.valueOf(pageSize));
        //构建查询条件
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(FileInfo::getDelFlag,0).eq(FileInfo::getUserId,userid);
        if (filePid == null) {
            filePid = String.valueOf(0);
        }
        lambdaQueryWrapper.eq(FileInfo::getFilePid, filePid);
        if (fileNameFuzzy != null) {
            lambdaQueryWrapper.like(FileInfo::getFileName, fileNameFuzzy);
        }

//        if (type != 0) {
//            lambdaQueryWrapper.eq(FileInfo::getFileCategory, type);
//        }
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
     * * 管理员获得当前文件目录
     * @param session
     * @param filepath
     * @param shareId
     * @return
     */
    @PostMapping("/getFolderInfo")
    public R<List<FileInfo>> getFolderInfo(HttpSession session,
                                           @RequestParam("path") String filepath,
                                           @RequestParam("shareId") String shareId) {
        String rootzh = session.getAttribute("root").toString();
        //验证是否为管理用户
        if (!rootzh.equals(root)) {
            return R.error("非法操作！");
        }
        //分割字符串
        String[] path = filepath.split("/");
        //保存结果
        List<FileInfo> results = new ArrayList<>();
        // 2. 遍历每个分割的部分并执行查询
        for (String part : path) {
            LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(FileInfo::getFileId, part);
//            lambdaQueryWrapper.eq(FileInfo::getUserId, userid);
            FileInfo one = fileInfoService.getOne(lambdaQueryWrapper);
            results.add(one);
        }
        return R.success(results);
    }


    /***
     *  管理员创建下载连接
     * @param session
     * @param userId
     * @param fileid
     * @return
     */
    @PostMapping("/createDownloadUrl/{userId}/{id}")
    public R<String> createDownloadUrl(HttpSession session, @PathVariable("userId") String userId,
                                       @PathVariable("id") String fileid) {
        String rootzh = session.getAttribute("root").toString();
        //验证是否为管理用户
        if (!rootzh.equals(root)) {
            return R.error("非法操作！");
        }
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getUserId, userId).eq(FileInfo::getFileId, fileid);
        FileInfo one = fileInfoService.getOne(lambdaQueryWrapper);
        return R.success(one.getFileMd5());
    }

    /***
     * 管理端视频播放
     * @param response
     * @param fileId
     * @param userid
     * @param session
     */
    @GetMapping("ts/getVideoInfo/{userId}/{fileId}")
    public void getVideo(HttpServletResponse response,
                         @PathVariable("fileId") String fileId,
                         @PathVariable("userId") String userid,
                         HttpSession session) {
        String rootzh = session.getAttribute("root").toString();
        //验证是否为管理用户
        if (!rootzh.equals(root)) {
            log.info("非法播放操作！");
            return;
        }
        String path;
        if (fileId.endsWith(".ts")) {
            String[] split = fileId.split("_");
            path = filepath + File.separator + split[0] + File.separator + fileId;
        } else {
            path = filepath + File.separator + fileId + File.separator + fileId + ".m3u8";
        }
        fileUtil.readFile(response, path);
    }


    /***
     * 更新用户文件状态
     * @param session
     * @param status
     * @param userid
     * @return
     */
    @PostMapping("/updateFileStatus")
    public R<String> updateFileStatus(HttpSession session,
                                      @RequestParam(value = "status", required = false) String status,
                                      @RequestParam(value = "fileId", required = false) String fileId,
                                      @RequestParam(value = "userId", required = false) String userid) {

        String rootzh = session.getAttribute("root").toString();
        //验证是否为管理用户
        if (!rootzh.equals(root)) {
            return R.error("非法操作！");
        }
        LambdaUpdateWrapper<FileInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(FileInfo::getUserId, userid);
        lambdaUpdateWrapper.eq(FileInfo::getFileId, fileId);
        lambdaUpdateWrapper.set(FileInfo::getFengJing, status).set(FileInfo::getLastUpdateTime, LocalDateTime.now());
        if (fileInfoService.update(lambdaUpdateWrapper)) {
            //数据发生更新
            String cachekey1 = String.format("admin:user:%s:loadFileList", rootzh);
            redisTemplate.delete(cachekey1);
            return R.success("更新成功!");
        }
        return R.error("更新失败");
    }


}
