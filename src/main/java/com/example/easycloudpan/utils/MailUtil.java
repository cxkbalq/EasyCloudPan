package com.example.easycloudpan.utils;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext; // 如果使用的是Thymeleaf的IContext接口
import org.thymeleaf.spring5.SpringTemplateEngine; // 如果使用的是Spring集成的Thymeleaf模板引擎

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Component
@Slf4j

public class MailUtil {

    @Value("${spring.mail.username}")
    private String MAIL_SENDER; //邮件发送者
    @Autowired
    private SpringTemplateEngine templateEngine;

    /**
     * 发送文本邮件
     * <p>
     * //
     */
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Value("${spring.mail.username}")
    private String from;

    //用于处理HTML内容
    public void sendTemplateEmail(String to, String subject, String content) throws MessagingException {
        //复杂类型的邮件传送
        MimeMessage message = mailSender.createMimeMessage();
        try {
            //使用帮助类，并配置multipart多部件使用为true
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            //发送邮件
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean sendEmail(String to, String text) throws MessagingException {
        String subject = "EasyCloudPan";
        //使用模板邮件定制邮件正文内容
        Context context = new Context();
        context.setVariable("subject", "EasyCloudPan");
        context.setVariable("code", text);
        context.setVariable("charset", "UTF-8");
        try {
            //使用TemplateEngine设置要处理的模板页面
            //process第一个参数是解析的模板页面，第二个参数是页面的动态数据
            String emailContent = templateEngine.process("mail", context);
            sendTemplateEmail(to, subject, emailContent);
            log.info("模板邮件发送成功");
            return true;
        } catch (MessagingException e) {
            log.error("邮件发送失败" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}
