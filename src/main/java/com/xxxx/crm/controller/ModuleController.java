package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.service.ModuleService;
import com.xxxx.crm.vo.Module;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RequestMapping("module")
@Controller
public class ModuleController extends BaseController {

    @Resource
    private ModuleService moduleService;

    @RequestMapping("index")
    public String index(){
        return "module/module";
    }

    /**
     * 添加资源
     * @param module
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addModule(Module module){
        moduleService.addModule(module);
        return success("资源添加成功!");
    }

    /**
     * 添加资源
     * @param module
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateModule(Module module){
        moduleService.updateModule(module);
        return success("资源更新成功!");
    }

    /**
     * 删除资源(修改其有效值)
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("delete")
    public ResultInfo deleteModule(Integer id){
        moduleService.deleteModule(id);
        return success("资源删除成功!");
    }

    /**
     * 查询所有的模块信息
     * @return
     */
    @ResponseBody
    @RequestMapping("queryAllModules")
    public List<TreeModel> queryAllModules(Integer roleId){
        return moduleService.queryAllModules(roleId);
    }

    /**
     * 打开授权页面
     * @return
     */
    @RequestMapping("toGrantPage")
    public String toGrantPage(Integer roleId, HttpServletRequest request){
        //将要授权的角色id返回给前台
        request.setAttribute("roleId",roleId);
        return "/role/grant";
    }


    /**
     * 查询所有的资源列表
     * @return
     */
    @ResponseBody
    @RequestMapping("list")
    public Map<String,Object> queryModuleList(){
        return  moduleService.queryModuleList();
    }

    /**
     * 打开添加模块页面
     * @return
     */
    @RequestMapping("toAddModulePage")
    public String toAddModulePage(Integer grade,Integer parentId,HttpServletRequest request){
        //将接收到的参数放到请求域中
        request.setAttribute("grade",grade);
        request.setAttribute("parentId",parentId);
        //转发到对应的视图
        return "/module/add";
    }

    @RequestMapping("toUpdateModulePage")
    public String toUpdateModulePage(Integer id,HttpServletRequest request){
        request.setAttribute("module",moduleService.selectByPrimaryKey(id));
        return "/module/update";
    }


}
