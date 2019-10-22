package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 供应商订单商品工序
 */
@Entity
@Table(name = "sup_sales_order_detail_procedure")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_sales_order_detail_procedure", allocationSize=1)
public class SupSalesOrderDetailProcedure extends GenericEntity {

	/**
	 *	订单详情id
	 */
	@Column(name = "detail_id")
	private Integer detailId;


	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "detail_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupSalesOrderDetail supSalesOrderDetail;

	/**
	 * 工序id
	 */
	@Column(name = "procedure_id")
	private Integer procedureId;

	/**
	 * 工序名称
	 */
	@Column(name = "procedure_name")
	private String procedureName;

	/**
	 * 工序工价
	 */
	@Column(name = "price")
	private Double price;

	public Integer getDetailId() {
		return detailId;
	}

	public void setDetailId(Integer detailId) {
		this.detailId = detailId;
	}

	public SupSalesOrderDetail getSupSalesOrderDetail() {
		return supSalesOrderDetail;
	}

	public void setSupSalesOrderDetail(SupSalesOrderDetail supSalesOrderDetail) {
		this.supSalesOrderDetail = supSalesOrderDetail;
	}

	public Integer getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(Integer procedureId) {
		this.procedureId = procedureId;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
}
