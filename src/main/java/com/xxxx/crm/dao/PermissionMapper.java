package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Permission;

import javax.management.relation.InvalidRelationTypeException;
import java.util.List;

public interface PermissionMapper  extends BaseMapper<Permission,Integer> {

    /**
     * 通过角色id查询资源表是否存在相关的资源权限
     * @param roleId
     * @return
     */
    Integer queryPermissionByRoleId(Integer roleId);

    /**
     * 通过角色id删除相关的资源权限
     * @param roleId
     * @return
     */
    Integer deletePermission(Integer roleId);

    /**
     * 查询对应角色所拥有的资源id
     * @param roleId
     * @return
     */
    List<Integer> queryRoleHasModuleIdByRoleId(Integer roleId);

    /**
     * 通过用户id查询对应的用户列表(对应的用户所拥有的资源权限码)
     * @param userId
     * @return
     */
    List<String> queryUserHasPermissionByUserId(Integer userId);

    /**
     * 通过资源id查询权限记录
     * @param moduleId
     * @return
     */
    Integer queryCountByModuleId(Integer moduleId);

    /**
     * 通过资源id删除对应的权限记录
     * @param moduleId
     * @return
     */
    Integer deletePermissionByModuleId(Integer moduleId);
}