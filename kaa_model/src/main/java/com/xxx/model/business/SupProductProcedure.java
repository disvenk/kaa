package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 供应商商品工序
 */
@Entity
@Table(name = "sup_product_procedure")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_product_procedure", allocationSize=1)
public class SupProductProcedure extends GenericEntity {

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
	 * 工序id
	 */
	@Column(name = "procedure_id")
	private Integer procedureId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "procedure_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupProcedure supProcedure;

	/**
	 * 工序工价
	 */
	@Column(name = "price")
	private Double price;

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

	public Integer getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(Integer procedureId) {
		this.procedureId = procedureId;
	}

	public SupProcedure getSupProcedure() {
		return supProcedure;
	}

	public void setSupProcedure(SupProcedure supProcedure) {
		this.supProcedure = supProcedure;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
}
