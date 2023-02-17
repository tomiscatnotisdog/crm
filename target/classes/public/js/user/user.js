layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    //加载数据表格
    var tableIns=table.render({
        //id值
        id:'UserId',
        //table容器的id属性值
        elem: '#userList',
        //容器的高度,
        height:'full-125',
        //单元格的最小宽度
        cellMinWidth:40,
        //每页显示的数量
        limit:10,
        //可以选择的显示数量
        limits:[10,20,30,40,50,60,70,80,90],
        //后台的数据接口,
        url: ctx+'/user/list',
        //开启分页
        page: true,
        //绑定头部工具栏
        toolbar:"#toolbarDemo",
        //用户列表展示
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'userName', title: '用户名',align:"center"},
            {field: 'email', title: '用户邮箱',  align:'center'},
            {field: 'phone', title: '手机号', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'updateDate', title: '更新时间', align:'center'},
            /*id绑定行工具栏*/
            {title: '操作', templet:'#userListBar',fixed:"right",align:"center", minWidth:150}
        ]]
    });

    /**
     * 监听头部根据栏
     */
    table.on('toolbar(users)',function (data) {
        if (data.event==="add"){
            //调用更新或添加营销机会的展示页面,传入营销机会id
            toAddOrUpdate();
        }else if (data.event==="del"){
            //获取被选中的数据的信息
            var checkStatus=table.checkStatus(data.config.id);
            //调用删除方法
            deleteUser(checkStatus.data);
        }
    });

    /**
     * 删除营销机会(多条)
     */
    function deleteUser(userData) {
        //判断数据数组的长度是否大于<1(即:用户是否选择了数据)
        if (userData.length<1){
            layer.msg("请选择你要删除的数据!",{icon:5});
            return;
        }
        //长度大于0,就询问用户是否真的删除
        layer.confirm("你确定要删除选择的记录吗?",{icon:3,title:'用户管理'},function (index) {
            //关闭询问框
            layer.close(index);
            //传递的参数是数组  ids=1&ids=2&ids=3
            var ids ="ids=";
            //循环选择的行记录,获取其id进行拼接
            for (var i=0;i<userData.length;i++){
                if (i!==userData.length-1){
                    ids=ids+userData[i].id+"&ids=";
                }else {
                    ids=ids+userData[i].id
                }
            }
            //发送ajax请求(post)删除数据
            $.post(ctx+"/user/delete?"+ids,{},function (result) {
                if (result.code===200){
                    //表示删除成功
                    layer.msg("记录删除成功!",{icon:6});
                    //刷新表格数据
                    tableIns.reload();
                }else {
                    //表示删除失败
                    layer.msg(result.msg,{icon:5});
                }
            })
        });
    }


    /**
     * 监听行工具栏
     */
    table.on('tool(users)',function (data) {
        if (data.event==="edit"){
            //调用更新或添加营销机会的展示页面,传入营销机会id
            toAddOrUpdate(data.data.id);
        }else if (data.event==="del"){
            //询问用户是否是真的要删除
            layer.confirm("你确定要本条记录删除吗?",{icon:3,title:"用户管理"},function (index) {
                //点击确认后进入此方法,进入后通过索引将询问框关闭
                layer.close(index);
                //$.get(提交的url地址,提交的数据,成功的回调函数(后端传回的结果));
                //发送ajax(post)请求,删除此记录
                $.post(ctx+"/user/delete",{ids:data.data.id},function (result) {
                    if (result.code===200){
                        //表示删除成功
                        layer.msg("记录删除成功!",{icon:6});
                        //刷新表格数据
                        tableIns.reload();
                    }else {
                        //表示删除失败
                        layer.msg(result.msg,{icon:5});
                    }
                })
            });
        }
    });



    /**
     *进入到用户的添加与修改
     * @param userId
     */
    function toAddOrUpdate(userId) {

        //弹出层的标题
        var title=null;  //标题
        var url= null;  //跳转地址

        //判断记录ID是否存在
        if (userId!=null && userId!=''){ //添加
            title="<h3>用户管理-修改用户记录</h3>";
            url=[ctx+"/user/toAddOrUpdate?userId="+userId,'no'];
        }else {
            title="<h3>用户管理-添加用户记录</h3>";
            url=[ctx+"/user/toAddOrUpdate",'no'];
        }

        /**
         * 用户的添加与修改的ifream弹出框
         */
        layui.layer.open({
            type: 2,
            //标题
            title:title,
            //宽高
            area:['500px','450px'],
            //开启最大最小化
            maxmin:true,
            //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            content:url
        });
    }

    //通过class选择器为搜索按钮绑定点击事件
    $(".search_btn").click(function () {
        //这里以搜索为例 通过表格对象绑定重新加载
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设  如:  后台需要的参数名:前台传递的数据
                userName: $("[name='userName']").val(),  //用户名(通过name选择器获取)
                email: $("[name='email']").val(), //邮箱(通过name选择器获取)
                phone:$("[name='phone']").val() //手机号(通过name选择器获取)
            },
            page: {
                curr: 1 //重新从第 1 页开始
            }
        })
    });

});