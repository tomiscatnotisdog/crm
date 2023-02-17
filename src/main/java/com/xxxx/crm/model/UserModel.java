package com.xxxx.crm.model;

import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;

public class UserModel {

    /*private Integer userId; //用户id*/
    private String userIdStr; //加密后的userId
    private String userName; //用户名称
    private String trueName; //真实姓名

    public String getUserIdStr() {
        return userIdStr;
    }

    public void setUserIdStr(String userIdStr) {
        this.userIdStr = userIdStr;
    }

    /*public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }*/

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }
}
