package com.xxx.admin.service;


import com.alibaba.fastjson.JSONArray;
import com.xxx.admin.form.DesignerSaveForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.DesDesigner;
import com.xxx.model.business.PubUserBase;
import com.xxx.model.business.PubUserLogin;
import com.xxx.user.service.UploadFileService;
import com.xxx.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DesignerService extends CommonService {

    @Autowired
    private UploadFileService uploadFileService;

    /**
     * @Description: 获取设计师列表
     * @Author: Chen.zm
     * @Date: 2017/11/22 0022
     */
    @Cacheable(value = {"DesDesigner"})
    public PageList<DesDesigner> designerList(PageQuery pageQuery, String name, String phone) {
        Criterion cri = Restrictions.eq("logicDeleted", false);//可有可无
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isNotBlank(name)) {
            ExtFilter filter = new ExtFilter("pubUserLogin.pubUserBase.name", "string", name, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(phone)) {
            ExtFilter filter = new ExtFilter("pubUserLogin.pubUserBase.mobile", "string", phone, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.hibernateFetchFields = "pubUserLogin,pubUserLogin.pubUserBase";
        pageQuery.order = "desc";
        pageQuery.sort = "createdDate";
        PageList<DesDesigner> list = hibernateReadonlyRepository.getList(DesDesigner.class, pageQuery);
        return list;
    }

    /**
     * @Description: 设计师详情
     * @Author: Chen.zm
     * @Date: 2017/11/22 0022
     */
    @Cacheable(value = {"DesDesigner"})
    public DesDesigner designerDetail(Integer id) {
        Criterion cri = Restrictions.eq("id", id);
        return (DesDesigner) getCurrentSession().createCriteria(DesDesigner.class)
                .add(cri)
                .setFetchMode("pubUserLogin", FetchMode.JOIN)
                .setFetchMode("pubUserLogin.pubUserBase", FetchMode.JOIN)
                .uniqueResult();
    }

    /**
     * @Description: 修改设计师展示状态
     * @Author: Chen.zm
     * @Date: 2017/11/22 0022
     */
    @CacheEvict(value = {"DesDesigner"}, allEntries = true)
    public void updateIsShow(Integer id, Boolean isShow) throws ResponseEntityException, UpsertException{
        DesDesigner designer = get2(DesDesigner.class, id);
        if (designer == null)
            throw new ResponseEntityException(120, "设计师不存在");
        designer.setShow(isShow);
        upsert2(designer);
    }

    /**
     * @Description: 删除设计师
     * @Author: Chen.zm
     * @Date: 2017/11/22 0022
     */
    @CacheEvict(value = {"DesDesigner"}, allEntries = true)
    public void deleteDesigner(Integer id) throws ResponseEntityException, UpsertException{
        DesDesigner designer = get2(DesDesigner.class, id);
        if (designer == null)
            throw new ResponseEntityException(120, "设计师不存在");
        designer.setLogicDeleted(true);
        upsert2(designer);
    }


    /**
     * @Description: 设计师编辑新增
     * @Author: Chen.zm
     * @Date: 2017/11/22 0022
     */
    @CacheEvict(value = {"DesDesigner"}, allEntries = true)
    public void saveDesigner(DesignerSaveForm form) throws ResponseEntityException, UpsertException{
        DesDesigner designer = new DesDesigner();
        if (form.id != null) {
            designer = get2(DesDesigner.class, form.id);
            if (designer == null)
                throw new ResponseEntityException(120, "设计师不存在");
        }
        designer.setType(form.type);
        designer.setResume(form.resume);
        designer.setRemarks(form.remarks);
        designer.setShow(form.isShow);
        designer.setProvince(form.province);
        designer.setProvinceName(form.provinceName);
        designer.setCity(form.city);
        designer.setCityName(form.cityName);
        designer.setZone(form.zone);
        designer.setZoneName(form.zoneName);
        designer.setAddress(form.address);
        designer.setDescription(form.description);
        designer = upsert2(designer);

        PubUserBase base = designer.getUserId() != null ? (PubUserBase) get2(PubUserBase.class, "userId", designer.getUserId()) : new PubUserBase();
        //创建用户信息
        if (designer.getUserId() == null || base == null) {
            PubUserLogin login = new PubUserLogin();
            login.setLoginType(2);
            login.setRelationId(designer.getId());
            login.setUserCode(form.mobile);
            login.setUserPassword(MD5Utils.md5Hex("888888"));
            login.setUseable(true);
            login = upsert2(login);

            designer.setUserId(login.getId());
            upsert2(designer);

            base = new PubUserBase();
            base.setUserId(login.getId());
        }
        if (StringUtils.isNotBlank(form.icon)) {
            base.setIcon(uploadFileService.saveOssUploadFileByBase64(form.icon).toString());
        }
        base.setName(form.receiver);
        base.setMobile(form.mobile);
        base.setSex(form.sex);
        upsert2(base);
    }

}
