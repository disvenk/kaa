package com.xxx.admin.service;

import com.xxx.admin.form.BoxOperateLogform;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.BoxInfo;
import com.xxx.model.business.BoxOperateLog;
import com.xxx.model.business.PubUserLogin;
import com.xxx.model.business.StoStoreInfo;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class HeYiBoxService extends CommonService{

    /**
     * @Description: 查询用户信息
     * @Author: disvenk.dai
     * @Date: 2017/12/22
     */
    @Cacheable(value = {"PubUserLogin"})
    public PubUserLogin getPubUserLogin(Integer id) {
        return get2(PubUserLogin.class,"id",id);
    }

    /**
     * @Description: 查询门店
     * @Author: disvenk.dai
     * @Date: 2017/12/22
     */
    @Cacheable(value = {"BoxInfo"})
    public BoxInfo getStoStoreInfo(Integer id) {


        StoStoreInfo stoStoreInfo =(StoStoreInfo) hibernateReadonlyRepository.get(StoStoreInfo.class,"userId",id);

        return getBoxInfo(stoStoreInfo.getId());
    }

    /**
     * @Description: 查询盒子详情
     * @Author: disvenk.dai
     * @Date: 2017/12/22
     */
    @Cacheable(value = {"BoxInfo"})
    public BoxInfo getBoxInfo(Integer id) {
        BoxInfo boxInfo =(BoxInfo) hibernateReadonlyRepository.get(BoxInfo.class,"storeId",id);

        return  boxInfo;
    }

    /**
     * @Description: 查询盒子日志详情
     * @Author: disvenk.dai
     * @Date: 2017/12/22
     */
    @Cacheable(value = {"BoxOperateLog"})
    public PageList<BoxOperateLog> getBoxOperateLog(BoxOperateLogform form, PageQuery pageQuery) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
            cri = Restrictions.and(cri, Restrictions.eq("storeId", form.id));

        pageQuery.hibernateCriterion = cri;
//        pageQuery.hibernateFetchFields = "plaProductCategory";
        pageQuery.order = "desc";
        pageQuery.sort = "createdDate";
        PageList<BoxOperateLog> list = hibernateReadonlyRepository.getList(BoxOperateLog.class, pageQuery);
        return list;
    }

    /**
     * @Description: 退押金
     * @Author: disvenk,dai
     * @Date: 2017/12/23
     */
    public void returnYaJin(Integer id) throws UpsertException, ResponseEntityException {
        BoxInfo boxInfo = get2(BoxInfo.class,"id",id);
        boxInfo.setDeposit(Double.parseDouble("0"));
        BoxOperateLog boxOperateLog = new BoxOperateLog();
        boxOperateLog.setCount(boxInfo.getCount());
        boxOperateLog.setName("退押金");
        boxOperateLog.setDeposit(Double.parseDouble("0"));
        boxOperateLog.setPrice(Double.parseDouble("0"));
        boxOperateLog.setStoreId(boxInfo.getStoreId());
        boxOperateLog.setTermTime(boxInfo.getTermTime());
        upsert2(boxOperateLog);
         upsert2(boxInfo);
    }
}
