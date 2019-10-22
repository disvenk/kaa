package com.xxx.suplier.service;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.SupProcedure;
import com.xxx.suplier.form.ProcedureFroms;
import com.xxx.suplier.form.ProcedureSaveForm;
import com.xxx.user.security.CurrentUser;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class ProductProcedureService extends CommonService {

    /**
     * @Description:查询列表
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @Cacheable(value = {"SupProcedure"})
    public List<SupProcedure> findProcedureListCombox() throws ResponseEntityException {
        List<SupProcedure> list = getCurrentSession().createCriteria(SupProcedure.class)
                .add(Restrictions.eq("suplierId", CurrentUser.get().getSuplierId()))
                .add(Restrictions.eq("logicDeleted",false))
                .addOrder(Order.asc("sort"))
                .list();
        return list;
    }

    /**
     * @Description:查询列表
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @Cacheable(value = {"SupProcedure"})
    public PageList<SupProcedure> findProcedureList(PageQuery pageQuery, String name) throws ResponseEntityException {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name) && name != null) {
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        }

        cri = Restrictions.and(cri, Restrictions.eq("suplierId",  CurrentUser.get().getSuplierId()));
        pageQuery.hibernateCriterion = cri;
//        pageQuery.hibernateFetchFields = "plaProductCategory";
        pageQuery.order = "asc";
        pageQuery.sort = "sort";
        PageList<SupProcedure> list = hibernateReadonlyRepository.getList(SupProcedure.class, pageQuery);
        return list;
    }

    /**
     * @Description:新增
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @CacheEvict(value = "SupProcedure",allEntries = true)
    public SupProcedure addProductProcedure(ProcedureSaveForm form) throws UpsertException, ResponseEntityException {
        SupProcedure supProcedure = null;
        if(form.id!=null){
            supProcedure = get2(SupProcedure.class,form.id);
            supProcedure.setName(form.name);
            supProcedure.setPrice(new BigDecimal(form.price));
            supProcedure.setRemarks(form.remarks);
            return upsert2(supProcedure);
        }else {
            supProcedure = new SupProcedure();
            String sql = "select count(*) from sup_procedure where suplier_id="+CurrentUser.get().getSuplierId()+" and logicDeleted=0";
            String countStr =  getCurrentSession().createSQLQuery(sql).uniqueResult().toString();
            Integer count = Integer.parseInt(countStr);
            String hql = "select max(s.sort) from sup_procedure s where suplier_id="+CurrentUser.get().getSuplierId()+" and logicDeleted=0";
            Session session =getCurrentSession();
           Object o = session.createSQLQuery(hql).uniqueResult();
           String max = null;
                   if(o!=null){
                max = o.toString();
                   }else {
                       max="0";
                   }
            Double  maxSort = Double.parseDouble(max);
            List<SupProcedure> supProcedureList = session.createCriteria(SupProcedure.class)
                    .add(Restrictions.eq("suplierId", CurrentUser.get().getSuplierId()))
                    .add(Restrictions.eq("logicDeleted",false))
                    .addOrder(Order.asc("sort"))
                    .list();
            if(form.sort != null){
                Integer sort = Integer.parseInt(form.sort.toString().substring(0,form.sort.toString().indexOf(".")));
                //如果设置的排序大于总数量，那么就在最大排序后添加一位
                if(form.sort>count){
                    supProcedure.setName(form.name);
                    supProcedure.setRemarks(form.remarks);
                    supProcedure.setSort(maxSort+1);
                    supProcedure.setPrice(new BigDecimal(form.price));
                    supProcedure.setSuplierId(CurrentUser.get().getSuplierId());
                    supProcedure.setUpdateDate(new Date());
                    return upsert2(supProcedure);
                }else if(form.sort==1.0){
                    supProcedure.setName(form.name);
                    supProcedure.setRemarks(form.remarks);
                    supProcedure.setPrice(new BigDecimal(form.price));
                    supProcedure.setSort(supProcedureList.get(sort-1).getSort()-0.01);
                    supProcedure.setSuplierId(CurrentUser.get().getSuplierId());
                    supProcedure.setUpdateDate(new Date());
                    return upsert2(supProcedure);
                }
                supProcedure.setName(form.name);
                supProcedure.setPrice(new BigDecimal(form.price));
                supProcedure.setRemarks(form.remarks);
                supProcedure.setSort(supProcedureList.get(sort-1).getSort()+0.01);
                supProcedure.setSuplierId(CurrentUser.get().getSuplierId());
                supProcedure.setUpdateDate(new Date());
                return upsert2(supProcedure);

            }else {
                supProcedure.setPrice(new BigDecimal(form.price));
                supProcedure.setName(form.name);
                supProcedure.setRemarks(form.remarks);
                supProcedure.setSort(Double.parseDouble(maxSort.toString())+1);
                supProcedure.setSuplierId(CurrentUser.get().getSuplierId());
                supProcedure.setUpdateDate(new Date());
                return upsert2(supProcedure);
            }
        }

    }

    /**
     * @Description:新增多个
     * @Author: disvenk.dai
     * @Date: 2018/1/10
     */
    @CacheEvict(value = "SupProcedure",allEntries = true)
    public ResponseEntity addProductProcedureMany(ProcedureSaveForm form) throws UpsertException, ResponseEntityException {
        for (ProcedureFroms procedureFroms: form.list) {
            SupProcedure supProcedure = new SupProcedure();
            supProcedure.setName(form.name);
            supProcedure.setPrice(new BigDecimal(form.price));
            supProcedure.setRemarks(form.remarks);

            String sql = "select count(*) from sup_procedure where suplier_id="+CurrentUser.get().getSuplierId()+" and logicDeleted=0";
            String countStr =  getCurrentSession().createSQLQuery(sql).uniqueResult().toString();
            Integer count = Integer.parseInt(countStr);
            String hql = "select max(s.sort) from sup_procedure s where suplier_id="+CurrentUser.get().getSuplierId()+" and logicDeleted=0";
            Session session =getCurrentSession();
            Object o = session.createSQLQuery(hql).uniqueResult();
            String max = null;
            if(o!=null){
                max = o.toString();
            }else {
                max="0";
            }
            Double  maxSort = Double.parseDouble(max);

            supProcedure.setPrice(new BigDecimal(form.price));
            supProcedure.setName(form.name);
            supProcedure.setRemarks(form.remarks);
            supProcedure.setSort(Double.parseDouble(maxSort.toString())+1);
            supProcedure.setSuplierId(CurrentUser.get().getSuplierId());
            supProcedure.setUpdateDate(new Date());
            upsert2(supProcedure);
        }

                return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);



    }

    /**
     * @Description:编辑保存Max
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @CacheEvict(value = {"SupProcedure"}, allEntries = true)
    public ResponseEntity addProductProcedureMax(ProcedureSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        SupProcedure supProcedure = get2(SupProcedure.class,"id",form.id);
        String sql = "select count(*) from sup_procedure where suplier_id="+CurrentUser.get().getSuplierId()+" and logicDeleted=0";
        String countStr =  getCurrentSession().createSQLQuery(sql).uniqueResult().toString();
        Integer count = Integer.parseInt(countStr);
        String hql = "select max(s.sort) from sup_procedure s where suplier_id="+CurrentUser.get().getSuplierId()+" and logicDeleted=0";
        Session session = getCurrentSession();
        Object o = session.createSQLQuery(hql).uniqueResult();
        String max = null;
        if(o!=null){
            max = o.toString();
        }else {
            max="0";
        }
        Double  maxSort = Double.parseDouble(max);

        List<SupProcedure> supProcedureList = getCurrentSession().createCriteria(SupProcedure.class)
                .add(Restrictions.eq("suplierId", CurrentUser.get().getSuplierId()))
                .add(Restrictions.eq("logicDeleted",false))
                .addOrder(Order.asc("sort"))
                .list();
        //如果menu存在
        if (supProcedure == null) {
            throw new ResponseEntityException(210, "菜单不存在");
        }
                //如果排序字段不为空

                    //更改为最大排序
                    if(form.sort>count){
                        supProcedure.setName(form.name);
                        supProcedure.setPrice(new BigDecimal(form.price));
                        supProcedure.setRemarks(form.remarks);
                        supProcedure.setSort(Double.parseDouble(maxSort.toString())+1);
                        upsert2(supProcedure);
                        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
                    }

                    supProcedure.setName(form.name);
                    supProcedure.setSort(supProcedureList.get(Integer.parseInt(form.sort
                            .toString()
                            .substring(0,form.sort
                                    .toString()
                                    .indexOf(".")))-1).getSort()+0.001);
                    upsert2(supProcedure);
                    return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:编辑保存Min
     * @Author: disvenk.dai
     * @Date: 2011/1/9
     */
    @CacheEvict(value = {"SupProcedure"}, allEntries = true)
    public ResponseEntity addProductProcedureMin(ProcedureSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        SupProcedure supProcedure = get2(SupProcedure.class,"id",form.id);
        String sql = "select count(*) from sup_procedure where suplier_id="+CurrentUser.get().getSuplierId()+" and logicDeleted=0";
        String countStr =  getCurrentSession().createSQLQuery(sql).uniqueResult().toString();
        Integer count = Integer.parseInt(countStr);
        String hql = "select max(s.sort) from sup_procedure s where suplier_id="+CurrentUser.get().getSuplierId()+" and logicDeleted=0";
        Session session = getCurrentSession();
        Object o = session.createSQLQuery(hql).uniqueResult();
        String max = null;
        if(o!=null){
            max = o.toString();
        }else {
            max="0";
        }
        Double  maxSort = Double.parseDouble(max);

        List<SupProcedure> supProcedureList = getCurrentSession().createCriteria(SupProcedure.class)
                .add(Restrictions.eq("suplierId", CurrentUser.get().getSuplierId()))
                .add(Restrictions.eq("logicDeleted",false))
                .addOrder(Order.asc("sort"))
                .list();
        //如果menu存在
        if (supProcedure == null) {
            throw new ResponseEntityException(210, "菜单不存在");
        }
        //如果排序字段不为空

        //更改为最大排序
        if(form.sort>count){
            supProcedure.setName(form.name);
            supProcedure.setPrice(new BigDecimal(form.price));
            supProcedure.setRemarks(form.remarks);
            supProcedure.setSort(Double.parseDouble(maxSort.toString())+1);
            upsert2(supProcedure);
            return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
        }

        supProcedure.setName(form.name);
        supProcedure.setSort(supProcedureList.get(Integer.parseInt(form.sort
                .toString()
                .substring(0,form.sort
                        .toString()
                        .indexOf(".")))-1).getSort()-0.001);
        upsert2(supProcedure);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:查询最大排序
     * @Author: disvenk.dai
     * @Date: 2018/1/12
     */
    public Integer checkMaxSort() throws ResponseEntityException {
        String sql = "select count(*) from sup_procedure where suplier_id="+CurrentUser.get().getSuplierId()+" and logicDeleted=0";
        Integer count = Integer.parseInt(getCurrentSession().createSQLQuery(sql).uniqueResult().toString())+1;
       return count;
    }

    /**
     * @Description:查询唯一
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @Cacheable(value = "SupProcedure")
    public Boolean checkUnique(ProcedureSaveForm form) throws ResponseEntityException {
        SupProcedure supProcedure = get2(SupProcedure.class,"name",form.name,"logicDeleted",false,
                "suplierId",CurrentUser.get().getSuplierId());
        if(supProcedure !=null)
            return true;
        return false;
    }

    /**
     * @Description:编辑详情
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @Cacheable(value = "SupProcedure")
    public SupProcedure checkProcedureDetail(ProcedureSaveForm form) throws ResponseEntityException {
        SupProcedure supProcedure = get2(SupProcedure.class,form.id);
        if(supProcedure==null)
            throw new ResponseEntityException(220,"该记录不存在");
        return supProcedure;
    }

    /**
     * @Description:删除分类
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @CacheEvict(value = "SupProcedure",allEntries = true)
    public SupProcedure deleteProcedure(ProcedureSaveForm form) throws ResponseEntityException, UpsertException {
        SupProcedure supProcedure = get2(SupProcedure.class,form.id);
       if(supProcedure==null)
           throw new ResponseEntityException(220,"该记录不存在");
        supProcedure.setLogicDeleted(true);
        return upsert2(supProcedure);
    }

    /**
     * @Description:查询排序工具
     * @Author: disvenk.dai
     * @Date: 2018/1/10
     */
    @Cacheable(value = "SupProcedure")
    public Integer getIndex(Integer id) throws ResponseEntityException {
        List<SupProcedure> list = getCurrentSession().createCriteria(SupProcedure.class)
                .add(Restrictions.eq("logicDeleted", false))
                .add(Restrictions.eq("suplierId", CurrentUser.get().getSuplierId()))
                .addOrder(Order.asc("sort"))
                .list();

        SupProcedure supProcedure = get2(SupProcedure.class,id);
        for (int i=0;i<list.size();i++) {
            if(supProcedure.getId()==list.get(i).getId()){
                return i+1;
            }
        }
        return null;
    }
}
