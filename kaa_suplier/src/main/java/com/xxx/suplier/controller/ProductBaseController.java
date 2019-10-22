package com.xxx.suplier.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.SupProductBase;
import com.xxx.suplier.form.CategorySaveForm;
import com.xxx.suplier.form.SupProductBaseForm;
import com.xxx.suplier.service.ProductBaseService;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("productBase")
public class ProductBaseController {

    @Autowired
    private ProductBaseService productBaseService;

    /**
     * @Description:分类下拉接口
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @RequestMapping(value="/findCategoryListCombox",method = {RequestMethod.POST})
    public ResponseEntity findCategoryListCombox(@RequestBody CategorySaveForm form) throws ResponseEntityException {
        if(form.type==null)return new ResponseEntity(new RestResponseEntity(110,"查询类型不能为空",null), HttpStatus.OK);
        List<SupProductBase> list = productBaseService.findCategoryListCombox(form.type);
        JSONArray data = new JSONArray();
        for(SupProductBase supProductBase : list){
            JSONObject json = new JSONObject();
            json.put("id", supProductBase.getId());
            json.put("name", supProductBase.getName());
            json.put("remarks", supProductBase.getRemarks());
            json.put("createdDate",DateTimeUtils.parseStr(supProductBase.getCreatedDate()));
            json.put("updateDate", DateTimeUtils.parseStr(supProductBase.getUpdateDate()));
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功",data), HttpStatus.OK);
    }

    /**
     * @Description:分类列表
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @RequestMapping(value="/findCategoryList",method = {RequestMethod.POST})
    public ResponseEntity findCategoryList(@RequestBody CategorySaveForm form) throws ResponseEntityException {
        if(form.pageNum==null)return new ResponseEntity(new RestResponseEntity(110,"页码不能为空",null), HttpStatus.OK);
        if(form.type==null)return new ResponseEntity(new RestResponseEntity(110,"查询类型不能为空",null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
       PageList<SupProductBase> list = productBaseService.findCategoryList(pageQuery,form.type,form.name);
        JSONArray data = new JSONArray();
        for(SupProductBase supProductBase : list){
            JSONObject json = new JSONObject();
            json.put("id", supProductBase.getId());
            json.put("name", StringUtils.trimToEmpty(supProductBase.getName()));
            json.put("remarks", StringUtils.trimToEmpty(supProductBase.getRemarks()));
            json.put("createdDate",DateTimeUtils.parseStr(supProductBase.getCreatedDate()));
            json.put("updateDate", DateTimeUtils.parseStr(supProductBase.getUpdateDate()));
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description:批量增加
     * @Author: disvenk.dai
     * @Date: 2018/1/10
     */
    @RequestMapping(value="/addProductBaseMany",method = {RequestMethod.POST})
    public ResponseEntity addProductBaseMany(@RequestBody CategorySaveForm form) throws UpsertException, ResponseEntityException {
        for (SupProductBaseForm supProductBaseForm : form.list) {
            if(supProductBaseForm.name==null && StringUtils.isBlank(supProductBaseForm.name))
                return new ResponseEntity(new RestResponseEntity(110,"名称不能为空",null), HttpStatus.OK);
        }
        productBaseService.addProductBaseMany(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:增加和编辑
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @RequestMapping(value="/addProductBase",method = {RequestMethod.POST})
    public ResponseEntity addProductBase(@RequestBody CategorySaveForm form) throws UpsertException, ResponseEntityException {
     if(form.name==null && StringUtils.isBlank(form.name))
         return new ResponseEntity(new RestResponseEntity(110,"名称不能为空",null), HttpStatus.OK);
        productBaseService.addProductBase(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:查询唯一
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @RequestMapping(value="/checkUniqueBase")
    public ResponseEntity checkUniqueBase(@RequestBody CategorySaveForm form) throws ResponseEntityException {
        if(form.name==null || StringUtils.isBlank(form.name))
            return new ResponseEntity(new RestResponseEntity(120,"分类名称不能为空",null), HttpStatus.OK);
        Boolean isUnique = productBaseService.checkUnique(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",isUnique), HttpStatus.OK);
    }

    /**
     * @Description:编辑详情
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @RequestMapping(value="/checkProductBaseDetail",method = {RequestMethod.POST})
    public ResponseEntity checkProductBaseDetail(@RequestBody CategorySaveForm form) throws ResponseEntityException {
        if(form.id==null)
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null), HttpStatus.OK);
        SupProductBase supProductBase = productBaseService.checkBaseDetail(form);
        JSONObject json = new JSONObject();
        json.put("id", supProductBase.getId());
        json.put("name", supProductBase.getName());
        json.put("remarks", supProductBase.getRemarks());
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description:删除
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value="deleteProductBase",method = {RequestMethod.POST})
    public ResponseEntity deleteProductBase(@RequestBody CategorySaveForm form){
        if(form.id==null)
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null), HttpStatus.OK);
        try {
            productBaseService.deleteBase(form);
            return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(new RestResponseEntity(110,"删除失败",null), HttpStatus.OK);
        }

    }
}
