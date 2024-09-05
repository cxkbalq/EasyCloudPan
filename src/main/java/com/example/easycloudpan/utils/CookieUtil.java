package com.example.easycloudpan.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Component
public class CookieUtil {
    /***
     * 获得cookie里面的值
     * @param request
     * @param tag
     * @param JosnVale
     * @return
     * @throws JsonProcessingException
     * @throws UnsupportedEncodingException
     */

    public String getCookiesValue(HttpServletRequest request, String tag, String JosnVale) throws JsonProcessingException, UnsupportedEncodingException {

        Cookie[] cookies = request.getCookies();
        String test="";
        for (Cookie cookie : cookies) {
            if (tag.equals(cookie.getName())) {
                ObjectMapper objectMapper = new ObjectMapper();
                // 解码 URL 编码的 cookie 值
                String decodedValue = URLDecoder.decode(cookie.getValue(), "UTF-8");
                // 解析 JSON 格式的解码值
                JsonNode jsonNode = objectMapper.readTree(decodedValue);
                String text = jsonNode.get(JosnVale).asText();
                if(text!="" && text!=null){
                    test=text;
                    break;
                }
            }
        }
        return test;
    }
}