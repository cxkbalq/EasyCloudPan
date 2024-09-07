package com.example.easycloudpan.controller;


import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.easycloudpan.common.R;
import com.example.easycloudpan.pojo.EmailCode;
import com.example.easycloudpan.pojo.UserInfo;
import com.example.easycloudpan.pojo.vo.SessionWebUserVO;
import com.example.easycloudpan.service.EmailCodeService;
import com.example.easycloudpan.service.UserInfoServise;
import com.example.easycloudpan.utils.CreateImageCodeUtils;
import com.example.easycloudpan.utils.JwtUtils;
import com.example.easycloudpan.utils.MailUtil;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.*;
import javax.servlet.http.HttpSession;
import javax.swing.text.StyledEditorKit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//用户信息 前端控制器

@RestController
@RequestMapping
//确保其符合指定的验证规则
@Validated
@Slf4j
public class UserInfoController {
    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private CreateImageCodeUtils createImageCodeUtils;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private EmailCodeService emailCodeService;
    @Autowired
    private UserInfoServise userInfoServise;
    @Value("${easycloudpan.filepath}")
    private String filepath;
    @Value("${easycloudpan.rootuser}")
    private String root;
    @Value("${easycloudpan.sys_diskspace}")
    private String diskFreeSpace;
    @Value("${easycloudpan.sys_space}")
    private String sysSpace;
    // 定义常量，1 MB = 1024 * 1024 字节
    private static final long MEGABYTE_TO_BYTES = 1024L * 1024L;

    //生成图像验证码
    @GetMapping("/checkCode")
    public void checkCode(HttpServletRequest request, HttpSession session, Integer type, HttpServletResponse response) {
        //设置返回类型为imge
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expire", "0");
        response.setHeader("Pragma", "no-cache");
        // getRandomCodeImage方法会直接将生成的验证码图片写入response
        createImageCodeUtils.getRandomCodeImage(request, response);
    }

    //发送邮箱验证码
    @PostMapping("/sendEmailCode")
    public R<String> sendEmailCode(HttpSession session,
                                   @RequestParam String email,
                                   @RequestParam String checkCode,
                                   @RequestParam String type) throws MessagingException {
        //获取session里的验证码
        String sessionCode = String.valueOf(session.getAttribute("CODE")).toLowerCase();
        log.info("session里的验证码：" + sessionCode);
        log.info("获得code" + checkCode);
        if (sessionCode.equals(checkCode)) {
            String scode = createImageCodeUtils.getgenerateRandomString();
            Boolean b = mailUtil.sendEmail(email, scode);
            if (b) {
                //将验证码储存到redis
                //找回密码
                if (type.equals(1)) {
                    redisTemplate.opsForValue().set(email + "zh", scode, 30, TimeUnit.MINUTES);
                } else {
                    redisTemplate.opsForValue().set(email, scode, 30, TimeUnit.MINUTES);
                }
                return R.success("验证码发送成功，请去邮箱查看");
            } else {
                return R.error("验证码发送失败，请重新输入！");
            }

        } else {
            return R.error("验证码错误，请重新输入！");
        }
    }

    //获取用户空间
    @PostMapping("/getUseSpace")
    public R<UserInfo> getUseSpace(HttpSession session) {
        // session.setAttribute("userid", "1784458528288247809");
        String userid = session.getAttribute("userid").toString();
        UserInfo one = userInfoServise.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, userid));
        return R.success(one);
    }

    //注册用户
    @PostMapping("/register")
    public R<String> register(HttpSession session, @RequestParam String email,
                              @RequestParam String emailCode,
                              @RequestParam String nickName,
                              @RequestParam String checkCode,
                              @RequestParam String password) {
        //获取session里的验证码
        String sessionCode = String.valueOf(session.getAttribute("CODE")).toLowerCase();
        if (sessionCode.equals(checkCode)) {
            //判断redis里是否存在这个键值
            if (redisTemplate.opsForValue().get(email) != null & redisTemplate.opsForValue().get(email).equals(emailCode)) {
                UserInfo userInfo = new UserInfo();
                userInfo.setCreateTime(LocalDateTime.now());
                userInfo.setUpdateTime(LocalDateTime.now());
                userInfo.setEmail(email);
                //检测这账号是否存在
                LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(UserInfo::getEmail, email);
                UserInfo one = userInfoServise.getOne(queryWrapper);
                if (one != null) {
                    return R.error("账号已存在");
                }
                //默认空间为 5GB ,修改请登陆管理员账号自行修改！
                long bytes = 5 * MEGABYTE_TO_BYTES;
                //获取文件盘符所剩余空间
                File diskPartition = new File(diskFreeSpace + ":"); // 根目录，或指定其他目录
                long freeSpace = diskPartition.getUsableSpace(); // 获取剩余空间

                if (bytes > freeSpace - Long.valueOf(sysSpace) * MEGABYTE_TO_BYTES) {
                    return R.error("磁盘可用空间不足，请联系管理员扩充磁盘！");
                }

                //转换为md5格式
                String s = DigestUtils.md5DigestAsHex(password.getBytes());
                userInfo.setPassword(s);
                userInfo.setNickName(nickName);
                userInfo.setTotalSpace(524288000l);
                userInfo.setUseSpace(0l);
                userInfo.setStatus(1);
                userInfo.setJoinTime(LocalDateTime.now());
                String string = UUID.randomUUID().toString();
                redisTemplate.delete(email);
                userInfoServise.save(userInfo);
                return R.success("账号注册成功");
                //创建用户根目录
//                File file = new File(filepath + userInfo.getUserId());
//                boolean mkdir = file.mkdir();
//                if (mkdir) {
//                    userInfoServise.save(userInfo);
//                    return R.success("账号注册成功");
//                } else {
//                    return R.error("用户空间初始化错误！，请联系管理员");
//                }

            } else {
                return R.error("邮箱验证码错误或已过期！");
            }
        } else {
            return R.error("人机验证码错误！");
        }

    }

    //用户登录
    @PostMapping("/login")
    public R<SessionWebUserVO> login(HttpSession session, @RequestParam String email,
                                     @RequestParam String password,
                                     @RequestParam String checkCode) {
        //获取session里的验证码
        String sessionCode = String.valueOf(session.getAttribute("CODE")).toLowerCase();
        log.info(email);
        log.info(password);
        if (!sessionCode.equals(checkCode)) {
            LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserInfo::getEmail, email).eq(UserInfo::getPassword, password);
            UserInfo one = userInfoServise.getOne(lambdaQueryWrapper);
            if (one != null) {
                if (one.getStatus() == 0) {
                    return R.error("你的账号已被禁用");
                } else {
                    session.setAttribute("userid", one.getUserId());
                    session.setAttribute("root", one.getEmail());
                    redisTemplate.opsForValue().set(one.getUserId(), one, 300, TimeUnit.MINUTES);
                    //  File file = new File(filepath + one.getUserId());
                    File file1 = new File(filepath + "\\" + "croveImage");
                    if (file1.exists() && file1.isDirectory()) {
                        System.out.println("目录存在");
                    } else {
                        //  file.mkdir();
                        file1.mkdir();
                    }

                    //创建jwt令牌，并返回给前端，为了减少代码量，将jwt储存在msg里了
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("id", one.getUserId());
                    claims.put("email", one.getEmail());
                    JwtUtils jwtUtils = new JwtUtils();
                    String jwt = jwtUtils.generateJwt(claims);
                    log.info("用户端jwt为：{}", jwt);

                    SessionWebUserVO sessionWebUserVO = new SessionWebUserVO();
                    sessionWebUserVO.setUserId(one.getUserId());
                    sessionWebUserVO.setNickname(one.getNickName());
                    sessionWebUserVO.setAvatar(one.getQqAvatar());
                    sessionWebUserVO.setJwt(jwt);
                    if (one.getEmail().equals(root)) {
                        sessionWebUserVO.setIsAdmin(true);
                    } else {
                        sessionWebUserVO.setIsAdmin(false);
                    }
                    LambdaUpdateWrapper<UserInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    lambdaUpdateWrapper.set(UserInfo::getLastLoginTime, LocalDateTime.now());
                    lambdaUpdateWrapper.eq(UserInfo::getUserId, one.getUserId());
                    userInfoServise.update(lambdaUpdateWrapper);
                    return R.success(sessionWebUserVO);
                }
            } else {
                return R.error("账号或密码错误");
            }
        } else {
            return R.error("图形验证码错误");
        }
    }

    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        String userid = (String) session.getAttribute("userid");
        session.removeAttribute("userid");
        // redisTemplate.delete(userid);
        return R.success("请求成功！");
    }

    //找回密码
    @PostMapping("/resetPwd")
    public R<String> resetPwd(HttpSession session, @RequestParam String email,
                              @RequestParam String emailCode,
                              @RequestParam String nickName,
                              @RequestParam String checkCode,
                              @RequestParam String password) {

        //获取session里的验证码
        String sessionCode = String.valueOf(session.getAttribute("CODE")).toLowerCase();
        if (sessionCode.equals(checkCode)) {
            //判断redis里是否存在这个键值
            if (redisTemplate.opsForValue().get(email + "zh") != null & redisTemplate.opsForValue().get(email + "zh").equals(emailCode)) {
                //转换为md5格式
                String s = DigestUtils.md5DigestAsHex(password.getBytes());
                redisTemplate.delete(email);
                LambdaUpdateWrapper<UserInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                //设置基本信息
                lambdaUpdateWrapper.eq(UserInfo::getEmail, email)
                        .set(UserInfo::getPassword, s)
                        .set(UserInfo::getUpdateTime, LocalDateTime.now());
                userInfoServise.update(lambdaUpdateWrapper);
                return R.success("，密码重置成功");
            } else {
                return R.error("邮箱验证码错误或已过期！");
            }
        } else {
            return R.error("人机验证码错误！");
        }
    }
    //头像获取
/*    @GetMapping("/getAvatar/{userId}")
    public void getAvatar(@PathVariable String userId,HttpServletRequest request,HttpServletResponse response) {
        response.setContentType("image/png");
        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_BGR);
    }*/

}

