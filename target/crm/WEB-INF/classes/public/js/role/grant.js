$(function () {
    //加载树形结构
    loadModuleData();
});

//定义一个树形结构对象
var zTreeObj;

// zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
var setting = {
    //使用复选框
    check: {
        enable: true
    },
    //开启简单数据模式
    data: {
        simpleData: { //simple-->易于理解的
            enable: true
        }
    },
    //绑定函数(用于捕获 checkbox / radio 被勾选 或 取消勾选的事件回调函数)
    callback: {
        onCheck: zTreeOnCheck
    }

};

/**
 * 每次点击 checkbox 或 radio 后， 弹出该节点的 tId、name 以及当前勾选状态的信息
 * @param event 标准的 js event 对象
 * @param treeId  对应 zTree 的 treeId，便于用户操控
 * @param treeNode  被勾选 或 取消勾选的节点 JSON 数据对象
 */ //treeNode-->勾选或取消的节点
function zTreeOnCheck(event, treeId, treeNode) {
    //alert(treeNode.tId + ", " + treeNode.name + "," + treeNode.checked);

    //1.通过getCheckedNodes(checked):获取所有被勾选的节点checked=true表示拿选中的,反之,则...
    var nodes=zTreeObj.getCheckedNodes(true);

    //获取所有勾选的资源id
    if (nodes.length>0){
        //等于存储资源ID的变量
        var mIds="mIds=";
        //遍历节点集合,获取资源的ID
        for (var i=0;i<nodes.length;i++){
            if (i<nodes.length-1){
                mIds+=nodes[i].id+"&mIds="
            }else {
                mIds+=nodes[i].id
            }
        }
    }
    //获取需要授权的角色id
    var roleId=$(".roleId").val();

    //发送ajax请求,执行角色的授权操作
    $.ajax({
        url:ctx+"/role/roleGrant?"+mIds+"&roleId="+roleId,
        type:"post",
        dataType:"json",
        success:function (data) {
            //无须操作,打印一下结果即可
            console.log(data)
        }
    })

}


/**
 * 加载树形结构的方法
 */
function loadModuleData() {
    //通过ajax从后台拿取数据
    $.ajax({
        type:"get",
        //我们在查询所有的资源列表是,传递角色id(查询当前角色已经授权的资源)
        url:ctx+"/module/queryAllModules?roleId="+$(".roleId").val(),
        success:function (data) {
            //zTreeObj = $.fn.zTree.init(容器, 配置, 数据);
            zTreeObj = $.fn.zTree.init($(".ztree"), setting, data);
        }
    });
}






