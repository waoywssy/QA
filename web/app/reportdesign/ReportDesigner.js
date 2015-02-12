Ext.define('qa.reportdesign.ReportDesigner', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.reportdesigner',
    title: 'Report Designer',
    bodyPadding: 5,
    closable: true,
    height: 600,
    requires: ['qa.store.CategoryTreeStore'
                , 'qa.store.DatabaseStore'
                , 'qa.store.ServerStore'
                , 'qa.reportdesign.QueryParamsEditor'
                , 'qa.model.Query'
                , 'qa.reportdesign.LineChartPalette'
                , 'qa.reportdesign.PivotChartPalette'
                , 'qa.reportdesign.TableChartPalette'
                , 'qa.reportdesign.TreeChartPalette'
    ],
    initComponent: function() {
        Ext.apply(this, {
            items: [{
                    title: 'Query',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [{
                            title: 'Query Text',
                            xtype: 'form',
                            flex: 1,
                            layout: {
                                type: 'vbox',
                                align: 'stretch' // Child items are stretched to full width
                            },
                            tools: [{
                                    type: 'gear',
                                    tooltip: 'execute the query',
                                    handler: this.executeQuery
                                }],
                            reader: new Ext.data.reader.Json({
                                model: 'qa.model.Query'
                            }),
                            items: [{
                                    xtype: 'textarea',
                                    hideLabel: true,
                                    name: 'queryText',
                                    flex: 3
                                }, {
                                    xtype: 'panel',
                                    title: 'Query Parameters',
                                    flex: 5,
                                    layout: 'hbox',
                                    bodyPadding: '5 0 0 5',
                                    items: [
                                        {
                                            xtype: 'container',
                                            defaults: {
                                                width: 300,
                                                labelWidth: 90
                                            },
                                            border: 1,
                                            padding: '5 5 5 5',
                                            style: {
                                                borderColor: 'silver',
                                                borderStyle: 'solid'
                                            },
                                            items: [{
                                                    xtype: 'textfield',
                                                    name: 'name',
                                                    fieldLabel: 'Report Name',
                                                    allowBlank: false
                                                }, {
                                                    xtype: 'combobox',
                                                    fieldLabel: 'Server',
                                                    name: 'server',
                                                    displayField: 'name',
                                                    valueField: 'name',
                                                    value: '127.0.0.1',
                                                    store: Ext.create('qa.store.ServerStore')
                                                }, {
                                                    xtype: 'combobox',
                                                    fieldLabel: 'Database',
                                                    name: 'database',
                                                    displayField: 'name',
                                                    valueField: 'name',
                                                    value: 'RetailListing',
                                                    store: Ext.create('qa.store.DatabaseStore')
                                                }, {
                                                    xtype: 'combobox',
                                                    fieldLabel: 'Analysis Start',
                                                    name: 'startRow',
                                                    displayField: 'name',
                                                    valueField: 'name',
                                                    value: 1,
                                                    store: [0, 1, 2],
                                                    emptyText: '0:only qa first row'
                                                }, {
                                                    xtype: 'hiddenfield',
                                                    name: 'node',
                                                    margin: '0',
                                                    value: this.node
                                                }, {
                                                    xtype: 'hiddenfield',
                                                    name: 'parentNode',
                                                    margin: '0',
                                                    value: this.parentNode
                                                }, {
                                                    xtype: 'hiddenfield',
                                                    name: 'queryType',
                                                    margin: '0'
                                                }, {
                                                    xtype: 'hiddenfield',
                                                    name: 'isReport',
                                                    margin: '0',
                                                    value: true
                                                }, {
                                                    xtype: 'hiddenfield',
                                                    name: 'changeServer',
                                                    margin: '0',
                                                    value: true
                                                }, {
                                                    xtype: 'button',
                                                    text: 'Save',
                                                    margin: '0 0 0 100',
                                                    width: 50,
                                                    handler: function() {
                                                        var form = this.up('form').getForm();
                                                        var tabpanel = this.up('tabpanel');
                                                        form.submit({
                                                            scope: this,
                                                            url: "QueryEditorServlet?method=update",
                                                            success: function(form, action) {
                                                                Ext.example.msg('', 'success');
                                                                tabpanel.setTitle(form.findField('name').getValue());

                                                                if (!tabpanel.node) {
                                                                    tabpanel.node = action.result.msg;
                                                                    form.findField('node').setValue(action.result.msg);
                                                                    tabpanel.down('queryparamseditor').reportID = action.result.msg;
                                                                }
                                                            }
                                                        });
                                                    }
                                                }]//end Query Parameters Panel
                                        }, {
                                            xtype: 'queryparamseditor',
                                            margin: '0 0 0 20',
                                            reportID: this.node
                                        }
                                    ]
                                }], //end query form items
                            listeners: {
                                afterrender: {
                                    fn: function(panel, eOpts) {
                                        var tabpanel = this.up('tabpanel');
                                        var node = tabpanel.node;
                                        if (node) {
                                            panel.getForm().load({
                                                url: 'ReportDesignServlet?method=getReportInfo&node=' + node,
                                                waitMsg: 'Loading...',
                                                scope: this,
                                                success: function(form, action) {
                                                    var reportType = form.findField('queryType').getValue();
                                                    tabpanel.reportType = reportType;
                                                }
                                            });
                                        }
                                    }
                                }
                            }

                        }]
                }, //end query tab panel
                {
                    title: 'Results',
                    layout: 'fit',
                    items: [{
                            xtype: 'gridpanel',
                            sortableColumns: false,
                            columns: [{
                                    header: 'Data'
                                }]
                        }]
                }, {
                    title: 'Report Designer',
                    layout: 'fit',
                    items: [{
                            xtype: 'panel',
                            layout: 'border',
                            items: [{
                                    xtype: 'panel',
                                    region: 'west',
                                    width: 230,
                                    collapsible: true,
                                    header: false,
                                    split: true,
                                    layout: {
                                        type: 'vbox',
                                        align: 'stretch'
                                    },
                                    items: [{
                                            title: 'Selected Fields',
                                            flex: 4,
                                            tools: [{
                                                    type: 'refresh'
                                                }],
                                            layout: 'fit',
                                            items: [{
                                                    itemId: 'selectedColumns',
                                                    xtype: 'gridpanel',
                                                    hideHeaders: true,
                                                    forceFit: true,
                                                    columns: [{
                                                            header: 'ColumnName',
                                                            dataIndex: 'name'
                                                        }],
                                                    viewConfig: {
                                                        stripeRows: true,
                                                        enableTextSelection: true
                                                    },
                                                    store: {
                                                        fields: [{
                                                                name: 'name'
                                                            }]
                                                    }
                                                }]
                                        }, {
                                            title: 'Palette',
                                            flex: 3,
                                            layout: 'fit',
                                            items: [{
                                                    xtype: 'gridpanel',
                                                    hideHeaders: true,
                                                    columns: [{
                                                            text: 'Image',
                                                            dataIndex: 'img',
                                                            flex: 3
                                                        }, {
                                                            text: 'Name',
                                                            dataIndex: 'name',
                                                            flex: 7
                                                        }],
                                                    viewConfig: {
                                                        plugins: {
                                                            ptype: 'gridviewdragdrop',
                                                            dragText: 'Drag and drop to reorganize',
                                                            ddGroup: 'grid-to-form'
                                                        }
                                                    },
                                                    store: Ext.create('Ext.data.Store', {
                                                        fields: ['name', 'img', 'chartType'],
                                                        data: [{
                                                                'name': 'Bar Chart',
                                                                'chartType': 'barchart',
                                                                "img": "<image src=icons/palette_bar_chart.png width=40 height=40>"

                                                            }, {
                                                                'name': 'Pie Chart',
                                                                'chartType': 'piechart',
                                                                'img': '<image src=icons/palette_pie_chart.png width=40 height=40>'
                                                            }, {
                                                                'name': 'Line Chart',
                                                                'chartType': 'linechart',
                                                                'img': '<image src=icons/palette_line_chart.png width=40 height=40>'
                                                            }, {
                                                                'name': 'Table',
                                                                'chartType': 'tablechart',
                                                                'img': '<image src=icons/palette_table.png width=40 height=40>'
                                                            }, {
                                                                'name': 'Pivot Table',
                                                                'chartType': 'pivotchart',
                                                                'img': '<image src=icons/palette_crosstab.png width=40 height=40>'
                                                            }, {
                                                                'name': 'Tree',
                                                                'chartType': 'treechart',
                                                                'img': '<image src=icons/palette_tree.png width=40 height=40>'
                                                            }]
                                                    })
                                                }]
                                        }]
                                }, {
                                    region: 'center',
                                    title: 'Drag & drop here a widget from the palette',
                                    dropGroup: 'Palette',
                                    align: 'center',
                                    autoScroll: true,
                                    bodyPadding: 10,
                                    layout: 'fit',
                                    listeners: {
                                        afterrender: {
                                            scope: this,
                                            fn: function(panel, eOpts) {
                                                var body = panel.body;
                                                if (this.reportType) {
                                                    panel.add({
                                                        xtype: this.reportType+'palette',
                                                        node: this.node
                                                    });
                                                }
                                                this.formPanelDropTarget = new Ext.dd.DropTarget(body, {
                                                    ddGroup: 'grid-to-form',
                                                    notifyEnter: function(ddSource, e, data) {
                                                        //Add some flare to invite drop.
                                                        body.stopAnimation();
                                                        body.highlight();
                                                    },
                                                    notifyDrop: function(ddSource, e, data) {
                                                        var node = panel.up('tabpanel').node;
                                                        if (!node) {
                                                            Ext.MessageBox.show({
                                                                title: 'Tip',
                                                                msg: 'Please save query'
                                                            });
                                                            return false;
                                                        }
                                                        panel.remove(panel.down('panel'));
                                                        var chartType = data.records[0].data.chartType;
                                                        panel.add({xtype: chartType + 'palette', node: node});

                                                        Ext.Ajax.request({
                                                            url: 'ReportDesignServlet',
                                                            params: {
                                                                method: 'updateReportType',
                                                                type: chartType,
                                                                node: node
                                                            }
                                                        });
                                                        return true;
                                                    }
                                                });
                                            }
                                        },
                                        beforeDestroy: function() {
                                            var target = this.formPanelDropTarget;
                                            if (target) {
                                                target.unreg();
                                                this.formPanelDropTarget = null;
                                            }
                                        }
                                    }
                                }]
                        }]
                } //end report design
            ]//end tabpanel
        }//end apply config
        );
        //end apply
        this.callParent();
    }, //end initComponent function
    executeQuery: function(event, toolEl, panel) {
        var postdata = this.up('form').getForm().getValues();
        postdata.method = 'executeQuery';
        var tabpanel = this.up('tabpanel');
        var paramStore = tabpanel.down('queryparamseditor').getStore();
        var listRecord = new Array();
        Ext.each(paramStore.getRange(), function(record) {
            listRecord.push(record.data);
        });
        postdata.params = Ext.encode(listRecord);

        tabpanel.setActiveTab(1);
        myMask = new Ext.LoadMask(tabpanel.getActiveTab(), {
            msg: "Please wait..."
        });

        myMask.show();
        Ext.Ajax.request({
            scope: this,
            url: 'ReportDesignServlet',
            params: postdata,
            success: function(response) {
                myMask.hide();
                var text = response.responseText;
                var json = Ext.decode(text, true);
                if (!json) {
                    Ext.MessageBox.show({
                        title: 'Warn',
                        msg: response.responseText
                    });
                    return;
                }
                var storeFields = [];
                var gridColumns = [];
                var columnNames = [];
                gridColumns.push({
                    xtype: 'rownumberer',
                    align: 'center',
                    width: 30
                });
                for (var key in json[0]) {
                    storeFields.push({
                        'name': key
                    });
                    gridColumns.push({
                        'header': key,
                        'dataIndex': key
                    });
                    columnNames.push({
                        name: key
                    });
                }
                var store = Ext.create('Ext.data.Store', {
                    fields: storeFields,
                    data: json
                });
                var gridPanel = tabpanel.getComponent(1).down('gridpanel');
                gridPanel.reconfigure(store, gridColumns);
                tabpanel.getComponent(2).down('gridpanel#selectedColumns').store.loadData(columnNames);
            }
        });
    } //end executeQuery function
});
