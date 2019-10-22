package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 商品标签关系表
 */
@Entity
@Table(name = "des_product_label")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="des_product_label", allocationSize=1)
public class DesignerProductLabel extends GenericEntity {

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
	private DesignerProduct designerProduct;

	/**
	 *  商品标签ID
	 */
	@Column(name = "label_id")
	private Integer labelId;

	/**
	 * 商品标签名称
	 */
	@Column(name = "label_name")
	private String labelNmae;


	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public DesignerProduct getDesignerProduct() {
		return designerProduct;
	}

	public void setDesignerProduct(DesignerProduct designerProduct) {
		this.designerProduct = designerProduct;
	}

	public Integer getLabelId() {
		return labelId;
	}

	public void setLabelId(Integer labelId) {
		this.labelId = labelId;
	}

	public String getLabelNmae() {
		return labelNmae;
	}

	public void setLabelNmae(String labelNmae) {
		this.labelNmae = labelNmae;
	}

}
