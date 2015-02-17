Ext.define('qa.QaResultTreePanel', {
    extend : 'Ext.tree.Panel',
    xtype : 'qaresulttreepanel',
    title: 'AutoQaResults',
    iconCls: 'nav', // see the HEAD section for style used
    useArrows: true,
    rootVisible: false,
    hideHeaders: true,
    requires:['qa.store.QaResultTreeStore'],
    initComponent : function() {
        Ext.apply(this, {
            store: new qa.store.QaResultTreeStore(), 
            columns: [{
                xtype: 'treecolumn', //this is so we know which column will show the tree
                width: 1000,
                dataIndex: 'name'
                ,renderer: function(value, record, param1){
                    if (param1.raw.iconCls=='bot' && param1.raw.needsQA){
                        return Ext.String.format('<span class="error">{0}</span>', value);    
                    }
                    return value;
                }
            }],
            viewConfig: {
                style: {
                    overflow: 'auto', 
                    overflowX: 'hidden'
                },
                listeners: {
                    itemclick:function( ths, record, item, index, e, eOpts ){
                        if(record.data.nodeType!=3){
                            return;
                        }
                        var tabPanel = this.ownerCt.ownerCt.ownerCt.child('tabpanel');
                        var tab = tabPanel.child("[title='"+record.data.name+"']");
                        if(tab){
                            tabPanel.setActiveTab(tab);
                            return;
                        }

                        tabPanel.add({
                            xtype:'reportpanel',
                            itemId:record.data.id,
                            title:record.data.name,
                            closable:true,
                            isDefault:true,
                            node:record.data.refer
                        }).show();
                    }
                }
            }
        });//end apply
        this.callParent();
    }
}
)
