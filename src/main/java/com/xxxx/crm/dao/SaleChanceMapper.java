package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.SaleChance;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Repository;

public interface SaleChanceMapper extends BaseMapper<SaleChance,Integer> {

    /**
     * 营销机会展示
     *      selectByParams(baseQuery) 方法
     */
    /*多个地方需要进行查询,将方法定义在父类BaseMapper中*/

    /**
     * 添加营销记录返回行数
     *
     * @return
     * public Integer insertSelective(SaleChance saleChance) throws DataAccessException;
     */


    /**
     * 修改营销机会
     * @param saleChance
     * @return
     */
    public abstract int updateSaleChance(SaleChance saleChance);

    /**
     * 根据主键ID进行批量删除
     * deleteBatch
     */

}