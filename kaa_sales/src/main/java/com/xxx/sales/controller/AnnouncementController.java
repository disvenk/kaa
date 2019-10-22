package com.xxx.sales.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.CmsContent;
import com.xxx.model.business.CmsMenu;
import com.xxx.sales.form.IdForm;
import com.xxx.sales.service.CmsService;
import com.xxx.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/announcementManage")
public class AnnouncementController {
    @Autowired
    private CmsService cmsService;

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
    public ResponseEntity findCmsList() throws Exception{

        PageQuery pageQuery = new PageQuery(1);
        PageList<CmsContent> list = cmsService.findCmsContentList(pageQuery);
       JSONArray data = new JSONArray();
       if(list.size()>=4){
           for(int i=0;i<4;i++){
               JSONObject json = new JSONObject();
               json.put("id",list.get(i).getId());
               json.put("title",list.get(i).getTitle());
               CmsMenu cmsMenu = cmsService.getCmsMenu(Integer.parseInt(list.get(i).getMenuId()));
               json.put("style",cmsMenu.getName());
               json.put("updateDate", DateTimeUtils.parseStr(list.get(i).getUpdateDate()));
               json.put("sort",list.get(i).getSort());
               json.put("isShow",list.get(i).getShow());
               data.add(json);
           }
       }else if(list.size()<4){
           for(int i=0;i<list.size();i++){
               JSONObject json = new JSONObject();
               json.put("id",list.get(i).getId());
               json.put("title",list.get(i).getTitle());
               CmsMenu cmsMenu = cmsService.getCmsMenu(Integer.parseInt(list.get(i).getMenuId()));
               json.put("style",cmsMenu.getName());
               json.put("updateDate", DateTimeUtils.parseStr(list.get(i).getUpdateDate()));
               json.put("sort",list.get(i).getSort());
               json.put("isShow",list.get(i).getShow());
               data.add(json);
           }
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
           json.put("updateDate", DateTimeUtils.parseStr(cmsContent.getUpdateDate()));

        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

}
