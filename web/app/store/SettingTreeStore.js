Ext.define('qa.store.SettingTreeStore', {
    extend: 'Ext.data.TreeStore',
    autoLoad: true,
    lazyFill: true,
    proxy: {
        type: 'ajax',
        reader: 'json',
        url: 'TreeNodeServlet?method=getNode'
    },  
    root:{
        id:'0',
        name:'Root'     
    },
    fields: [{
        name: 'name'
    }, {
        name: 'parentId'
    },{
        name: 'id'
    },{
        name: 'nodeType',
        type:'int'
    },{
        name: 'leaf',
        type:'boolean'
    },{
        name: 'refer'
    },{
        name:'iconCls'
    }]
    
});