package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import com.xxx.model.system.SYS_Role;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.*;

/**
 * 平台的所有账号管理
 */
@Entity
@Table(name = "pub_user_login")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="pub_user_login", allocationSize=1)
public class PubUserLogin extends GenericEntity {


	/**
	 *	账户登录入口
	 * 		0：内部平台入口   平台人员
	 * 		1：销售平台入口   门店
	 * 		2：设计师入口     设计师
	 * 		3：供应商入口     供应商
	 * 		4：工人入口       供应商工人
	 */
	@Column(name = "logintype")
	private Integer loginType;

	/**
	 *  根据logintype关联具体的信息     门店ID/供应商ID/平台人员ID/设计师ID/工人ID
	 */
	@Column(name = "relation_id")
	private Integer relationId;

	@JSONField
	@JsonIgnore
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id",referencedColumnName="user_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private PubUserBase pubUserBase;

	/**
	 * 收件地址列表
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "pubUserLogin")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<PubUserAddress> pubUserAddressDetailList = new ArrayList<>();

	/**
	 * 角色类型
	 * 	 	0：平台人员
	 * 		1：网销店
	 * 		2：零售门店
	 * 		3：结婚领域流量平台
	 * 		4：婚庆服务行业
	 * 		5：个人用户2C
	 */
	@Column(name = "user_type")
	private Integer userType;

	/**
	 * 登录用户名
	 */
	@Column(name = "user_code")
	private String userCode;

	/**
	 * 密码
	 */
	@Column(name = "user_password")
	private String userPassword;

	/**
	 * 最后登录时间
	 */
	@Column(name = "final_logindate")
	private Date finalLogindate;

	/**
	 * 最后一次登录的上一次登录时间
	 */
	@Column(name = "last_logindate")
	private Date lastLogindate;

	/**
	 * 最后登录ip
	 */
	@Column(name = "finallogin_ip")
	private String finalloginIp;

	/**
	 * 是否可用
	 */
	@Column(name = "useable")
	private Boolean useable;

	//角色集合
	@JSONField(serialize = false)
	@Fetch(FetchMode.SUBSELECT)
	@org.hibernate.annotations.ForeignKey(name = "none")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "PUB_User_Role",
			joinColumns = @JoinColumn(name = "userId", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id")
	)
	private Set<SYS_Role> roleList = new LinkedHashSet();

	public Set<SYS_Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(Set<SYS_Role> roleList) {
		this.roleList = roleList;
	}

	public List<PubUserAddress> getPubUserAddressDetailList() {
		return pubUserAddressDetailList;
	}

	public void setPubUserAddressDetailList(List<PubUserAddress> pubUserAddressDetailList) {
		this.pubUserAddressDetailList = pubUserAddressDetailList;
	}

	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	public Integer getRelationId() {
		return relationId;
	}

	public void setRelationId(Integer relationId) {
		this.relationId = relationId;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public Date getFinalLogindate() {
		return finalLogindate;
	}

	public void setFinalLogindate(Date finalLogindate) {
		this.finalLogindate = finalLogindate;
	}

	public Date getLastLogindate() {
		return lastLogindate;
	}

	public void setLastLogindate(Date lastLogindate) {
		this.lastLogindate = lastLogindate;
	}

	public String getFinalloginIp() {
		return finalloginIp;
	}

	public void setFinalloginIp(String finalloginIp) {
		this.finalloginIp = finalloginIp;
	}

	public Boolean getUseable() {
		return useable;
	}

	public void setUseable(Boolean useable) {
		this.useable = useable;
	}

	public PubUserBase getPubUserBase() {
		return pubUserBase;
	}

	public void setPubUserBase(PubUserBase pubUserBase) {
		this.pubUserBase = pubUserBase;
	}

}
