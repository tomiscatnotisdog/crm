layui.use(['table', 'layer'], function () {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    //加载数据表格
    var tableIns = table.render({
        //id值
        id: 'RoleId',
        //table容器的id属性值
        elem: '#roleList',
        //容器的高度,
        height: 'full-125',
        //单元格的最小宽度
        cellMinWidth: 40,
        //每页显示的数量
        limit: 10,
        //可以选择的显示数量
        limits: [10, 20, 30, 40, 50, 60, 70, 80, 90],
        //后台的数据接口,
        url: ctx + '/role/list',
        //开启分页
        page: true,
        //绑定头部工具栏
        toolbar: "#toolbarDemo",
        //用户列表展示
        cols: [[
            {type: "checkbox", fixed: "center"},
            {field: "id", title: '编号', fixed: "true"},
            {field: 'roleName', title: '角色名', align: "center"},
            {field: 'roleRemark', title: '角色备注', align: 'center'},
            {field: 'createDate', title: '创建时间', align: 'center'},
            {field: 'updateDate', title: '更新时间', align: 'center'},
            /*id绑定行工具栏*/
            {title: '操作', templet: '#roleListBar', fixed: "right", align: "center", minWidth: 150}
        ]]
    });

    //通过class选择器为搜索按钮绑定点击事件
    $(".search_btn").click(function () {
        //这里以搜索为例 通过表格对象绑定重新加载
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设  如:  后台需要的参数名:前台传递的数据
                roleName: $("[name='roleName']").val()  //用户名(通过name选择器获取)
            },
            page: {
                curr: 1 //重新从第 1 页开始
            }
        })
    });

    /**
     * 监听头部根据栏
     */           //toolbar(lay-filter的属性值);  lay-filter="roles"
    table.on('toolbar(roles)', function (data) {
        if (data.event === "add") {
            //调用添加方法
            openAddOrUpdate();
        } else if (data.event === "grant") {
            //获取被选中的数据的信息
            var checkStatus = table.checkStatus(data.config.id);
            //打开授权窗口
            openGrantPage(checkStatus.data);
        }
    });

    function openGrantPage(data) {
        //判断是否选择了记录
        if (data.length == 0) {
            layer.msg("请选择你要授权的记录!", {icon: 5});
            return;
        }
        if (data.length > 1) {
            layer.msg("暂不支持批量角色授权!", {icon: 5});
            return
        }
        //打开授权页面
        var title = "<h3>角色管理-角色授权</h3>>";
        var url = ctx + "/module/toGrantPage?roleId=" + data[0].id;
       layui.layer.open({
              type:2,
              title:title,
              content:url,
              maxmin:true,
              area:["400px","600px"]

       });
    }

    /**
     *进入到角色的添加与修改
     * @param userId
     */
    function openAddOrUpdate(roleId) {
        //弹出层的标题
        var title = null;  //标题
        var url = null;  //跳转地址
        //判断记录ID是否存在
        if (roleId != null && roleId != '') { //添加
            title = "<h3>角色管理-角色更新</h3>";
            url = [ctx + "/role/toAddOrUpdate?roleId=" + roleId, 'no'];
        } else {
            title = "<h3>角色管理-角色添加</h3>";
            url = [ctx + "/role/toAddOrUpdate", 'no'];
        }
        /**
         * 用户的添加与修改的ifream弹出框
         */
        layui.layer.open({
            type: 2,
            //标题
            title: title,
            //宽高
            area: ['450px', '250px'],
            //开启最大最小化
            maxmin: true,
            //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            content: url
        });
    }


    /**
     * 监听行工具栏
     */
    table.on('tool(roles)', function (data) {
        if (data.event === "edit") {
            //调用更新或添加营销机会的展示页面,传入营销机会id
            openAddOrUpdate(data.data.id);
        } else if (data.event === "del") {
            //询问用户是否是真的要删除
            layer.confirm("你确定要本条记录删除吗?", {icon: 3, title: "角色管理"}, function (index) {
                //点击确认后进入此方法,进入后通过索引将询问框关闭
                layer.close(index);
                //$.get(提交的url地址,提交的数据,成功的回调函数(后端传回的结果));
                //发送ajax(post)请求,删除此记录
                $.post(ctx + "/role/delete", {roleId: data.data.id}, function (result) {
                    if (result.code === 200) {
                        //表示删除成功
                        layer.msg("角色删除成功!", {icon: 6});
                        //刷新表格数据
                        tableIns.reload();
                    } else {
                        //表示删除失败
                        layer.msg(result.msg, {icon: 5});
                    }
                })
            });
        }
    });


});
