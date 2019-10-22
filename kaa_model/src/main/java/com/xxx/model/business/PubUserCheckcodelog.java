package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户资料修改验证码记录
 */
@Entity
@Table(name = "pub_user_checkcodelog")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="pub_user_checkcodelog", allocationSize=1)
public class PubUserCheckcodelog extends GenericEntity {


	/**
	 * 类型
	 * 		0：用户注册时
	 * 		1：用户资料修改时
	 * 		2：用户找回密码时
	 * 		3：忘记密码
	 */
	@Column(name = "usetype")
	private Integer usetype;

	/**
	 * 用户账号ID
	 */
	@Column(name = "user_id")
	private Integer userId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private PubUserLogin pubUserLogin;

	/**
	 * 手机号
	 */
	@Column(name = "mobile")
	private String mobile;

	/**
	 * 验证码
	 */
	@Column(name = "checkcode")
	private String checkcode;

	/**
	 * 验证码发送时间
	 */
	@Column(name = "receivetime")
	private Date receivetime;

	/**
	 * 失效时间
	 */
	@Column(name = "expire")
	private Date expire;

	public PubUserLogin getPubUserLogin() {
		return pubUserLogin;
	}

	public void setPubUserLogin(PubUserLogin pubUserLogin) {
		this.pubUserLogin = pubUserLogin;
	}

	public Integer getUsetype() {
		return usetype;
	}

	public void setUsetype(Integer usetype) {
		this.usetype = usetype;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCheckcode() {
		return checkcode;
	}

	public void setCheckcode(String checkcode) {
		this.checkcode = checkcode;
	}

	public Date getReceivetime() {
		return receivetime;
	}

	public void setReceivetime(Date receivetime) {
		this.receivetime = receivetime;
	}

	public Date getExpire() {
		return expire;
	}

	public void setExpire(Date expire) {
		this.expire = expire;
	}
}
