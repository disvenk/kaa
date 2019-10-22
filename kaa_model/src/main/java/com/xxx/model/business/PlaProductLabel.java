package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 平台商品标签关系表
 */
@Entity
@Table(name = "pla_product_label")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="pla_product_label", allocationSize=1)
public class PlaProductLabel extends GenericEntity {


	/**
	 * 商品id
	 */
	@Column(name = "pid")
	private Integer pid;


	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pid", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private PlaProduct plaProduct;

	/**
	 * 商品标签ID, 关联PlaProductBase  type=1
	 */
	@Column(name = "label_id")
	private Integer labelId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "label_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private PlaProductBase plaProductBase;

	/**
	 * 商品标签名称
	 */
	@Column(name = "label_name")
	private String labelNmae;

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public Integer getLabelId() {
		return labelId;
	}

	public void setLabelId(Integer labelId) {
		this.labelId = labelId;
	}

	public PlaProductBase getPlaProductBase() {
		return plaProductBase;
	}

	public void setPlaProductBase(PlaProductBase plaProductBase) {
		this.plaProductBase = plaProductBase;
	}

	public PlaProduct getPlaProduct() {
		return plaProduct;
	}

	public void setPlaProduct(PlaProduct plaProduct) {
		this.plaProduct = plaProduct;
	}

	public String getLabelNmae() {
		return labelNmae;
	}

	public void setLabelNmae(String labelNmae) {
		this.labelNmae = labelNmae;
	}
}
