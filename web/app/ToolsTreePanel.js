Ext.define('qa.ToolsTreePanel', {
    extend : 'Ext.tree.Panel',
    xtype : 'toolstreepanel',
    title: 'Tools',
    iconCls: 'nav', // see the HEAD section for style used
    useArrows: true,
    rootVisible: false,
    hideHeaders: true,
    requires:['qa.store.SettingTreeStore','qa.ToolsTreeMenu'],
    initComponent : function() {
        this.menu1 = Ext.create('qa.ToolsTreeMenu');
        
        Ext.apply(this, {
            store: Ext.create('qa.store.SettingTreeStore',{
                root:{
                    id:'-1',
                    name:'Root'     
                }
            }), 
            columns: [{
                xtype: 'treecolumn', //this is so we know which column will show the tree
                width:1000,
                dataIndex: 'name'
            }],
            viewConfig: {
                style: {
                    overflow: 'auto', 
                    overflowX: 'hidden'
                }
            },
            listeners: {
                beforeitemcontextmenu:function(ths, record, item, index, e, eOpts){
                    e.preventDefault();
                    this.menu1.record=record;
                    this.menu1.treeView = ths;
                    this.menu1.showAt(e.getXY());

                },
                itemclick:function( ths, record, item, index, e, eOpts ){
                    var tabPanel=window.viewport.getComponent('centerTabPanel');
                    var tab = tabPanel.getComponent(record.data.id);

                    if(tab){
                
                    }
                    else if(record.data.nodeType==13){
                        //query
                        tab =tabPanel.add(Ext.create('qa.QueryEditor',{
                            title:record.data.name,
                            node:record.data.refer,
                            itemId:record.data.id
                        }));
                        tab.loadData();
                    } else  if(record.data.nodeType==15){
                        //report
                        tab =tabPanel.add({
                            xtype:'reportpanel',
                            itemId:record.data.id,
                            title:record.data.name,
                            closable:true,
                            node:record.data.refer
                        });
                    } else  if(record.data.nodeType==18){
                        //component
                        tab =tabPanel.add(Ext.create('qa.'+record.data.name,{
                            itemId:record.data.id,
                            title:record.data.name,
                            closable:true
                        }));
                    }
                    tabPanel.setActiveTab(tab);
                }
            }
        });//end apply
        this.callParent();
    }
}
)
