Ext.define('qa.BotSchedulePanel', {
    extend : 'Ext.panel.Panel',
    xtype : 'botschedulepanel',
    bodyPadding : 5,
    acchor : '100%',
    title: 'Schedule',
    layout : {
        type : 'vbox',
        align : 'stretch' // Child items are stretched to full width
    },
    requires:['qa.store.ScheduleStore','qa.store.ScheduleStatusStore','qa.store.SectorStore'],
    initComponent : function() {
        Ext.apply(this, {
            loaded:false,
            closable:true,
            layout:'fit',
            items : [
            {
                id:'botGridPanel',
                layout:'fit',
                xtype:'gridpanel',
                store: Ext.create('qa.store.ScheduleStore'),
                multiSelect: true,
                viewConfig: {
                    stripeRows: true,
                    enableTextSelection: true
                },
                currentSearchIndex:-1,
                searchText:'',
                updateInterval:null,
                qaStatusStore:Ext.create('qa.store.ScheduleStatusStore'),
                qaStatusBtns:new Ext.util.HashMap(),
                columns: [{
                    xtype: 'rownumberer',
                    align:'center',
                    width: 30,
                    sortable: false
                },{
                    header: 'Run/Stop',
                    dataIndex:'QaStatus',
                    type:'int',
                    renderer: function (v, m, r) {
                        // v 是value，是当前Cell的值
                        // m 是model, 是当前的行模型对象
                        // r 是raw, 是当前Cell的原始数据
                        var id = Ext.id();
                        var text='Run';
                        if(v==2){
                            text='Waiting';
                        }else if(v==1){
                            // 正在QA
                            text='Stop';
                        }
                        
                        // 50 miliseconds 后执行方法
                        // defer( fn, millis, [scope], [args], [appendArgs] ) : Number
                        Ext.defer(function () {
                            // widget( [name], [config] ) : Object
                            // Convenient shorthand to create a widget by its xtype or a config object. 
                            var btn= Ext.widget ('button', {
                                renderTo: id,
                                text: text,
                                width: 75,
                                // button 的handler用于响应点击事件
                                handler: function () {
                                    if(this.getText()=='Run'){
                                        var urlStr ='BotScheduleServlet?method=startSchdeule&botID='+r.get('ID');
                                        this.setText('Waiting');
                                        Ext.Ajax.request({
                                            url: urlStr
                                        });
                                    }
                                }
                            });
                            this.qaStatusBtns.add(r.get('ID'),btn);
                        }, 50, this);
                        return Ext.String.format('<div id="{0}"></div>', id);
                    }
                },
                {
                    header: 'BotName', 
                    dataIndex: 'BotName',
                    width:200
                },
                {
                    header: 'Sector', 
                    dataIndex: 'Sector'
                },
                {
                    header: 'Checked', 
                    dataIndex: 'Checked',
                    renderer:function(v, m, r){
                        //console.log(r);
                        if (v){
                            return Ext.String.format('<span style="background-color:red;">'+v+'</span>');    
                        }
                        return v;
                    }
                },
                {
                    header: 'QaDate', 
                    dataIndex: 'LastQaDate'
                },
                {
                    header: 'JobIDs', 
                    dataIndex: 'JobIDs'
                },
                {
                    header: 'Disabled', 
                    dataIndex: 'Disabled',
                    renderer:function(v, m, r){
                        if (v){
                            /*
                            var record = qaStatusStore.getNodeById(id),
                            rowIndex = qaStatusStore.indexOf(record);
                            console.log(record);
                            */
                            //this.view.addRowCls(index, 'hide');
                            //m.classes.put('hide');
                        }
                        return v;
                    }
                }
                ],
                tbar : [{
                    xtype: 'textfield',
                    name: 'searchField',
                    emptyText:'bot name',
                    hideLabel: true,
                    width: 200
                }, {
                    xtype: 'button',
                    text: '&lt;',
                    tooltip: 'Find Previous Row',
                    handler: function(){
                        this.ownerCt.ownerCt.findText(2); 
                    }
                },{
                    xtype: 'button',
                    text: '&gt;',
                    tooltip: 'Find Next Row',
                    handler:   function(){
                        this.ownerCt.ownerCt.findText(1); 
                    }
                },{
                    xtype: 'button',
                    text: 'Search',
                    handler: function(){
                        var value=this.ownerCt.child('textfield').getValue();
                        with(this.ownerCt.ownerCt){
                            searchText=value;
                            currentSearchIndex=-1;
                            findText(1);
                            }
                    }
                }, {
                    xtype: 'button',
                    text: 'Hide/Show disabled',
                    handler: function(){
                        //console.log('隐藏所有的');
                    }
                }, '->' ,
                {
                    xtype: 'button',
                    text: 'Add',
                    scope:this,
                    handler: function(){
                        this.showAddBotWindow().getForm().reset();
                    }
                },{
                    text: 'Edit',
                    scope:this,
                    handler: function() {
                        var grid = this.down('gridpanel');
                        if(grid.getSelectionModel().hasSelection()){
                            var model = grid.getSelectionModel().getLastSelected();
                            var formPanel= this.showAddBotWindow();
                            var form = formPanel.getForm();
                            form.findField('BotID').setValue(model.get('ID'));
                            form.findField('BotName').setValue(model.get('BotName'));
                            form.findField('JobIDs').setValue(model.get('JobIDs'));
                            form.findField('Sector').setValue(model.get('Sector'));
                            form.findField('Disable').setValue(model.get('Disabled'));
                        } 
                    }
                },
                {
                    xtype: 'button',
                    text: 'Reload',
                    handler: function(){
                        this.ownerCt.ownerCt.store.reload();
                    }
                }],
                findText:function(direction){
                    //1,down;2,up
                    var searchText=this.searchText;
                    if(searchText.length==0){
                        return;
                    }
                    var findFlag=false;
                    var view=this.getView();
                    var regex = new RegExp(searchText,'i');
                    var record ;
                    if(direction==2){
                        for( i=this.currentSearchIndex-1;i>=0;i--){
                            record = view.getRecord(view.getNode(i));
                            if(regex.test(record.data.BotName)){
                                this.currentSearchIndex=i;
                                findFlag=true;
                                break;
                            }
                        }
                    }else{
                        for( i=this.currentSearchIndex+1;i<this.store.getCount();i++){
                            record = view.getRecord(view.getNode(i));
                            if(regex.test(record.data.BotName)){
                                this.currentSearchIndex=i;
                                findFlag=true;
                                break;
                            }
                        }
                    }
                    if(!findFlag&&direction==1){
                        Ext.example.msg('','Reach end!');
                    }else if(!findFlag&&direction==2){
                        Ext.example.msg('','Reach top!');
                    }else{
                        view.getSelectionModel().select(this.currentSearchIndex);
                    }
                },//end findText() 
                listeners:{
                    activate:{
                        fn:function(){
                            this.updateInterval = setInterval(function(grid){
                                grid.qaStatusStore.ownerCmp = grid;
                                grid.qaStatusStore.load();
                            },10000,this);
                        }
                    },
                    deactivate:{
                        fn:function(){
                            clearInterval(this.updateInterval);
                        }
                    }
                }
            }    
            ], //end initComponent items
            listeners:{
                activate:{
                    fn:function( panel, eOpts){
                        panel.child('gridpanel').fireEvent('activate');
                    }
                },
                deactivate:{
                    fn:function( panel, eOpts){
                        panel.child('gridpanel').fireEvent('deactivate');
                    }
                },
                beforedestroy:{
                    fn:function( panel, eOpts){
                        panel.child('gridpanel').fireEvent('deactivate');
                    }
                }
            },//end listeners
            showAddBotWindow:function(){
                if(!this.addBotPanel){
                    var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';
                    this.addBotPanel =  Ext.widget('window',{
                        title:'Add Bot',
                        layout:'fit',
                        closeAction:'hide',
                        width:300,
                        height:230,
                        items:[
                        {
                            xtype: 'form',
                            url: 'BotScheduleServlet',
                            bodyPadding: '5 5 0',
                            fieldDefaults: {
                                msgTarget: 'side',
                                labelWidth: 75
                            },
                            defaultType: 'textfield',
                            items: [{
                                xtype: 'hiddenfield',
                                name: 'method',
                                value:'update'
                            },{
                                xtype: 'hiddenfield',
                                name: 'BotID'
                            },{
                                fieldLabel: 'BotName',
                                afterLabelTextTpl: required,
                                name: 'BotName',
                                allowBlank: false
                            },{
                                fieldLabel: 'JobIDs',
                                afterLabelTextTpl: required,
                                name: 'JobIDs',
                                allowBlank: false
                            },  {
                                fieldLabel: 'Sector',
                                afterLabelTextTpl: required,
                                xtype:'combobox',
                                name:'Sector',
                                valueField:'name',
                                displayField:'name',
                                allowBlank: false,
                                store: Ext.create('qa.store.SectorStore')
                            },{
                                fieldLabel: 'Disabled',
                                xtype:'checkbox',
                                name: 'Disable',
                                checked :false
                            }],
                            buttons: [{
                                text: 'Save',
                                handler: function() {
                                    var form= this.up('form').getForm();
                                    if(form.isValid()){
                                        form.submit({
                                            success: function(form, action) {
                                                Ext.example.msg('', 'success');
                                                form.reset();
                                                form.owner.up('window').close();
                                            },
                                            failure: function(form, action) {
                                                Ext.Msg.alert('Failure', action.result.msg);
                                            }
                                        });
                                    }
                                }
                            },{
                                text: 'Cancel',
                                handler: function() {
                                    var formPanel= this.up('form');
                                    formPanel.getForm().reset();
                                    this.up('window').close();
                                }
                            }]
                        }//end add bot from
                        ]//end window items
                    });
                }
               
                this.addBotPanel.show();
                return this.addBotPanel.getComponent(0);
            }
        }
       
        );
        this.callParent();
    }
});
