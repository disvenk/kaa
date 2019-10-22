package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.service.FeedbackService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.Feedback;
import com.xxx.utils.DateTimeUtils;
import com.xxx.admin.form.FeedbackQueryForm;
import com.xxx.admin.form.IdForm;
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
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * @Description: 意见反馈列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/29
     */
    @RequestMapping(value = "/opinionHtml", method = {RequestMethod.GET})
    public String opinionHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "channelManage/opinion";
    }

    /**
     * @Description: 查询意见反馈列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/findFeedbackList", method = {RequestMethod.POST})
    public ResponseEntity findFeedbackList(@RequestBody FeedbackQueryForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<Feedback> feedbackList = feedbackService.findFeedbackList(form.name, form.telephone, pageQuery);
        JSONArray jsonArray = new JSONArray();
        for (Feedback feedback : feedbackList) {
            JSONObject json = new JSONObject();
            json.put("id", feedback.getId());
            json.put("name", feedback.getName());
            json.put("telephone", feedback.getTelephone());
            json.put("description", feedback.getDescription());
            json.put("createdDate", DateTimeUtils.parseStr(feedback.getCreatedDate()));
            //去除null
            for (String key : json.keySet()){
                if(json.get(key) == null) json.put(key,"");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, feedbackList.total), HttpStatus.OK);
    }

    /**
     * @Description: 删除
     * @Author: Steven.Xiao
     * @Date: 2017/11/29
     */
    @RequestMapping(value = "/deleteFeedback", method = {RequestMethod.POST})
    public ResponseEntity deleteFeedback(@RequestBody IdForm form) throws Exception {
        feedbackService.deleteFeedback(form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

}
