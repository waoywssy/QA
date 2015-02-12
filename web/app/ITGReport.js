Ext.define('qa.ITGReport', {
    extend : 'Ext.ux.IFrame',
    title : 'ITGReport',
    initComponent : function() {
        Ext.apply(this, {
            loadMask: 'Loading...',
            listeners:{
                afterrender:function(ths,opts){
                    ths.load('http://10.10.0.40/drupal/');
                }
            }
        });
        this.callParent();
    }
})