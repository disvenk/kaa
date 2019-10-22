package com.xxx.sales.service;


import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.Feedback;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class SalesHomeService extends CommonService {

    /**
     * @Description: 收集反馈意见
     * @Author: Steven.Xiao
     * @Date: 2017/11/29
     */

    @CacheEvict(value = {"Feedback"}, allEntries = true)
    public Feedback saveFeedback(String name, String telephone, String description) throws ResponseEntityException, UpsertException{
        Feedback feedback=new Feedback();
        feedback.setName(name);
        feedback.setTelephone(telephone);
        feedback.setDescription(description);
        feedback.setStatus(0);
        return upsert2(feedback);
    }



}
