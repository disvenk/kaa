package com.xxx.suplier.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.SupProcedure;
import com.xxx.suplier.form.CategorySaveForm;
import com.xxx.suplier.form.ProcedureFroms;
import com.xxx.suplier.form.ProcedureSaveForm;
import com.xxx.suplier.service.ProductProcedureService;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping("procedure")
public class ProductProcedureController {

    @Autowired
    private ProductProcedureService productProcedureService;

    /**
     * @Description:列表不分页
     * @Author: disvenk.dai
     * @Date: 2018/1/10
     */
    @RequestMapping(value="/findProcedureListCombox",method = {RequestMethod.POST})
    public ResponseEntity findCategoryListCombox() throws ResponseEntityException {

        List<SupProcedure> list = productProcedureService.findProcedureListCombox();
        JSONArray data = new JSONArray();
        for(SupProcedure supProcedure : list){
            JSONObject json = new JSONObject();
            json.put("id", supProcedure.getId());
            json.put("name", supProcedure.getName());
            json.put("price",supProcedure.getPrice());
            json.put("remarks", supProcedure.getRemarks());
            json.put("updateDate", DateTimeUtils.parseStr(supProcedure.getUpdateDate()));
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功",data), HttpStatus.OK);
    }

    /**
     * @Description:工序列表
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @RequestMapping(value="/findProcedureList",method = {RequestMethod.POST})
    public ResponseEntity findCategoryList(@RequestBody CategorySaveForm form) throws ResponseEntityException {
        if(form.pageNum==null)return new ResponseEntity(new RestResponseEntity(110,"页码不能为空",null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
       PageList<SupProcedure> list = productProcedureService.findProcedureList(pageQuery,form.name);
        JSONArray data = new JSONArray();
        for(SupProcedure supProcedure : list){
            JSONObject json = new JSONObject();
            json.put("id", supProcedure.getId());
            json.put("name", supProcedure.getName());
            json.put("price", supProcedure.getPrice() == null ? "" : supProcedure.getPrice());
            json.put("remarks", StringUtils.trimToEmpty(supProcedure.getRemarks()));
            json.put("updateDate", DateTimeUtils.parseStr(supProcedure.getUpdateDate()));
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description:批量增加
     * @Author: disvenk.dai
     * @Date: 2018/1/10
     */
    @RequestMapping(value="/addProductProcedureMany",method = {RequestMethod.POST})
    public ResponseEntity addProductProcedureMany(@RequestBody ProcedureSaveForm form) throws UpsertException {
        for (ProcedureFroms procedureFroms: form.list) {
            if(procedureFroms.price==null)
                return new ResponseEntity(new RestResponseEntity(110,"工价不能为空",null), HttpStatus.OK);
            Integer result=null;
            try{
                BigDecimal b = new BigDecimal("0");
                BigDecimal c = new BigDecimal(procedureFroms.price);
                result = new BigDecimal(procedureFroms.price).compareTo(b);
            }catch (Exception e){
                try {
                    throw new ResponseEntityException(220,"工价不能输入非法字符");
                } catch (ResponseEntityException e1) {
                    e1.printStackTrace();
                }
            }

            if(procedureFroms.name==null && StringUtils.isBlank(procedureFroms.name))
                return new ResponseEntity(new RestResponseEntity(110,"工序名称不能为空",null), HttpStatus.OK);
            if(procedureFroms.price==null || result==0 || result==-1)
                return new ResponseEntity(new RestResponseEntity(110,"工价不能为空或0或负数",null), HttpStatus.OK);
            if(procedureFroms.price==null)
                return new ResponseEntity(new RestResponseEntity(110,"工价不能为空",null), HttpStatus.OK);
        }


        try {
            productProcedureService.addProductProcedure(form);
        } catch (ResponseEntityException e) {
            e.printStackTrace();
            return new ResponseEntity(new RestResponseEntity(100,"新增失败，请联系管理员",null), HttpStatus.OK);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:增加
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @RequestMapping(value="/addProductProcedure",method = {RequestMethod.POST})
    public ResponseEntity addProductProcedure(@RequestBody ProcedureSaveForm form) throws UpsertException {
        if(form.price==null)
            return new ResponseEntity(new RestResponseEntity(110,"工价不能为空",null), HttpStatus.OK);
        Integer result=null;
        try{
            BigDecimal b = new BigDecimal("0");
         BigDecimal c = new BigDecimal(form.price);
            result = new BigDecimal(form.price).compareTo(b);
        }catch (Exception e){
            try {
                throw new ResponseEntityException(220,"工价不能输入非法字符");
            } catch (ResponseEntityException e1) {
                e1.printStackTrace();
            }
        }

        if(form.name==null && StringUtils.isBlank(form.name))
            return new ResponseEntity(new RestResponseEntity(110,"工序名称不能为空",null), HttpStatus.OK);
        if(form.price==null || result==0 || result==-1)
            return new ResponseEntity(new RestResponseEntity(110,"工价不能为空或0或负数",null), HttpStatus.OK);
        if(form.price==null)
            return new ResponseEntity(new RestResponseEntity(110,"工价不能为空",null), HttpStatus.OK);
        if(form.sort==null)
            return new ResponseEntity(new RestResponseEntity(110,"排序不能为空",null), HttpStatus.OK);

        try {
            productProcedureService.addProductProcedure(form);
        } catch (ResponseEntityException e) {
            e.printStackTrace();
            return new ResponseEntity(new RestResponseEntity(100,"新增失败，请联系管理员",null), HttpStatus.OK);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:编辑工序Max
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @RequestMapping(value="/editorProductProcedure",method = {RequestMethod.POST})
    public ResponseEntity editorProductProcedure(@RequestBody ProcedureSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        if(form.id==null && StringUtils.isBlank(form.name))
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null), HttpStatus.OK);
        if(form.name==null && StringUtils.isBlank(form.name))
            return new ResponseEntity(new RestResponseEntity(110,"工序名称不能为空",null), HttpStatus.OK);
        if(form.price==null)
            return new ResponseEntity(new RestResponseEntity(110,"工价不能为空",null), HttpStatus.OK);
        if(form.sort==null)
            return new ResponseEntity(new RestResponseEntity(110,"排序不能为空",null), HttpStatus.OK);
        Integer sort = productProcedureService.getIndex(form.id);
        if(form.sort>sort){
            return  productProcedureService.addProductProcedureMax(form);
        }else if(form.sort<sort){
            return  productProcedureService.addProductProcedureMin(form);
        }else{
            productProcedureService.addProductProcedure(form);
            return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
        }
    }

    /**
     * @Description:查询最大排序
     * @Author: disvenk.dai
     * @Date: 2018/1/12
     */
    @RequestMapping(value="/checkMaxSort",method = {RequestMethod.POST})
    public ResponseEntity checkMaxSort() throws ResponseEntityException {

        Integer count = productProcedureService.checkMaxSort();
        return new ResponseEntity(new RestResponseEntity(100,"成功",count), HttpStatus.OK);
    }

    /**
     * @Description:查询唯一
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @RequestMapping(value="/checkUniqueProcedure",method = {RequestMethod.POST})
    public ResponseEntity checkUniqueBase(@RequestBody ProcedureSaveForm form) throws ResponseEntityException {
        if(form.name==null || StringUtils.isBlank(form.name))
            return new ResponseEntity(new RestResponseEntity(120,"工序名称不能为空",null), HttpStatus.OK);
        Boolean isUnique = productProcedureService.checkUnique(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",isUnique), HttpStatus.OK);
    }

    /**
     * @Description:编辑详情
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @RequestMapping(value="/checkProductProcedureDetail",method = {RequestMethod.POST})
    public ResponseEntity checkProductBaseDetail(@RequestBody ProcedureSaveForm form) throws ResponseEntityException {
        if(form.id==null)
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null), HttpStatus.OK);
        SupProcedure supProcedure = productProcedureService.checkProcedureDetail(form);
        JSONObject json = new JSONObject();
        json.put("id", supProcedure.getId());
        json.put("name", supProcedure.getName());
        json.put("price",supProcedure.getPrice());
        json.put("remarks", supProcedure.getRemarks());
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description:删除
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value="deleteProductProcedure",method = {RequestMethod.POST})
    public ResponseEntity deleteProductBase(@RequestBody ProcedureSaveForm form){
        if(form.id==null)
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null), HttpStatus.OK);
        try {
            productProcedureService.deleteProcedure(form);
            return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(new RestResponseEntity(110,"删除失败",null), HttpStatus.OK);
        }

    }
}
