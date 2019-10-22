package com.xxx.suplier.controller;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.config.Configs;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.suplier.form.DateStrEndForm;
import com.xxx.suplier.form.JoinForm;
import com.xxx.suplier.form.TypeForm;
import com.xxx.suplier.service.SuplierHomeService;
import com.xxx.suplier.service.WeChatService;
import com.xxx.user.form.IconForm;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.utils.SessionUtils;
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
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * @Description: 供应商手机端入口
 * @Author: Chen.zm
 * @Date: 2018/1/13 0013
 */
@Controller
@RequestMapping("/suplierHome")
public class SuplierHomeController {

    @Autowired
    private SuplierHomeService suplierService;
    @Autowired
    private WeChatService weChatService;


    @RequestMapping(value = "/getshareinfo", method = {RequestMethod.GET})
    public ResponseEntity getShareInfo(HttpServletRequest request) throws Exception {
        Map map = weChatService.gettick(request);
        return new ResponseEntity(new RestResponseEntity(100, "成功", map), HttpStatus.OK);
    }


    /**
     * 手机端首页
     * @Date: 2018/1/13 0013
     */
    @RequestMapping(value = "/workerHomeHtml", method = {RequestMethod.GET})
    public String workerHomeHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        modelMap.put("token", SessionUtils.getSession(request, SessionUtils.TOKEN) == null ? "" : SessionUtils.getSession(request, SessionUtils.TOKEN));
        return "mobile/workerCode/home";
    }

    /**
     * 扫码后 领取工单页
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/getOrderHtml", method = {RequestMethod.GET})
    public String getOrderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        modelMap.put("token", SessionUtils.getSession(request, SessionUtils.TOKEN) == null ? "" : SessionUtils.getSession(request, SessionUtils.TOKEN));
        return "mobile/workerCode/getOrder";
    }

    /**
     * 登录页面
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/loginHtml", method = {RequestMethod.GET})
    public String log_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/login";
    }

    @RequestMapping(value = "/workerDetailHtml", method = {RequestMethod.GET})
    public String workerDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/workerCode/workerDetail";
    }

    @RequestMapping(value = "/getSuccessHtml", method = {RequestMethod.GET})
    public String getSuccessHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/workerCode/getSuccess";
    }

    /**
     * @Description: 供应商首页  【旧版
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/homePageHtml", method = {RequestMethod.GET})
    public String homePageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/homePage";
    }

    /**
     * @Description: 获取供应商首页数据
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/homePageInfo", method = {RequestMethod.POST})
    public ResponseEntity supOrderDeliveryLogList() throws Exception {
        JSONObject json = suplierService.findHomeInfo(CurrentUser.get().getSuplierId());
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description: 获取供应商首页订单统计数据
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/orderCount", method = {RequestMethod.POST})
    public ResponseEntity orderCount(@RequestBody TypeForm form) throws Exception {
        if (form.type == null)
            return new ResponseEntity(new RestResponseEntity(110,"类型不能为空",null), HttpStatus.OK);
        JSONObject json = suplierService.orderCount(CurrentUser.get().getSuplierId(), form.type);
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description: 订单列表
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/orderHtml", method = {RequestMethod.GET})
    public String orderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/order";
    }

    /**
     * @Description: 商品列表
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/goodsHtml", method = {RequestMethod.GET})
    public String goodsHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/goods";
    }

    /**
     * @Description: 供应商入驻
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/supplierShowHtml", method = {RequestMethod.GET})
    public String supplierShowHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/supplierShow";
    }


    /**
     * @Description: 个人中心
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/personalInfoHtml", method = {RequestMethod.GET})
    public String personalInfoHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/personalInfo";
    }

    /**
     * @Description: 修改名称
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/changeNameHtml", method = {RequestMethod.GET})
    public String changeNameHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/changeName";
    }

    /**
     * @Description: 修改密码
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/changePasswordHtml", method = {RequestMethod.GET})
    public String changePasswordHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/changePassword";
    }

    /**
     * @Description: 忘记密码
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/forgetPasswordHtml", method = {RequestMethod.GET})
    public String forgetPasswordHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/forgetPassword";
    }

    /**
     * @Description: 注册
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/registerHtml", method = {RequestMethod.GET})
    public String registerHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/register";
    }

    /**
     * @Description: 供应商入驻
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/joinHtml", method = {RequestMethod.GET})
    public String joinHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/join";
    }

    /**
     * @Description: 供应商入驻提示
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/auditHtml", method = {RequestMethod.GET})
    public String auditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobile/audit";
    }


    /**
     * @Description: 供应商入驻信息提交
     * @Author: Chen.zm
     * @Date: 2017/11/8 0008
     */
    @RequestMapping(value = "/joinSuplier", method = {RequestMethod.POST})
    public ResponseEntity joinSuplier(HttpServletRequest request, @RequestBody JoinForm form) throws Exception {
        if (form.phone == null)
            return new ResponseEntity(new RestResponseEntity(110,"手机号不能为空",null), HttpStatus.OK);
        suplierService.joinSuplier(request, CurrentUser.get().userId, form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 修改头像 【微信图片
     * @Author: Chen.zm
     * @Date: 2017/11/8 0008
     */
    @RequestMapping(value = "/updateIconWeChat", method = {RequestMethod.POST})
    public ResponseEntity updateIcon(HttpServletRequest request, @RequestBody IconForm form) throws Exception {
        if (StringUtils.isBlank(form.icon))
            return new ResponseEntity(new RestResponseEntity(110, "头像不能为空", null), HttpStatus.OK);
        suplierService.updateIcon(request, CurrentUser.get().userId, form.icon);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }



    @RequestMapping(value = "/listHtml", method = {RequestMethod.GET})
    public String listHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/list";
    }


    /**
     * @Description: 平台工单各状态及数量
     * @Author: Chen.zm
     * @Date: 2017/12/7 0007
     */
    @RequestMapping(value = "/producedStatusCountOnline", method = {RequestMethod.POST})
    public ResponseEntity producedStatusCountOnline() throws Exception {
        JSONArray data = suplierService.producedStatusCountOnline(CurrentUser.get().getSuplierId());
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

    /**
     * @Description: 供应商工单各状态及数量
     * @Author: Chen.zm
     * @Date: 2017/12/7 0007
     */
    @RequestMapping(value = "/producedStatusCountOffline", method = {RequestMethod.POST})
    public ResponseEntity producedStatusCountOffline() throws Exception {
        JSONArray data = suplierService.producedStatusCountOffline(CurrentUser.get().getSuplierId());
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

    /**
     * @Description: 首页统计数据
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    @RequestMapping(value = "/supplierHomeInfo", method = {RequestMethod.POST})
    public ResponseEntity supplierHomeInfo() throws Exception {
        JSONObject data = suplierService.supplierHomeInfo(CurrentUser.get().getSuplierId());
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

    /**
     * @Description: 本地工单交货日期列表
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    @RequestMapping(value = "/supOrderDeliveryList", method = {RequestMethod.POST})
    public ResponseEntity supOrderDeliveryList(@RequestBody TypeForm form) throws Exception {
        List<JSONObject> list = suplierService.supOrderDeliveryList(CurrentUser.get().getSuplierId(), form.type);
        JSONArray data = new JSONArray();
        data.addAll(list);
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

    /**
     * @Description: 客户下单量
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    @RequestMapping(value = "/supOrderCustomerList", method = {RequestMethod.POST})
    public ResponseEntity supOrderCustomerList(@RequestBody DateStrEndForm form) throws Exception {
        List<JSONObject> list = suplierService.supOrderCustomerList(CurrentUser.get().getSuplierId(), form.dateStr, form.dateEnd);
        JSONArray data = new JSONArray();
        data.addAll(list);
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

    /**
     * @Description: 工人工单量
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    @RequestMapping(value = "/supOrderWorkerList", method = {RequestMethod.POST})
    public ResponseEntity supOrderWorkerList(@RequestBody DateStrEndForm form) throws Exception {
        List<JSONObject> list = suplierService.supOrderWorkerList(CurrentUser.get().getSuplierId(), form.dateStr, form.dateEnd);
        JSONArray data = new JSONArray();
        data.addAll(list);
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

}
