Ext.define('qa.ImportDataPanel', {
    extend : 'Ext.panel.Panel',
    xtype : 'importdatapanel',
    requires : ['qa.store.ServerStore'],
    width:800,
    height:600,
    title:'ImportData',
    layout:'vbox',
    initComponent : function() {
        Ext.apply(this, {
            items:[
            {
                xtype:'form',
                title:'Sync JobCentral Info',
                waitMsgTarget: true,
                bodyPadding:10,
                margin:10,
                height:150,
                flex:0.3,
                items:[{

                    xtype : 'combo',
                    store : Ext.create('qa.store.ServerStore'),
                    displayField : 'name',
                    name:'server',
                    allowBlank:false,
                    editable:false,
                    fieldLabel:'To Server',
                    emptyText : 'Select server'
                },{
                    xtype:'numberfield',
                    name:'JobID',
                    fieldLabel:'JobID',
                    allowBlank:false
                }
                ],
                buttons:[
                {
                    text:'Sync',
                    handler:function(){
                        this.up('form').getForm().submit({
                            url:'ImportDataServlet?method=importJobInfo',
                            waitMsg: 'Sync...'
                        });
                    }
                }
                ]
            },{
                xtype:'form',
                waitMsgTarget: true,
                bodyPadding:10,
                flex:0.5,
                margin:'0 10 10 10',
                title: 'Import Data',
                items:[{
                    xtype : 'combo',
                    store : Ext.create('qa.store.ServerStore'),
                    displayField : 'name',
                    name:'fromServer',
                    allowBlank:false,
                    editable:false,
                    fieldLabel:'From Server',
                    emptyText : 'Select server'
                },{
                    xtype : 'combo',
                    store : Ext.create('qa.store.ServerStore'),
                    displayField : 'name',
                    name:'toServer',
                    allowBlank:false,
                    editable:false,
                    fieldLabel:'To Server',
                    emptyText : 'Select server'
                },{
                    xtype:'textfield',
                    name:'toTable',
                    fieldLabel:'To Table',
                    emptyText:'database.dbo.table',
                    allowBlank:false
                },{
                    xtype:'textarea',
                    name:'queryText',
                    emptyText :'query text',
                    allowBlank:false,
                    width:500,
                    height:150,
                    flex:1
                }
                ],
                buttons:[
                {
                    text:'Import',
                    handler:function(){
                        this.up('form').getForm().submit({
                            url:'ImportDataServlet?method=importdata',
                            waitMsg: 'import...'
                        });
                    }
                }
                ]
            }
            ]//end items
        });
        this.callParent();
    }
})