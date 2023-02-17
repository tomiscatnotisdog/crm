package com.xxxx.crm.controller;


import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    //注入service层的依赖
    @Resource
    private UserService userService;

    /**
     * 用户登陆
     * Controller层 （控制层：接收请求、响应结果）
     *     1. 通过形参接收客户端传递的参数
     *     2. 调用业务逻辑层的登录方法，得到登录结果
     *     3. 响应数据给客户端
     * @return
     */
    @ResponseBody
    @PostMapping("login")
    public ResultInfo userLogin(String userName, String userPwd){
        ResultInfo resultInfo=new ResultInfo();
        //调用方法
        UserModel userModel = userService.userLogin(userName, userPwd);
        //存储对象
        resultInfo.setResult(userModel);
        /*try {

        } catch (ParamsException pe) {
            resultInfo.setMsg(pe.getMsg());
            resultInfo.setCode(pe.getCode());
            pe.printStackTrace();

        }catch (Exception e){
            resultInfo.setCode(500);
            resultInfo.setMsg("登陆失败!");
        }*/

        return resultInfo;
    }

    /**
     *  修改密码Controller层
     *     1. 通过形参接收前端传递的参数 （原始密码、新密码、确认密码）
     *     2. 通过request对象，获取设置在cookie中的用户ID
     *     3. 调用Service层修改密码的功能，得到ResultInfo对象
     *     4. 返回ResultInfo对象
     * @return
     */
    @ResponseBody
    @PostMapping("updatePassword")
    public ResultInfo updatePassword(HttpServletRequest request,String oldPassword,String newPassword,String ackPassword) {
        ResultInfo resultInfo=new ResultInfo();

        //1. 通过request对象，获取设置在cookie中的用户ID
        Integer userId= LoginUserUtil.releaseUserIdFromCookie(request);
        //2. 调用Service层修改密码的功能，得到ResultInfo对象
        userService.updateUserPassword(userId,oldPassword,newPassword,ackPassword);

        /*try {

        } catch (ParamsException pe) {//自定义异常
            pe.printStackTrace();
            //设置状态码和提示信息
            resultInfo.setMsg(pe.getMsg());
            resultInfo.setCode(pe.getCode());
        }catch (Exception ex){
            resultInfo.setCode(500);
            resultInfo.setMsg("修改失败!");
        }*/
        return resultInfo;
    }

    /**
     * 密码修改
     * @return
     */
    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }

    /**
     * 查询所有的销售人员
     * @return
     */
    @GetMapping("queryAllSalse")
    @ResponseBody
    public List<Map<String,Object>> queryAllSalse(){
        return userService.queryAllSalse();
    }

    /**
     * 多条件查询用户对象
     * @param userQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryByParamsForTableForUser(UserQuery userQuery){
        return userService.queryByParamsForTable(userQuery);
    }

    /**
     * 进入用户管理模块
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    /**
     * 添加用户
     * @return
     */
    @PostMapping("insert")
    @ResponseBody
    public ResultInfo insertUser(User user){
        //调用添加方法
        userService.insertUser(user);
        //返回结果
        return success("用户添加成功!");
    }

    /**
     * 修改用户
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user){
        //调用添加方法
        userService.updateUser(user);
        //返回结果
        return success("用户更新成功!");
    }

    /**
     * 返回添加用户的视图
     * @return
     */
    @RequestMapping("toAddOrUpdate")
    public String toAddOrUpdate(Integer userId,HttpServletRequest request){
        if (userId!=null){
            User user=userService.selectByPrimaryKey(userId);
            request.setAttribute("userInfo",user);
        }
        return "user/add_update";
    }

    /**
     * 单条或多条删除用户记录
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping("delete")
    public ResultInfo deleteUsers(Integer[] ids){
        userService.deleteUsersByids(ids);
        return success("用户记录删除成功!");
    }
}
