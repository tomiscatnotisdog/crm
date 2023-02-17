package com.xxxx.crm.query;

import com.xxxx.crm.base.BaseQuery;

public class SaleChanceQuery extends BaseQuery {

    //营销机会管理  多条件查询
    private String customerName; // 客户名
    private String createMan; // 创建人
    private Integer state; // 分配状态

    //客户开发计划
    private Integer devResult; //开发状态
    private Integer assignMan; //指派人|分配人(id)

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCreateMan() {
        return createMan;
    }

    public void setCreateMan(String createMan) {
        this.createMan = createMan;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getDevResult() {
        return devResult;
    }

    public void setDevResult(Integer devResult) {
        this.devResult = devResult;
    }

    public Integer getAssignMan() {
        return assignMan;
    }

    public void setAssignMan(Integer assignMan) {
        this.assignMan = assignMan;
    }

    @Override
    public String toString() {
        return "SaleChanceQuery{" +
                "customerName='" + customerName + '\'' +
                ", createMan='" + createMan + '\'' +
                ", state=" + state +
                ", devResult=" + devResult +
                ", assignMan=" + assignMan +
                '}';
    }
}
