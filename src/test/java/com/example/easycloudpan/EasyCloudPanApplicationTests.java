package com.example.easycloudpan;

import com.example.easycloudpan.utils.MailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;

@SpringBootTest
class EasyCloudPanApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private MailUtil sendEmailService;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${easycloudpan.sys_diskspace}")
    private String diskFreeSpace;

    @Test
    public void sendTemplateEmailTest() throws MessagingException {
        //写入自己的QQ邮箱号码
        String to = "1951196623@qq.com";
        String subject = "EasyCloudPan";
        //使用模板邮件定制邮件正文内容
        Context context = new Context();
        context.setVariable("subject", "EasyCloudPan");
        context.setVariable("code", "456321");
        context.setVariable("charset", "UTF-8");
        //使用TemplateEngine设置要处理的模板页面
        //process第一个参数是解析的模板页面，第二个参数是页面的动态数据
        String emailContent = templateEngine.process("mail", context);
        sendEmailService.sendTemplateEmail(to, subject, emailContent);
    }

    @Test
    public void s() {
        File file = new File("E:\\code\\java\\EasyCloudPan\\src\\main\\resources\\res\\temp\\3db90fbd9cb72c329659f53e5c4c5203.0");
        try {
            // 创建并删除文件的代码
            if (file.delete()) {
                System.out.println("文件已成功删除");
            } else {
                System.out.println("无法删除文件");
            }
        } catch (SecurityException e) {
            System.out.println("没有足够的权限来删除文件");
            e.printStackTrace();
        }
    }
    @Test
    public void getfreespace(){
        File diskPartition = new File(diskFreeSpace+":"); // 根目录，或指定其他目录
        long freeSpace = diskPartition.getUsableSpace(); // 获取剩余空间
        System.out.println("Free space: " + freeSpace + " bytes");
    }


        @Test
    public void getfr1eespace(){
        String tse="1784458528288247809.fengxiang";
            String[] split = tse.split("\\.");
            for (String s:split){
                System.out.println(s);
            }
        }
}
