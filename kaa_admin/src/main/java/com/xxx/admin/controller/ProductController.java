package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.IdForm;
import com.xxx.admin.form.ProductIsAddForm;
import com.xxx.admin.form.ProductListForm;
import com.xxx.admin.service.ProductService;
import com.xxx.admin.service.SalesProductManageService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.admin.form.ProductForm;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SalesProductManageService salesProductManageService;

    //跳转到平台首页页面
    @RequestMapping(value = "/productHtml", method = {RequestMethod.GET})
    public String data_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/index";
    }


    @RequestMapping(value = "/goodsManageHtml", method = {RequestMethod.GET})
    public String data1_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/goodsManage/goodsManage";
    }

    @RequestMapping(value = "/goodsManageAddHtml", method = {RequestMethod.GET})
    public String data2_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "/goodsManage/goodsManageAdd";
    }

    @RequestMapping(value = "/goodsManageDetailHtml", method = {RequestMethod.GET})
    public String data3_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "/goodsManage/goodsManageDetail";
    }

    @RequestMapping(value = "/showGoodsManageDetailHtml", method = {RequestMethod.GET})
    public String data4_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "/showGoods/goodsManageDetail";
    }

    @RequestMapping(value = "/goodsManageEditHtml", method = {RequestMethod.GET})
    public String data5_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "/goodsManage/goodsManageEdit";
    }

    /**
     * @Description:获取供应商名称
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/suplierNameList", method = {RequestMethod.POST})
    public ResponseEntity SuplierNameList() throws Exception {
        List<SupSuplier> SuplierNameList = productService.findSuplierNameList();
        JSONArray jsonArray = new JSONArray();
        for(SupSuplier supSuplier : SuplierNameList){
            JSONObject json = new JSONObject();
            json.put("id", supSuplier.getId());
            json.put("suplierName", supSuplier.getName());
            jsonArray.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",jsonArray), HttpStatus.OK);
    }
    
    /**
     * @Description:商品库新增商品
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @RequestMapping(value = "/insertProduct", method = {RequestMethod.POST})
    public ResponseEntity insertProduct(@RequestBody ProductForm form) throws Exception {
        productService.insertProduct(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:商品库删除商品
     * @Author: hanchao
     * @Date: 2017/10/27 0027
     */
    @RequestMapping(value = "/deleteProduct", method = {RequestMethod.POST})
    public ResponseEntity deleteProduct(@RequestBody IdForm form) throws UpsertException,ResponseEntityException {
        if (form.id ==null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        productService.updateProductId(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:商品编辑保存
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */

    @RequestMapping(value = "/updateProduct", method = {RequestMethod.POST})
    public ResponseEntity updateProduct(@RequestBody ProductForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
//        PlaProduct plaProduct = productService.selectPno(form.pno,form.id);
//        if (plaProduct != null)
//            throw new ResponseEntityException(120, "商品货号已存在");
        productService.updateProduct(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }


    /**
     * @Description:商品详情
     * @Author: hanchao
     * @Date: 2017/10/27 0026
     */
    @RequestMapping(value = "/productDetail", method = {RequestMethod.POST})
    public ResponseEntity productDetail(@RequestBody IdForm form) throws Exception {
        JSONArray jsonArray = new JSONArray();
        PlaProduct pla = productService.getPlaProduct(form.id);
        if (pla == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("productCode",pla.getProductCode());
        json.put("name", pla.getName());
        json.put("vedioUrl", OSSClientUtil.getObjectUrl(pla.getVedioUrl()));

        //2017.12.07 商品详情，单独取得
//        json.put("description",pla.getDescription());

//        json.put("pno",pla.getPno());
        json.put("price", pla.getPrice());//零售价
        json.put("isDesigner", pla.getDesigner());
        json.put("updateDate", DateTimeUtils.parseStr(pla.getUpdateDate()));
        json.put("remark",pla.getRemarks());
        json.put("colligate",pla.getColligate());
        json.put("sales",pla.getSales());
        json.put("views",pla.getViews());
        json.put("createTime", pla.getCreatedDate() == null ? "" : pla.getCreatedDate().getTime());

        /*判断销售平台是否已经存在商品库的商品(在销售平台的商品详情页会调用这个接口所以多加了这个判定)*/
        SalesProduct salesProduct = salesProductManageService.getAdminProduct(pla.getId());
        if (salesProduct != null) {
            json.put("platProductIdStatus", 1);//门店里面还没有从商品库添加数据,有,1,没有,0
        } else {
            json.put("platProductIdStatus", 0);
        }

        JSONArray picture=new JSONArray();
        for (PlaProductPicture plaProductPicture1:pla.getPlaProductPictureList()){
            JSONObject jsonObject = new JSONObject();
//          jsonObject.put("href",plaProductPicture1.getHref());
            jsonObject.put("href", OSSClientUtil.getObjectUrl(plaProductPicture1.getHref()));
            json.put("isMainpic",plaProductPicture1.getMainpic());
            picture.add(jsonObject);
        }
        json.put("picture",picture);
        JSONArray labelName = new JSONArray();
        for (PlaProductLabel plaProductLabel : pla.getPlaProductLabelList()){
            JSONObject labelJson = new JSONObject();
            labelJson.put("labelName",plaProductLabel.getLabelNmae());
            labelJson.put("labelId",plaProductLabel.getLabelId());
            labelName.add(labelJson);
        }
        json.put("labelName",labelName);
        json.put("categoryName",pla.getPlaProductCategory()==null?"":pla.getPlaProductCategory().getName());
        json.put("suplierDay",pla.getSuplierDay());
        json.put("material",pla.getMaterial());
        json.put("technics",pla.getTechnics());

        JSONArray jsonSupplier=new JSONArray();
        for (PlaProductPrice plaProductPrice: pla.getPlaProductPriceList()){
            JSONObject jsonObject=new JSONObject();
            json.put("brand",pla.getBrand());
//            json.put("productSupplierName", pla.getSupSuplier() == null ? "" : pla.getSupSuplier().getName());//产品供应商名称
//            json.put("suplierDay",pla.getSuplierDay());
            jsonObject.put("color", plaProductPrice.getColor());
            jsonObject.put("size", plaProductPrice.getSize());
            jsonObject.put("onlinePrice",plaProductPrice.getOnlinePrice() == null ? "" : plaProductPrice.getOnlinePrice());
            jsonObject.put("offlinePrice",plaProductPrice.getOfflinePrice() == null ? "" : plaProductPrice.getOfflinePrice());
            jsonObject.put("stock", plaProductPrice.getStock());
            jsonObject.put("categoryRemark",plaProductPrice.getRemarks());
            jsonSupplier.add(jsonObject);
        }

        JSONArray schannelName = new JSONArray();
        for (PlaProductSaleschannel plaProductSaleschannel: pla.getPlaProductSaleschannelList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("saleschannelName",plaProductSaleschannel.getSaleschannelName());
            jsonObject.put("saleschannelId",plaProductSaleschannel.getSaleschannelId());
            schannelName.add(jsonObject);
        }
        json.put("schannelName",schannelName);
        json.put("jsonSupplier",jsonSupplier);
        for(String key : json.keySet()){
            if(json.get(key) == null) json.put(key,"");
        }
        //2017.12.07 图文详情
        json.put("description",productService.getProductDescription(form.id));

        jsonArray.add(json);
        return new ResponseEntity(new RestResponseEntity(100,"成功",jsonArray), HttpStatus.OK);
    }


    /**
     * @Description:商品编辑详情
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @RequestMapping(value = "/productEditDetail", method = {RequestMethod.POST})
    public ResponseEntity productEditDetail(@RequestBody IdForm form) throws Exception {
        JSONArray jsonArray = new JSONArray();
        PlaProduct pla=productService.getPlaProduct(form.id);
        if (pla == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品不存在", null), HttpStatus.OK);

        JSONObject json = new JSONObject();
        json.put("productCode", pla.getProductCode());
        json.put("name", pla.getName());
        json.put("vedioKey", pla.getVedioUrl());
        json.put("vedioUrl", OSSClientUtil.getObjectUrl(pla.getVedioUrl()));

        //2017.12.07 图文详情字段 隔离
//        json.put("description",pla.getDescription());

        json.put("pno",pla.getPno());
        json.put("price", pla.getPrice());
        json.put("isDesigner", pla.getDesigner());
        json.put("updateDate", DateTimeUtils.parseStr(pla.getUpdateDate()));
        json.put("remark",pla.getRemarks());
        json.put("colligate",pla.getColligate());
        json.put("sales",pla.getSales());
        json.put("views",pla.getViews());
        json.put("brand",pla.getBrand());
//        json.put("productSupplierName",pla.getSupSuplier() == null ? "" : pla.getSupSuplier().getName());//产品供应商名称
        json.put("suplierDay",pla.getSuplierDay());
        json.put("material",pla.getMaterial());

        json.put("technics",pla.getTechnics());

//        json.put("createTime", pla.getCreatedDate() == null ? "" : pla.getCreatedDate().getTime());

        //二级分类
        json.put("secondCategoryId",pla.getPlaProductCategory() == null ? "" : pla.getPlaProductCategory().getId());
        json.put("secondCategoryName",pla.getPlaProductCategory() == null ? "" : pla.getPlaProductCategory().getName());

        //一级分类
        json.put("firstCategoryId",pla.getPlaProductCategory().getPlaProductCategory() == null ? "" : pla.getPlaProductCategory().getPlaProductCategory().getId());
        json.put("firstCategoryName",pla.getPlaProductCategory().getPlaProductCategory() == null ? "" : pla.getPlaProductCategory().getPlaProductCategory().getName());

        JSONArray picture=new JSONArray();
        for (PlaProductPicture plaProductPicture1:pla.getPlaProductPictureList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", plaProductPicture1.getHref());
            jsonObject.put("href",OSSClientUtil.getObjectUrl(plaProductPicture1.getHref()));
            jsonObject.put("isMainpic",plaProductPicture1.getMainpic());
            picture.add(jsonObject);
        }
        json.put("picture",picture);

        JSONArray labelName = new JSONArray();
        for (PlaProductLabel plaProductLabel : pla.getPlaProductLabelList()){
            JSONObject labelJson = new JSONObject();
            labelJson.put("labelName",plaProductLabel.getLabelNmae());
            labelJson.put("labelId",plaProductLabel.getLabelId());
            labelName.add(labelJson);
        }
        json.put("labelName",labelName);

        JSONArray jsonSupplier = new JSONArray();
        for (PlaProductPrice plaProductPrice: pla.getPlaProductPriceList()){
            JSONObject jsonObject=new JSONObject();
//            jsonObject.put("priceNormal",plaProductPrice.getPrice());//正常价格
            jsonObject.put("color", plaProductPrice.getColor());
            jsonObject.put("size", plaProductPrice.getSize());
            jsonObject.put("onlinePrice",plaProductPrice.getOnlinePrice() == null ? "" : plaProductPrice.getOnlinePrice());
            jsonObject.put("offlinePrice",plaProductPrice.getOfflinePrice() == null ? "" : plaProductPrice.getOfflinePrice());
            jsonObject.put("stock", plaProductPrice.getStock());
            jsonObject.put("categoryRemark",plaProductPrice.getRemarks());
            jsonSupplier.add(jsonObject);
        }
        JSONArray schannelName = new JSONArray();
        for (PlaProductSaleschannel plaProductSaleschannel: pla.getPlaProductSaleschannelList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("saleschannelName",plaProductSaleschannel.getSaleschannelName());
            jsonObject.put("saleschannelId",plaProductSaleschannel.getSaleschannelId());
            schannelName.add(jsonObject);
        }
        json.put("schannelName",schannelName);
        json.put("jsonSupplier",jsonSupplier);

        //2017.12.07 图文详情
//        json.put("description",productService.getProductDescription(form.id));

        jsonArray.add(json);
        return new ResponseEntity(new RestResponseEntity(100,"成功",jsonArray), HttpStatus.OK);
    }

    /**
     * @Description: 获取平台商品列表
     * @Author: Chen.zm
     * @Date: 2017/10/25 0025
     */
    @RequestMapping(value = "/productList", method = {RequestMethod.POST})
    public ResponseEntity productList(@RequestBody ProductListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<PlaProduct> productList = productService.findProductList(pageQuery, form.isMoren,form.name, form.categoryId,form.pno,form.designer);
        JSONArray jsonArray = new JSONArray();
        for (PlaProduct plaProduct : productList) {
            JSONObject json = new JSONObject();
            json.put("id",plaProduct.getId());
            //商品编号：
            json.put("productCode", plaProduct.getProductCode() == null ? "" : plaProduct.getProductCode());
            json.put("name", plaProduct.getName());
            json.put("suplierRemark",plaProduct.getRemarks());
            json.put("mainpicHref",OSSClientUtil.getObjectUrl(plaProduct.getHref()));//主图地址
//            json.put("pno", plaProduct.getPno());
//            json.put("suplierDay",plaProduct.getSuplierDay());
//            json.put("productSupplierName", plaProduct.getSupSuplier() == null ? "" : plaProduct.getSupSuplier().getName());//产品供应商名称
            json.put("isDesigner",plaProduct.getDesigner() == null  ? "" : plaProduct.getDesigner());
            json.put("brand", plaProduct.getBrand());
            json.put("remark", plaProduct.getRemarks() == null ? "" : plaProduct.getRemarks());
            json.put("isAdd", plaProduct.getAdd());
            json.put("categoryName",plaProduct.getPlaProductCategory() == null ? "" : plaProduct.getPlaProductCategory().getName());
            json.put("isMoren",plaProduct.getAdd());
            Double onlinePrice_min = 0.0;
            Double onlinePrice_max = 0.0;
            Double offlinePrice_min = 0.0;
            Double offlinePrice_max = 0.0;
            Set<String> colors = new HashSet<>();
            Set<String> sizes = new HashSet<>();
            for (PlaProductPrice price : plaProduct.getPlaProductPriceList()) {
                colors.add(price.getColor());
                sizes.add(price.getSize());
                if (onlinePrice_min == 0.0 || onlinePrice_min > (price.getOnlinePrice() == null ? 0 : price.getOnlinePrice())) onlinePrice_min = price.getOnlinePrice();
                if (onlinePrice_max == 0.0 || onlinePrice_max < (price.getOnlinePrice() == null ? 0 : price.getOnlinePrice())) onlinePrice_max = price.getOnlinePrice();
                if (offlinePrice_min == 0.0 || offlinePrice_min > (price.getOfflinePrice() == null ? 0 : price.getOfflinePrice())) offlinePrice_min = price.getOfflinePrice();
                if (offlinePrice_max == 0.0 || offlinePrice_max < (price.getOfflinePrice() == null ? 0 : price.getOfflinePrice())) offlinePrice_max = price.getOfflinePrice();
            }
            json.put("color", colors.size() == 0 ? "" : colors.toString().substring(1, colors.toString().length() - 1));
            json.put("size", sizes.size() == 0 ? "" : sizes.toString().substring(1, sizes.toString().length() - 1));
            json.put("onlinePrice", onlinePrice_min + "-" + onlinePrice_max);
            json.put("offlinePrice", offlinePrice_min + "-" + offlinePrice_max);
            json.put("updateDate", DateTimeUtils.parseStr(plaProduct.getUpdateDate()));
            StringBuffer saleschannelName = new StringBuffer();
            for(PlaProductSaleschannel saleschannelList : plaProduct.getPlaProductSaleschannelList()){
                saleschannelName.append(saleschannelList.getSaleschannelName() + ",");
            }
            json.put("saleschannelName", saleschannelName.length() == 0 ? "" : saleschannelName.substring(0, saleschannelName.length() - 1));
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, productList.total), HttpStatus.OK);
    }

    /**
     * @Description: 单独取得图文详情
     * @Author: steven
     * @Date: 2017/12/11
     */
    @RequestMapping(value = "/getProductDescription", method = {RequestMethod.POST})
    public ResponseEntity getProductDescription(@RequestBody IdForm form) throws Exception {
        JSONObject json = new JSONObject();
        json.put("description", productService.getProductDescription(form.id));
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 取得分类的供货周期
     * @Author: Steven.Xiao
     * @Date: 2017/12/12
     */
    @RequestMapping(value = "/getCategorySupplierDay", method = {RequestMethod.POST})
    public ResponseEntity getCategorySupplierDay(@RequestBody IdForm form) throws Exception {
        JSONObject json = new JSONObject();
        json.put("categorySupplierDay", productService.getCategorySupplierDay(form.id));
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 设置商品是否默认添加至门店
     * @Author: Chen.zm
     * @Date: 2017/12/21 0021
     */
    @RequestMapping(value = "/productIsAdd", method = {RequestMethod.POST})
    public ResponseEntity productIsAdd(@RequestBody ProductIsAddForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        productService.productIsAdd(form.id, form.isAdd);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 添加默认商品至门店
     * @Author: Chen.zm
     * @Date: 2017/12/21 0021
     */
    @RequestMapping(value = "/insertStoreProduct", method = {RequestMethod.POST})
    public ResponseEntity insertStoreProduct(@RequestBody ProductIsAddForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        productService.insertStoreProduct(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

}
