Ext.define('qa.DefaultTreeMenu', {
    extend: 'qa.TreeMenuBase',
    xtype: 'dbtablesmenu',
    requires: ['qa.TreeMenuBase','qa.reportdesign.ReportDesigner'],
    width: 150,
    initComponent: function() {
        Ext.apply(this, {
            items: [{
                    text: 'Edit',
                    scope: this,
                    iconCls: 'edit',
                    handler: this.edit
                }, {
                    text: 'Delete',
                    scope: this,
                    iconCls: 'delete',
                    handler: this.deleteNode
                },
                '-',
                {
                    text: 'BookMark',
                    iconCls: 'favourite',
                    scope: this,
                    handler: this.bookMark
                }, {
                    text: 'Refresh',
                    iconCls: 'refresh',
                    scope: this,
                    handler: this.refresh
                }]//end items
        });
        this.callParent();
    },
    edit: function() {
        var tabPanel = window.viewport.getComponent('centerTabPanel');
        var tab = tabPanel.getComponent(this.record.data.id + "_edit");

        if (tab) {
            tabPanel.setActiveTab(tab);
        } else if (this.record.data.nodeType == 15) {
            tab = tabPanel.add({
                xtype: 'reportdesigner',
                title: this.record.data.name,
                parentNode: this.record.data.parentId,
                node: this.record.data.id,
                itemId: this.record.data.id + "_edit"
            });

            tabPanel.setActiveTab(tab);
        }
    },
    bookMark: function() {
        var type = this.record.get('nodeType');
        if (type == 15 || type == 13) {
            Ext.Ajax.request({
                url: 'TreeNodeServlet',
                params: {
                    method: 'bookMark',
                    name: this.record.get('name'),
                    refer: this.record.get('id'),
                    nodeType: type
                }
            });
        }
    }
})

