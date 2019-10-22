package com.xxx.admin.controller;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.ProductListForm;
import com.xxx.admin.service.SuplierProductService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/suplierProduct")
public class SuplierProductController {

    @Autowired
    private SuplierProductService suplierProductService;

    /**
     * @Description: 获取供应商商品列表
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/productList", method = {RequestMethod.POST})
    public ResponseEntity productList(@RequestBody ProductListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 10;
        PageList<SupProduct> supProductList = suplierProductService.findSupProductList(pageQuery, form.suplierId, form.pno, form.name);
        JSONArray data = new JSONArray();
        for (SupProduct supProduct : supProductList) {
            JSONObject json = new JSONObject();
            json.put("id", supProduct.getId());
            json.put("pno", supProduct.getPno());
            json.put("name", supProduct.getName());
            json.put("href", OSSClientUtil.getObjectUrl(supProduct.getHref()));
            json.put("categoryName", supProduct.getCategoryBase() == null ? "" : supProduct.getCategoryBase().getName());
            json.put("updateTime", DateTimeUtils.parseStr(supProduct.getUpdateDate()));
            json.put("remarks", supProduct.getRemarks());
            json.put("suplierName", supProduct.getSupSuplier() == null ? "" : supProduct.getSupSuplier().getName());
            Set<String> colors = new HashSet<>();
            Set<String> sizes = new HashSet<>();
            for (SupProductPrice price : supProduct.getSupProductPriceList()) {
                if (price.getColorBase() == null || price.getSizeBase() == null) continue;
                colors.add(price.getColorBase().getName());
                sizes.add(price.getSizeBase().getName());
            }
            json.put("color", colors.size() == 0 ? "" : colors.toString().substring(1, colors.toString().length() - 1));
            json.put("size", sizes.size() == 0 ? "" : sizes.toString().substring(1, sizes.toString().length() - 1));
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, supProductList.total), HttpStatus.OK);
    }



}
