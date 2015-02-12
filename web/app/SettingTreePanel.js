Ext.define('qa.SettingTreePanel', {
    extend: 'Ext.tree.Panel',
    xtype: 'settingtreepanel',
    title: 'Setting',
    iconCls: 'settings',
    useArrows: true,
    rootVisible: false,
    hideHeaders: true,
    requires: ['qa.store.SettingTreeStore', 'qa.FolderMenu',
        'qa.QueryEditor', 'qa.DefaultTreeMenu',
        'qa.DefaultTreeMenu', 'qa.report.ReportPanel'],
    initComponent: function() {

        this.reportMenu = Ext.create('qa.FolderMenu');
        this.tableMenu = Ext.create('qa.DBTableMenu');
        this.tablesMenu = Ext.create('qa.DBTablesMenu');
        this.spsMenu = Ext.create('qa.DBSPsMenu');
        this.spMenu = Ext.create('qa.DBSPMenu');
        this.defaultMenu = Ext.create('qa.DefaultTreeMenu');

        Ext.apply(this, {
            store: new qa.store.SettingTreeStore(),
            viewConfig: {
                style: {
                    overflowX: 'hidden'
                },
                plugins: {
                    ptype: 'treeviewdragdrop',
                    containerScroll: true,
                    ddGroup: 'treenodedrop_group',
                    enableDrop: false
                }
            },
            columns: [{
                    xtype: 'treecolumn', //this is so we know which column will show the tree
                    width: 500,
                    dataIndex: 'name'
                }],
            listeners: {
                beforeitemcontextmenu: function(ths, record, item, index, e, eOpts) {
                    var menu = null;
                    var nodeType = record.data.nodeType;
                    if (nodeType == 2 || nodeType == -13 || nodeType == -14 || nodeType == -15) {
                        menu = this.reportMenu;
                    } else if (nodeType == 8) {
                        menu = this.tableMenu;
                    } else if (nodeType == -8) {
                        menu = this.tablesMenu;
                    } else if (nodeType == -7) {
                        menu = this.spsMenu;
                    } else if (nodeType == 7) {
                        menu = this.spMenu;
                    } else {
                        menu = this.defaultMenu;
                    }
                    if (menu) {
                        menu.record = record;
                        menu.treeView = ths;
                        menu.showAt(e.getXY());
                    }
                    e.preventDefault();
                },
                itemclick: function(ths, record, item, index, e, eOpts) {
                    if (record.data.nodeType == 1) {
                        window.open('DownloadServlet?node=' + record.data.id);
                        return;
                    }
                    var tabPanel = window.viewport.getComponent('centerTabPanel');
                    var tab = tabPanel.getComponent(record.data.id);

                    if (tab) {

                    } else if (record.data.nodeType == -12) {
                        tab = tabPanel.add(Ext.create('qa.BotSchedulePanel', {
                            itemId: record.data.id
                        }));
                    } else if (record.data.nodeType == 13) {
                        tab = tabPanel.add(Ext.create('qa.QueryEditor', {
                            title: record.data.name,
                            node: record.data.id,
                            parentNode: record.data.parentId,
                            itemId: record.data.id
                        }));
                        tab.loadData();
                    } else if (record.data.nodeType == 17) {
                        var frame = new Ext.ux.IFrame({
                            xtype: 'iframepanel',
                            itemId: record.data.id,
                            title: record.data.name,
                            closable: true,
                            layout: 'fit',
                            loadMask: 'loading...',
                            border: false
                        });
                        Ext.defer(function() {
                            frame.load("TreeNodeServlet?method=viewBirtReport");
                        }, 500, this);
                        tab = tabPanel.add(frame);
                    } else if (record.data.nodeType == 16) {
                        tab = tabPanel.add(Ext.create('qa.ScheduledScriptPanel', {
                            itemId: record.data.id
                        }));
                        tab.loadData(record.data.refer);
                    } else if (record.data.nodeType == 15) {
                        //report
                        tab = tabPanel.add({
                            xtype: 'reportpanel',
                            itemId: record.data.id,
                            title: record.data.name,
                            closable: true,
                            node: record.data.id
                        });
                    } else if (record.data.nodeType == 3) {
                        //bot default report
                        tab = tabPanel.add({
                            xtype: 'reportpanel',
                            itemId: record.data.id,
                            title: record.data.name,
                            closable: true,
                            isDefault: true,
                            node: record.data.refer
                        });
                    }
                    tabPanel.setActiveTab(tab);
                }
            }
        });

        this.callParent();
    }
})