package util.database.hanlders;

import java.sql.CallableStatement;
import java.sql.SQLException;

public abstract interface CallHandlerWithParameters<CAL>
{
  public abstract CAL handleCall(CallableStatement paramCallableStatement, Object[] paramArrayOfObject)
    throws SQLException;
}
