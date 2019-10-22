package com.xxx.suplier.service;


import com.alibaba.fastjson.JSONArray;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.PlaProductPicture;
import com.xxx.model.business.SupOrder;
import com.xxx.model.business.SupOrderDetailPicture;
import com.xxx.suplier.form.SupOrderListOnlineForm;
import com.xxx.utils.BarcodeUtil;
import com.xxx.utils.Base64ImageUtils;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SupOrderOnlineService extends CommonService {

    /**
     * @Description: 平台工单列表
     * @Author: Chen.zm
     * @Date: 2017/12/4 0004
     */
    public PageList<SupOrder> supOrderListOnline(PageQuery pageQuery, SupOrderListOnlineForm form, Integer suplierId) {
        Criterion cri = Restrictions.eq("orderType", 1);
        cri = Restrictions.and(cri, Restrictions.eq("suplierId", suplierId));
        if (form.producedStatus != null)
            cri = Restrictions.and(cri, Restrictions.eq("producedStatus", form.producedStatus));
        if (StringUtils.isNotBlank(form.supOrderNo))
            cri = Restrictions.and(cri, Restrictions.like("orderNo", form.supOrderNo, MatchMode.ANYWHERE));
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isNotBlank(form.pno)) {
            ExtFilter filter = new ExtFilter("supOrderDetail.pno", "string", form.pno, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.startTime)) {
            ExtFilter filter = new ExtFilter("createdDate", "date", form.startTime, ExtFilter.ExtFilterComparison.ge, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.endTime)) {
            ExtFilter filter = new ExtFilter("createdDate", "date", form.endTime, ExtFilter.ExtFilterComparison.le, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.deliveryDateStart)) {
            ExtFilter filter = new ExtFilter("supOrderDelivery.deliveryDate", "date", form.deliveryDateStart, ExtFilter.ExtFilterComparison.ge, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.deliveryDateEnd)) {
            ExtFilter filter = new ExtFilter("supOrderDelivery.deliveryDate", "date", form.deliveryDateEnd, ExtFilter.ExtFilterComparison.le, null);
            jsonArray.add(filter);
        }
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "supOrderDetail,supOrderDelivery,supSuplier";
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<SupOrder> list = hibernateReadonlyRepository.getList(SupOrder.class, pageQuery);
        return list;
    }


    /**
     * @Description: 获取word导出数据源
     * @Author: Chen.zm
     * @Date: 2017/12/8 0008
     */
    public Map<String, Object> exportWord(SupOrder supOrder) throws Exception{
        Map<String, Object> map = new HashMap<>();
        map.put("supOrderNo", supOrder.getOrderNo());
        map.put("customer", supOrder.getSupOrderDelivery().getCustomer());
        map.put("productCode", supOrder.getSupOrderDetail().getPlaProduct().getProductCode());
        map.put("pno", supOrder.getSupOrderDetail().getPno());
        map.put("supOrderDate", DateTimeUtils.parseStr(supOrder.getCreatedDate(), "yyyy-MM-dd"));
        map.put("deliveryDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
        map.put("color", supOrder.getSupOrderDetail().getColor());
        map.put("size", supOrder.getSupOrderDetail().getSize());
        map.put("throatheight",supOrder.getSupOrderDetail().getThroatheight());
        map.put("shoulder",supOrder.getSupOrderDetail().getShoulder());
        map.put("bust",supOrder.getSupOrderDetail().getBust());
        map.put("waist",supOrder.getSupOrderDetail().getWaist());
        map.put("hipline",supOrder.getSupOrderDetail().getHipline());
        map.put("height",supOrder.getSupOrderDetail().getHeight());
        map.put("weight",supOrder.getSupOrderDetail().getWeight());
        map.put("material", supOrder.getSupOrderDetail().getPlaProduct().getMaterial());
        map.put("technics", supOrder.getSupOrderDetail().getPlaProduct().getTechnics());
        map.put("description", supOrder.getDescription());
        map.put("barCode", BarcodeUtil.generateBase64(supOrder.getOrderNo()));
        for (String key : map.keySet()) {
            if (map.get(key) == null) map.put(key, "");
        }
        map.put("img1", "");
        map.put("img2", "");
        map.put("img3", "");
        map.put("img4", "");
        map.put("img5", "");

        int i = 1;
        for (PlaProductPicture picture: supOrder.getSupOrderDetail().getPlaProduct().getPlaProductPictureList()) {
            //word图片占位符
            StringBuffer str = new StringBuffer();
            str.append("<w:binData w:name=\"wordml://02000007" + i + ".jpg\" xml:space=\"preserve\">");
            str.append(Base64ImageUtils.getImageBase64FromUrl(OSSClientUtil.getObjectUrl(picture.getHref())));
            str.append("</w:binData><v:shape id=\"_x0000_i1030\" type=\"#_x0000_t75\" style=\"width:191.6pt;height:166.55pt\">" +
                    "<v:imagedata src=\"wordml://02000007" + i + ".jpg\" o:title=\"5\"/></v:shape>");
            map.put("img" + i, str);
            i++;
        }
        return map;
    }

    /**
     * @Description: 批量获取word数据源
     * @Author: Chen.zm
     * @Date: 2017/12/18 0018
     */
    public List<Map> exportWordList(Integer suplierId, List<Integer> supOrderIds) throws Exception{
        Criterion cri = Restrictions.eq("suplierId", suplierId);
        cri = Restrictions.and(cri, Restrictions.in("id", supOrderIds));
        List<SupOrder> supOrderList = getCurrentSession().createCriteria(SupOrder.class)
                .add(cri)
                .setFetchMode("supOrderDetail", FetchMode.JOIN)
                .list();
        List<Map> list = new ArrayList<>();
        for (SupOrder supOrder : supOrderList) {
            Map<String, Object> map = exportWord(supOrder);
            map.put("wordName", supOrder.getOrderNo());
            list.add(map);
        }
        return list;
    }

}
