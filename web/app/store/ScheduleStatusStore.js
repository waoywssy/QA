Ext.define('qa.store.ScheduleStatusStore', {
    extend: 'Ext.data.Store',
    autoLoad:false,
    ownerCmp:null,
    proxy: {
        type: 'ajax',
        reader: 'json',
        url: 'BotScheduleServlet?nodeId=-1&method=getSchedules'
    },
    fields: [{
        name: 'ID',
        type:'int'
    }, {
        name: 'QaStatus',
        type:'int'
    }],
    listeners: {
        'load':{
            fn:function(v, records, successful, eOpts){
                var grid =this.ownerCmp;
                for( i=0;i<records.length;i++){
                    var btn = grid.qaStatusBtns.get(records[i].data.ID);
                    if(btn){
                        switch(records[i].data.QaStatus){
                            case 0:
                                btn.setText('Run');
                                break;
                            case 1:
                                btn.setText('Stop');
                                break;
                            case 2:
                                btn.setText('Waiting');
                                break;
                            default:
                                btn.setText('Run');
                        }            
                    }else{
                        //schedule not exists,refresh all table content
                        grid.store.reload();
                        break;
                    }
                }
            }
        }
    }
});