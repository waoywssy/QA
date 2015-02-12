Ext.define('qa.store.QueryParamStore', {
    extend: 'Ext.data.Store',
    requires:['qa.model.QueryParamModel'],
    autoLoad: false,
    autoSync: true,
    model : 'qa.model.QueryParamModel',
    proxy: {
        type:'ajax',
        reader: {
            type: 'json',
            root: 'data'
        },
        writer: {
            type: 'json'
        },
        api: {
            create  : 'ReportDesignServlet?method=updateQueryParam' ,
            read    : 'ReportDesignServlet?method=getQueryParams' ,
            update  :'ReportDesignServlet?method=updateQueryParam' ,
            destroy :'ReportDesignServlet?method=updateQueryParam&delete=true'
        }
    }
});