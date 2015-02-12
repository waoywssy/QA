package util.database.hanlders;

import java.sql.CallableStatement;
import java.sql.SQLException;

public abstract interface CallHandler<T>
{
  public abstract T handleCall(CallableStatement paramCallableStatement)
    throws SQLException;
}
