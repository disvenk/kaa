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
 * 门店的商品库管理
 */
@Entity
@Table(name = "sto_product")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sto_product", allocationSize=1)
public class StoProduct extends GenericEntity {

	/**
	 *	平台商品ID
	 */
	@Column(name = "plat_product_id")
	private Integer platProductId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plat_product_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private PlaProduct plaProduct;

	/**
	 * 门店ID
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
	 *  分类ID
	 */
	@Column(name = "category_id")
	private Integer categoryId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private PlaProductCategory plaProductCategory;

	/**
	 * 商品名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 *  商品主图地址
	 */
	@Column(name = "href")
	private String href;

	/**
	 *  单件供货周期: 单位：天
	 */
	@Column(name = "suplier_day")
	private Double suplierDay;

	/**
	 *  材料说明
	 */
	@Column(name = "material")
	private String material;

	/**
	 *  品牌
	 */
	@Column(name = "brand")
	private String brand;

	/**
	 * 是否设计师款
	 */
	@Column(name = "is_designer")
	private Boolean isDesigner;

//	/**
//	 * 商品图文详情
//	 */
//	@Column(name = "description")
//	private String description;

	/**
	 * 商品图文详情
	 */
	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id",referencedColumnName="pid", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private StoProductDescription stoProductDescription;

	/**
	 * 建议零售价
	 */
	@Column(name = "price")
	private Double price;

	/**
	 * 最低价
	 */
	@Column(name = "min_price")
	private Double minPrice;


	/**
	 * 最高价
	 */
	@Column(name = "max_price")
	private Double maxPrice;

	/**
	 * 排序
	 */
	@Column(name = "sort")
	private Integer sort;

	/**
	 * 是否上架
	 * 		0:未上架
	 * 		1:已上架
	 */
	@Column(name = "status")
	private Integer status;

	/**
	 * 浏览量
	 */
	@Column(name = "views")
	private Integer views = 0;

	/**
	 * 销量
	 */
	@Column(name = "sales")
	private Integer sales = 0;

	/**
	 * 综合排序
	 */
	@Column(name = "colligate")
	private Integer colligate;

	/**
	 * 视频地址
	 */
	@Column(name = "vedioUrl")
	private String vedioUrl;

	/**
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;

	/**
	 * 商品标签集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "stoProduct")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<StoProductLabel> stoProductLabelList = new ArrayList<>();

	/**
	 * 商品图片集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "stoProduct")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<StoProductPicture> stoProductPictureList = new ArrayList<>();

	/**
	 * 门店商品的规格价格表集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "stoProduct")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<StoProductPrice> stoProductPriceList = new ArrayList<>();

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public PlaProduct getPlaProduct() {
		return plaProduct;
	}

	public void setPlaProduct(PlaProduct plaProduct) {
		this.plaProduct = plaProduct;
	}

	public PlaProductCategory getPlaProductCategory() {
		return plaProductCategory;
	}

	public void setPlaProductCategory(PlaProductCategory plaProductCategory) {
		this.plaProductCategory = plaProductCategory;
	}

	public StoStoreInfo getStoStoreInfo() {
		return stoStoreInfo;
	}

	public void setStoStoreInfo(StoStoreInfo stoStoreInfo) {
		this.stoStoreInfo = stoStoreInfo;
	}

	public Integer getPlatProductId() {
		return platProductId;
	}

	public void setPlatProductId(Integer platProductId) {
		this.platProductId = platProductId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getDesigner() {
		return isDesigner;
	}

	public void setDesigner(Boolean designer) {
		isDesigner = designer;
	}

//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	}

	public StoProductDescription getStoProductDescription() {
		return stoProductDescription;
	}

	public void setStoProductDescription(StoProductDescription stoProductDescription) {
		this.stoProductDescription = stoProductDescription;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	public String getVedioUrl() {
		return vedioUrl;
	}

	public void setVedioUrl(String vedioUrl) {
		this.vedioUrl = vedioUrl;
	}

	public Integer getSales() {
		return sales;
	}

	public void setSales(Integer sales) {
		this.sales = sales;
	}

	public Integer getColligate() {
		return colligate;
	}

	public void setColligate(Integer colligate) {
		this.colligate = colligate;
	}

	public List<StoProductLabel> getStoProductLabelList() {
		return stoProductLabelList;
	}

	public void setStoProductLabelList(List<StoProductLabel> stoProductLabelList) {
		this.stoProductLabelList = stoProductLabelList;
	}

	public List<StoProductPicture> getStoProductPictureList() {
		return stoProductPictureList;
	}

	public void setStoProductPictureList(List<StoProductPicture> stoProductPictureList) {
		this.stoProductPictureList = stoProductPictureList;
	}

	public List<StoProductPrice> getStoProductPriceList() {
		return stoProductPriceList;
	}

	public void setStoProductPriceList(List<StoProductPrice> stoProductPriceList) {
		this.stoProductPriceList = stoProductPriceList;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Double getSuplierDay() {
		return suplierDay;
	}

	public void setSuplierDay(Double suplierDay) {
		this.suplierDay = suplierDay;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}
}
