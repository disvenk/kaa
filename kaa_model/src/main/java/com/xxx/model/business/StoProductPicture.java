package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 门店商品的图片
 */
@Entity
@Table(name = "sto_product_picture")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sto_product_picture", allocationSize=1)
public class StoProductPicture extends GenericEntity {

	/**
	 *	商品ID
	 */
	@Column(name = "pid")
	private Integer pid;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pid", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private StoProduct stoProduct;

	/**
	 *  商品图片地址
	 */
	@Column(name = "href")
	private String href;

	/**
	 *  排序
	 */
	@Column(name = "sort")
	private Double sort;

	/**
	 *  是否是主图
	 */
	@Column(name = "is_mainpic")
	private Boolean isMainpic;

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Double getSort() {
		return sort;
	}

	public void setSort(Double sort) {
		this.sort = sort;
	}

	public Boolean getMainpic() {
		return isMainpic;
	}

	public void setMainpic(Boolean mainpic) {
		isMainpic = mainpic;
	}

	public StoProduct getStoProduct() {
		return stoProduct;
	}

	public void setStoProduct(StoProduct stoProduct) {
		this.stoProduct = stoProduct;
	}
}
