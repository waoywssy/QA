Ext.define('qa.QueryDesign', {
    extend : 'Ext.panel.Panel',
    xtype : 'querydesign',
    title : 'Query Design',
    bodyPadding : 5,
    acchor : '100%',
    layout : {
        type : 'hbox',
        align : 'stretch' // Child items are stretched to full width
    },

    initComponent : function() {
        this.tableGrid = Ext.create('Ext.grid.Panel', {
            xtype : 'grid',
            title:'Table',
            width : 250,
            enableColumnHide : false,
            sortableColumns : false,
            forceFit : true,
            columns : [{
                xtype : 'checkcolumn',
                dataIndex : 'Active',
                width : 30
            }, {
                header : 'Column',
                dataIndex : 'Name'

            }],
            store : Ext.create('Ext.data.Store', {
                fields : [{
                    name:'Name',
                    mapping:'name'
                }, {
                    name : 'Active',
                    defaultValue : false
                }],
                proxy : {
                    type : 'ajax',
                    url : 'TreeNodeServlet?method=getNode',
                    reader : {
                        type : 'json'
                    }
                },
                autoLoad : false
            }),
            listeners:{
                afterrender:{
                    fn:function(){
                        var target  =this;
                        var dropTarget = new Ext.dd.DropTarget(this.body, {
                            ddGroup : 'treenodedrop_group',
                            notifyDrop : function(ddSource, e, data) {
                                var data1= data.records[0].data;
                                if(data1.nodeType!=8){
                                    return false;
                                }
                                target.setTitle(data1.name);
                                target.getStore().getProxy().setExtraParam('node',data1.id);
                                target.getStore().load();
                                var queryContainer = target.ownerCt.getComponent('queryCt');
                                queryContainer.getComponent(0).getStore().removeAll();
                                queryContainer.getComponent(1).getStore().removeAll();
                                return true;
                            }
                        });
                    }
                }
            }
        });

        this.funGrid = Ext.create('Ext.grid.Panel', {
            xtype : 'grid',
            title:'Functions',
            width : 200,
            forceFit : true,
            enableColumnHide : false,
            sortableColumns : false,
            margin:'0 0 0 10',
            columns : [{
                xtype : 'checkcolumn',
                dataIndex : 'Active',
                width : 30
            }, {
                header : 'Fun',
                dataIndex : 'Name'
            }],
            store : Ext.create('Ext.data.Store', {
                fields : ['Name', 'Fun','Params', {
                    name : 'Active',
                    defaultValue : false
                }],
                proxy : {
                    type : 'ajax',
                    url : 'data/queryFuns.json',
                    reader : {
                        type : 'json'
                    }
                },
                autoLoad : true
            })
        });

        this.queryCt = Ext.create('Ext.container.Container', {
            itemId:'queryCt',
            width : 500,
            layout:{
                type : 'vbox',
                align : 'stretch' 
            },
            items : [{
                title : 'Select fields',
                xtype : 'grid',
                flex:1,
                enableColumnHide : false,
                sortableColumns : false,
                columns : [{
                    header : 'ColumnName',
                    dataIndex : 'ColumnName'
                }, {
                    header : 'FunName',
                    dataIndex : 'FunName'
                }, {
                    header : 'Alias',
                    dataIndex : 'Alias',
                    width:120,
                    editor : {
                        xtype : 'textfield'
                    }
                }, {
                    header : 'Order',
                    dataIndex : 'Order',
                    width : 50,
                    editor : {
                        xtype : 'combo',
                        displayField : 'data',
                        queryModel : 'local',
                        store : Ext.create('Ext.data.ArrayStore', {
                            fields : ['data'],
                            data : [['NONE'], ['ASC'], ['DESC']]
                        })
                    }
                }, {
                    text : 'Group',
                    xtype : 'checkcolumn',
                    dataIndex : 'Group',
                    width : 50
                }, {
                    xtype : 'actioncolumn',
                    width : 50,
                    items : [{
                        icon : 'icons/filter.gif',
                        scope : this,
                        handler : function(grid, rowIndex, colIndex) {
                            var rec = grid.getStore().getAt(rowIndex);
                            this.queryCt.getComponent(1).getStore().add({
                                ColumnName : rec.get('ColumnName'),
                                Operator : '>'
                            });
                        }
                    },{
                        icon : 'icons/delete.gif',
                        scope : this,
                        handler : function(grid, rowIndex, colIndex) {
                            grid.getStore().removeAt(rowIndex);
                        }
                    }]
                }],
                store : Ext.create('Ext.data.Store', {
                    fields : ['ColumnName', 'Fun', 'Alias', 'FunName', {
                        name : 'Group',
                        type : 'boolean'
                    }, 'Order']
                }),
                selType : 'rowmodel',
                plugins : [Ext.create('Ext.grid.plugin.RowEditing', {
                    clicksToEdit : 2
                })],
                viewConfig : {
                    plugins : {
                        ptype : 'gridviewdragdrop',
                        dragText : 'Drag and drop to reorganize'
                    }
                }
            }, {
                title : 'Where clause',
                xtype : 'grid',
                enableColumnHide : false,
                sortableColumns : false,
                forceFit : true,
                height : 200,
                columns : [{
                    header : 'Column',
                    dataIndex : 'ColumnName',
                    width : 50
                }, {
                    header : 'Operator',
                    dataIndex : 'Operator',
                    width : 35,
                    editor : {
                        xtype : 'combo',
                        displayField : 'data',
                        queryModel : 'local',
                        store : Ext.create('Ext.data.ArrayStore', {
                            fields : ['data'],
                            data : [['>'], ['='], [':Operation']]
                        })
                    }
                }, {
                    header : 'Right Operator',
                    dataIndex : 'RightOperator',
                    editor : {
                        xtype : 'combo',
                        displayField : 'data',
                        queryModel : 'local',
                        store : Ext.create('Ext.data.ArrayStore', {
                            fields : ['data'],
                            data : [['DATEADD(DAY,-7,GETDATE())'], ['MAX'], [':RunDate'], ['DATEADD(MI,DATEDIFF(MI,0,:RunDate),0)']]
                        })
                    }
                }, {
                    xtype : 'actioncolumn',
                    width : 50,
                    items : [{
                        icon : 'icons/delete.gif',
                        scope : this,
                        handler : function(grid, rowIndex, colIndex) {
                            grid.getStore().removeAt(rowIndex);
                        }
                    }]
                }],
                store : Ext.create('Ext.data.Store', {
                    fields : ['ColumnName', 'Operator', 'RightOperator']
                }),
                selType : 'rowmodel',
                plugins : [Ext.create('Ext.grid.plugin.RowEditing', {
                    clicksToEdit : 1
                })]
            }]
        });

        Ext.apply(this, {
            items : [this.tableGrid, this.funGrid, {
                width : 50,
                xtype : 'container',
                items : [{
                    xtype : 'button',
                    text : 'add',
                    scope : this,
                    margin : '100 0 0 0',
                    handler : this.addField
                }, {
                    xtype : 'button',
                    margin : '20 0 0 0',
                    text : 'view',
                    scope : this,
                    handler : this.view
                }]
            }, this.queryCt]
        })
        this.callParent();
    },
    addField : function() {
        var selectedCols = this.tableGrid.getStore().getModifiedRecords();
        var selectedFuns = this.funGrid.getStore().getModifiedRecords();
        var selectFieldGridStore = this.queryCt.down('grid').getStore();
        
        if(selectedFuns.length==1&&selectedFuns[0].get('Params')>1){
            var delimit='';
            var columns='';
            for (var i = 0; i < selectedCols.length; i++) {
                var columnName = selectedCols[i].get('Name');
                if (columnName.indexOf('(') > 0) {
                    columnName = columnName.substring(0, columnName.indexOf('('));
                }
                columns+=delimit+columnName;
                delimit=',';
            }
            selectFieldGridStore.add({
                ColumnName : columns,
                Fun : selectedFuns[0].get('Fun'),
                FunName :selectedFuns[0].get('Name'),
                Active : false,
                Alias :  selectedFuns[0].get('Name') + columnName
            });
        }else{
            for (var i = 0; i < selectedCols.length; i++) {
                var columnName = selectedCols[i].get('Name');
                if (columnName.indexOf('(') > 0) {
                    columnName = columnName.substring(0, columnName.indexOf('('));
                }
                if (selectedFuns.length == 0) {
                    //select the column
                    selectFieldGridStore.add({
                        ColumnName : columnName,
                        Fun : '{0}',
                        FunName : '',
                        Active : false,
                        Alias : columnName
                    });
                    continue;
                }
                for (var j = 0; j < selectedFuns.length; j++) {
                    var funName = selectedFuns[j].get('Name');
                    var alias = funName == 'TotalItem' ? 'TotalItem' : selectedFuns[j].get('Name') + columnName;
                    selectFieldGridStore.add({
                        ColumnName : columnName,
                        Fun : selectedFuns[j].get('Fun'),
                        FunName : funName,
                        Active : false,
                        Alias : alias
                    });
                }
            }//end for selectedCols
        }
        this.tableGrid.getStore().rejectChanges();
        this.funGrid.getStore().rejectChanges();
    },
    view : function() {
        //generate the query
        var table = this.tableGrid.title;
        var selectedFieldClause = '';
        var groupClause = '';
        var orderClause = '';
        this.queryCt.getComponent(0).getStore().each(function(record) {
            //Select field
            var columName = record.get('ColumnName');
            var alias = record.get('Alias');

            var order = record.get('Order');
            if (selectedFieldClause.length > 0) {
                selectedFieldClause += '\n,';
            }
            if (columName == alias) {
                selectedFieldClause += alias;
            } else {
                var fun= record.get('Fun');
                var columns = columName.split(',');
                for(var i=0;i<columns.length;i++){
                    fun=fun.replace('{'+i+'}', columns[i]);
                }
                selectedFieldClause += fun + ' AS ' + alias;
            }
            //Group field
            var group = record.get('Group');
            if (group) {
                if (groupClause.length > 0) {
                    groupClause += ',';
                }
                groupClause += columName
            }
            //Order field
            var order = record.get('Order');
            if (order == 'DESC' || order == 'ASC') {
                if (orderClause.length > 0) {
                    orderClause += ',';
                }
                orderClause += columName + ' ' + order;
            }
        });

        //Filter clause
        var filterClause = '';
        this.queryCt.getComponent(1).getStore().each(function(record) {
            if (filterClause.length > 0) {
                filterClause += '\nAND ';
            }
            var rightOpeartor = record.get('RightOperator');
            if (rightOpeartor == 'MAX') {
                filterClause += record.get('ColumnName') + record.get('Operator') + '(SELECT MAX(' + record.get('ColumnName') + ') FROM ' + table + ')';
            } else {
                filterClause += record.get('ColumnName') + record.get('Operator') + rightOpeartor;
            }

        });

        var query = '';
        if (selectedFieldClause.length == 0) {
            query = 'SELECT * FROM ' + table
        } else {
            query = 'SELECT ' + selectedFieldClause + '\nFROM ' + table;
            if (filterClause.length > 0) {
                query += '\nWHERE ' + filterClause;
            }
            if (groupClause.length > 0) {
                query += '\nGROUP BY ' + groupClause;
            }
            if (orderClause.length > 0) {
                query += '\nORDER BY ' + orderClause;
            }
        }
        if(!this.win){
            this.win = Ext.create('Ext.window.Window',{
                width:500,
                height:350,
                closeAction:'hide',
                layout:'fit',
                x:50,
                items:[
                {
                    xtype:'textarea'
                }
                ]
            });
        }
        this.win.getComponent(0).setValue(query);
        this.win.show();
    }
});
