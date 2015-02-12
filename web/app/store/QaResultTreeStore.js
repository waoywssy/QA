Ext.define('qa.store.QaResultTreeStore', {
	extend : 'Ext.data.TreeStore',
	autoLoad : true,
	lazyFill : true,
	proxy : {
		type : 'ajax',
		reader : 'json',
		url : 'TreeNodeServlet?method=getQaResultNode'
	},
	root : {
		'nodeType' : '-12',
		'id' : '2',
		'name' : 'Root'
	},
	fields : [{
		name : 'name'
	}, {
		name : 'parentId'
	}, {
		name : 'id'
	}, {
		name : 'nodeType',
		type : 'int'
	}, {
		name : 'leaf',
		type : 'boolean'
	}, {
		name : 'refer'
	}, {
		name : 'iconCls'
	}]

}); 