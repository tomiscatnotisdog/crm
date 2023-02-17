package com.xxxx.crm.controller;

import com.xxxx.crm.annoation.RequiredParmission;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.enums.DevResult;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 营销机会管理模块
 */
@RequestMapping("sale_chance")
@Controller
public class SaleChanceController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    /**
     * 修改营销机会的开发状态
     * @param saleChanceId
     * @param devResult
     * @return
     */
    @PostMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer saleChanceId,Integer devResult){
        saleChanceService.updateSaleChanceDevResult(saleChanceId,devResult);
        return success("操作成功!");
    }

    /**
     * 修改营销机会
     * @return
     */
    @RequiredParmission(code = 101004)
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(SaleChance saleChance){

        //调用service中的添加方法
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会修改成功!");
    }


    /**
     * 从cookie中获取添加记录的对象,获取其用户名称,添加到saleChance对象中
     *  调用service层的添加方法
     *      返回ResultInfo对象
     * @param saleChance
     * @return
     */
    @RequiredParmission(code = 101002)
    @PostMapping("insert")
    @ResponseBody
    public ResultInfo insertSaleChance(SaleChance saleChance, HttpServletRequest request){
        //1.从请求对象中获取对应的cookie值
        String userName = CookieUtil.getCookieValue(request, "userName");
        //2.将添加记录的用户名添加到saleChanse对象
        saleChance.setCreateMan(userName);
        //3.调用service中的添加方法
        saleChanceService.insertSaleChance(saleChance);
        return success("营销机会添加成功!");
    }


    /**
     * 根据主键批量删除营销机会
     */
    @ResponseBody
    @PostMapping("delete")
    @RequiredParmission(code = 101003)
    public ResultInfo deleteBatchSaleChance(Integer[] ids){
        //调用service层的删除方法
        saleChanceService.deleteBatchSaleChance(ids);
        //返回ResultInfo对象
        return success("营销机会记录删除成功!");
    }



    /**
     * 营销机会管理展示
     * @param saleChanceQuery
     * @return
     */
    @RequiredParmission(code = 101001)
    @ResponseBody
    @RequestMapping("list")
    public Map<String,Object> queryByParamsForTable(SaleChanceQuery saleChanceQuery,Integer flag,HttpServletRequest request){

        //判断前台传入的flag的值,如果为'1',表示查询的是客户开发计划,否则查询的是营销机会管理
        if (flag !=null && flag==1){

            //获取指派人|分配人的userId
            Integer userId= LoginUserUtil.releaseUserIdFromCookie(request);
            //设置指派人的userid
            saleChanceQuery.setAssignMan(userId);
            //设置营销机会的分配状态
            saleChanceQuery.setState(StateStatus.STATED.getType());
        }
        return saleChanceService.queryByParamsForTable(saleChanceQuery);
    }



    /**
     * 跳转到营销机会管理
     * @return
     */
    @RequiredParmission(code = 1010)
    @RequestMapping("index")
    public String index(){
        return "/saleChance/sale_chance";
    }


    /**
     * 跳转到添加营销机会页面
     * @return
     */
    @RequestMapping("toAddOrUpdatePage")
    public String toAddOrUpdatePage(Integer id,HttpServletRequest request){

        //判断是否存在记录id  存在则修改  否则为添加
        if (id != null){
            //查询相应的记录
            SaleChance saleChance=saleChanceService.selectByPrimaryKey(id);
            //将对象设置到请求域中
            request.setAttribute("saleChance",saleChance);

        }

        return "/saleChance/add_update";
    }

}
