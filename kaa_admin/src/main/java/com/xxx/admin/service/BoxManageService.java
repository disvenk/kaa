package com.xxx.admin.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.dao.SuplierOrderDao;
import com.xxx.admin.form.*;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.service.BaseDataService;
import com.xxx.user.service.UploadFileService;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BoxManageService extends CommonService {

    @Autowired
    private SuplierOrderDao suplierOrderDao;
    /**
     * @Description:点击确认按钮修改订单状态为已完成
     * @Author: hanchao
     * @Date: 2017/12/23 0023
     */
    @CacheEvict(value = {"BoxUseLog"}, allEntries = true)
    public void boxRecordPayStatus(Integer id) throws UpsertException, ResponseEntityException {
        BoxUseLog boxUseLog = get2(BoxUseLog.class,"id",id);
        if (boxUseLog == null)
            throw new ResponseEntityException(210, "商品不存在");
        boxUseLog.setStatus(3);//3,已完成
        upsert2(boxUseLog);
    }

    /**
     * @Description:获取当前记录号的物流信息
     * @Author: hanchao
     * @Date: 2017/12/23 0023
     */
    @Cacheable(value = {"BoxUseLog"})
    public List<BoxUseLog> findBoxRecordDeliveryList(Integer id) {
        Criterion cri = Restrictions.eq("id",id);
        return getCurrentSession().createCriteria(BoxUseLog.class)
                .add(cri)
                .setFetchMode("plaProductBase", FetchMode.JOIN)
                .addOrder(Order.desc("id"))
                .list();
    }

    /**
     * @Description:盒子商品快递寄出
     * @Author: hanchao
     * @Date: 2017/12/23 0023
     */
    @CacheEvict(value = {"BoxUseLog"}, allEntries = true)
    public void saveBoxRecordDeliver(Integer id, Integer deliveryCompany, String deliveryCompanyName, String deliveryNo) throws UpsertException, ResponseEntityException {
        BoxUseLog boxUseLog = get2(BoxUseLog.class,"id",id);
        if (boxUseLog == null)
            throw new ResponseEntityException(210, "商品不存在");
        boxUseLog.setIssueDeliveryCompany(deliveryCompany);
        boxUseLog.setIssueDeliveryCompanyName(deliveryCompanyName);
        boxUseLog.setIssueDeliveryNo(deliveryNo);
        boxUseLog.setStatus(1);
        upsert2(boxUseLog);
    }

    /**
     * @Description:盒子订阅记录
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    @Cacheable(value = {"BoxUseLog,BoxUseLogProduct"})
    public PageList<BoxUseLog> findBoxRecordManageList(PageQuery pageQuery, String userName, String startTime,String endTime,Integer status) throws ParseException {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isNotBlank(userName)) {
            ExtFilter filter = new ExtFilter("stoStoreInfo.storeName", "string", userName, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if(status != null){
            cri = Restrictions.and(cri, Restrictions.eq("status", status));
        }
        if(StringUtils.isNotBlank(startTime))
            cri = Restrictions.and(cri, Restrictions.ge("createdDate",DateTimeUtils.parseDate(startTime,"yyyy-MM-dd")));
        if(StringUtils.isNotBlank(endTime))
            cri = Restrictions.and(cri, Restrictions.le("createdDate", DateTimeUtils.parseDate(endTime,"yyyy-MM-dd")));
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<BoxUseLog> list = hibernateReadonlyRepository.getList(BoxUseLog.class, pageQuery);
        for (BoxUseLog boxUseLog : list) {
            for(BoxUseLogProduct boxUseLogProduct : boxUseLog.getBoxUseLogProductList());
        }
        return list;
    }


    /**
     * @Description:将商品库商品添加到盒子
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    @CacheEvict(value = {"BoxProduct"}, allEntries = true)
    public BoxProduct insertBoxProduct(IdForm form) throws UpsertException,ResponseEntityException {
        PlaProduct pla = get2(PlaProduct.class, form.id);
        if (pla == null)
            throw new ResponseEntityException(210, "商品不存在");
        //盒子商品
        BoxProduct box = new BoxProduct();
        box.setPid(pla.getId());
        box.setStatus(0);//默认从商品库添加的商品为上架状态
        box.setUpdateDate(new Date());
        box.setDescription(pla.getPlaProductDescription() == null ? "" : pla.getPlaProductDescription().getDescription());
        return upsert2(box);
    }

    /**
     * @Description:盒子商品上下架
     * @Author: hanchao
     * @Date: 2017/12/23 0023
     */
    @CacheEvict(value = {"BoxProduct"}, allEntries = true)
    public void updateProductStatus(Integer id,Integer status) throws UpsertException, ResponseEntityException {
        BoxProduct box = get2(BoxProduct.class, id);
        if(box == null){
            throw new ResponseEntityException(210, "商品不存在");
        }
        box.setStatus(status);
        upsert2(box);
    }

    /**
     * @Description:盒子商品删除
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    @CacheEvict(value = {"BoxProduct"}, allEntries = true)
    public void updateProductId(Integer id) throws UpsertException, ResponseEntityException {
        BoxProduct box = get2(BoxProduct.class, id);
        if(box == null){
            throw new ResponseEntityException(210, "商品不存在");
        }
        box.setLogicDeleted(true);
        upsert2(box);
    }

    /**
     * @Description:合一盒子从商品库新增商品(列表筛选显示)
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    @Cacheable(value = {"PlaProduct,PlaProductPicture,PlaProductPrice,PlaProductCategory"})
    public PageList<PlaProduct> findProductList(PageQuery pageQuery, List<Integer> plaProductCategory, String labelIds, Double startPrice, Double endPrice,String productCode) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (plaProductCategory.size() != 0)
            cri = Restrictions.and(cri, Restrictions.in("categoryId", plaProductCategory));
        if (StringUtils.isNotBlank(labelIds)) {
            ExtFilter filter = new ExtFilter("plaProductLabelList.labelId", "list", labelIds, ExtFilter.ExtFilterComparison.in, null);
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(filter);
            pageQuery.filter = jsonArray.toJSONString();
        }
        if (startPrice != null)
            cri = Restrictions.and(cri, Restrictions.ge("minPrice", startPrice));
        if (endPrice != null)
            cri = Restrictions.and(cri, Restrictions.le("minPrice", endPrice));
        if (StringUtils.isNotBlank(productCode))
            cri = Restrictions.and(cri, Restrictions.like("productCode", productCode,MatchMode.ANYWHERE));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<PlaProduct> list = hibernateReadonlyRepository.getList(PlaProduct.class, pageQuery);
        return list;
    }

    /**
     * @Description:合一盒子详情保存
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    @CacheEvict(value = {"BoxProduct"}, allEntries = true)
    public BoxProduct updateBoxManageDetailEdit(BoxDetailForm form) throws UpsertException,ResponseEntityException,ParseException {
        BoxProduct box = get2(BoxProduct.class, form.id);
        if (box == null)
            throw new ResponseEntityException(210, "商品不存在");
        box.setDescription(form.description);
        box.setStock(form.stock);
        return upsert2(box);
    }

    /**
     * @Description:根据id获取订阅次数
     * @Author: hanchao
     * @Date: 2017/12/29 0029
     */
    @Cacheable(value = {"BoxUseLogProduct"})
    public JSONObject findOrderCount(Integer orderCount) throws ResponseEntityException, UpsertException {
        JSONObject js =  suplierOrderDao.findOrderCount(orderCount);
        return js;
    }


    /**
     * @Description:合一盒子平台管理
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    @CacheEvict(value = {"BoxProduct"}, allEntries = true)
    public PageList<BoxProduct> findBoxManageList(PageQuery pageQuery, String name, String productCode) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(productCode)) {
            ExtFilter filter = new ExtFilter("plaProduct.productCode", "string", productCode, ExtFilter.ExtFilterComparison.like, null);
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(filter);
            pageQuery.filter = jsonArray.toJSONString();
        }
        if (StringUtils.isNotBlank(name)) {
            ExtFilter filter = new ExtFilter("plaProduct.name", "string", name, ExtFilter.ExtFilterComparison.like, null);
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(filter);
            pageQuery.filter = jsonArray.toJSONString();
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "BoxProduct";
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<BoxProduct> list = hibernateReadonlyRepository.getList(BoxProduct.class, pageQuery);
        for (BoxProduct boxProduct : list) {}
        return list;
    }

    /**
     * @Description: 体验师信息管理
     * @Author: Steven.Xiao
     * @Date: 2017/12/24
     */
    @CacheEvict(value = {"SalesTeacher"}, allEntries = true)
    public PageList<SalesTeacher> findSalesTeacherList(PageQuery pageQuery) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "SalesTeacher";
        pageQuery.order = "desc";
        pageQuery.sort = "createdDate";
        PageList<SalesTeacher> list = hibernateReadonlyRepository.getList(SalesTeacher.class, pageQuery);
        return list;
    }

}
