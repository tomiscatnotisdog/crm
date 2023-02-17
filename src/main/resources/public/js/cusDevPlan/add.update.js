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
     * 绑定确认按钮的submit事件
     */
    form.on("submit(addOrUpdateCusDevPlan)", function (data) {
        var index = top.layer.msg('数据提交中，请稍候', {icon: 16, time: false, shade: 0.8});
        //弹出loading
        var url=ctx + "/cus_dev_plan/insert";
        //判断窗口上是否存在Id属性值  通过元素选择器来拿
        var saleChanceId = $("[name='id']").val();

        if (saleChanceId != null && saleChanceId != '') {
            url = ctx + "/cus_dev_plan/update";
        }


        $.post(url, data.field, function (result) {
            if (result.code === 200) {
                setTimeout(function () {
                    top.layer.close(index);
                    top.layer.msg("操作成功！");
                    layer.closeAll("iframe");
                    //刷新父页面
                    parent.location.reload();
                }, 500);
            } else {
                layer.msg(
                    result.msg, {
                            icon: 5
                        }
                    );
            }
        });
        return false;
    });
});