package com.xxx.admin.controller;

import com.xxx.admin.service.BannerService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.model.business.PlaProductDescription;
import com.xxx.model.business.SalesProductDescription;
import com.xxx.model.business.StoProductDescription;
import com.xxx.model.business.SupWorker;
import com.xxx.user.service.UploadFileService;
import com.xxx.utils.OSSClientUtil;
import org.hibernate.FetchMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
@RequestMapping("test")
public class TestController {

//    @Autowired
//    private UploadFileService uploadFileService;
//
//    @RequestMapping(value = "/aaHtml", method = {RequestMethod.GET})
//    public String aa(HttpServletRequest request, ModelMap modelMap) throws Exception {
//        PageQuery pageQuery = new PageQuery(0,10);
//        List<StoProductDescription> sdList = uploadFileService.aaa(pageQuery);
//        for (StoProductDescription sd : sdList) {
//            System.out.println(sd.getPid() +" : 进入");
//            Pattern p_image;
//            Matcher m_image;
////       String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址
//            String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
//            p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
//            m_image = p_image.matcher("<img>" + sd.getDescription());
//
//            StringBuffer sb = new StringBuffer();
//            while (m_image.find()) {
//                // Matcher m =
//                // Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(img); //匹配src
//                Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(m_image.group());
//                while (m.find()) {
//                    if ("http".equals(m.group(1).substring(0,4))) continue;
//                    Integer id = uploadFileService.saveOssUploadFileByBase64(m.group(1));
////                    System.out.println(OSSClientUtil.getObjectUrl(id+""));
//
//                    m.appendReplacement(sb, "src=\"" + OSSClientUtil.getObjectUrl(id+"") + "\"");
////                break;
//                }
//                m.appendTail(sb);
////            break;
//            }
////            System.out.println(sb.substring(5));
//            sd.setDescription(sb.substring(5));
//            uploadFileService.upsert2(sd);
//            System.out.println(sd.getPid() +" : 修改成功");
//        }
//        return null;
//    }

    public static void main(String[] args) throws Exception {
        Pattern p_image;
        Matcher m_image;
//       String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址
        String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher("<img><p style=\"text-align: center;\">" +
                "<img src=\"http://kaa.oss-cn-hangzhou.aliyuncs.com/478?\"/></p>" +
                "<p style=\"text-align: center;\">" +
                "<img src=\"http://kaa.oss-cn-hangzhou.aliyuncs.com/477\"/></p>" +
                "<p style=\"text-align: center;\">" +
                "<img src=\"http://kaa.oss-cn-hangzhou.aliyuncs.com/476?\"/></p>" +
                "<p style=\"text-align: center;\">"  +
                "<img src=\"http://kaa.oss-cn-hangzhou.aliyuncs.com/479\"/></p>");
        StringBuffer sb = new StringBuffer();
        while (m_image.find()) {
            // Matcher m =
            // Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(img); //匹配src
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(m_image.group());
            while (m.find()) {
                if ("http".equals(m.group(1).substring(0,4))) continue;
                System.out.println(m.group(1));
                m.appendReplacement(sb, "src=\"66666666\"");
            }
            m.appendTail(sb);
//            m_image.appendReplacement(sb, "66666666");
        }
//        m_image.appendTail(sb);
        System.out.println(sb.substring(5));
    }

}
