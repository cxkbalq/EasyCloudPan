package com.example.easycloudpan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;


/***
 * 客户端-服务器模型：Web 应用基于客户端和服务器之间的交互。客户端发起请求，服务器处理请求并返回响应。
 *
 * 无状态性（Statelessness）：每个请求都是独立的，服务器不会记住之前请求的状态。这要求客户端在每次请求时提供所有必要的信息。
 *
 * 超媒体作为应用状态引擎（HATEOAS）：在 RESTful API 设计中，超链接被用来指导客户端如何进行下一步操作，使得客户端可以通过服务器提供的链接导航和交互
 */
@SpringBootApplication  //filter使用必须要加上这个给注解，开启web特性
@ServletComponentScan
@EnableAsync
public class EasyCloudPanApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasyCloudPanApplication.class, args);
	}

}
