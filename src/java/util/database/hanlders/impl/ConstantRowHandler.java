package util.database.hanlders.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import util.database.hanlders.RowHandler;

public class ConstantRowHandler<T>
  implements RowHandler<T>
{
  protected Class cls_;
  private int VALUE_NOT_SET = -100;
  private int resultType_ = this.VALUE_NOT_SET;

  public ConstantRowHandler(Class cls)
  {
    this.cls_ = cls;
  }

  public ConstantRowHandler(Class cls, int resultType)
  {
    this(cls);
    this.resultType_ = resultType;
  }

  public T handle(ResultSet resultSet, T object)
    throws SQLException
  {
    if (this.resultType_ != this.VALUE_NOT_SET) {
      return createUsingRegisteredType(resultSet);
    }
    return checkTypeAndCreate(resultSet);
  }

  private T createUsingRegisteredType(ResultSet resultSet)
    throws SQLException
  {
    int columnIndex = 1;
    Object returnValue;
    switch (this.resultType_) {
    case 5:
      returnValue = Short.valueOf(resultSet.getShort(1));
      if (resultSet.wasNull()) returnValue = null; break;
    case 4:
      returnValue = Integer.valueOf(resultSet.getInt(1));
      if (resultSet.wasNull()) returnValue = null; break;
    case -5:
      returnValue = Long.valueOf(resultSet.getLong(1));
      break;
    case 8:
      returnValue = Double.valueOf(resultSet.getDouble(1));
      if (resultSet.wasNull()) returnValue = null; break;
    case 6:
      returnValue = Float.valueOf(resultSet.getFloat(1));
      if (resultSet.wasNull()) returnValue = null; break;
    case 3:
      returnValue = resultSet.getBigDecimal(1);
      break;
    case 91:
      returnValue = resultSet.getDate(1);
      break;
    case 92:
      returnValue = resultSet.getTime(1);
      break;
    case 93:
      returnValue = resultSet.getTimestamp(1);
      break;
    case 12:
      returnValue = resultSet.getString(1);
      break;
    case -3:
    case -2:
      returnValue = resultSet.getBytes(1);
      break;
    case 2004:
      returnValue = resultSet.getBlob(1);
      break;
    case 2005:
      returnValue = resultSet.getClob(1);
      break;
    default:
        returnValue=null;
    }

    return (T)returnValue;
  }

  private T checkTypeAndCreate(ResultSet resultSet)
    throws SQLException
  {
    int columnIndex = 1;
    Object returnValue = null;
    if (String.class.equals(this.cls_)) {
      returnValue = resultSet.getString(1);
    }
    else if (Boolean.class.equals(this.cls_)) {
      returnValue = Boolean.valueOf(resultSet.getBoolean(1));
      if (resultSet.wasNull()) returnValue = null;
    }
    else if (Boolean.TYPE.equals(this.cls_)) {
      returnValue = Boolean.valueOf(resultSet.getBoolean(1));
    }
    else if (Byte.class.equals(this.cls_)) {
      returnValue = Byte.valueOf(resultSet.getByte(1));
      if (resultSet.wasNull()) returnValue = null;
    }
    else if (Byte.TYPE.equals(this.cls_)) {
      returnValue = Byte.valueOf(resultSet.getByte(1));
    }
    else if (Short.class.equals(this.cls_)) {
      returnValue = Short.valueOf(resultSet.getShort(1));
      if (resultSet.wasNull()) returnValue = null;
    }
    else if (Short.TYPE.equals(this.cls_)) {
      returnValue = Short.valueOf(resultSet.getShort(1));
    }
    else if (Integer.class.equals(this.cls_)) {
      returnValue = Integer.valueOf(resultSet.getInt(1));
      if (resultSet.wasNull()) returnValue = null;
    }
    else if (Integer.TYPE.equals(this.cls_)) {
      returnValue = Integer.valueOf(resultSet.getInt(1));
    }
    else if (Long.class.equals(this.cls_)) {
      returnValue = Long.valueOf(resultSet.getLong(1));
      if (resultSet.wasNull()) returnValue = null;
    }
    else if (Long.TYPE.equals(this.cls_)) {
      returnValue = Long.valueOf(resultSet.getLong(1));
    }
    else if (Float.class.equals(this.cls_)) {
      returnValue = Float.valueOf(resultSet.getFloat(1));
      if (resultSet.wasNull()) returnValue = null;
    }
    else if (Float.TYPE.equals(this.cls_)) {
      returnValue = Float.valueOf(resultSet.getFloat(1));
    }
    else if (Double.class.equals(this.cls_)) {
      returnValue = Double.valueOf(resultSet.getDouble(1));
      if (resultSet.wasNull()) returnValue = null;
    }
    else if (Double.TYPE.equals(this.cls_)) {
      returnValue = Double.valueOf(resultSet.getDouble(1));
    }

    return (T)returnValue;
  }
}