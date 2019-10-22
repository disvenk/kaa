package com.xxx.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.BooleanForm;
import com.xxx.admin.form.CmsContentAddForm;
import com.xxx.admin.form.CmsQueryForm;
import com.xxx.admin.form.IdForm;
import com.xxx.admin.service.CmsService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.CmsContent;
import com.xxx.model.business.CmsMenu;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/announcementManage")
public class AnnouncementController {
    @Autowired
    private CmsService cmsService;


    /**
     * @Description: cms:公告管理列表页面
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @RequestMapping(value="/announcementManageHtml",method = {RequestMethod.GET})
    public String announcementManageHtml(HttpServletRequest request, ModelMap modelMap){
        return  "/salesHeadCategoryManage/announcementManage";
    }

    /**
     * @Description: cms:新增页面
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @RequestMapping(value="/announcementAddHtml",method = {RequestMethod.GET})
    public String announcementAddHtml(){
        return "/salesHeadCategoryManage/announcementManageAdd";
    }

    /**
     * @Description: cms:编辑页面
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @RequestMapping(value="/announcementEditHtml",method = {RequestMethod.GET})
    public String announcementEditHtml(){
        return "/salesHeadCategoryManage/announcementManageEdit";
    }

    /**
     * @Description: cms:查询菜单列表
     * @Author: disvenk.dai
     * @Date: 2017/12/20
     */
    @RequestMapping(value="/findCmsMenuList",method = {RequestMethod.POST})
    public ResponseEntity findCmsMenuList() throws Exception{
        List<CmsMenu> list = cmsService.findCmsMenuList();
        JSONArray data = new JSONArray();
        for(CmsMenu cmsMenu : list){
            JSONObject json = new JSONObject();
            json.put("id",cmsMenu.getId());
            json.put("name",cmsMenu.getName());
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, 0, 1, 0), HttpStatus.OK);
    }

    /**
     * @Description: cms:查询公告列表
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @RequestMapping(value="/findCmsList",method = {RequestMethod.POST})
    public ResponseEntity findCmsList(@RequestBody CmsQueryForm cmsQueryForm) throws Exception{
        if(cmsQueryForm.pageNum == null) {
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        }
        PageQuery pageQuery = new PageQuery(cmsQueryForm.pageNum);
        PageList<CmsContent> list = cmsService.findCmsContentList(pageQuery,cmsQueryForm.name);
       JSONArray data = new JSONArray();
        for(CmsContent cmsContent : list){
            JSONObject json = new JSONObject();
            json.put("id",cmsContent.getId());
            json.put("title",cmsContent.getTitle());
            CmsMenu cmsMenu = cmsService.getCmsMenu(Integer.parseInt(cmsContent.getMenuId()));
            json.put("style",cmsMenu.getName());
            json.put("updateDate", DateTimeUtils.parseStr(cmsContent.getUpdateDate()));
            json.put("sort",cmsContent.getSort());
            json.put("isShow",cmsContent.getShow());
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description: cms:查询单个公告列表
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @RequestMapping(value="/findCmsContent",method = {RequestMethod.POST})
    public ResponseEntity findCmsContent(@RequestBody IdForm form) throws Exception{
        if(form.id == null) {
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        }
      CmsContent cmsContent =  cmsService.getCmsContent(form.id);


            JSONObject json = new JSONObject();
            json.put("id",cmsContent.getId());
            json.put("title",cmsContent.getTitle());
            json.put("content",cmsContent.getContent());
            CmsMenu cmsMenu = cmsService.getCmsMenu(Integer.parseInt(cmsContent.getMenuId()));
            json.put("style",cmsMenu.getName());

        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description: cms:增加公告
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @RequestMapping(value = "/saveCmsContent", method = {RequestMethod.POST})
    public ResponseEntity saveChannel(@RequestBody CmsContentAddForm form) throws Exception {
        if (StringUtils.isBlank(form.title) || "".equals(form.title))
            return new ResponseEntity(new RestResponseEntity(110, "标题不能为空", null), HttpStatus.OK);
        cmsService.saveCmsContent(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 公告编辑保存
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @RequestMapping(value = "/cmsContentUpdate", method = {RequestMethod.POST})
    public ResponseEntity channelUpdate(@RequestBody CmsContentAddForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "公告id不能为空", null), HttpStatus.OK);
        cmsService.updateCmsContent(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 公告删除
     * @Author: disvenk.dai
     * @Date: 2017/12/19
     */
    @RequestMapping(value = "/cmsContentRemove", method = {RequestMethod.POST})
    public ResponseEntity channelRemove(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "公告id不能为空", null), HttpStatus.OK);
        cmsService.removeCmsContent(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 修改公告展示状态
     * @Author: disvenk.dai
     * @Date: 2017/12/20
     */
    @RequestMapping(value = "/isShowUpdate", method = {RequestMethod.POST})
    public ResponseEntity isShowUpdate(@RequestBody BooleanForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        cmsService.updateIsShow(form.id, form.isTrue);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }
}
