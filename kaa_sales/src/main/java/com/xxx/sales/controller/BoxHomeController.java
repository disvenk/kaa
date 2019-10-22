package com.xxx.sales.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.sales.form.*;
import com.xxx.sales.service.BoxHomeService;
import com.xxx.user.Commo;
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
import java.util.*;

/**
 * 合一盒子
 */
@Controller
@RequestMapping("/boxHome")
public class BoxHomeController {

    @Autowired
    private BoxHomeService boxHomeService;

    /**
     * @Description: 盒子商品列表
     *
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxProductList", method = {RequestMethod.POST})
    public ResponseEntity boxProductList(@RequestBody PageNumForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 8;
        PageList<BoxProduct> boxProducts = boxHomeService.boxProductList(pageQuery);
        JSONArray data = new JSONArray();
        for (BoxProduct boxProduct : boxProducts) {
            if (boxProduct.getPlaProduct() == null) continue;
            JSONObject json = new JSONObject();
            json.put("id", boxProduct.getId());
            json.put("name", boxProduct.getPlaProduct().getName());
            json.put("href", OSSClientUtil.getObjectUrl(boxProduct.getPlaProduct().getHref()));
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, boxProducts.total), HttpStatus.OK);
    }

    /**
     * @Description: 移动端：盒子商品列表
     * @Author: steven
     * @Date: 2017/12/24
     */
    @RequestMapping(value = "/mobileBoxProductList", method = {RequestMethod.POST})
    public ResponseEntity mobileBoxProductList(@RequestBody PageNumForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 100;
        PageList<BoxProduct> boxProducts = boxHomeService.boxProductList(pageQuery);
        JSONArray data = new JSONArray();
        for (BoxProduct boxProduct : boxProducts) {
            if (boxProduct.getPlaProduct() == null) continue;
            JSONObject json = new JSONObject();
            json.put("id", boxProduct.getId());
            json.put("name", boxProduct.getPlaProduct().getName());
            json.put("href", OSSClientUtil.getObjectUrl(boxProduct.getPlaProduct().getHref()));
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, boxProducts.total), HttpStatus.OK);
    }


    /**
     * @Description: 盒子商品详情
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxProductDetail", method = {RequestMethod.POST})
    public ResponseEntity boxProductDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "ID不能为空", null), HttpStatus.OK);
        BoxProduct boxProduct = boxHomeService.getBoxProduct(form.id);
        if (boxProduct == null || boxProduct.getPlaProduct() == null)
            return new ResponseEntity(new RestResponseEntity(120, "商品不存在", null), HttpStatus.OK);
        JSONObject data = new JSONObject();
        data.put("name", boxProduct.getPlaProduct().getName());
        data.put("href", OSSClientUtil.getObjectUrl(boxProduct.getPlaProduct().getHref()));
        data.put("productCode", boxProduct.getPlaProduct().getProductCode());
        data.put("price", boxProduct.getPlaProduct().getMaxPrice());
        Set<String> colors = new TreeSet<>();
        List<String> sizes = new ArrayList<>();
        for (PlaProductPrice price : boxProduct.getPlaProduct().getPlaProductPriceList()) {
            colors.add(price.getColor());
//            sizes.add(price.getSize());
        }
        //默认显示尺寸
        sizes.add("M");sizes.add("S");sizes.add("L");sizes.add("XL");
        data.put("colors", colors);
        data.put("sizes", sizes);
        data.put("description", boxProduct.getPlaProduct().getPlaProductDescription() == null ? "" :
                boxProduct.getPlaProduct().getPlaProductDescription().getDescription());
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


    /**
     * @Description: 盒子商品购买时详情
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxProductPayDetail", method = {RequestMethod.POST})
    public ResponseEntity boxProductPayDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "ID不能为空", null), HttpStatus.OK);
        BoxProduct boxProduct = boxHomeService.getBoxProduct(form.id);
        if (boxProduct == null || boxProduct.getPlaProduct() == null)
            return new ResponseEntity(new RestResponseEntity(120, "商品不存在", null), HttpStatus.OK);
        JSONObject data = new JSONObject();
        data.put("name", boxProduct.getPlaProduct().getName() == null ? "" : boxProduct.getPlaProduct().getName());
        data.put("href", boxProduct.getPlaProduct().getHref() == null ? "" : OSSClientUtil.getObjectUrl(boxProduct.getPlaProduct().getHref()));
        data.put("description", boxProduct.getDescription() == null ? "" : boxProduct.getDescription());
        data.put("price", boxProduct.getPlaProduct().getMaxPrice());
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 获取盒子类型列表
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxTypeList", method = {RequestMethod.POST})
    public ResponseEntity boxTypeList() throws Exception {
        List<BoxType> boxTypes = boxHomeService.boxTypeList();
        JSONObject json = new JSONObject();
        json.put("isOnePay", true);
        if (boxHomeService.getBoxCount() <= 0) {
            json.put("isOnePay", false);
            json.put("message", "限购数量已满");
        }
        if (CurrentUser.get() != null && boxHomeService.getBoxPayOrderOne(CurrentUser.get().getStoreId())) {
            json.put("isOnePay", false);
            json.put("message", "您已购买限购盒子");
        }
        BoxInfo boxInfo = CurrentUser.get() == null ? null : (BoxInfo)boxHomeService.get2(BoxInfo.class, "storeId", CurrentUser.get().getStoreId());
        json.put("needDeposit", boxInfo == null || boxInfo.getDeposit() == null || boxInfo.getDeposit() == 0 ? BoxHomeService.DEPOSIT : 0);
        JSONArray typeList = new JSONArray();
        for (BoxType boxType : boxTypes) {
            JSONObject js = new JSONObject();
            js.put("id", boxType.getId());
            js.put("name", boxType.getName());
            js.put("price", boxType.getPrice());
            typeList.add(js);
        }
        json.put("typeList", typeList);
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }


    /**
     * @Description: 获取盒子用户信息
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxInfo", method = {RequestMethod.POST})
    public ResponseEntity boxInfo() throws Exception {
        BoxInfo boxInfo = boxHomeService.get2(BoxInfo.class, "storeId", CurrentUser.get().getStoreId());
        if (boxInfo == null) boxInfo = new BoxInfo();
        JSONObject json = new JSONObject();
        json.put("id", boxInfo.getId());
        json.put("deposit", boxInfo.getDeposit());
        json.put("count", boxInfo.getCount());
        json.put("termTime", DateTimeUtils.parseStr(boxInfo.getTermTime(), "yyyy-MM-dd"));
        json.put("boxCheck", true);
        if (boxInfo.getCount() == null || boxInfo.getCount() <= 0 || boxInfo.getTermTime() == null || boxInfo.getTermTime().getTime() < new Date().getTime()) {
            json.put("boxCheck", false);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }


    /**
     * @Description:  获取限购盒子的剩余数量
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxCount", method = {RequestMethod.POST})
    public ResponseEntity boxCount() throws Exception {
        JSONObject json = new JSONObject();
        json.put("count", boxHomeService.getBoxCount());
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }


    /**
     * @Description: 盒子购买下单
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/saveBoxPayOrder", method = {RequestMethod.POST})
    public ResponseEntity saveBoxPayOrder(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "ID不能为空", null), HttpStatus.OK);
        BoxPayOrder boxPayOrder = boxHomeService.saveBoxPayOrder(CurrentUser.get().getStoreId(), form.id);
        JSONObject json = new JSONObject();
        json.put("orderId", boxPayOrder.getId());
        BoxType boxType = boxHomeService.get2(BoxType.class, boxPayOrder.getBoxTypeId());
        json.put("boxName", boxType == null ? "" : boxType.getName());
        StoStoreInfo info = boxHomeService.get2(StoStoreInfo.class, boxPayOrder.getStoreId());
        json.put("mobile", info.getPubUserLogin() == null ? "" : info.getPubUserLogin().getPubUserBase() == null ? "" :
                info.getPubUserLogin().getPubUserBase().getMobile());
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }


    /**
     * @Description: 盒子使用前校验
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxUseLogCheck", method = {RequestMethod.POST})
    public ResponseEntity boxUseLogCheck(@RequestBody IdsForm form) throws Exception {
        if (form.ids.size() == 0)
            return new ResponseEntity(new RestResponseEntity(110, "商品信息不能为空", null), HttpStatus.OK);
        boxHomeService.boxUseLogCheck(CurrentUser.get().getStoreId());
        JSONArray data = new JSONArray();
        for (IdForm f : form.ids) {
            BoxProduct boxProduct = boxHomeService.get2(BoxProduct.class, f.id);
            if (boxProduct == null || boxProduct.getPlaProduct() == null)
                return new ResponseEntity(new RestResponseEntity(120, "商品信息不存在", null), HttpStatus.OK);
            JSONObject json = new JSONObject();
            json.put("id", boxProduct.getId());
            json.put("name", boxProduct.getPlaProduct().getName());
            json.put("href", OSSClientUtil.getObjectUrl(boxProduct.getPlaProduct().getHref()));
            json.put("price", boxProduct.getPlaProduct().getMaxPrice());
            json.put("count", 1);
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


    /**
     * @Description: 盒子确认使用
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxUseLogSave", method = {RequestMethod.POST})
    public ResponseEntity boxUseLogSave(@RequestBody BoxUseLogForm form) throws Exception {
        if (form.ids.size() == 0)
            return new ResponseEntity(new RestResponseEntity(110, "商品信息不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.receiver))
            return new ResponseEntity(new RestResponseEntity(120, "联系人不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.mobile))
            return new ResponseEntity(new RestResponseEntity(130, "联系电话不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.address))
            return new ResponseEntity(new RestResponseEntity(140, "详细地址不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.provinceName) || StringUtils.isBlank(form.cityName) || StringUtils.isBlank(form.zoneName))
            return new ResponseEntity(new RestResponseEntity(150, "所在地区不能为空", null), HttpStatus.OK);
        boxHomeService.saveBoxUseLog(CurrentUser.get().getStoreId(), form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 盒子使用记录列表
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxUseLogList", method = {RequestMethod.POST})
    public ResponseEntity boxUseLogList(@RequestBody PageNumForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 99;
        PageList<BoxUseLog> boxUseLogs = boxHomeService.boxUseLogList(CurrentUser.get().getStoreId(), pageQuery);
        JSONArray data = new JSONArray();
        for (BoxUseLog boxUseLog : boxUseLogs) {
            JSONObject json = new JSONObject();
            json.put("id", boxUseLog.getId());
            json.put("orderNo", boxUseLog.getOrderNo());
            json.put("status", boxUseLog.getStatus());
            json.put("statusName", Commo.parseBoxUseLogStatus(boxUseLog.getStatus()));
            json.put("createTime", DateTimeUtils.parseStr(boxUseLog.getCreatedDate()));
            if (boxUseLog.getStatus() == 1 && boxUseLog.getIssueDeliveryCompany() != null) {
                PlaProductBase base = boxHomeService.get2(PlaProductBase.class, boxUseLog.getIssueDeliveryCompany());
                json.put("deliveryCom", base == null ? "" : base.getDescription());
                json.put("deliveryNo", boxUseLog.getIssueDeliveryNo());
                json.put("deliveryCompany", boxUseLog.getIssueDeliveryCompany());
                json.put("deliveryCompanyName", boxUseLog.getIssueDeliveryCompanyName());
            } else if (boxUseLog.getStatus() == 2 && boxUseLog.getReturnDeliveryCompany() != null) {
                PlaProductBase base = boxHomeService.get2(PlaProductBase.class, boxUseLog.getReturnDeliveryCompany());
                json.put("deliveryCom", base == null ? "" : base.getDescription());
                json.put("deliveryNo", boxUseLog.getReturnDeliveryNo());
                json.put("deliveryCompany", boxUseLog.getReturnDeliveryCompany());
                json.put("deliveryCompanyName", boxUseLog.getIssueDeliveryCompanyName());
            }
            JSONArray productList = new JSONArray();
            for (BoxUseLogProduct product : boxUseLog.getBoxUseLogProductList()) {
                JSONObject js = new JSONObject();
                js.put("id", product.getId());
                js.put("name", product.getName());
                js.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
                js.put("price", product.getPrice());
                js.put("count", product.getCount());
                js.put("status", product.getStatus());
                js.put("statusName", Commo.parseBoxUseLogProductStatus(product.getStatus()));
                productList.add(js);
            }
            json.put("productList", productList);
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, boxUseLogs.total), HttpStatus.OK);
    }


    /**
     * @Description: 盒子使用记录退回
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxUseLogReturn", method = {RequestMethod.POST})
    public ResponseEntity boxUseLogReturn(@RequestBody BoxUseLogReturnForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "ID不能为空", null), HttpStatus.OK);
        if (form.deliveryNo == null)
            return new ResponseEntity(new RestResponseEntity(120, "快递单号不能为空", null), HttpStatus.OK);
        if (form.deliverCompanyId == null || form.deliveryCompanyName == null)
            return new ResponseEntity(new RestResponseEntity(130, "快递公司不能为空", null), HttpStatus.OK);
        boxHomeService.updateBoxUseLogReturn(CurrentUser.get().getStoreId(), form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description:判断盒子商品是否还有库存
     * @Author: hanchao
     * @Date: 2017/12/29 0029
     */
    @RequestMapping(value = "/boxOrderCount", method = {RequestMethod.POST})
    public ResponseEntity boxOrderCount(@RequestBody IdForm form) throws Exception {
        BoxProduct box = boxHomeService.get2(BoxProduct.class,form.id);
        if(box == null){
            return new ResponseEntity(new RestResponseEntity(110, "该商品不存在", null), HttpStatus.OK);
        }
        //订购数量
        JSONObject js = boxHomeService.findOrderCount(box.getId());
        if(Integer.parseInt((js == null || js.equals("") ? 0 : js.get("orderCount")).toString()) >= box.getStock()){
            return new ResponseEntity(new RestResponseEntity(120, "抱歉，该商品已抢购完", null), HttpStatus.OK);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }
}
