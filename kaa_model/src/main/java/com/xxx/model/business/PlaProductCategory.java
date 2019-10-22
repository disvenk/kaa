package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品所有分类及子分类
 */
@Entity
@Table(name = "pla_product_category")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="pla_product_category", allocationSize=1)
public class PlaProductCategory extends GenericEntity {


	/**
	 * 商品分类名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 供应商周期(天)
	 */
	@Column(name = "supplierday")
	private String supplierDay;

	/**
	 * 排序
	 */
	@Column(name = "sort")
	private Integer sort;

	/**
	 * 商品父分类ID  (同表关联)
	 */
	@Column(name = "parent_id")
	private Integer parentId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private PlaProductCategory plaProductCategory;

	/**
	 * 分类编码：一级分类：00  +  二级分类：000, +  商品流水码：00000  == 商品编码： 0100100001
	 */

	@Column(name = "categoryCode")
	private String categoryCode;

	/**
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;

	/**
	 * 子分类集合
	 */
	@JSONField(serialize = false)
	@Where(clause = "logicDeleted=0")
	@OneToMany(mappedBy = "plaProductCategory")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<PlaProductCategory> children = new ArrayList<>();

	public String getSupplierDay() {
		return supplierDay;
	}

	public void setSupplierDay(String supplierDay) {
		this.supplierDay = supplierDay;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public PlaProductCategory getPlaProductCategory() {
		return plaProductCategory;
	}

	public void setPlaProductCategory(PlaProductCategory plaProductCategory) {
		this.plaProductCategory = plaProductCategory;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<PlaProductCategory> getChildren() {
		return children;
	}

	public void setChildren(List<PlaProductCategory> children) {
		this.children = children;
	}
}
