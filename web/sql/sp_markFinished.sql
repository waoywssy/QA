USE [Qa_DataStatistic1]
GO
/****** Object:  StoredProcedure [dbo].[sp_markFinish]    Script Date: 08/16/2013 09:21:29 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROC [dbo].[sp_markFinish]
(
	@tableName varchar(200),
	@jobIds varchar(50),
	@qadate datetime,
	@column_data varchar(50),
	@column_runs varchar(50),
	@convertType int
)
AS
BEGIN
	DECLARE @convert VARCHAR(30);
	
	IF(@convert IS NULL OR @convert = 0)
		SET @convert = ':param';
	ELSE IF(@convert=1)
		SET @convert = 'DATEADD(DAY,0,DATEDIFF(DAY,0,:param))';
	ELSE IF(@convert=2)
		SET @convert = 'DATEADD(MI,DATEDIFF(MI,0,:param),0)';
	ELSE IF(@convert=3)
		SET @convert = 'CAST(CONVERT(varchar(25), :param, 120) AS DATETIME)';
			
	DECLARE @smt nvarchar(1000);
	SET @smt = 'UPDATE ' +@tableName + ' SET Finished = 0 '+'
	'+'WHERE ' +@column_data 
	+' IN (SELECT '+ replace(@convert,':param',@column_runs) +' FROM dbo.Maj_Runs WHERE QaDate = @param1'+' AND JobID IN('+@jobIds+'))'
	+' 
	AND QaDate <> @param1';
		
	exec sp_executesql @stmt =@smt,@params=N'@param1 datetime',@param1 = @qadate
	
			
	SET @smt = 'INSERT '+@tableName+'(QaDate,'+@column_data+',Finished)
		SELECT A.QaDate,'+replace(@convert,':param','A.'+@column_runs)+',1
		FROM dbo.Maj_Runs AS A
		LEFT JOIN '+@tableName+' AS B
		ON A.QaDate=B.QaDate AND A.'+@column_data+' = '+replace(@convert,':param','B.'+@column_data)
		+' WHERE A.QaDate = @param1 AND A.JobID IN('+@jobIds+') AND B.RunDate IS NULL'
		--print(@smt);
	exec sp_executesql @stmt =@smt,@params=N'@param1 datetime',@param1 = @qadate
	
END

  /*
EXEC [dbo].[sp_markFinish]
		@tableName = N'Maj_Runs',
		@jobIds = N'21090',
		@qadate = N'2013-02-04 08:21:12.163',
		@column_data = N'RunID',
		@column_runs = N'RunID',
		@convert = null
		--@convert =N'CAST(CONVERT(VARCHAR(26),:param,120) AS DATETIME)'
*/
GO
CREATE PROC [dbo].[sp_getMaj_Runs]
(
	@BotID INT
)
AS
BEGIN
	SET NOCOUNT ON;
	DECLARE @JobIDs VARCHAR(50);

	SELECT @JobIDs =JobIDs FROM mj_auto_qa.dbo.Bots WHERE ID = @BotID;

	EXEC(
		'SELECT TOP 7 QaDate,RunDate ,RunID,JobID,DateFinished ,Success,Interval=CASE WHEN DateFinished IS NULL THEN  DATEDIFF(MI,RunDate,QaDate)/60.0 - 12 ELSE DATEDIFF(MI,RunDate,DateFinished)/60.0 END FROM dbo.Maj_Runs WHERE Finished = 1 AND JobID IN ('+@JobIDs+') ORDER BY RunID DESC'
	)
END