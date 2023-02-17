package com.xxxx.crm.aspect;

import com.xxxx.crm.annoation.RequiredParmission;
import com.xxxx.crm.exceptions.AuthException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@Aspect
public class PermissionProxy {

    @Resource
    private HttpSession session;

    /**
     * 切面会拦截制定包下的指定注解
     * 拦截@annotation(com.xxxx.crm.annoation.RequiredParmission)
     * @param pjp
     * @return
     */
    @Around(value = "@annotation(com.xxxx.crm.annoation.RequiredParmission)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object result=null;
        //得到当前登陆用户拥有的权限(session作用域)
        List<String> permissions= (List<String>) session.getAttribute("permissions");
        if (permissions==null || permissions.size()<1){
            //抛出自定义异常(认证异常)
            throw new AuthException();
        }

        //得到对应的目标
        MethodSignature methodSignature= (MethodSignature) pjp.getSignature();
        //得到方法上的注解
        RequiredParmission requiredParmission=methodSignature.getMethod().getDeclaredAnnotation(RequiredParmission.class);
        //判断对应注解上的状态码
        if (!permissions.contains(requiredParmission.code())){
            //如果用户所拥有的权限码不包含状态码中的权限码抛出自定义异常(认证异常)
            throw new AuthException();
        }
        result=pjp.proceed();
        return result;
    }
}
