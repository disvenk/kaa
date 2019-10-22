package com.xxx.admin.form;

import java.util.ArrayList;
import java.util.List;

public class FooterContentSaveForm {
    public Integer id;
    public String name;
    public String description;
    public Integer kind;
    public Integer watch;
    public String vedioUrl;
    public String shortDesc;
    public List<ProductPictureListForm> picture = new ArrayList<>();//图片集合
}
