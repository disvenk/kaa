package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.*;
import com.xxx.admin.service.SalesProductManageService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.model.business.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/salesProduct")
public class SalesProductManageController {

    @Autowired
    private SalesProductManageService salesProductManageService;

    @RequestMapping(value = "/showGoodsHtml", method = {RequestMethod.GET})
    public String data1_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/showGoods/goodsManage";
    }

    @RequestMapping(value = "/showGoodsEditHtml", method = {RequestMethod.GET})
    public String data2_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "/showGoods/goodsManageEdit";
    }

    @RequestMapping(value = "/goodsManageFromStoreHtml", method = {RequestMethod.GET})
    public String data3_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/showGoods/goodsManageFromStore";
    }

    @RequestMapping(value = "/goodsManageEditHtml", method = {RequestMethod.GET})
    public String data4_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "/goodsManage/goodsManageEdit";
    }


    /**
     * @Description:商品库商品添加到销售平台商品
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @RequestMapping(value = "/insertSalesProducManage", method = {RequestMethod.POST})
    public ResponseEntity insertsalesProducManage(@RequestBody IdForm form) throws Exception {
        if (form.id ==null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        salesProductManageService.insertsalesProducManage(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:销售平台从商品库新增商品(筛选列表显示)
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @RequestMapping(value = "/storeAddProductManageList", method = {RequestMethod.POST})
    public ResponseEntity storeAddProductManageList(@RequestBody SalesAddProductManageForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 12;
        List<Integer> ids = new ArrayList<>();
        for (SalesAddProductCategoryForm id : form.salesProductCategoryList) {
            ids.add(id.categoryId);//父分类
            ids.add(id.relateCategoryId); //子分类
        }
        StringBuffer str = new StringBuffer();
        for (SalesAddProductLabelForm id : form.salesProductLabelList) str.append(id.labelId + ",");

        if (str.length() > 0) str = new StringBuffer(str.substring(0, str.length() - 1));

        PageList<PlaProduct> plaProductList = salesProductManageService.findSalesProductManageList(pageQuery, ids, str.toString());
        JSONArray jsonArray = new JSONArray();
        for (PlaProduct pla : plaProductList) {
            JSONObject json = new JSONObject();
            json.put("id", pla.getId());
            json.put("name", pla.getName());
            json.put("mainpicHref", OSSClientUtil.getObjectUrl(pla.getHref()));//主图地址
            json.put("price", pla.getMinPrice());
            json.put("suplierDay", pla.getSuplierDay());
            /*判断销售平台是否已经存在商品库的商品*/
            SalesProduct salesProduct = salesProductManageService.getAdminProduct(pla.getId());
            json.put("platProductIdStatus", salesProduct != null ? 1 : 0);//门店里面还没有从商品库添加数据,有,1,没有,0
            for(String key : json.keySet()){
                if (json.get(key) == null) json.put(key,"");
            }
            jsonArray.add(json);
        }

        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, plaProductList.total), HttpStatus.OK);
    }


    /**
    * @Description:销售平台商品上下架
    * @Author: hanchao
    * @Date: 2017/11/17 0017
    */
    @RequestMapping(value = "/deleteSalesProductStatus", method = {RequestMethod.POST})
    public ResponseEntity deleteSalesProductStatus(@RequestBody StatusForm form) throws ResponseEntityException, UpsertException {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        salesProductManageService.updateSalesProductStatus(form.id,form.status);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
    * @Description:销售平台商品删除
    * @Author: hanchao
    * @Date: 2017/11/17 0017
    */
    @RequestMapping(value = "/deleteSalesProduct", method = {RequestMethod.POST})
    public ResponseEntity deleteSalesProduct(@RequestBody StatusForm form) throws ResponseEntityException, UpsertException {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        salesProductManageService.deleteSalesProduct(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:销售平台商品编辑详情保存
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @RequestMapping(value = "/updateSalesProductManage", method = {RequestMethod.POST})
    public ResponseEntity updateStoreProductManage(@RequestBody SalesProductManageForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        salesProductManageService.updateSalesProduct(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:销售平台商品编辑详情
     * @Author: hanchao
     * @Date: 2017/11/16 0016
     */
    @RequestMapping(value = "/salesProductManageEditDetail", method = {RequestMethod.POST})
    public ResponseEntity salesProductManageEditDetail(@RequestBody IdForm form) throws Exception {
        JSONArray jsonArray = new JSONArray();
        SalesProduct sal = salesProductManageService.getSalesProduct(form.id);
        if (sal == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("productCode",sal.getPlaProduct() == null ? "" : sal.getPlaProduct().getProductCode());
        json.put("name", sal.getName());
        json.put("platProductId",sal.getPlatProductId());
        json.put("vedioKey", sal.getVedioUrl());
        json.put("vedioUrl", (OSSClientUtil.getObjectUrl(sal.getVedioUrl())));
        json.put("pno",sal.getPlaProduct() == null ? "" : sal.getPlaProduct().getPno());
        json.put("remarks",sal.getRemarks());
        json.put("colligate",sal.getColligate());
        json.put("sales",sal.getSales());
        json.put("views",sal.getViews());

        //2017.12.07  图文详情隔离
//        json.put("description",sal.getDescription());
        json.put("description",salesProductManageService.getProductDescription(form.id));

        //二级分类
        json.put("secondCategoryId",sal.getPlaProductCategory() == null ? "" : sal.getPlaProductCategory().getId());
        json.put("secondCategoryName",sal.getPlaProductCategory() == null ? "" : sal.getPlaProductCategory().getName());

        //一级分类
        json.put("firstCategoryId",sal.getPlaProductCategory() == null ? "" : sal.getPlaProductCategory().getPlaProductCategory() == null ? "" : sal.getPlaProductCategory().getPlaProductCategory().getId());
        json.put("firstCategoryName",sal.getPlaProductCategory() == null ? "" : sal.getPlaProductCategory().getPlaProductCategory() == null ? "" : sal.getPlaProductCategory().getPlaProductCategory().getName());
        json.put("categoryId",sal.getPlaProductCategory() == null ? "" : sal.getPlaProductCategory().getId());
        json.put("brand",sal.getBrand());
        json.put("status",sal.getStatus());

        /*图片*/
        JSONArray picture=new JSONArray();
        for (SalesProductPicture salesProductPicture : sal.getSalesProductPictureList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key",salesProductPicture.getHref());
            jsonObject.put("href",OSSClientUtil.getObjectUrl(salesProductPicture.getHref()));
            picture.add(jsonObject);
        }
        json.put("picture",picture);

        /*商品标签*/
        JSONArray jsonLabel = new JSONArray();
        for (SalesProductLabel salesProductLabel : sal.getSalesProductLabelList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("labelId",salesProductLabel.getLabelId());
            jsonObject.put("labelName",salesProductLabel.getLabelNmae());
            jsonLabel.add(jsonObject);
        }
        json.put("jsonLabel",jsonLabel);

        /*商品规格*/
        JSONArray jsonProduct=new JSONArray();
        for (SalesProductPrice salesProductPrice : sal.getSalesProductPriceList()){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("color", salesProductPrice.getColor());
            jsonObject.put("size", salesProductPrice.getSize());
            jsonObject.put("offlinePrice",salesProductPrice.getOfflinePrice());
            jsonObject.put("stock", salesProductPrice.getStock());
            jsonObject.put("specRemark",salesProductPrice.getRemarks());
            jsonProduct.add(jsonObject);
        }
        json.put("jsonSupplier",jsonProduct);
        //去除null值
        for (String key : json.keySet()){
            if (json.get(key) == null) json.put(key, "");
        }
        jsonArray.add(json);
        return new ResponseEntity(new RestResponseEntity(100,"成功",jsonArray), HttpStatus.OK);
    }
    /**
     * @Description:销售平台的商品列表
     * @Author: hanchao
     * @Date: 2017/11/16 0016
     */
    @RequestMapping(value = "/salesProductManageList", method = {RequestMethod.POST})
    public ResponseEntity productList(@RequestBody SalesProductManageListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SalesProduct> salesProductList = salesProductManageService.findSalesProductList(pageQuery, form.name, form.categoryId, form.productCode);
        JSONArray jsonArray = new JSONArray();
        for (SalesProduct salesProduct : salesProductList) {
            JSONObject json = new JSONObject();
            json.put("id",salesProduct.getId());
            json.put("productCode",salesProduct.getPlaProduct() == null ? "" : salesProduct.getPlaProduct().getProductCode());
            json.put("name", salesProduct.getName());
            json.put("categoryName",salesProduct.getPlaProductCategory() == null ? "" : salesProduct.getPlaProductCategory().getName());
            json.put("mainpicHref",OSSClientUtil.getObjectUrl(salesProduct.getHref()));
            json.put("pno", salesProduct.getPlaProduct() == null ? "" : salesProduct.getPlaProduct().getPno());
            json.put("updateDate", DateTimeUtils.parseStr(salesProduct.getUpdateDate()));
            json.put("status",salesProduct.getStatus());
            json.put("brand", salesProduct.getBrand());

            Double offlinePrice_min = 0.0;
            Double offlinePrice_max = 0.0;
            Set<String> colors = new HashSet<>();
            Set<String> sizes = new HashSet<>();
            for (SalesProductPrice price : salesProduct.getSalesProductPriceList()) {
                colors.add(price.getColor());
                sizes.add(price.getSize());
                if (offlinePrice_min == 0.0 || offlinePrice_min > (price.getOfflinePrice() == null ? 0 : price.getOfflinePrice())) offlinePrice_min = price.getOfflinePrice();
                if (offlinePrice_max == 0.0 || offlinePrice_max < (price.getOfflinePrice() == null ? 0 : price.getOfflinePrice())) offlinePrice_max = price.getOfflinePrice();
            }
            json.put("color", colors.size() == 0 ? "" : colors.toString().substring(1, colors.toString().length() - 1));
            json.put("size", sizes.size() == 0 ? "" : sizes.toString().substring(1, sizes.toString().length() - 1));
            json.put("offlinePrice", offlinePrice_min + "-" + offlinePrice_max);
            json.put("remark", salesProduct.getRemarks());
            //去除null值
            for (String key : json.keySet()){
                if (json.get(key) == null) json.put(key, "");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, salesProductList.total), HttpStatus.OK);
    }



}
