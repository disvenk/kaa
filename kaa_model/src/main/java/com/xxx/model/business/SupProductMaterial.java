package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 供应商商品原材料
 */
@Entity
@Table(name = "sup_product_material")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_product_material", allocationSize=1)
public class SupProductMaterial extends GenericEntity {

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
	 *  原材料
	 */
	@Column(name = "material_id")
	private Integer materialId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "material_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupProductBase materialBase;

	/**
	 * 价格
	 */
	@Column(name = "price")
	private Double price;

	/**
	 * 数量
	 */
	@Column(name = "count")
	private Double count;

	/**
	 * 单位
	 */
	@Column(name = "unit")
	private String unit;

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

	public Integer getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}

	public SupProductBase getMaterialBase() {
		return materialBase;
	}

	public void setMaterialBase(SupProductBase materialBase) {
		this.materialBase = materialBase;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getCount() {
		return count;
	}

	public void setCount(Double count) {
		this.count = count;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
