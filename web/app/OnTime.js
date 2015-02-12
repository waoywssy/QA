Ext.define('qa.OnTime', {
    extend : 'Ext.ux.IFrame',
    title : 'OnTime',
    initComponent : function() {
        Ext.apply(this, {
            loadMask: 'Loading...',
            listeners:{
                afterrender:function(ths,opts){
                    ths.load('http://10.8.253.139/Outsourcers');
                }
            }
        });
        this.callParent();
    }
})