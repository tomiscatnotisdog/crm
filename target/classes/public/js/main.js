layui.use(['element', 'layer', 'layuimini','jquery','jquery_cookie'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        $ = layui.jquery_cookie($);

    // 菜单初始化
    $('#layuiminiHomeTabIframe').html('<iframe width="100%" height="100%" frameborder="0"  src="welcome"></iframe>')
    layuimini.initTab();

    /**
     * .class类选择器
     *  退出登陆
     */
    $(".login-out").click(function () {
        /*

        layer.confirm('问题', {icon: 3, title:'提示标题'}, function(index){
            //do something

            layer.close(index);
        })

        * */                                                            //点击确定要调用的函数
        layer.confirm('你确定要退出登陆吗?', {icon: 3, title:'系统提示'}, function(index){
            //1.关闭提示框
            layer.close(index);
            //2.清除cookie
            $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
            $.removeCookie("userName",{domain:"localhost",path:"/crm"});
            $.removeCookie("trueName",{domain:"localhost",path:"/crm"});
            //3.跳转到登陆页面
            window.parent.location.href=ctx+"/index"
        })

    })
});