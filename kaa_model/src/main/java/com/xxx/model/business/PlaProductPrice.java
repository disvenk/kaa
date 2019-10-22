package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 平台商品与供应商关系表
 */
@Entity
@Table(name = "pla_product_price")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="pla_product_price", allocationSize=1)
public class PlaProductPrice extends GenericEntity {


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
	 * 颜色
	 */
	@Column(name = "color")
	private String color;

	/**
	 * 尺寸
	 */
	@Column(name = "size")
	private String size;

	/**
	 * 库存
	 */
	@Column(name = "stock")
	private Integer stock;

	/**
	 * 价格
	 */
	@Column(name = "price")
	private Double price;

	/**
	 * 线上销售价格
	 */
	@Column(name = "online_price")
	private Double onlinePrice;

	/**
	 * 线下销售价格
	 */
	@Column(name = "offline_price")
	private Double offlinePrice;

	/**
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;

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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getOnlinePrice() {
		return onlinePrice;
	}

	public void setOnlinePrice(Double onlinePrice) {
		this.onlinePrice = onlinePrice;
	}

	public Double getOfflinePrice() {
		return offlinePrice;
	}

	public void setOfflinePrice(Double offlinePrice) {
		this.offlinePrice = offlinePrice;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
