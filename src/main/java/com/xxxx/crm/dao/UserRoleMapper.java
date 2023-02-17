package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {


    /**
     * 通过用户id查看对应的角色记录
     * @param userId
     * @return
     */
    Integer queryUserRoleByUserId(Integer userId);

    /**
     * 根据用户id批量删除相应用户角色记录
     * @param userId
     * @return
     */
    Integer deleteUserRoleByUserId(Integer userId);
}