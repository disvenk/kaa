package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 供应商的规格价格表
 */
@Entity
@Table(name = "sup_product_price")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_product_price", allocationSize=1)
public class SupProductPrice extends GenericEntity {

	/**
	 *	商品ID
	 */
	@Column(name = "pid")
	private Integer pid;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pid", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupProduct supProduct;

	/**
	 * 颜色id
	 */
	@Column(name = "color_id")
	private Integer colorId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "color_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupProductBase colorBase;

	/**
	 * 尺寸id
	 */
	@Column(name = "size_id")
	private Integer sizeId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "size_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupProductBase sizeBase;

//	/**
//	 * 颜色
//	 */
//	@Column(name = "color")
//	private String color;
//
//	/**
//	 * 尺寸
//	 */
//	@Column(name = "size")
//	private String size;

//	/**
//	 *  库存 【弃用
//	 */
//	@Column(name = "stock")
//	private Integer stock;
//
//	/**
//	 * 价格  【弃用
//	 */
//	@Column(name = "price")
//	private Double price;
//
//	/**
//	 *  线上销售价格  【弃用
//	 */
//	@Column(name = "online_price")
//	private Double onlinePrice;
//
//	/**
//	 *  线下销售价格 【弃用
//	 */
//	@Column(name = "offline_price")
//	private Double offlinePrice;

	/**
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public SupProduct getSupProduct() {
		return supProduct;
	}

	public void setSupProduct(SupProduct supProduct) {
		this.supProduct = supProduct;
	}

	public Integer getColorId() {
		return colorId;
	}

	public void setColorId(Integer colorId) {
		this.colorId = colorId;
	}

	public SupProductBase getColorBase() {
		return colorBase;
	}

	public void setColorBase(SupProductBase colorBase) {
		this.colorBase = colorBase;
	}

	public Integer getSizeId() {
		return sizeId;
	}

	public void setSizeId(Integer sizeId) {
		this.sizeId = sizeId;
	}

	public SupProductBase getSizeBase() {
		return sizeBase;
	}

	public void setSizeBase(SupProductBase sizeBase) {
		this.sizeBase = sizeBase;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


}
