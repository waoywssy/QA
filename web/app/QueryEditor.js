Ext.define('qa.QueryEditor', {
    extend : 'Ext.form.Panel',
    xtype : 'queryeditor',
    title : 'Query Editor',
    bodyPadding : 5,
    acchor : '100%',
    isEditor : true,
    closable : true,
    layout : {
        type : 'vbox',
        align : 'stretch' // Child items are stretched to full width
    },
    requires : ['qa.store.ServerStore', 'qa.store.DatabaseStore', 'qa.model.Query'],
    initComponent : function() {
        Ext.apply(this, {
            reader : new Ext.data.reader.Json({
                model : 'qa.model.Query'
            }),
            items : [{
                xtype:'panel',
                title:'Query Text',
                layout:'fit',
                margin:'0 0 10 0',
                collapsible:true,
                items:[
                {
                    xtype : 'textarea',
                    name : 'queryText',
                    border:false,
                    height : 300
                }
                ]
            }
            , {
                layout : 'hbox',
                border : false,
                pading : '100 5 3 10',
                items : [{
                    xtype : 'hiddenfield',
                    name : 'node',
                    value : this.node
                }, {
                    xtype : 'hiddenfield',
                    name : 'parentNode',
                    value : this.parentNode
                }, {
                    xtype:'checkbox',
                    name:'changeServer',
                    checked:true
                },{
                    xtype : 'combobox',
                    fieldLabel : 'Server',
                    labelWidth : 40,
                    width : 150,
                    name : 'server',
                    margin : '0 0 0 8',
                    displayField : 'name',
                    valueField : 'name',
                    value : '127.0.0.1',
                    store : Ext.create('qa.store.ServerStore')
                }, {
                    xtype : 'combobox',
                    fieldLabel : 'Database',
                    margin : '0 0 0 8',
                    labelWidth : 60,
                    width : 180,
                    name : 'database',
                    displayField : 'name',
                    valueField : 'name',
                    value : 'RetailListing',
                    store : Ext.create('qa.store.DatabaseStore')
                }, {
                    xtype : 'textfield',
                    name : 'name',
                    value : 'new query',
                    labelWidth : 40,
                    margin : '0 0 0 8',
                    fieldLabel : 'Name'
                }, {
                    xtype : 'toolbar',
                    margin : '0 0 0 8',
                    border : false,
                    padding : 0,
                    flex : 1,
                    items : [{
                        text : 'Select',
                        iconCls : 'bmenu',
                        menu : {
                            items : [{
                                text : 'Select top',
                                menu : {
                                    items : [{
                                        text : 'Sequence',
                                        scope : this,
                                        handler : function() {
                                            var text = '\nSELECT TOP 100 * \nFROM TableName\nORDER BY RunDate DESC';
                                            this.insertQueryText(text);
                                        }
                                    }, {
                                        text : 'Max value',
                                        scope : this,
                                        handler : function() {
                                            var text = '\nSELECT TOP 100 * \nFROM TableName\nWHERE RunDate = (SELECT MAX(RunDate) FROM TableName)\nORDER BY RunDate DESC';
                                            this.insertQueryText(text);
                                        }
                                    }]
                                }
                            }, {
                                text : 'Select count',
                                scope : this,
                                handler : function() {
                                    var text = '\nSELECT TOP 5 RunDate, COUNT(*) AS TotalItem,COUNT(DISTINCT ) AS TotalDif \nFROM TableName\nWHERE  RunDate= \nGROUP BY RunDate\nORDER BY RunDate DESC';
                                    this.insertQueryText(text);
                                }
                            }, {
                                text : 'Select top events',
                                scope : this,
                                handler : function() {
                                    var text = '\nSELECT TOP 100 JobId, RunId, EventTime, SeverityId, Subject, Details \nFROM EventTable\n' + 'WHERE RunID = (SELECT MAX(RunID) FROM EventTable)\nORDER BY EventID DESC'
                                    this.insertQueryText(text);
                                }
                            }, {
                                text : 'Select events statistic',
                                scope : this,
                                handler : function() {
                                    var text = '\nSELECT TOP 80 RunId, JobID, Subject, COUNT(*) AS TotalItem,SeverityId FROM EventTable WITH(NOLOCK)\n' + 'WHERE EventTime > DATEADD(Week,-2,GETDATE()) AND RunId :Operation :RunID AND JobID = 21106\n' + 'GROUP BY JobID,RunId,Subject,SeverityId ORDER BY RunId DESC,SeverityId DESC'
                                    this.insertQueryText(text);
                                }
                            }, {
                                text : 'path CTE',
                                scope : this,
                                tooltip : 'generate category path use static table or history table',
                                handler : function() {
                                    this.insertQueryText(window.globalFinalVariables.get('pathCTE'));
                                }
                            }, {
                                text : 'Delete',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n--DELETE FROM TABLE WHERE ');
                                }
                            }, {
                                text : 'Truncate',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n--TRUNCATE TABLE ');
                                }
                            }]
                        }
                    }, {
                        text : 'Functions',
                        menu : {
                            items : [{
                                text : 'String',
                                menu : {
                                    items : [{
                                        text : 'ASCII',
                                        tooltip : 'Returns the ASCII code value of the leftmost character of a character expression',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' ASCII(character) ');
                                        }
                                    }, {
                                        text : 'CHAR',
                                        tooltip : 'Converts an int ASCII code to a character',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' CHAR(integer) ');
                                        }
                                    }, {
                                        text : 'CHARINDEX',
                                        tooltip : 'Searches expression2 for expression1 and returns its starting position if found. The search starts at start_location.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' CHARINDEX( expression1 ,expression2 [ , start_location ] ) ');
                                        }
                                    }, {
                                        text : 'DIFFERENCE',
                                        tooltip : 'Returns an integer value that indicates the difference between the SOUNDEX values of two character expressions.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' DIFFERENCE( character_expression , character_expression ) ');
                                        }
                                    }, {
                                        text : 'LEFT',
                                        tooltip : 'Returns the left part of a character string with the specified number of characters.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' LEFT( character_expression , integer_expression ) ');
                                        }
                                    }, {
                                        text : 'LEN',
                                        tooltip : 'Returns the number of characters of the specified string expression, excluding trailing blanks.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' LEN() ');
                                        }
                                    }, {
                                        text : 'LOWER',
                                        tooltip : 'Returns a character expression after converting uppercase character data to lowercase.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' LOWER() ');
                                        }
                                    }, {
                                        text : 'LTRIM',
                                        tooltip : 'Returns a character expression after it removes leading blanks.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' LTRIM() ');
                                        }
                                    }, {
                                        text : 'NCHAR',
                                        tooltip : 'Returns the Unicode character with the specified integer code, as defined by the Unicode standard.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' NCHAR(integer_expression) ');
                                        }
                                    }, {
                                        text : 'PATINDEX',
                                        tooltip : 'Returns the starting position of the first occurrence of a pattern in a specified expression, or zeros if the pattern is not found, on all valid text and character data types.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' PATINDEX( \'%pattern%\' , expression ) ');
                                        }
                                    }, {
                                        text : 'QUOTENAME',
                                        tooltip : 'Returns a Unicode string with the delimiters added to make the input string a valid Microsoft SQL Server delimited identifier.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' QUOTENAME( \'character_string\' [ , \'quote_character\' ]) ');
                                        }
                                    }, {
                                        text : 'REPLACE',
                                        tooltip : 'Replaces all occurrences of a specified string value with another string value.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' REPLACE(input , pattern , replacement) ');
                                        }
                                    }, {
                                        text : 'REPLICATE',
                                        tooltip : 'Repeats a string value a specified number of times.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' REPLICATE( string_expression ,integer_expression ) ');
                                        }
                                    }, {
                                        text : 'REVERSE',
                                        tooltip : 'Returns the reverse of a character expression.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' REVERSE(character_expression) ');
                                        }
                                    }, {
                                        text : 'RIGHT',
                                        tooltip : 'Returns the right part of a character string with the specified number of characters.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' RIGHT(character_expression , integer_expression) ');
                                        }
                                    }, {
                                        text : 'RTRIM',
                                        tooltip : 'Returns a character string after truncating all trailing blanks.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' RTRIM() ');
                                        }
                                    }, {
                                        text : 'SOUNDEX',
                                        tooltip : 'Returns a four-character (SOUNDEX) code to evaluate the similarity of two strings.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' SOUNDEX(character_expression) ');
                                        }
                                    }, {
                                        text : 'SPACE',
                                        tooltip : 'Returns a string of repeated spaces.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' SPACE(integer_expression) ');
                                        }
                                    }, {
                                        text : 'STR',
                                        tooltip : 'Returns character data converted from numeric data.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' STR(float_expression[ , length [ , decimal ] ]) ');
                                        }
                                    }, {
                                        text : 'STUFF',
                                        tooltip : 'The STUFF function inserts a string into another string. It deletes a specified length of characters in the first string at the start position and then inserts the second string into the first string at the start position.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' STUFF( character_expression , start , length ,character_expression) ');
                                        }
                                    }, {
                                        text : 'SUBSTRING',
                                        tooltip : 'Returns part of a character, binary, text, or image expression.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' SUBSTRING( value_expression ,start_expression , length_expression) ');
                                        }
                                    }, {
                                        text : 'UNICODE',
                                        tooltip : 'Returns the integer value, as defined by the Unicode standard, for the first character of the input expression.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' UNICODE(\'ncharacter_expression\') ');
                                        }
                                    }, {
                                        text : 'UPPER',
                                        tooltip : 'Returns a character expression with lowercase character data converted to uppercase.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' UPPER () ');
                                        }
                                    }]
                                }
                            }, {
                                text : 'Date',
                                menu : {
                                    items : [{
                                        text : 'getdate',
                                        tooltip : 'Returns a datetime2(7) value that contains the date and time of the computer on which the instance of SQL Server is running.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' GETDATE() ');
                                        }
                                    }, {
                                        text : 'back 7 days',
                                        tooltip : 'dateadd(day,-7,getdate()).',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' DATEADD(DAY,-7,GETDATE()) ');
                                        }
                                    }, {
                                        text : 'dateadd',
                                        tooltip : 'Returns a new datetime value by adding an interval to the specified datepart of the specified date.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' DATEADD(datepart , number , date) ');
                                        }
                                    }, {
                                        text : 'datediff',
                                        tooltip : 'Returns the number of date or time datepart boundaries that are crossed between two specified dates.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' DATEDIFF(datepart, startdate, enddate) ');
                                        }
                                    }, {
                                        text : 'datepart',
                                        tooltip : 'Returns an integer that represents the specified datepart of the specified date.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' DATEPART(datepart, date) ');
                                        }
                                    }, {
                                        text : 'datename',
                                        tooltip : 'Returns a character string that represents the specified datepart of the specified date.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' DATENAME(datepart, date) ');
                                        }
                                    }, {
                                        text : 'day',
                                        tooltip : 'Returns an integer that represents the day day part of the specified date.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' DAY(date) ');
                                        }
                                    }, {
                                        text : 'month',
                                        tooltip : 'Returns an integer that represents the month part of a specified date.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' MONTH(date) ');
                                        }
                                    }, {
                                        text : 'year',
                                        tooltip : 'Returns an integer that represents the year part of a specified date.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' YEAR(date) ');
                                        }
                                    }, {
                                        text : 'isdate',
                                        tooltip : 'Determines whether a datetime or smalldatetime input expression is a valid date or time value.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' ISDATE(expression) ');
                                        }
                                    }, {
                                        text : 'Convert U.S.',
                                        tooltip : 'mm/dd/yyyy',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' CONVERT(varchar(25), date, 101) ');
                                        }
                                    }, {
                                        text : 'Convert USA',
                                        tooltip : 'mm-dd-yy',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' CONVERT(varchar(25), date, 110) ');
                                        }
                                    }, {
                                        text : 'Convert ODBC',
                                        tooltip : 'yyyy-mm-dd hh:mi:ss(24h)',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' CONVERT(varchar(25), date, 120) ');
                                        }
                                    }, {
                                        text : 'Convert ODBC mills',
                                        tooltip : 'yyyy-mm-dd hh:mi:ss.mmm(24h)',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' CONVERT(varchar(25), date, 121) ');
                                        }
                                    }]
                                }
                            }, {
                                text : 'Aggregate',
                                menu : {
                                    items : [{
                                        text : 'AVG ',
                                        tooltip : 'Returns the average of the values in a group. Null values are ignored.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' AVG([ALL|DISTINCT] expression ) ');
                                        }
                                    }, {
                                        text : 'CHECKSUM_AGG',
                                        tooltip : 'Returns the checksum of the values in a group. Null values are ignored.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' CHECKSUM_AGG( [ ALL | DISTINCT ] expression) ');
                                        }
                                    }, {
                                        text : 'COUNT',
                                        tooltip : 'Returns the number of items in a group.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' COUNT({ [ [ ALL | DISTINCT ] expression ] | * }) ');
                                        }
                                    }, {
                                        text : 'COUNT_BIG',
                                        tooltip : 'Returns(bigint) the number of items in a group.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' COUNT_BIG( { [ ALL | DISTINCT ] expression } | *) ');
                                        }
                                    }, {
                                        text : 'GROUPING',
                                        tooltip : 'Indicates whether a specified column expression in a GROUP BY list is aggregated or not.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' GROUPING( <column_expression>) ');
                                        }
                                    }, {
                                        text : 'MAX',
                                        tooltip : 'Returns the maximum value in the expression.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' MAX( [ ALL | DISTINCT ] expression) ');
                                        }
                                    }, {
                                        text : 'MIN',
                                        tooltip : 'Returns the minimum value in the expression.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' MIN( [ ALL | DISTINCT ] expression) ');
                                        }
                                    }, {
                                        text : 'SUM',
                                        tooltip : 'Returns the sum of all the values, or only the DISTINCT values, in the expression.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' SUM( [ ALL | DISTINCT ] expression) ');
                                        }
                                    }, {
                                        text : 'STDEV',
                                        tooltip : 'Returns the statistical standard deviation of all values in the specified expression.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' STDEV( [ ALL | DISTINCT ] expression) ');
                                        }
                                    }, {
                                        text : 'STDEVP',
                                        tooltip : 'Returns the statistical standard deviation for the population for all values in the specified expression.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' STDEVP( [ ALL | DISTINCT ] expression) ');
                                        }
                                    }, {
                                        text : 'VAR',
                                        tooltip : 'Returns the statistical variance of all values in the specified expression.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' VAR( [ ALL | DISTINCT ] expression) ');
                                        }
                                    }, {
                                        text : 'VARP',
                                        tooltip : 'Returns the statistical variance for the population for all values in the specified expression.',
                                        scope : this,
                                        handler : function() {
                                            this.insertQueryText(' VARP ( [ ALL | DISTINCT ] expression) ');
                                        }
                                    }]
                                }
                            }]
                        }
                    }, {
                        text : 'Quick',
                        menu : {
                            items : [{
                                text : 'Min',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,MIN(Column1) AS Min');
                                }
                            }, {
                                text : 'Max',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,MAX(Column1) AS Max');
                                }
                            }, {
                                text : 'Avg',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,AVG(Column1) AS Avg');
                                }
                            }, {
                                text : 'Sum',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,SUM(Column1) AS Sum');
                                }
                            }, {
                                text : 'Total',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,COUNT(Column1) AS Total');
                                }
                            }, {
                                text : 'TotalDif',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,COUNT(DISTINCT Column1) AS TotalDif');
                                }
                            }, {
                                text : 'TotalNull',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,COUNT(CASE WHEN Column1 IS NULL THEN 1 ELSE NULL END) AS TotalNull');
                                }
                            }, {
                                text : 'TotalNotEmpty',
                                tooltip : 'Count(Len(x)>0)',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,COUNT(CASE WHEN LEN(Column1)>0 THEN 1 ELSE NULL END) AS TotalNotEmpty');
                                }
                            }, {
                                text : 'TotalEmpty',
                                tooltip : 'x is null or Len(x)=0',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,COUNT(CASE WHEN Column1 IS NULL OR LEN(Column1)>0 THEN NULL ELSE 1 END) AS TotalEmpty');
                                }
                            }, {
                                text : 'MinLen',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,MIN(LEN(Column1)) AS MinLen');
                                }
                            }, {
                                text : 'MaxLen',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,MAX(LEN(Column1)) AS MaxLen');
                                }
                            }, {
                                text : 'Total A>B',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,COUNT(CASE WHEN Column1>Column2 THEN 1 ELSE NULL END) AS TotalBigger');
                                }
                            }, {
                                text : 'TotalAllNull',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,COUNT(CASE WHEN Column1 IS NULL AND Column2 IS NULL THEN 1 ELSE NULL END) AS TotalAllNull');
                                }
                            }, {
                                text : 'TotalBetween',
                                scope : this,
                                handler : function() {
                                    this.insertQueryText('\n,COUNT(CASE WHEN Column1 BETWEEN a AND b THEN 1 ELSE NULL END) AS TotalBetween');
                                }
                            }]//end Quick
                        }
                    }, {
                        text : 'Execute',
                        margin : '0 0 0 20',
                        tooltip : 'F5',
                        scope : this,
                        handler : function() {
                            this.executeQuery();
                        }
                    }, {
                        text : 'Save',
                        scope : this,
                        tooltip : 'Ctrl+S',
                        margin : '0 0 0 8',
                        handler : function() {
                            this.saveQuery();
                        }
                    }, {
                        text : 'Reload',
                        scope : this,
                        margin : '0 0 0 8',
                        handler : function() {
                            this.loadData();
                        }
                    }]//end toolbar items
                }//end toolbar
                ]//end form items
            }, //end form
            {
                itemId : 'queryResultHeaderPanel',
                xtype : 'panel',
                style : 'z-index:5;overflow:hidden;',
                html : '&nbsp;',
                margin : '5 17 0 1',
                border : false
            }, {
                itemId : 'queryResult',
                xtype : 'panel',
                margin : '-22 0 0 0',
                autoScroll : true,
                flex : 1,
                listeners : {
                    afterrender : {
                        fn : function(panel, eOpts) {
                            panel.body.addListener('scroll', function(e, t, eOpts) {
                                this.previousNode().body.dom.scrollLeft = e.target.scrollLeft;
                            }, this);
                        }
                    }
                },
                fixTableHeader : function() {
                    var queryResultHeaderPanel = this.previousNode();
                    var table = this.body.down('table', true);
                    if (!table) {
                        queryResultHeaderPanel.update('<table><tr><td>&nbsp;<td></tr></table>');
                        this.update("<br/>" + this.body.getHTML());
                        return;
                    }
                    //                    var fn = function(e) {
                    //                        var ele = e.target.parentElement;
                    //                        if (ele.className == "highlight") {
                    //                            ele.className = "";
                    //                        } else {
                    //                            ele.className = "highlight";
                    //                        };
                    //                    }
                    //                    if (table.addEventListener) {
                    //                        table.addEventListener('dblclick', fn);
                    //                    } else {
                    //                        table.attachEvent('ondblclick', fn);
                    //                    }
                    var cells = table.rows[0].cells;
                    var html = "<table class='qaResult' width=" + table.rows[0].clientWidth + "px><tr>";
                    for (var i = 0; i < cells.length; i++) {
                        html += "<th><div style='width:" + (cells[i].clientWidth - 6) + "px' >" + cells[i].innerHTML + "</div></th>";
                    }
                    html += "</tr></table>";
                    queryResultHeaderPanel.update(html);
                }
            }]
        //end initComponent items
        });
        this.callParent();
    },
    insertQueryText : function(insertValue) {
        var dom = this.getForm().findField('queryText').inputEl.dom;
        dom.value = dom.value.substr(0, dom.selectionStart) + insertValue + dom.value.substr(dom.selectionStart);
    },
    getSelectQueryText : function() {
        var textArea = this.getForm().findField('queryText');
        var dom = textArea.inputEl.dom;
        if (dom.selectionStart == dom.selectionEnd) {
            return textArea.getValue();
        } else {
            return textArea.getValue().substring(dom.selectionStart, dom.selectionEnd);
        }
    },
    executeQuery : function() {
        var form = this.getForm();
        var postdata = form.getValues();
        postdata.method = "query";
        postdata.queryText = this.getSelectQueryText();
        if (this.myMask) {
            this.myMask.hide();
            this.myMask = null;
        }
        this.myMask = new Ext.LoadMask(this.getComponent('queryResult'), {
            msg : "Please wait..."
        });

        this.myMask.show();

        Ext.Ajax.request({
            url : "QueryEditorServlet",
            params : postdata,
            scope : this,
            timeout:1000*60*5,
            success : function(response) {
                this.myMask.hide();
                var queryResultPanel = this.getComponent('queryResult');
                queryResultPanel.update(response.responseText);
                queryResultPanel.fixTableHeader();
                this.myMask = null;
            },
            failure:function(){
                this.myMask.hide();
            }
        });
    },
    saveQuery : function() {
        var target = this;
        var form = this.getForm();
        var postdata = form.getValues();
        postdata.method = "update";
        if(postdata.node=="" &&postdata.parentNode==""){
            Ext.MessageBox.show({
                title: 'Warn',
                msg: 'Could not save, it belong to no node'
            });
            return;
        }
        Ext.Ajax.request({
            url : "QueryEditorServlet",
            params : postdata,
            success : function(response) {
                Ext.example.msg('', 'success');
                var json = Ext.decode(response.responseText);
                form.findField('node').setValue(json['msg']);
                target.setTitle(form.findField('name').getValue());
            }
        });
    },
    loadData : function() {
        if (this.node) {
            var form = this.getForm();
            form.load({
                url : 'QueryEditorServlet?method=get&node=' + this.node,
                waitMsg : 'Loading...',
                scope:this,
                success: function(form, action) {
                    this.setTitle(form.findField('name').getValue());
                }
            });
        }
    }
});
