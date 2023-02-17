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
        url: ctx+'/sale_chance/list?flag=1',
        //开启分页
        page: true,
        //绑定头部工具栏(id选择器)
        toolbar:"#头部工具栏id",
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
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'updateDate', title: '修改时间', align:'center'},
            {field: 'devResult', title: '开发状态', align:'center',templet:function (d) {
                    return formatterDevResult(d.devResult);
                }},
            //行工具栏       绑定行工具栏
            {title: '操作', templet:'#op',fixed:"right",align:"center", minWidth:125 }
        ]]
    });


    /**
     * 监听行工具栏
     */
    table.on('tool(saleChances)',function (data) {
        if (data.event==="dev"){ //开发
            //调用客户开发计划的开发或详情页面,传入营销机会id与标题
            toCusDevPlanPage("客户开发计划开发页面",data.data.id);

        }else if (data.event==="info"){ //详情
            //调用客户开发计划的开发或详情页面,传入营销机会id与标题
            toCusDevPlanPage("客户开发计划详情页面",data.data.id);

        }
    });

    /**
     * 进入客户开发计划的开发或详情页面
     * @param title
     * @param SaleChanceId
     */
    function toCusDevPlanPage(title,saleChanceId){
        layui.layer.open({
            type: 2,
            //标题
            title:title,
            //宽高
            area:['750px','600px'],
            //开启最大最小化
            maxmin:true,
            //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            content:[ctx+'/cus_dev_plan/toCusDevPlanPage?saleChanceId='+saleChanceId,'no']
        });
    }


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



    //通过class选择器为搜索按钮绑定点击事件
    $(".layui-btn").click(function () {
        //这里以搜索为例 通过表格对象绑定重新加载
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设  如:  后台需要的参数名:前台传递的数据
                customerName: $("[name='customerName']").val(),  //客户名称(通过name选择器获取)
                createMan: $("[name='createMan']").val(), //创建人(通过name选择器获取)
                devResult:$("#devResult").val() //开发状态(通过id选择器获取)
            },
            page: {
                curr: 1 //重新从第 1 页开始
            }
        })
    });


    /**
     * 判断开发状态
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
});
