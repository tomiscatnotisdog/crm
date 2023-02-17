package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.User;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;


public interface UserMapper extends BaseMapper<User,Integer> {

    //根据用户名查询用户对象
    public abstract User queryUserByUserName(String userName);

    //修改用户密码
    int updatePasswordByUserId(User user);

    //查询所有的销售人员
    List<Map<String,Object>> queryAllSalse();

}