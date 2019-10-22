package com.xxx.sales.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.sales.form.StoreJoinForm;
import com.xxx.sales.service.UserCenterService;
import com.xxx.sales.form.UserCenterSupplierOrderQueryForm;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.model.business.StoStoreInfo;
import com.xxx.model.business.StoStorePicture;
import com.xxx.model.business.StoSuplierOrder;
import com.xxx.model.business.StoSuplierOrderDetail;
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
@RequestMapping("/userCenter")
public class UserCenterController {

    @Autowired
    private UserCenterService userCenterService;

    @RequestMapping(value = "/vipCenterHtml", method = {RequestMethod.GET})
    public String vipCenterHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "vipCenter/vipCenter";
    }

    @RequestMapping(value = "/myInfoHtml", method = {RequestMethod.GET})
    public String myInfoHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "vipCenter/myInfo";
    }

    @RequestMapping(value = "/myAddressHtml", method = {RequestMethod.GET})
    public String myAddressHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "vipCenter/myAddress";
    }

    @RequestMapping(value = "/boxHtml", method = {RequestMethod.GET})
    public String boxHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "vipCenter/heyiBox";
    }


    /**
     * @Description: 门店入驻
     * @Author: Chen.zm
     * @Date: 2017/11/20 0020
     */
    @RequestMapping(value = "/storeJoin", method = {RequestMethod.POST})
    public ResponseEntity submitOrderSales(@RequestBody StoreJoinForm form) throws Exception {
        if (StringUtils.isBlank(form.name))
            return new ResponseEntity(new RestResponseEntity(110, "门店名称不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.contact))
            return new ResponseEntity(new RestResponseEntity(120, "联系人不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.contactPhone))
            return new ResponseEntity(new RestResponseEntity(130, "联系电话不能为空", null), HttpStatus.OK);
        userCenterService.saveStoreInfo(form, CurrentUser.get().getStoreId());
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 门店信息
     * @Author: Chen.zm
     * @Date: 2017/11/16 0016
     */
    @RequestMapping(value = "/storeInfo", method = {RequestMethod.POST})
    public ResponseEntity storeInfo() throws Exception {
        StoStoreInfo storeInfo = userCenterService.storeInfo(CurrentUser.get().getStoreId());
        JSONObject json = new JSONObject();
        json.put("storeName", storeInfo.getStoreName());
        json.put("address", storeInfo.getAddress());
        json.put("province", storeInfo.getProvince());
        json.put("provinceName", storeInfo.getProvinceName());
        json.put("city", storeInfo.getCity());
        json.put("cityName", storeInfo.getCityName());
        json.put("zone", storeInfo.getZone());
        json.put("zoneName", storeInfo.getZoneName());
        json.put("contact", storeInfo.getContact());
        json.put("contactPhone", storeInfo.getContactPhone());
        json.put("scope", storeInfo.getScope());
        json.put("credentials", OSSClientUtil.getObjectUrl(storeInfo.getCredentials()));
        JSONArray pictureList = new JSONArray();
        for (StoStorePicture picture : storeInfo.getStoStorePictureList()) {
            JSONObject js = new JSONObject();
            js.put("id", picture.getId());
            js.put("href", OSSClientUtil.getObjectUrl(picture.getHref()));
            pictureList.add(js);
        }
        json.put("pictureList", pictureList);
//        JSONArray categoryList = new JSONArray();
//        for (StoSalesCategory category : storeInfo.getStoSalesCategoryList()) {
//            JSONObject js = new JSONObject();
//            js.put("categoryId", category.getCategoryId());
//            js.put("categoryName", category.getPlaProductCategory() == null ? "" : category.getPlaProductCategory().getName());
//            categoryList.add(js);
//        }
//        json.put("categoryList", categoryList);
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }


    /**
     * @Description: 用户中心：取得正在进行中的采购订单
     * @Author: Steven.Xiao
     * @Date: 2017/11/16
     */
    @RequestMapping(value = "/findSuplierOrderList", method = {RequestMethod.POST})
    public ResponseEntity findSuplierOrderList(@RequestBody UserCenterSupplierOrderQueryForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        Integer storeId = CurrentUser.get().getStoreId();
        /*分页*/
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<StoSuplierOrder> orderList = userCenterService.findSupplierOrderList(pageQuery, storeId);
        JSONArray jsonArray = new JSONArray();
        for (StoSuplierOrder order : orderList) {
            JSONObject json = new JSONObject();
            /*订单ID*/
            json.put("orderId", order.getId());
            /* 订单号*/
            json.put("orderNo", order.getOrderNo());
            /* 订单日期*/
            json.put("orderDate", DateTimeUtils.parseStr(order.getOrderDate()));
            //销售订单状态
            json.put("orderStatus", Commo.parseSalesOrderStatus(order.getStatus()));

            /*订单明细*/
            JSONArray JsonDetailList = new JSONArray();
            for (StoSuplierOrderDetail detail : order.getStoSuplierOrderDetailList()) {
                JSONObject detailjson = new JSONObject();
                /*商品ID*/
                detailjson.put("productId", detail.getPid());
                /* 商品主图*/
                detailjson.put("productPicture", OSSClientUtil.getObjectUrl(detail.getHref()));
                /*商品名称*/
                detailjson.put("productName", detail.getProductName());
                //颜色
                detailjson.put("color", detail.getColor());
                //尺寸
                detailjson.put("size", detail.getSize());
                /*单价*/
                detailjson.put("price", detail.getPrice());
                /*数量*/
                detailjson.put("qty", detail.getQty());

                JsonDetailList.add(detailjson);
            }
            /*明细Json数据加入主订单Json中返回*/
            json.put("detailList", JsonDetailList);
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, orderList.total), HttpStatus.OK);
    }
}

