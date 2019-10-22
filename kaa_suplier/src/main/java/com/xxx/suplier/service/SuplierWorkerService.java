package com.xxx.suplier.service;


import com.alibaba.fastjson.JSONArray;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.suplier.form.*;
import com.xxx.user.service.AccountService;
import com.xxx.user.service.UploadFileService;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.MD5Utils;
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

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SuplierWorkerService extends CommonService {

    @Autowired
    private AccountService accountService;

    /**
     * @Description:工厂工人信息逻辑删除
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @CacheEvict(value = {"SupWorker"}, allEntries = true)
    public void removeSuplierWorker(Integer id, Integer suplierId) throws UpsertException,ResponseEntityException {
        SupWorker supWorker = get2(SupWorker.class, "id", id, "suplierId", suplierId);
        if (supWorker == null)
            throw new ResponseEntityException(120, "工人信息不存在");
        supWorker.setLogicDeleted(true);
        upsert2(supWorker);
    }


    /**
     * @Description:供应商工人信息新增
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @CacheEvict(value = {"SupWorker,SupWorkerStation"}, allEntries = true)
    public void addSuplierWorker(SuplierWorkerEditForm form,Integer suplierId) throws UpsertException, ResponseEntityException, ParseException {
        SupWorker supWorker = new SupWorker();
        //校验工人手机号是否存在
        SupWorker sup = get2(SupWorker.class, "phone",form.phone);
        if (sup != null)
            throw new ResponseEntityException(130, "手机号已存在");
        supWorker.setCode(form.code);
        supWorker.setName(form.name);
        supWorker.setPhone(form.phone);
        supWorker.setRemarks(form.remark);
//        supWorker.setWorkerType(form.workerType);
        supWorker.setSuplierId(suplierId);
        supWorker.setUpdateDate(new Date());
        supWorker = upsert2(supWorker);
        if (form.workerStationTypeList.size() != 0) {
            for (SuplierWorkerStationTypeForm suplierWorkerStationTypeForm : form.workerStationTypeList) {
                SupWorkerStation supWorkerStation = new SupWorkerStation();
                supWorkerStation.setWorkerId(supWorker.getId());
                supWorkerStation.setProcedureId(suplierWorkerStationTypeForm.workerStationType);
                upsert2(supWorkerStation);
            }
        }
        //创建工人账户
        accountService.saveRegisterUser(supWorker.getPhone(), supWorker.getPhone(), MD5Utils.md5Hex("888888"), 4, null, form.name, supWorker.getId());
    }

    /**
     * @Description:编辑工厂工人信息
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @CacheEvict(value = {"SupWorker,SupWorkerStation"}, allEntries = true)
    public void saveSuplierWorker(Integer supplierId, SuplierWorkerEditForm form) throws UpsertException, ResponseEntityException, ParseException {
        SupWorker supWorker = get2(SupWorker.class, "id", form.id, "suplierId", supplierId);
        if (supWorker == null)
            throw new ResponseEntityException(120, "工人信息不存在");
        //校验工人手机号是否存在
        SupWorker sup = get2(SupWorker.class, "phone", form.phone);
        if (sup != null && sup.getId() != form.id)
            throw new ResponseEntityException(130, "手机号已存在");

        //校验工人账户信息
        PubUserLogin userLogin = accountService.getPubUserLogin(form.phone, form.phone, 4);
        if (userLogin != null && userLogin.getRelationId() != supWorker.getId())
            throw new ResponseEntityException(130, "手机号已存在");
        if (userLogin == null) {
            //创建工人账户
            accountService.saveRegisterUser(supWorker.getPhone(), supWorker.getPhone(), MD5Utils.md5Hex("888888"), 4, null, form.name, supWorker.getId());
        } else {
            userLogin.setUserCode(form.phone);
            upsert2(userLogin);
            PubUserBase base = get2(PubUserBase.class, "userId", userLogin.getId());
            if (base == null) {
                base = new PubUserBase();
                base.setUserId(userLogin.getId());
            }
            base.setMobile(form.phone);
            base.setName(form.name);
            upsert2(base);
        }

        supWorker.setName(form.name);
        supWorker.setCode(form.code);
        supWorker.setPhone(form.phone);
        supWorker.setRemarks(form.remark);
//        supWorker.setWorkerType(form.workerType);
        supWorker.setSuplierId(supplierId);
        supWorker.setUpdateDate(DateTimeUtils.parseDate(form.updateDate,"yyyy-MM-dd HH:mm:ss"));
        supWorker = upsert2(supWorker);

        if (form.workerStationTypeList.size() != 0) {
            String hql = "delete SupWorkerStation where workerId =:workerId";
            getCurrentSession().createQuery(hql).setInteger("workerId", supWorker.getId()).executeUpdate();
            for (SuplierWorkerStationTypeForm suplierWorkerStationTypeForm : form.workerStationTypeList) {
                SupWorkerStation supWorkerStation = new SupWorkerStation();
                supWorkerStation.setWorkerId(supWorker.getId());
                supWorkerStation.setProcedureId(suplierWorkerStationTypeForm.workerStationType);
                upsert2(supWorkerStation);
            }
        }
    }

    /**
     * @Description:供应商工人详情
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @Cacheable(value = {"SupWorker,SupWorkerStation"})
    public SupWorker findSuplierWorkerDateil(Integer id) {
        Criterion cri = Restrictions.eq("id", id);
        SupWorker supWorker = (SupWorker) getCurrentSession().createCriteria(SupWorker.class)
                .add(cri)
                .setFetchMode("supWorkerStationList", FetchMode.JOIN)
                .uniqueResult();
        return supWorker;
    }

    /**
     * @Description:获取工厂工人维护列表
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @Cacheable(value = {"SupWorker"})
    public PageList<SupWorker> findSuplierWorkerList(PageQuery pageQuery,String code,String name,String phone,String stationType, Integer suplierId) {
        Criterion cri = Restrictions.eq("suplierId", suplierId);
        JSONArray jsonArray = new JSONArray();
        if(StringUtils.isNotBlank(code))
            cri = Restrictions.and(cri, Restrictions.like("code", code, MatchMode.ANYWHERE));
        if(StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if(StringUtils.isNotBlank(phone))
            cri = Restrictions.and(cri, Restrictions.like("phone", phone, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(stationType)) {
            ExtFilter filter = new ExtFilter("supWorkerStationList.stationType", "string", stationType, ExtFilter.ExtFilterComparison.eq, null);
            jsonArray.add(filter);
        }
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupWorker> list = hibernateReadonlyRepository.getList(SupWorker.class, pageQuery);
        for (SupWorker supWorker : list) {
            for (SupWorkerStation supWorkerStation : supWorker.getSupWorkerStationList());
        }
        return list;
    }


    /**
     * @Description: 重置密码
     * @Author: Chen.zm
     * @Date: 2017/12/19 0019
     */
    @CacheEvict(value = {"SupWorker"}, allEntries = true)
    public void resetPassword(Integer id, Integer suplierId) throws UpsertException,ResponseEntityException {
        SupWorker supWorker = get2(SupWorker.class, "id", id, "suplierId", suplierId);
        if (supWorker == null)
            throw new ResponseEntityException(120, "工人信息不存在");
        PubUserLogin userLogin = accountService.getPubUserLogin(supWorker.getPhone(), supWorker.getPhone(), 4);
        if (userLogin == null)
            throw new ResponseEntityException(130, "账户信息不存在");
        userLogin.setUserPassword(MD5Utils.md5Hex("888888"));
        upsert2(userLogin);
    }

    /**
     * @Description: 工人列表
     * @Author: Chen.zm
     * @Date: 2018/1/11 0011
     */
    public List<SupWorker> supOrderList(Integer suplierId) {
        List<SupWorker> list = getCurrentSession().createCriteria(SupWorker.class)
                .add(Restrictions.and(Restrictions.eq("logicDeleted", false), Restrictions.eq("suplierId", suplierId)))
                .addOrder(Order.desc("id"))
                .list();
        return list;
    }

}
