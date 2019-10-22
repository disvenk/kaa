package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 盒子使用记录 关联商品
 */
@Entity
@Table(name = "box_use_log_product")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="box_use_log_product", allocationSize=1)
public class BoxUseLogProduct extends GenericEntity {

	/**
	 * 盒子使用记录Id
	 */
	@Column(name = "box_use_log_id")
	private Integer boxUseLogId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "box_use_log_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private BoxUseLog boxUseLog;


	/**
	 * 盒子商品Id
	 */
	@Column(name = "box_product_id")
	private Integer boxProductId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "box_product_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private BoxProduct boxProduct;

	/**
	 * 状态  0：未支付    1：已支付
	 */
	@Column(name = "status")
	private Integer status = 0;

	/**
	 * 商品名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 *  商品主图地址
	 */
	@Column(name = "href")
	private String href;

	/**
	 * 单价
	 */
	@Column(name = "price")
	private Double price;

	/**
	 * 数量
	 */
	@Column(name = "count")
	private Integer count;


	public Integer getBoxUseLogId() {
		return boxUseLogId;
	}

	public void setBoxUseLogId(Integer boxUseLogId) {
		this.boxUseLogId = boxUseLogId;
	}

	public BoxUseLog getBoxUseLog() {
		return boxUseLog;
	}

	public void setBoxUseLog(BoxUseLog boxUseLog) {
		this.boxUseLog = boxUseLog;
	}

	public Integer getBoxProductId() {
		return boxProductId;
	}

	public void setBoxProductId(Integer boxProductId) {
		this.boxProductId = boxProductId;
	}

	public BoxProduct getBoxProduct() {
		return boxProduct;
	}

	public void setBoxProduct(BoxProduct boxProduct) {
		this.boxProduct = boxProduct;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}
