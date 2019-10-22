package com.xxx.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.service.CategoryShowService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.PlaProductCategory;
import com.xxx.utils.DateTimeUtils;
import com.xxx.admin.form.CategoryIdsSaveForm;
import com.xxx.admin.form.CategoryListForm;
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
@RequestMapping("/category")
public class CategoryShowController {
    @Autowired
    private CategoryShowService categoryShowService;

    @RequestMapping(value = "/categoryShowHtml", method = {RequestMethod.GET})
    public String data1_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/categoryManage/categoryShow";
    }

    @RequestMapping(value = "/designerGoodsEditHtml", method = {RequestMethod.GET})
    public String data2_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "DesignerGoodsManage/DesignerGoodsEdit";
    }

    /**
     * @Description:商品分类根据id删除二级分类
     * @Author: hanchao
     * @Date: 2017/11/22 0022
     */
    @RequestMapping(value = "/deleteCategoryId", method = {RequestMethod.POST})
    public ResponseEntity deleteCategoryId(@RequestBody IdForm form) throws ResponseEntityException, UpsertException {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "分类id不能为空", null), HttpStatus.OK);
        categoryShowService.updateCategoryId(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:商品分类根据id删除一级分类
     * @Author: hanchao
     * @Date: 2017/11/22 0022
     */
    @RequestMapping(value = "/deleteCategoryParentId", method = {RequestMethod.POST})
    public ResponseEntity deleteCategoryParentId(@RequestBody IdForm form) throws ResponseEntityException, UpsertException {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "分类id不能为空", null), HttpStatus.OK);
        categoryShowService.updateCategoryParentId(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:新增分类
     * @Author: hanchao
     * @Date: 2017/11/24 0024
     */
    @RequestMapping(value = "/newCategoryAddManage", method = {RequestMethod.POST})
    public ResponseEntity insertNewCategoryAddManage(@RequestBody CategoryIdsSaveForm form) throws Exception {
        categoryShowService.insertNewCategoryAddManage(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }
    
    /**
     * @Description:商品添加下级分类
     * @Author: hanchao
     * @Date: 2017/11/23 0023
     */
    @RequestMapping(value = "/categoryAddManage", method = {RequestMethod.POST})
    public ResponseEntity insertCategoryManage(@RequestBody CategoryIdsSaveForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "分类id不能为空", null), HttpStatus.OK);
        categoryShowService.insertCategoryManage(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:商品一级和二级分类编辑保存
     * @Author: hanchao
     * @Date: 2017/11/23 0023
     */
    @RequestMapping(value = "/updateCategoryManageParentId", method = {RequestMethod.POST})
    public ResponseEntity updateCategoryManageParentId(@RequestBody CategoryIdsSaveForm form) throws Exception {
        categoryShowService.updateCategoryManageParentId(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:商品一级和二级分类编辑详情
     * @Author: hanchao
     * @Date: 2017/11/23 0023
     */
    @RequestMapping(value = "/categoryManageEditDetail", method = {RequestMethod.POST})
    public ResponseEntity categoryManageEditDetail(@RequestBody CategoryIdsSaveForm form) throws Exception {
        JSONArray jsonArray = new JSONArray();
        PlaProductCategory pla = categoryShowService.getPlaProductCategory(form.id,form.parentId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",pla.getId());
        jsonObject.put("parentId",pla.getParentId());
        jsonObject.put("name",pla.getName());
        jsonObject.put("suplierDay",pla.getSupplierDay());
        jsonObject.put("updateDate",DateTimeUtils.parseStr(pla.getUpdateDate()));
        jsonObject.put("remark",pla.getRemarks());
        jsonArray.add(jsonObject);
        return new ResponseEntity(new RestResponseEntity(100,"成功",jsonArray), HttpStatus.OK);
    }


    /**
     * @Description:获取分类列表信息
     * @Author: hanchao
     * @Date: 2017/11/22 0022
     */
    @RequestMapping(value = "/getPlaProductCategoryListAll", method = {RequestMethod.POST})
    public ResponseEntity getPlaProductCategoryListAll(@RequestBody CategoryListForm form) throws Exception {
        if (form.pageNum == null)
                return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 999;
        JSONArray data = new JSONArray();
        //1.通过查询条件 获取一级分类
        PageList<PlaProductCategory> list = categoryShowService.getPlaProductCategoryListAll(pageQuery, form.categoryId, null);
        for (PlaProductCategory category : list) { //所有一级分类
            JSONObject json = new JSONObject();
            json.put("id", category.getId());
            json.put("name", category.getName() == null ? "" : category.getName());
            json.put("remark",category.getRemarks() == null ? "" : category.getRemarks());
            json.put("suplierDay",category.getSupplierDay() == null ? "" : category.getSupplierDay());
            json.put("updateDate", DateTimeUtils.parseStr(category.getUpdateDate()));
            JSONArray categoryList = new JSONArray();
            //2.通过查询条件 根据一级分类获取二级分类
            PageList<PlaProductCategory> list2 = categoryShowService.getPlaProductCategoryListAll(pageQuery, null, category.getId());
            for (PlaProductCategory cat : list2) {
                JSONObject js = new JSONObject();
                js.put("id", cat.getId());
                js.put("name", cat.getName() == null ? "" : cat.getName());
                js.put("remark",cat.getRemarks() == null ? "" : cat.getRemarks());
                js.put("suplierDay",cat.getSupplierDay() == null ? "" : cat.getSupplierDay());
                js.put("updateDate",DateTimeUtils.parseStr(cat.getUpdateDate()));
                categoryList.add(js);
            }
            json.put("categoryList", categoryList);
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }
}
