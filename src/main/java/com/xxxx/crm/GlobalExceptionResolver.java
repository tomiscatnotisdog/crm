package com.xxxx.crm;

import com.alibaba.fastjson.JSON;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.AuthException;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.PrintWriter;

/**
 * 全局异常处理类
 */
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    /**
     * 处理方法 根据返回的结果判断是响应view还是json数据
     * 两种响应方法:1.视图  2.Json数据
     * 如何判断返回的是视图还是数据:通过判断对应的方法上是否含有@ResponseBody注解
     * 有,则为Json数据   无,则为view视图
     *
     * @param request  request请求对象
     * @param response response响应对象
     * @param handler  处理方法
     * @param ex       产生的异常
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {


        //先创建一个默认异常处理
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("code", 500);
        modelAndView.addObject("msg", "系统异常,请稍后重试...");



        /**
         * 判断是否为未登录异常
         *  如果抛出该异常,则要求用户登陆,重定向到登陆yemian
         */

        if (ex instanceof NoLoginException){
            NoLoginException nx= (NoLoginException) ex;
            modelAndView.setViewName("redirect:/index");
            modelAndView.addObject("code",nx.getCode());
            modelAndView.addObject("msg", nx.getMsg());
            return modelAndView;
        }


        // 判断handler处理程序是否是一个程序处理方法
        if (handler instanceof HandlerMethod) {
            //是的话,进行强转
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //通过程序处理方法获取方法上的@ResponseBody注解
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            //判断@ResponseBody是否存在,存在则返回的是一个Json数据,如果不存在,则返回的是一个视图
            if (responseBody == null) {
                /**
                 * 方法返回视图
                 */
                if (ex instanceof ParamsException) {
                    //判断异常是否为自定义异常
                    ParamsException px = (ParamsException) ex;
                    //修改视图
                    modelAndView.addObject("code", px.getCode());
                    modelAndView.addObject("msg", px.getMsg());

                }else if (ex instanceof AuthException){
                    AuthException ax= (AuthException) ex;
                    modelAndView.setViewName("redirect:/index");
                    modelAndView.addObject("code",ax.getCode());
                    modelAndView.addObject("msg", ax.getMsg());
                    return modelAndView;
                }
                //返回结果
                return modelAndView;
            } else {
                /**
                 * 方法返还Json数据
                 */
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常,请稍后重试...");
                //判断异常是否为自定义异常
                if (ex instanceof ParamsException) {
                    //强转转换
                    ParamsException px = (ParamsException) ex;
                    //修改数据
                    resultInfo.setMsg(px.getMsg());
                    resultInfo.setCode(px.getCode());
                }else if (ex instanceof AuthException) { //认证异常
                    //强转转换
                    AuthException ax = (AuthException) ex;
                    //修改数据
                    resultInfo.setMsg(ax.getMsg());
                    resultInfo.setCode(ax.getCode());
                }
                // 设置响应类型和编码格式 （响应JSON格式）
                response.setContentType("application/json;charset=utf-8");
                //构建输出流
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    //将对象转换为Json的数据格式进行输出
                    out.write(JSON.toJSONString(resultInfo));
                    //刷出
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //判断输出流是否存在
                    if (out != null) {
                        //关闭流
                        out.close();
                    }
                }

                return null;
            }
        }
        return modelAndView;
    }

}