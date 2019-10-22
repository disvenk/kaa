package com.xxx.suplier.controller;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.suplier.form.*;
import com.xxx.suplier.service.SuplierProductService;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
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
        PageList<SupProduct> supProductList = suplierProductService.findSupProductList(pageQuery, CurrentUser.get().getSuplierId(), form.pno, form.name);
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

    /**
     * @Description: 供应商商品删除
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/removeProduct", method = {RequestMethod.POST})
    public ResponseEntity removeProduct(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        suplierProductService.removeProduct(form.id, CurrentUser.get().getSuplierId());
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }



    /**
     * @Description: 供应商商品详情
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/productDetail", method = {RequestMethod.POST})
    public ResponseEntity productDetail(@RequestBody ProductDetailForm form) throws Exception {
        if (form.id == null && StringUtils.isBlank(form.pno))
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        SupProduct supProduct = suplierProductService.findSupProductDateil(CurrentUser.get().getSuplierId(), form.id, form.pno);
        if (supProduct == null)
            return new ResponseEntity(new RestResponseEntity(120, "商品不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("id", supProduct.getId());
        json.put("pno", supProduct.getPno());
        json.put("name", supProduct.getName());
        json.put("categoryId", supProduct.getCategoryId());
        json.put("categoryName", supProduct.getCategoryBase() == null ? "" : supProduct.getCategoryBase().getName());
        json.put("material", supProduct.getMaterial());
        json.put("technics", supProduct.getTechnics());
        json.put("description", supProduct.getDescription());

        json.put("shoulder", supProduct.getShoulder());
        json.put("bust", supProduct.getBust());
        json.put("waist", supProduct.getWaist());
        json.put("hipline", supProduct.getHipline());
        json.put("height", supProduct.getHeight());
        json.put("weight", supProduct.getWeight());
        json.put("throatheight", supProduct.getThroatheight());
        json.put("price", supProduct.getPrice());

        json.put("remarks", supProduct.getRemarks());
        JSONArray pictures = new JSONArray();
        for (SupProductPicture picture : supProduct.getSupProductPictureList()) {
            JSONObject img = new JSONObject();
            img.put("href", OSSClientUtil.getObjectUrl(picture.getHref()));
            img.put("key", picture.getHref());
            pictures.add(img);
        }
        json.put("pictures", pictures);

        Set<String> colors = new HashSet<>();
        Set<String> sizes = new HashSet<>();
        for (SupProductPrice price : supProduct.getSupProductPriceList()) {
            if (price.getColorBase() == null || price.getSizeBase() == null) continue;
            colors.add(price.getColorBase().getName());
            sizes.add(price.getSizeBase().getName());
        }
        json.put("colors", colors);
        json.put("sizes", sizes);

        //获取原材料
        List<SupProductMaterial> materialList = suplierProductService.findSupProductMaterialList(supProduct.getId());
        JSONArray materials = new JSONArray();
        for (SupProductMaterial material : materialList) {
            JSONObject js = new JSONObject();
            js.put("materialId", material.getMaterialId());
            js.put("materialName", material.getMaterialBase() == null ? "" : material.getMaterialBase().getName());
            js.put("count", material.getCount());
            js.put("price", material.getPrice());
            js.put("unit", material.getUnit());
            materials.add(js);
        }
        json.put("materials", materials);

        //获取工序
        List<SupProductProcedure> procedureList = suplierProductService.findSupProductProcedureList(supProduct.getId());
        JSONArray procedures = new JSONArray();
        for (SupProductProcedure procedure : procedureList) {
            JSONObject js = new JSONObject();
            js.put("procedureId", procedure.getProcedureId());
            js.put("procedureName", procedure.getSupProcedure() == null ? "" : procedure.getSupProcedure().getName());
            js.put("price", procedure.getPrice());
            procedures.add(js);
        }
        json.put("procedures", procedures);

        for(String key : json.keySet()){
            if(json.get(key) == null) json.put(key,"");
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description: 保存商品
     * @Author: Chen.zm
     * @Date: 2017/11/10 0010
     */
    @RequestMapping(value = "/saveSuplierProduct", method = {RequestMethod.POST})
    public ResponseEntity saveSuplierProduct(HttpServletRequest request, @RequestBody SuplierProductForm form) throws Exception {
        suplierProductService.saveSuplierProduct(CurrentUser.get().getSuplierId(), form);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

}
