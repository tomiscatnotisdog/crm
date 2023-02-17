layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    /**
     * 表单提交的监听
     *    form.on('submit(按钮的lay-filter属性值)', function(data){}
     */
    form.on('submit(saveBtn)',function(data){

        /*表单校验*/

        /*提交ajax请求*/
        $.ajax({
            type:"post",
            url:ctx+"/user/updatePassword",
            data:{
                oldPassword:data.field.old_password,
                newPassword:data.field.new_password,
                ackPassword:data.field.again_password
            },
            dataType:"JSON",
            success:function (resultInfo) {
                layer.msg("xxxx",{icon:7});

                console.log(resultInfo); //打印前台返回的数据

                //对数据进行判断
                if (resultInfo.code===200){
                    //密码修改成功,自动退出登陆
                    layer.msg("密码修改成功,系统将在3秒后退出,请重新登陆...",function () {
                        //退出系统后,将原有的cookie删除   domain-->范围   path-->路径
                        $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
                        $.removeCookie("userName",{domain:"localhost",path:"/crm"});
                        $.removeCookie("trueName",{domain:"localhost",path:"/crm"});
                        //跳转到登陆页面
                        window.parent.location.href=ctx+"/index";
                    })

                }else {
                    layer.msg(resultInfo.msg,{icon:5})
                }

            }
        })

    })





});