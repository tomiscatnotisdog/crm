package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Permission;
import com.xxxx.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleService extends BaseService<Role,Integer> {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private ModuleMapper moduleMapper;

    /**
     * 查询所有的角色列表
     * @return
     */
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }

    /**
     * 添加角色
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRole(Role role){
        //1.参数校验(角色名 非空 唯一)
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名称不可以为空!"); //非空
        AssertUtil.isTrue(roleMapper.queryRoleByRoleName(role.getRoleName())!=null,"所添加的用户名称已存在,请重新输入!");
        //2.设置默认值(是否有效,创建时间,修改时间)
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        //3.添加角色
        AssertUtil.isTrue(roleMapper.insertSelective(role)!=1,"角色添加失败,请重试!");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role){
        //1.参数的校验(角色id非空且存在,角色名非空且未占用)
        AssertUtil.isTrue(role.getId()==null,"待更新的角色记录不存在!");
        //根据角色id查询
        Role tempRole=roleMapper.selectByPrimaryKey(role.getId());
        //判断查询的角色是否存在
        AssertUtil.isTrue(tempRole==null,"待更新的角色记录不存在!");
        //判断角色名是否为空
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名称不可以为空!");
        //通过角色名查询
        tempRole=roleMapper.queryRoleByRoleName(role.getRoleName());
        //判断角色是否存在,或者是否是自己
        AssertUtil.isTrue(tempRole!=null&& !tempRole.getId().equals(role.getId()),"角色名称已存在,请重新输入!");
        //2.设置默认值(修改时间)
        role.setUpdateDate(new Date());
        //3.调用修改方法
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)!=1,"角色修改失败,请重试!");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer roleId){
        //1.参数的校验(id非空且有效)
        AssertUtil.isTrue(roleId==null,"待删除的角色记录不存在!");
        //通过角色id查询角色记录
        Role tempRole=roleMapper.selectByPrimaryKey(roleId);
        //判断角色是否存在
        AssertUtil.isTrue(tempRole==null,"待删除的角色记录不存在!");
        //2.设置默认值(有效值,更新时间)
        tempRole.setIsValid(0);
        tempRole.setUpdateDate(new Date());
        //3.调用更新方法
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(tempRole)!=1,"角色记录删除失败!");
    }

    /**
     * 角色授权
     * @param mIds
     * @param roleId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void roleGrant(Integer roleId,Integer[] mIds){
        //1.通过角色id查询资源表是否存在相关的资源权限
        Integer count=permissionMapper.queryPermissionByRoleId(roleId);
        //2.判断是否存在记录,如果存在则全部移除
        if (count>0){
            AssertUtil.isTrue(permissionMapper.deletePermission(roleId)!=count,"角色授权失败!");
        }
        //3.判断是否传入了相应的权限id,如果有则进行添加
        if (mIds!=null && mIds.length>0){
            //创建存放Permission对象的集合
            List<Permission> permissionList=new ArrayList<>();
            for (Integer mId:mIds){
                Permission permission=new Permission();
                permission.setModuleId(mId); //设置资源id
                permission.setRoleId(roleId); //设置角色id
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mId).getOptValue()); //设置授权码
                permission.setCreateDate(new Date()); //设置创建时间
                permission.setUpdateDate(new Date()); //设置更新时间
                //将设置好的对象放到lIst集合中
                permissionList.add(permission);
            }
            //批量添加数据
            AssertUtil.isTrue(permissionMapper.insertBatch(permissionList)!=permissionList.size(),"角色授权失败!");
        }
    }

}
