package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.service.PermissionService;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    @Resource
    private UserService userService;
    @Resource
    private PermissionService permissionService;

    /**
     * 系统登录⻚
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "index";
    }

    // 系统界⾯欢迎⻚
    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }
    /**
     * 后端管理主⻚⾯
     * @return
     */
    @RequestMapping("main")
    public String main(HttpServletRequest request) {
        // 通过⼯具类，从cookie中获取userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 调⽤对应Service层的⽅法，通过userId主键查询⽤户对象
        User user = userService.selectByPrimaryKey(userId);
        // 将⽤户对象设置到request作⽤域中
        request.getSession().setAttribute("user", user);

        //通过当前登陆用户id查询用户拥有的资源列表(查询对应资源的授权码)
        List<String> permissions=permissionService.queryUserHasPermissionByUserId(userId);
        //将集合设置到session作用域中
        request.getSession().setAttribute("permissions",permissions);

        return "main";
    }

}
