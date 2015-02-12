Ext.define('qa.reportdesign.LineChartPalette', {
    extend: 'Ext.form.Panel',
    alias: 'widget.linechartpalette',
    title: 'Line Chart Palette',
    requires: ['qa.model.LineChartModel'],
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
                model: 'qa.model.LineChartModel'
            }),
            items: [
                {
                    xtype: 'numberfield',
                    name: 'ReportID',
                    value: this.node,
                    hidden :true
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'Category',
                    name: 'Category',
                    allowBlank: false
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'Lines',
                    name: 'LineColumns',
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