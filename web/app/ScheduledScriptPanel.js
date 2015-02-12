Ext.define('qa.ScheduledScriptPanel', {
    extend : 'Ext.grid.Panel',
    xtype : 'scheduledscriptpanel',
    require : ['qa.model.ScheduledScriptModel'],
    title : 'Scheduled Scripts',
    padding : 10,
    sortableColumns : false,
    enableColumnHide : false,
    enableColumnMove : false,
    closable : true,
    initComponent : function() {
        Ext.apply(this, {
            store : Ext.create('qa.store.ScheduledScriptStore'),    
            viewConfig: {
                stripeRows: true,
                enableTextSelection: true
            },
            columns : [{
                header : 'Name',
                dataIndex : 'Name',
                width : 250
            },{
                header : 'JoinColumn',
                dataIndex : 'JoinColumn',
                editor : {
                    allowBlank : true,
                    xtype : 'textfield'
                },
                width : 120
            },{
                header : 'JoinRunsColumn',
                dataIndex : 'JoinRunsColumn',
                editor : {
                    allowBlank : true,
                    xtype : 'combo',
                    queryMode : 'local',
                    store:[['RunDate','RunDate'],['RunId','RunId']]
                },
                width : 120
            },{
                header : 'Convert',
                dataIndex : 'ConvertType',
                width : 170,
                editor : {
                    allowBlank : true,
                    xtype : 'combo',
                    queryMode : 'local',
                    displayField: 'name',
                    valueField: 'v',
                    store:Ext.create('Ext.data.Store',{
                        fields: ['name', 'v'],
                        data:[{
                            name:'none',
                            v:0
                        },{
                            name:'yyyy-mm-dd',
                            v:1
                        },{
                            name:'yyyy-mm-dd hh:MM',
                            v:2
                        },{
                            name:'yyyy-mm-dd hh:MM:ss',
                            v:3
                        }]
                    })
                }
            },{
                xtype:'actioncolumn',
                width: 50,
                items: [{
                    iconCls: 'delete',
                    handler: function(grid, rowIndex, colIndex) {
                        grid.getStore().removeAt(rowIndex);
                    }
                }]
            }],
            plugins : [{
                ptype : 'rowediting',
                autoCancel : true
            }],
            listeners:{
                afterrender:{
                    fn:function(){
                        var dropTarget = new Ext.dd.DropTarget(this.body, {
                            ddGroup : 'treenodedrop_group',
                            notifyDrop : function(ddSource, e, data) {
                                var data1= data.records[0].data;
                                if(data1.nodeType!=13){
                                    return false;
                                }
                                var id= data1.id;
                                var name = data1.name;
                                var gridstore = this.gridpanel.getStore();
                                var record = Ext.create('qa.model.ScheduledScriptModel',{
                                    Name:name,
                                    BotID:this.gridpanel.BotID,
                                    QueryID:id
                                })
                                gridstore.insert(0,record);
                                return true;
                            }
                        });
                        dropTarget.gridpanel = this;
                    }
                }
            }
        
        });
        this.callParent();
    },
    loadData:function(botID){
        this.store.getProxy().setExtraParam("BotID",botID);
        this.BotID = botID;
        this.store.load();
    }
})