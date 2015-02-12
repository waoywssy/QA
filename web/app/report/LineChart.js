/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('qa.report.LineChart', {
    extend: 'Ext.container.Container',
    alias: 'widget.linechart',
    overflowX: 'auto',
    margin: '10 0',
    height:400,
    layout:'fit',
    initComponent: function() {
        Ext.apply(this, {
            data: this.data,
            listeners: {
                afterrender: {
                    fn: function(ths, opts) {
                        ths.renderLine();
                    }
                }
            }
        });//end Ext.apply
        this.callParent();
    },//end initComponent
    renderLine:function(){
        var category=this.data.Category;
        var lineColumns=this.data.LineColumns;
        var storeData=this.data.StoreData;

        var series1 =[];
        var storeFields=[];
        for(var i=0;i<lineColumns.length;i++){
            var y = lineColumns[i];
            storeFields.push(lineColumns[i]);
            series1.push({
                type: 'line',
                axis: 'left',
                xField: category,
                yField: y,
                tips: {
                    trackMouse: true,
                    width: 170,
                    height: 45,
                    renderer: function(storeItem, item) {
                        this.setTitle(storeItem.get(item.series.xField));
                        this.update(storeItem.get(item.series.yField));
                    }
                }
            });
        };
        
        storeFields.push(category);
        var config= {
            xtype:'chart',
            overflowX: 'auto',
            store:  Ext.create('Ext.data.JsonStore',{
                fields:storeFields,
                data:storeData
            }),
            legend: {
                position: 'right'
            },
            axes: [{
                type: 'Numeric',
                minimum: 0,
                position: 'left',
                fields: lineColumns,
                grid: true
            }, {
                type: 'Category',
                position: 'bottom',
                fields: category,
                title:this.data.reportName,
                label: {
                    rotate: {
                        degrees: 70
                    },
                    renderer: function(name) {
                        if(name&&name.length>10){
                            return name.substr(0, 10);
                        }
                        return name;
                      
                    }
                }
            }],
            series: series1
        }
        this.add(config);
    }//end renderLine
})
