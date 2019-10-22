package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 盒子服务类型
 */
@Entity
@Table(name = "box_type")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="box_type", allocationSize=1)
public class BoxType extends GenericEntity {

	/**
	 * 名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 价格
	 */
	@Column(name = "price")
	private Double price;

	/**
	 * 天数
	 */
	@Column(name = "day")
	private Integer day;

	/**
	 * 次数
	 */
	@Column(name = "count")
	private Integer count;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}


}
