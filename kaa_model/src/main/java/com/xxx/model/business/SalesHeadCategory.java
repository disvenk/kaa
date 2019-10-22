package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.xxx.core.entity.GenericEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 电商首页楼层类别维护
 */
@Entity
@Table(name = "sal_headcategory")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sal_headcategory", allocationSize=1)
public class SalesHeadCategory extends GenericEntity {

	/**
	 *	类别名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 *  描述
	 */
	@Column(name = "description")
	private String description;

	/**
	 * 排序
	 */
	@Column(name = "sort")
	private Integer sort;

	/**
	 * 是否展示
	 * 		0:不展示
	 * 		1:展示
	 */
	@Column(name = "status")
	private Integer status;

	/**
	 * 商品集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "salesHeadCategory")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<SalesHeadCategoryProduct> salesHeadCategoryProductList = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public List<SalesHeadCategoryProduct> getSalesHeadCategoryProductList() {
		return salesHeadCategoryProductList;
	}

	public void setSalesHeadCategoryProductList(List<SalesHeadCategoryProduct> salesHeadCategoryProductList) {
		this.salesHeadCategoryProductList = salesHeadCategoryProductList;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
