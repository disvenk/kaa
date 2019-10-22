package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 首页分类相关商品维护
 */
@Entity
@Table(name = "sal_headcategory_product")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sal_headcategory_product", allocationSize=1)
public class SalesHeadCategoryProduct extends GenericEntity {


	/**
	 *	首页楼层类别维护Id
	 */
	@Column(name = "headcategory_id")
	private Integer headcategoryId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "headcategory_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SalesHeadCategory salesHeadCategory;

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

	public Integer getHeadcategoryId() {
		return headcategoryId;
	}

	public void setHeadcategoryId(Integer headcategoryId) {
		this.headcategoryId = headcategoryId;
	}

	public SalesHeadCategory getSalesHeadCategory() {
		return salesHeadCategory;
	}

	public void setSalesHeadCategory(SalesHeadCategory salesHeadCategory) {
		this.salesHeadCategory = salesHeadCategory;
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

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
}
