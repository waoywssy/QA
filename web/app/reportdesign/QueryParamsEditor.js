Ext.define('qa.reportdesign.QueryParamsEditor', {
    extend : 'Ext.grid.Panel',
    alias : 'widget.queryparamseditor',
    requires:['qa.model.QueryParamModel','qa.store.QueryParamStore'],
    height : 200,
    width:350,
    initComponent : function() {
        var store = Ext.create('qa.store.QueryParamStore');
        var rowEditing = Ext.create('Ext.grid.plugin.RowEditing',{
            autoCancel : true,
            listeners:{
                canceledit:function( editor, context, eOpts ){
                    if (context.record.phantom) {
                        store.remove(context.record);
                    }
                }
            }
        });
        Ext.apply(this, {
            sortableColumns : false,
            enableColumnHide : false,
            store:store,
            columns : [{
                header : 'Name',
                dataIndex : 'Name',
                type:'string',
                width : 100,
                editor : {
                    type:'textfield'
                }
            }, {
                header : 'DataType',
                dataIndex : 'DataType',
                type:'string',
                width : 80,
                editor : new Ext.form.field.ComboBox({
                    store : [ 'string' ,'int', 'float', 'date', 'sql'],
                    editable:false
                })
            }, {
                header : 'DefaultValue',
                width : 120,
                type:'string',
                dataIndex : 'DefaultValue',
                editor : {
                    xtype : 'textfield'
                }
            }, {
                xtype : 'actioncolumn',
                width : 20,
                sortable : false,
                menuDisabled : true,
                items : [{
                    iconCls : 'delete',
                    scope : this,
                    handler : function(grid, rowIndex, colIndex){
                        this.getStore().removeAt(rowIndex);
                    }
                }
                ]
            }
            ],
            buttons : [{
                text : 'Parse',
                scope : this,
                handler :  function() {
                    if(!this.reportID){
                        Ext.MessageBox.show({
                            title:'Warn',
                            msg:'Please save query.'
                        });
                        return;
                    }
                    var store = this.getStore();
                    var queryText = this.up('form').getForm().findField('queryText').getValue();
                    var regex=/:(\w+)/g;
                    var array;
                    while((array=regex.exec(queryText))!=null){
                        var name = array[1];
                        var index=store.find('Name',name);
                        if(index==-1){
                            store.insert(0, Ext.create('qa.model.QueryParamModel',{
                                ReportID:this.reportID,
                                Name:name
                            }));
                        }
                    }
                }
            },{
                text : 'Add',
                scope : this,
                handler :  function() {
                    if(this.reportID){
                        store.insert(0, Ext.create('qa.model.QueryParamModel',{
                            ReportID:this.reportID
                        }));
                        rowEditing.startEdit(0, 0);
                    }else{
                        Ext.MessageBox.show({
                            title:'Warn',
                            msg:'Please save query.'
                        });
                    }
                }
            }],      
            plugins : [rowEditing],
            listeners:{
                afterrender:function(panel,option){
                    if(this.reportID){
                        panel.getStore().load({
                            params:{
                                ReportID:this.reportID
                            }
                        });
                    }
                }
            }
        });

        this.callParent();
    }
})