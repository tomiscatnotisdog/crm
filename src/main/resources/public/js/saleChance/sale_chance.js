layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    //加载数据表格
    var tableIns=table.render({
        //id值
        id:'saleChanceId',
        //table容器的id属性值
        elem: '#saleChanceList',
        //容器的高度,
        height:'full-125',
        //单元格的最小宽度
        cellMinWidth:40,
        //每页显示的数量
        limit:10,
        //可以选择的显示数量
        limits:[10,20,30,40,50,60,70,80,90],
        //后台的数据接口,
        url: ctx+'/sale_chance/list',
        //开启分页
        page: true,
        //绑定头部工具栏
        toolbar:"#toolbarDemo",
        //用户列表展示
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'chanceSource', title: '机会来源',align:"center"},
            {field: 'customerName', title: '客户名称',  align:'center'},
            {field: 'cgjl', title: '成功几率', align:'center'},
            {field: 'overview', title: '概要', align:'center'},
            {field: 'linkMan', title: '联系人',  align:'center'},
            {field: 'linkPhone', title: '联系电话', align:'center'},
            {field: 'description', title: '描述', align:'center'},
            {field: 'createMan', title: '创建人', align:'center'},
            {field: 'uname', title: '分配人', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'assignTime', title: '分配时间', align:'center'},
            {field: 'updateDate', title: '修改时间', align:'center'},
            {field: 'state', title: '分配状态', align:'center',templet:function(d){
                    return formatterState(d.state);
                }},
            {field: 'devResult', title: '开发状态', align:'center',templet:function (d) {
                    return formatterDevResult(d.devResult);
                }},
            {title: '操作', templet:'#saleChanceListBar',fixed:"right",align:"center", minWidth:150}
        ]]
    });

    /**
     * 分配状态
     * @param state
     * @returns {string}
     */
    function formatterState(state){
        if(state==0){
            return "<div style='color:#00b7ee '>未分配</div>";
        }else if(state==1){
            return "<div style='color: green'>已分配</div>";
        }else{
            return "<div style='color: red'>未知</div>";
        }
    }

    /**
     * 开发状态
     * @param value
     * @returns {string}
     */
    function formatterDevResult(value){
        /**
         * 0-未开发
         * 1-开发中
         * 2-开发成功
         * 3-开发失败
         */
        if(value==0){
            return "<div style='color: #00b7ee'>未开发</div>";
        }else if(value==1){
            return "<div style='color: #00FF00;'>开发中</div>";
        }else if(value==2){
            return "<div style='color: #00B83F'>开发成功</div>";
        }else if(value==3){
            return "<div style='color: red'>开发失败</div>";
        }else {
            return "<div style='color: #af0000'>未知</div>"
        }
    }


    //通过class选择器为搜索按钮绑定点击事件
    $(".layui-btn").click(function () {
        //这里以搜索为例 通过表格对象绑定重新加载
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设  如:  后台需要的参数名:前台传递的数据
                customerName: $("[name='customerName']").val(),  //客户名称(通过name选择器获取)
                createMan: $("[name='createMan']").val(), //创建人(通过name选择器获取)
                state:$("#state").val() //分配状态(通过id选择器获取)
            },
            page: {
                curr: 1 //重新从第 1 页开始
            }
        })
    });

    /**
     * 监听头部根据栏
     */
    table.on('toolbar(saleChances)',function (data) {
        if (data.event==="add"){
            //调用更新或添加营销机会的展示页面,传入营销机会id
            toAddOrUpdate();
        }else if (data.event==="delete"){
            //调用删除方法
            deleteSaleChance(data);
        }
    });



    /**
     *进入到营销机会的添加与修改
     * @param saleChanceId
     */
    function toAddOrUpdate(saleChanceId) {

        //弹出层的标题
        var title=null;  //标题
        var url= null;  //跳转地址

        //判断记录ID是否存在
        if (saleChanceId!=null && saleChanceId!=''){ //添加
            title="<h3>营销机会管理-修改营销机会</h3>";
            url=[ctx+"/sale_chance/toAddOrUpdatePage?id="+saleChanceId,'no'];
        }else {
            title="<h3>营销机会管理-添加营销机会</h3>";
            url=[ctx+"/sale_chance/toAddOrUpdatePage",'no'];
        }


        layui.layer.open({
            type: 2,
            //标题
            title:title,
            //宽高
            area:['500px','590px'],
            //开启最大最小化
            maxmin:true,
            //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            content:url
        });
    }

    /**
     * 监听行工具栏
     */
    table.on('tool(saleChances)',function (data) {
        if (data.event=="updataBar"){
            //调用更新或添加营销机会的展示页面,传入营销机会id
            toAddOrUpdate(data.data.id);
        }else if (data.event=="deleteBar"){
            //询问用户是否是真的要删除
            layer.confirm("你确定要本条记录删除吗?",{icon:3,title:"营销机会管理"},function (index) {
                //点击确认后进入此方法,进入后通过索引将询问框关闭
                layer.close(index);
                //$.get(提交的url地址,提交的数据,成功的回调函数(后端传回的结果));
                //发送ajax(post)请求,删除此记录
                $.post(ctx+"/sale_chance/delete",{ids:data.data.id},function (result) {
                    if (result.code==200){
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
     * 删除营销机会(多条)
     */
    function deleteSaleChance() {
        //获取选择表格选择的行数据 table.checkStatus(数据表格的id值);
        var checkStatus=table.checkStatus("saleChanceId");
        //checkStatus-->里面存储的是表格选择的每条记录组成的,里面的data是一个记录对象数组
        //获取选择的数据
        var saleChanceData=checkStatus.data;
        //判断数据数组的长度是否大于<1
        if (saleChanceData.length<1){
            layer.msg("请选择你要删除的数据!",{icon:5});
            return;
        }
        //长度大于0,就询问用户是否真的删除
        layer.confirm("你确定要删除选择的记录吗?",{icon:3,title:'营销机会管理'},function (index) {
            //关闭询问框
            layer.close(index);
            //传递的参数是数组  ids=1&ids=2&ids=3
            var ids ="ids=";
            //循环选择的行记录,获取其id进行拼接
            for (var i=0;i<saleChanceData.length;i++){
                if (i!==saleChanceData.length-1){
                    ids=ids+saleChanceData[i].id+"&ids=";
                }else {
                    ids=ids+saleChanceData[i].id
                }
            }
            //发送ajax请求(post)删除数据
            $.post(ctx+"/sale_chance/delete?"+ids,{},function (result) {
                if (result.code==200){
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
