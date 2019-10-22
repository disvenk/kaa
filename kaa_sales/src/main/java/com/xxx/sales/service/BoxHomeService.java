package com.xxx.sales.service;


import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ListUtils;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.sales.dao.SalesProductDao;
import com.xxx.sales.form.BoxUseLogForm;
import com.xxx.sales.form.BoxUseLogReturnForm;
import com.xxx.sales.form.IdForm;
import com.xxx.user.dao.PropertiesDao;
import com.xxx.user.security.CurrentUser;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.Date;
import java.util.List;

@Service
public class BoxHomeService extends CommonService {

    @Autowired
    private SalesProductDao salesProductDao;

    //一元限购盒子剩余购买数量配置常量
    public static final String BOX_ONE_COUNT = "BOX_ONE_COUNT";
    //限购盒子的id(boxType.id)
    public static final int BOX_ONE_Id = 1;
    //需要支付的押金金额
    public static final double DEPOSIT = 800;

    @Autowired
    protected PropertiesDao propertiesDao;

    /**
     * @Description: 获取限购盒子的剩余数量
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    public Integer getBoxCount() {
        SysDict dict = get2(SysDict.class, "keyName", BOX_ONE_COUNT);
        return dict == null ? 0 : StringUtils.isBlank(dict.getKeyValue()) ? 0 : Integer.parseInt(dict.getKeyValue());
    }


    /**
     * @Description: 盒子商品列表
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    public PageList<BoxProduct> boxProductList(PageQuery pageQuery) {
        pageQuery.hibernateFetchFields = "plaProduct";
        pageQuery.order = "desc";
        pageQuery.sort = "createdDate";
        PageList<BoxProduct> list = hibernateReadonlyRepository.getList(BoxProduct.class, pageQuery);
        return list;
    }

    /**
     * @Description: 获取盒子商品
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    public BoxProduct getBoxProduct(Integer id) {
        return (BoxProduct) getCurrentSession().createCriteria(BoxProduct.class)
                .add(Restrictions.eq("id", id))
                .setFetchMode("plaProduct", FetchMode.JOIN)
                .setFetchMode("plaProduct.plaProductDescription", FetchMode.JOIN)
                .setFetchMode("plaProduct.plaProductPriceList", FetchMode.JOIN)
                .uniqueResult();
    }


    /**
     * @Description: 获取盒子类型列表
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    public List<BoxType> boxTypeList() {
        return getCurrentSession().createCriteria(BoxType.class)
                .addOrder(Order.asc("id"))
                .list();
    }


    /**
     * @Description: 获取用户限购盒子购买记录
     * @Author: Chen.zm
     * @Date: 2017/12/23 0023
     */
    public boolean getBoxPayOrderOne(Integer storeId) {
        Criterion cri = Restrictions.eq("storeId", storeId);
        cri = Restrictions.and(cri, Restrictions.eq("status", 1));
        cri = Restrictions.and(cri, Restrictions.eq("boxTypeId", BOX_ONE_Id));
        List<BoxPayOrder> list = getCurrentSession().createCriteria(BoxPayOrder.class)
                .add(cri)
                .list();
        if (list == null || list.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @Description: 盒子购买下单
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    public BoxPayOrder saveBoxPayOrder(Integer storeId, Integer boxTypeId) throws ResponseEntityException, UpsertException{
        BoxType boxType = get2(BoxType.class, boxTypeId);
        if (boxType == null)
            throw new ResponseEntityException(210, "盒子类型不存在");
        if (boxType.getId() == BOX_ONE_Id) {
            //校验名额是否足够
            if (getBoxCount() <= 0)
                throw new ResponseEntityException(220, "盒子限购名额已满");
            //校验是否购买过
            if (getBoxPayOrderOne(storeId))
                throw new ResponseEntityException(230, "您已购买过该限购盒子");
        }
        if (boxType.getPrice() == null)
            throw new ResponseEntityException(240, "盒子金额异常");

        BoxPayOrder boxPayOrder = new BoxPayOrder();
        boxPayOrder.setOrderNo(propertiesDao.generateBoxPayOrderNumber());
        boxPayOrder.setStoreId(storeId);
        boxPayOrder.setStatus(0);
        boxPayOrder.setBoxTypeId(boxTypeId);
        boxPayOrder.setDay(boxType.getDay());
        boxPayOrder.setCount(boxType.getCount());
        //校验是否需要支付押金
        double total = boxType.getPrice();
        BoxInfo boxInfo = get2(BoxInfo.class, "storeId", storeId);
        if (boxInfo == null || boxInfo.getDeposit() != DEPOSIT) {
            total += DEPOSIT;
            boxPayOrder.setDeposit(DEPOSIT);
        }
        boxPayOrder.setTotal(total);
        return upsert2(boxPayOrder);
    }


    /**
     * @Description: 盒子使用前校验
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    public BoxInfo boxUseLogCheck(Integer storeId) throws ResponseEntityException{
        BoxInfo boxInfo = get2(BoxInfo.class, "storeId", storeId);
        if (boxInfo == null)
            throw new ResponseEntityException(210, "盒子服务未开通");
        if (boxInfo.getCount() == null || boxInfo.getCount() <= 0)
            throw new ResponseEntityException(220, "盒子剩余使用次数不足");
        if (boxInfo.getTermTime() == null || boxInfo.getTermTime().getTime() < new Date().getTime())
            throw new ResponseEntityException(230, "盒子服务已过期");
        //校验当前是否有正在进行中的服务
        Criterion cri = Restrictions.eq("storeId", storeId);
        cri = Restrictions.and(cri, Restrictions.ne("status", 3));
        List<BoxUseLog> list = getCurrentSession().createCriteria(BoxUseLog.class)
                .add(cri)
                .list();
        if (list != null && list.size() != 0)
            throw new ResponseEntityException(240, "当前有正在进行中的盒子服务");
        return boxInfo;
    }


    /**
     * @Description: 盒子确认使用
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    public void saveBoxUseLog(Integer storeId, BoxUseLogForm form) throws ResponseEntityException, UpsertException{
        BoxInfo boxInfo = boxUseLogCheck(storeId);

        BoxUseLog useLog = new BoxUseLog();
        useLog.setStoreId(storeId);
        useLog.setStatus(0);
        useLog.setOrderNo(propertiesDao.generateBoxUseLogNumber());
        useLog.setReceiver(form.receiver);
        useLog.setMobile(form.mobile);
        useLog.setProvince(form.province);
        useLog.setProvinceName(form.provinceName);
        useLog.setCity(form.city);
        useLog.setCityName(form.cityName);
        useLog.setZone(form.zone);
        useLog.setZoneName(form.zoneName);
        useLog.setAddress(form.address);
        useLog = upsert2(useLog);

        for (IdForm f : form.ids) {
            BoxProduct boxProduct = get2(BoxProduct.class, f.id);
            if (boxProduct == null || boxProduct.getPlaProduct() == null)
                throw new ResponseEntityException(250, "商品信息不存在");
            BoxUseLogProduct useLogProduct = new BoxUseLogProduct();
            useLogProduct.setBoxUseLogId(useLog.getId());
            useLogProduct.setBoxProductId(f.id);
            useLogProduct.setStatus(0);
            useLogProduct.setName(boxProduct.getPlaProduct().getName());
            useLogProduct.setHref(boxProduct.getPlaProduct().getHref());
            useLogProduct.setPrice(boxProduct.getPlaProduct().getMaxPrice());
            useLogProduct.setCount(1);
            upsert2(useLogProduct);
        }

        //扣除剩余使用次数
        boxInfo.setCount(boxInfo.getCount() - 1);
        upsert2(boxInfo);

        //创建盒子服务记录
        saveBoxOperateLog(storeId, "盒子使用(记录号:" + useLog.getOrderNo() + ")");
    }


    /**
     * @Description: 获取盒子使用记录列表
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    public PageList<BoxUseLog> boxUseLogList(Integer storeId, PageQuery pageQuery) {
        Criterion cri = Restrictions.eq("storeId", storeId);
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "boxUseLogProductList";
        pageQuery.order = "desc";
        pageQuery.sort = "createdDate";
        PageList<BoxUseLog> list = hibernateReadonlyRepository.getList(BoxUseLog.class, pageQuery);
        return ListUtils.removeDuplicateWithOrder(list);
    }


    /**
     * @Description: 盒子使用记录退回
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    public void updateBoxUseLogReturn(Integer storeId, BoxUseLogReturnForm form) throws ResponseEntityException, UpsertException{
        BoxUseLog boxUseLog = get2(BoxUseLog.class, "id", form.id, "storeId", storeId);
        if (boxUseLog == null)
            throw new ResponseEntityException(210, "盒子使用记录不存在");
        if (boxUseLog.getStatus() != 1)
            throw new ResponseEntityException(220, "盒子使用记录状态非已发出, 不可退回");
        boxUseLog.setStatus(2);
        boxUseLog.setReturnDeliveryNo(form.deliveryNo);
        boxUseLog.setReturnDeliveryCompanyName(form.deliveryCompanyName);
        boxUseLog.setReturnDeliveryCompany(form.deliverCompanyId);
        upsert2(boxUseLog);

        //创建盒子服务记录
        saveBoxOperateLog(storeId, "样衣退回");
    }


    /**
     * @Description: 创建盒子服务记录
     * @Author: Chen.zm
     * @Date: 2017/12/22 0022
     */
    public void saveBoxOperateLog(Integer storeId, String name) throws ResponseEntityException, UpsertException{
        BoxInfo boxInfo = get2(BoxInfo.class, "storeId", CurrentUser.get().getStoreId());
        BoxOperateLog boxOperateLog = new BoxOperateLog();
        boxOperateLog.setStoreId(storeId);
        boxOperateLog.setName(name);
        boxOperateLog.setPrice(0.0);
        boxOperateLog.setDeposit(boxInfo.getDeposit());
        boxOperateLog.setCount(boxInfo.getCount());
        boxOperateLog.setTermTime(boxInfo.getTermTime());
        upsert2(boxOperateLog);
    }

    /**
     * @Description:根据id获取订阅次数
     * @Author: hanchao
     * @Date: 2017/12/29 0029
     */
    @Cacheable(value = {"BoxUseLogProduct"})
    public JSONObject findOrderCount(Integer orderCount) throws ResponseEntityException, UpsertException {
        JSONObject js =  salesProductDao.findOrderCount(orderCount);
        return js;
    }

}
