Ext.define('qa.NoteEditor', {
    extend : 'Ext.window.Window',
    xtype : 'noteeditor',
    requires:['qa.model.Note'],
    title : 'Note Pad',
    width : 650,
    height : 400,
    closeAction : 'hide',
    maximizable : true,
    layout:'fit',
    constrainHeader: true,
    initComponent : function() {
        Ext.apply(this, {
            items : [{
                xtype : 'form',
                layout : {
                    type:'fit',
                    align:'stretchmax'
                },
                reader : new Ext.data.reader.Json({
                    model : 'qa.model.Note'
                }),
                items : [{
                    flex:1,
                    xtype : 'htmleditor',
                    name:'NoteContent',
                    enableSourceEdit : false,
                    enableAlignments : false,
                    enableFontSize:false,
                    listeners : {
                        afterrender : function(ths, opts) {
                            var bar = ths.getToolbar();
                            bar.add({
                                text : 'Time',
                                handler : function() {
                                    var date = '<b>'+Ext.Date.format(new Date(), 'Y-m-d h:i:s')+'</b><br>&nbsp;';
                                    this.ownerCt.ownerCt.insertAtCursor(date);
                                }
                            });
                            bar.add({
                                itemId:'save',
                                iconCls:'save',
                                handler : function() {
                                    this.up('form').getForm().submit({
                                        url:'TreeNodeServlet?method=updateNote',
                                        waitMsg:'Saving...'
                                    });
                                }
                            });
                        }
                    }
                },{
                    xtype:'hidden',
                    name:'ID'
                }]
            }]//end items

        });
        this.callParent();
    },
    showPad:function(id){
        var form=this.down('form').getForm();
 
        this.show();
        form.load({
            url:'TreeNodeServlet?method=getNote&ID='+id
        });
    },
    showContent:function(html){
        this.show();
        this.down('htmleditor').setValue(html);
    }
})