package com.xxx.suplier.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.pay.zhifubao.utils.ZxingUtils;
import com.xxx.suplier.form.*;
import com.xxx.user.dao.PropertiesDao;
import com.xxx.user.utils.SessionUtils;
import com.xxx.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SupOrderOfflineService extends CommonService {

    @Autowired
    private SuplierOrderService suplierOrderService;
    @Autowired
    private PropertiesDao propertiesDao;

    /**
     * @Description:修改结款状态
     * @Author: hanchao
     * @Date: 2017/12/18 0018
     */
    @CacheEvict(value = {"SupOrder"}, allEntries = true)
    public void supOrderPayStatusSave(Integer suplierId, List<IdForm> ids,String payStatus) throws UpsertException, ResponseEntityException,ParseException {
        for(IdForm id : ids){
            SupOrder supOrder = get2(SupOrder.class,"id",id.id,"suplierId",suplierId);
            if(supOrder == null)
                throw new ResponseEntityException(210, "工单不存在");
            supOrder.setPayStatus(payStatus);
            upsert2(supOrder);
        }
    }

    /**
     * @Description: 供应商工单列表
     * @Author: Chen.zm
     * @Date: 2017/12/4 0004
     */
    public PageList<SupOrder> supOrderListOffline(PageQuery pageQuery, SupOrderListOfflineForm form, Integer suplierId) {
        Criterion cri = Restrictions.eq("orderType", 2);
        cri = Restrictions.and(cri, Restrictions.eq("suplierId", suplierId));
        if (form.producedStatus != null)
            cri = Restrictions.and(cri, Restrictions.eq("producedStatus", form.producedStatus));
        if (form.urgent != null)
            cri = Restrictions.and(cri, Restrictions.eq("urgent", form.urgent));
        if (StringUtils.isNotBlank(form.supOrderNo))
            cri = Restrictions.and(cri, Restrictions.like("orderNo", form.supOrderNo, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(form.payStatus))
            cri = Restrictions.and(cri, Restrictions.like("payStatus", form.payStatus, MatchMode.ANYWHERE));
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
        if (StringUtils.isNotBlank(form.customer)) {
            ExtFilter filter = new ExtFilter("supOrderDelivery.customer", "string", form.customer, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(form.salesOrderNo)) {
            ExtFilter filter = new ExtFilter("supSalesOrder.orderNo", "string", form.salesOrderNo, ExtFilter.ExtFilterComparison.like, null);
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
     * @Description: 供应商工单新增编辑
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"SupOrder"}, allEntries = true)
    public SupOrder saveSupOrderOffline(Integer suplierId, SupOrderOfflineSaveForm form) throws UpsertException, ResponseEntityException,ParseException {
        SupOrder supOrder = new SupOrder();
        SupOrderDetail supOrderDetail = new SupOrderDetail();
        SupOrderDelivery supOrderDelivery = new SupOrderDelivery();
        if (form.supOrderId != null) {
            supOrder = suplierOrderService.findSupOrderDateil(form.supOrderId, suplierId);
            if (supOrder == null || supOrder.getOrderType() != 2)
                throw new ResponseEntityException(210, "工单不存在");
            supOrderDetail = supOrder.getSupOrderDetail();
            supOrderDelivery = supOrder.getSupOrderDelivery();
        } else {
            supOrder.setProducedStatus(2);
            supOrder.setOrderType(2);
            supOrder.setSuplierId(suplierId);
            SupSuplier suplier = get2(SupSuplier.class, suplierId);
            if (suplier == null || StringUtils.isBlank(suplier.getCode()))
                throw new ResponseEntityException(220, "账户信息异常");
            supOrder.setOrderNo(propertiesDao.generateSupOrderNumber(suplier.getCode()));
        }
        supOrder.setInsideOrderNo(form.insideOrderNo);
        supOrder.setRemarks(form.remarks);
        supOrder.setDescription(form.description);
        supOrder = upsert2(supOrder);

        supOrderDelivery.setOrderId(supOrder.getId());
        supOrderDelivery.setCustomer(form.customer);
        supOrderDelivery.setCustomerPhone(form.customerPhone);
        supOrderDelivery.setCustomerId(form.customerId);
        if(form.customerAddressId != null){
            supOrderDelivery.setCustomerAddressId(form.customerAddressId);
            SupCustomerAddress supCustomerAddress = get2(SupCustomerAddress.class, form.customerAddressId);
            supOrderDelivery.setReceiver(supCustomerAddress.getReceiver() == null ? "" : supCustomerAddress.getReceiver());
            supOrderDelivery.setMobile(supCustomerAddress.getMobile());
            supOrderDelivery.setProvince(supCustomerAddress.getProvince());
            supOrderDelivery.setProvinceName(supCustomerAddress.getProvinceName());
            supOrderDelivery.setCity(supCustomerAddress.getCity());
            supOrderDelivery.setCityName(supCustomerAddress.getCityName());
            supOrderDelivery.setZone(supCustomerAddress.getZone());
            supOrderDelivery.setZoneName(supCustomerAddress.getZoneName());
            supOrderDelivery.setAddress(supCustomerAddress.getAddress());
            supOrderDelivery.setDeliveryDate(DateTimeUtils.parseDate(form.deliveryDate, "yyyy-MM-dd"));
        }
        upsert2(supOrderDelivery);

        supOrderDetail.setOrderId(supOrder.getId());
        supOrderDetail.setPno(form.pno);
        supOrderDetail.setHref(form.imgs.size() == 0 ? "" : form.imgs.get(0).href);
        supOrderDetail.setCategoryName(form.categoryName);
        supOrderDetail.setColor(form.color);
        supOrderDetail.setSize(form.size);
        supOrderDetail.setThroatheight(form.throatheight);
        supOrderDetail.setShoulder(form.shoulder);
        supOrderDetail.setBust(form.bust);
        supOrderDetail.setWaist(form.waist);
        supOrderDetail.setHipline(form.hipline);
        supOrderDetail.setHeight(form.height);
        supOrderDetail.setWeight(form.weight);
        supOrderDetail.setQty(form.qty);
        supOrderDetail.setOutputPrice(form.outputPrice);
        supOrderDetail.setMaterial(form.material);
        supOrderDetail.setTechnics(form.technics);
        supOrderDetail = upsert2(supOrderDetail);

        //删除原先图片
        String hql = "delete SupOrderDetailPicture  where detailId = :detailId";
        getCurrentSession().createQuery(hql).setInteger("detailId", supOrderDetail.getId()).executeUpdate();
        for (HrefForm img : form.imgs) {
            SupOrderDetailPicture picture = new SupOrderDetailPicture();
            picture.setDetailId(supOrderDetail.getId());
            picture.setHref(img.href);
            upsert2(picture);
        }
        return supOrder;
    }

    /**
     * @Description: 供应商工单删除
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @CacheEvict(value = {"SupOrder"}, allEntries = true)
    public void removeSupOrderOffline(Integer suplierId, Integer supOrderId) throws UpsertException, ResponseEntityException{
        SupOrder supOrder = suplierOrderService.findSupOrderDateil(supOrderId, suplierId);
        if (supOrder == null || supOrder.getOrderType() != 2)
            throw new ResponseEntityException(210, "工单不存在");
        supOrder.setLogicDeleted(true);
        upsert2(supOrder);
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
//        map.put("productCode", "");
        map.put("pno", supOrder.getSupOrderDetail().getPno());
        map.put("createTime", DateTimeUtils.parseStr(supOrder.getCreatedDate(), "yyyy-MM-dd"));
        map.put("deliveryTime", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
        map.put("color", supOrder.getSupOrderDetail().getColor());
        map.put("size", supOrder.getSupOrderDetail().getSize());
        map.put("throatheight",supOrder.getSupOrderDetail().getThroatheight());
        map.put("shoulder",supOrder.getSupOrderDetail().getShoulder());
        map.put("bust",supOrder.getSupOrderDetail().getBust());
        map.put("waist",supOrder.getSupOrderDetail().getWaist());
        map.put("hipline",supOrder.getSupOrderDetail().getHipline());
        map.put("height",supOrder.getSupOrderDetail().getHeight());
        map.put("weight",supOrder.getSupOrderDetail().getWeight());
        map.put("material", supOrder.getSupOrderDetail().getMaterial());
        map.put("technics", supOrder.getSupOrderDetail().getTechnics());
        map.put("description", supOrder.getDescription());

        map.put("barCode", BarcodeUtil.generateBase64(supOrder.getOrderNo()));
        map.put("qrCode", ZxingUtils.getQRCodeBase64("http://f.heyizhizao.com/suplierHome/getOrderHtml?orderId=" + supOrder.getId(), 100,100));
        for (String key : map.keySet()) {
            if (map.get(key) == null) map.put(key, "");
        }
        map.put("img1", "");
        map.put("img2", "");
        map.put("imgs", "");

        StringBuffer imgs =  new StringBuffer("");
        int i = 1;
        for (SupOrderDetailPicture picture: supOrder.getSupOrderDetail().getSupOrderDetailPictureList()) {
            if (i < 3) {
                JSONObject json = Base64ImageUtils.getImageBase64FromUrlJSON(OSSClientUtil.getObjectUrl(picture.getHref()));
                String base64 = json.getString("base64");
                int width = json.getIntValue("width");
                int height = json.getIntValue("height");

                //等比例缩放
                if (width <= 0 || height <= 0) {
                    width = 187;
                    height = 335;
                } else if (width > 187) {
                    double d = Arith.div(187, width);
                    if (Arith.mul(height, d) > 335) {
                        d = Arith.div(335, height);
                        width = (int)Arith.mul(width, d);
                        height = 335;
                    } else {
                        height = (int)Arith.mul(height, d);
                        width = 187;
                    }
                } else if (height > 335) {
                    double d = Arith.div(335, height);
                    if (Arith.mul(width, d) > 187) {
                        d = Arith.div(187, width);
                        height = (int)Arith.mul(height, d);
                        width = 187;
                    } else {
                        width = (int)Arith.mul(width, d);
                        height = 335;
                    }
                }

//                if (width <= 0 || width > 190) width = 190;
//                if (height <= 0 || height > 335) height = 335;

                //word图片占位符
                StringBuffer str = new StringBuffer();
                str.append("<w:binData w:name=\"wordml://02000007" + i + ".jpg\" xml:space=\"preserve\">");
                str.append(base64);
                str.append("</w:binData><v:shape id=\"_x0000_i1030\" type=\"#_x0000_t75\" style=\"width:"+width+"pt;height:"+height+"pt\">" +
                        "<v:imagedata src=\"wordml://02000007" + i + ".jpg\" o:title=\"5\"/></v:shape>");

                map.put("img" + i, str);
            } else {
                imgs.append("<w:r wsp:rsidRPr=\"000119FD\"><w:rPr><w:rFonts w:ascii=\"苹方 中等\" w:fareast=\"苹方 中等\" w:h-ansi=\"苹方 中等\" w:cs=\"苹方 中等\"/><wx:font wx:val=\"苹方 中等\"/><w:noProof/><w:sz w:val=\"18\"/><w:sz-cs w:val=\"18\"/></w:rPr><w:pict>");

                imgs.append("<w:binData w:name=\"wordml://02000007" + i + ".jpg\" xml:space=\"preserve\">");
                imgs.append(Base64ImageUtils.getImageBase64FromUrl(OSSClientUtil.getObjectUrl(picture.getHref())));
                imgs.append("</w:binData><v:shape id=\"_x0000_i1030\" type=\"#_x0000_t75\" style=\"width:40pt;height:40pt\">" +
                        "<v:imagedata src=\"wordml://02000007" + i + ".jpg\" o:title=\"5\"/></v:shape>");

                imgs.append("</w:pict></w:r>" +
                        "<w:r wsp:rsidR=\"006D1968\"><w:rPr><w:rFonts w:ascii=\"苹方 中等\" w:fareast=\"苹方 中等\" w:h-ansi=\"苹方 中等\" w:cs=\"苹方 中等\" w:hint=\"fareast\"/><wx:font wx:val=\"苹方 中等\"/><w:sz w:val=\"18\"/><w:sz-cs w:val=\"18\"/></w:rPr><w:t> </w:t></w:r>");
            }
            i++;
        }
        map.put("imgs", imgs);

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

    /**
     * @Description: 修改紧急状态
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    public void updateUrgent(Integer suplierId, Integer supOrderId, Integer urgent) throws UpsertException, ResponseEntityException{
        SupOrder supOrder = suplierOrderService.findSupOrderDateil(supOrderId, suplierId);
        if (supOrder == null || supOrder.getOrderType() != 2)
            throw new ResponseEntityException(210, "工单不存在");
        supOrder.setUrgent(urgent);
        upsert2(supOrder);
    }

}
