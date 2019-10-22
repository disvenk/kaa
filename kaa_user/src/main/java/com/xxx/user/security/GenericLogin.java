package com.xxx.user.security;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;

import java.io.Serializable;

/**
 * 说明:登录信息
 */
public class GenericLogin implements Serializable  {
	public int userId;
	public int loginType;                 //0：平台人员   1： 门店   2：设计师  3：供应商  4：工人
	public String userName;
	public int userStatus;               //是否关联对于账户(loginType对于的账户是否存在)   0：正常   1：账户不存在(未提交审核)   2:审核中  3：审核失败
	public boolean isExperience = false;   //是否体验账号
//	public Integer workerType;   //目前仅用于供应商或工人的权限字段， 参照SupWorker.type字段

	private Integer platId;
	private Integer storeId;
	private Integer designerId;
	private Integer suplierId;
	private Integer workerId;


	public GenericLogin() {
	}

	public GenericLogin(int userId, int loginType, String userName, int userStatus, boolean isExperience, Integer platId, Integer storeId, Integer designerId, Integer suplierId, Integer workerId) {
		this.userId = userId;
		this.loginType = loginType;
		this.userName = userName;
		this.userStatus = userStatus;
		this.isExperience = isExperience;
		this.platId = platId;
		this.storeId = storeId;
		this.designerId = designerId;
		this.suplierId = suplierId;
		this.workerId = workerId;
	}

	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		json.put("userId", userId);
		json.put("loginType", loginType);
		json.put("userName", userName);
		json.put("userStatus", userStatus);
		json.put("isExperience", isExperience);
		json.put("platId", platId);
		json.put("storeId", storeId);
		json.put("designerId", designerId);
		json.put("suplierId", suplierId);
		json.put("workerId", workerId);
		return json.toJSONString();
	}

	public Integer getPlatId() throws ResponseEntityException {
		if (platId == null)
			throw new ResponseEntityException(300, "需要平台角色");
//		CommonService commonServic =(CommonService) SpringContext.getApplicationContext().getBean("commonService");
//		Brand brand = commonServic.get2(Brand.class,brandId);
//
//        if(!manageUserName.equals(brand.getManagerPhone()))
//            throw new ResponseEntityException(300, "权限变更，请重新登录");
		return platId;
	}


	public Integer getStoreId() throws ResponseEntityException {
		if (storeId == null)
			throw new ResponseEntityException(300, "需要门店角色");
		return storeId;
	}

	public Integer getDesignerId() throws ResponseEntityException {
		if (designerId == null)
			throw new ResponseEntityException(300, "需要设计师角色");
		return designerId;
	}

	public Integer getSuplierId() throws ResponseEntityException {
		if (suplierId == null)
			throw new ResponseEntityException(300, "需要供应商角色");
		return suplierId;
	}

	public Integer getWorkerId() throws ResponseEntityException {
		if (workerId == null)
			throw new ResponseEntityException(300, "需要工人角色");
		return workerId;
	}

	public Integer getLoginType() throws ResponseEntityException {
		return loginType;
	}

	public void setPlatId(Integer platId) {
		this.platId = platId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public void setDesignerId(Integer designerId) {
		this.designerId = designerId;
	}

	public void setSuplierId(Integer suplierId) {
		this.suplierId = suplierId;
	}

	public void setWorkerId(Integer workerId) {
		this.workerId = workerId;
	}
}