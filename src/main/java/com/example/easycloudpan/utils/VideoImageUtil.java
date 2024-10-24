package com.example.easycloudpan.utils;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.service.FileInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;

@Slf4j
@Component
public class VideoImageUtil {
    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${easycloudpan.rootuser}")
    private String root;
    @Value("${easycloudpan.path.temppath}")
    private String temppath1;
    @Value("${ffmpeg.mp4YaSuo}")
    private String mp4YaSuo;

    /**
     * @param file           原始图像文件，作为输入进行处理。
     * @param thumbnailWidth 指定的缩略图宽度。
     * @param targetFile     生成的缩略图文件的目标路径。
     * @param delSource      一个布尔值，指示在缩略图生成后是否删除原始图像文件
     * @return
     */
    //创建缩略图
    public Boolean createThumbnailWidthFFmpeg(File file, int thumbnailWidth, File targetFile, Boolean delSource) {
        try {
            BufferedImage src = ImageIO.read(file);
            //thumbnailWidth 缩略图的宽度   thumbnailHeight 缩略图的高度
            int sorceW = src.getWidth();
            int sorceH = src.getHeight();
            //小于 指定高宽不压缩
            if (sorceW <= thumbnailWidth) {
                return false;
            }
//            compressImage(file, thumbnailWidth, targetFile, delSource);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //以宽度百分比来压缩图像
//  public  void compressImageWidthPercentage(String sourceFile, BigDecimal widthPercentage, String targetFile) {
//        try {
//            BigDecimal widthResult = widthPercentage.multiply(new BigDecimal(ImageIO.read(new File(sourceFile)).getWidth()));
//            compressImage(sourceFile, widthResult.intValue(), targetFile, true);
//        } catch (Exception e) {
//            log.error("压缩图片失败");
//        }
//    }

    //生成视频封面
    @Async
    public void createCover4Video(File sourceFile, Integer width, File targetFile) {
        try {
            String cmd = "ffmpeg -i %s -y -vframes 1 -vf scale=%d:%d/a %s";
            ProcessUtils.executeCommand(String.format(cmd, "\"" + sourceFile.getAbsoluteFile() + "\"", width, width, targetFile.getAbsoluteFile()), false);
        } catch (Exception e) {
            log.error("生成视频封面失败", e);
        }
    }

    //压缩图片
    public void compressImage(File sourceFile, Integer width, File targetFile, Boolean delSource, String fileId) {
        try {
            String compressImage = "ffmpeg -i %s -vf scale=%d:-1 %s -y";
            String cmd = String.format(compressImage, "\"" + sourceFile.getAbsoluteFile() + "\"", width, "\"" + targetFile.getAbsoluteFile() + "\"");
            ProcessUtils.executeCommand(cmd, false);
            if (delSource) {
                FileUtils.forceDelete(sourceFile);
            }
            //更新数据库
            LambdaUpdateWrapper<FileInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(FileInfo::getFileId, fileId).set(FileInfo::getFileCover, fileId + ".png");
            fileInfoService.update(lambdaUpdateWrapper);
        } catch (Exception e) {
            log.error("压缩图片失败");
        }
    }

    //利用java代码操作命令行窗口执行FFmpeg对视频进行切割，生成.m3u8索引文件和.ts切片文件
    //设为消息队列，提高用户体验
    @Async
    public void cutFile4Video(String fileId, String videoFilePath, String path, String userid) throws Exception {
        //临时路径
        String temppath = temppath1;

        //创建同名切片目录
        File tsFolder = new File(path + File.separator + fileId);
        if (!tsFolder.exists()) {
            tsFolder.mkdirs();
        }


        //视频压缩，并转换格式
        String CMD_TRANSFER_TO_H264 = "ffmpeg -i %s -c:v libx264 -preset veryfast -crf 28 -c:a aac -b:a 128k %s";
        if (mp4YaSuo.equals("true")) {
            log.info("已开启视频压缩!");
            CMD_TRANSFER_TO_H264 = "ffmpeg -i %s -c:v libx264 -preset veryfast -crf 28 -c:a aac -b:a 128k %s";
        } else {
            CMD_TRANSFER_TO_H264 = "ffmpeg -i %s -c:v libx264 -preset veryfast -crf 0 -c:a aac -b:a 128k %s";
        }

        //转换为ts
        final String CMD_TRANSFER_2TS = "ffmpeg -y -i %s  -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s";
        //生成m3u8文件，以及ts切片
        final String CMD_CUT_TS = "ffmpeg -i %s -c copy -map 0 -f segment -segment_list %s -segment_time 5 %s/%s_%%4d.ts";
        //切片路径
        String tsPath = tsFolder + File.separator + fileId + ".ts";

        //格式转换，防止出现出错
        String cmd = String.format(CMD_TRANSFER_TO_H264, "\"" + videoFilePath + "\"", temppath + fileId + ".mp4");
        ProcessUtils.executeCommand(cmd, true);


        //生成index.ts
        cmd = String.format(CMD_TRANSFER_2TS, "\"" + temppath + fileId + ".mp4" + "\"", tsPath);
        log.info(cmd);
        ProcessUtils.executeCommand(cmd, true);
        // 删除原始文件
        File originalFile = new File(temppath + fileId + ".mp4");
        originalFile.delete();

//        // 重新命名文件
//        File newFile = new File(temppath);
//        newFile.renameTo(new File(videoFilePath));

        //生成索引文件.m3u8 和切片.ts
        cmd = String.format(CMD_CUT_TS, "\"" + tsPath + "\"", "\"" + tsFolder.getPath() + "/" + fileId + ".m3u8" + "\"", tsFolder.getPath(), fileId);
        log.info(cmd);
        ProcessUtils.executeCommand(cmd, true);

        //删除index.ts
        new File(tsPath).delete();

        //更新数据库
        LambdaUpdateWrapper<FileInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(FileInfo::getFileId, fileId).set(FileInfo::getStatus, 2).set(FileInfo::getFileCover, fileId + ".png");
        String cachekey1 = String.format("fileInfo:user:%s:loadDataList", userid);
        //输出发生变化,删除键值
        redisTemplate.delete(cachekey1);
        //数据发生更新
        String cachekey2 = String.format("admin:user:%s:loadUserList", root);
        redisTemplate.delete(cachekey2);
        fileInfoService.update(lambdaUpdateWrapper);
    }
}
