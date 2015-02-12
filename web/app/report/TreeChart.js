/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('qa.report.TreeChart', {
    extend: 'Ext.container.Container',
    alias: 'widget.treechart',
    overflowX: 'auto',
    margin: '10 0',
    initComponent: function() {
        Ext.apply(this, {
            data: this.data,
            listeners: {
                afterrender: {
                    fn: function(ths, opts) {
                        ths.renderTree();
                    }
                }
            }
        });//end Ext.apply
        this.callParent();
    },//end initComponent
    renderTree:function(){
        var StoreData=this.data.StoreData;
        var config= {
            xtype:'treepanel',
            bodyPadding:10,
            rootVisible: false,
            store:Ext.create('qa.store.CategoryTreeStore',{
                maxLastFound:StoreData.maxLastFound,
                root:StoreData
            }),
            tbar: [{
                text: 'Expand All',
                handler: function(){
                    this.up('treepanel').expandAll();
                }
            }, {
                text: 'Collapse All',
                handler:  function(){
                    this.up('treepanel').collapseAll();
                }
            }]
        }
        this.add(config);
    }//end renderTree
})
