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
 * 工单明细
 */
@Entity
@Table(name = "sup_order_detail")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sup_order_detail", allocationSize=1)
public class SupOrderDetail extends GenericEntity {


	/**
	 * 工单ID
	 */
	@Column(name = "order_id")
	private Integer orderId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private SupOrder supOrder;

	/**
	 * 商品库ID
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
	 * 供应商商品编号:  supOrder.orderType=2 的时候使用该字段   默认取plaProduct内容
	 */
	@Column(name = "pno")
	private String pno;

	/**
	 * 材料说明    supOrder.orderType=2 的时候使用该字段   默认取plaProduct内容
	 */
	@Column(name = "material")
	private String material;

	/**
	 * 工艺说明   	supOrder.orderType=2 的时候使用该字段   默认取plaProduct内容
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
	 * 生产单价
	 */
	@Column(name = "output_price")
	private Double outputPrice;

	/**
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;

	/**
	 * 图片集合   supOrder.orderType=2 的时候使用该字段   默认取plaProduct内容
	 */
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "supOrderDetail")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<SupOrderDetailPicture> supOrderDetailPictureList = new ArrayList<>();

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	public SupOrder getSupOrder() {
		return supOrder;
	}

	public void setSupOrder(SupOrder supOrder) {
		this.supOrder = supOrder;
	}

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

	public Double getOutputPrice() {
		return outputPrice;
	}

	public void setOutputPrice(Double outputPrice) {
		this.outputPrice = outputPrice;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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

	public List<SupOrderDetailPicture> getSupOrderDetailPictureList() {
		return supOrderDetailPictureList;
	}

	public void setSupOrderDetailPictureList(List<SupOrderDetailPicture> supOrderDetailPictureList) {
		this.supOrderDetailPictureList = supOrderDetailPictureList;
	}
}
