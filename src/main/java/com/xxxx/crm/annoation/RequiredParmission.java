package com.xxxx.crm.annoation;


import java.lang.annotation.*;

/**
 * 定义方法需要的对应的权限码
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredParmission {
    //权限码
    int code() default 0;
}
