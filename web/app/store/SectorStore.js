Ext.define('qa.store.SectorStore', {
    extend: 'Ext.data.Store',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        reader: 'json',
        url: 'TreeNodeServlet?method=getNode&node=2'
    }, 
    fields: [{
        name: 'name'
    }, {
        name: 'parentId'
    },{
        name: 'id'
    }]
});