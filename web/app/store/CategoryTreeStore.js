Ext.define('qa.store.CategoryTreeStore', {
    extend: 'Ext.data.TreeStore',
    constructor: function (config) {
         this.fields[6].maxLastFound=config.maxLastFound;
        this.callSuper(arguments); 
       
    },
    proxy: {
        type: 'memory',
        reader: {
            type: 'json'
        }
    },
    fields: [{
        name: 'text',
        mapping:'name',
        convert:function(v, record){
            if(record.data.leaf){
                return v;
            }else{
                return v+' ('+record.data.count+')';
            }
        }
    }, {
        name: 'parentId'
    },{
        name: 'id'
    },{
        name: 'level'
    },{
        name: 'leaf',
        type:'boolean'
    },{
        name:'count',
        type:'int'
    },{
        name:'iconCls',
        mapping:'lastfound',
        convert :function(v,record){
            if (v <this.maxLastFound){
                return 'no';
            }else if(v!=this.maxLastFound){
                var i=0;
            }
        }
    }]
});