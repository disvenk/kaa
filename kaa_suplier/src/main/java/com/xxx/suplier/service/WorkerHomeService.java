package com.xxx.suplier.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.PubUserBase;
import com.xxx.model.business.PubUserLogin;
import com.xxx.model.business.SupOrderProductionLog;
import com.xxx.model.business.SupSuplier;
import com.xxx.suplier.dao.SuplierDao;
import com.xxx.suplier.dao.WorkerDao;
import com.xxx.suplier.form.JoinForm;
import com.xxx.user.Commo;
import com.xxx.user.service.UploadFileService;
import com.xxx.utils.DateTimeUtils;
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
public class WorkerHomeService extends CommonService {

    @Autowired
    private SuplierOrderService suplierOrderService;
    @Autowired
    private WorkerDao workerDao;

    /**
     * @Description: 工人订单量统计
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    public JSONObject findWorkerOrder(Integer workerId, String dateStr, String dateEnd) {
        return workerDao.findWorkerOrder(workerId, dateStr, dateEnd);
    }


    /**
     * @Description: 工人每月总工价统计
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    public List<JSONObject> findWorkerMonthOrder(Integer workerId, String dateStr, String dateEnd) {
        return workerDao.findWorkerMonthOrder(workerId, dateStr, dateEnd);
    }

    /**
     * @Description: 工人扫码工单列表
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    public List<JSONObject> findWorkerSupOrder(Integer workerId, String dateStr, String dateEnd) {
        return workerDao.findWorkerSupOrder(workerId, dateStr, dateEnd);
    }


    /**
     * @Description: 创建工单生产记录
     * @Author: Chen.zm
     * @Date: 2018/1/11 0011
     */
    public void saveSupOrderProductionLog(Integer workerId, Integer orderId, Integer detailProcedureId, String procedureName, String userName) throws UpsertException{
        SupOrderProductionLog supOrderProductionLog = new SupOrderProductionLog();
        supOrderProductionLog.setDetailProcedureId(detailProcedureId);
        supOrderProductionLog.setOrderId(orderId);
        supOrderProductionLog.setWorkerId(workerId);
        supOrderProductionLog.setProductionDate(new Date());
        upsert2(supOrderProductionLog);

        //创建工单记录
        suplierOrderService.saveSupOrderDeliveryLog(orderId, "领取工单",procedureName, userName);
    }
}
