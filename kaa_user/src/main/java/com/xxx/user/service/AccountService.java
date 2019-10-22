package com.xxx.user.service;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.model.business.*;
import com.xxx.user.dao.AccountDao;
import com.xxx.user.security.GenericLogin;
import com.xxx.core.service.CommonService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AccountService extends CommonService {

    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private AccountDao accountDao;

    /**
     * @Description: 刷新最后一次登录时间
     * @Author: Chen.zm
     * @Date: 2017/11/24 0024
     */
    @CacheEvict(value = {"PubUserLogin"}, allEntries = true)
    public void updateFinalLoginDate(PubUserLogin userLogin){
        try {
            userLogin.setLastLogindate(userLogin.getFinalLogindate());
            userLogin.setFinalLogindate(new Date());
            upsert2(userLogin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户登录
     * @param usercode
     * @param password
     * @return
     */
    @Cacheable(value = {"PubUserLogin,DesDesigner,StoStoreInfo,SupSuplier"})
    public GenericLogin processMerchantLogin(String usercode, String password, Integer loginType) throws Exception {
//        Criterion cri = Restrictions.eq("loginType", loginType);
//        cri = Restrictions.and(cri, Restrictions.eq("useable", true));
//        cri = Restrictions.and(cri, Restrictions.or(Restrictions.eq("userCode", usercode), Restrictions.eq("p.mobile", usercode)));
//        cri = Restrictions.and(cri, Restrictions.eq("userPassword", password));
//        PubUserLogin userLogin = (PubUserLogin) getCurrentSession().createCriteria(PubUserLogin.class)
//                .createAlias("pubUserBase", "p", JoinType.LEFT_OUTER_JOIN)
//                .add(cri)
//                .uniqueResult();

        PubUserLogin userLogin = getPubUserLogin(usercode, usercode, loginType);
        if (userLogin != null && !password.equals(userLogin.getUserPassword())) {
//            throw new ResponseEntityException(200, "用户名或密码不正确");
            throw new ResponseEntityException(200, "密码不正确");
        }
        if (userLogin != null && !userLogin.getUseable()) {
            throw new ResponseEntityException(220, "账户已被禁用");
        }

        //供应商登录时， 若找不到用户，默认再次查询工人用户
        if (loginType == 3 && userLogin == null) {
            return processMerchantLogin(usercode, password, 4);
        }
        if (userLogin == null)
            throw new ResponseEntityException(200, "账户不存在");
        PubUserBase userBase = get2(PubUserBase.class, "userId", userLogin.getId());
        //得到用户名，如果没有就返回登录名，如果有真是的姓名就返回真实的姓名
        String userName = userBase == null ? userLogin.getUserCode() : userBase.getName();
        // AccountCache.putLoginData(genericLogin.userId, genericLogin.loginType, genericLogin);
        //记录最后登录的时间
        updateFinalLoginDate(userLogin);

        return returnGenericLogin(userLogin, userName);
    }

    public GenericLogin returnGenericLogin(PubUserLogin userLogin, String userName) throws ResponseEntityException{
        Integer platId = null , storeId = null, designerId = null, suplierId = null, workerId = null;
        int userStatus = 0;
//        Integer workerType = 0;
        //名称为空会导致序列化异常
        userName = userName == null ? " " : userName;
        switch (userLogin.getLoginType()) {
            case 0:  platId = userLogin.getRelationId(); break;
            case 1:  storeId = userLogin.getRelationId();
                StoStoreInfo store = get2(StoStoreInfo.class, "userId", userLogin.getId());
                if (store != null && StringUtils.isNotBlank(store.getStoreName())) userName = store.getStoreName();
                if (store == null || store.getStoreStatus() == null) {
                    userStatus = 1; break;
                } else if (store.getStoreStatus() == 0) {
                    userStatus = 2; break;
                } else if (store.getStoreStatus() == 2) {
                    userStatus = 3; break;
                }
                break;
            case 2:  designerId = userLogin.getRelationId(); break;
            case 3:  suplierId = userLogin.getRelationId();
//                workerType = 1;
                SupSuplier suplier = get2(SupSuplier.class, "userId", userLogin.getId());
                if (suplier != null && StringUtils.isNotBlank(suplier.getName())) userName = suplier.getName();
                if (suplier == null) {
                    userStatus = 1; break;
                } else if (suplier.getApproveStatus() == 0) {
                    userStatus = 2; break;
                } else if (suplier.getApproveStatus() == 2) {
                    userStatus = 3; break;
                }
                break;
            case 4: workerId = userLogin.getRelationId();
//                SupWorker worker = get2(SupWorker.class, "phone", userLogin.getUserCode());
                SupWorker worker = get2(SupWorker.class, "id", userLogin.getRelationId());
                if (worker == null || worker.isLogicDeleted())
                    throw new ResponseEntityException(210, "账户不存在");
                suplierId = worker.getSuplierId();
//                workerType = worker.getWorkerType() == 1 ? 1 : 0;
        }
        GenericLogin genericLogin = new GenericLogin(userLogin.getId(), userLogin.getLoginType(), userName, userStatus, false, platId, storeId, designerId, suplierId, workerId);
//        genericLogin.workerType = workerType;
        return genericLogin;
    }

    /**
     * @Description: 注册
     * @Author: Chen.zm
     * @Date: 2017/11/8 0008
     */
    @CacheEvict(value = {"PubUserLogin,PubUserBase"}, allEntries = true)
    public GenericLogin saveRegisterUser(String userCode, String phoneNumber, String password, Integer loginType, Integer userType, String name, Integer relationId) throws UpsertException, ResponseEntityException {
        if (userCode == null) userCode = phoneNumber;
        if (name == null) name = phoneNumber;

        PubUserLogin userLogin = getPubUserLogin(userCode, phoneNumber, loginType);
        if (userLogin != null)
            throw new ResponseEntityException(200, "账户已存在");

        userLogin = new PubUserLogin();
        userLogin.setLoginType(loginType);
        userLogin.setUserType(userType);
        userLogin.setUserCode(userCode);
        userLogin.setUserPassword(password);
        userLogin.setUseable(true);
        userLogin.setRelationId(relationId);
        userLogin = upsert2(userLogin);

        PubUserBase base = new PubUserBase();
        base.setUserId(userLogin.getId());
        base.setName(name);
        base.setMobile(phoneNumber);
        upsert2(base);

        if (userLogin.getLoginType() == 1) { //门店用户注册
            //初始化门店信息
            StoStoreInfo storeInfo = new StoStoreInfo();
            storeInfo.setUserId(userLogin.getId());
            storeInfo = upsert2(storeInfo);
            userLogin.setRelationId(storeInfo.getId());
            upsert2(userLogin);
        } else if (userLogin.getLoginType() == 3) {
//            accountDao.supplierProductInit()
        }
        return returnGenericLogin(userLogin, base.getName());
    }

    /**
     * @Description: 获取用户信息
     * @Author: Chen.zm
     * @Date: 2017/12/19 0019
     */
    @Cacheable(value = {"PubUserLogin"})
    public PubUserLogin getPubUserLogin(String userCode, String phoneNumber, Integer loginType) {
        Criterion cri = Restrictions.eq("loginType", loginType);
        cri = Restrictions.and(cri, Restrictions.or(Restrictions.eq("userCode", userCode), Restrictions.eq("p.mobile", phoneNumber)));
        return (PubUserLogin) getCurrentSession().createCriteria(PubUserLogin.class)
                .createAlias("pubUserBase", "p", JoinType.LEFT_OUTER_JOIN).add(cri).uniqueResult();
    }

    /**
     * 修改密码
     * @param password
     * @throws UpsertException
     */
    @CacheEvict(value = {"PubUserLogin"}, allEntries = true)
    public void updatePassword(Integer userId, String password) throws UpsertException, ResponseEntityException {
        PubUserLogin userLogin = get2(PubUserLogin.class, userId);
        if (userLogin == null)
            throw new ResponseEntityException(200, "账户不存在");
        userLogin.setUserPassword(password);
        upsert2(userLogin);
    }

    /** 修改名称
     * @Description:
     * @Author: Chen.zm
     * @Date: 2017/11/8 0008
     */
    @CacheEvict(value = {"PubUserLogin,PubUserBase"}, allEntries = true)
    public void updateName(Integer userId, String userName) throws UpsertException, ResponseEntityException {
        PubUserLogin userLogin = get2(PubUserLogin.class, userId);
        if (userLogin == null)
            throw new ResponseEntityException(200, "账户不存在");
        PubUserBase base = get2(PubUserBase.class, "userId", userId);
        if (base == null) {
            base = new PubUserBase();
            base.setUserId(userId);
        }
        base.setName(userName);
        upsert2(base);
    }

    /** 修改头像
     * @Description:
     * @Author: Chen.zm
     * @Date: 2017/11/8 0008
     */
    @CacheEvict(value = {"PubUserLogin,PubUserBase"}, allEntries = true)
    public void updateIcon(Integer userId, String icon) throws UpsertException, ResponseEntityException {
        PubUserLogin userLogin = get2(PubUserLogin.class, userId);
        if (userLogin == null)
            throw new ResponseEntityException(200, "账户不存在");
        PubUserBase base = get2(PubUserBase.class, "userId", userId);
        if (base == null) {
            base = new PubUserBase();
            base.setUserId(userId);
        }
        base.setIcon(uploadFileService.saveOssUploadFileByBase64(icon).toString());
        upsert2(base);
    }

    /**
     * @Description: 修改用户信息
     * @Author: Chen.zm
     * @Date: 2017/11/16 0016
     */
    @CacheEvict(value = {"PubUserLogin,PubUserBase"}, allEntries = true)
    public void updateUserBase(Integer userId, String name, Integer sex, String phone, String icon) throws UpsertException, ResponseEntityException {
        PubUserLogin userLogin = get2(PubUserLogin.class, userId);
        if (userLogin == null)
            throw new ResponseEntityException(200, "账户不存在");
        PubUserBase base = get2(PubUserBase.class, "userId", userId);
        if (base == null) {
            base = new PubUserBase();
            base.setUserId(userId);
        }
        if (StringUtils.isNotBlank(name)) base.setName(name);
        if (StringUtils.isNotBlank(phone)) base.setMobile(phone);
        if (sex != null) base.setSex(sex);
        if (StringUtils.isNotBlank(icon)) base.setIcon(uploadFileService.saveOssUploadFileByBase64(icon).toString());
        upsert2(base);
    }


}
