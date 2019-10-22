package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 平台的商品详情表
 */
@Entity
@Table(name = "sal_product_description")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sal_product_description", allocationSize=1)
public class SalesProductDescription extends GenericEntity {

	/**
	 * 商品id
	 */
	@Column(name = "pid")
	private Integer pid;

//	@JSONField
//	@JsonIgnore
//	@NotFound(action= NotFoundAction.IGNORE)
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "pid", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
//	private SalesProduct salesProduct;

	/**
	 * 商品详情
	 */
	@Column(name = "description")
	private String description;

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

//	public SalesProduct getSalesProduct() {
//		return salesProduct;
//	}
//
//	public void setSalesProduct(SalesProduct salesProduct) {
//		this.salesProduct = salesProduct;
//	}
}
