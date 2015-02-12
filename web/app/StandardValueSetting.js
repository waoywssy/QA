/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('qa.StandardValueSetting', {
    extend : 'Ext.window.Window',
    xtype : 'standardvaluesetting',
    requires:['qa.model.StandardValueModel'],
    title : 'Standard Value Setting',
    width : 230,
    height : 270,
    closeAction : 'hide',
    constrainHeader: true,
    maximizable : false,
    layout:'fit',
    initComponent : function() {
        Ext.apply(this, {
            items : [{
                xtype : 'form',
                bodyPadding: '3 5 0',
                reader : new Ext.data.reader.Json({
                    model : 'qa.model.StandardValueModel'
                }),
                
                items : [{
                    xtype:'numberfield',
                    name:'ID',
                    hidden:true
                },{
                    xtype:'numberfield',
                    name:'ReportID',
                    hidden:true
                },{
                    xtype:'hidden',
                    name:'ColOrder'
                },{
                    xtype : 'textfield',
                    name : 'ColumnName',
                    labelWidth : 40,
                    fieldLabel : 'Name',
                    readOnly:true
                },{
                    xtype : 'numberfield',
                    name : 'MinValue',
                    labelWidth : 40,
                    fieldLabel : 'Min'
                },{
                    xtype : 'numberfield',
                    name : 'MaxValue',
                    labelWidth : 40,
                    fieldLabel : 'Max'
                },{
                    xtype : 'numberfield',
                    name : 'AvgValue',
                    labelWidth : 40,
                    fieldLabel : 'MinP(%)'
                },{
                    xtype : 'numberfield',
                    name : 'Fluctuation',
                    labelWidth : 40,
                    fieldLabel : 'MaxP(%)'
                },{
                    xtype : 'checkbox',
                    name : 'Show',
                    labelWidth : 40,
                    fieldLabel : 'Show'
                }],  
                buttons: [{
                    text: 'Save',
                    handler: function() {
                        var form = this.up('form').getForm();
                        if(form.isValid()){
                            form.submit({
                                url:'StandardValueServlet?method=updateValue',
                                submitEmptyText: false,
                                jsonSubmit :true,
                                waitMsg: 'Saving Data...'
                            });
                            this.up('window').hide();
                        }
                    }
                },{
                    text: 'Cancel',
                    handler: function() {
                        this.up('window').hide();
                    }
                }]
            }]//end items
           
        });
        this.callParent();
    },
    showWin:function(reportID,columnName){
       
        this.down('form').getForm().load({
            url:'StandardValueServlet?method=getStandardValue&ReportID='+reportID+'&ColumnName='+columnName
        });
        this.show();
    }
})
