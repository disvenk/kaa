package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 门店的图片
 */
@Entity
@Table(name = "sto_store_picture")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sto_store_picture", allocationSize=1)
public class StoStorePicture extends GenericEntity {

	/**
	 *	门店ID
	 */
	@Column(name = "store_id")
	private Integer storeId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private StoStoreInfo stoStoreInfo;

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

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public StoStoreInfo getStoStoreInfo() {
		return stoStoreInfo;
	}

	public void setStoStoreInfo(StoStoreInfo stoStoreInfo) {
		this.stoStoreInfo = stoStoreInfo;
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


}
