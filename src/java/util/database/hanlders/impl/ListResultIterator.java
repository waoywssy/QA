package util.database.hanlders.impl;

import java.util.List;
import util.database.ResultIterator;

public class ListResultIterator<T>
  implements ResultIterator<T>
{
  List<T> underlyingList_;
  int row_;

  public ListResultIterator(List<T> aList)
  {
    this.underlyingList_ = aList;
  }

  public boolean hasNext() {
    if (this.row_ < this.underlyingList_.size() - 1) {
      return true;
    }
    return false;
  }

  public T next()
  {
    return this.underlyingList_.get(this.row_++);
  }

  public void remove()
  {
    //throw ExceptionFactory.createDataRuntimeExceptionForRuntimeOnly(null, Messages.getText("ERR_METHOD_NOT_SUP", new Object[] { "remove()" }), null, 11223);
  }

  public void close()
  {
  }
}