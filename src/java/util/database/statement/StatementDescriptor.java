package util.database.statement;

import util.database.hanlders.HandlerContainer;
import util.database.hanlders.ResultHandler;
import util.database.hanlders.RowHandler;


public abstract interface StatementDescriptor
{
  public abstract String[] getColumnNames();

  public abstract String getProcessedSql();

  public abstract SqlStatementType getSqlStatementType();

  public abstract <ROW> RowHandler<ROW> getRowHandler(HandlerContainer<?, ? extends ROW, ?> paramHandlerContainer);

  public abstract RowHandler<?> getRowHandler();

  public abstract <RES> ResultHandler<RES> getResultHandler(HandlerContainer<? extends RES, ?, ?> paramHandlerContainer);

  public abstract String getMethodNameAndParameterTypesString();
}
