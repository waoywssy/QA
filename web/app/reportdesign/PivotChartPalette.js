Ext.define('qa.reportdesign.PivotChartPalette', {
    extend: 'Ext.form.Panel',
    alias: 'widget.pivotchartpalette',
    title: 'Pivot Chart Palette',
    requires: ['qa.model.PivotChartDesignModel'],
    width: 350,
    height: 250,
    initComponent: function() {
        Ext.apply(this, {
            node: this.node,
            url: 'ReportDesignServlet?method=updateChartConfig&ReportID=' + this.node,
            defaults: {
                width: 300,
                labelWidth: 80
            },
            bodyPadding: 5,
            reader: new Ext.data.reader.Json({
                model: 'qa.model.PivotChartDesignModel'
            }),
            items: [
                {
                    xtype: 'numberfield',
                    name: 'ReportID',
                    value: this.node,
                    hidden: true
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'xAxis',
                    name: 'xAxis',
                    allowBlank: false
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'xAxisValues',
                    name: 'xAxisValues',
                    allowBlank: true
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'yAxis',
                    name: 'yAxis',
                    allowBlank: false
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'CountColumn',
                    name: 'CountColumn',
                    allowBlank: false
                }
            ], //end items
            buttons: [
                {
                    text: 'Save',
                    handler: function() {
                        this.up('form').getForm().submit({
                            jsonSubmit: true,
                            waitMsg: 'Saving...'
                        });
                    }
                }
            ],
            listeners: {
                afterrender: function(panel, options) {
                    if (panel.node) {
                        panel.getForm().load({
                            url: 'ReportDesignServlet?method=getChartConfig',
                            waitMsg: 'Loading...',
                            params: {
                                ReportID: this.node
                            }
                        });
                    }
                }
            }
        });
        //end apply

        this.callParent();
    }
})