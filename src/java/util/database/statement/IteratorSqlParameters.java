/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.database.statement;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public final class IteratorSqlParameters implements Iterator<HashMap<String, Object>> {

    private LinkedList<HashMap<String, Object>>  list = new LinkedList<HashMap<String, Object>>();

    public IteratorSqlParameters() {
       
    }

    public IteratorSqlParameters(ResultSet result) throws SQLException {
        HashMap<String, Object> hashMap = null;
        ResultSetMetaData meta = result.getMetaData();

        try {
            while (result.next()) {
                hashMap = new HashMap<String, Object>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    hashMap.put(meta.getColumnName(i), result.getObject(i));
                }
                Add(hashMap);
            }
        } catch (SQLException ex) {
            Logger.getLogger(IteratorSqlParameters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Add(HashMap<String, Object> parameters) {
        this.list.add(parameters);
    }

    public void Add(List<HashMap<String, Object>> parameters) {
        this.list.addAll(parameters);
    }

    @Override
    public boolean hasNext() {
        return list.size() > 0;
    }

    @Override
    public HashMap<String, Object> next() {
        return list.removeFirst();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
