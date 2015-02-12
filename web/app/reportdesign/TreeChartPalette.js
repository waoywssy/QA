Ext.define('qa.reportdesign.TreeChartPalette', {
    extend: 'Ext.form.Panel',
    alias: 'widget.treechartpalette',
    requires: ['qa.model.TreeReportModel'],
    title: 'Tree Report Palette',
    bodyPadding: 5,
    acchor: '100%',
    layout: {
        type: 'vbox',
        align: 'stretch' // Child items are stretched to full width
    },
    initComponent: function() {
        Ext.apply(this, {
            node: this.node,
            url: 'ReportDesignServlet?method=updateChartConfig&ReportID=' + this.node,
            reader: new Ext.data.reader.Json({
                model: 'qa.model.TreeReportModel'
            }),
            items: [{
                    xtype: 'numberfield',
                    name: 'ReportID',
                    value: this.node,
                    hidden: true
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'CatID',
                    name: 'CatID',
                    allowBlank: false
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'CatParentID',
                    name: 'CatParentID',
                    allowBlank: false
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'CatName',
                    name: 'CatName',
                    allowBlank: false
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'CatLevel',
                    name: 'CatLevel',
                    allowBlank: false
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'LastFound',
                    name: 'LastFound',
                    allowBlank: false
                }, {
                    xtype: 'combobox',
                    fieldLabel: 'LostPoint',
                    name: 'LostPoint',
                    queryMode: 'local',
                    value: '1',
                    store: [['1', 'Last one'], ['2', 'Last two']]
                }],
            buttons: [{
                    text: 'Save',
                    disabled: true,
                    formBind: true,
                    handler: function() {
                        this.up('form').getForm().submit({
                            jsonSubmit: true,
                            waitMsg: 'Saving Data...'
                        });
                    }
                }
            ],
            listeners: {
                afterrender: function(panel, options) {
                    var form = panel.getForm();
                    var reportID = form.findField('ReportID').getValue();
                    if (panel.node) {
                        form.load({
                            url: 'ReportDesignServlet?method=getChartConfig&ReportID=' + panel.node,
                            waitMsg: 'Loading...'
                        });
                    }
                }
            }
            //end initComponent items
        });
        this.callParent();
    }
});
