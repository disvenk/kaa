package com.xxx.model.system;

import com.alibaba.fastjson.annotation.JSONField;
import com.xxx.core.entity.GenericEntity;
import com.xxx.model.business.PubUserLogin;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="SYS_Role", allocationSize=1)
public class SYS_Role extends GenericEntity {

    /**
     * 角色名称
     */
    @Column(nullable = false, unique = true, length = 30)
    private String name;

    /**
     * 备注
     */
    private String remark;

    //用户集合
    @JSONField(serialize = false)
    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(mappedBy = "roleList",fetch = FetchType.LAZY)
    @org.hibernate.annotations.ForeignKey(name = "none")
    private Set<PubUserLogin> userList = new LinkedHashSet<>();

    //菜单集合
    @JSONField(serialize = false)
    @Fetch(FetchMode.SUBSELECT)
    @org.hibernate.annotations.ForeignKey(name = "none")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "SYS_Role_Menu",
            joinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "menuId", referencedColumnName = "id")
    )
    private Set<SYS_Menu> menuList = new LinkedHashSet<>();


    public  Set<SYS_Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList( Set<SYS_Menu> menuList) {
        this.menuList = menuList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Set<PubUserLogin> getUserList() {
        return userList;
    }

    public void setUserList(Set<PubUserLogin> userList) {
        this.userList = userList;
    }
}
