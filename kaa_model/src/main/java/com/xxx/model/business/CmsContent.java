package com.xxx.model.business;

import com.xxx.core.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * CMS内容管理
 */
@Entity
@Table(name = "cms_content")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="cms_content", allocationSize=1)
public class CmsContent extends GenericEntity {

	/**
	 *	标题
	 */
	@Column(name = "title")
	private String title;

	/**
	 *  标签
	 */
	@Column(name = "contentlabel")
	private String contentLabel;

	/**
	 *  内容
	 */
	@Column(name = "content")
	private String content;

	/**
	 *	菜单ID
	 */
	@Column(name = "menu_id")
	private String menuId;

	/**
	 *	排序
	 */
	@Column(name = "sort")
	private Double sort;

	/**
	 * 是否展示
	 */
	@Column(name = "is_show")
	private Boolean isShow;

	public Boolean getShow() {
		return isShow;
	}

	public void setShow(Boolean show) {
		isShow = show;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContentLabel() {
		return contentLabel;
	}

	public void setContentLabel(String contentLabel) {
		this.contentLabel = contentLabel;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public Double getSort() {
		return sort;
	}

	public void setSort(Double sort) {
		this.sort = sort;
	}
}
