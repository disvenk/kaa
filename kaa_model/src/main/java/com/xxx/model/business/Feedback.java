package com.xxx.model.business;

import com.xxx.core.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 收集反馈意见
 */
@Entity
@Table(name = "sal_feedback")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sal_feedback", allocationSize=1)
public class Feedback extends GenericEntity {


	/**
	 * 姓名
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 联系方式
	 */
	@Column(name = "telephone")
	private String telephone;

	/**
	 * 反馈意见
	 */
	@Column(name = "description")
	private String description;

	/**
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;

	/**
	 * 处理状态：0 待处理  1 已处理
	 */
	@Column(name = "status")
	private Integer status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
