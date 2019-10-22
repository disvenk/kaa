package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 供应商订单详情对应商品的图片集
 */
@Entity
@Table(name = "sup_sales_order_detail_picture")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_sales_order_detail_picture", allocationSize=1)
public class SupSalesOrderDetailPicture extends GenericEntity {

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
	 *  商品图片地址
	 */
	@Column(name = "href")
	private String href;

	/**
	 *  排序
	 */
	@Column(name = "sort")
	private Double sort;

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

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Double getSort() {
		return sort;
	}

	public void setSort(Double sort) {
		this.sort = sort;
	}
}
