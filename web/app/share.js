


var lastShowNoteID;
function showNote(ele, reportID) {
    if (lastShowNoteID == reportID) {
        return;
    } else {
        lastShowNoteID = reportID;
    }
    infoTip.getLoader( ).load({
        params: {
            ID: reportID
        }
    });
    infoTip.setTarget(Ext.get(ele));
    infoTip.show();
}

