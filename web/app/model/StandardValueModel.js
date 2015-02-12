Ext.define('qa.model.StandardValueModel', {
    extend : 'Ext.data.Model',
    fields : [
    {
        name:'ID',
        type:'int'
    },
    {
        name:'ReportID',
        type:'int'
    },{
        name : 'ColumnName',
        type : 'string'
    }, {
        name : 'MinValue',
        type : 'float',
        convert :function(v,record){
            if(v==null||v.length==0){
                return null;
            }else{
                return v;
            }
        }
    }, {
        name : 'MaxValue',
        type : 'float',
        convert :function(v,record){
            if(v==null||v.length==0){
                return null;
            }else{
                return v;
            }
        }
    }, {
        name : 'AvgValue',
        type : 'float',
        convert :function(v,record){
            if(v==null||v.length==0){
                return null;
            }else{
                return v;
            }
        }
    }, {
        name : 'Fluctuation',
        type : 'int',
        convert :function(v,record){
            if(v==null||v.length==0){
                return null;
            }else{
                return v;
            }
        }
    }, {
        name : 'Show',
        type : 'bool'
    }, {
        name : 'ColOrder',
        type : 'int'
    }]
});