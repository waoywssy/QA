package util.database;

import java.util.Iterator;

public abstract interface ResultIterator<T> extends Iterator<T>
{
  public abstract void close();
}