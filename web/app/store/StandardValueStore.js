Ext.define('qa.store.StandardValueStore', {
    extend : 'Ext.data.Store',
    model : 'qa.model.StandardValueModel',
    autoDestroy : true,
    autoLoad: false,
    autoSync: true,

    proxy: {
        type: 'ajax',
        reader: 'json',
        writer: 'json',
        api: {
            create  : 'StandardValueServlet?method=addValue' ,
            read    : 'StandardValueServlet?method=getValues' ,
            update  :'StandardValueServlet?method=updateValue' 
        },
        listeners: { 
            exception: function(proxy, response, options) {
                Ext.example.msg('',response.responseText);
            }
        }
    }
}); 