Ext.define('qa.LogPanel', {
    extend : 'Ext.panel.Panel',
    xtype : 'logpanel',
    width : 150,
    closable:true,
    loader:{
        url: 'LogServlet?method=getLog',
        autoLoad: false  
    },
    initComponent : function() {
        Ext.apply(this, {
            listeners:{
                afterrender:function(ths,options){
                    this.updateInterval = setInterval(function(ths){
                        ths.getLoader().load();
                    },7000,this);
                },
                destroy:function( ths, eOpts ){
                     clearInterval(this.updateInterval);
                }
            }
        });
        this.callParent();
    }
})