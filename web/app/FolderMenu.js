Ext.define('qa.FolderMenu', {
    extend : 'qa.TreeMenuBase',
    xtype : 'foldermenu',
    requires : ['qa.TreeMenuBase'],

    initComponent : function() {
        Ext.apply(this, {
            items : [{
                text : 'New Query',
                scope : this,
                iconCls : 'query',
                handler : this.newQuery
            }, {
                text : 'New Report',
                scope : this,
                iconCls : 'report',
                handler : this.newReport
            }, {
                text : 'New Folder',
                iconCls : 'folder',
                scope : this,
                handler : this.newFolder
            }, {
                text : 'Upload File',
                scope : this,
                iconCls : 'upload',
                handler : this.uploadFile
            }, '-', {
                text : 'Delete',
                scope : this,
                iconCls : 'delete',
                handler : this.deleteNode
            }, {
                text : 'Refresh',
                iconCls : 'refresh',
                scope : this,
                handler : this.refresh
            }]//end items
        });
        //end apply

        this.callParent();
    },
    newQuery : function(record) {
        var tabPanel = window.viewport.getComponent("centerTabPanel");
        tab = tabPanel.add(Ext.create('qa.QueryEditor', {
            closable : true,
            node : 0,
            parentNode : this.record.data.id
        }));
        tabPanel.setActiveTab(tab);
    },
    newReport : function(record) {
        var tabPanel =  window.viewport.getComponent("centerTabPanel");

        tab = tabPanel.add({
            xtype:'reportdesigner',
            closable : true,
            parentNode : this.record.data.id
        });
        tabPanel.setActiveTab(tab);
    },
    newFolder : function(record) {
        Ext.MessageBox.prompt('Name', 'Please enter folder name:', function(btn, text) {
            if (btn == 'ok') {
                Ext.Ajax.request({
                    url : 'TreeNodeServlet',
                    params : {
                        method : 'addFolder',
                        name : text,
                        node : this.record.data.id
                    },
                    success : function(response) {
                        Ext.example.msg('', response.responseText);
                    }
                });
            }
        }, this);
    },
    uploadFile : function(record) {
        if (!this.uploadWin) {
            var uploadForm = Ext.create('Ext.form.Panel', {
                border : false,
                bodyPadding : 10,
                defaults : {
                    anchor : '100%',
                    allowBlank : false,
                    msgTarget : 'side',
                    labelWidth : 50
                },
                items : [{
                    xtype : 'hiddenfield',
                    name : 'parentNode'
                }, {
                    xtype : 'textfield',
                    fieldLabel : 'Name',
                    name : 'name'
                }, {
                    xtype : 'filefield',
                    emptyText : 'Select an file',
                    fieldLabel : 'File',
                    name : 'file',
                    listeners : {
                        change : {
                            fn : function(ths, value, eOpts) {
                                var regex = /([^\\]*)\.\w+$/i;
                                if (regex.test(value)) {
                                    var fileName = regex.exec(value)[0];
                                    ths.previousSibling().setValue(fileName);
                                } else {
                                    ths.previousSibling().setValue(value);
                                }
                            }
                        }
                    }
                }],

                buttons : [{
                    text : 'Save',
                    handler : function() {
                        var form = this.up('form').getForm();
                        if (form.isValid()) {
                            form.submit({
                                scope : this,
                                url : 'UploadServlet',
                                waitMsg : 'Uploading your file...',
                                timeout : 180,
                                success : function(fp, o) {
                                    this.up('window').close();
                                    Ext.example.msg('', o.result.msg);
                                },
                                failure : function(fp, o) {
                                    this.up('window').close();
                                    Ext.example.msg('', o.result.msg);
                                }
                            });
                        }
                    }
                }, {
                    text : 'Reset',
                    handler : function() {
                        this.up('form').getForm().reset();
                    }
                }]
            });
            //end create upload form
            this.uploadWin = Ext.widget('window', {
                title : 'File Upload',
                closeAction : 'hide',
                width : 350,
                height : 150,
                layout : 'fit',
                resizable : true,
                modal : true,
                items : uploadForm
            });
        }//end if create upload form
        var form = this.uploadWin.down('form').getForm();
        form.reset();
        form.findField('parentNode').setValue(this.record.data.id);
        this.uploadWin.show();
    }//end upload file
})