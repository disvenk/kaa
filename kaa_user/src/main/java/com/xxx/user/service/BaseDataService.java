package com.xxx.user.service;

import com.xxx.core.service.CommonService;
import com.xxx.model.business.PlaProductBase;
import com.xxx.model.business.PlaProductCategory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BaseDataService extends CommonService {

    /**
     * @Description: 基本参数的取得
     * 		1：商品标签
     * 		2：商品颜色
     * 		3：商品尺寸
     * 		4：用户身份
     * 		5：快递公司
     * 		6：销售渠道
     * @Author: Steven.Xiao
     * @Date: 2017/11/3
     */
    @Cacheable(value = {"PlaProductBase"})
    public List<PlaProductBase> getBaseDataList(Integer parameterType) {
        Criterion cri = Restrictions.eq("type", parameterType);
        cri = Restrictions.and(cri, Restrictions.eq("logicDeleted", false));
        return getCurrentSession().createCriteria(PlaProductBase.class)
                .add(cri)
                .addOrder(Order.asc("id"))
                .list();
    }

    /**
     * @Description:获取基础属性
     * @Author: hanchao
     * @Date: 2017/10/31 0031
     */
    @Cacheable(value = {"PlaProductBase"})
    public List<PlaProductBase> findPlaProductBaseList() {
        Criterion cri = Restrictions.isNull("parentId");
        return  getCurrentSession().createCriteria(PlaProductBase.class)
                .add(cri)
                .list();
    }

    /**
     * @Description: 取得商品的一级分类列表；
     * @Author: Steven.Xiao
     * @Date: 2017/11/7
     */
    @Cacheable(value = {"PlaProductCategory"})
    public List<PlaProductCategory> getPlaProductCategoryLevelOneList() {
        Criterion cri = Restrictions.isNull("parentId" );
        cri = Restrictions.and(cri, Restrictions.eq("logicDeleted", false));
        return getCurrentSession().createCriteria(PlaProductCategory.class)
                .add(cri)
                .addOrder(Order.asc("sort"))
                .list();
    }

    /**
     * @Description: 获取商品分类
     * @Author: Chen.zm
     * @Date: 2017/11/15 0015
     */
    @Cacheable(value = {"PlaProductCategory"})
    public List<PlaProductCategory> getPlaProductCategoryList(Integer parentId) {
        Criterion cri = Restrictions.eq("parentId", parentId);
        cri = Restrictions.and(cri, Restrictions.eq("logicDeleted", false));
        return getCurrentSession().createCriteria(PlaProductCategory.class)
                .add(cri)
                .addOrder(Order.asc("sort"))
                .list();
    }

    /**
     * @Description: 获取商品全部分类
     * @Author: Chen.zm
     * @Date: 2017/11/15 0015
     */
    @Cacheable(value = {"PlaProductCategory"})
    public List<PlaProductCategory> getPlaProductCategoryListAll() {
        return getCurrentSession().createCriteria(PlaProductCategory.class)
                .add(Restrictions.eq("logicDeleted", false))
                .addOrder(Order.asc("parentId"))
                .addOrder(Order.asc("sort"))
                .list();
    }

    /**
     * @Description: 查询 某分类及该分类下面的所有子分类；
     * @Author: Steven.Xiao
     * @Date: 2017/11/2
     */
    @Cacheable(value = {"PlaProductCategory"})
    public List<Integer> getAllCategoryIdByParentId(Integer id) {
        List<Integer> list = new ArrayList<>();
        list.add(id);
        Criterion cri = Restrictions.eq("parentId", id);
        cri = Restrictions.and(cri, Restrictions.eq("logicDeleted", false));
        List<PlaProductCategory> categories = getCurrentSession().createCriteria(PlaProductCategory.class).add(cri).list();
        for (PlaProductCategory category : categories) {
            list.addAll(getAllCategoryIdByParentId(category.getId()));
        }
        return list;
    }
}






