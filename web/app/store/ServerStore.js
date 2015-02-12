Ext.define('qa.store.ServerStore', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    proxy: {
        type: 'ajax',
        reader: 'json',
        url: 'TreeNodeServlet?method=getNode&node=3'
    }, 
    fields: [{
        name: 'name'
    }, {
        name: 'parentId'
    },{
        name: 'id'
    }]
});