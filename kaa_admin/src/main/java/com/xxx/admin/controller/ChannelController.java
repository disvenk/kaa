package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.*;
import com.xxx.admin.service.ChannelService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.DesChannel;
import com.xxx.model.business.PlaChannel;
import com.xxx.model.business.SupChannel;
import com.xxx.model.business.SysArea;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.JavaValidate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/channel")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    /**
     * @Description: 跳转渠道页面
     * @Author: Chen.zm
     * @Date: 2017/10/25
     */
    @RequestMapping(value = "/channelHtml", method = {RequestMethod.GET})
    public String data1_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/channelManage/channel";
    }

    @RequestMapping(value = "/channelEditHtml", method = {RequestMethod.GET})
    public String data2_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "channelManage/channelEdit";
    }

    @RequestMapping(value = "/channelAddHtml", method = {RequestMethod.GET})
    public String data3_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "channelManage/channelAdd";
    }

    @RequestMapping(value = "/indexHtml", method = {RequestMethod.GET})
    public String channelsHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/channel/index";
    }

    @RequestMapping(value = "/designerJoinHtml", method = {RequestMethod.GET})
    public String designerJoinHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/channelAce/designerJoin";
    }

    @RequestMapping(value = "/designerJoinEditHtml", method = {RequestMethod.GET})
    public String designerJoinEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/channelAce/designerJoinEdit";
    }

    @RequestMapping(value = "/factoryJoinHtml", method = {RequestMethod.GET})
    public String factoryJoinHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/channelAce/factoryJoin";
    }

    @RequestMapping(value = "/factoryJoinEditHtml", method = {RequestMethod.GET})
    public String factoryJoinEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/channelAce/factoryJoinEdit";
    }
    /**
     * @Description: 新增渠道信息
     * @Author: Chen.zm
     * @Date: 2017/10/25
     */
    @RequestMapping(value = "/saveChannel", method = {RequestMethod.POST})
    public ResponseEntity saveChannel(@RequestBody ChannelForm form) throws Exception {
        if (StringUtils.isBlank(form.name) || "".equals(form.name))
            return new ResponseEntity(new RestResponseEntity(110, "公司名称不能为空", null), HttpStatus.OK);
        channelService.savePlaChannel(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 渠道列表
     * @Author: Chen.zm
     * @Date: 2017/10/25 0025
     */
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public ResponseEntity list(@RequestBody ChannelListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<PlaChannel> list = channelService.findPlaChannelList(pageQuery, form.contact, form.telephone);
        JSONArray data = new JSONArray();
        for (PlaChannel pal : list) {
            JSONObject json = new JSONObject();
            json.put("name", pal.getName());
            json.put("id", pal.getId());
            json.put("address",(pal.getProvinceName() == null ? "" : pal.getProvinceName()) +
                      (pal.getCityName() == null ? "" :pal.getCityName()) + (pal.getZoneName() == null ? "" : pal.getZoneName()) +
                    (pal.getAddress() == null ? "" : pal.getAddress()));
            json.put("contact", pal.getContact());
            json.put("telephone", pal.getTelephone());
            json.put("type", pal.getType());
            json.put("createTime", DateTimeUtils.parseStr(pal.getCreatedDate()));
            //去除null
            for(String key : json.keySet()){
                if(json.get(key) == null) json.put(key,"");
            }
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description: 渠道详情
     * @Author: Chen.zm
     * @Date: 2017/10/25 0025
     */
    @RequestMapping(value = "/channelDetail", method = {RequestMethod.POST})
    public ResponseEntity channelDetail(@RequestBody IdForm form) throws Exception {
        PlaChannel pal = channelService.getPlaChannel(form.id);
        if (pal == null)
            return new ResponseEntity(new RestResponseEntity(110, "渠道不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("id",pal.getId());
        json.put("name", pal.getName());
        json.put("address", pal.getAddress());
        json.put("contact", pal.getContact());
        json.put("telephone", pal.getTelephone());
        json.put("userType", pal.getType());
        json.put("provinceId", pal.getProvince());
        json.put("provinceName", pal.getProvinceName());
        json.put("cityId", pal.getCity());
        json.put("cityName", pal.getCityName());
        json.put("zoneId", pal.getZone());
        json.put("zoneName", pal.getZoneName());
        json.put("createTime", pal.getCreatedDate() == null ? "" : DateTimeUtils.parseStr(pal.getCreatedDate().getTime()));
        if(StringUtils.isNotBlank(pal.getProvinceName())){
            SysArea e = channelService.get2(SysArea.class,"name",pal.getProvinceName(),"type",1);
            json.put("provinceId",e.getCode());
            if(StringUtils.isNotBlank(pal.getCityName())){
                SysArea e1 = channelService.get2(SysArea.class,"name",pal.getCityName(),"parentCode",e.getCode());
                json.put("cityId",e1.getCode());
            }
        }
        //去除null
        for(String key : json.keySet()){
            if(json.get(key) == null) json.put(key,"");
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description: 渠道删除
     * @Author: Chen.zm
     * @Date: 2017/10/25 0025
     */
    @RequestMapping(value = "/channelRemove", method = {RequestMethod.POST})
    public ResponseEntity channelRemove(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "渠道id不能为空", null), HttpStatus.OK);
        channelService.removePlaChannel(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 渠道编辑
     * @Author: Chen.zm
     * @Date: 2018/10/25 0025
     */
    @RequestMapping(value = "/channelUpdate", method = {RequestMethod.POST})
    public ResponseEntity channelUpdate(@RequestBody ChannelForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "渠道id不能为空", null), HttpStatus.OK);
        channelService.updatePlaChannel(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 工厂信息列表
     * @Author: disvenk.dai
     * @Date: 2017/2/5 0025
     */
    @RequestMapping(value = "/factoryInfoList",method = RequestMethod.POST)
    public ResponseEntity factoryInfoList(@RequestBody FactoryQueryForm form){
        if(form.pageNum==null)return new ResponseEntity(new RestResponseEntity(110,"页码不能为空",null),HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        JSONArray data = new JSONArray();
        PageList<SupChannel> list = channelService.factoryInfoList(pageQuery,form.name,form.tel);
        for(SupChannel supChannel : list){
            JSONObject json = new JSONObject();
            json.put("id",supChannel.getId());
            json.put("name",supChannel.getName());
            json.put("tel",supChannel.getTelephone());
            StringBuilder sb = new StringBuilder();
            sb.append(supChannel.getProvinceName()==null?"":supChannel.getProvinceName());
            sb.append(supChannel.getCityName()==null?"":supChannel.getCityName());
            sb.append(supChannel.getZoneName()==null?"":supChannel.getZoneName());
            sb.append(supChannel.getAddress()==null?"":supChannel.getAddress());
            json.put("address",sb.toString());
            json.put("createDate",DateTimeUtils.parseStr(supChannel.getCreatedDate()));
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100,"成功",data,pageQuery.page,pageQuery.limit,list.total),HttpStatus.OK);

    }

    /**
     * @Description: 工厂申请新增和编辑
     * @Author: disvenk.dai
     * @Date: 2017/2/5 0025
     */
    @RequestMapping(value = "/addFactory",method = RequestMethod.POST)
    public ResponseEntity addFactory(@RequestBody FactoryAddForm form) throws ResponseEntityException, UpsertException {
        if(form.factoryName==null || StringUtils.isBlank(form.factoryName)) return new ResponseEntity(new RestResponseEntity(110, "企业名称不能为空", null), HttpStatus.OK);
        if(form.tel==null || StringUtils.isBlank(form.tel))return new ResponseEntity(new RestResponseEntity(110, "联系方式不能为空", null), HttpStatus.OK);
        if(form.province==null || StringUtils.isBlank(form.province))return new ResponseEntity(new RestResponseEntity(110, "省信息不能为空", null), HttpStatus.OK);
        if(form.city==null || StringUtils.isBlank(form.city))return new ResponseEntity(new RestResponseEntity(110, "市信息不能为空", null), HttpStatus.OK);
        if(form.zone==null || StringUtils.isBlank(form.zone))return new ResponseEntity(new RestResponseEntity(110, "区信息不能为空", null), HttpStatus.OK);
        channelService.addFactory(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 工厂信息编辑回显详情
     * @Author: disvenk.dai
     * @Date: 2017/2/5 0025
     */
    @RequestMapping(value = "factoryEditDetail",method = RequestMethod.POST)
    public ResponseEntity factoryEdit(@RequestBody FactoryAddForm form) throws ResponseEntityException {
        if(form.id==null)return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null),HttpStatus.OK);
           SupChannel supChannel = channelService.factoryEditDetail(form);
        JSONObject json = new JSONObject();
        json.put("id",supChannel.getId());
        json.put("name",supChannel.getName());
        json.put("tel",supChannel.getTelephone());
        json.put("provice",supChannel.getProvinceName());
        json.put("city",supChannel.getCityName());
        json.put("zone",supChannel.getZoneName());
        json.put("address",supChannel.getAddress());
        if(StringUtils.isNotBlank(supChannel.getProvinceName())){
            SysArea e = channelService.get2(SysArea.class,"name",supChannel.getProvinceName(),"type",1);
            json.put("provinceCode",e.getCode());
            if(StringUtils.isNotBlank(supChannel.getCityName())){
                SysArea e1 = channelService.get2(SysArea.class,"name",supChannel.getCityName(),"parentCode",e.getCode());
                json.put("cityCode",e1.getCode());
            }
        }

        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description: 工厂信息删除
     * @Author: disvenk.dai
     * @Date: 2017/2/5 0025
     */
    @RequestMapping(value = "factoryDelete",method = RequestMethod.POST)
    public ResponseEntity factoryDelete(@RequestBody FactoryAddForm form) throws ResponseEntityException, UpsertException {
        if(form.id==null)return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null),HttpStatus.OK);
        channelService.factoryDelete(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null),HttpStatus.OK);
    }

    /**
     * @Description:设计师渠道删除
     * @Author: hanchao
     * @Date: 2018/2/5 0005
     */
    @RequestMapping(value = "/channelDesRemove", method = {RequestMethod.POST})
    public ResponseEntity channelDesRemove(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "渠道id不能为空", null), HttpStatus.OK);
        channelService.channelDesRemove(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:设计师渠道编辑保存
     * @Author: hanchao
     * @Date: 2018/2/5 0005
     */
    @RequestMapping(value = "/channelDesUpdate", method = {RequestMethod.POST})
    public ResponseEntity channelDesUpdate(@RequestBody DesAddForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "渠道id不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.telephone))
            return new ResponseEntity(new RestResponseEntity(120, "联系电话不能为空", null), HttpStatus.OK);
        if (!JavaValidate.isMobileNO(form.telephone))
            return new ResponseEntity(new RestResponseEntity(130, "手机号格式不正确", null), HttpStatus.OK);
        DesChannel des = channelService.selectDesChannel(form.id,form.telephone,form.type);
        if(des != null){
            return new ResponseEntity(new RestResponseEntity(140, "手机号已存在", null), HttpStatus.OK);
        }
        channelService.channelDesUpdate(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:设计师渠道编辑
     * @Author: hanchao
     * @Date: 2018/2/5 0005
     */
    @RequestMapping(value = "/channelDesEdit", method = {RequestMethod.POST})
    public ResponseEntity channelDesEdit(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "渠道id不能为空", null), HttpStatus.OK);
        DesChannel des = channelService.get2(DesChannel.class,"id",form.id,"logicDeleted",false);
        JSONObject json = new JSONObject();
        json.put("id", des.getId());
        json.put("name", des.getName());
        json.put("provinceName", des.getProvinceName());
        json.put("cityName", des.getCityName());
        json.put("zoneName", des.getZoneName());
        json.put("address", des.getAddress());
        json.put("telephone", des.getTelephone());
        json.put("type", des.getType());
        if(StringUtils.isNotBlank(des.getProvinceName())){
            SysArea e = channelService.get2(SysArea.class,"name",des.getProvinceName(),"type",1);
            json.put("provinceCode",e.getCode());
            if(StringUtils.isNotBlank(des.getCityName())){
                SysArea e1 = channelService.get2(SysArea.class,"name",des.getCityName(),"parentCode",e.getCode());
                json.put("cityCode",e1.getCode());
            }
        }
        //去除null
        for(String key : json.keySet()){
            if(json.get(key) == null) json.put(key,"");
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description:设计师渠道列表
     * @Author: hanchao
     * @Date: 2018/2/5 0005
     */
    @RequestMapping(value = "/desChannellist", method = {RequestMethod.POST})
    public ResponseEntity desChannellist(@RequestBody DesListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<DesChannel> list = channelService.findDesChannelList(pageQuery, form.name, form.telephone);
        JSONArray data = new JSONArray();
        for (DesChannel des : list) {
            JSONObject json = new JSONObject();
            json.put("id", des.getId());
            json.put("name", des.getName());
            StringBuilder sb = new StringBuilder();
            sb.append(des.getProvinceName() == null ? "" : des.getProvinceName());
            sb.append(des.getCityName() == null ? "" : des.getCityName());
            sb.append(des.getZoneName() == null ? "" : des.getZoneName());
            sb.append(des.getAddress() == null ? "" : des.getAddress());
            json.put("address",sb.toString());
            json.put("telephone", des.getTelephone());
            json.put("type", des.getType());
            json.put("createdDate",DateTimeUtils.parseStr(des.getCreatedDate()));
            //去除null
            for(String key : json.keySet()){
                if(json.get(key) == null) {
                    json.put(key,"");
                }
            }
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description:设计师渠道新增
     * @Author: hanchao
     * @Date: 2018/2/5 0005
     */
    @RequestMapping(value = "/channelDesAdd", method = {RequestMethod.POST})
    public ResponseEntity channelDesAdd(@RequestBody DesAddForm form) throws Exception {
        if (StringUtils.isBlank(form.name))
            return new ResponseEntity(new RestResponseEntity(110, "名称不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.telephone))
            return new ResponseEntity(new RestResponseEntity(120, "联系电话不能为空", null), HttpStatus.OK);
        if (!JavaValidate.isMobileNO(form.telephone))
            return new ResponseEntity(new RestResponseEntity(130, "手机号格式不正确", null), HttpStatus.OK);
        DesChannel des = channelService.get2(DesChannel.class,"telephone",form.telephone,"type",form.type,"logicDeleted",false);
        if(des != null){
            return new ResponseEntity(new RestResponseEntity(140, "手机号已存在", null), HttpStatus.OK);
        }
        channelService.channelDesAdd(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }
}
