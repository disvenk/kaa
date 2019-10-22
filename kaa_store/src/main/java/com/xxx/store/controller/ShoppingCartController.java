package com.xxx.store.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.store.form.IdForm;
import com.xxx.store.form.ShopcartAddForm;
import com.xxx.store.form.UpdateNumForm;
import com.xxx.store.service.ShoppingCartService;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.OSSClientUtil;
import com.xxx.model.business.StoShopcart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@Controller
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * @Description: 加入购物车
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @RequestMapping(value = "/shopcartAdd", method = {RequestMethod.POST})
    public ResponseEntity shopcartAdd(@RequestBody ShopcartAddForm form) throws Exception {
        if (form.pid == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        if (form.num == null || form.num <= 0)
            return new ResponseEntity(new RestResponseEntity(130, "数量不能为空", null), HttpStatus.OK);
        shoppingCartService.saveShopcartProduct(CurrentUser.get().getStoreId(), form.pid, form.num ,form.color, form.size);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 购物车列表
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @RequestMapping(value = "/shopcartList", method = {RequestMethod.POST})
    public ResponseEntity shopcartList() throws Exception {
        List<StoShopcart> shopcarts = shoppingCartService.findShopcartList(CurrentUser.get().getStoreId());
        JSONArray data = new JSONArray();
        for (StoShopcart shopcart : shopcarts) {
            JSONObject json = new JSONObject();
            json.put("id", shopcart.getId());
            json.put("imgUrl", shopcart.getStoProduct() == null ? "" : OSSClientUtil.getObjectUrl(shopcart.getStoProduct().getHref()));
            json.put("name", shopcart.getStoProduct() == null ? "" : shopcart.getStoProduct().getName());
            json.put("color", shopcart.getColor());
            json.put("size", shopcart.getSize());
            json.put("price", shopcart.getPrice());
            json.put("number", shopcart.getQty());
            json.put("total", shopcart.getSubtotal());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
    }


    /**
     * @Description: 删除购物车内商品
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @RequestMapping(value = "/shopcartRemove", method = {RequestMethod.POST})
    public ResponseEntity shopcartRemove(@RequestBody IdForm form) throws Exception {
        shoppingCartService.removeShopcart(CurrentUser.get().getStoreId(), form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 修改购物车内商品的数量
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @RequestMapping(value = "/shopcartUpdateNum", method = {RequestMethod.POST})
    public ResponseEntity shopcartUpdateNum(@RequestBody UpdateNumForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "购物车id不能为空", null), HttpStatus.OK);
        if (form.num == null || form.num <= 0)
            return new ResponseEntity(new RestResponseEntity(120, "商品数量不能为空", null), HttpStatus.OK);
        shoppingCartService.updateShopcartNum(CurrentUser.get().getStoreId(), form.id, form.num);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

}
