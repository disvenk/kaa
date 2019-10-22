package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 支付二维码
 */
@Entity
@Table(name = "sto_suplierorder_qrcode")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sto_suplierorder_qrcode", allocationSize=1)
public class StoSuplierOrderQrcode extends GenericEntity {


	/**
	 * 采购订单id
	 */
	@Column(name = "order_id")
	private Integer orderId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private StoSuplierOrder stoSuplierOrder;

	/**
	 * 二维码
	 */
	@Column(name = "qrcode")
	private String qrcode;

	/**
	 * 生效时间
	 */
	@Column(name = "begintime")
	private Date begintime;

	/**
	 * 失效时间
	 */
	@Column(name = "endtime")
	private Date endtime;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public StoSuplierOrder getStoSuplierOrder() {
		return stoSuplierOrder;
	}

	public void setStoSuplierOrder(StoSuplierOrder stoSuplierOrder) {
		this.stoSuplierOrder = stoSuplierOrder;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public Date getBegintime() {
		return begintime;
	}

	public void setBegintime(Date begintime) {
		this.begintime = begintime;
	}

	public Date getEndtime() {
		return endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
}
