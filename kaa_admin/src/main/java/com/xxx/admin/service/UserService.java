package com.xxx.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.UserSaveForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.PubUserBase;
import com.xxx.model.business.PubUserLogin;
import com.xxx.model.system.SYS_Role;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService extends CommonService {

    /**
     * @Description:获取用户列表信息
     * @Author: disvenk.dai
     * @Date: 2018/1/3
     */
    @Cacheable(value = {"PubUserLogin"})
    public PageList<PubUserLogin> getUserListAll(PageQuery pageQuery) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        cri = Restrictions.and(cri,Restrictions.eq("loginType",0));
        cri = Restrictions.and(cri,Restrictions.eq("createdBy", CurrentUser.get().userId));
        pageQuery.hibernateCriterion = cri;
//        pageQuery.hibernateFetchFields = "plaProductCategory";
        PageList<PubUserLogin> list = hibernateReadonlyRepository.getList(PubUserLogin.class, pageQuery);

        return list;
    }

    /**
     * @Description:新增用户与编辑
     * @Author: disvenk.dai
     * @Date: 2018/1/6
     */
    @CacheEvict(value="PubUserLogin",allEntries = true)
    public ResponseEntity newUserddManage(UserSaveForm form) throws UpsertException {
        PubUserLogin pubUserLogin = null;
        PubUserBase pubUserBase = null;
        if(form.id!=null){
            pubUserLogin = get2(PubUserLogin.class,"id",form.id);
            if(!pubUserLogin.getUserCode().equals(form.userName)){
                if(get2(pubUserLogin.getClass(),"userCode",form.userName)!=null)return new ResponseEntity(new RestResponseEntity(110, "用户名已存在",null) , HttpStatus.OK);
            }
             pubUserBase = pubUserLogin.getPubUserBase();
           if(pubUserBase==null){
               pubUserBase=new PubUserBase();
               pubUserBase.setMobile(form.tel);
               pubUserBase.setName(form.relName);
           }else {
               pubUserBase.setMobile(form.tel);
               pubUserBase.setName(form.relName);
           }
        }else {
            if(get2(PubUserLogin.class,"userCode",form.userName)!=null)return new ResponseEntity(new RestResponseEntity(110, "用户名已存在",null) , HttpStatus.OK);
            pubUserLogin = new PubUserLogin();
            pubUserBase = new PubUserBase();
            pubUserBase.setMobile(form.tel);
            pubUserLogin.setUseable(true);
            pubUserBase.setName(form.relName);

        }

        pubUserLogin.setLoginType(0);
        pubUserLogin.setUserCode(form.userName);
        if(form.id!=null && form.password!= null && StringUtils.isNotBlank(form.password)){
            pubUserLogin.setUserPassword(MD5Utils.md5Hex(form.password));
        }else if(form.id==null){
            pubUserLogin.setUserPassword(MD5Utils.md5Hex(form.password));
        }

        Set<SYS_Role> sys_roles = new HashSet<>();
        if(form.roleStr!=null && StringUtils.isNotBlank(form.roleStr)) {
            String[] roleIds = form.roleStr.split(",");
            for (String roleId : roleIds) {
                SYS_Role sys_role = get2(SYS_Role.class, "id", Integer.parseInt(roleId));
                sys_roles.add(sys_role);
            }
        }
        pubUserLogin.setRoleList( sys_roles);
        PubUserLogin pubUserLogin1 = upsert2(pubUserLogin);
        pubUserBase.setUserId(pubUserLogin1.getId());
          upsert2(pubUserBase);

        return  new ResponseEntity(new RestResponseEntity(100, "成功",null) , HttpStatus.OK);
    }

    /**
     * @Description:用户编辑详情
     * @Author: disvenk.dai
     * @Date: 2018/1/4
     */
    @Cacheable(value = {"PubUserLogin"})
    public JSONObject checkUser(Integer id) throws ResponseEntityException {
        PubUserLogin pubUserLogin = get2(PubUserLogin.class,"id", id);
        if (pubUserLogin == null) {
            throw new ResponseEntityException(210, "用户不存在");
        }
        JSONObject json = new JSONObject();
        json.put("id", pubUserLogin.getId());
        json.put("tel", pubUserLogin.getPubUserBase() == null ? "" : pubUserLogin.getPubUserBase().getMobile());
        json.put("userName",pubUserLogin.getUserCode());
        json.put("relName",pubUserLogin.getPubUserBase()==null?"":pubUserLogin.getPubUserBase().getName());
        JSONArray data = new JSONArray();
        Set<SYS_Role> roleList = pubUserLogin.getRoleList();
        if(roleList!=null && roleList.size()!=0){
            for (SYS_Role sys_role: roleList) {
                JSONObject json2 = new JSONObject();
                json2.put("id", sys_role.getId());
                json2.put("name",sys_role.getName());
                data.add(json2);
            }
            json.put("role",data);
        }



        return json;
    }

    /**
     * @Description:用户删除
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @CacheEvict(value = "PubUserLogin",allEntries = true)
    public void deleteUser(UserSaveForm form){
        PubUserLogin pubUserLogin = get2(PubUserLogin.class,"id",form.id);
        pubUserLogin.setRoleList(null);
        Session session =getCurrentSession();
        if(pubUserLogin!=null)
            session.delete(pubUserLogin);

    }

    /**
     * @Description:密码重置
     * @Author: disvenk.dai
     * @Date: 2018/1/10
     */
    @CacheEvict(value = "ExpUser",allEntries = true)
    public void resetUserPass(UserSaveForm form) throws UpsertException {
        PubUserLogin pubUserLogin = get2(PubUserLogin.class,"id",form.id);
        pubUserLogin.setUserPassword(MD5Utils.md5Hex("888888"));
        upsert2(pubUserLogin);

    }
}
