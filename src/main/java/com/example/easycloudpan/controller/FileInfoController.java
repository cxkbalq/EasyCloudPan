package com.example.easycloudpan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.easycloudpan.common.R;
import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.pojo.dto.FileInfoDto;
import com.example.easycloudpan.pojo.dto.FileUploadDTO;
import com.example.easycloudpan.pojo.vo.UploadResultVO;
import com.example.easycloudpan.service.FileInfoService;
import com.example.easycloudpan.utils.FileUtil;
import com.example.easycloudpan.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/file")
//确保其符合指定的验证规则
@Validated
@Slf4j
public class FileInfoController {
    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private RedisTemplate redisTemplate;
    //文件路径
    @Value("${easycloudpan.filepath}")
    private String filepath;


    //获取文件列表
    @PostMapping("/loadDataList")
    public R<FileInfoDto> loadDataList(HttpSession session,
                                       @RequestParam String category,
                                       @RequestParam(required = false) String filePid,
                                       @RequestParam(required = false) String fileNameFuzzy,
                                       @RequestParam(required = false) String pageNo,
                                       @RequestParam(required = false) String pageSize) {
        log.info("当前类型为：" + category);

        //转化类型
        String[] typelist = {"all", "video", "music", "image", "doc", "others"};
        int type = Arrays.asList(typelist).indexOf(category);
        log.info("当前类型为：" + String.valueOf(type));
        String userid = session.getAttribute("userid").toString();
        if (filePid == null) {
            filePid = String.valueOf(0);
        }
        // 缓存键
        String cacheKey = String.format("fileInfo:user:%s:category:%s:filePid:%s:fileNameFuzzy:%s:pageNo:%s:pageSize:%s",
                userid, type, filePid, fileNameFuzzy, pageNo, pageSize);

        String cachekey1 = String.format("fileInfo:user:%s:loadDataList", userid);

        //判断数据是否以及被修改
        if (redisTemplate.opsForValue().get(cachekey1) == null) {
            //为空已经被修改,删除所有原数据
            // 1:视频 2:音频 3:图片 4:文档 5:其他
            for (int i = 0; i < 6; i++) {
                cacheKey = String.format("fileInfo:user:%s:category:%s:filePid:%s:fileNameFuzzy:%s:pageNo:%s:pageSize:%s",
                        userid, i, filePid, fileNameFuzzy, pageNo, pageSize);
                redisTemplate.delete(cacheKey);
            }

            redisTemplate.opsForValue().set(cachekey1, 1, 10, TimeUnit.MINUTES);
        }

        cacheKey = String.format("fileInfo:user:%s:category:%s:filePid:%s:fileNameFuzzy:%s:pageNo:%s:pageSize:%s",
                userid, type, filePid, fileNameFuzzy, pageNo, pageSize);
        // 从 Redis 获取缓存数据
        FileInfoDto cachedDto = (FileInfoDto) redisTemplate.opsForValue().get(cacheKey);
        if (cachedDto != null) {
            return R.success(cachedDto);
        }

        Page<FileInfo> page = new Page<>(Long.valueOf(pageNo), Long.valueOf(pageSize));
        /*        Page<FileInfo> page=new Page<>(1,15);*/
        //构建查询条件
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getDelFlag, 0).eq(FileInfo::getUserId, userid);

        lambdaQueryWrapper.eq(FileInfo::getFilePid, filePid);
        if (fileNameFuzzy != null) {
            lambdaQueryWrapper.like(FileInfo::getFileName, fileNameFuzzy);
        }

        if (type != 0) {
            lambdaQueryWrapper.eq(FileInfo::getFileCategory, type);
        }
        Page<FileInfo> page1 = fileInfoService.page(page, lambdaQueryWrapper);
        //构建返回对象
        FileInfoDto fileInfoDto = new FileInfoDto();
        fileInfoDto.setList(page1.getRecords());
        fileInfoDto.setPageSize(Long.valueOf(pageSize));
        fileInfoDto.setPageNo(Long.valueOf(pageNo));
        fileInfoDto.setPageTotal(page1.getTotal());
        fileInfoDto.setTotalCount(page1.getCurrent());
        // 更新缓存
        redisTemplate.opsForValue().set(cacheKey, fileInfoDto, 60, TimeUnit.MINUTES);

        return R.success(fileInfoDto);
    }

    //文件上传
    @PostMapping("/uploadFile")
    public R<UploadResultVO> uploadFile(@RequestPart("file") MultipartFile file,
                                        @RequestParam("fileId") String fileId,
                                        @RequestParam("fileName") String fileName,
                                        @RequestParam("filePid") String filePid,
                                        @RequestParam("fileMd5") String fileMd5,
                                        @RequestParam("chunkIndex") String chunkIndex,
                                        @RequestParam("chunks") String chunks,
                                        HttpSession session) throws Exception {

        String userid = session.getAttribute("userid").toString();
        String cachekey1 = String.format("fileInfo:user:%s:loadDataList", userid);

        //输出发生变化,删除键值
        redisTemplate.delete(cachekey1);

        // 构建FileUploadDTO
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setId(fileId);
        fileUploadDTO.setFilePid(filePid);
        fileUploadDTO.setFileMd5(fileMd5);
        fileUploadDTO.setFilename(fileName);
        fileUploadDTO.setChunks(Integer.valueOf(chunks));
        fileUploadDTO.setChunkIndex(Integer.valueOf(chunkIndex));
        UploadResultVO uploadResultVO = fileUtil.jieShou(fileUploadDTO, file, session);
        return R.success(uploadResultVO);
    }

    /**
     * //视频播放
     *
     * @param response
     * @param fileId
     */

    @GetMapping("ts/getVideoInfo/{fileId}")
    public void getVideo(HttpServletResponse response, @PathVariable("fileId") String fileId) {

        //解决保存文件以及秒传文件，出现请求错误id的情况，后期加上redis，提高响应速度
        String cacheKey = "fileInfo:" + fileId.substring(0, 10);
        ValueOperations<String, FileInfo> valueOperations = redisTemplate.opsForValue();
        FileInfo one = valueOperations.get(cacheKey);
        if (one == null) {
            LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(FileInfo::getFileId, fileId.substring(0, 10));
            one = fileInfoService.getOne(lambdaQueryWrapper);
            if (one != null) {
                valueOperations.set(cacheKey, one, 10, TimeUnit.MINUTES);
            }
        }
        if (one != null) {
            //文件实际所处的路径
            String substring = one.getFilePath().substring(0, 10);
            //构建返回路径
            String path;
            if (fileId.endsWith(".ts")) {
                String[] split = fileId.split("_");
                path = filepath + File.separator + substring + File.separator + substring + "_" + split[1];
                log.info(path);
            } else {
                path = filepath + File.separator + substring + File.separator + substring + ".m3u8";
            }
            fileUtil.readFile(response, path);
        }

    }


    /**
     * 创建新目录
     *
     * @param fileId
     * @param fileName
     * @param filePid
     * @return
     */
    @PostMapping("/newFoloder")
    public R<String> newFoloder(HttpSession session, @RequestParam("fileId") String fileId,
                                @RequestParam("fileName") String fileName,
                                @RequestParam("filePid") String filePid) {
        String userid = session.getAttribute("userid").toString();
        String cachekey1 = String.format("fileInfo:user:%s:loadDataList", userid);
        //输出发生变化,删除键值
        redisTemplate.delete(cachekey1);
        //判断是否存在同名目录
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getFileName, fileName);
        lambdaQueryWrapper.eq(FileInfo::getUserId, userid);
        lambdaQueryWrapper.eq(FileInfo::getFilePid, filePid);
        FileInfo one = fileInfoService.getOne(lambdaQueryWrapper);
        if (one != null) {
            return R.error("当前目录下,已存在同名目录！");
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(fileName);
        fileInfo.setUserId(userid);
        fileInfo.setFilePid(filePid);
        fileInfo.setFileId(new StringUtil().generateRandomString(10));
        fileInfo.setStatus(2);
        fileInfo.setFolderType(1);
        fileInfo.setDelFlag(0);
        fileInfo.setFengJing(2);
        fileInfo.setCreateTime(LocalDateTime.now());
        fileInfo.setLastUpdateTime(LocalDateTime.now());
        fileInfoService.save(fileInfo);
        return R.success("创建成功");
    }

    /**
     * 获得当前文件目录
     *
     * @param session
     * @param filepath
     * @param shareId
     * @return
     */
    @PostMapping("/getFolderInfo")
    public R<List<FileInfo>> getFolderInfo(HttpSession session,
                                           @RequestParam("path") String filepath,
                                           @RequestParam("shareId") String shareId) {
        String userid = session.getAttribute("userid").toString();
        // 分割字符串
        String[] pathParts = filepath.split("/");
        // 保存结果
        List<FileInfo> results = new ArrayList<>();
        // Redis 缓存操作
        ValueOperations<String, FileInfo> valueOperations = redisTemplate.opsForValue();


        // 遍历每个分割的部分并执行查询
        for (String part : pathParts) {
            String cacheKey = "fileInfo:" + userid + ":" + part;
            String cachekey1 = String.format("fileInfo:user:%s:loadDataList", userid);
            //判断数据是否以及被修改
            if (redisTemplate.opsForValue().get(cachekey1) == null) {
                //为空已经被修改,删除原数据
                redisTemplate.delete(cacheKey);
                redisTemplate.opsForValue().set(cachekey1, 1, 10, TimeUnit.MINUTES);
            }
            FileInfo fileInfo = valueOperations.get(cacheKey);
            if (fileInfo == null) {
                LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(FileInfo::getFileId, part);
                lambdaQueryWrapper.eq(FileInfo::getUserId, userid);
                fileInfo = fileInfoService.getOne(lambdaQueryWrapper);

                if (fileInfo != null) {
                    valueOperations.set(cacheKey, fileInfo, 60, TimeUnit.MINUTES);
                }
            }

            results.add(fileInfo);
        }

        return R.success(results);
    }

    /***
     * 重新命名文件
     * @param session
     * @param fileId
     * @param fileName
     * @return
     */

    @PostMapping("/rename")
    public R<String> rename(HttpSession session, @RequestParam("fileId") String fileId,
                            @RequestParam("fileName") String fileName) {
        String userid = session.getAttribute("userid").toString();
        String cachekey1 = String.format("fileInfo:user:%s:loadDataList", userid);
        //输出发生变化,删除键值
        redisTemplate.delete(cachekey1);
        //判断同一目录是否存在同名文件
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getUserId, userid);
        lambdaQueryWrapper.eq(FileInfo::getFileId, fileId);
        FileInfo one = fileInfoService.getOne(lambdaQueryWrapper);
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(FileInfo::getFileName, fileName);
        lambdaQueryWrapper1.eq(FileInfo::getUserId, userid);
        if (one.getFilePid() != null) {
            lambdaQueryWrapper1.eq(FileInfo::getFilePid, one.getFilePid());
        }
        FileInfo one1 = fileInfoService.getOne(lambdaQueryWrapper1);

        if (one1 != null) {
            return R.error("当前目录下,已存在同名目录！");
        }
        //判断文件是目录还是文件
        if (one.getFolderType() != 1) {
            fileName = fileName + "." + one.getFileName().split("\\.")[1];
        }
        //更新名称
        LambdaUpdateWrapper<FileInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(FileInfo::getUserId, userid).eq(FileInfo::getFileId, fileId);
        lambdaUpdateWrapper.set(FileInfo::getFileName, fileName);
        lambdaUpdateWrapper.set(FileInfo::getLastUpdateTime, LocalDateTime.now());
        fileInfoService.update(lambdaUpdateWrapper);
        return R.success("重命名成功！");
    }

    /***
     * 获取当前账号下的所有目录
     * @param session
     * @return
     */

    @PostMapping("/loadAllFolder")
    public R<List<FileInfo>> loadAllFolder(HttpSession session) {
        String userid = session.getAttribute("userid").toString();
        // 构建缓存键
        String cacheKey = String.format("fileInfo:user:%s:folderList", userid);
        String cachekey1 = String.format("fileInfo:user:%s:loadDataList", userid);
        //判断数据是否以及被修改
        if (redisTemplate.opsForValue().get(cachekey1) == null) {
            //为空已经被修改,删除原数据
            redisTemplate.delete(cacheKey);
            redisTemplate.opsForValue().set(cachekey1, 1, 10, TimeUnit.MINUTES);
        }
        // 从 Redis 获取缓存数据
        List<FileInfo> cachedList = (List<FileInfo>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedList != null) {
            return R.success(cachedList);
        }
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getFolderType, 1);
        lambdaQueryWrapper.eq(FileInfo::getUserId, userid);
        lambdaQueryWrapper.eq(FileInfo::getDelFlag, 0).eq(FileInfo::getFengJing, 2);
        List<FileInfo> list1 = new ArrayList<>();
        //创建默认目录
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId("0");
        fileInfo.setFileName("根目录");
        list1.add(fileInfo);
        List<FileInfo> list = fileInfoService.list(lambdaQueryWrapper);
        for (FileInfo x : list) {
            list1.add(x);
        }
        if (list.size() == 0 || list == null) {
            R.error("当前未创建目录！");
        }
        // 更新缓存
        redisTemplate.opsForValue().set(cacheKey, list1, 10, TimeUnit.MINUTES);
        return R.success(list1);
    }

    /***
     * 移动文件位置
     * @param session
     * @param fileIds
     * @param filePid
     * @return
     */
    @PostMapping("/changeFileFolder")
    @Transactional
    public R<String> changeFileFolder(HttpSession session, @RequestParam("fileIds") String fileIds,
                                      @RequestParam("filePid") String filePid) {
        String userid = session.getAttribute("userid").toString();
        String cachekey1 = String.format("fileInfo:user:%s:loadDataList", userid);

        //输出发生变化,删除键值
        redisTemplate.delete(cachekey1);
        String[] split = fileIds.split(",");
        //是否有重命名失败的文件
        int flag = 0;
        for (String id : split) {
            if (id.equals(filePid)) {
                flag++;
            }
        }
        if (flag > 0) {
            return R.error("自己不能移动到自己内部哦!");
        }
        flag = 0;
        //更新数据
        for (String id : split) {
            LambdaUpdateWrapper<FileInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(FileInfo::getUserId, userid).eq(FileInfo::getFileId, id);
            lambdaUpdateWrapper.set(FileInfo::getFilePid, filePid);
            boolean update = fileInfoService.update(lambdaUpdateWrapper);
            if (!update) {
                flag++;
            }
        }
        if (flag == 0) {
            return R.success("移动成功！");
        }
        return R.error("移动失败！");
    }

    /**
     * 创建下载连接
     *
     * @param session
     * @param fileid
     * @return
     */

    @PostMapping("/createDownloadUrl/{id}")
    public R<String> createDownloadUrl(HttpSession session, @PathVariable("id") String fileid) {
        String userid = session.getAttribute("userid").toString();
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getUserId, userid).eq(FileInfo::getFileId, fileid);
        FileInfo one = fileInfoService.getOne(lambdaQueryWrapper);
        return R.success(one.getFileMd5() + "_" + one.getFileId());
    }

    /***
     * 删除文件（回收站）
     * @param session
     * @param fileIds
     * @return
     */
    @Transactional
    @PostMapping("/delFile")
    public R<String> delFile(HttpSession session,
                             @RequestParam("fileIds") String fileIds) {
        String userid = session.getAttribute("userid").toString();
        String cachekey1 = String.format("fileInfo:user:%s:loadDataList", userid);
        String cachekey2 = String.format("recycle:user:%s:loadRecycleList", userid);
        //输出发生变化,删除键值
        redisTemplate.delete(cachekey1);
        redisTemplate.delete(cachekey2);
        String[] split_id = fileIds.split(",");
        LambdaUpdateWrapper<FileInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        for (String id : split_id) {
            lambdaUpdateWrapper.set(FileInfo::getDelFlag, 1);
            lambdaUpdateWrapper.set(FileInfo::getRecoveryTime, LocalDateTime.now());
            lambdaUpdateWrapper.set(FileInfo::getLastUpdateTime, LocalDateTime.now());
            lambdaUpdateWrapper.eq(FileInfo::getFileId, id).eq(FileInfo::getUserId, userid);
            boolean update = fileInfoService.update(lambdaUpdateWrapper);
            if (!update) {
                log.info("删除数据更新失败！");
                return R.error("删除数据更新失败！");
            }
        }

        return R.success("删除成功！");
    }


}
