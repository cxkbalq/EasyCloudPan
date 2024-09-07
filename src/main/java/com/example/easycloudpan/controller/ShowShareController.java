package com.example.easycloudpan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.easycloudpan.common.R;
import com.example.easycloudpan.pojo.FileInfo;
import com.example.easycloudpan.pojo.FileShare;
import com.example.easycloudpan.pojo.SaveInfo;
import com.example.easycloudpan.pojo.UserInfo;
import com.example.easycloudpan.pojo.dto.FileShareDto;
import com.example.easycloudpan.pojo.vo.FileShareVo;
import com.example.easycloudpan.pojo.vo.ShowShareVo;
import com.example.easycloudpan.service.FileInfoService;
import com.example.easycloudpan.service.FileShareService;
import com.example.easycloudpan.service.SaveInfoService;
import com.example.easycloudpan.service.UserInfoServise;
import com.example.easycloudpan.utils.CookieUtil;
import com.example.easycloudpan.utils.FileUtil;
import com.example.easycloudpan.utils.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
    private SaveInfoService saveInfoService;
    @Autowired
    private CookieUtil cookieUtil;
    @Value("${easycloudpan.filepath}")
    private String filepath;
    @Autowired
    private FileUtil fileUtil;

    /***
     *
     * @param session
     * @param shareId
     * @return
     */

    @PostMapping("/{shareId}")
    public R<FileShare> shareR(HttpSession session, @PathVariable("shareId") String shareId) {
        LambdaQueryWrapper<FileShare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileShare::getShareId, shareId);
        FileShare one = fileShareService.getOne(lambdaQueryWrapper);
        return R.success(one);
    }

    /***
     *
     * @param session
     * @param
     * @param shareId
     * @return
     */
    @PostMapping("/getFolderInfo")
    public R<List<FileInfo>> getFolderInfo(HttpSession session,
                                           @RequestParam("path") String filepath,
                                           @RequestParam("shareId") String shareId) {
        LambdaQueryWrapper<FileShare> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(FileShare::getShareId, shareId);
        FileShare one = fileShareService.getOne(lambdaQueryWrapper1);


        //分割字符串
        String[] path = filepath.split("/");
        //保存结果
        List<FileInfo> results = new ArrayList<>();
        // 2. 遍历每个分割的部分并执行查询
        for (String part : path) {
            LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(FileInfo::getFileId, part);
            lambdaQueryWrapper.eq(FileInfo::getUserId, one.getUserId());
            FileInfo one1 = fileInfoService.getOne(lambdaQueryWrapper);
            results.add(one1);
        }
        return R.success(results);

    }


    /***
     *
     * @param session
     * @param shareId
     * @return
     */

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

    /***
     *
     * @param session
     * @param request
     * @param pageNo
     * @param pageSize
     * @param shareId
     * @param filePid
     * @return
     * @throws JsonProcessingException
     */
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

        //获取session里面的验证码为空跳转到验证页面
        String code = (String) session.getAttribute("code");
        if (code == null || !code.equals(fileShare.getCode())) {
            FileShareDto fileShareDto = new FileShareDto();
            R<FileShareDto> r = new R<>();
            r.setStatus("success");
            r.setCode(200);
            r.setInfo("验证码错误");
            return r;
        }

        Page<FileInfo> page = new Page<>(Long.valueOf(pageNo), Long.valueOf(pageSize));
        //构建分页
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();

        //判断是否为目录
        if (fileShare.getFileId().equals(filePid)) {
            lambdaQueryWrapper2.eq(FileInfo::getFilePid, filePid);
        } else {
            lambdaQueryWrapper2.eq(FileInfo::getFileId, fileShare.getFileId()).eq(FileInfo::getFilePid, filePid);
        }

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

        //更新浏览次数
        LambdaUpdateWrapper<FileShare> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(FileShare::getShareId, shareId);
        lambdaUpdateWrapper.set(FileShare::getBrowseCount, fileShare.getBrowseCount() + 1)
                .set(FileShare::getUpdateTime, LocalDateTime.now());
        fileShareService.update(lambdaUpdateWrapper);

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
            //成功后写入验证码
            session.setAttribute("code", code);
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

    /***
     *分享视频播放
     * @param response
     * @param fileId
     * @param fileShare
     * @param session
     */
    @GetMapping("ts/getVideoInfo/{fileShare}/{fileId}")
    public void getVideo(HttpServletResponse response,
                         @PathVariable("fileId") String fileId,
                         @PathVariable("fileShare") String fileShare,
                         HttpSession session) {

        //解决保存文件以及秒传文件，出现请求错误id的情况，后期加上redis，提高响应速度
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getFileId,fileId.substring(0,10));
        FileInfo one = fileInfoService.getOne(lambdaQueryWrapper);
        //文件实际所处的路径
        String substring = one.getFilePath().substring(0, 10);
        //构建返回路径
        String path;
        if (fileId.endsWith(".ts")) {
            String[] split = fileId.split("_");
            path = filepath + "\\" + substring + "\\" + substring+"_"+split[1];
            log.info(path);
        } else {
            path = filepath + "\\" + substring + "\\" + substring + ".m3u8";
        }
        fileUtil.readFile(response, path);
    }

    /***
     *
     * 保存到我的云盘
     * @param session
     * @param request
     * @param shareId
     * @param shareFileIds
     * @param myFolderId
     * @return
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     */
    @PostMapping("/saveShare")
    @Transactional
    public R<String> saveShare(HttpSession session,
                               HttpServletRequest request,
                               @RequestParam("shareId") String shareId,
                               @RequestParam("shareFileIds") String shareFileIds,
                               @RequestParam("myFolderId") String myFolderId) throws UnsupportedEncodingException, JsonProcessingException {
        //先构建所需要的信息
        String cookiesValueId = cookieUtil.getCookiesValue(request, "userInfo", "userId"); //登录者的id

        LambdaQueryWrapper<FileShare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileShare::getShareId, shareId);
        FileShare fileShare = fileShareService.getOne(lambdaQueryWrapper);


        String[] split = shareFileIds.split(",");
        for (String fileids : split) {
            //获得当前文件的所有信息
            LambdaQueryWrapper<FileInfo> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(FileInfo::getFileId,fileids).eq(FileInfo::getUserId,fileShare.getUserId());
            FileInfo one = fileInfoService.getOne(lambdaQueryWrapper1);

            //生成保存文件新的id信息
            String newId = StringUtil.generateRandomString(10);
            String saveid = StringUtil.generateRandomString(15);

            //保存到save_info
            SaveInfo saveInfo = new SaveInfo();
            saveInfo.setSaveId(saveid);
            saveInfo.setXianUserId(cookiesValueId);
            saveInfo.setXianFileId(newId);
            saveInfo.setYuanFileId(fileShare.getFileId());
            saveInfo.setYuanUserId(fileShare.getUserId());
            saveInfoService.save(saveInfo);

            //保存文件类
            FileInfo fileInfo1 = new FileInfo();
            BeanUtils.copyProperties(one, fileInfo1);
            fileInfo1.setFileId(newId);
            fileInfo1.setFilePid(myFolderId);
            fileInfo1.setSaveSf(true);
            fileInfo1.setUserId(cookiesValueId);
            fileInfo1.setCreateTime(LocalDateTime.now());
            fileInfoService.save(fileInfo1);
            //递归
            duiGui(fileids, cookiesValueId, fileShare,newId);
        }

        return R.success("保存成功");
    }

    /***
     * 创建下载连接
     * @param session
     * @param fileid
     * @return
     */
        @PostMapping("/createDownloadUrl/{www}/{id}")
    public R<String> createDownloadUrl(HttpSession session, @PathVariable("id") String fileid) {
            //解决保存文件以及秒传文件，出现请求错误id的情况
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getFileId, fileid);
        FileInfo one = fileInfoService.getOne(lambdaQueryWrapper);
        String substring = one.getFilePath().substring(0, 10);
        return R.success(one.getFileMd5()+"_"+substring);
    }

       @GetMapping("/download/{code}")
    public void getFile(@PathVariable("code") String filemd5, HttpServletResponse response, HttpSession session) throws IOException {

        try {
            //对路劲进行分割
            String[] split = filemd5.split("_");

            LambdaQueryWrapper<FileInfo> lambdaQueryWrappe = new LambdaQueryWrapper<>();
            lambdaQueryWrappe.eq(FileInfo::getFileMd5, split[0]).eq(FileInfo::getFileId,split[1]);
            FileInfo one = fileInfoService.getOne(lambdaQueryWrappe);
            //创建输入流，读取传入的图片
            String path = filepath + "\\" + "\\" + one.getFilePath();
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
     * 加载文件和图片
     * @param fileId
     * @param response
     * @param session
     * @throws IOException
     */
        @PostMapping("/getFile/{www}/{imageName}")
    public void getFileiamge(@PathVariable("imageName") String fileId, HttpServletResponse response, HttpSession session) throws IOException {
        try {
            LambdaQueryWrapper<FileInfo> lambdaQueryWrappe = new LambdaQueryWrapper<>();
            lambdaQueryWrappe.eq(FileInfo::getFileId, fileId);
            FileInfo one = fileInfoService.getOne(lambdaQueryWrappe);
            log.info(one.getFileName());
            //创建输入流，读取传入的图片
            String path = filepath + "\\" + "\\" + one.getFilePath();
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

    @Transactional
    //递归查询当前目录下还存在子目录
    public void duiGui(String fileId, String cookiesValueId, FileShare fileShare,String pidid) {
        //先对这个文件或目录进行保存
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getFilePid, fileId);
        List<FileInfo> list = fileInfoService.list(lambdaQueryWrapper);
        for (FileInfo fileInfo : list) {
            //如果是目录，继续调用这个函数，并保存这个目录
            if (fileInfo.getFolderType() == 1) {
                log.info("递归");
                //生成保存文件新的id信息
                String newId = StringUtil.generateRandomString(10);
                String saveid = StringUtil.generateRandomString(15);

                //保存到save_info
                SaveInfo saveInfo = new SaveInfo();
                saveInfo.setSaveId(saveid);
                saveInfo.setXianUserId(cookiesValueId);
                saveInfo.setXianFileId(newId);
                saveInfo.setYuanFileId(fileInfo.getFileId());
                saveInfo.setYuanUserId(fileInfo.getUserId());
                saveInfoService.save(saveInfo);

                //保存文件类
                FileInfo fileInfo1 = new FileInfo();
                BeanUtils.copyProperties(fileInfo, fileInfo1);
                fileInfo1.setFileId(newId);
                fileInfo1.setFilePid(pidid);
                fileInfo1.setSaveSf(true);
                fileInfo1.setUserId(cookiesValueId);
                fileInfo1.setCreateTime(LocalDateTime.now());
                fileInfoService.save(fileInfo1);

                //递归
                duiGui(fileInfo.getFileId(), cookiesValueId, fileShare,newId);
            }
            //如果是文件，进行保存
            else {
                log.info("保存");
                //生成保存文件新的id信息
                String newId = StringUtil.generateRandomString(10);
                String saveid = StringUtil.generateRandomString(15);

                //保存到save_info
                SaveInfo saveInfo = new SaveInfo();
                saveInfo.setSaveId(saveid);
                saveInfo.setXianUserId(cookiesValueId);
                saveInfo.setXianFileId(newId);
                saveInfo.setYuanFileId(fileInfo.getFileId());
                saveInfo.setYuanUserId(fileInfo.getUserId());
                saveInfoService.save(saveInfo);

                //保存文件类
                FileInfo fileInfo1 = new FileInfo();
                BeanUtils.copyProperties(fileInfo, fileInfo1);
                fileInfo1.setFileId(newId);
                fileInfo1.setFilePid(pidid);
                fileInfo1.setSaveSf(true);
                fileInfo1.setUserId(cookiesValueId);
                fileInfo1.setCreateTime(LocalDateTime.now());
                fileInfoService.save(fileInfo1);
            }
        }
    }

}
