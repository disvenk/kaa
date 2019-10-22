package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 供应商基础资料
 */
@Entity
@Table(name = "sup_product_base")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_product_base", allocationSize=1)
public class SupProductBase extends GenericEntity {



	/**
	 * 供应商ID
	 */
	@Column(name = "suplier_id")
	private Integer suplierId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "suplier_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupSuplier supSuplier;


	/**
	 * 名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 基础资料类型
	 * 		1：商品分类
	 * 		2：商品颜色
	 * 		3：商品尺寸
	 * 		4：原材料
	 */
	@Column(name = "type")
	private Integer type;

	/**
	 * 排序
	 */
	@Column(name = "sort")
	private Double sort;

	/**
	 * 描述
	 */
	@Column
	private String remarks;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Double getSort() {
		return sort;
	}

	public void setSort(Double sort) {
		this.sort = sort;
	}

	public Integer getSuplierId() {
		return suplierId;
	}

	public void setSuplierId(Integer suplierId) {
		this.suplierId = suplierId;
	}

	public SupSuplier getSupSuplier() {
		return supSuplier;
	}

	public void setSupSuplier(SupSuplier supSuplier) {
		this.supSuplier = supSuplier;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
