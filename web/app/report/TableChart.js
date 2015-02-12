/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('qa.report.TableChart', {
    extend: 'Ext.container.Container',
    alias: 'widget.tablechart',
    overflowX: 'auto',
    margin: '10 0',
    initComponent: function() {
        Ext.apply(this, {
            data: this.data,
            loader: {
                loadMask: true,
                autoLoad: false,
                url: 'ViewReportServlet',
                ajaxOptions: {
                    timeout: 1000 * 60 * 5
                },
                params: {
                    method: 'view',
                    node: this.data.reportID
                },
                renderer: function(loader, response, active) {
                    var target = loader.getTarget();
                    var text = response.responseText;
                    target.data = Ext.decode(text, true);
                    target.renderTable();
                }
            }, //end loader
            listeners: {
                afterrender: {
                    fn: function(ths, opts) {
                        ths.renderTable();
                    }
                },
                click: {
                    element: 'el',
                    scope: this,
                    fn: function(event) {
                        var original = Ext.EventManager.getTarget(event);
                        var nodeName = original.nodeName.toLowerCase();
                        var cls = original.getAttribute('class');

                        if (cls == 'exportselectedcolumn') {
                            this.exportSelectCloumns(original, this.data.reportID);
                        } else if (cls == 'openAutoQaScript') {
                            this.openAutoQaScript();
                        } else if (cls == 'openReportSetting') {
                            this.openReportSetting();
                        } else if (cls == 'setStandarValues') {
                            this.setStandarValues();
                        } else if (cls == 'reload') {
                            this.getLoader().load();
                        } else if (cls == 'viewNote') {
                            window.notepad.showPad(this.data.reportID);
                        } else if ('th' == nodeName) {
                            Ext.fly(original).toggleCls('highlight');
                        } else {
                        }
                    }
                }, //end click
                contextmenu: {
                    element: 'el',
                    scope: this,
                    fn: function(event) {
                        var original = Ext.EventManager.getTarget(event);
                        var nodeName = original.nodeName.toLowerCase();
                        if ('th' != nodeName && 'td' != nodeName) {
                            return;
                        }
                        if ('th' == nodeName) {
                            if (this.data.reportID > 0) {
                                this.openHeadLinkChart(original.cellIndex, original);
                            }
                        } else if (this.data.reportID > 0) {
                            this.rowIndex = original.parentNode.rowIndex-1;
                            this.colIndex = original.cellIndex;
                            this.menu.showAt(Ext.EventManager.getPageX(event), Ext.EventManager.getPageY(event));
                        }

                        Ext.EventManager.stopPropagation(event);
                        Ext.EventManager.preventDefault(event);
                        return false;
                    }
                }, //contextmenu
                dblclick: {
                    element: 'el',
                    scope: this,
                    fn: function(event) {
                        var original = Ext.EventManager.getTarget(event);
                        var nodeName = original.nodeName.toLowerCase();

                        if ('th' == nodeName) {
                            standardValueSetting.showWin(this.data.reportID, this.data.headers[original.cellIndex].name);
                        } else if ('td' == nodeName) {
                            Ext.fly(original.parentNode).toggleCls('highlight')
                        }
                    }
                }
            }
        });

        this.menu = Ext.create('Ext.menu.Menu', {
            width: 120,
            height: 80,
            reportPanel: this,
            margin: '0 0 10 0',
            floating: true, // usually you want this set to True (default)
            items: [{
                text: 'Show Values',
                scope: this,
                handler: this.drilldown
            }, {
                text: 'Show Rows',
                scope: this.drilldown,
                handler: null
            }, {
                text: 'Show QA',
                scope: this,
                handler: this.showRow
            }]//end items
        });
        this.callParent();
    }, //end initComponent
    renderTable: function() {
        var json = this.data;
        var tHeader, tBody;
        var tTitle, table, caption;
        var reportID = json["reportID"];
        if (reportID > 0) {
            tTitle = "<a class='openReportSetting' href='javascript:void(0)'>" + json["reportName"] + "</a>";
        } else {
            tTitle = json["reportName"];
        }
        var isRunTable = 0;
        var hasError = false;
        if (json["reportName"] == 'Maj_Runs'){
            isRunTable = 1;
        }
        caption = '';
        if (reportID > 0) {
            caption += "<img src='icons/application_go.png' class='exportselectedcolumn' alt='export selected column'>";
        }
        if (json["referQueryID"] > 0) {
            caption += "<img src='icons/connect.gif' class='openAutoQaScript' alt='export selected column'>";
        }
        caption += "<img src='icons/cog.png' class='setStandarValues' alt='Stardard value setting'>";
        caption += "<img src='icons/reload.png' class='reload' alt='reload'>";
        caption += "<img src='icons/information.png' class='viewNote' alt='Stardard value setting' onmouseover='showNote(this," + reportID + ")'>";
        caption += "</caption>";

        //table header
        tHeader = "<tr>";
        for (var i = 0; i < json["headers"].length; i++) {
            if (!json["headers"][i]["show"]) {
                tHeader += "<th style='display:none;'></th>";
            } else {
                var title = "" + json["headers"][i]["min"] + "~" + json["headers"][i]["max"] + ";" + json["headers"][i]["minp"] + "~" + json["headers"][i]["maxp"];
                tHeader += "<th title='" + title + "'>" + json["headers"][i]["name"] + "</th>";
            }
        }
        tHeader += "</tr>";
        
        //table content
        tBody = '';
        var startRow = json.startRow;
        for (var i = 0; json["values"] && i < json["values"].length; i++) {
            var row = json["values"][i];
            tBody += "<tr>";
            var total = json["totalColumn"] == -1 ? null : row[json["totalColumn"]];
            for (var j = 0; j < json["headers"].length; j++) {
                if (isRunTable && json["headers"][j]['name'] == 'Success'){
                    console.log(row[j]);
                }
                
                if (!json["headers"][j]["show"]) {
                    tBody += "<td style='display:none;'></td>";
                } else if (startRow == 1//从第一行开始分�?
                    || (startRow == 0 && i == 0)//只分析第一�?
                    || (startRow == 2 && i != 0)) //从第二行开始分�?
                {
                    var err = this.validateValue(row[j], total, json["headers"][j]);
                    if (!hasError && err == " class='error'"){
                        hasError = true;
                    }
                    tBody += "<td" + err + ">" + row[j] + "</td>";
                } else {
                    tBody += "<td>" + row[j] + "</td>";
                }
            }
            tBody += "</tr>";
        }
        if (json["error"] && json["error"].length > 0) {
            tBody += "<tr><td>" + json["error"] + "</tr></td>";
        }
        if (hasError){
            tTitle = "<span class='error'>" + tTitle + "</span>";    
        }
        
        table = "<table class='qaResult'><caption>" + tTitle + caption + tHeader + tBody + "</table>";
        this.update(table);
    }, //end renderTable
    error: " class='error'",
    validateValue: function(value, total, validator) {
        if (value == null) {
            value = 0;
        }

        if (validator["min"] && value < validator["min"]) {
            return this.error;
        }

        if (validator["max"] && value > validator["max"]) {
            return this.error;
        }

        if (total != null && total != 0) {
            if (value > 0 && validator["minp"] && value * 100 / total < validator["minp"]) {
                return this.error;
            }

            if (validator["maxp"] && value / total * 100 > validator["maxp"]) {
                return this.error;
            }
        }
        return "";
    }, //end validateValue
    showRow: function() {
        var rowIndex = this.rowIndex;
        var table = "<table class='qaResult'>";
        var row = this.data["values"][rowIndex];
        var headers = this.data["headers"].concat();
        var total = this.data["totalColumn"] == -1 ? -1 : row[this.data["totalColumn"]];
        headers.sort(function compare(a, b) {
            if (a.originalColumn > b.originalColumn) {
                return 1;
            } else if (a.originalColumn < b.originalColumn) { 
                return -1;
            } 
            return 0;
        });
        table += "<tr style='text-align:left'><th>Name</th><th>Value</th><th>Flu</th><th>Min</th><th>Max</th><th>MinP</th><th>MaxP</th></tr>";
        var original = "";
        var header;
        for (var j = 0; j < headers.length; j++) {
            header = headers[j];
            var value = total == -1 ? null : row[header.index] * 100 / total;
            if (original != header["originalColumn"]) {
                original = header["originalColumn"];
                table += "<tr><td colspan='7' style='text-align:center;padding-top:5px;'>" + original + "</td>";
            }
            table += "<tr>";
            table += "<td>" + header["name"] + "</td>";
            table += "<td" + this.validateValue(row[header.index], total, header) + ">" + row[header.index] + "</td>";
            table += "<td>" + (value == null || isNaN(value) ? value : value.toFixed(3)) + "</td>";
            table += "<td>" + header["min"] + "</td>";
            table += "<td>" + header["max"] + "</td>";
            table += "<td>" + header["minp"] * 100 + "</td>";
            table += "<td>" + header["maxp"] * 100 + "</td>";
            table += "</tr>";
        }
        table += "</table>";

        var win = Ext.create('Ext.window.Window', {
            width: 600,
            height: 500,
            layout: 'fit',
            autoShow: true,
            maximizable: true,
            constrainHeader: true,
            autoScroll: true,
            html: table,
            y: 50
        });
    }, //end show
    openHeadLinkChart: function(colIndex, th) {
        var linkColumn = this.data.headers[colIndex].name;
        console.log(linkColumn);
        var firstRowCells = th.parentNode.cells;
        for (var i = 0; i < firstRowCells.length; i++) {
            if (i == colIndex || firstRowCells[i].className != 'highlight') {
                continue;
            }
            linkColumn += ',' + this.data.headers[i].name;
        }
        Ext.create('Ext.window.Window', {
            title: linkColumn,
            width: 800,
            height: 500,
            autoShow: true,
            maximizable: true,
            constrainHeader: true,
            y: 50,
            layout: 'fit',
            items: [
            {
                xtype: 'reportpanel',
                layout: 'fit',
                node: this.data.reportID,
                linkColumn: linkColumn
            }
            ]
        });
    }, //end openHeadLinkChart
    drilldown: function() {
        var win = Ext.create('Ext.window.Window', {
            width: 800,
            height: 500,
            layout: 'fit',
            autoShow: true,
            maximizable: true,
            constrainHeader: true,
            autoScroll: true,
            y: 50
        });

        var myMask = new Ext.LoadMask(win, {
            autoShow: true,
            msg: "Please wait..."
        });
        var postdata = {};
        try {
            var selectedColumn = this.data.headers[this.colIndex].name;
            postdata["SelectedColumn"] = selectedColumn;
            postdata["ReferQueryID"] = this.data.referQueryID;
            var row = this.data.values[this.rowIndex];
            for (var i = 0; i < this.data.groupColumns.length; i++) {
                var index = this.data.groupColumns[i];
                postdata[this.data.headers[index].name] = row[index];
            }
            postdata[selectedColumn] = row[this.colIndex];

        } catch (e) {
            Ext.example.msg("Error", e);
        }

        Ext.Ajax.request({
            url: "DrillDownServlet?method=drilldown&params=" + Ext.encode(postdata),
            scope: this,
            callback: function(options, success, response) {
                myMask.hide();
                win.update(response.responseText);
            }
        });
    }, //end drilldown
    exportSelectCloumns: function(element, reportID) {
        element = Ext.fly(element);
        var table = element.up("table");
        var selectedElments = table.select("th[class=highlight]");
        var selectedColumnsStr = reportID;

        for (var i = 0; i < selectedElments.getCount(); i++) {
            var dom = selectedElments.item(i).dom;
            selectedColumnsStr += "," + (dom.textContent || dom.innerText).trim();
        }
        Ext.Ajax.request({
            url: "ViewReportServlet?method=viewSelectedColumnReport&Data=" + selectedColumnsStr,
            success: function(response) {
                window.notepad.showContent(response.responseText);
            }
        });
    }, //end exportSelectCloumns
    openReportSetting: function() {
        var id = this.data.reportID;
        var name = this.data.reportName;
        var tabPanel = window.viewport.getComponent('centerTabPanel');
        var tab = tabPanel.getComponent(id + "_edit");
        if (tab) {
            tabPanel.setActiveTab(tab);
        } else {
            tab = tabPanel.add({
                xtype: 'reportdesigner',
                title: name,
                parentNode: '',
                node: id,
                itemId: id + "_edit"
            });
            tabPanel.setActiveTab(tab);
        }
    }, //end openReportSetting
    openAutoQaScript: function() {
        var queryID = this.data.referQueryID;
        var tabPanel = window.viewport.getComponent('centerTabPanel');
        var tab = tabPanel.getComponent(queryID);
        if (tab) {
        }
        else {
            //query
            tab = tabPanel.add(Ext.create('qa.QueryEditor', {
                title: 'Edit',
                node: queryID,
                itemId: queryID
            }));
            tab.loadData();
        }
        tabPanel.setActiveTab(tab);
    }, //end openAutoQaScript
    setStandarValues: function() {
        var reportID = this.data.reportID;
        if (!window.standValueEditor) {
            window.standValueEditor = Ext.create('Ext.window.Window', {
                x: 50,
                width: 600,
                height: 500,
                constrainHeader: true,
                layout: 'fit',
                closeAction: 'hide',
                items: [
                {
                    xtype: 'tablechartpalette'
                }
                ]
            });
        }

        var editor = window.standValueEditor.down('tablechartpalette');
        editor.node = reportID;
        editor.loadData(reportID, false);
        window.standValueEditor.show();

    }, //setStandarValues
    openLinkChart: function(reportID, params) {
        Ext.create('Ext.window.Window', {
            autoShow: true,
            width: 800,
            height: 500,
            layout: 'fit',
            maximizable: true,
            constrainHeader: true,
            y: 50,
            items: [
            {
                xtype: 'reportpanel',
                node: reportID,
                params: Ext.encode(params)
            }
            ]
        })
    }//openLinkChart
})
