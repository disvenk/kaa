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
 * 供应商商品库
 */
@Entity
@Table(name = "sup_product")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_product", allocationSize=1)
public class SupProduct extends GenericEntity {

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
	 * 货号 （同一供应商不能重复）
	 */
	@Column(name = "pno")
	private String pno;

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
	private SupProductBase categoryBase;

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
	 * 重要说明
	 */
	@Column(name = "description")
	private String description;

	/**
	 * 商品图文详情
	 */
	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id",referencedColumnName="pid", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupProductDescription supProductDescription;

	/**
	 * 价格
	 */
	@Column(name = "price")
	private Double price;


	/**
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;


	/**
	 * 肩宽
	 */
	@Column(name = "shoulder")
	private Double shoulder;

	/**
	 * 胸围
	 */
	@Column(name = "bust")
	private Double bust;

	/**
	 * 腰围
	 */
	@Column(name = "waist")
	private Double waist;

	/**
	 * 臀围
	 */
	@Column(name = "hipline")
	private Double hipline;


	/**
	 * 身高
	 */
	@Column(name = "height")
	private Double height;

	/**
	 * 体重
	 */
	@Column(name = "weight")
	private Double weight;

	/**
	 * 喉到地
	 */
	@Column(name = "throatheight")
	private Double throatheight;

	/**
	 * 其它
	 */
	@Column(name = "other")
	private String other;

//	/**
//	 *  库存
//	 */
//	@Column(name = "stock")
//	private Integer stock;
//
//	/**
//	 *  单件供货周期: 单位：天
//	 */
//	@Column(name = "suplier_day")
//	private Double suplierDay;
//
//	/**
//	 *  品牌
//	 */
//	@Column(name = "brand")
//	private String brand;
//
//	/**
//	 * 是否上架
//	 * 		0:未上架
//	 * 		1:已上架
//	 */
//	@Column(name = "status")
//	private Integer status;
//
//	/**
//	 * 排序
//	 */
//	@Column(name = "sort")
//	private Integer sort;
//
//
//	/**
//	 * 视频地址
//	 */
//	@Column(name = "vedioUrl")
//	private String vedioUrl;



	/**
	 * 商品图片集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "supProduct")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<SupProductPicture> supProductPictureList = new ArrayList<>();

	/**
	 * 商品的颜色尺寸表集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "supProduct")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<SupProductPrice> supProductPriceList = new ArrayList<>();

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public SupProductBase getCategoryBase() {
		return categoryBase;
	}

	public void setCategoryBase(SupProductBase categoryBase) {
		this.categoryBase = categoryBase;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getTechnics() {
		return technics;
	}

	public void setTechnics(String technics) {
		this.technics = technics;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SupProductDescription getSupProductDescription() {
		return supProductDescription;
	}

	public void setSupProductDescription(SupProductDescription supProductDescription) {
		this.supProductDescription = supProductDescription;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getShoulder() {
		return shoulder;
	}

	public void setShoulder(Double shoulder) {
		this.shoulder = shoulder;
	}

	public Double getBust() {
		return bust;
	}

	public void setBust(Double bust) {
		this.bust = bust;
	}

	public Double getWaist() {
		return waist;
	}

	public void setWaist(Double waist) {
		this.waist = waist;
	}

	public Double getHipline() {
		return hipline;
	}

	public void setHipline(Double hipline) {
		this.hipline = hipline;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getThroatheight() {
		return throatheight;
	}

	public void setThroatheight(Double throatheight) {
		this.throatheight = throatheight;
	}

	public List<SupProductPicture> getSupProductPictureList() {
		return supProductPictureList;
	}

	public void setSupProductPictureList(List<SupProductPicture> supProductPictureList) {
		this.supProductPictureList = supProductPictureList;
	}

	public List<SupProductPrice> getSupProductPriceList() {
		return supProductPriceList;
	}

	public void setSupProductPriceList(List<SupProductPrice> supProductPriceList) {
		this.supProductPriceList = supProductPriceList;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
}
