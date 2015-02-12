package util.database.hanlders.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import util.database.hanlders.RowHandler;

public class ListCallResultHandler<T> extends ListResultHandler<T>
{
  public ListCallResultHandler(RowHandler<T> singleRowFactory)
  {
    super(singleRowFactory);
  }

  public ListCallResultHandler(Class<T> beanClass)
  {
    super(beanClass);
  }

  public void close(ResultSet resultSet)
  {
    if (resultSet != null)
      try {
        resultSet.close();
      }
      catch (SQLException e)
      {
      }
  }
}