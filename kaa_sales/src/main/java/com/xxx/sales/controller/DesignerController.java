package com.xxx.sales.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.sales.form.PageNumForm;
import com.xxx.sales.service.DesignerService;
import com.xxx.model.business.DesDesigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/designer")
public class DesignerController {

    @Autowired
    private DesignerService designerService;

   /**
    * @Description: 设计师列表
    * @Author: Chen.zm
    * @Date: 2017/11/22 0022
    */
    @RequestMapping(value = "/designerList", method = {RequestMethod.POST})
    public ResponseEntity designerList(@RequestBody PageNumForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<DesDesigner> desDesigners = designerService.designerList(pageQuery);
        JSONArray data = new JSONArray();
        for (DesDesigner designer : desDesigners) {
            JSONObject json = new JSONObject();
            json.put("description", designer.getDescription());
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, desDesigners.total), HttpStatus.OK);
    }


}
