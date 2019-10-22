package com.xxx.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.PubUserLogin;
import com.xxx.model.system.SYS_Menu;
import com.xxx.model.system.SYS_Role;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PermissionService extends CommonService {

    /**
     * @Description:根据用户查询边栏菜单
     * @Author: disvenk.dai
     * @Date: 2017/1/6
     */
    @Cacheable(value = {"SYS_Menu"})
    public JSONArray findMenuByUser(Integer userId){
        PubUserLogin pubUserLogin = get2(PubUserLogin.class, "id", userId);
        Set<SYS_Role> roleList = pubUserLogin.getRoleList();
        JSONArray data1 = new JSONArray();
        //根据用户查询角色
        for (SYS_Role sys_role : roleList ) {
            //如果是超级管理员
            if(sys_role.getName().equals("admin")){
                //新建数组并查询所有的菜单
                JSONArray admin = new JSONArray();
                List<SYS_Menu> allMenu = getCurrentSession().createCriteria(SYS_Menu.class)
                        .add(Restrictions.eq("logicDeleted",false))
                        .add(Restrictions.isNull("parent"))
                        .addOrder(Order.asc("sort"))
                        .list();

                for (SYS_Menu sys_menu: allMenu) {
                    if(sys_menu.getParentId()==null){
                        List<SYS_Menu> list = getCurrentSession().createCriteria(SYS_Menu.class)
                                .add(Restrictions.eq("logicDeleted",false))
                                .add(Restrictions.eq("parentId",sys_menu.getId()))
                                .addOrder(Order.asc("sort"))
                                .list();
                        JSONObject json1 = new JSONObject();
                        json1.put("text",sys_menu.getName());
                     /*   json1.put("icon",sys_menu.getIcon());
                        json1.put("href",sys_menu.getHref());*/
                        JSONArray data2 = new JSONArray();
                        if(list!=null && list.size()!=0) {
                            for (SYS_Menu sys_menu1 : list) {
                                // if(sys_menu1.getParentId()==sys_menu.getId()){
                                JSONObject json2 = new JSONObject();
                                json2.put("id", UUID.randomUUID().toString());
                                json2.put("text", sys_menu1.getName());
                                json2.put("href", sys_menu1.getHref());
                                data2.add(json2);
                                // }
                            }
                        }
                        json1.put("items",data2);
                        admin.add(json1);
                    }

                }

                return  admin;
            }
        }

        //}
        String hql = "select distinct m from PubUserLogin u inner join u.roleList r inner join r.menuList m where u.id = ? and m.parentId is null group by m.id order by m.sort asc";
        final List<SYS_Menu> menuList = getCurrentSession().createQuery(hql).setInteger(0,userId).list();

        // Set<SYS_Menu> menuList = sys_role.getMenuList();
        if(menuList!=null && menuList.size()!=0){
            for (SYS_Menu sys_menu: menuList) {
                JSONObject json1 = new JSONObject();
                List<SYS_Menu> list = getCurrentSession().createCriteria(SYS_Menu.class)
                        .add(Restrictions.eq("logicDeleted",false))
                        .add(Restrictions.eq("parentId",sys_menu.getId()))
                        .addOrder(Order.asc("sort"))
                        .list();
                if(sys_menu.getParentId()==null){
                    json1.put("text",sys_menu.getName());
                    /*json1.put("icon",sys_menu.getIcon());
                    json1.put("href",sys_menu.getHref());*/
                    JSONArray data2 = new JSONArray();
                    if(list!=null && list.size()!=0) {
                        for (SYS_Menu sys_menu1 : list) {
                                JSONObject json2 = new JSONObject();
                                json2.put("id", UUID.randomUUID().toString());
                                json2.put("text", sys_menu1.getName());
                                json2.put("href", sys_menu1.getHref());
                                data2.add(json2);
                        }
                    }
                    json1.put("items",data2);
                    data1.add(json1);
                }
            }
        }
        return  data1;
        //return  data1;
    }

}
