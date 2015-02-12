package util.database.hanlders;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract interface RowHandler<ROW>
{
  public abstract ROW handle(ResultSet paramResultSet, ROW paramROW)
    throws SQLException;
}
