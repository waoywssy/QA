Ext.define('qa.reportdesign.TableChartPalette', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.tablechartpalette',
    requires: ['qa.model.StandardValueModel'
                , 'qa.store.StandardValueStore'
                , 'Ext.grid.plugin.RowEditing'
                , 'qa.model.LineChartModel'],
    title: 'Table Report Palette',
    layout: 'border',
    height: 550,
    initComponent: function() {
        var linkValueColumnCombo = Ext.create('Ext.form.field.ComboBox', {
            queryMode: 'local',
            displayField: 'ColumnName',
            valueField: 'ColumnName',
            store: Ext.create('Ext.data.Store', {
                fields: ['ColumnName']
            })
        });
        Ext.apply(this, {
            node: this.node,
            linkValueColumnCombo: linkValueColumnCombo,
            listeners: {
                afterrender: function(panel, options) {
                    if (panel.node) {
                        panel.loadData(panel.node, false);
                    }
                }
            },
            tools: [
                {
                    type: 'refresh',
                    tooltip: 'Sync report columns',
                    scope: this,
                    handler: function(event, toolEl, panel) {
                        if (this.node) {
                            this.loadData(this.node, true);
                        } else {
                            Ext.Msg.alert('Warn', 'Please save query');
                        }
                    }
                }
            ],
            items: [
                {
                    itemId: 'default',
                    region: 'center',
                    store: Ext.create('qa.store.StandardValueStore'),
                    xtype: 'grid',
                    flex: 0.5,
                    sortableColumns: false,
                    enableColumnHide: false,
                    enableColumnMove: false,
                    viewConfig: {
                        plugins: {
                            ptype: 'gridviewdragdrop'
                        },
                        listeners: {
                            drop: {
                                fn: function(node, data, overModel, dropPosition, eOpts) {
                                    var endPosition = -1;
                                    if (dropPosition == "before") {
                                        endPosition = node.rowIndex - 1;
                                    } else {
                                        endPosition = node.rowIndex + 1;
                                    }

                                    Ext.Ajax.request({
                                        url: 'StandardValueServlet',
                                        params: {
                                            method: 'updateColumnOrder',
                                            id: data.records[0].data.ID,
                                            reportId: data.records[0].data.ReportID,
                                            end: endPosition + 1
                                        },
                                        success: function(response) {
                                            Ext.example.msg('', response.responseText);
                                            //<--- the server response
                                        }
                                    });
                                },
                                scope: this
                            }
                        }
                    },
                    columns: [{
                            header: 'ColumnName',
                            dataIndex: 'ColumnName',
                            width: 120
                        }, {
                            xtype: 'numbercolumn',
                            header: 'Min',
                            dataIndex: 'MinValue',
                            editor: {
                                allowBlank: true,
                                xtype: 'numberfield'
                            },
                            width: 90
                        }, {
                            xtype: 'numbercolumn',
                            header: 'Max',
                            dataIndex: 'MaxValue',
                            editor: {
                                xtype: 'numberfield'
                            },
                            width: 90
                        }, {
                            xtype: 'numbercolumn',
                            header: 'MinP(%)',
                            dataIndex: 'AvgValue',
                            editor: {
                                xtype: 'numberfield'
                            },
                            width: 90
                        }, {
                            xtype: 'numbercolumn',
                            header: 'MaxP(%)',
                            dataIndex: 'Fluctuation',
                            editor: {
                                xtype: 'numberfield'
                            },
                            width: 60
                        }, {
                            xtype: 'checkcolumn',
                            header: 'Show',
                            dataIndex: 'Show',
                            width: 60,
                            editor: {
                                xtype: 'checkbox'
                            }
                        }, {
                            xtype: 'actioncolumn',
                            header: 'Link',
                            width: 50,
                            items: [{
                                    iconCls: 'edit', // Use a URL in the icon config
                                    tooltip: 'Edit',
                                    handler: function(grid, rowIndex, colIndex) {
                                        var rec = grid.getStore().getAt(rowIndex);
                                        var formPanel = this.up('gridpanel').nextSibling().down('form')
                                        var form1 = formPanel.getForm();
                                        var form2 = formPanel.nextSibling().getForm();
                                        var paramMappingStore = formPanel.nextSibling().down('gridpanel').getStore();
                                        myMask = new Ext.LoadMask(formPanel.ownerCt, {
                                            msg: "Loading..."
                                        });
                                        myMask.show();
                                        Ext.Ajax.request({
                                            url: 'ReportDesignServlet?method=getLinkReport&ReportID=' + rec.get('ReportID') + "&LinkColumn=" + rec.get('ColumnName'),
                                            success: function(response, options) {
                                                myMask.hide();
                                                var json = Ext.decode(response.responseText);
                                                form1.loadRecord(new qa.model.LineChartModel(json['NameLink']));
                                                form2.loadRecord(new qa.model.LineChartModel(json['ValueLink']));
                                                paramMappingStore.loadData(json['ParamsMapping']);
                                            }
                                        });

                                    }
                                }]
                        }],
                    plugins: [{
                            ptype: 'rowediting',
                            autoCancel: true
                        }]
                }, {
                    title: 'Column Link',
                    region: 'south',
                    xtype: 'panel',
                    flex: 0.5,
                    layout: 'border',
                    items: [
                        {
                            region: 'west',
                            flex: 0.4,
                            xtype: 'form',
                            frame: true,
                            url: 'ReportDesignServlet?method=updateHeadColumnReport',
                            waitMsgTarget: true,
                            bodyPadding: 10,
                            defaults: {
                                allowBlank: false,
                                editable: false,
                                labelWidth: 80,
                                width: 300
                            },
                            items: [
                                {
                                    xtype: 'textfield',
                                    fieldLabel: 'LinkColumn',
                                    name: 'LinkColumn',
                                    readOnly: true
                                }, {
                                    xtype: 'numberfield',
                                    fieldLabel: 'Top Rows',
                                    name: 'TopRows',
                                    emptyText: '0:delete',
                                    editable: true,
                                    value: 30
                                },
                                {
                                    xtype: 'combo',
                                    queryMode: 'local',
                                    displayField: 'ColumnName',
                                    valueField: 'ColumnName',
                                    fieldLabel: 'Category',
                                    name: 'Category',
                                    store: Ext.create('Ext.data.Store', {
                                        fields: ['ColumnName']
                                    })
                                }, {
                                    xtype: 'combo',
                                    queryMode: 'local',
                                    displayField: 'ColumnName',
                                    valueField: 'ColumnName',
                                    fieldLabel: 'LineColumns',
                                    name: 'LineColumns',
                                    multiSelect: true,
                                    store: Ext.create('Ext.data.Store', {
                                        fields: ['ColumnName']
                                    })
                                }, {
                                    xtype: 'hidden',
                                    name: 'ReportID',
                                    value: this.node
                                }
                            ],
                            buttons: [
                                {
                                    text: 'Save',
                                    disabled: true,
                                    formBind: true,
                                    handler: function() {
                                        this.up('form').getForm().submit({
                                            waitMsg: 'Saving Data...'
                                        });
                                    }
                                }
                            ]
                        }, {
                            region: 'center',
                            flex: 0.6,
                            xtype: 'form',
                            frame: true,
                            bodyPadding: 10,
                            items: [
                                {
                                    xtype: 'textfield',
                                    fieldLabel: 'LinkValueColumn',
                                    name: 'LinkColumn',
                                    readOnly: true,
                                    allowBlank: false
                                },
                                {
                                    xtype: 'textfield',
                                    fieldLabel: 'Sub Report',
                                    name: 'SubReportName',
                                    allowBlank: false,
                                    readOnly: true,
                                    listeners: {
                                        afterrender: function(field, eOpts) {
                                            this.dropTarget = new Ext.dd.DropTarget(field.getEl(), {
                                                ddGroup: 'treenodedrop_group',
                                                notifyDrop: function(ddSource, e, data) {
                                                    if (data.records[0].data.nodeType != 15) {
                                                        return false;
                                                    }
                                                    field.setValue(data.records[0].data.name);
                                                    field.nextSibling().setValue(data.records[0].data.id);
                                                    var store = field.up('form').down('gridpanel').getStore();
                                                    store.getProxy().setExtraParam('ReportID', data.records[0].data.id);
                                                    store.load();
                                                    return true;
                                                }
                                            });
                                        },
                                        beforeDestroy: function() {
                                            var target = this.dropTarget;
                                            if (target) {
                                                target.unreg();
                                                this.dropTarget = null;
                                            }
                                        }
                                    }
                                }, {
                                    xtype: 'hidden',
                                    name: 'SubReportID'
                                }, {
                                    xtype: 'hidden',
                                    name: 'ReportID',
                                    value: this.node
                                }, {
                                    xtype: 'grid',
                                    sortableColumns: false,
                                    enableColumnHide: false,
                                    plugins: [{
                                            ptype: 'cellediting',
                                            autoCancel: true
                                        }],
                                    store: Ext.create('Ext.data.Store', {
                                        proxy: {
                                            type: 'ajax',
                                            reader: 'json',
                                            url: 'ReportDesignServlet?method=getQueryParams'
                                        },
                                        fields: [
                                            'Name', 'DataType', 'ValueColumn'
                                        ]
                                    }),
                                    columns: [
                                        {
                                            text: 'Name',
                                            width: 70,
                                            dataIndex: 'Name'
                                        }, {
                                            text: 'DataType',
                                            width: 70,
                                            dataIndex: 'DataType'
                                        }, {
                                            text: 'ValueColumn',
                                            width: 120,
                                            dataIndex: 'ValueColumn',
                                            editor: linkValueColumnCombo
                                        }
                                    ]
                                }
                            ],
                            buttons: [{
                                    text: 'Delete',
                                    handler: function() {
                                        var formPanel = this.up('form');
                                        var form = formPanel.getForm();

                                        Ext.Ajax.request({
                                            url: 'ReportDesignServlet',
                                            params: {
                                                method: 'deleteLinkReport',
                                                ReportID: form.findField('ReportID').getValue(),
                                                LinkColumn: form.findField('LinkColumn').getValue()
                                            }
                                        });
                                        form.findField('SubReportName').setValue('');
                                        formPanel.down('gridpanel').getStore().removeAll();
                                    }
                                }, {
                                    text: 'Save',
                                    disabled: true,
                                    formBind: true,
                                    handler: function() {
                                        var form = this.up('form');
                                        var i = 0;
                                        var formdata = "{";
                                        form.down('gridpanel').getStore().each(function(record) {
                                            if (i != 0) {
                                                formdata += ",";
                                                i = 1;
                                            }
                                            formdata += "\"" + record.get('Name') + "\":\"" + record.get('ValueColumn') + "\"";
                                        });
                                        formdata += "}";
                                        form.getForm().submit({
                                            url: 'ReportDesignServlet?method=updateColumnValueLink',
                                            params: {
                                                params: formdata
                                            },
                                            waitMsg: 'Saving Data...'
                                        });
                                    }
                                }
                            ]
                        }
                    ]
                }]//end items
        }
        );
        this.callParent();
    },
    loadData: function(reportId, syncReportColumn) {
        var store = this.getComponent('default').getStore();
        if (syncReportColumn == null) {
            syncReportColumn = false;
        }
        if (reportId != null) {
            store.getProxy().setExtraParam("node", reportId);
        }
        store.getProxy().setExtraParam("syncReportColumn", syncReportColumn);
        store.load({
            scope: this,
            callback: function(records, operation, success) {
                //reset link head panel
                var formPanel = this.down('form');
                var form = formPanel.getForm();
                form.findField('Category').getStore().loadRecords(records);
                form.findField('LineColumns').getStore().loadRecords(records);
                this.linkValueColumnCombo.getStore().loadRecords(records);
            }
        });
    }
})