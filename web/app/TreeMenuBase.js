Ext.define('qa.TreeMenuBase', {
    extend : 'Ext.menu.Menu',
    xtype : 'treemenubase',
    width : 120,
    height : 200,
    margin : '0 0 10 0',
    floating : true, // usually you want this set to True (default)

    deleteNode : function() {
        var node = this.record.data.id;
        Ext.MessageBox.confirm('Warn', 'Do you want to delete?', function(btn, text) {
            if ('yes' == btn) {
                Ext.Ajax.request({
                    scope : this,
                    url : 'TreeNodeServlet',
                    params : {
                        method : 'delete',
                        node : node
                    },
                    success : function(response) {
                        Ext.example.msg('', response.responseText);
                        var treeStore = this.treeView.getTreeStore();
                        treeStore.load({
                            node : treeStore.getNodeById(this.record.data.parentId)
                        });
                    }
                });
            }
        }, this);
    }, //end deleteNode
    refresh : function(item) {
        if(this.record.data.nodeType==15){
            var tabPanel =  window.viewport.getComponent('centerTabPanel');
            var tab = tabPanel.getComponent(this.record.data.id);
            if(tab){
                tabPanel.setActiveTab(tab);
                tab.getLoader().load();
            }
        }else{
            var treeStore = this.treeView.getTreeStore();
            treeStore.load({
                node : treeStore.getNodeById(this.record.data.id)
            });
        }
    },
    generateScript:function(item){
        var server=this.down('combo').getValue();
        var curNode = this.treeView.getTreeStore().getNodeById(this.record.data.id);
        var database = null;
        if(curNode.parentNode.data.nodeType==5){
            database= curNode.parentNode.data.name;
        }else{
            database= curNode.parentNode.parentNode.data.name;
        }
           
        var table = curNode.data.name.trim();
        var command = item.text;
        var isSync = command.indexOf('Sync')==0
        var textArea = null;
        var myMask =null;

        if(isSync){
            myMask = new Ext.LoadMask(window.viewport.getComponent('west-panel'), {
                msg:"Please wait..."
            });
        }else{
            if(command=="SP Scripts"||command=="Table Scripts"){
                var text = window.prompt("Pattern", "%%");
                if(text==null||text==""){
                    return;
                }
                table = text;
            }
            var tabPanel =  window.viewport.getComponent('centerTabPanel');
            var curTab= tabPanel.getActiveTab();
            if(curTab&&curTab.isEditor){
                           
            }else{
                curTab= tabPanel.add(Ext.create('qa.QueryEditor'));
                tabPanel.setActiveTab(curTab);
            }
            textArea = curTab.down('textarea');         
            myMask = new Ext.LoadMask(textArea, {
                msg:"Please wait..."
            });
        }
        
        myMask.show();
            
        Ext.Ajax.request({
            url : "GenerateScriptServlet",
            timeout:1000*60*5,
            params : {
                'server':server,
                'database':database,
                'table':table,
                'command':command,
                'node':this.record.data.id
            },
            scope : this,
            success : function(response) {   
                if(isSync){
                    Ext.example.msg('',response.responseText);
                }else{
                    textArea.setValue(textArea.getValue()+"\n\n"+response.responseText);
                }
                myMask.hide();
            }
        });
    }
})