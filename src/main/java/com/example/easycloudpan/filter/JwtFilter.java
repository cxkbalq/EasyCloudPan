package com.example.easycloudpan.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.easycloudpan.common.R;
import com.example.easycloudpan.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@WebFilter(urlPatterns = "/*")

public class JwtFilter implements Filter {

    //定义不需要拦截的路径
    private static final String[] Path = {"download","/login", "/sendEmailCode", "/getShareInfo","/getImage","/ts","/uploadFile","/getAvatar","/getShareLoginInfo"
            , "/share", "/checkShareCode", "/loadFileList", "/getFolderInfo", "/getFile","/checkCode","/register"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取请求路径，以及当前请求方法
        String url = request.getRequestURI();
        String method = request.getMethod();
        log.info("当前拦截url为：" + url + method);

        if ("OPTIONS".equals(method)) {
            log.info("当前为CORS验证请求，直接放行");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
//        设置跨域资源共享
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");

        //放行登录页面
        if (Arrays.stream(Path).anyMatch(url::contains)) {
            log.info("放行登录页面...");
            filterChain.doFilter(servletRequest, servletResponse);
//            writeErrorResponse(response, "NOT_LOGIN");
            return;
        }

        //验证登录信息是否完整
        // 获取 HttpSession
        HttpSession session = request.getSession();
        Object userId = session.getAttribute("userid");
        if (userId == null) {
            log.info("用户信息不完整，返回登录页面！");
            writeErrorResponse(response, "NOT_LOGIN", 901);
            return;
        }


        String jwt = request.getHeader("jwtToken");

        if (!StringUtils.hasLength(jwt)) {
            log.info("令牌不存在当前{}，直接返回错误请求", jwt);
            writeErrorResponse(response, "NOT_LOGIN", 901);
            return;
        }

       // jwt = jwt.substring(1, jwt.length() - 1);

        try {
            JwtUtils jwtUtils = new JwtUtils();
            jwtUtils.parseJWT(jwt);
            log.info("令牌解析成功...放行");
        } catch (Exception e) {
            log.info(jwt);
            log.info("JWT解析失败");
            writeErrorResponse(response, "NOT_LOGIN", 901);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


    private void writeErrorResponse(HttpServletResponse response, String message, Integer code) throws IOException {
        R result = R.success("jwt_exp");
        result.setInfo(message);
        result.setCode(code);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(JSONObject.toJSONString(result).getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

}