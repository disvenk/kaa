package com.xxx.admin.service;


import com.xxx.admin.form.ChannelForm;
import com.xxx.admin.form.DesAddForm;
import com.xxx.admin.form.FactoryAddForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.DesChannel;
import com.xxx.model.business.PlaChannel;
import com.xxx.model.business.SupChannel;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ChannelService extends CommonService {

    /**
     * @Description: 新增渠道
     * @Author: Chen.zm
     * @Date: 2017/10/25 0025
     */
    @CacheEvict(value = {"PlaChannel"}, allEntries = true)
    public PlaChannel savePlaChannel(ChannelForm form) throws UpsertException{
        PlaChannel pal = new PlaChannel();
        pal.setName(form.name);
        pal.setType(form.type);
        pal.setZone(form.zone);
        pal.setZoneName(form.zoneName);
        pal.setCity(form.city);
        pal.setCityName(form.cityName);
        pal.setProvince(form.province);
        pal.setProvinceName(form.provinceName);
        pal.setAddress(form.address);
        pal.setContact(form.contact);
        pal.setTelephone(form.telephone);
        return upsert2(pal);
    }
    /**
     * @Description:渠道保存
     * @Author: hanchao
     * @Date: 2017/11/27 0027
     */
    @CacheEvict(value = {"PlaChannel"}, allEntries = true)
    public PlaChannel updatePlaChannel(ChannelForm form) throws UpsertException,ResponseEntityException{
        PlaChannel pal = getPlaChannel(form.id);
        if (pal == null)
            throw new ResponseEntityException(120, "渠道不存在");
        pal.setName(form.name);
        pal.setType(form.type);
        pal.setZone(form.zone);
        pal.setZoneName(form.zoneName);
        pal.setCity(form.city);
        pal.setCityName(form.cityName);
        pal.setProvince(form.province);
        pal.setProvinceName(form.provinceName);
        pal.setAddress(form.address);
        pal.setContact(form.contact);
        pal.setTelephone(form.telephone);
        return upsert2(pal);
    }

    /**
     * @Description: 删除渠道
     * @Author: Chen.zm
     * @Date: 2017/10/25 0025
     */
    @CacheEvict(value = {"PlaChannel"}, allEntries = true)
    public void removePlaChannel(Integer id) throws ResponseEntityException, UpsertException {
        PlaChannel pla = get2(PlaChannel.class,"id",id);
        if (pla == null) {
            throw new ResponseEntityException(120, "渠道不存在");
        }
        pla.setLogicDeleted(true);
        upsert2(pla);
    }

    /**
     * @Description: 根据id获取渠道
     * @Author: Chen.zm
     * @Date: 2017/10/25 0025
     */
    @Cacheable(value = {"PlaChannel"})
    public PlaChannel getPlaChannel(Integer id) {
        return get2(PlaChannel.class, "id", id);
    }

    /**
     * @Description: 获取渠道列表
     * @Author: Chen.zm
     * @Date: 2017/10/25 0025
     */
    @Cacheable(value = {"PlaChannel"})
    public PageList<PlaChannel> findPlaChannelList(PageQuery pageQuery,String contact, String telephone) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(contact))
            cri = Restrictions.and(cri, Restrictions.like("contact", contact, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(telephone))
            cri = Restrictions.and(cri, Restrictions.like("telephone", telephone, MatchMode.ANYWHERE));
        pageQuery.hibernateCriterion = cri;
//        pageQuery.hibernateFetchFields = "plaProductCategory";
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        PageList<PlaChannel> list = hibernateReadonlyRepository.getList(PlaChannel.class, pageQuery);
        return list;
    }

    /**
     * @Description: 工厂申请加入
     * @Author: disvenk.dai
     * @Date: 2018/2/5 0025
     */
    public SupChannel addFactory(FactoryAddForm form) throws ResponseEntityException, UpsertException {
        SupChannel supChannel = null;
        if(form.id!=null){
            SupChannel supChannel1 = (SupChannel) getCurrentSession().createCriteria(SupChannel.class).add(Restrictions.eq("logicDeleted",false))
                    .add(Restrictions.eq("name",form.factoryName))
                    .add(Restrictions.ne("id",form.id))
                    .uniqueResult();
            if(supChannel1!=null)throw  new ResponseEntityException(220,"该企业已经申请过了");
            supChannel = get2(SupChannel.class,"id",form.id);
            supChannel.setName(form.factoryName);
            supChannel.setTelephone(form.tel);
            supChannel.setProvinceName(form.province);
            supChannel.setCityName(form.city);
            supChannel.setZoneName(form.zone);
            supChannel.setAddress(form.address);
        }else {
            SupChannel supChannel1 = get2(SupChannel.class,"name",form.factoryName);
            if(supChannel1!=null)throw  new ResponseEntityException(220,"该企业已经申请过了");
            supChannel = new SupChannel();
            supChannel.setTelephone(form.tel);
            supChannel.setName(form.factoryName);
            supChannel.setProvinceName(form.province);
            supChannel.setCityName(form.city);
            supChannel.setZoneName(form.zone);
            supChannel.setAddress(form.address);

        }
        return upsert2(supChannel);
    }

    /**
     * @Description: 工厂列表
     * @Author: disvenk.dai
     * @Date: 2018/2/5 0025
     */
    @Cacheable(value = "SupChannel")
    public PageList<SupChannel> factoryInfoList(PageQuery query,String name,String tel){
        Criterion cri = Restrictions.eq("logicDeleted",false);
        if(name!=null && StringUtils.isNotBlank(name)){
            cri = Restrictions.and(cri,Restrictions.like("name",name,MatchMode.ANYWHERE));
        }
        if(tel!=null && StringUtils.isNotBlank(tel)){
            cri = Restrictions.and(cri,Restrictions.like("telephone",tel));
        }

        query.hibernateCriterion=cri;
        query.sort="updateDate";
        query.order="desc";
        PageList<SupChannel> list = hibernateReadonlyRepository.getList(SupChannel.class,query);
        return list;
    }

    /**
     * @Description: 工厂信息删除
     * @Author: disvenk.dai
     * @Date: 2018/2/5 0025
     */
    @CacheEvict(value = "SupChannel",allEntries = true)
    public SupChannel factoryDelete(Integer id) throws ResponseEntityException, UpsertException {
        SupChannel supChannel = get2(SupChannel.class,"id",id);
        if(supChannel==null)throw new ResponseEntityException(220,"该订单不存在");
        supChannel.setLogicDeleted(true);
        return upsert2(supChannel);
    }

    /**
     * @Description:工厂信息编辑
     * @Author: hanchao
     * @Date: 2018/2/5 0005
     */
    @CacheEvict(value = "SupChannel",allEntries = true)
    public SupChannel factoryEditDetail(FactoryAddForm form) throws ResponseEntityException {
        SupChannel supChannel = get2(SupChannel.class,"id",form.id);
        if(supChannel==null) throw new ResponseEntityException(220,"该工厂不存在");
        return supChannel;
    }

    /**
     * @Description:设计师渠道删除
     * @Author: hanchao
     * @Date: 2018/2/5 0005
     */
    @CacheEvict(value = {"DesChannel"}, allEntries = true)
    public void channelDesRemove(Integer id) throws ResponseEntityException, UpsertException {
        DesChannel des = get2(DesChannel.class,"id",id);
        if (des == null) {
            throw new ResponseEntityException(120, "渠道不存在");
        }
        des.setLogicDeleted(true);
        upsert2(des);
    }

    /**
     * @Description:设计师渠道编辑查询重复手机号
     * @Author: hanchao
     * @Date: 2018/2/5 0005
     */
    @Cacheable(value = {"DesChannel"})
    public DesChannel selectDesChannel(Integer id,String telephone,String type) throws UpsertException,ResponseEntityException {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        cri = Restrictions.and(cri,Restrictions.ne("id",id));
        cri = Restrictions.and(cri,Restrictions.eq("telephone",telephone));
        cri = Restrictions.and(cri,Restrictions.eq("type",type));
        return (DesChannel) getCurrentSession().createCriteria(DesChannel.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:设计师渠道编辑保存
     * @Author: hanchao
     * @Date: 2018/2/5 0005
     */
    @CacheEvict(value = {"DesChannel"}, allEntries = true)
    public DesChannel channelDesUpdate(DesAddForm form) throws UpsertException,ResponseEntityException{
        DesChannel des = get2(DesChannel.class,"id",form.id);
        if (des == null) {
            throw new ResponseEntityException(120, "渠道不存在");
        }
        des.setName(form.name);
        des.setType(form.type);
        des.setZoneName(form.zoneName);
        des.setCityName(form.cityName);
        des.setProvinceName(form.provinceName);
        des.setAddress(form.address);
        des.setTelephone(form.telephone);
        return upsert2(des);
    }

    /**
     * @Description:设计师渠道列表
     * @Author: hanchao
     * @Date: 2018/2/5 0005
     */
    @Cacheable(value = {"DesChannel"})
    public PageList<DesChannel> findDesChannelList(PageQuery pageQuery,String name, String telephone) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(name))
            cri = Restrictions.and(cri, Restrictions.like("name", name, MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(telephone))
            cri = Restrictions.and(cri, Restrictions.like("telephone", telephone, MatchMode.ANYWHERE));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "createdDate";
        PageList<DesChannel> list = hibernateReadonlyRepository.getList(DesChannel.class, pageQuery);
        return list;
    }

    /**
     * @Description:设计师渠道新增
     * @Author: hanchao
     * @Date: 2018/2/5 0005
     */
    @CacheEvict(value = {"DesChannel"}, allEntries = true)
    public DesChannel channelDesAdd(DesAddForm form) throws UpsertException,ResponseEntityException{
        DesChannel des = new DesChannel();
        des.setName(form.name);
        des.setType(form.type);
        des.setZoneName(form.zoneName);
        des.setCityName(form.cityName);
        des.setProvinceName(form.provinceName);
        des.setAddress(form.address);
        des.setTelephone(form.telephone);
        return upsert2(des);
    }

}
