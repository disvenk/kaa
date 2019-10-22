package com.xxx.model.business;

import com.xxx.core.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 帮助中心菜单设置
 */
@Entity
@Table(name = "cms_menu")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="cms_menu", allocationSize=1)
public class CmsMenu extends GenericEntity {

	/**
	 *	菜单名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 *  父菜单id
	 */
	@Column(name = "parent_id")
	private Integer parentId;

	/**
	 *  地址路径
	 */
	@Column(name = "herf")
	private String herf;

	/**
	 *	排序
	 */
	@Column(name = "sort")
	private Double sort;

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

	public String getHerf() {
		return herf;
	}

	public void setHerf(String herf) {
		this.herf = herf;
	}

	public Double getSort() {
		return sort;
	}

	public void setSort(Double sort) {
		this.sort = sort;
	}
}
