package util.database.hanlders;

import java.sql.ResultSet;
import java.sql.SQLException;

public  interface ResultHandler<RES>
{
    /**
     *
     * @param resultSet
     * @return
     */
    public RES handle(ResultSet resultSet) throws SQLException;
}
