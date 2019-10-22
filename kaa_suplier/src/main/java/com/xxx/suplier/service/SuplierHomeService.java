package com.xxx.suplier.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.suplier.dao.SuplierDao;
import com.xxx.suplier.form.JoinForm;
import com.xxx.user.Commo;
import com.xxx.user.dao.AccountDao;
import com.xxx.user.service.UploadFileService;
import com.xxx.utils.DateTimeUtils;
import com.xxx.model.business.PubUserBase;
import com.xxx.model.business.PubUserLogin;
import com.xxx.model.business.SupSuplier;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service()
public class SuplierHomeService extends CommonService {

    @Autowired
    private SuplierDao suplierDao;
    @Autowired
    private WeChatService weChatService;
    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private AccountDao accountDao;

    /**
     * @Description: 获取首页商品信息
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @Cacheable(value = {"SupProduct,SupOrder"})
    public JSONObject findHomeInfo(Integer suplierId) {
        return suplierDao.findHomeInfo(suplierId);
    }

    /**
     * @Description: 获取指定日期的订单信息
     * @Author: Chen.zm
     * @Date: 2017/11/8 0008
     */
    @Cacheable(value = {"SupOrder"})
    public JSONObject orderCount(Integer suplierId, Integer type) {
        //1.日   2.月  3.年
        String startDate = "";
        String endDate =  DateTimeUtils.parseStr(DateTimeUtils.getDateByAddDay(new Date(), 1), "yyyy-MM-dd");
        switch (type) {
            case 1: startDate = DateTimeUtils.parseStr(DateTimeUtils.getDateByAddDay(new Date(), 0), "yyyy-MM-dd"); break;
            case 2: startDate = DateTimeUtils.parseStr(DateTimeUtils.getDateByAddDay(new Date(), 0, -1, 0), "yyyy-MM-dd"); break;
            case 3: startDate = DateTimeUtils.parseStr(DateTimeUtils.getDateByAddDay(new Date(), 0,0,-1), "yyyy-MM-dd"); break;
        }
        return suplierDao.orderCount(suplierId, startDate, endDate);
    }

    /**
     * @Description: 提交入驻信息
     * @Author: Chen.zm
     * @Date: 2017/11/8 0008
     */
    @CacheEvict(value = {"PubUserLogin,PubUserBase,SupSuplier"}, allEntries = true)
    public SupSuplier joinSuplier(HttpServletRequest request, Integer userId, JoinForm form) throws UpsertException,ResponseEntityException {
        PubUserLogin userLogin = get2(PubUserLogin.class, userId);
        if (userLogin == null)
            throw new ResponseEntityException(200, "用户信息不存在");
        SupSuplier suplier = get2(SupSuplier.class, "userId", userId);
        if (suplier != null)
            throw new ResponseEntityException(210, "入驻信息已提交");
        PubUserBase base = get2(PubUserBase.class, "userId", userId);
        if (base == null) {
            base = new PubUserBase();
            base.setUserId(userId);
            base.setMobile(form.phone);
        }
        base.setName(form.name);
        base.setSex(form.sex);
        base.setPersonID(form.personID);
        upsert2(base);

        suplier = new SupSuplier();
        suplier.setUserId(userId);
        suplier.setName(form.companyName);
        suplier.setContact(form.name);
        suplier.setContactPhone(form.phone);
//        suplier.setCompanyName(form.companyName);
        suplier.setAddress(form.address);
        suplier.setOpenYears(form.openYears);
        suplier.setScope(form.scope);
        suplier.setSmith(form.smith);
        suplier.setSewer(form.sewer);
        suplier.setEditer(form.editer);
        suplier.setModelSet(form.modelSet);
        suplier.setDescription(form.description);
        if (StringUtils.isNotBlank(form.qualifications) && form.scopeType) {
            // 拼接请求地址
            String requestUrl = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
            requestUrl = requestUrl.replace("ACCESS_TOKEN", weChatService.getAccToken(request)).replace("MEDIA_ID", form.qualifications);
            suplier.setQualifications(uploadFileService.saveOssUploadFileByUrl(requestUrl).toString());
        } else if (StringUtils.isNotBlank(form.qualifications) && !form.scopeType) {
            suplier.setQualifications(uploadFileService.saveOssUploadFileByBase64(form.qualifications).toString());
        }
        suplier.setApproveStatus(0);//待审核
        suplier = upsert2(suplier);

        //初始化供应商信息
        accountDao.supplierProductInit(suplier.getId());

        userLogin.setRelationId(suplier.getId());
        upsert2(userLogin);
        return suplier;
    }


    /** 修改头像 【微信
     * @Description:
     * @Author: Chen.zm
     * @Date: 2017/11/8 0008
     */
    @CacheEvict(value = {"PubUserLogin,PubUserBase"}, allEntries = true)
    public void updateIcon(HttpServletRequest request, Integer userId, String icon) throws UpsertException, ResponseEntityException {
        PubUserLogin userLogin = get2(PubUserLogin.class, userId);
        if (userLogin == null)
            throw new ResponseEntityException(200, "账户不存在");
        PubUserBase base = get2(PubUserBase.class, "userId", userId);
        if (base == null) {
            base = new PubUserBase();
            base.setUserId(userId);
        }
        // 拼接请求地址
        String requestUrl = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", weChatService.getAccToken(request)).replace("MEDIA_ID", icon);
        base.setIcon(uploadFileService.saveOssUploadFileByUrl(requestUrl).toString());
        upsert2(base);
    }


    /**
     * @Description: 平台工单各状态及数量
     * @Author: Chen.zm
     * @Date: 2017/12/7 0007
     */
    @Cacheable(value = {"SupOrder"})
    public JSONArray producedStatusCountOnline(Integer suplierId) {
        List<JSONObject> list = suplierDao.producedStatusCount(suplierId, 1);
        //将工单数量转为map形式
        Map<Integer, Integer> map = new HashMap<>();
        for (JSONObject json : list) {
            map.put(json.getInteger("producedStatus"), json.getInteger("count"));
        }
        //生产状态 从2开始， 屏蔽待分配
        JSONArray data = new JSONArray();
        for (int i=2; i<=10; i++) {
            JSONObject json = new JSONObject();
            json.put("type", i);
            json.put("name", Commo.parseSuplierOrderProducedStatus(i));
            json.put("count", map.get(i) == null ? "0" : map.get(i));
            data.add(json);
        }
        return data;
    }


    /**
     * @Description: 供应商工单各状态及数量
     * @Author: Chen.zm
     * @Date: 2017/12/7 0007
     */
    @Cacheable(value = {"SupOrder"})
    public JSONArray producedStatusCountOffline(Integer suplierId) {
        List<JSONObject> list = suplierDao.producedStatusCount(suplierId, 2);
        //将工单数量转为map形式
        Map<Integer, Integer> map = new HashMap<>();
        for (JSONObject json : list) {
            map.put(json.getInteger("producedStatus"), json.getInteger("count"));
        }
        //生产状态 2：待生产  3：生产中   4：已发货  7：已完成
        JSONArray data = new JSONArray();
        for (int i=3; i<=7; i++) {
            JSONObject json = new JSONObject();
            json.put("type", i);
            json.put("name", Commo.parseSuplierOrderProducedStatusOffline(i));
            json.put("count", map.get(i) == null ? "0" : map.get(i));
            data.add(json);
            if (i == 4) i = 6;//屏蔽5,6状态
        }
        return data;
    }


    /**
     * @Description: 首页统计数据
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    public JSONObject supplierHomeInfo(Integer suplierId) {
        return suplierDao.supplierHomeInfo(suplierId);
    }

    /**
     * @Description: 本地工单交货日期列表
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    public List<JSONObject> supOrderDeliveryList(Integer suplierId, Integer type) {
        String deliveryDateStr = null, deliveryDateEnd = null;
        if (type == 1) { //临近交货日期  3天内
            deliveryDateStr = DateTimeUtils.parseStr(DateTimeUtils.getDateByAddDay(new Date(), 0));
            deliveryDateEnd = DateTimeUtils.parseStr(DateTimeUtils.getDateByAddDay(new Date(), 3));
        } else if (type == 2) { //已过交货期
            deliveryDateEnd = DateTimeUtils.parseStr(new Date());
        }
        return suplierDao.supOrderDeliveryList(suplierId, deliveryDateStr, deliveryDateEnd);
    }

    /**
     * @Description: 客户下单量
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    public List<JSONObject> supOrderCustomerList(Integer suplierId, String dateStr, String dateEnd) {
        return suplierDao.supOrderCustomerList(suplierId, dateStr, dateEnd);
    }

    /**
     * @Description: 工人工单量
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    public List<JSONObject> supOrderWorkerList(Integer suplierId, String dateStr, String dateEnd) {
        return suplierDao.supOrderWorkerList(suplierId, dateStr, dateEnd);
    }

}
