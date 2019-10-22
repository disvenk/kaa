package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 设计师作品
 */
@Entity
@Table(name = "des_works")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="des_works", allocationSize=1)
public class DesWorks extends GenericEntity {

	/**
	 *	作品名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 *  缩略图地址
	 */
	@Column(name = "thumbnail")
	private String thumbnail;

	/**
	 *  首图地址
	 */
	@Column(name = "mainpic")
	private String mainpic;

	/**
	 *	更新日期
	 */
	@Column(name = "lastdate_update")
	private Date lastdateUpdate;

	/**
	 *	是否展示
	 */
	@Column(name = "is_show")
	private Boolean isShow;

	/**
	 *	图文详情
	 */
	@Column(name = "description")
	private String description;

	/**
	 *	所属设计师ID
	 */
	@Column(name = "designer_id")
	private Integer designerId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "designer_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private DesDesigner desDesigner;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getMainpic() {
		return mainpic;
	}

	public void setMainpic(String mainpic) {
		this.mainpic = mainpic;
	}

	public Date getLastdateUpdate() {
		return lastdateUpdate;
	}

	public void setLastdateUpdate(Date lastdateUpdate) {
		this.lastdateUpdate = lastdateUpdate;
	}

	public Boolean getShow() {
		return isShow;
	}

	public void setShow(Boolean show) {
		isShow = show;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getDesignerId() {
		return designerId;
	}

	public void setDesignerId(Integer designerId) {
		this.designerId = designerId;
	}

	public DesDesigner getDesDesigner() {
		return desDesigner;
	}

	public void setDesDesigner(DesDesigner desDesigner) {
		this.desDesigner = desDesigner;
	}
}
