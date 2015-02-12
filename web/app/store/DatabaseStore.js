Ext.define('qa.store.DatabaseStore', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    proxy: {
        type: 'ajax',
        reader: 'json',
        url: 'TreeNodeServlet?method=getNode&node=1'
    }, 
    fields: [{
        name: 'name'
    }, {
        name: 'parentId'
    },{
        name: 'id'
    }]
});