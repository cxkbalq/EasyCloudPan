package com.example.easycloudpan.controller;

import com.aliyuncs.utils.MapUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.easycloudpan.common.R;
import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.service.FileInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping()
@Slf4j
public class CommonController {
    @Value("${easycloudpan.imgepath}")
    private String imgepath;
    @Value("${easycloudpan.filepath}")
    private String fileimagepath;
    @Autowired
    private FileInfoService fileInfoService;
    @Value("${easycloudpan.rootuser}")
    private String root;

    /**
     * 上传图片
     * MapUtils 本质还是 Apache Commons Collections 提供的工具类，
     * file变量名必须要与浏览器提交的文件名相同
     * 且大多使用post方法
     **/

    @PostMapping("/updateUserAvatar")
    public R<String> uplod(@RequestPart("avatar") MultipartFile file, HttpSession session) throws IOException {

        //file 是tomcat临时产生的文件
        log.info("当前获得文件信息为：{}", file);
        //获取文件的后缀格式
        String imgename = file.getOriginalFilename();
        String userid = session.getAttribute("userid").toString();
        String imge = userid + ".png";
        //判断文件目录是否存在
        File dir = new File(imgepath);
        if (dir.exists()) {
            log.info("文件夹无需再次创建");
        } else {
            log.info("文件夹不存在已创建");
            dir.mkdirs();
        }
        log.info("当前新的文件为：{}", imge);

        try {
            file.transferTo(new File(imgepath + imge));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(imge);
    }


    /**
     * 下载图片
     **/
    @GetMapping("/getAvatar/{userId}")
    public void download(@PathVariable String userId, HttpServletResponse response) throws IOException {
        try {
            //创建输入流，读取传入的图片
            FileInputStream fileInputStream = new FileInputStream(imgepath + userId + ".png");
            //创建输出流，向浏览器发生读取的数据
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/png");
            int len = 1;
            //定义一次传入可以的最大字节
            byte[] bytes = new byte[2048];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                //刷新输出流，确保所有数据都被写入到输出目标中。
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //管理员下载缩略图
    //获得文件图片
    @GetMapping("/file/getImage/{imageName}/{userid}")
    public void getImageRoot(
            @PathVariable("imageName") String imageName,
            HttpServletResponse response,
            @PathVariable("userid") String userid1, HttpSession session) throws IOException {
        String userid = session.getAttribute("userid").toString();
        String rootzh = session.getAttribute("root").toString();
               //验证是否为管理用户
        if (!rootzh.equals(root)) {
            log.info("非法操作");
            return;
        }
        try {
            log.info(imageName);
            //创建输入流，读取传入的图片
            String[] split = imageName.split("\\.");
            String path = fileimagepath + userid1 + "\\" + "croveImage" + "\\" + imageName;
            FileInputStream fileInputStream = new FileInputStream(path);
            //创建输出流，向浏览器发生读取的数据
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/png");
            int len = 1;
            //定义一次传入可以的最大字节
            byte[] bytes = new byte[2048];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                //刷新输出流，确保所有数据都被写入到输出目标中。
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    //下载缩略图
    //获得文件图片
    @GetMapping("/file/getImage/{imageName}")
    public void getImage(
            @PathVariable("imageName") String imageName,
            HttpServletResponse response, HttpSession session) throws IOException {
        String userid = session.getAttribute("userid").toString();
        try {
            log.info(imageName);
            //创建输入流，读取传入的图片
            String[] split = imageName.split("\\.");
            String path = fileimagepath + userid + "\\" + "croveImage" + "\\" + imageName;
            FileInputStream fileInputStream = new FileInputStream(path);
            //创建输出流，向浏览器发生读取的数据
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/png");
            int len = 1;
            //定义一次传入可以的最大字节
            byte[] bytes = new byte[2048];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                //刷新输出流，确保所有数据都被写入到输出目标中。
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }



    /***
     * 普通用户下载文件图片
     * @param fileId
     * @param response
     * @param session
     * @throws IOException
     */
    @PostMapping("/file/getFile/{imageName}")
    public void getFileiamge(
            @PathVariable("imageName") String fileId, HttpServletResponse response, HttpSession session) throws IOException {
        String userid = session.getAttribute("userid").toString();
        try {
            LambdaQueryWrapper<FileInfo> lambdaQueryWrappe = new LambdaQueryWrapper<>();
            lambdaQueryWrappe.eq(FileInfo::getFileId, fileId);
            FileInfo one = fileInfoService.getOne(lambdaQueryWrappe);
            log.info(one.getFileName());
            //创建输入流，读取传入的图片
            String path = fileimagepath + userid + "\\" + "\\" +  one.getFilePath();
            FileInputStream fileInputStream = new FileInputStream(path);
            //创建输出流，向浏览器发生读取的数据
            ServletOutputStream outputStream = response.getOutputStream();
//            response.setContentType("image/png");
            int len = 1;
            //定义一次传入可以的最大字节
            byte[] bytes = new byte[2048];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                //刷新输出流，确保所有数据都被写入到输出目标中。
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /***
     * 普通用户下载文件
     * @param filemd5
     * @param response
     * @param session
     * @throws IOException
     */
    @GetMapping("/file/download/{code}")
    public void getFile(@PathVariable("code") String filemd5, HttpServletResponse response, HttpSession session) throws IOException {
        String userid = session.getAttribute("userid").toString();
        try {
            LambdaQueryWrapper<FileInfo> lambdaQueryWrappe = new LambdaQueryWrapper<>();
            lambdaQueryWrappe.eq(FileInfo::getFileMd5, filemd5);
            FileInfo one = fileInfoService.getOne(lambdaQueryWrappe);
            log.info(one.getFileName());
            //创建输入流，读取传入的图片
            String path = fileimagepath + userid + "\\" + "\\" + one.getFilePath();
            response.setContentType("application/octet-stream");
            // 对文件名进行 URL 编码,解决前端无法识别空格导致下载格式异常的问题
            String encodedFileName = URLEncoder.encode(one.getFileName(), StandardCharsets.UTF_8.toString())
                    .replace("+", "%20"); // 替换加号为空格
            // 使用适当的编码格式来处理文件名
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
            FileInputStream fileInputStream = new FileInputStream(path);
            //创建输出流，向浏览器发生读取的数据
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 1;
            //定义一次传入可以的最大字节
            byte[] bytes = new byte[2048];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                //刷新输出流，确保所有数据都被写入到输出目标中。
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


        @GetMapping("/admin/download/{code}/{userid}")
    public void getFileRoot(@PathVariable("code") String filemd5,
                            @PathVariable("userid") String userid,
                            HttpServletResponse response, HttpSession session) throws IOException {
        String rootzh = session.getAttribute("root").toString();
        //验证是否为管理用户
        if (!rootzh.equals(root)) {
            log.info("非法下载操作！");
            return ;
        }
        try {
            LambdaQueryWrapper<FileInfo> lambdaQueryWrappe = new LambdaQueryWrapper<>();
            lambdaQueryWrappe.eq(FileInfo::getFileMd5, filemd5);
            FileInfo one = fileInfoService.getOne(lambdaQueryWrappe);
            log.info(one.getFileName());
            //创建输入流，读取传入的图片
            String path = fileimagepath + userid + "\\" + "\\" + one.getFilePath();
            response.setContentType("application/octet-stream");
            // 对文件名进行 URL 编码,解决前端无法识别空格导致下载格式异常的问题
            String encodedFileName = URLEncoder.encode(one.getFileName(), StandardCharsets.UTF_8.toString())
                    .replace("+", "%20"); // 替换加号为空格
            // 使用适当的编码格式来处理文件名
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
            FileInputStream fileInputStream = new FileInputStream(path);
            //创建输出流，向浏览器发生读取的数据
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 1;
            //定义一次传入可以的最大字节
            byte[] bytes = new byte[2048];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                //刷新输出流，确保所有数据都被写入到输出目标中。
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    /***
     * 普通用户下载文件图片
     * @param fileId
     * @param response
     * @param session
     * @throws IOException
     */
    @PostMapping("/admin/getFile/{userid}/{imageName}")
    public void getFileiamgeRoot(@PathVariable("userid") String userid,
                                   @PathVariable("imageName") String fileId,
                                 HttpServletResponse response,
                                 HttpSession session) throws IOException {
        String rootzh = session.getAttribute("root").toString();
        //验证是否为管理用户
        if (!rootzh.equals(root)) {
            log.info("非法下载操作！");
            return ;
        }
        try {
            LambdaQueryWrapper<FileInfo> lambdaQueryWrappe = new LambdaQueryWrapper<>();
            lambdaQueryWrappe.eq(FileInfo::getFileId, fileId);
            FileInfo one = fileInfoService.getOne(lambdaQueryWrappe);
            log.info(one.getFileName());
            //创建输入流，读取传入的图片
            String path = fileimagepath + userid + "\\" + "\\" + one.getFilePath();
            FileInputStream fileInputStream = new FileInputStream(path);
            //创建输出流，向浏览器发生读取的数据
            ServletOutputStream outputStream = response.getOutputStream();
//            response.setContentType("image/png");
            int len = 1;
            //定义一次传入可以的最大字节
            byte[] bytes = new byte[2048];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                //刷新输出流，确保所有数据都被写入到输出目标中。
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}