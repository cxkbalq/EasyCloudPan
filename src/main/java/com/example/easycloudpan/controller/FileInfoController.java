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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    //文件路径
    @Value("${easycloudpan.filepath}")
    private String filepath;

    //获取文件列表
    @PostMapping("/loadDataList")
    public R<FileInfoDto> loadDataList(@RequestParam String category,
                                       @RequestParam(required = false) String filePid,
                                       @RequestParam(required = false) String fileNameFuzzy,
                                       @RequestParam(required = false) String pageNo,
                                       @RequestParam(required = false) String pageSize) {
        //转化类型
        String[] typelist = {"all", "video", "music", "image", "doc", "others"};
        int type = Arrays.asList(typelist).indexOf(category);
        //设置分页参数
//        if (pageNo == "") {
//            pageNo = "1";
//            pageSize = "15";
//        }

        Page<FileInfo> page = new Page<>(Long.valueOf(pageNo), Long.valueOf(pageSize));
        /*        Page<FileInfo> page=new Page<>(1,15);*/
        //构建查询条件
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getDelFlag,0);
        if (filePid == null) {
            filePid = String.valueOf(0);
        }
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
     * @param session
     */

    @GetMapping("ts/getVideoInfo/{fileId}")
    public void getVideo(HttpServletResponse response, @PathVariable("fileId") String fileId, HttpSession session) {
        //构建返回路径
        String userid = session.getAttribute("userid").toString();
        String path;
        if (fileId.endsWith(".ts")) {
            String[] split = fileId.split("_");
            path = filepath + userid + "\\" + split[0] + "\\" + fileId;
        } else {
            path = filepath + userid + "\\" + fileId + "\\" + fileId + ".m3u8";
        }
        fileUtil.readFile(response, path);
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
        fileInfo.setUserId(Long.valueOf(userid));
        fileInfo.setFilePid(filePid);
        fileInfo.setFileId(new StringUtil().generateRandomString(10));
        fileInfo.setStatus(2);
        fileInfo.setFolderType(1);
        fileInfo.setDelFlag(0);
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
        //分割字符串
        String[] path = filepath.split("/");
        //保存结果
        List<FileInfo> results = new ArrayList<>();
        // 2. 遍历每个分割的部分并执行查询
        for (String part : path) {
            LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(FileInfo::getFileId, part);
            lambdaQueryWrapper.eq(FileInfo::getUserId, userid);
            FileInfo one = fileInfoService.getOne(lambdaQueryWrapper);
            results.add(one);
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
        lambdaUpdateWrapper.set(FileInfo::getLastUpdateTime,LocalDateTime.now());
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
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getFolderType, 1);
        lambdaQueryWrapper.eq(FileInfo::getUserId, userid);
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
        return R.success(one.getFileMd5());
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
        String[] split_id = fileIds.split(",");
        LambdaUpdateWrapper<FileInfo>lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        for (String id:split_id){
            lambdaUpdateWrapper.set(FileInfo::getDelFlag,1);
            lambdaUpdateWrapper.set(FileInfo::getRecoveryTime,LocalDateTime.now());
            lambdaUpdateWrapper.set(FileInfo::getLastUpdateTime,LocalDateTime.now());
            lambdaUpdateWrapper.eq(FileInfo::getFileId,id).eq(FileInfo::getUserId,userid);
            boolean update = fileInfoService.update(lambdaUpdateWrapper);
            if(!update){
                log.info("删除数据更新失败！");
                return R.error("删除数据更新失败！");
            }
        }

        return R.success("删除成功！");
    }

    //    @PostMapping("/newFoloder")
//    public R<String> rename2(HttpSession session, @RequestParam("fileId") String fileId,
//                             @RequestParam("fileName") String fileName) {
//        String userid = session.getAttribute("userid").toString();
//        return null;
//    }

}
