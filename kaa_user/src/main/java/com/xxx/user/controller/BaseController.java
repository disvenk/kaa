package com.xxx.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.user.form.BaseDataForm;
import com.xxx.user.form.IdForm;
import com.xxx.user.form.ParentIdForm;
import com.xxx.user.service.BaseDataService;
import com.xxx.model.business.PlaProductBase;
import com.xxx.model.business.PlaProductCategory;
import org.apache.velocity.runtime.directive.Break;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基本资料
 */
@RestController
@RequestMapping("/base")
public class BaseController {

	@Autowired
	private BaseDataService baseDataService;


	/**
	 * 	基础资料类型
	 * 		1：商品标签
	 * 		2：商品颜色
	 * 		3：商品尺寸
	 * 		4：用户身份
	 * 		5：快递公司
	 * 		6：销售渠道
	 * @Description: 基本参数的取得
	 * @Author: Steven.Xiao
	 * @Date: 2017/11/3
	 */
	@RequestMapping(value = "/getBaseDataList", method = {RequestMethod.POST})
	public ResponseEntity getBaseDataList(@RequestBody BaseDataForm form) throws Exception {
		if (form.parameterType==null)
			return new ResponseEntity(new RestResponseEntity(110, "参数类别不能为空", null), HttpStatus.OK);
		List<PlaProductBase> baseDataList = baseDataService.getBaseDataList(form.parameterType);
		JSONArray data = new JSONArray();
		for (PlaProductBase base : baseDataList) {
			JSONObject json = new JSONObject();
			json.put("id", base.getId());
			json.put("name", base.getName());
			json.put("sort", base.getSort());
			json.put("description", base.getDescription());
			data.add(json);
		}
		return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
	}

	/**
	 * @Description:获取基础属性
	 * @Author: hanchao
	 * @Date: 2017/10/31 0031
	 */
	@RequestMapping(value = "/plaProductBaseList", method = {RequestMethod.POST})
	public ResponseEntity plaProductBaseList() throws Exception {
		List<PlaProductBase> plaProductBases = baseDataService.findPlaProductBaseList();
		JSONArray jsonArray = new JSONArray();
		JSONObject json = new JSONObject();
		JSONArray productLabel = new JSONArray();
		JSONArray productColor = new JSONArray();
		JSONArray productSize = new JSONArray();
		JSONArray userType = new JSONArray();//用户身份
		JSONArray courierCompany = new JSONArray();
		JSONArray salesChannel = new JSONArray();
		JSONArray suplierscope = new JSONArray();//主营业务

		for(PlaProductBase plaProductBase : plaProductBases){
			if(plaProductBase.getType() == 1){
				JSONObject label = new JSONObject();
				label.put("labelId",plaProductBase.getId());
				label.put("labelName",plaProductBase.getName());
				productLabel.add(label);
			}else if (plaProductBase.getType() == 2){
				JSONObject color = new JSONObject();
				color.put("colorId",plaProductBase.getId());
				color.put("colorName",plaProductBase.getName());
				productColor.add(color);
			}else if (plaProductBase.getType() == 3){
				JSONObject size = new JSONObject();
				size.put("sizeId",plaProductBase.getId());
				size.put("sizeName",plaProductBase.getName());
				productSize.add(size);
			}else if(plaProductBase.getType() == 4){
				JSONObject user_Type = new JSONObject();
				user_Type.put("user_TypeId",plaProductBase.getId());
				user_Type.put("user_TypeName",plaProductBase.getName());
				userType.add(user_Type);
			}
			else if (plaProductBase.getType() == 5){
				JSONObject company = new JSONObject();
				company.put("companyId",plaProductBase.getId());
				company.put("companyName",plaProductBase.getName());
				courierCompany.add(company);
			}else if(plaProductBase.getType() == 6){
				JSONObject channel = new JSONObject();
				channel.put("channelId",plaProductBase.getId());
				channel.put("channelName",plaProductBase.getName());
				salesChannel.add(channel);
			}else if(plaProductBase.getType() == 7){
				JSONObject scope = new JSONObject();
				scope.put("scopeId",plaProductBase.getId());
				scope.put("scopelName",plaProductBase.getName());
				suplierscope.add(scope);
			}
		}
		json.put("productLabel",productLabel);
		json.put("productColor",productColor);
		json.put("productSize",productSize);
		json.put("courierCompany",courierCompany);
		json.put("salesChannel",salesChannel);
		json.put("Suplierscope",suplierscope);
		json.put("userType",userType);
		jsonArray.add(json);
		return new ResponseEntity(new RestResponseEntity(100,"成功",jsonArray), HttpStatus.OK);
	}

	/**
	 * @Description: 取得商品的一级分类
	 * @Author: Steven.Xiao
	 * @Date: 2017/11/7
	 */
	@RequestMapping(value = "/getPlaProductCategoryLevelOneList", method = {RequestMethod.POST})
	public ResponseEntity getPlaProductCategoryLevelOneList() throws Exception {
		List<PlaProductCategory> list = baseDataService.getPlaProductCategoryLevelOneList();
		JSONArray data = new JSONArray();
		for (PlaProductCategory base : list) {
			JSONObject json = new JSONObject();
			json.put("id", base.getId());
			json.put("name", base.getName());
			json.put("sort", base.getSort());
			data.add(json);
		}
		return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
	}

	/**
	 * @Description: 商品的所有分类
	 * @Author: Chen.zm
	 * @Date: 2017/11/7
	 */
	@RequestMapping(value = "/getPlaProductCategoryList", method = {RequestMethod.POST})
	public ResponseEntity getPlaProductCategoryList(@RequestBody ParentIdForm form) throws Exception {
		List<PlaProductCategory> list = baseDataService.getPlaProductCategoryList(form.parentId);
		JSONArray data = new JSONArray();
		for (PlaProductCategory base : list) {
			JSONObject json = new JSONObject();
			json.put("id", base.getId());
			json.put("name", base.getName());
			json.put("sort", base.getSort());
			data.add(json);
		}
		return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
	}


	/**
	 * @Description: 取得商品的全部分类
	 * @Author: Chen.zm
	 * @Date: 2017/11/7
	 */
	@RequestMapping(value = "/getPlaProductCategoryListAll", method = {RequestMethod.POST})
	public ResponseEntity getPlaProductCategoryListAll() throws Exception {
		List<PlaProductCategory> list = baseDataService.getPlaProductCategoryListAll();
		//处理数据格式
		Map<Integer, JSONObject> map = new HashMap<>();
		for (PlaProductCategory base : list) {
			if (base.getParentId() == null) {
				JSONObject json = new JSONObject();
				json.put("id", base.getId());
				json.put("name", base.getName());
				json.put("categoryList", new JSONArray());
				map.put(base.getId(), json);
				continue;
			}
			JSONObject json = new JSONObject();
			json.put("id", base.getId());
			json.put("name", base.getName());
			((JSONArray) map.get(base.getParentId()).get("categoryList")).add(json);
		}
		//封装数据
		JSONArray data = new JSONArray();
		for (JSONObject json : map.values()) {
			data.add(json);
		}
		return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
	}

	/**
	 * @Description: 查询 某分类及该分类下面的所有子分类；
	 * @Author: Steven.Xiao
	 * @Date: 2017/11/16
	 */
	@RequestMapping(value = "/getAllCategoryIdByParentId", method = {RequestMethod.POST})
	public ResponseEntity getAllCategoryIdByParentId(@RequestBody ParentIdForm form) throws Exception {
		List<Integer> list = baseDataService.getAllCategoryIdByParentId(form.parentId);
		JSONObject data = new JSONObject();
		data.put("data",list);
		return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
	}



	/**
	 * @Description:获取工位列表接口
	 * @Author: hanchao
	 * @Date: 2017/12/6 0006
	 */
	@RequestMapping(value = "/getAllSupWorkerStationType", method = {RequestMethod.POST})
	public ResponseEntity getAllSupWorkerStationType() throws Exception {
		JSONArray json = new JSONArray();
		for (int i = 1; i<= 5; i++){
			JSONObject data = new JSONObject();
			if(i == 1){
				data.put("id",i);
				data.put("name","打板");
			}else if (i == 2){
				data.put("id",i);
				data.put("name","车位");
			}else if (i == 3){
				data.put("id",i);
				data.put("name","排花");
			}else if (i == 4){
				data.put("id",i);
				data.put("name","裁剪");
			}else if (i == 5){
				data.put("id",i);
				data.put("name","质检");
			}
			json.add(data);
		}
		return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
	}
}
