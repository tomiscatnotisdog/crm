package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.service.RoleService;
import com.xxxx.crm.vo.Role;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequestMapping("role")
@Controller
public class RoleController extends BaseController {

    @Resource
    private RoleService roleService;

    /**
     * 查询所有的角色列表
     * @return
     */
    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }

    /**
     * 分页查询角色列表
     * @param roleQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryRoleListByParams(RoleQuery roleQuery){
        return roleService.queryByParamsForTable(roleQuery);
    }

    /**
     * 进入到角色管理页面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "/role/role";
    }

    /**
     * 添加角色
     * @param role
     * @return
     */
    @PostMapping("insert")
    @ResponseBody
    public ResultInfo addRole(Role role){
        roleService.addRole(role);
        return success("角色添加成功!");
    }

    /**
     * 更新角色
     * @param role
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        roleService.updateRole(role);
        return success("角色更新成功!");
    }

    /**
     * 更新角色
     * @param roleId
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer roleId){
        roleService.deleteRole(roleId);
        return success("角色删除成功!");
    }


    /**
     * 打开添加或修改角色的页面
     * @return
     */
    @RequestMapping("toAddOrUpdate")
    public String toAddOrUpdate(Integer roleId, HttpServletRequest request){

        if (roleId!=null){
            //通过角色id查询角色记录
            Role role=roleService.selectByPrimaryKey(roleId);
            //将角色记录存放到请求域中
            request.setAttribute("role",role);
        }
        return  "/role/add_update";
    }

    /**
     * 进行角色授权
     * @return
     */
    @RequestMapping("roleGrant")
    @ResponseBody
    public ResultInfo roleGrant(Integer roleId,Integer[] mIds){
        roleService.roleGrant(roleId,mIds);
        return success("角色授权成功!");
    }

}
