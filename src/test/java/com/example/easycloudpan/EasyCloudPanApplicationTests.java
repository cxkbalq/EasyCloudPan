//package com.example.easycloudpan;
//
//import com.example.easycloudpan.pojo.dto.FileUploadDTO;
//import com.example.easycloudpan.utils.MailUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.convert.DataSizeUnit;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.Context;
//
//import javax.mail.MessagingException;
//import javax.servlet.http.HttpSession;
//import java.io.File;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.lang.reflect.Method;
//import java.nio.MappedByteBuffer;
//import java.nio.channels.FileChannel;
//
//@SpringBootTest
//@Slf4j
//class EasyCloudPanApplicationTests {
//
//    @Test
//    void contextLoads() {
//    }
//
//    @Autowired
//    private MailUtil sendEmailService;
//    @Autowired
//    private TemplateEngine templateEngine;
//    @Value("${easycloudpan.sys_diskspace}")
//    private String diskFreeSpace;
//    @Value("${easycloudpan.temppath}")
//    private String temppath;
//    @Value("${easycloudpan.filepath}")
//    private String filepath;
//
//
//    @Test
//     //合并文件
//    public void heBing() throws IOException {
//
//        FileUploadDTO fileUploadDTO=new FileUploadDTO();
//        fileUploadDTO.setFilename("test.mp4");
//        fileUploadDTO.setChunks(27);
//        fileUploadDTO.setFileMd5("9c813fe7a11606dc049ced03042ce7e9");
//        String filename="test.mp4";
//        log.info("开始文件合并");
//        String outputFile = filepath +  File.separator + filename;
//        log.info(outputFile);
//        try (RandomAccessFile outputRaf = new RandomAccessFile(outputFile, "rw");
//             FileChannel outputChannel = outputRaf.getChannel()) {
//            long position = 0;
//            for (int i = 0; i < fileUploadDTO.getChunks(); i++) {
//                String inputFile = temppath + fileUploadDTO.getFileMd5() + "." + String.valueOf(i);
//                try (RandomAccessFile inputRaf = new RandomAccessFile(inputFile, "r");
//                     FileChannel inputChannel = inputRaf.getChannel()) {
//                    long size = inputChannel.size();
//                    MappedByteBuffer inputBuffer = inputChannel.map(FileChannel.MapMode.READ_ONLY, 0, size);
//                    MappedByteBuffer outputBuffer = outputChannel.map(FileChannel.MapMode.READ_WRITE, position, size);
//                    outputBuffer.put(inputBuffer);
//                    position += size;
//                    // 手动清理 MappedByteBuffer这个会占用文件的句柄需要释放了
//                    //clean(inputBuffer);
//                  //  clean(outputBuffer);
//                }
//            }
//            // 关闭输出文件的 RandomAccessFile 和 FileChannel
//         //   outputRaf.close();
//          //  outputChannel.close();
//
//            log.info("文件合并成功！");
////            for (int i = 0; i < fileUploadDTO.getChunks(); i++) {
////                String inputFile = temppath + fileUploadDTO.getFileMd5() + "." + String.valueOf(i);
////                File file = new File(inputFile);
////                boolean delete = file.delete();
////                if (delete) {
////                    log.info("删除成功！");
////                } else {
////                    log.error("删除失败！" + inputFile);
////                }
////            }
//            //log.info("临时文件清理成功！");
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("文件合并错误！");
//            return ;
//        }
//        File file = new File(outputFile);
//        long length = file.length();
//        log.info("当前文件大小："+String.valueOf(length));
//        return;
//    }
//    private void clean(MappedByteBuffer buffer) {
//        if (buffer == null) return;
//        try {
//            Method cleanerMethod = buffer.getClass().getMethod("cleaner");
//            cleanerMethod.setAccessible(true);
//            Object cleaner = cleanerMethod.invoke(buffer);
//            if (cleaner != null) {
//                Method cleanMethod = cleaner.getClass().getMethod("clean");
//                cleanMethod.invoke(cleaner);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}
