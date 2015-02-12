Ext.define('qa.model.Query', {
    extend: 'Ext.data.Model',
    fields: ['node', 'parentNode', 'database','name','queryText','server',{
        name:'changeServer',
        type:'boolean',
        defaultValue:false
    },{
        name:'queryType'
    },{
        name:'startRow',
        type:'int'
    }]
});