package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module, Integer> {

    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 查询所有的模块信息
     *
     * @return
     */
    public List<TreeModel> queryAllModules(Integer roleId) {
        //查询所有的资源列表
        List<TreeModel> treeModelList = moduleMapper.queryAllModules();
        //查询指定角色拥有的资源(即:对应的资源id)
        List<Integer> permissionIds = permissionMapper.queryRoleHasModuleIdByRoleId(roleId);
        //判断该角色是否拥有资源
        if (permissionIds != null && permissionIds.size() > 0) {
            //循环所有的资源列表,判断是否有被选择的资源,设置其checked属性值为true
            treeModelList.forEach(treeModel -> {
                if (permissionIds.contains(treeModel.getId())) {
                    //如果包含则设置
                    treeModel.setChecked(true);
                }
            });
        }
        return treeModelList;
    }

    /**
     * 查询所有的资源列表
     *
     * @return
     */
    public Map<String, Object> queryModuleList() {
        Map<String, Object> moduleMap = new HashMap<>();

        List<Module> moduleList = moduleMapper.queryModuleList();

        moduleMap.put("code", 0);
        moduleMap.put("msg", "");
        moduleMap.put("count", moduleList.size());
        moduleMap.put("data", moduleList);

        return moduleMap;
    }

    /**
     * 添加资源列表
     * @param module
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addModule(Module module) {
        /*1.参数的校验*/
        //层级grade 非空 且只有0|1|2
        Integer grade = module.getGrade();
        AssertUtil.isTrue(null == grade || !(grade == 0 || grade == 1 | grade == 2), "菜单层级不合法!");

        //模块名称 moduleName 非空 且唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "模块名称不可以为空!");
        //判断同一层级下是否存在相同的模块名称
        AssertUtil.isTrue(moduleMapper.queryModuleByGradeAndModuleName(grade, module.getModuleName()) != null, "该层级下模块名称已存在!");

        //菜单的url
        if (grade == 1) {
            //菜单的url 二级菜单(grade=1) 非空
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "url不可以为空!");
            //菜单的url 二级菜单(grade=1) 同级不可重复
            AssertUtil.isTrue(null != moduleMapper.queryModuleByGradeAndUrl(grade, module.getUrl()), "url不可以重复!");
        }

        //父级id parentid
        //如果是一级菜单 则无父id parentID=-1
        if (grade == 0) {
            module.setParentId(-1);
        }
        //如果是二级或三级菜单(grade=1或2)  父级id 非空 且 存在
        if (grade == 1 || grade == 2) {
            AssertUtil.isTrue(module.getParentId() == null, "父级id不可以为空!");
            //查询父级id对应的资源是否存在
            AssertUtil.isTrue(null == moduleMapper.selectByPrimaryKey(module.getParentId()), "父级菜单未找到!");
        }

        //权限码optValue非空且不可重复
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()), "权限码不可以为空!");
        //判断是否重复
        AssertUtil.isTrue(null != moduleMapper.queryModuleByOptValue(module.getOptValue()), "权限码已经存在!");

        /*2.设置默认值*/
        module.setIsValid((byte) 1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());

        /*3.执行添加操作*/
        AssertUtil.isTrue(moduleMapper.insertSelective(module) != 1, "资源添加失败!");
    }


    /**
     * 更新资源列表
     * @param module
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module) {
        /*1.参数的校验*/
        //层级grade 非空 且只有0|1|2
        AssertUtil.isTrue(module.getId()==null,"待更新的资源id不可以为空!");
        //通过id查询数据是否存在
        Module tempModule=moduleMapper.selectByPrimaryKey(module.getId());
        //判断对象是否存在
        AssertUtil.isTrue(null==tempModule,"待更新的资源未找到!");


        Integer grade = module.getGrade();
        AssertUtil.isTrue(null == grade || !(grade == 0 || grade == 1 | grade == 2), "菜单层级不合法!");

        //模块名称 moduleName 非空 且唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "模块名称不可以为空!");
        //判断同一层级下是否存在相同的模块名称
        tempModule=moduleMapper.queryModuleByGradeAndModuleName(grade, module.getModuleName());
        //查看查询到的对象是否和要修改的对象是同一个
        if (tempModule!=null){
            AssertUtil.isTrue(!tempModule.getId().equals(module.getId()),"该层级下模块名称已存在!");
        }


        //菜单的url
        if (grade == 1) {
            //菜单的url 二级菜单(grade=1) 非空
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "url不可以为空!");
            //菜单的url 二级菜单(grade=1) 同级不可重复
            tempModule=moduleMapper.queryModuleByGradeAndUrl(grade, module.getUrl());
            if (tempModule!=null){
                AssertUtil.isTrue(!tempModule.getId().equals(module.getId()),"该层级下菜单url已存在!");
            }
        }

        //父级id parentid
        //如果是一级菜单 则无父id parentID=-1
        if (grade == 0) {
            module.setParentId(-1);
        }
        //如果是二级或三级菜单(grade=1或2)  父级id 非空 且 存在
        if (grade == 1 || grade == 2) {
            AssertUtil.isTrue(module.getParentId() == null, "父级id不可以为空!");
            //查询父级id对应的资源是否存在
            AssertUtil.isTrue(null == moduleMapper.selectByPrimaryKey(module.getParentId()), "父级菜单未找到!");
        }

        //权限码optValue非空且不可重复
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()), "权限码不可以为空!");
        //通过权限码查询module对象
        tempModule=moduleMapper.queryModuleByOptValue(module.getOptValue());
        //判断是否是自己
        if (tempModule!=null){
            AssertUtil.isTrue(!tempModule.getId().equals(module.getId()),"权限码已存在!");
        }

        /*2.设置默认值*/
        module.setUpdateDate(new Date());

        /*3.执行添加操作*/
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module) != 1, "资源添加失败!");
    }

    /**
     * 删除资源(修改其有效值)
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModule(Integer id){
        /*1.判断要删除的记录是否为空*/
        //判断资源id是否为空
        AssertUtil.isTrue(id==null,"待删除的记录不存在!");
        //通过资源id查询对应的资源对象
        Module tempModule=moduleMapper.selectByPrimaryKey(id);
        //判断对象是否存在
        AssertUtil.isTrue(tempModule==null,"待删除的记录不存在!");

        /*2.查看是否存在子记录,如果存在则不可删除*/
        //查看是否有将本id作为父id的对象
        int count =moduleMapper.queryCountByParentId(id);
        //判断是否存在
        AssertUtil.isTrue(count>0,"待删除的记录存在子记录!");

        /*3.删除时,删除对应的权限记录*/
        //通过资源id查询权限记录
        count=permissionMapper.queryCountByModuleId(id);
        //查看是否有对应的记录
        if (count>0){
            //如果存在,则将其删除
            permissionMapper.deletePermissionByModuleId(id);
        }

        /*4.执行删除操作,将记录的有效值改为'0'*/
        tempModule.setIsValid((byte) 0);
        tempModule.setUpdateDate(new Date());
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(tempModule)!=1,"资源删除失败!");
    }
}
