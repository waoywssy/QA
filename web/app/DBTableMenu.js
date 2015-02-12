Ext.define('qa.DBTableMenu', {
    extend : 'qa.TreeMenuBase',
    xtype : 'dbtablemenu',
    requires : ['qa.TreeMenuBase'],
    width : 150,
    height : 350,
    initComponent : function() {
        Ext.apply(this, {
            items : [{
                xtype : 'combo',
                hideLabel : true,
                store : Ext.create('qa.store.ServerStore'),
                displayField : 'name',
                value : '127.0.0.1',
                emptyText : 'Select server'
            }, {
                text : 'SELECT TOP',
                scope : this,
                handler : this.generateScript
            }, {
                text : 'CREATE',
                scope : this,
                handler : this.generateScript
            }, {
                text : 'DROP',
                scope : this,
                handler : this.generateScript
            }, {
                text : 'INSERT',
                scope : this,
                handler : this.generateScript
            }, {
                text : 'UPDATE',
                scope : this,
                handler : this.generateScript
            }, {
                text : 'DELETE',
                scope : this,
                handler : this.generateScript
            }, {
                text : 'TRUNCATE',
                scope : this,
                handler : this.generateScript
            }, {
                text : 'INSERT PROC',
                scope : this,
                handler : this.generateScript
            },
            '-' 
            ,{
                text : 'Sync Column',
                iconCls : 'sync',
                scope : this,
                handler : this.generateScript
            }, {
                text : 'Refresh',
                iconCls : 'refresh',
                scope : this,
                handler : this.refresh
            }]//end items
        });
        this.callParent();
    }
})