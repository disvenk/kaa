package com.xxx.model.business;

import com.xxx.core.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 电商平台Banner位管理
 */
@Entity
@Table(name = "sal_banner")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sal_banner", allocationSize=1)
public class SalesBanner extends GenericEntity {


	/**
	 *	banner名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 *  banner图片地址
	 */
	@Column(name = "picaddress")
	private String picaddress;

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
	 * 描述
	 */
	@Column(name = "description")
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicaddress() {
		return picaddress;
	}

	public void setPicaddress(String picaddress) {
		this.picaddress = picaddress;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
