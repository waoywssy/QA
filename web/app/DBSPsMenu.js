Ext.define('qa.DBSPsMenu', {
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
                text : 'SP Scripts',
                scope : this,
                iconCls:'src',
                handler : this.generateScript
            }, {
                text : 'Sync SP',
                iconCls : 'sync',
                scope : this,
                handler : this.generateScript
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