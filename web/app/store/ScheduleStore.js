Ext.define('qa.store.ScheduleStore', {
    extend: 'Ext.data.Store',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        reader: 'json',
        url: 'BotScheduleServlet?nodeId=0&method=getSchedules'
    },
    fields: [{
        name: 'ID',
        type:'int'
    }, {
        name: 'BotName'
    },{
        name: 'Database_ID'
    },{
        name: 'JobIDs'
    },{
        name: 'Sector'
    },{
        name: 'LastQaDate'
    },{
        name:'LastCheckDate'
    },{
        name:'QaStatus',
        type:'int'
    },{
        name:'RunDate'
    },{
        name:'RunID'
    },{
        name:'DateFinished'
    },{
        name:'Success'
    },{
        name:'priority'
    },{
        name:'Checked'
    },{
        name:'Disabled'
    },{
        name:'DatabaseName'
    },{
        name:'QaDate'
    }]
});