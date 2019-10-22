package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 工厂端客户维护
 */
@Entity
@Table(name = "sup_customer")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_customer", allocationSize=1)
public class SupCustomer extends GenericEntity {


	/**
	 * 供应商ID
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
	 * 客户名称
	 */
	@Column(name = "customer")
	private String customer;

	/**
	 * 联系人
	 */
	@Column(name = "customer_init")
	private String customerInit;

	/**
	 * 客户手机号
	 */
	@Column(name = "customer_phone")
	private String customerPhone;

	/**
	 * 客户头像
	 */
	@Column(name = "icon")
	private String icon;

	/**
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;

	/**
	 *客户关联地址
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "supCustomer")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<SupCustomerAddress> supCustomerAddressList = new ArrayList<>();

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

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCustomerInit() {
		return customerInit;
	}

	public void setCustomerInit(String customerInit) {
		this.customerInit = customerInit;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<SupCustomerAddress> getSupCustomerAddressList() {
		return supCustomerAddressList;
	}

	public void setSupCustomerAddressList(List<SupCustomerAddress> supCustomerAddressList) {
		this.supCustomerAddressList = supCustomerAddressList;
	}
}
