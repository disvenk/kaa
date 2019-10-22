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
 * 平台的商品库
 */
@Entity
@Table(name = "pla_product")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="pla_product", allocationSize=1)
public class PlaProduct extends GenericEntity {

	/**
	 * 商品编码：0100100001 ,一级分类：00  +  二级分类：000, +  商品流水码：00000  == 商品编码： 0100100001
	 */
	@Column(name = "product_code")
	private String productCode;

	/**
	 * 货号	 供应商产品编号
	 */
	@Column(name = "pno")
	private String pno;

	/**
	 * 分类ID
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
	 * 供应商id
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
	 * 单件供货周期: 单位：天
	 */
	@Column(name = "suplier_day")
	private Double suplierDay;

	/**
	 * 供货价 : 2017.11.15 需求变更：一个商品对应一个供货价
	 */
	@Column(name = "suplier_price")
	private Double suplierPrice;

	/**
	 * 材料说明
	 */
	@Column(name = "material")
	private String material;

	/**
	 * 工艺说明
	 */
	@Column(name = "technics")
	private String technics;

	/**
	 * 品牌
	 */
	@Column(name = "brand")
	private String brand;

	/**
	 * 是否设计师
	 */
	@Column(name = "is_designer")
	private Boolean isDesigner;

	/**
	 * 是否默认添加至门店商品
	 */
	@Column(name = "is_add")
	private Boolean isAdd;


	/**
	 * 商品图文详情
	 */
	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id",referencedColumnName="pid", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private PlaProductDescription plaProductDescription;

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
	 * 视频地址
	 */
	@Column(name = "vedio_url")
	private String vedioUrl;

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
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;

	/**
	 * 维护供应商价格时输入备注
	 */
	@Column(name = "supplier_remarks")
	private String supplierRemarks;

	/**
	 * 商品标签集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "plaProduct")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<PlaProductLabel> plaProductLabelList = new ArrayList<>();

	/**
	 * 商品图片集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "plaProduct")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<PlaProductPicture> plaProductPictureList = new ArrayList<>();

	/**
	 * 商品销售渠道集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "plaProduct")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<PlaProductSaleschannel> plaProductSaleschannelList = new ArrayList<>();

	/**
	 * 商品规格与供应商集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "plaProduct")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<PlaProductPrice> plaProductPriceList = new ArrayList<>();

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<PlaProductLabel> getPlaProductLabelList() {
		return plaProductLabelList;
	}

	public void setPlaProductLabelList(List<PlaProductLabel> plaProductLabelList) {
		this.plaProductLabelList = plaProductLabelList;
	}

	public List<PlaProductPicture> getPlaProductPictureList() {
		return plaProductPictureList;
	}

	public void setPlaProductPictureList(List<PlaProductPicture> plaProductPictureList) {
		this.plaProductPictureList = plaProductPictureList;
	}

	public List<PlaProductSaleschannel> getPlaProductSaleschannelList() {
		return plaProductSaleschannelList;
	}

	public void setPlaProductSaleschannelList(List<PlaProductSaleschannel> plaProductSaleschannelList) {
		this.plaProductSaleschannelList = plaProductSaleschannelList;
	}

	public List<PlaProductPrice> getPlaProductPriceList() {
		return plaProductPriceList;
	}

	public void setPlaProductPriceList(List<PlaProductPrice> plaProductPriceList) {
		this.plaProductPriceList = plaProductPriceList;
	}

	public String getPno() {
		return pno;
	}

	public void setPno(String pno) {
		this.pno = pno;
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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getVedioUrl() {
		return vedioUrl;
	}

	public void setVedioUrl(String vedioUrl) {
		this.vedioUrl = vedioUrl;
	}

	public PlaProductCategory getPlaProductCategory() {
		return plaProductCategory;
	}

	public void setPlaProductCategory(PlaProductCategory plaProductCategory) {
		this.plaProductCategory = plaProductCategory;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
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

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

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

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public Double getSuplierPrice() {
		return suplierPrice;
	}

	public void setSuplierPrice(Double suplierPrice) {
		this.suplierPrice = suplierPrice;
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

	public String getSupplierRemarks() {
		return supplierRemarks;
	}

	public void setSupplierRemarks(String supplierRemarks) {
		supplierRemarks = supplierRemarks;
	}

	public String getTechnics() {
		return technics;
	}

	public void setTechnics(String technics) {
		this.technics = technics;
	}

	public PlaProductDescription getPlaProductDescription() {
		return plaProductDescription;
	}

	public void setPlaProductDescription(PlaProductDescription plaProductDescription) {
		this.plaProductDescription = plaProductDescription;
	}

	public Boolean getAdd() {
		return isAdd;
	}

	public void setAdd(Boolean add) {
		isAdd = add;
	}
}
