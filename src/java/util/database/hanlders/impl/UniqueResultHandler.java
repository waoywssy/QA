/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.database.hanlders.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import util.database.hanlders.ResultHandler;

/**
 *
 * @author Administrator
 */
public class UniqueResultHandler<T> implements ResultHandler<T> {

    @Override
    public T handle(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return (T)resultSet.getObject(1);
        } else {
            return null;
        }
    }
}
