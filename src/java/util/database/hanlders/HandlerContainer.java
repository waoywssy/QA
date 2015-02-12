package util.database.hanlders;


public class HandlerContainer<RES, ROW, CAL>
{
  private final ResultHandler<? extends RES> resultHandler_;
  private final RowHandler<? extends ROW> rowHandler_;
  private final CallHandlerWithParameters<? extends CAL> callHandlerWithParameters_;
  private final ParameterHandler parameterHandler_;

  private HandlerContainer(ResultHandler<? extends RES> resultHandler, RowHandler<? extends ROW> rowHandler, CallHandlerWithParameters<? extends CAL> callHandlerWithParameters, ParameterHandler parameterHandler)
  {
    this.resultHandler_ = resultHandler;
    this.rowHandler_ = rowHandler;
    this.callHandlerWithParameters_ = callHandlerWithParameters;
    this.parameterHandler_ = parameterHandler;
  }

  public HandlerContainer(ResultHandler<? extends RES> resultHandler)
  {
    this(resultHandler, null, null, null);
  }

  public HandlerContainer(RowHandler<? extends ROW> rowHandler)
  {
    this(null, rowHandler, null, null);
  }

  public HandlerContainer(CallHandlerWithParameters<? extends CAL> callHandlerWithParameters)
  {
    this(null, null, callHandlerWithParameters, null);
  }

  public HandlerContainer(ParameterHandler parameterHandler)
  {
    this(null, null, null, parameterHandler);
  }

  public HandlerContainer(ResultHandler<? extends RES> resultHandler, ParameterHandler parameterHandler)
  {
    this(resultHandler, null, null, parameterHandler);
  }

  public HandlerContainer(RowHandler<? extends ROW> rowHandler, ParameterHandler parameterHandler)
  {
    this(null, rowHandler, null, parameterHandler);
  }

  public HandlerContainer(CallHandlerWithParameters<? extends CAL> callHandlerWithParameters, ParameterHandler parameterHandler)
  {
    this(null, null, callHandlerWithParameters, parameterHandler);
  }

  public ResultHandler<? extends RES> getResultHandler()
  {
    return this.resultHandler_;
  }

  public RowHandler<? extends ROW> getRowHandler()
  {
    return this.rowHandler_;
  }

  public CallHandlerWithParameters<? extends CAL> getCallHandlerWithParameters()
  {
    return this.callHandlerWithParameters_;
  }

  public ParameterHandler getParameterHandler()
  {
    return this.parameterHandler_;
  }
}