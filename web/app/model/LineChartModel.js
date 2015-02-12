Ext.define('qa.model.LineChartModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'LinkColumn', type: 'string'}
        , {name: 'Category', type: 'string'}
        , {name: 'TopRows', type: 'int'}
        , {name: 'LineColumns', type: 'string'}
        , {name: 'ReportID', type: 'int'}
        , {name: 'SubReportID', type: 'int'}
        , {name: 'SubReportName', type: 'string'}
    ]
});