package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 用户浏览商品记录表
 */
@Entity
@Table(name = "pub_user_viewlog")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="pub_user_viewlog", allocationSize=1)
public class PubUserViewLog extends GenericEntity {

	/**
	 * 用户id
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
	 * 商品库的商品id
	 */
	@Column(name = "pid")
	private Integer pid;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pid", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private PlaProduct plaProduct;

	/**
	 *	排序
	 */
	@Column(name = "sort")
	private Double sort;


	public Double getSort() {
		return sort;
	}

	public void setSort(Double sort) {
		this.sort = sort;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public PubUserLogin getPubUserLogin() {
		return pubUserLogin;
	}

	public void setPubUserLogin(PubUserLogin pubUserLogin) {
		this.pubUserLogin = pubUserLogin;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public PlaProduct getPlaProduct() {
		return plaProduct;
	}

	public void setPlaProduct(PlaProduct plaProduct) {
		this.plaProduct = plaProduct;
	}
}
