package com.example.easycloudpan;

import com.example.easycloudpan.utils.MailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;

@SpringBootTest
class EasyCloudPanApplicationTests {

	@Test
	void contextLoads() {
	}
	@Autowired
    private MailUtil sendEmailService;
    @Autowired
	private TemplateEngine templateEngine;

	@Test
	public void sendTemplateEmailTest() throws MessagingException {
		//写入自己的QQ邮箱号码
		String to="1951196623@qq.com";
		String subject="EasyCloudPan";
		//使用模板邮件定制邮件正文内容
		Context context=new Context();
		context.setVariable("subject", "EasyCloudPan");
		context.setVariable("code", "456321");
        context.setVariable("charset", "UTF-8");
		//使用TemplateEngine设置要处理的模板页面
		//process第一个参数是解析的模板页面，第二个参数是页面的动态数据
		String emailContent=templateEngine.process("mail", context);
		sendEmailService.sendTemplateEmail(to, subject, emailContent);
}
}
