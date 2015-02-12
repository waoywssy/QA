Ext.define('qa.model.QueryParamModel', {
    extend: 'Ext.data.Model',
    fields: [{
        name:'ReportID',
        type:'int'
    },{
        name:'Name',
        type:'string'
    },{
        name:'OldName',
        type:'string',
        mapping:'Name'
    },{
        name:'DataType',
        type:'string'
    } ,{
        name:'DefaultValue',
        type:'string'
    } ],
    validations: [{
        type: 'length',
        field: 'Name',
        min: 1
    }]
});