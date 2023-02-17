package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.CusDevPlanService;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("cus_dev_plan")
public class CusDevPlanController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;
    @Resource
    private CusDevPlanService cusDevPlanService;

    /**
     * 进入客户开发计划
     * @return
     */
    @RequestMapping("index")
    public String cus_dev_plan(){
        return "/cusDevPlan/cus_dev_plan";
    }


    //进入客户开发计划的开发或详情页面
    @RequestMapping("toCusDevPlanPage")
    public String cus_dev_plan_data(Integer saleChanceId, HttpServletRequest request){
        //根据传入的记录Id查询开发开发计划
        SaleChance saleChance=saleChanceService.selectByPrimaryKey(saleChanceId);
        //将查询到的用户记录,存放到请求域中
        request.setAttribute("saleChance",saleChance);
        //请求转发到视图
        return "cusDevPlan/cus_dev_plan_data";
    }


    /**
     * 客户开发计划展示
     * @param cusDevPlanQuery
     * @return
     */
    @ResponseBody
    @RequestMapping("list")
    public Map<String,Object> queryByParamsForTable(CusDevPlanQuery cusDevPlanQuery){
        return cusDevPlanService.queryCusDevPlanForTable(cusDevPlanQuery);
    }

    /**
     * 添加开发计划项
     * @param cusDevPlan
     * @return
     */
    @PostMapping("insert")
    @ResponseBody
    public ResultInfo insertCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.insertCusDevPlan(cusDevPlan);
        return success("计划项添加成功!");
    }

    /**
     * 修改开发计划项
     * @param cusDevPlan
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("计划项修改成功!");
    }


    /**
     * 删除开发计划项
     * @param cusDevPlanId
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(Integer cusDevPlanId){
        cusDevPlanService.deleteCusDevPlan(cusDevPlanId);
        return success("计划项删除成功!");
    }

    /**
     * 返回添加计或修改计划项的视图
     * @return
     */
    @RequestMapping("toAddOrUpdateCusDevPlan")
    public String toAddOrUpdateCusDevPlan(Integer sid,HttpServletRequest request,Integer id){

        //将营销机会的id放到请求域中,供添加计划项时获取
        request.setAttribute("sid",sid);
        //通过计划项id查询,将查询到的记录存到请求域中
        CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
        request.setAttribute("cusDevPlan",cusDevPlan);
        //返回视图
        return "cusDevPlan/add_update";

    }
}
