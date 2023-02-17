layui.use(['form', 'layer', 'formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    var formSelects = layui.formSelects;


    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        //当你在iframe页面关闭自身时
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });

    /**
     * 监听提交事件(添加用确认按钮)
     */
    form.on('submit(addOrUpdateUser)', function (data) {
        //console.log(data.field);//当前容器的全部表单字段，名值对形式：{name: value}-->saleChance对象
        var index = top.layer.msg('数据提交中，请稍候', {
            icon: 16,  //图标
            time: false,  //不关闭
            shade: 0.5 //设置遮罩的透明度
        });

        var url = ctx + "/user/insert";  //提交的地址
        var record = data.field;//提交的数据
        var successMsg = "用户记录添加成功!";  //操作成功提示信息
        var errorMsg = "用户记录添加失败!";  //操作失败提示信息

        //判断窗口上是否存在Id属性值  通过元素选择器来拿
        var saleChanceId = $("[name='id']").val();

        if (saleChanceId != null && saleChanceId != '') {
            successMsg = "用户记录修改成功!";
            errorMsg = "用户记录修改失败!";
            url = ctx + "/user/update";
        }

        $.post(url, record, function (resultInfo) {
            //判断从后台返回的结果
            if (resultInfo.code === 200) {
                //添加成功
                layer.msg(successMsg, {icon: 6});
                //关闭加载层
                layer.close(index);
                //关闭表单
                layer.closeAll("iframe");
                //刷新父窗口,重新加载营销机会展示
                parent.location.reload();
            } else {
                //添加失败
                layer.msg(errorMsg, {icon: 5})
            }
        });
        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });


    /**
     * 1.配置远程搜索, 请求头, 请求参数, 请求类型等
     * formSelects.config(ID, Options, isJson);
     * @param ID        xm-select的值
     * @param Options   配置项
     * @param isJson    是否传输json数据, true将添加请求头 Content-Type: application/json; charset=UTF-8
     */
    //获取当前用户的id,便于查看拥有哪些角色
    var userId=$("[name='id']").val();
    formSelects.config("selectId",{
        type: 'post',  //请求方式: post, get, put, delete...
        searchUrl:ctx+ '/role/queryAllRoles?userId='+userId, //搜索地址, 默认使用xm-select-search的值, 此参数优先级高
        keyName: 'roleName', //自定义返回数据中name的key, 默认 name
        keyVal: 'id' //自定义返回数据中value的key, 默认 value
    },true)
});