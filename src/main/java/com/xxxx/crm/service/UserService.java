package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User,Integer> {

    //注入Dao层的依赖
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     *用户登陆
     * Service层 （业务逻辑层：非空判断、条件判断等业务逻辑处理）
     *     1. 参数判断，判断用户姓名、用户密码非空弄
     *         如果参数为空，抛出异常（异常被控制层捕获并处理）
     *     2. 调用数据访问层，通过用户名查询用户记录，返回用户对象
     *     3. 判断用户对象是否为空
     *         如果对象为空，抛出异常（异常被控制层捕获并处理）
     *     4. 判断密码是否正确，比较客户端传递的用户密码与数据库中查询的用户对象中的用户密码
     *         如果密码不相等，抛出异常（异常被控制层捕获并处理）
     *     5. 如果密码正确，登录成功
     */
    public UserModel userLogin(String userName, String userPwd){

        //1. 参数判断，判断用户姓名、用户密码非空-->如果参数为空，抛出异常（异常被控制层捕获并处理）
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名称不可以为空!");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不可以为空!");
        //2. 调用数据访问层，通过用户名查询用户记录，返回用户对象
        User user=userMapper.queryUserByUserName(userName);
        //3. 判断用户对象是否为空-->如果对象为空，抛出异常（异常被控制层捕获并处理）
        AssertUtil.isTrue(user==null,"你所登陆的用户不存在!");
        //4. 判断密码是否正确，比较客户端传递的用户密码与数据库中查询的用户对象中的用户密码-->如果密码不相等，抛出异常（异常被控制层捕获并处理）
        AssertUtil.isTrue(!Md5Util.encode(userPwd).equals(user.getUserPwd()),"密码错误,请重新输入!");
        //5. 如果密码正确，登录成功,创建UserModel对象,返回用户数据
        UserModel userModel=new UserModel();
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(userName);
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /**
     * 添加用户记录
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertUser(User user){
        //1.参数校验(用户名,邮箱,手机号)
        addUserParamProof(user,null);
        //2.设置默认值(有效值,创建时间,修改时间,默认密码)
        addUserDefaultValue(user);
        //3.调用添加方法
        AssertUtil.isTrue(userMapper.insertSelective(user)<1,"用户添加失败!");

        /*用户角色关联*/
        relationUserRole(user.getId(),user.getRoleIds());
    }

    /**
     * 用户角色关联
     * @param userId
     * @param roleIds
     */
    private void relationUserRole(Integer userId, String roleIds) {
        //1.通过用户id查看是否存在对应的角色记录
        Integer count=userRoleMapper.queryUserRoleByUserId(userId);
        //2.判断角色记录是否存在
        if (count>0){
            //3.存在就删除相应的记录
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色关联失败!");
        }
        //4.判断传入的角色是否为空,如果不为空,则添加新的角色记录到用户角色表中
        if (StringUtils.isNotBlank(roleIds)){
            //将传入的角色id字符串分割成数组
            String[] roleIdArr=roleIds.split(",");
            //遍历数组,得到对应的用户角色对象,放到list集合中
            List<UserRole> userRoleList=new ArrayList<>();
            for (String roleId:roleIdArr) {
                UserRole userRole=new UserRole(); //创建userRole对象
                userRole.setRoleId(Integer.parseInt(roleId)); //设置角色id
                userRole.setUserId(userId); //设置对应的用户ID
                userRole.setCreateDate(new Date()); //设置角色记录的创建时间
                userRole.setUpdateDate(new Date()); //设置角色记录的修改时间
                userRoleList.add(userRole);
            }
            //批量添加用户角色记录
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList)!=userRoleList.size(),"用户角色关联失败!");
        }
    }


    /**
     * 更新用户记录
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user){
        //1.参数校验(用户id,用户是否存在,用户名,邮箱,手机号)
        AssertUtil.isTrue(user.getId()==null,"待更新的记录不存在!"); //判断是否存在用户ID
        User tempUser=userMapper.selectByPrimaryKey(user.getId()); //查询id对应的用户
        AssertUtil.isTrue(tempUser==null,"待更新的记录不存在"); //判断是否存在该id的用户

        addUserParamProof(user,tempUser.getId());
        //2.设置默认值(修改时间)
        user.setUpdateDate(new Date());
        //3.调用添加方法
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)!=1,"用户添加失败!");

        /*用户角色关联*/
        relationUserRole(user.getId(),user.getRoleIds());

    }

    /**
     * 根据主键ID批量删除用户记录
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUsersByids(Integer[] ids){
        //参数校验
        AssertUtil.isTrue(  null==ids || ids.length < 1,"请选择你要删除的营销记录!");
        //执行删除(修改is_valid值)
        AssertUtil.isTrue(userMapper.deleteBatch(ids)!=ids.length,"营销记录删除失败!");
        //遍历用户id的数组
        for (Integer userId: ids){
            //查询相应的用户id是否存在对应的角色记录
            int count=userRoleMapper.queryUserRoleByUserId(userId);
            //判断是否存在角色记录
            if (count>0){
                //存在则删除用户所绑定的对应角色
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户解除角色失败!");
            }
        }
    }




    /**
     * 设置添加用户的参数
     * @param user
     *///有效值,创建时间,修改时间,默认密码
    private void addUserDefaultValue(User user) {
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
    }

    /**
     * 添加用户的参数校验
     * @param user
     */
    private void addUserParamProof(User user,Integer tempUserId){
        AssertUtil.isTrue(StringUtils.isBlank(user.getUserName()),"用户名不可以为空");
        //判断所添加的用户是否存在(而且是否为自己)将通过用户名查询到的用户的id和传入的id对比(不是一个则表示被占用,不能使用)
        User tempUser=userMapper.queryUserByUserName(user.getUserName());
        AssertUtil.isTrue(tempUser!=null && !tempUser.getId().equals(tempUserId),"所添加的用户已存在!");
        //判断邮箱
        AssertUtil.isTrue(StringUtils.isBlank(user.getEmail()),"用户邮箱不可以为空!");
        //判断手机号
        AssertUtil.isTrue(StringUtils.isBlank(user.getPhone()),"用户的手机号不可以为空!");
        //判断手机号的格式
        AssertUtil.isTrue(!PhoneUtil.isMobile(user.getPhone()),"手机号格式不正确!");
    }

    /**
     * 修改密码
     * Service层
     *     1. 接收四个参数 （用户ID、原始密码、新密码、确认密码）
     *     2. 通过用户ID查询用户记录，返回用户对象
     *     3. 参数校验
     *         待更新用户记录是否存在 （用户对象是否为空）
     *         判断原始密码是否为空
     *         判断原始密码是否正确（查询的用户对象中的用户密码是否原始密码一致）
     *         判断新密码是否为空
     *         判断新密码是否与原始密码一致 （不允许新密码与原始密码）
     *         判断确认密码是否为空
     *         判断确认密码是否与新密码一致
     *     4. 设置用户的新密码
     *         需要将新密码通过指定算法进行加密（md5加密）
     *     5. 执行更新操作，判断受影响的行数
     * @param userid
     * @param oldPassword
     * @param newPassword
     * @param ackPassword
     */         //1. 接收四个参数 （用户ID、原始密码、新密码、确认密码）
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPassword(Integer userid,String oldPassword,String newPassword,String ackPassword){
        //2. 通过用户ID查询用户记录，返回用户对象
        User user=userMapper.selectByPrimaryKey(userid);

        //3. 参数校验
        //待更新用户记录是否存在 （用户对象是否为空）
        AssertUtil.isTrue(user==null,"用户未登陆或不存在!");
        //判断原始密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"原始密码不可以为空!");
        //判断原始密码是否正确（查询的用户对象中的用户密码是否原始密码一致）

        AssertUtil.isTrue(!Md5Util.encode(oldPassword).equals(user.getUserPwd()),"原始密码不正确!");
        //判断新密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"新密码不可以为空!");
        //判断新密码是否与原始密码一致 （不允许新密码与原始密码）
        AssertUtil.isTrue(newPassword.equals(oldPassword),"新密码不可以和原始密码相同!");
        //判断确认密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(ackPassword),"确认密码不可以为空!");
        //判断确认密码是否与新密码一致
        AssertUtil.isTrue(!newPassword.equals(ackPassword),"新密码和确认密码要保持一致");

        //4. 设置用户的新密码-->需要将新密码通过指定算法进行加密（md5加密）
        user.setUserPwd(Md5Util.encode(newPassword));
        //5. 执行更新操作，判断受影响的行数
        AssertUtil.isTrue(userMapper.updatePasswordByUserId(user)<1,"修改失败!");
    }

    /**
     * 查询所有的销售人员
     * @return
     */
    public List<Map<String,Object>> queryAllSalse() {
        return userMapper.queryAllSalse();
    }
}
