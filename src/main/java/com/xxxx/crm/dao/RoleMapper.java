package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Role;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


public interface RoleMapper extends BaseMapper<Role,Integer> {

    /**
     * 查询所有的角色列表
     * @return
     */
    List<Map<String,Object>> queryAllRoles(Integer userId);

    /**
     * 根据角色名查询角色
     * @param roleName
     * @return
     */
    Role queryRoleByRoleName(String roleName);
}