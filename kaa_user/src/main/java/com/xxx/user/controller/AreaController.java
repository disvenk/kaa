package com.xxx.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.SysArea;
import com.xxx.user.form.AreaCodeForm;
import com.xxx.user.service.AreaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 省市区
 */
@RestController
@RequestMapping("/area")
public class AreaController {

	@Autowired
	private AreaService areaService;

	/** 获取所有省份
	 * @Description:
	 * @Author: Chen.zm
	 * @Date: 2017/10/30 0030
	 */
	@RequestMapping(value = "/findProvince", method = {RequestMethod.POST})
	public ResponseEntity findProvince() throws Exception {
		List<SysArea> areas = areaService.findProvince();
		JSONArray data = new JSONArray();
		for (SysArea area : areas) {
			JSONObject json = new JSONObject();
			json.put("id", area.getId());
			json.put("code", area.getCode());
			json.put("name", area.getName());
			data.add(json);
		}
		return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
	}


	/**
	 * @Description: 获取省份下的所有市或区
	 * @Author: Chen.zm
	 * @Date: 2017/10/30 0030
	 */
	@RequestMapping(value = "/findCity", method = {RequestMethod.POST})
	public ResponseEntity findCity(@RequestBody AreaCodeForm form) throws Exception {
		if (StringUtils.isBlank(form.code))
			return new ResponseEntity(new RestResponseEntity(110, "编码不能为空", null), HttpStatus.OK);
		List<SysArea> areas = areaService.findCity(form.code);
		JSONArray data = new JSONArray();
		for (SysArea area : areas) {
			JSONObject json = new JSONObject();
			json.put("id", area.getId());
			json.put("code", area.getCode());
			json.put("name", area.getName());
			data.add(json);
		}
		return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
	}

	/**
	 * @Description: 获取省市区
	 * @Author: Chen.zm
	 * @Date: 2017/11/27 0027
	 */
	@RequestMapping(value = "/findProvinceCityZone", method = {RequestMethod.POST})
	public ResponseEntity findProvinceCityZone() throws Exception {
		JSONArray data = areaService.findProvinceCityZone();
		return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
	}


}
