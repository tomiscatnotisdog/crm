package com.xxxx.crm.model;

public class TreeModel {

    private Integer id; //模块id
    private Integer pId; //父模块id
    private String name; //模块名称
    private boolean checked=false; //是否被选择(true是选中,false是不选中)
    private boolean open=true;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public String toString() {
        return "TreeModel{" +
                "id=" + id +
                ", pId=" + pId +
                ", name='" + name + '\'' +
                ", checked=" + checked +
                '}';
    }
}
