package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 盒子服务记录
 */
@Entity
@Table(name = "box_operate_log")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="box_operate_log", allocationSize=1)
public class BoxOperateLog extends GenericEntity {

	/**
	 * 门店ID
	 */
	@Column(name = "store_id")
	private Integer storeId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private StoStoreInfo stoStoreInfo;

	/**
	 * 记录名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 支付金额
	 */
	@Column(name = "price")
	private Double price;

	/**
	 * 剩余押金
	 */
	@Column(name = "deposit")
	private Double deposit;

	/**
	 * 剩余次数
	 */
	@Column(name = "count")
	private Integer count;

	/**
	 * 剩余有效期
	 */
	@Column(name = "term_time")
	private Date termTime;


	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public StoStoreInfo getStoStoreInfo() {
		return stoStoreInfo;
	}

	public void setStoStoreInfo(StoStoreInfo stoStoreInfo) {
		this.stoStoreInfo = stoStoreInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getDeposit() {
		return deposit;
	}

	public void setDeposit(Double deposit) {
		this.deposit = deposit;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Date getTermTime() {
		return termTime;
	}

	public void setTermTime(Date termTime) {
		this.termTime = termTime;
	}
}
