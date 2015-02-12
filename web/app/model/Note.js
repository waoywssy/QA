Ext.define('qa.model.Note', {
    extend: 'Ext.data.Model',
    requires: [
    'Ext.data.reader.Json'
    ],
    fields: ['ID', 'NoteContent']
});