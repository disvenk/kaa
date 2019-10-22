package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * banner位商品管理
 */
@Entity
@Table(name = "sal_banner_product")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sal_banner_product", allocationSize=1)
public class SalesBannerProduct extends GenericEntity {


	/**
	 *	banner位id
	 */
	@Column(name = "banner_id")
	private Integer bannerId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "banner_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SalesBanner salesBanner;

	/**
	 *  商品id
	 */
	@Column(name = "product_id")
	private Integer productId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SalesProduct salesProduct;

	/**
	 * 排序
	 */
	@Column(name = "sort")
	private Integer sort;

	public Integer getBannerId() {
		return bannerId;
	}

	public void setBannerId(Integer bannerId) {
		this.bannerId = bannerId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public SalesProduct getSalesProduct() {
		return salesProduct;
	}

	public void setSalesProduct(SalesProduct salesProduct) {
		this.salesProduct = salesProduct;
	}

	public SalesBanner getSalesBanner() {
		return salesBanner;
	}

	public void setSalesBanner(SalesBanner salesBanner) {
		this.salesBanner = salesBanner;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
}
