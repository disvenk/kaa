package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 供应商订单商品原材料
 */
@Entity
@Table(name = "sup_sales_order_detail_material")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_sales_order_detail_material", allocationSize=1)
public class SupSalesOrderDetailMaterial extends GenericEntity {

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
	 *  原材料id
	 */
	@Column(name = "material_id")
	private Integer materialId;

	/**
	 *  原材料名称
	 */
	@Column(name = "material_name")
	private String materialName;

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

	public Integer getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
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
