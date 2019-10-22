package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 平台商品销售渠道关系表
 */
@Entity
@Table(name = "pla_product_saleschannel")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="pla_product_saleschannel54", allocationSize=1)
public class PlaProductSaleschannel extends GenericEntity {


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
	 * 销售渠道ID,   关联PlaProductBase  type=6
	 */
	@Column(name = "saleschannel_id")
	private Integer saleschannelId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "saleschannel_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private PlaProductBase plaProductBase;

	/**
	 * 销售渠道名称
	 */
	@Column(name = "saleschannel_name")
	private String saleschannelName;


	public String getSaleschannelName() {
		return saleschannelName;
	}

	public void setSaleschannelName(String saleschannelName) {
		this.saleschannelName = saleschannelName;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public PlaProduct getPlaProduct() {
		return plaProduct;
	}

	public void setPlaProduct(PlaProduct plaProduct) {
		this.plaProduct = plaProduct;
	}

	public Integer getSaleschannelId() {
		return saleschannelId;
	}

	public void setSaleschannelId(Integer saleschannelId) {
		this.saleschannelId = saleschannelId;
	}

	public PlaProductBase getPlaProductBase() {
		return plaProductBase;
	}

	public void setPlaProductBase(PlaProductBase plaProductBase) {
		this.plaProductBase = plaProductBase;
	}
}
