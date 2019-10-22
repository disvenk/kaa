package com.xxx.store.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.StoProduct;
import com.xxx.model.business.StoProductPrice;
import com.xxx.model.business.StoShopcart;
import com.xxx.store.form.IdForm;
import com.xxx.store.form.SubmitOrderForm;
import com.xxx.store.service.SalesProductOrderService;
import com.xxx.store.service.ShoppingCartService;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.OSSClientUtil;
import com.xxx.store.form.SalesOrderForm;
import com.xxx.store.form.ShopcartAddForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/salesProductOrder")
public class SalesProductOrderController {

    @Autowired
    private SalesProductOrderService salesProductOrderService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * @Description: 门店商品下单
     * @Author: Chen.zm
     * @Date: 2017/10/30 0030
     */
    @RequestMapping(value = "/salesOrderOne", method = {RequestMethod.POST})
    public ResponseEntity salesOrderOne(@RequestBody ShopcartAddForm form) throws Exception {
        if (form.pid == 0)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        if (form.num == 0)
            return new ResponseEntity(new RestResponseEntity(130, "商品数量不能为空", null), HttpStatus.OK);
        StoProduct product = salesProductOrderService.getStoreProduct(form.pid);
        StoProductPrice productPrice = shoppingCartService.getStoProductPrice(form.pid, form.color, form.size);
        if (productPrice == null)
            return new ResponseEntity(new RestResponseEntity(140, "商品信息不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("name", product.getName());
        json.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
        json.put("price", productPrice.getOfflinePrice());
        json.put("pno", product.getPlaProduct() == null ? "" : product.getPlaProduct().getPno());
        json.put("categoryName", product.getPlaProductCategory() == null ? "" : product.getPlaProductCategory().getName());
        json.put("color", form.color);
        json.put("size", form.size);
        json.put("pid", form.pid);
        json.put("num", form.num);
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 购物车商品结算
     * @Author: Chen.zm
     * @Date: 2017/10/30 0030
     */
    @RequestMapping(value = "/salesOrder", method = {RequestMethod.POST})
    public ResponseEntity shopcartAdd(@RequestBody SalesOrderForm form) throws Exception {
        if (form.shopcartIds.size() == 0)
            return new ResponseEntity(new RestResponseEntity(110, "购物车商品不能为空", null), HttpStatus.OK);
        List<Integer> ids = new ArrayList<>();
        for (IdForm id : form.shopcartIds) {
            ids.add(id.id);
        }
        List<StoShopcart> shopcarts = salesProductOrderService.findShopcartList(CurrentUser.get().getStoreId(), ids);
        JSONArray data = new JSONArray();
        for (StoShopcart shopcart : shopcarts) {
            JSONObject json = new JSONObject();
            if (shopcart.getStoProduct() == null) continue;
            json.put("id", shopcart.getId());
            json.put("name", shopcart.getStoProduct().getName());
            json.put("href", OSSClientUtil.getObjectUrl(shopcart.getStoProduct().getHref()));
            json.put("price", shopcart.getPrice());
            json.put("num", shopcart.getQty());
            json.put("total", shopcart.getSubtotal());
            json.put("code", shopcart.getStoProduct().getPlaProduct() == null ? "" : shopcart.getStoProduct().getPlaProduct().getProductCode());
            json.put("categoryName", shopcart.getStoProduct().getPlaProductCategory() == null ? "" : shopcart.getStoProduct().getPlaProductCategory().getName());
            json.put("color", shopcart.getColor());
            json.put("size", shopcart.getSize());
            json.put("pid", shopcart.getPid());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 提交门店商品销售订单
     * @Author: Chen.zm
     * @Date: 2017/10/30 0030
     */
    @RequestMapping(value = "/submitOrder", method = {RequestMethod.POST})
    public ResponseEntity submitOrder(@RequestBody SubmitOrderForm form) throws Exception {
        if (form.orderDetail.size() == 0)
            return new ResponseEntity(new RestResponseEntity(110, "订单商品不能为空", null), HttpStatus.OK);
        salesProductOrderService.saveStoSalesOrder(form, CurrentUser.get().getStoreId());
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

}
