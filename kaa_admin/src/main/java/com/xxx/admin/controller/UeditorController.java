package com.xxx.admin.controller;


import com.baidu.ueditor.ActionEnter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxx.user.service.UploadFileService;
import com.xxx.utils.OSSClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度富文本编辑器配置接口
 */
@Controller
public class UeditorController {

    @Autowired
    private UploadFileService uploadFileService;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping("/ueditorConfig")
    @ResponseBody
    public void config(String action, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        String rootPath = request.getSession().getServletContext().getRealPath("/assets/ueditor/jsp/");
        try {
            String exec = new ActionEnter(request, rootPath).exec();
            PrintWriter writer = response.getWriter();
            writer.write(exec);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "ueditor/uploadImage", method = RequestMethod.POST)
    @ResponseBody
    public String uploadImage(@RequestParam("upfile") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        String output = "";
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String fileName = file.getOriginalFilename();
            String type = fileName.substring(fileName.lastIndexOf(".")+1);
            String imgId = uploadFileService.saveOssUploadFileByByte(file.getBytes(), type).toString();
            result.put("url", OSSClientUtil.getObjectUrl(imgId));
            result.put("original", imgId);
            result.put("size", file.getSize());
            result.put("type", type);
            result.put("state", "SUCCESS");
            output = mapper.writeValueAsString(result);
        } catch (Exception e) {
        }
        return output;
    }

    @RequestMapping(value = "ueditor/uploadVideo", method = RequestMethod.POST)
    @ResponseBody
    public String uploadVideo(@RequestParam("upfile") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        String output = "";
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String fileName = file.getOriginalFilename();
            String type = fileName.substring(fileName.lastIndexOf(".")+1);
            String id = uploadFileService.saveOssUploadFileByByte_video(file.getBytes(), type).toString();
            result.put("url", OSSClientUtil.getObjectUrl(id));
            result.put("original", id);
            result.put("size", file.getSize());
            result.put("type", type);
            result.put("state", "SUCCESS");
            output = mapper.writeValueAsString(result);
        } catch (Exception e) {
        }
        return output;
    }

}
