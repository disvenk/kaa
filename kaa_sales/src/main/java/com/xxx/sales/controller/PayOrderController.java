package com.xxx.sales.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.cache.RedisUtils;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.sales.form.*;
import com.xxx.sales.service.StoreSuplierOrderService;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.service.PaymentService;
import com.xxx.user.utils.GenerateNumberUtil;
import com.xxx.utils.Arith;
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


@Controller
@RequestMapping("/payOrder")
public class PayOrderController {

    @Autowired
    private PaymentService paymentService;

    private static String KEY_PREFIX = "PayReturn";

    /**
     * 支付回调返回页面,在支付前就要传入，并且传来传去
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/payReturnHtml", method = {RequestMethod.GET})
    public String payReturnHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id") == null ? "" : request.getParameter("id");
        String type = request.getParameter("type") == null ? "1" : request.getParameter("type");
        String boxIds = request.getParameter("boxIds") == null ? "" : request.getParameter("boxIds");
        //判断跳转路径
        String returnUrl = "../salesHome/indexHtml#menu/second-menu";
        if (Integer.parseInt(type) == 2) {
            returnUrl = "../salesHome/boxHtml";
            if (StringUtils.isNotBlank(boxIds)) {
                returnUrl = "../salesHome/orderConfirmHtml?boxIds=" + boxIds;
            }
        }
        modelMap.put("type", type);
        modelMap.put("returnUrl", returnUrl);

//        modelMap.put("sendVerify", false);
//        //判断是否发送短信
//        if (RedisUtils.get(KEY_PREFIX + id) == null) {
//            RedisUtils.setex(KEY_PREFIX + id, 300 , "");
//            modelMap.put("sendVerify", true);
//        }
        return "pay/payReturn";
    }


    /**
     * @Description: 在buyOrderBuy.js中调用支付接口请求到此方法，支付页跳转，此页面为过渡页面
     * @Author: Chen.zm
     * @Date: 2017/11/7 0007
     */
    @RequestMapping(value = "/buyOrderPayHtml", method = {RequestMethod.GET})
    public String buyOrderPayHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        String type = request.getParameter("type") == null ? "1" : request.getParameter("type");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(type)) {
//            modelMap.put("message", "数据异常");
//            return "pay/payWay";
            throw new ResponseEntityException(210, "支付数据异常");
        }
        buyOrderPayCheck(Integer.parseInt(id), Integer.parseInt(type), modelMap);
        modelMap.put("id", Integer.parseInt(id));
        modelMap.put("type", Integer.parseInt(type));
        modelMap.put("suc", request.getParameter("suc") == null ? "" : request.getParameter("suc"));
        //使用了modelMap将参数返回时，前端的url中可以带有这些参数
        return "pay/payWay";
    }

    /**
     * @Description: 支付前校验
     * @Author: Chen.zm
     * @Date: 2017/12/23 0023
     */
    public void buyOrderPayCheck(Integer id, Integer type, ModelMap modelMap) throws Exception {
        if (type == 1) {
            StoSuplierOrder suplierOrder = paymentService.get2(StoSuplierOrder.class, id);
            if (suplierOrder == null) {
                throw new ResponseEntityException(210, "支付数据异常");
            }
            modelMap.put("orderNo", suplierOrder.getOrderNo());
            modelMap.put("total", suplierOrder.getTotal());
            modelMap.put("body", "合一智造订单-" + suplierOrder.getOrderNo());
            modelMap.put("subject", "合一智造订单-" + suplierOrder.getOrderNo());
        } else if (type == 2) {
            BoxPayOrder boxPayOrder = paymentService.get2(BoxPayOrder.class, id);
            if (boxPayOrder == null) {
                throw new ResponseEntityException(210, "支付数据异常");
            }
            modelMap.put("orderNo", boxPayOrder.getOrderNo());
            modelMap.put("total", boxPayOrder.getTotal());

            BoxType boxType = paymentService.get2(BoxType.class, boxPayOrder.getBoxTypeId());
            String boxName = boxType == null ? "" : boxType.getName();
            String mobile = boxPayOrder.getStoStoreInfo() == null ? "" : boxPayOrder.getStoStoreInfo().getPubUserLogin() == null ? "" :
                    boxPayOrder.getStoStoreInfo().getPubUserLogin().getPubUserBase() == null ? "" :
                            boxPayOrder.getStoStoreInfo().getPubUserLogin().getPubUserBase().getMobile();
            modelMap.put("body", "合一盒子" + boxName + "-" + mobile);
            modelMap.put("subject", "合一盒子" + boxName + "-" + mobile);
        }

    }

    /**
     * @Description: 二维码支付页
     * @Author: Chen.zm
     * @Date: 2017/12/16 0016
     */
    @RequestMapping(value = "/buyOrderPayWayHtml", method = {RequestMethod.GET})
    public String buyOrderPayWayHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        String type = request.getParameter("type") == null ? "1" : request.getParameter("type");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(type)) {
//            modelMap.put("message", "数据异常");
//            return "pay/payWay";
            throw new ResponseEntityException(210, "支付数据异常");
        }
        buyOrderPayCheck(Integer.parseInt(id), Integer.parseInt(type), modelMap);
        modelMap.put("id", Integer.parseInt(id));
        modelMap.put("type", Integer.parseInt(type));
        modelMap.put("suc", request.getParameter("suc") == null ? "" : request.getParameter("suc"));
        modelMap.put("channel", request.getParameter("channel") == null ? "" : request.getParameter("channel"));

        modelMap.put("returnUrl", request.getParameter("returnUrl"));
        return "pay/buyPay";
    }


    /**
     * @Description: 获取采购订单状态 （前端页面定时器  用于获取支付成功回调）
     * @Author: Chen.zm
     * @Date: 2017/11/7 0007
     */
    @RequestMapping(value = "/findPayOrderStatus", method = {RequestMethod.POST})
    public ResponseEntity findStoreSuplierOrderStatus(@RequestBody PayOrderStatusForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        JSONObject data = new JSONObject();
        data.put("statusCheck", false);
        if (form.type == 1) {
            StoSuplierOrder suplierOrder = paymentService.get2(StoSuplierOrder.class, form.id);
            if (suplierOrder == null) {
                return new ResponseEntity(new RestResponseEntity(120, "订单不存在", null), HttpStatus.OK);
            }
            if (suplierOrder.getStatus() == 1) {
                data.put("statusCheck", true);
            }
        } else if (form.type == 2) {
            BoxPayOrder boxPayOrder = paymentService.get2(BoxPayOrder.class, form.id);
            if (boxPayOrder == null) {
                return new ResponseEntity(new RestResponseEntity(120, "订单不存在", null), HttpStatus.OK);
            }
            if (boxPayOrder.getStatus() == 1) {
                data.put("statusCheck", true);
            }
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


}
