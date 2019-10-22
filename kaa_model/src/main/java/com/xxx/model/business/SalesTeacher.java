package com.xxx.model.business;

import com.xxx.core.entity.GenericEntity;

import javax.persistence.*;

/**
 * 体验师
 */
@Entity
@Table(name = "sal_teacher")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sal_teacher", allocationSize=1)
public class SalesTeacher extends GenericEntity {

	/**
	 * 姓名
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 电话
	 */
	@Column(name = "mobile")
	private String mobile;

	/**
	 * 年龄
	 */
	@Column(name = "age")
	private String age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}
}
