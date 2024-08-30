package com.example.easycloudpan.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.pojo.UserInfo;
import com.example.easycloudpan.pojo.dto.FileUploadDTO;
import com.example.easycloudpan.pojo.vo.UploadResultVO;
import com.example.easycloudpan.service.FileInfoService;
import com.example.easycloudpan.service.UserInfoServise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class FileUtil {
    @Autowired
    FileInfoService fileInfoService;
    @Autowired
    UserInfoServise userInfoServise;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    VideoImageUtil videoImageUtil;
    @Value("${easycloudpan.temppath}")
    private String temppath;
    @Value("${easycloudpan.filepath}")
    private String filepath;

    //秒传判断
    public Boolean isExist(String md5) {
        FileInfo one = fileInfoService.getOne(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getFileMd5, md5));
        //如果存在这个文件
        if (one != null) {
            return true;
        } else {
            return false;
        }
    }

    //秒传
    public UploadResultVO miaoChan(FileInfo one, FileUploadDTO fileUploadDTO, HttpSession session) {
        String userid = session.getAttribute("userid").toString();
        //创建数据库更新结果
        FileInfo fileInfo = new FileInfo();
        BeanUtils.copyProperties(fileInfo, one);
        fileInfo.setFilePid(one.getFileId());
        fileInfo.setCreateTime(LocalDateTime.now());
        fileInfo.setLastUpdateTime(LocalDateTime.now());
        String s = new StringUtil().generateRandomString(10);
        fileInfo.setFileId(s);
        fileInfo.setFilePath(one.getFilePath());
        fileInfo.setUserId(Long.valueOf(userid));
        fileInfo.setFolderType(0);
        fileInfoService.save(fileInfo);
        //创建返回结果
        UploadResultVO uploadResultVO = new UploadResultVO();
        uploadResultVO.setId(fileUploadDTO.getId());
        uploadResultVO.setStatus("upload_seconds");
        return uploadResultVO;
    }

    //分片上传
    public UploadResultVO fenPian(FileUploadDTO fileUploadDTO, MultipartFile file, HttpSession session) throws Exception {
        fileUploadDTO.setFilename(fileUploadDTO.getFilename().trim());
        //获取userid
        String userid = (String) session.getAttribute("userid");
        //创建同一返回对象
        UploadResultVO uploadResultVO = new UploadResultVO();
        //剩余空间判断
        if (!kongJIan(fileUploadDTO, session, file.getSize()) && fileUploadDTO.getChunks() == 0) {
            uploadResultVO.setStatus("fail");
            uploadResultVO.setId(fileUploadDTO.getId());
            return uploadResultVO;
        }
        //如何上传到最后一个
        if (Integer.valueOf(fileUploadDTO.getChunkIndex()) == Integer.valueOf(fileUploadDTO.getChunks() - 1)) {
            try {
                file.transferTo(new File(temppath + fileUploadDTO.getFileMd5() + "." + fileUploadDTO.getChunkIndex()));
                uploadResultVO.setId(fileUploadDTO.getId());
                //创建工具类
                StringUtil stringUtil = new StringUtil();
                //设置返回状态
                uploadResultVO.setStatus("upload_finish");
                //生成新的filename
                String s1 = new StringUtil().generateRandomString(10);
                String s = s1 + fileUploadDTO.getFilename();
                //合并文件,获得文件大小
                Long filesize = heBing(fileUploadDTO, session, s);
                //合并失败
                if (filesize == 0l) {
                    return uploadResultVO;
                }

                //更新数据库
                FileInfo fileInfo = new FileInfo();
                //获取文件类型
                /**
                 * 文件分类 1:视频 2:音频 3:图片 4:文档 5:其他
                 */
                Integer fileType = stringUtil.getfolderType(fileUploadDTO.getFilename());
                if (fileType == 1 || fileType == 2) {
                    /**
                     * 0:转码中 1:转码失败 2:转码成功
                     */
                    fileInfo.setStatus(0);
                } else {
                    fileInfo.setStatus(2);
                }


                fileInfo.setFileMd5(fileUploadDTO.getFileMd5());
                fileInfo.setFileType(stringUtil.getfileType(fileUploadDTO.getFilename()));
                fileInfo.setFileCategory(fileType);
                fileInfo.setLastUpdateTime(LocalDateTime.now());
                //判断父id
                if (fileUploadDTO.getFilePid() != null) {
                    fileInfo.setFilePid(fileUploadDTO.getFilePid());
                } else {
                    fileInfo.setFilePid("0");
                }
                //设置文件路径
                fileInfo.setFilePath(s);
                fileInfo.setFileName(fileUploadDTO.getFilename());
                fileInfo.setFileSize(filesize);

                fileInfo.setDelFlag(0);
                fileInfo.setFileId(s1);
                fileInfo.setUserId(Long.valueOf(userid));
                fileInfo.setFolderType(0);
                fileInfoService.save(fileInfo);
                //更新用户的使用空间
                LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UserInfo::getUserId, userid);
                LambdaUpdateWrapper<UserInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                UserInfo one = userInfoServise.getOne(lambdaQueryWrapper);
                one.setUseSpace(one.getUseSpace() + filesize);
                lambdaUpdateWrapper.set(UserInfo::getUseSpace, one.getUseSpace());
                userInfoServise.update(one, lambdaQueryWrapper);

                //如果是特殊文件需要进行处理
//                String videopath= fileUploadDTO.getFilename();
                videoImageChuLi(fileType, s, session, s1);
                return uploadResultVO;
            } catch (IOException e) {
                log.error("上传失败");
                uploadResultVO.setId(fileUploadDTO.getId());
                //上传失败
                uploadResultVO.setStatus("fail");
                return uploadResultVO;
            }
        } else {
            //不是最后第一个
            try {
                File file1 = new File(temppath);
                file.transferTo(new File(temppath + fileUploadDTO.getFileMd5() + "." + fileUploadDTO.getChunkIndex()));
                uploadResultVO.setId(fileUploadDTO.getId());
                uploadResultVO.setStatus("uploading");
                return uploadResultVO;
            } catch (IOException e) {
                log.error("上传失败");
                uploadResultVO.setId(fileUploadDTO.getId());
                //上传失败
                uploadResultVO.setStatus("fail");
                return uploadResultVO;
            }
        }
    }

    //文件切片，封面，图片缩略图获取
    public Boolean videoImageChuLi(Integer type, String filename, HttpSession session, String fileId) throws Exception {
        String userid = (String) session.getAttribute("userid");
        String outputFile = filepath + userid + "\\" + filename;
        //进行视频处理
        if (type == 1) {
        //    D:\Downloads\file\1784458528288247809\RYzvdHGlUN7zwoIgSKJZinput.mp4
            videoImageUtil.cutFile4Video(fileId, outputFile, filepath + userid);
            videoImageUtil.createCover4Video(new File(outputFile), 300, new File(filepath + userid + "\\" + "croveImage" + "\\" + fileId + ".png"));
        }
        //进行图片处理
        if (type == 3) {
            videoImageUtil.compressImage(new File(outputFile), 300, new File(filepath + userid + "\\" + "croveImage"+"\\"+ fileId + ".png"), false,fileId);
        }
        return true;
    }

    //文件接收
    public UploadResultVO jieShou(FileUploadDTO fileUploadDTO, MultipartFile file, HttpSession session) throws Exception {
        //秒传
        if (isExist(fileUploadDTO.getFileMd5())) {
            //判断数据库里是否存在这个文件
            FileInfo one = fileInfoService.getOne(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getFileMd5, fileUploadDTO.getFileMd5()));
            UploadResultVO uploadResultVO = miaoChan(one, fileUploadDTO, session);
            return uploadResultVO;
        }
        //分片上传
        else {
            UploadResultVO uploadResultVO = fenPian(fileUploadDTO, file, session);
            return uploadResultVO;
        }
    }


    //合并文件
    public Long heBing(FileUploadDTO fileUploadDTO, HttpSession session, String filename) throws IOException {
        /*        String filename=filename1.trim();*/
        log.info("开始文件合并");
        String userid = (String) session.getAttribute("userid");
        String outputFile = filepath + userid + "\\" + filename;
//        if(outputFile.length()>100){
//            log.error("文件名长度过大");
//            log.error("文件合并错误！");
//            return 0l;
//        }
        try (RandomAccessFile outputRaf = new RandomAccessFile(outputFile, "rw");
             FileChannel outputChannel = outputRaf.getChannel()) {
            long position = 0;
            for (int i = 0; i < fileUploadDTO.getChunks(); i++) {
                String inputFile = temppath + fileUploadDTO.getFileMd5() + "." + String.valueOf(i);
                try (RandomAccessFile inputRaf = new RandomAccessFile(inputFile, "r");
                     FileChannel inputChannel = inputRaf.getChannel()) {
                    long size = inputChannel.size();
                    MappedByteBuffer inputBuffer = inputChannel.map(FileChannel.MapMode.READ_ONLY, 0, size);
                    MappedByteBuffer outputBuffer = outputChannel.map(FileChannel.MapMode.READ_WRITE, position, size);
                    outputBuffer.put(inputBuffer);
                    position += size;
                    // 手动清理 MappedByteBuffer这个会占用文件的句柄需要释放了
                    clean(inputBuffer);
                    clean(outputBuffer);
                }
            }
            // 关闭输出文件的 RandomAccessFile 和 FileChannel
            outputRaf.close();
            outputChannel.close();

            log.info("文件合并成功！");
            for (int i = 0; i < fileUploadDTO.getChunks(); i++) {
                String inputFile = temppath + fileUploadDTO.getFileMd5() + "." + String.valueOf(i);
                File file = new File(inputFile);
                boolean delete = file.delete();
                if (delete) {
                    log.info("删除成功！");
                } else {
                    log.error("删除失败！" + inputFile);
                }
            }
            log.info("临时文件清理成功！");
        } catch (Exception e) {
            log.error("文件合并错误！");
            return 0l;
        }
        File file = new File(outputFile);
        long length = file.length();
        return length;
    }

    //用户剩余空间的判断
    public Boolean kongJIan(FileUploadDTO fileUploadDTO, HttpSession session, Long fileChunkSize) {
        //计算文件大小
        Long fileSize = fileChunkSize * fileUploadDTO.getChunks();
        String userid = session.getAttribute("userid").toString();
        UserInfo userInfo = (UserInfo) redisTemplate.opsForValue().get(userid);
        if (userInfo.getTotalSpace() - userInfo.getUseSpace() < fileSize) {
            return false;
        }
        return true;
    }

    //清楚MappedByteBuffer的句柄
    private void clean(MappedByteBuffer buffer) {
        if (buffer == null) return;
        try {
            Method cleanerMethod = buffer.getClass().getMethod("cleaner");
            cleanerMethod.setAccessible(true);
            Object cleaner = cleanerMethod.invoke(buffer);
            if (cleaner != null) {
                Method cleanMethod = cleaner.getClass().getMethod("clean");
                cleanMethod.invoke(cleaner);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //文件初始化
    private boolean inIt(FileUploadDTO fileUploadDTO, Integer integer) {
        //产看当前的redis是否存在这个上传
        FileUploadDTO fileUploadDTO1 = (FileUploadDTO) redisTemplate.opsForValue().get(fileUploadDTO.getFileMd5());
        if (fileUploadDTO1 == null) {
            //储存当前上传数据
            redisTemplate.opsForValue().set(fileUploadDTO.getFileMd5(), fileUploadDTO, 100, TimeUnit.MINUTES);
        } else {
            //判断是否
        }
        return true;
    }
    //文件进行转码

    //webFile输出文件 ,向前端写入文件
    public void readFile(HttpServletResponse response, String filePath) {
        OutputStream out = null;
        FileInputStream in = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            in = new FileInputStream(file);
            byte[] byteData = new byte[1024];
            out = response.getOutputStream();
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            log.error("读取文件异常", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("IO异常", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("IO异常", e);
                }
            }
        }
    }

}
