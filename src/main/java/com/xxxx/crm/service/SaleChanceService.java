package com.xxxx.crm.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.base.BaseQuery;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.enums.DevResult;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 营销机会管理
 */
@Service
public class SaleChanceService extends BaseService<SaleChance, Integer> {

    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 根据主键ID批量删除营销记录
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteBatchSaleChance(Integer[] ids){
        //参数校验
        AssertUtil.isTrue(  null==ids || ids.length < 1,"请选择你要删除的营销记录!");
        //执行删除(修改is_valid值)
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids)!=ids.length,"营销记录删除失败!");
    }

    /**
     * 通过SaleChance对象添加营销记录对象
     * /**
     * 1.参数校验
     *   customerName:非空
     *   linkMan:非空
     *   linkPhone:非空 11位手机号
     * 2.设置相关参数默认值
     * isValid :默认有效数据(1-有效 0-无效)
     * createDate updateDate:默认当前系统时间
     *     state:默认未分配 如果选择分配人 state 为已分配
     *     assignTime:如果 如果选择分配人   时间为当前系统时间
     *     devResult:默认未开发
     * 如果选择分配人devResult为开发中 0-未开发 1-开发中 2-开发成功 3-开发失败
     * 3.执行添加 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertSaleChance(SaleChance saleChance) {
        /**
         * 1.参数校验
         *   customerName:非空 客户名称
         *   linkMan:非空  联系人
         *   linkPhone:非空 11位手机号  联系电话
         */
        paramsProof(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());

        //2.设置相关参数默认值
        //isValid :默认有效数据(1-有效 0-无效)
        saleChance.setIsValid(1);
        //createDate updateDate:默认当前系统时间
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        //state:默认未分配 如果选择分配人 state 为已分配
        if (StringUtils.isBlank(saleChance.getAssignMan())) {
            //是空则设置未分配
            saleChance.setState(StateStatus.UNSTATE.getType());
            //devResult:默认未开发-->如果选择分配人devResult为开发中 0-未开发 1-开发中 2-开发成功 3-开发失败
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        } else {
            //设置已分配
            saleChance.setState(StateStatus.STATED.getType());
            //assignTime:如果 如果选择分配人   时间为当前系统时间
            saleChance.setAssignTime(new Date());
            //开发状态为开发中
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        }

        //3.调用添加方法
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance)!=1,"营销机会添加失败!");
    }



    /**
     * 通过id修改对应的营销机会
     * @param saleChance 要修改成的记录
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance){


        //营销机会id 非空  数据库中存在对应的记录
        AssertUtil.isTrue(null==saleChance.getId(),"请填入所待修改的记录ID!");
        //查询数据库中是否存在对应的对象
        SaleChance tempChance=saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        //判断要更新的对象是否存在
        AssertUtil.isTrue(tempChance==null,"待修改的营销记录不存在!");
        /**
         * 参数校验
         * customerName:非空
         * linkMan:非空
         * linkPhone:非空 11位手机号
         */
        paramsProof(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        /**
         * 设置默认值
         * updateDate 设置为当前系统时间
         * assignMan指派人
         *   原始记录中未设置
         *      修改后设置
         *          assignTime 设置为当前系统时间
         *          分配状态设置为已分配 1
         *          开发状态设置为开发中 1
         *      修改后未设置
         *          无须操作
         *   原始记录中已设置
         *      修改后设置
         *          判断原始记录和现在是否为一个人
         *              是
         *                  无须操作
         *              不是
         *                  指派时间为当前系统时间
         *      修改后未设置
         */
        //updateDate 设置为当前系统时间
        saleChance.setUpdateDate(new Date());
        //判断原始记录中是否存在
        if (tempChance.getAssignMan()==null){//原始记录中不存在
            //判断修改后是否存在
            if (saleChance.getAssignMan()!=null){//修改后存在
                saleChance.setAssignTime(new Date()); //指派时间
                saleChance.setDevResult(DevResult.DEVING.getStatus()); //开发结果
                saleChance.setState(StateStatus.STATED.getType()); //分配状态
            }
        }else {//原始记录中已经存在
            if (saleChance.getAssignMan()==null){ //修改后不存在
                saleChance.setAssignTime(null); //指派时间
                saleChance.setDevResult(DevResult.UNDEV.getStatus()); //开发结果
                saleChance.setState(StateStatus.UNSTATE.getType()); //分配状态
            }else {//修改后存在
                //判断修改前后是否为一个人
                if (!tempChance.getAssignMan().equals(saleChance.getAssignMan())){
                    saleChance.setAssignTime(new Date()); //指派时间
                }
            }
        }

        //执行更新操作,返回受影响的行数
        AssertUtil.isTrue(saleChanceMapper.updateSaleChance(saleChance)!=1,"营销机会修改失败!");
    }

    /**
     * 修改营销机会的开发状态
     * @param saleChanceId
     * @param devResult
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChanceDevResult(Integer saleChanceId,Integer devResult){
        //1.参数校验
        AssertUtil.isTrue(saleChanceId==null,"数据异常,请重试!");
        //2.查询记录,修改状态
        SaleChance saleChance=saleChanceMapper.selectByPrimaryKey(saleChanceId);
        AssertUtil.isTrue(saleChance==null,"待更新的记录不存在!");
        saleChance.setDevResult(devResult);
        //3.修改记录
        AssertUtil.isTrue(saleChanceMapper.updateSaleChance(saleChance)!=1,"开发状态更新失败!");
    }



    /**
     * 参数校验
     * @param customerName   客户名称
     * @param linkMan 联系人
     * @param linkPhone 联系电话
     */
    private void paramsProof(String customerName, String linkMan, String linkPhone) {
        //对客户名称进行校验
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "客户名称不可以为空!");
        //对联系人进行非空校验
        AssertUtil.isTrue(StringUtils.isBlank(linkMan), "联系人不可以为空!");
        //对联系人电话进行非空校验
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone), "联系电话不可以为空!");
        //判断联系电话的格式是否正确
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone), "联系电话的格式不正确!");
    }



    /**
     * 营销机会展示
     * @param baseQuery
     * @return
     */
    public Map<String, Object> queryByParamsForTable(BaseQuery baseQuery) {
        Map<String, Object> map = new HashMap<>();

        //使用页面帮助工具 startPage-->开启分页  startPage(当前页,每页显示的数量)
        PageHelper.startPage(baseQuery.getPage(), baseQuery.getLimit());
        //设置分页信息    selectByParams(baseQuery)要分页的数据
        PageInfo<SaleChance> pageInfo = new PageInfo<>(selectByParams(baseQuery));

        //设置map集合
        map.put("count", pageInfo.getTotal());  //记录数量
        map.put("data", pageInfo.getList());  //数据
        map.put("code", 0);  //状态码
        map.put("msg", "");  //提示信息
        return map;
    }

}
