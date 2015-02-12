/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('qa.report.PivotChart', {
    extend: 'Ext.container.Container',
    alias: 'widget.pivotchart',
    overflowX: 'auto',
    margin: '10 0',
    initComponent: function() {
        Ext.apply(this, {
            data: this.data,
            listeners: {
                afterrender: {
                    fn: function(ths, opts) {
                        ths.renderPivot();
                    }
                }
            }
        });//end Ext.apply
        this.callParent();
    }, //end initComponent
    renderPivot: function() {
        var json = this.data;
        var yAxisIndex = json.yAxis;
        var xAxisIndex = json.xAxis;
        var countIndex = json.CountColumn;
        var yAxis = new Ext.util.MixedCollection();
        var xAxis = new Ext.util.MixedCollection();
        var reportName = json.reportName,
        json = json.values;
        for (var i = 0; i < json.length; i++) {
            yAxis.add(json[i][yAxisIndex], 1);
            xAxis.add(json[i][xAxisIndex], 1);
        }
        ;

        // xAxis.sortByKey();
        // yAxis.sortByKey();

        var dh = Ext.DomHelper;
        var spec = {
            tag: 'table',
            cls: 'qaResult',
            cn: [
                {
                    tag: 'caption',
                    html: reportName
                }
            ]
        };
        var header = {
            tag: 'tr',
            cn: [{
                    tag: 'th',
                    html: yAxisIndex
                }]
        };
        xAxis.eachKey(function(key, item, index) {
            xAxis.replace(key, index + 1);
            header.cn.push({
                tag: 'th',
                html: key
            });
        });

        spec.cn.push(header);

        yAxis.eachKey(function(key, item, index) {
            yAxis.replace(key, index + 1);
            var tr = {
                tag: 'tr',
                cn: []
            };
            tr.cn.push({
                tag: 'td',
                html: key
            });
            for (var i = 0; i < xAxis.getCount(); i++) {
                tr.cn.push({
                    tag: 'td'
                });
            }
            spec.cn.push(tr);
        });

        var table = dh.append(this.getEl(), spec);
        //console.log(table);
        for (var i = 0; i < json.length; i++) {
            var colIndex = xAxis.get(json[i][xAxisIndex]);
            var rowIndex = yAxis.get(json[i][yAxisIndex]);
            var value = json[i][countIndex];
            table.rows[rowIndex].cells[colIndex].innerHTML = value;
        }
    }//end renderTree
})
