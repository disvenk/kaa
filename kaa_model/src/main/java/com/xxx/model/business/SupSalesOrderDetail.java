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
 * 供应商订单详情
 */
@Entity
@Table(name = "sup_sales_order_detail")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_sales_order_detail", allocationSize=1)
public class SupSalesOrderDetail extends GenericEntity {

	/**
	 * 供应商订单ID
	 */
	@Column(name = "order_id")
	private Integer orderId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupSalesOrder supSalesOrder;

	/**
	 * 供应商商品ID
	 */
	@Column(name = "pid")
	private Integer pid;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pid", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupProduct supProduct;

	/**
	 * 供应商商品编号
	 */
	@Column(name = "pno")
	private String pno;

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
	 *  商品主图地址
	 */
	@Column(name = "href")
	private String href;

	/**
	 *  商品名称
	 */
	@Column(name = "product_name")
	private String productName;

	/**
	 * 分类名称
	 */
	@Column(name = "category_name")
	private String categoryName;

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

	/**
	 * 件数
	 */
	@Column(name = "qty")
	private Integer qty;

	/**
	 * 单价
	 */
	@Column(name = "price")
	private Double price;

	/**
	 * 总价
	 */
	@Column(name = "subtotal")
	private Double subtotal;

	/**
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;

	/**
	 * 图片集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "supSalesOrderDetail")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<SupSalesOrderDetailPicture> supSalesOrderDetailPictureList = new ArrayList<>();

	/**
	 * 原材料集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "supSalesOrderDetail")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<SupSalesOrderDetailMaterial> supSalesOrderDetailMaterialList = new ArrayList<>();

	/**
	 * 工序集合
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "supSalesOrderDetail")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<SupSalesOrderDetailProcedure> supSalesOrderDetailProcedureList = new ArrayList<>();

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public SupSalesOrder getSupSalesOrder() {
		return supSalesOrder;
	}

	public void setSupSalesOrder(SupSalesOrder supSalesOrder) {
		this.supSalesOrder = supSalesOrder;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public SupProduct getSupProduct() {
		return supProduct;
	}

	public void setSupProduct(SupProduct supProduct) {
		this.supProduct = supProduct;
	}

	public String getPno() {
		return pno;
	}

	public void setPno(String pno) {
		this.pno = pno;
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

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<SupSalesOrderDetailPicture> getSupSalesOrderDetailPictureList() {
		return supSalesOrderDetailPictureList;
	}

	public void setSupSalesOrderDetailPictureList(List<SupSalesOrderDetailPicture> supSalesOrderDetailPictureList) {
		this.supSalesOrderDetailPictureList = supSalesOrderDetailPictureList;
	}

	public List<SupSalesOrderDetailMaterial> getSupSalesOrderDetailMaterialList() {
		return supSalesOrderDetailMaterialList;
	}

	public void setSupSalesOrderDetailMaterialList(List<SupSalesOrderDetailMaterial> supSalesOrderDetailMaterialList) {
		this.supSalesOrderDetailMaterialList = supSalesOrderDetailMaterialList;
	}

	public List<SupSalesOrderDetailProcedure> getSupSalesOrderDetailProcedureList() {
		return supSalesOrderDetailProcedureList;
	}

	public void setSupSalesOrderDetailProcedureList(List<SupSalesOrderDetailProcedure> supSalesOrderDetailProcedureList) {
		this.supSalesOrderDetailProcedureList = supSalesOrderDetailProcedureList;
	}
}
