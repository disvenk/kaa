package com.xxx.model.business;

import com.xxx.core.entity.GenericEntity;

import javax.persistence.*;

/**
 * 销售平台首页：视频区信息
 */
@Entity
@Table(name = "sal_vedioInfo")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sal_vedioInfo", allocationSize=1)
public class SalesVedioInfo extends GenericEntity {

	/**
	 * 类型：1 主播推荐  2 新娘课堂  3 体验师
	 */
	@Column(name = "vedio_type")
	private Integer vedioType;

	/**
	 * 名称
	 */
	@Column(name = "title")
	private String title;

	/**
	 * 主图：只能上传一张
	 */
	@Column(name = "pictureUrl")
	private String pictureUrl;

	/**
	 * 简介
	 */
	@Column(name = "short_description")
	private String shortDescription;

	/**
	 * 浏览量
	 */
	@Column(name = "views")
	private Integer views;

	/**
	 * 图文详情
	 */
	@Column(name = "description")
	private String description;

	/**
	 * 视频
	 */
	@Column(name = "vedioUrl")
	private String vedioUrl;


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

	public Integer getVedioType() {
		return vedioType;
	}

	public void setVedioType(Integer vedioType) {
		this.vedioType = vedioType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVedioUrl() {
		return vedioUrl;
	}

	public void setVedioUrl(String vedioUrl) {
		this.vedioUrl = vedioUrl;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

}
