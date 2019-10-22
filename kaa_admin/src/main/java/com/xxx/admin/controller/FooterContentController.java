package com.xxx.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.BooleanForm;
import com.xxx.admin.form.FooterContentQueryForm;
import com.xxx.admin.form.FooterContentSaveForm;
import com.xxx.admin.form.IdForm;
import com.xxx.admin.service.FooterContentService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.SalesVedioInfo;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("footerContent")
public class FooterContentController {

    @Autowired
    private FooterContentService footerContentService;

    /**
     * @Description: 底部内容列表查询
     * @Author: disvenk.dai
     * @Date: 2017/12/23
     */
    @RequestMapping(value="findFooterContentList",method = {RequestMethod.POST})
    public ResponseEntity findFooterContentList(@RequestBody FooterContentQueryForm form){
        if(form.pageNum==null){
            new ResponseEntity(new RestResponseEntity(110,"页码不能为空",null),HttpStatus.OK);
        }
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SalesVedioInfo> list = footerContentService.findFooterContetnList(pageQuery,form);
        JSONArray data = new JSONArray();
        for (SalesVedioInfo salesVedioInfo : list) {
            JSONObject json = new JSONObject();
            json.put("id",salesVedioInfo.getId());
            json.put("title",salesVedioInfo.getTitle());
            json.put("kind",salesVedioInfo.getVedioType());
            json.put("isShow",salesVedioInfo.getShow());
            json.put("watch",salesVedioInfo.getViews());
            if(salesVedioInfo.getUpdateDate()==null){
                json.put("updateDate","");
            }else {
                json.put("updateDate", DateTimeUtils.parseStr(salesVedioInfo.getUpdateDate()));
            }
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description：底部内容新增
     * @Author: disvenk.dai
     * @Date: 2017/12/24 0026
     */
    @RequestMapping(value = "/insertFooterContent", method = {RequestMethod.POST})
    public ResponseEntity insertFooterContent(@RequestBody FooterContentSaveForm form) throws Exception {
       footerContentService.saveFooterContent(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description：底部内容查询
     * @Author: disvenk.dai
     * @Date: 2017/12/24
     */
    @RequestMapping(value = "/findFooterContent", method = {RequestMethod.POST})
    public ResponseEntity findFooterContent(@RequestBody IdForm form) throws Exception {
        if(form.id==null){
            return  new ResponseEntity(new RestResponseEntity(110,"id不能为空",null), HttpStatus.OK);

        }
       SalesVedioInfo salesVedioInfo = footerContentService.getSalesVedioInfo(form.id);
        JSONObject json = new JSONObject();
        json.put("name",salesVedioInfo.getTitle());
        json.put("key",salesVedioInfo.getPictureUrl());
        json.put("picture", OSSClientUtil.getObjectUrl(salesVedioInfo.getPictureUrl()));
        json.put("vedioUrl",OSSClientUtil.getObjectUrl(salesVedioInfo.getVedioUrl()));
        json.put("vedioKey",salesVedioInfo.getVedioUrl());
        json.put("shortDesc",salesVedioInfo.getShortDescription());
        json.put("desc",salesVedioInfo.getDescription());
        json.put("kind",salesVedioInfo.getVedioType());
        json.put("watch",salesVedioInfo.getViews());
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description: 修改内容展示状态
     * @Author: disvenk.dai
     * @Date: 2017/12/24
     */
    @RequestMapping(value = "/isShowUpdate", method = {RequestMethod.POST})
    public ResponseEntity isShowUpdate(@RequestBody BooleanForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        footerContentService.updateIsShow(form.id, form.isTrue);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 内容删除
     * @Author: disvenk.dai
     * @Date: 2017/12/14
     */
    @RequestMapping(value = "/footerContentRemove", method = {RequestMethod.POST})
    public ResponseEntity channelRemove(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "内容id不能为空", null), HttpStatus.OK);
        footerContentService.removeCmsContent(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }
}
