package com.xxx.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.BooleanForm;
import com.xxx.admin.form.DesignerListForm;
import com.xxx.admin.form.DesignerSaveForm;
import com.xxx.admin.form.IdForm;
import com.xxx.admin.service.DesignerService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.DesDesigner;
import com.xxx.model.business.PubUserBase;
import com.xxx.user.Commo;
import com.xxx.utils.OSSClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/designer")
public class DesignerController {

    @Autowired
    private DesignerService designerService;

    @RequestMapping(value = "/designerHtml", method = {RequestMethod.GET})
    public String data_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/designerManage/designerManage";
    }

    @RequestMapping(value = "/designerAddHtml", method = {RequestMethod.GET})
    public String designerAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/designerManage/designerManageAdd";
    }

   /**
    * @Description: 设计师列表
    * @Author: Chen.zm
    * @Date: 2017/11/22 0022
    */
    @RequestMapping(value = "/designerList", method = {RequestMethod.POST})
    public ResponseEntity designerList(@RequestBody DesignerListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<DesDesigner> desDesigners = designerService.designerList(pageQuery, form.name, form.phone);
        JSONArray data = new JSONArray();
        for (DesDesigner designer : desDesigners) {
            PubUserBase userBase = designer.getPubUserLogin() == null ? new PubUserBase() :
                    designer.getPubUserLogin().getPubUserBase() == null ?  new PubUserBase() : designer.getPubUserLogin().getPubUserBase();
            JSONObject json = new JSONObject();
            json.put("id", designer.getId());
            json.put("phone", userBase.getMobile());
            json.put("icon", OSSClientUtil.getObjectUrl(userBase.getIcon()));
            json.put("name", userBase.getName());
            json.put("sex", Commo.parseSex(userBase.getSex()));
            json.put("type", Commo.parseDesignerType(designer.getType()));
            json.put("city", designer.getProvinceName() + "-" + designer.getCityName());
            json.put("remarks", designer.getRemarks());
            json.put("isShow", designer.getShow());
            data.add(json);
            // 过滤value == null 的数据    使其为空
            for (String key : json.keySet()) {
                if (json.get(key) == null) json.put(key, "");
            }
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, desDesigners.total), HttpStatus.OK);
    }


    /**
     * @Description: 设计师详情
     * @Author: Chen.zm
     * @Date: 2017/11/22 0022
     */
    @RequestMapping(value = "/designerDetail", method = {RequestMethod.POST})
    public ResponseEntity designerDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        DesDesigner designer = designerService.designerDetail(form.id);
        if (designer == null)
            return new ResponseEntity(new RestResponseEntity(120, "设计师不存在", null), HttpStatus.OK);
        PubUserBase userBase = designer.getPubUserLogin() == null ? new PubUserBase() :
                designer.getPubUserLogin().getPubUserBase() == null ?  new PubUserBase() : designer.getPubUserLogin().getPubUserBase();
        JSONObject json = new JSONObject();
        json.put("id", designer.getId());
        json.put("phone", userBase.getMobile());
        json.put("icon", OSSClientUtil.getObjectUrl(userBase.getIcon()));
        json.put("name", userBase.getName());
        json.put("sex", userBase.getSex());
        json.put("type", designer.getType());
        json.put("city", designer.getProvinceName() + "-" + designer.getCityName());
        json.put("remarks", designer.getRemarks());
        json.put("isShow", designer.getShow());
        json.put("resume", designer.getResume());
        json.put("description", designer.getDescription());
        json.put("address", designer.getAddress());
        json.put("province", designer.getProvince());
        json.put("city", designer.getCity());
        json.put("zone", designer.getZone());
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description: 修改设计师展示状态
     * @Author: Chen.zm
     * @Date: 2017/11/22 0022
     */
    @RequestMapping(value = "/isShowUpdate", method = {RequestMethod.POST})
    public ResponseEntity isShowUpdate(@RequestBody BooleanForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        designerService.updateIsShow(form.id, form.isTrue);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 删除设计师
     * @Author: Chen.zm
     * @Date: 2017/11/22 0022
     */
    @RequestMapping(value = "/designerDelete", method = {RequestMethod.POST})
    public ResponseEntity designerDelete(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        designerService.deleteDesigner(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }


    /**
     * @Description: 设计师编辑新增
     * @Author: Chen.zm
     * @Date: 2017/11/22 0022
     */
    @RequestMapping(value = "/designerSave", method = {RequestMethod.POST})
    public ResponseEntity designerSave(@RequestBody DesignerSaveForm form) throws Exception {
        if (StringUtils.isBlank(form.mobile))
            return new ResponseEntity(new RestResponseEntity(110, "手机号不能为空", null), HttpStatus.OK);
        designerService.saveDesigner(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }




}
