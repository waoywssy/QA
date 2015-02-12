package util.database.hanlders;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract interface ParameterHandler
{
  public abstract void handleParameters(PreparedStatement paramPreparedStatement, Object[] paramArrayOfObject)
    throws SQLException;
}
