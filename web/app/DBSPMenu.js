Ext.define('qa.DBSPMenu', {
    extend : 'qa.TreeMenuBase',
    xtype : 'dbtablesmenu',
    requires : ['qa.TreeMenuBase','qa.store.ServerStore'],
    width : 150,
    initComponent : function() {
        Ext.apply(this, {
            items : [{
                xtype : 'combo',
                hideLabel : true,
                store : Ext.create('qa.store.ServerStore'),
                displayField : 'name',
                value : '127.0.0.1',
                emptyText : 'Select server'
            },{
                text : 'SP Script',
                iconCls:'src',
                scope : this,
                handler : this.generateScript
            }, {
                text : 'Delete',
                iconCls : 'delete',
                scope : this,
                handler : this.deleteNode
            }, '-'
            ,{
                text : 'Refresh',
                iconCls : 'refresh',
                scope : this,
                handler : this.refresh
            }]//end items
        });
        this.callParent();
    }
})