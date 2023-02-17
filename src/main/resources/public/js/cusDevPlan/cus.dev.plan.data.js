layui.use(['table', 'layer'], function () {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    //加载数据表格
    var tableIns = table.render({
        //id值
        id: 'cusDevPlanId',
        //table容器的id属性值
        elem: '#cusDevPlanList',
        //容器的高度,
        height: 'full-125',
        //单元格的最小宽度
        cellMinWidth: 40,
        //每页显示的数量
        limit: 5,
        //可以选择的显示数量
        limits: [5, 10, 20, 50, 100, 50, 100],
        //后台的数据接口,
        url: ctx + '/cus_dev_plan/list?saleChanceId=' + $("[name='id']").val(),
        //开启分页
        page: true,
        //绑定头部工具栏
        toolbar: "#toolbarDemo",
        //用户列表展示
        cols: [[
            {type: "checkbox", fixed: "center"},
            {field: "id", title: '编号', fixed: "true"},
            {field: 'planItem', title: '计划项', align: "center"},
            {field: 'exeAffect', title: '执行效果', align: "center"},
            {field: 'planDate', title: '执行时间', align: "center"},
            {field: 'createDate', title: '创建时间', align: "center"},
            {field: 'updateDate', title: '更新时间', align: "center"},
            {title: '操作', fixed: "right", align: "center", minWidth: 120, templet: "#cusDevPlanListBar"}
        ]]
    });


    //监听头部工具栏
    table.on('toolbar(cusDevPlans)', function (data) {
        if (data.event === "add") {
            //添加计划项
            toAddOrUpdateCusDevPlan();
        } else if (data.event === "success") {
            updateSaleChanceDevResult($("input[name='id']").val(),2); //-->开发成功:2
        } else if (data.event === "failed") {  //updateSaleChanceDevResult(要修改的营销机会的id,要修改为的开发状态);
            updateSaleChanceDevResult($("input[name='id']").val(),3); //-->开发失败:3
        }
    });

    function updateSaleChanceDevResult(saleChanceId,devResult){
        //询问用户是否是真的要删除
        layer.confirm("你确定要执行该操作吗?", {icon: 3, title: "营销机会管理"}, function (index) {
            //点击确认后进入此方法,进入后通过索引将询问框关闭
            layer.close(index);
            //$.get(提交的url地址,提交的数据,成功的回调函数(后端传回的结果));
            //发送ajax(post)请求,删除此记录
            $.post(ctx + "/sale_chance/updateSaleChanceDevResult", {saleChanceId: saleChanceId,devResult:devResult}, function (result) {
                if (result.code == 200) {
                    //表示操作成功
                    layer.msg("操作成功!", {icon: 6});
                    //关闭窗口
                    layer.closeAll("iframe");
                    //刷新父窗口
                    parent.location.reload();
                } else {
                    //表示操作失败
                    layer.msg(result.msg, {icon: 5});
                }
            })
        });

    }


    /**
     * 监听行工具栏
     */
    table.on('tool(cusDevPlans)', function (data) {
        if (data.event === "edit") {
            //调用修改或添加营销机会的展示页面,传入营销机会id
            toAddOrUpdateCusDevPlan(data.data.id);
        } else if (data.event === "del") {
            //调用响应的方法,发送ajax请求,删除计划项
            deleteCusDevPlan(data.data.id);
        }
    });

    /**
     * 删除指定计划项
     * @param id
     */
    function deleteCusDevPlan(id) {
        //询问用户是否是真的要删除
        layer.confirm("你确定要本条记录删除吗?", {icon: 3, title: "营销机会管理"}, function (index) {
            //点击确认后进入此方法,进入后通过索引将询问框关闭
            layer.close(index);
            //$.get(提交的url地址,提交的数据,成功的回调函数(后端传回的结果));
            //发送ajax(post)请求,删除此记录
            $.post(ctx + "/cus_dev_plan/delete", {cusDevPlanId: id}, function (result) {
                if (result.code == 200) {
                    //表示删除成功
                    layer.msg("记录删除成功!", {icon: 6});
                    //刷新表格数据
                    tableIns.reload();
                } else {
                    //表示删除失败
                    layer.msg(result.msg, {icon: 5});
                }
            })
        });
    }

    /**
     * 打开添加或修改计划项
     */
    function toAddOrUpdateCusDevPlan(id) {
        var url = ctx + '/cus_dev_plan/toAddOrUpdateCusDevPlan?sid=' + $("[name='id']").val();
        var title = "添加计划项管理";
        //判断计划项id是否为空(为空则为添加,不为空就是修改)
        if (id != null && id != '') {
            title = "修改计划项管理";
            url = ctx + "/cus_dev_plan/toAddOrUpdateCusDevPlan?sid=" + $("[name='id']").val() + "&id=" + id;
        }
        layui.layer.open({
            type: 2,
            //标题
            title: title,
            //宽高
            area: ['500px', '300px'],
            //开启最大最小化
            maxmin: true,
            //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            content: url
        });
    }
});
