package com.xxxx.crm.config;

import com.xxxx.crm.interceptot.NoLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@Configuration    //配置类-->注释取消拦截
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Bean //将方法的返回值交给IOC容器进行维护
    public NoLoginInterceptor noLoginInterceptor(){
        return new NoLoginInterceptor();
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //需要一个实现类拦截器功能的示例对象,这里使用的是noLoginInterceptor
        registry.addInterceptor(noLoginInterceptor())
                //设置需要被拦截的资源
                .addPathPatterns("/**") //默认拦截所有资源
                //设置不需要被拦截的资源
                .excludePathPatterns("/css/**","/images/**","/js/**","/lib/**","/index","/user/login"); //需要放行的资源


        super.addInterceptors(registry);
    }
}
