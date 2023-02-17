package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.vo.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module,Integer> {

    /**
     * 查询所有的模块信息
     * @return
     */
    public List<TreeModel> queryAllModules();

    /**
     * 查询所有的资源列表
     * @return
     */
    public List<Module> queryModuleList();

    /**
     * 根据菜单层级和模块名称查询资源
     * @param grade
     * @param moduleName
     * @return
     */
    Module queryModuleByGradeAndModuleName(@Param("grade") Integer grade,@Param("moduleName") String moduleName);

    /**
     * 根据模块层级和菜单url查询资源
     * @param grade
     * @param url
     * @return
     */
    Module queryModuleByGradeAndUrl(@Param("grade") Integer grade,@Param("url") String url);

    /**
     * 根据权限码查询资源
     * @param optValue
     * @return
     */
    Module queryModuleByOptValue(String optValue);

    /**
     * 通过父id查询资源记录
     * @param id
     * @return
     */
    Integer queryCountByParentId(Integer id);



}