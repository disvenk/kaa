package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 供应商的审核记录表
 */
@Entity
@Table(name = "sup_approvelog")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_approvelog", allocationSize=1)
public class SupApproveLog extends GenericEntity {

	/**
	 *	供应商ID
	 */
	@Column(name = "suplier_id")
	private Integer suplierId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "suplier_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupSuplier supSuplier;

	/**
	 *	审核时间
	 */
	@Column(name = "approveDate")
	private Date approveDate;

	/**
	 * 说明
	 */
	@Column(name = "description")
	private String description;

	/**
	 * 操作：operate
	 */
	@Column(name = "operate")
	private String operate;

	public Integer getSuplierId() {
		return suplierId;
	}

	public void setSuplierId(Integer suplierId) {
		this.suplierId = suplierId;
	}

	public SupSuplier getSupSuplier() {
		return supSuplier;
	}

	public void setSupSuplier(SupSuplier supSuplier) {
		this.supSuplier = supSuplier;
	}

	public Date getApproveDate() {
		return approveDate;
	}

	public void setApproveDate(Date approveDate) {
		this.approveDate = approveDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}
}
