package com.xxx.suplier.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/WOManage")
public class WOManageController {
    @RequestMapping(value = "/WOlocalHtml", method = {RequestMethod.GET})
    public String WOlocalHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOlocal";
    }

    @RequestMapping(value = "/WOkaaHtml", method = {RequestMethod.GET})
    public String WOkaaHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOkaa";
    }

    @RequestMapping(value = "/WOoperateHtml", method = {RequestMethod.GET})
    public String WOoperateHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOoperate";
    }

    @RequestMapping(value = "/WOoperateEditHtml", method = {RequestMethod.GET})
    public String WOoperateEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOoperateEdit";
    }

    @RequestMapping(value = "/WOoperateAddHtml", method = {RequestMethod.GET})
    public String WOoperateAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOoperateAdd";
    }

    @RequestMapping(value = "/WOrecordHtml", method = {RequestMethod.GET})
    public String WOrecordHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOrecord";
    }

    @RequestMapping(value = "/WOrecordEditHtml", method = {RequestMethod.GET})
    public String WOrecordEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOrecordEdit";
    }

    @RequestMapping(value = "/localDetailHtml", method = {RequestMethod.GET})
    public String localDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOlocalDetail";
    }

    @RequestMapping(value = "/kaaDetailHtml", method = {RequestMethod.GET})
    public String kaaDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOkaaDetail";
    }

    @RequestMapping(value = "/localAddHtml", method = {RequestMethod.GET})
    public String localAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOlocalAdd";
    }

    @RequestMapping(value = "/localEditHtml", method = {RequestMethod.GET})
    public String localEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOlocalEdit";
    }

    @RequestMapping(value = "/WOsendHtml", method = {RequestMethod.GET})
    public String WOsendHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOsend";
    }

    @RequestMapping(value = "/WOcheckHtml", method = {RequestMethod.GET})
    public String WOcheckHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOcheck";
    }

    @RequestMapping(value = "/WOinHtml", method = {RequestMethod.GET})
    public String WOinHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/WOin";
    }

//    客户管理
    @RequestMapping(value = "/clientManageHtml", method = {RequestMethod.GET})
    public String clientManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/clientManage";
    }

    @RequestMapping(value = "/clientManageAddHtml", method = {RequestMethod.GET})
    public String clientManageAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/clientManageAdd";
    }

    @RequestMapping(value = "/clientManageEditHtml", method = {RequestMethod.GET})
    public String clientManageEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/clientManageEdit";
    }

    @RequestMapping(value = "/clientAddressHtml", method = {RequestMethod.GET})
    public String clientAddressHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/clientAddress";
    }

    @RequestMapping(value = "/clientAddressAddHtml", method = {RequestMethod.GET})
    public String clientAddressAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/clientAddressAdd";
    }

    @RequestMapping(value = "/clientAddressEditHtml", method = {RequestMethod.GET})
    public String clientAddressEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/clientAddressEdit";
    }

    /*
    *
    *
    * 本地订单
    *
    *
    **/
    @RequestMapping(value = "/localOrderHtml", method = {RequestMethod.GET})
    public String localOrderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/localOrder";
    }
    @RequestMapping(value = "/localOrderAddHtml", method = {RequestMethod.GET})
    public String localOrderAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/localOrderAdd";
    }
    @RequestMapping(value = "/localOrderEditHtml", method = {RequestMethod.GET})
    public String localOrderEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/localOrderEdit";
    }
    @RequestMapping(value = "/localOrderDetailHtml", method = {RequestMethod.GET})
    public String localOrderDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/localOrderDetail";
    }
    /*
    *
    *
    * 基础资料维护
    *
    *
    **/
    @RequestMapping(value = "/basicClientHtml", method = {RequestMethod.GET})
    public String basicClientHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicClient";
    }
    @RequestMapping(value = "/basicClientAddHtml", method = {RequestMethod.GET})
    public String basicClientAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicClientAdd";
    }
    @RequestMapping(value = "/basicClientAddressHtml", method = {RequestMethod.GET})
    public String basicClientAddressHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicClientAddress";
    }
    @RequestMapping(value = "/basicAddressAddHtml", method = {RequestMethod.GET})
    public String basicAddressAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicAddressAdd";
    }
    @RequestMapping(value = "/basicCategoryHtml", method = {RequestMethod.GET})
    public String basicCategoryHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicCategory";
    }

    @RequestMapping(value = "/basicCategoryAddHtml", method = {RequestMethod.GET})
    public String basicCategoryAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicCategoryAdd";
    }
    @RequestMapping(value = "/basicColorHtml", method = {RequestMethod.GET})
    public String basicColorHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicColor";
    }
    @RequestMapping(value = "/basicColorAddHtml", method = {RequestMethod.GET})
    public String basicColorAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicColorAdd";
    }
    @RequestMapping(value = "/basicSizeHtml", method = {RequestMethod.GET})
    public String basicSizeHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicSize";
    }
    @RequestMapping(value = "/basicSizeAddHtml", method = {RequestMethod.GET})
    public String basicSizeAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicSizeAdd";
    }

    @RequestMapping(value = "/basicMaterialHtml", method = {RequestMethod.GET})
    public String basicMaterialHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicMaterial";
    }
    @RequestMapping(value = "/basicMaterialAddHtml", method = {RequestMethod.GET})
    public String basicMaterialAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicMaterialAdd";
    }
    @RequestMapping(value = "/basicProcedureHtml", method = {RequestMethod.GET})
    public String basicProcedureHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicProcedure";
    }
    @RequestMapping(value = "/basicProcedureAddHtml", method = {RequestMethod.GET})
    public String basicProcedureAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicProcedureAdd";
    }
    @RequestMapping(value = "/basicProcedureEditorHtml", method = {RequestMethod.GET})
    public String basicProcedureEditorHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicProcedureEditor";
    }
    @RequestMapping(value = "/basicStorageHtml", method = {RequestMethod.GET})
    public String basicStorageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicStorage";
    }
    @RequestMapping(value = "/basicStorageAddHtml", method = {RequestMethod.GET})
    public String basicStorageAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicStorageAdd";
    }
    @RequestMapping(value = "/basicStorageEditHtml", method = {RequestMethod.GET})
    public String basicStorageEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/basicStorageEdit";
    }
    /*
    *
    *
    * 工作台
    *
    *
    **/
    @RequestMapping(value = "/workbenchHtml", method = {RequestMethod.GET})
    public String workbenchHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/workbench";
    }
    @RequestMapping(value = "/workbenchSalaryHtml", method = {RequestMethod.GET})
    public String workbenchSalaryHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/workbenchSalary";
    }
    @RequestMapping(value = "/workbenchGoodsHtml", method = {RequestMethod.GET})
    public String workbenchGoodsHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/woManage/workbenchGoods";
    }

}
