Ext.define('qa.store.ScheduledScriptStore', {
    extend : 'Ext.data.Store',
    model:'qa.model.ScheduledScriptModel',
    autoDestroy : true,
    autoLoad: false,
    autoSync: true,
    proxy: {
        type: 'ajax',
        reader: 'json',
        writer: 'json',
        api: {
            create  : 'ScheduleScriptServlet?method=update' ,
            read    : 'ScheduleScriptServlet?method=get' ,
            update  :'ScheduleScriptServlet?method=update',
            destroy :'ScheduleScriptServlet?method=delete'
        },
        listeners: { 
            exception: function(proxy, response, options) {
                Ext.example.msg('',response.responseText);
            }
        }
    }
}); 