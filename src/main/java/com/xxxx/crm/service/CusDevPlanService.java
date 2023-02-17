package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseQuery;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CusDevPlanMapper;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CusDevPlanService extends BaseService<CusDevPlan, Integer> {

    @Resource
    private CusDevPlanMapper cusDevPlanMapper;
    @Resource
    private SaleChanceService saleChanceService;

    /**
     * 用户开发计划展示
     *
     * @param cusDevPlanQuery
     * @return
     */
    public Map<String, Object> queryCusDevPlanForTable(CusDevPlanQuery cusDevPlanQuery) {
        Map<String, Object> map = new HashMap<>();

        //使用页面帮助工具 startPage-->开启分页  startPage(当前页,每页显示的数量)
        PageHelper.startPage(cusDevPlanQuery.getPage(), cusDevPlanQuery.getLimit());
        //设置分页信息    selectByParams(baseQuery)要分页的数据
        PageInfo<CusDevPlan> pageInfo = new PageInfo<>(selectByParams(cusDevPlanQuery));

        //设置map集合
        map.put("count", pageInfo.getTotal());  //记录数量
        map.put("data", pageInfo.getList());  //数据
        map.put("code", 0);  //状态码
        map.put("msg", "");  //提示信息
        return map;
    }

    /**
     * 删除计划项
     * @param cusDevPlanId
     */
    public void deleteCusDevPlan(Integer cusDevPlanId){
        //1.进行参数校验
        AssertUtil.isTrue(cusDevPlanId==null,"数据异常,请重试!");
        //2.通过计划项查询项的数据(将其有效值改为0)
        CusDevPlan cusDevPlan=cusDevPlanMapper.selectByPrimaryKey(cusDevPlanId);
        AssertUtil.isTrue(cusDevPlan==null,"带删除的记录不存在!");
        cusDevPlan.setIsValid(0);
        cusDevPlan.setUpdateDate(new Date());
        //3.调用修改方法
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)!=1,"计划项删除失败,请重试!");
    }

    /**
     * 修改用户计划项
     *
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCusDevPlan(CusDevPlan cusDevPlan) {
        //1.进行参数的非空校验
        //判断计划项的id是否为空
        AssertUtil.isTrue(cusDevPlan.getId()==null || cusDevPlanMapper.selectByPrimaryKey(cusDevPlan.getId())==null,"数据异常,请重新!");
        paramsNotIsNull(cusDevPlan.getSaleChanceId(), cusDevPlan.getPlanItem(), cusDevPlan.getPlanDate());
        //2.设置默认值(是否有效,创建时间,修改时间)
        cusDevPlan.setUpdateDate(new Date()); //将修改时间设置为当前系统时间
        //3.调用修改方法,添加计划项
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)!=1,"计划项修改失败!");
    }

    /**
     * 添加用户计划项
     *
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertCusDevPlan(CusDevPlan cusDevPlan) {
        //1.进行参数的非空校验
        paramsNotIsNull(cusDevPlan.getSaleChanceId(), cusDevPlan.getPlanItem(), cusDevPlan.getPlanDate());
        //2.设置默认值(是否有效,创建时间,修改时间)
        setDefaultValue(cusDevPlan);
        //3.调用添加方法,添加计划项
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan)!=1,"计划项添加失败!");
    }

    /**
     * 添加计划项设置默认值
     * @param cusDevPlan
     */
    private void setDefaultValue(CusDevPlan cusDevPlan) {
        cusDevPlan.setIsValid(1); //是否有效
        cusDevPlan.setCreateDate(new Date()); //创建时间
        cusDevPlan.setUpdateDate(new Date()); //修改时间
    }

    /**
     * 添加或修改计划项的参数校验
     *
     * @param saleChanceId
     * @param planItem
     * @param planDate
     */
    private void paramsNotIsNull(Integer saleChanceId, String planItem, Date planDate) {
        //计划向所属开发计划的id校验
        AssertUtil.isTrue(saleChanceId == null || null== saleChanceService.selectByPrimaryKey(saleChanceId), "数据异常,请重试!");
        //判断开发项的内容
        AssertUtil.isTrue(StringUtils.isBlank(planItem), "开发计划项的内容不可以为空!");
        //判断开发计划项的时间
        AssertUtil.isTrue(planDate == null, "开发计划项的时间不可以为空!");
    }
}
