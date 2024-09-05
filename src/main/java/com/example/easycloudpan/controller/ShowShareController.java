package com.example.easycloudpan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.easycloudpan.common.R;
import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.pojo.FileShare;
import com.example.easycloudpan.pojo.UserInfo;
import com.example.easycloudpan.pojo.dto.FileShareDto;
import com.example.easycloudpan.pojo.vo.FileShareVo;
import com.example.easycloudpan.pojo.vo.ShowShareVo;
import com.example.easycloudpan.service.FileInfoService;
import com.example.easycloudpan.service.FileShareService;
import com.example.easycloudpan.service.UserInfoServise;
import com.example.easycloudpan.utils.CookieUtil;
import com.example.easycloudpan.utils.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/showShare")
public class ShowShareController {
    @Autowired
    private FileShareService fileShareService;
    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private UserInfoServise userInfoServise;
    @Autowired
    private CookieUtil cookieUtil;
    @Value("${easycloudpan.filepath}")
    private String filepath;
    @Autowired
    private FileUtil fileUtil;

    @PostMapping("/{shareId}")
    public R<FileShare> shareR(HttpSession session, @PathVariable("shareId") String shareId) {
        LambdaQueryWrapper<FileShare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileShare::getShareId, shareId);
        FileShare one = fileShareService.getOne(lambdaQueryWrapper);
        return R.success(one);
    }

    @PostMapping("/getShareInfo")
    public R<ShowShareVo> getShareInfo(HttpSession session, @RequestParam("shareId") String shareId) {

        LambdaQueryWrapper<FileShare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileShare::getShareId, shareId);
        FileShare one = fileShareService.getOne(lambdaQueryWrapper);

        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(UserInfo::getUserId, one.getUserId());
        UserInfo one1 = userInfoServise.getOne(lambdaQueryWrapper1);

        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper2.eq(FileInfo::getFileId, one.getFileId());
        FileInfo one2 = fileInfoService.getOne(lambdaQueryWrapper2);

        ShowShareVo showShareVo = new ShowShareVo();
        BeanUtils.copyProperties(one, showShareVo);
        BeanUtils.copyProperties(one1, showShareVo);
        showShareVo.setShareTime(one.getCreateTime());
        showShareVo.setFileName(one2.getFileName());
        return R.success(showShareVo);
    }

    @SneakyThrows
    @PostMapping("/loadFileList")
    public R<FileShareDto> loadFileList(HttpSession session,
                                        HttpServletRequest request,
                                        @RequestParam("pageNo") String pageNo,
                                        @RequestParam("pageSize") String pageSize,
                                        @RequestParam("shareId") String shareId,
                                        @RequestParam("filePid") String filePid) throws JsonProcessingException {

        //先查出分享的相关信息
        LambdaQueryWrapper<FileShare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileShare::getShareId, shareId);
        FileShare fileShare = fileShareService.getOne(lambdaQueryWrapper);


        Page<FileInfo> page = new Page<>(Long.valueOf(pageNo), Long.valueOf(pageSize));
        //构建分页
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper2.eq(FileInfo::getFileId, fileShare.getFileId()).eq(FileInfo::getFilePid, filePid);
        Page<FileInfo> page1 = fileInfoService.page(page, lambdaQueryWrapper2);
        //构建返回对象
        FileShareDto FileShareDto = new FileShareDto();
        FileShareDto.setPageSize(Long.valueOf(pageSize));
        FileShareDto.setPageNo(Long.valueOf(pageNo));
        FileShareDto.setPageTotal(page1.getTotal());
        FileShareDto.setTotalCount(page1.getCurrent());
        List<FileInfo> records = page1.getRecords();
        List<FileShareVo> list = new ArrayList<>();
        for (FileInfo fileInfo : records) {

            FileShareVo fileShareVo = new FileShareVo();
            BeanUtils.copyProperties(fileInfo, fileShareVo);
            fileShareVo.setExpireTime(fileShare.getExpireTime());
            fileShareVo.setFileType(String.valueOf(fileInfo.getFileType()));
            fileShareVo.setFileCategory(String.valueOf(fileInfo.getFileCategory()));
            fileShareVo.setFolderType(String.valueOf(fileInfo.getFolderType()));
            list.add(fileShareVo);
        }
        FileShareDto.setList(list);
        return R.success(FileShareDto);
    }


    /***
     * 校验分享码
     * @param session
     * @param shareId
     * @param code
     * @return
     */
    @PostMapping("/checkShareCode")
    public R<String> checkShareCode(HttpSession session,
                                    @RequestParam("shareId") String shareId,
                                    @RequestParam("code") String code) {


        //先查出分享的相关信息
        LambdaQueryWrapper<FileShare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileShare::getShareId, shareId);
        FileShare fileShare = fileShareService.getOne(lambdaQueryWrapper);
        if (fileShare.getCode().equals(code)) {
            return R.success("请求成功");
        }
        return R.error("验证码不正确！");
    }

    /***
     * 获得用户的分享信息
     * @param session
     * @param request
     * @param shareId
     * @return
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     */
    @PostMapping("/getShareLoginInfo")
    public R<ShowShareVo> getShareLoginInfo(HttpSession session,
                                            HttpServletRequest request,
                                            @RequestParam("shareId") String shareId) throws UnsupportedEncodingException, JsonProcessingException {

        LambdaQueryWrapper<FileShare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileShare::getShareId, shareId);
        FileShare one = fileShareService.getOne(lambdaQueryWrapper);

        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(UserInfo::getUserId, one.getUserId());
        UserInfo one1 = userInfoServise.getOne(lambdaQueryWrapper1);

        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper2.eq(FileInfo::getFileId, one.getFileId());
        FileInfo one2 = fileInfoService.getOne(lambdaQueryWrapper2);

        ShowShareVo showShareVo = new ShowShareVo();
        BeanUtils.copyProperties(one, showShareVo);
        BeanUtils.copyProperties(one1, showShareVo);
        showShareVo.setShareTime(one.getCreateTime());
        showShareVo.setFileName(one2.getFileName());

        //验证是否为自己分享的文件
        String cookiesValueId = cookieUtil.getCookiesValue(request, "userInfo", "userId");
        if (cookiesValueId.equals(one.getUserId())) {
            showShareVo.setCurrentUser(Boolean.TRUE);
        }
        return R.success(showShareVo);
    }


    @GetMapping("ts/getVideoInfo/{fileShare}/{fileId}")
    public void getVideo(HttpServletResponse response,
                         @PathVariable("fileId") String fileId,
                         @PathVariable("fileShare") String fileShare,
                         HttpSession session) {
        LambdaQueryWrapper<FileShare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileShare::getShareId, fileShare);
        FileShare one = fileShareService.getOne(lambdaQueryWrapper);
        String userid = one.getUserId();

        String path;
        if (fileId.endsWith(".ts")) {
            String[] split = fileId.split("_");
            path = filepath + userid + "\\" + split[0] + "\\" + fileId;
        } else {
            path = filepath + userid + "\\" + fileId + "\\" + fileId + ".m3u8";
        }
        fileUtil.readFile(response, path);
    }
}
