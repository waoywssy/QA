package util.database.hanlders.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import util.database.hanlders.ResultHandler;
import util.database.hanlders.RowHandler;

public class ListResultHandler<T>
        implements ResultHandler<List<T>> {

    RowHandler<T> singleRowFactory_;
    Class<T> beanClass_;

    private ListResultHandler() {
    }

    public ListResultHandler(RowHandler<T> singleRowFactory) {
        this.singleRowFactory_ = singleRowFactory;
    }

    public ListResultHandler(Class<T> beanClass) {
        this.beanClass_ = beanClass;
    }

    @Override
    public List<T> handle(ResultSet resultSet) {
        List results = new ArrayList();
        try {
            while (resultSet.next()) {
                results.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
        } finally {
            close(resultSet);
        }
        return results;
    }

    public void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                Statement stmt = null;
                try {
                    stmt = resultSet.getStatement();
                } catch (Exception e) {
                }
                resultSet.close();
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
            }
        }
    }
}