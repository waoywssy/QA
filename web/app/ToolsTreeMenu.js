Ext.define('qa.ToolsTreeMenu', {
    extend : 'qa.TreeMenuBase',
    xtype : 'toolstreemenu',
    requires : ['qa.TreeMenuBase'],
    width : 150,
    initComponent : function() {
        Ext.apply(this, {
            items : [{
                text : 'Delete',
                iconCls : 'delete',
                scope : this,
                handler : this.deleteNode
            } ,{
                text : 'Refresh',
                iconCls : 'refresh',
                scope : this,
                handler : this.refresh
            }]//end items
        });
        this.callParent();
    }
})