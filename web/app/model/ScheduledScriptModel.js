Ext.define('qa.model.ScheduledScriptModel', {
    extend: 'Ext.data.Model',
    fields:[
    {
        name:'ID',
        type:'int'
    },
    {
        name:'BotID',
        type:'int'
    },{
        name:'QueryID',
        type:'int'
    },{
        name:'Name',
        type:'string'
    },{
        name:'JoinColumn',
        type:'string'
    },{
        name:'JoinRunsColumn',
        type:'string'
    },{
        name:'ConvertType',
        type:'int'
    }
    ]
});