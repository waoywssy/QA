Ext.define('qa.model.TreeReportModel', {
    extend: 'Ext.data.Model',
    requires: [
    'Ext.data.reader.Json'
    ],
    fields: ['ReportID', 'CatID', 'CatParentID','CatName','CatLevel','LastFound','LostPoint']
});