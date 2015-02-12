/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.database.hanlders.impl;

import com.sun.rowset.CachedRowSetImpl;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;
import util.database.hanlders.ResultHandler;

/**
 *
 * @author Administrator
 */
public class CachedRowSetResultHandler implements ResultHandler<CachedRowSet> {
    @Override
    public CachedRowSet handle(ResultSet resultSet)  throws SQLException{
        CachedRowSet cachedRowSet = new CachedRowSetImpl();
//        cachedRowSet.setFetchSize(1000);
//        cachedRowSet.setPageSize(1000);
        cachedRowSet.populate(resultSet);
        return cachedRowSet;
    }
}
