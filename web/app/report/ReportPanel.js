Ext.define('qa.report.ReportPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.reportpanel',
    overflowY: 'auto',
    requires: ['qa.report.TableChart'
                , 'qa.report.LineChart'
                , 'qa.report.TreeChart'
                , 'qa.report.PivotChart'
    ],
    initComponent: function() {
        Ext.apply(this, {
            renderTo: this.target,
            loader: {
                loadMask: true,
                autoLoad: false,
                url: 'ViewReportServlet',
                ajaxOptions: {
                    timeout: 1000 * 60 * 5
                },
                params: {
                    method: this.isDefault ? 'viewDefault' : 'view',
                    node: this.node,
                    params: this.params,
                    linkColumn: this.linkColumn
                },
                renderer: function(loader, response, active) {
                    var target = loader.getTarget();
                    var text = response.responseText;
                    var type = response.getResponseHeader('content-type');
                    target.removeAll(true);
                    if (loader.params.method == 'viewDefault') {
                        var botID = loader.params.node;
                        target.add({
                            xtype: 'panel',
                            border: false,
                            items: [{
                                    margin: '0 10 0 10',
                                    xtype: 'button',
                                    text: 'Checked',
                                    handler: function() {
                                        Ext.Ajax.request({
                                            url: 'ViewReportServlet?method=markChecked&BotID=' + botID
                                        });
                                    }
                                }, {
                                    xtype: 'button',
                                    text: 'Server',
                                    handler: function() {
                                        Ext.Ajax.request({
                                            url: "ViewReportServlet?method=showBotServer&BotID=" + botID,
                                            success: function(response) {
                                                Ext.example.msg('', response.responseText);
                                            }
                                        });
                                    }
                                }
                            ]
                        });
                    }
                    if (type.indexOf('json') > 0) {
                        var reports = Ext.decode(text, true);
                        if (!reports) {
                            //show error
                            target.update(text);
                            return;
                        }
                        if (!reports.length) {
                            reports = [reports];
                        }

                        for (var i = 0; i < reports.length; i++) {
                            var json = reports[i];
                            var reportType = json.reportType;
                            if (!reportType || reportType.length == 0) {
                                target.generateQueryParamForm(json['params']);
                                continue;
                            }
                            target.add({
                                xtype: reportType,
                                data: json
                            });
                        }
                    }
                    else {
                        target.update(text);
                    }
                    return true;
                }
            },
            listeners: {
                afterrender: function(ths, eOpts) {
                    ths.getLoader().load();
                }
            }
        }
        );
        this.callParent();
    }, //initComponent
    generateQueryParamForm: function(json) {
        if (!json || json.length == 0) {
            Ext.Msg.alert('Error', Ext.encode(json));
            return;
        }
        this.removeAll(true);
        this.removeDocked(this.getDockedComponent(0));
        var items = [];
        var reportID;
        for (var i = 0; i < json.length; i++) {
            if (i == 0) {
                reportID = json[i].ReportID;
            }
            items.push({
                xtype: 'textfield',
                fieldLabel: json[i].Name,
                name: json[i].Name,
                value: json[i].DefaultValue
            });
        }
        items.push({
            xtype: 'button',
            width: '70',
            text: 'Execute',
            handler: function() {
                var formPanel = this.up('form');
                var formdata = "{";

                formPanel.getForm().getFields().each(function(field, index, length) {
                    if (index != 0) {
                        formdata += ",";
                    }
                    formdata += "\"" + field.getName() + "\":\"" + field.getValue().trim() + "\"";
                });
                formdata += "}";
                formPanel.up('panel').getLoader().load({
                    params: {
                        node: reportID,
                        method: 'view',
                        params: formdata
                    }
                });
            }
        });
        this.addDocked({
            xtype: 'form',
            layout: 'hbox',
            height: 35,
            margin: '5 0 0 0',
            defaults: {
                width: 170,
                labelWidth: 60,
                margin: '0 0 0 10'
            },
            items: items
        }, 'top');
    } //generateQueryParamForm
})