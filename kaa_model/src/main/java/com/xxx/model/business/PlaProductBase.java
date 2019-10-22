package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 所有的基础资料，均配置在此表中。
 */
@Entity
@Table(name = "pla_product_base")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="pla_product_base", allocationSize=1)
public class PlaProductBase extends GenericEntity {


	/**
	 * 名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 基础资料类型
	 * 		1：商品标签
	 * 		2：商品颜色
	 * 		3：商品尺寸
	 * 		4：用户身份
	 * 		5：快递公司
	 * 		6：销售渠道
	 */
	@Column(name = "type")
	private Integer type;

	/**
	 * 父ID  (同表关联)
	 */
	@Column(name = "parent_id")
	private Integer parentId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private PlaProductBase plaProductBase;

	/**
	 * 排序
	 */
	@Column(name = "sort")
	private Double sort;

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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Double getSort() {
		return sort;
	}

	public void setSort(Double sort) {
		this.sort = sort;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PlaProductBase getPlaProductBase() {
		return plaProductBase;
	}

	public void setPlaProductBase(PlaProductBase plaProductBase) {
		this.plaProductBase = plaProductBase;
	}
}
