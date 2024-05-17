package com.example.easycloudpan.controller;

import com.aliyuncs.utils.MapUtils;
import com.example.easycloudpan.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping()
@Slf4j
public class CommonController {
    @Value("${easycloudpan.imgepath}")
    private String imgepath;

    /**
     * 上传图片
     * MapUtils 本质还是 Apache Commons Collections 提供的工具类，
     * file变量名必须要与浏览器提交的文件名相同
     * 且大多使用post方法
     **/

    @PostMapping("/updateUserAvatar")
    public R<String> uplod(@RequestPart("avatar") MultipartFile file, HttpSession  session) throws IOException {

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

}