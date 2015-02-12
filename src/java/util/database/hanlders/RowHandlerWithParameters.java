package util.database.hanlders;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract interface RowHandlerWithParameters<ROW> extends RowHandler<ROW>
{
  public abstract ROW handle(ResultSet paramResultSet, ROW paramROW, Object[] paramArrayOfObject)
    throws SQLException;
}
