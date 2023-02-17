layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;


    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        //当你在iframe页面关闭自身时
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });

    /**
     * 监听提交事件
     */
    form.on('submit(addOrUpdateSaleChance)', function (data) {
        //console.log(data.field);//当前容器的全部表单字段，名值对形式：{name: value}-->saleChance对象
        var index = top.layer.msg('数据提交中，请稍候', {
            icon: 16,  //图标
            time: false,  //不关闭
            shade: 0.5 //设置遮罩的透明度
        });

        var url = ctx + "/sale_chance/insert";  //提交的地址
        var record = data.field;//提交的数据
        var successMsg = "营销机会添加成功!";  //操作成功提示信息
        var errorMsg = "营销机会添加失败!";  //操作失败提示信息

        //判断窗口上是否存在Id属性值  通过元素选择器来拿
        var saleChanceId = $("[name='id']").val();

        if (saleChanceId != null && saleChanceId != '') {
            successMsg = "营销机会修改成功!";
            errorMsg = "营销机会修改失败!";
            url = ctx + "/sale_chance/update";
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
     * 加载数据到下拉框
     */
    //$.get(提交的url地址,{配置项},成功的回调函数(后端传回的结果));
    $.get(ctx+"/user/queryAllSalse",null,function (data) {
        if (data != null) {
            //获取隐藏域设置的指派人Id
            var assignManId=$("#assignManid").val();
            var opt=null;
            //遍历返回的数据
            for (var i = 0; i < data.length; i++) {
                if (assignManId==data[i].id){
                    opt = "<option value='"+data[i].id +"' selected>" + data[i].uname + "</option>";
                }else {
                    opt = "<option value='"+ data[i].id +"'>"+ data[i].uname + "</option>";
                }
                //将下拉项添加到下拉框中
                $("#assignMan").append(opt);
            }
        }
        //重新渲染下拉框的值
        layui.form.render("select");
    });

});