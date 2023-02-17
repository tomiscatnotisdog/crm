package com.xxxx.crm.interceptot;

import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 非法访问拦截
 */
public class NoLoginInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private UserMapper userMapper;

    /**
     * 目标方法执行前执行
     *      通过查看请求是否是登陆状态,如果是登陆状态则放行,否则进行拦截
     *       获取cookie 解析用户id
     * *  如果用户id存在 并且 数据库存在对应用户记录 放行 否
     * 则进行拦截 重定向到登录页面
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //获取请求中的cookie 并解析用户Id
        Integer userId= LoginUserUtil.releaseUserIdFromCookie(request);
        //判断userId是否为空,或者数据库中是否存在对应对象
        if (userId==null || userMapper.selectByPrimaryKey(userId)==null){
            //爆出未登陆异常
            throw new NoLoginException();
        }

        return true;
    }
}
