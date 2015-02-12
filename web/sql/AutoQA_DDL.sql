USE [mj_auto_qa]
GO
/****** Object:  Table [dbo].[ScheduledScripts]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ScheduledScripts]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[ScheduledScripts](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[BotID] [int] NULL,
	[QueryID] [int] NULL,
	[Name] [varchar](100) NULL,
	[JoinColumn] [varchar](100) NULL,
	[JoinRunsColumn] [varchar](100) NULL,
	[ConvertType] [int] NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Qa_Scripts]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Qa_Scripts]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[Qa_Scripts](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[BotID] [int] NOT NULL,
	[ScriptName] [varchar](200) NOT NULL,
	[Script] [ntext] NULL,
	[ScriptType] [int] NULL,
 CONSTRAINT [PK_Qa_ManualScripts] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Qa_ReportLinkChartParams]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Qa_ReportLinkChartParams]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[Qa_ReportLinkChartParams](
	[LinkID] [int] NOT NULL,
	[ParamName] [varchar](50) NOT NULL,
	[ParamValueLink] [varchar](50) NOT NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Qa_ReportLinkChart]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Qa_ReportLinkChart]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[Qa_ReportLinkChart](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[ReportID] [int] NOT NULL,
	[LinkColumn] [varchar](50) NOT NULL,
	[SubReportID] [int] NOT NULL,
	[SubReportName] [varchar](100) NULL,
 CONSTRAINT [PK_Qa_ReportLinkChart] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[Qa_ReportLinkChart]') AND name = N'IX_Qa_ReportLinkChart')
CREATE NONCLUSTERED INDEX [IX_Qa_ReportLinkChart] ON [dbo].[Qa_ReportLinkChart] 
(
	[ReportID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Qa_ReportHeadLineChart]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Qa_ReportHeadLineChart]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[Qa_ReportHeadLineChart](
	[LinkColumn] [varchar](100) NOT NULL,
	[Category] [varchar](100) NOT NULL,
	[TopRows] [int] NOT NULL,
	[LineColumns] [varchar](200) NOT NULL,
	[ReportID] [int] NOT NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Qa_ReportColumns]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Qa_ReportColumns]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[Qa_ReportColumns](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[ReportID] [int] NOT NULL,
	[ColumnName] [varchar](50) NOT NULL,
	[ColOrder] [smallint] NOT NULL,
	[Show] [bit] NULL,
	[MinValue] [float] NULL,
	[MaxValue] [float] NULL,
	[AvgValue] [float] NULL,
	[Fluctuation] [int] NULL,
 CONSTRAINT [PK_Qa_ReportColumns] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[Qa_ReportColumns]') AND name = N'IX_Qa_ReportColumns_ReportID')
CREATE NONCLUSTERED INDEX [IX_Qa_ReportColumns_ReportID] ON [dbo].[Qa_ReportColumns] 
(
	[ID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Qa_Report_Tree]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Qa_Report_Tree]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[Qa_Report_Tree](
	[CatID] [varchar](50) NOT NULL,
	[CatName] [varchar](100) NOT NULL,
	[CatParentID] [varchar](50) NOT NULL,
	[CatLevel] [varchar](50) NOT NULL,
	[LastFound] [varchar](50) NOT NULL,
	[LostPoint] [int] NOT NULL,
	[ReportID] [int] NOT NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Qa_Query_Params]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Qa_Query_Params]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[Qa_Query_Params](
	[ReportID] [int] NOT NULL,
	[Name] [varchar](50) NOT NULL,
	[DataType] [varchar](10) NOT NULL,
	[DefaultValue] [varchar](50) NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[Qa_Query_Params]') AND name = N'IX_Qa_Query_Params_ReportID')
CREATE NONCLUSTERED INDEX [IX_Qa_Query_Params_ReportID] ON [dbo].[Qa_Query_Params] 
(
	[ReportID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Qa_Queries]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Qa_Queries]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[Qa_Queries](
	[ID] [int] NOT NULL,
	[Name] [varchar](200) NOT NULL,
	[QueryText] [ntext] NULL,
	[QueryType] [int] NULL,
	[Server] [varchar](50) NOT NULL,
	[Database] [varchar](50) NOT NULL,
	[Comments] [varchar](1000) NULL,
	[StartRow] [int] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[Qa_Queries]') AND name = N'IX_Qa_Queries_ID')
CREATE CLUSTERED INDEX [IX_Qa_Queries_ID] ON [dbo].[Qa_Queries] 
(
	[ID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Qa_LineChart]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Qa_LineChart]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[Qa_LineChart](
	[Category] [varchar](100) NOT NULL,
	[LineColumns] [varchar](200) NOT NULL,
	[ReportID] [int] NOT NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Qa_Files]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Qa_Files]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[Qa_Files](
	[ID] [int] NOT NULL,
	[Name] [nvarchar](50) NULL,
	[Path] [nvarchar](200) NULL,
	[FileName] [nvarchar](100) NULL
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[Notes]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Notes]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[Notes](
	[ID] [int] NOT NULL,
	[NoteContent] [ntext] NULL,
 CONSTRAINT [PK_Notes] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[NodeTypes]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[NodeTypes]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[NodeTypes](
	[Id] [int] NOT NULL,
	[TypeName] [nvarchar](100) NOT NULL,
	[Cls] [varchar](50) NOT NULL,
 CONSTRAINT [PK_NodeTypes] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Nodes]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Nodes]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[Nodes](
	[id] [int] IDENTITY(10,1) NOT NULL,
	[parentId] [int] NULL,
	[name] [varchar](100) NULL,
	[nodeType] [int] NULL,
	[leaf] [bit] NULL,
	[show] [bit] NULL,
	[refer] [int] NULL,
 CONSTRAINT [PK_DB_Nodes] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[Nodes]') AND name = N'IX_DB_Nodes_parentId')
CREATE NONCLUSTERED INDEX [IX_DB_Nodes_parentId] ON [dbo].[Nodes] 
(
	[parentId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DB_Servers]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[DB_Servers]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[DB_Servers](
	[ID] [int] NOT NULL,
	[ServerName] [varchar](50) NULL,
	[ServerIP] [varchar](50) NOT NULL,
	[ServerPort] [varchar](50) NULL,
	[UserName] [varchar](50) NULL,
	[Psd] [varchar](50) NULL,
 CONSTRAINT [PK_DatabaseServers] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Bots]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Bots]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[Bots](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[BotName] [varchar](200) NOT NULL,
	[Database_ID] [int] NULL,
	[JobIDs] [varchar](50) NULL,
	[Sector] [varchar](50) NOT NULL,
	[LastQaDate] [datetime] NULL,
	[LastCheckDate] [datetime] NULL,
	[QaStatus] [int] NOT NULL,
	[RunDate] [datetime] NULL,
	[RunID] [int] NULL,
	[DateFinished] [datetime] NULL,
	[Success] [bit] NULL,
	[priority] [smallint] NOT NULL,
	[Disabled] [bit] NOT NULL,
	[Checked] [bit] NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  StoredProcedure [dbo].[sp_getTableMeta]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_getTableMeta]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

create proc [dbo].[sp_getTableMeta]
(
	@botName varchar(200),
	@dbName  varchar(200)
)
as
begin
SELECT TOP 500
 O.name AS TableName
,O.object_id AS Table_Id
,C.name AS ColumnName
,CASE WHEN IC.column_id IS NULL THEN '''' ELSE ''checked'' END AS HasIndex
,CASE WHEN PATINDEX(''%char%'',T.name) > 0 AND PATINDEX(''%n%'',T.name) = 1
		THEN T.name + ''(''+CAST(C.max_length/2 AS VARCHAR(4)) + '')'' 
	  WHEN PATINDEX(''%char%'',T.name) > 0 AND PATINDEX(''%n%'',T.name) <> 1
		THEN T.name + ''(''+CAST(C.max_length AS VARCHAR(4)) + '')''
	  WHEN T.name = ''decimal'' OR T.name =''numeric'' 
		THEN T.name + ''(''+CAST(C.precision AS VARCHAR(4)) + '','' + CAST(C.scale AS VARCHAR(4)) + '')'' 
	  ELSE T.name END AS DataType
,CASE WHEN C.is_nullable= 1 THEN ''Checked'' ELSE '''' END AS Nullable
,CASE WHEN C.is_identity = 1 THEN ''Checked'' ELSE '''' END AS IsIdentity
FROM RetailListing.sys.tables AS O
LEFT JOIN RetailListing.sys.columns AS C
ON O.object_id = C.object_id
LEFT JOIN RetailListing.sys.types AS T
ON C.user_type_id = T.user_type_id
LEFT JOIN RetailListing.sys.index_columns AS IC
ON O.object_id = IC.object_id AND C.column_id = IC.column_id
WHERE O.name like ''%!_''+@botName+''%'' ESCAPE ''!''
ORDER BY O.name, C.column_id ASC
end' 
END
GO
/****** Object:  Table [dbo].[temp]    Script Date: 12/31/2013 13:31:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[temp]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[temp](
	[id] [int] NULL,
	[name] [varchar](100) NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  StoredProcedure [dbo].[sp_getEntity]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_getEntity]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_getEntity]
(
	@dbName VARCHAR(200),
	@tableName VARCHAR(200)
)
AS
BEGIN

	SET NOCOUNT ON;
	DECLARE @table Table
	(
		ColumnName VARCHAR(100),
		DataType VARCHAR(100)
	);

	INSERT @table
	SELECT TOP 500
		C.name AS ColumnName
		,CASE WHEN T.name = ''datetime'' THEN ''SqlDateTime''
		WHEN T.name = ''int'' THEN ''SqlInt32''
		WHEN T.name = ''nvarchar'' THEN ''SqlString''
		WHEN T.name = ''varchar'' THEN ''SqlString''
		WHEN T.name = ''char'' THEN ''SqlString''
		WHEN T.name = ''nchar'' THEN ''SqlString''
		WHEN T.name = ''money'' THEN ''SqlMoney''
		WHEN T.name = ''bigint'' THEN ''SqlInt64''
		WHEN T.name = ''bit'' THEN ''SqlBoolean''
		WHEN T.name = ''decimal'' THEN ''SqlDecimal''
		WHEN T.name = ''float'' THEN ''SqlDecimal''
		WHEN T.name = ''image'' THEN ''SqlBytes''
		WHEN T.name = ''numeric'' THEN ''SqlDecimal''
		WHEN T.name = ''tinyint'' THEN ''SqlInt16''
	END AS DataType
	FROM RetailListing.sys.tables AS O
	LEFT JOIN RetailListing.sys.columns AS C
	ON O.object_id = C.object_id
	LEFT JOIN RetailListing.sys.types AS T
	ON C.user_type_id = T.user_type_id
	WHERE O.name = ''RetailListing_BotName_Categories''
	ORDER BY C.name ASC;


	DECLARE @ColumnName VARCHAR(100);
	DECLARE @DataType VARCHAR(100);
	DECLARE @Fields VARCHAR(MAX);
	DECLARE @Field VARCHAR(100);
	DECLARE @Properties VARCHAR(MAX);
	DECLARE @Property VARCHAR(200);
	DECLARE @TempStr VARCHAR(200);
	SET @Property =
	''
	public {0} {1}
	{
		get{return {2};}
		set{ {2} = value;}
	}
	'';
	SET @Fields = '''';
	SET @Properties = '''';
	WHILE(1=1)
	BEGIN
		IF NOT EXISTS(SELECT TOP 1 * FROM @Table )
			BREAK;
		SELECT TOP 1 @ColumnName = ColumnName,@DataType = DataType FROM @Table
		SET @Field = ''  _''+ LOWER(SUBSTRING(@ColumnName,1,1)) +SUBSTRING(@ColumnName,2,LEN(@ColumnName)-1);
		SET @Fields = @Fields + CHAR(10) +''private ''+@DataType + @Field + '';'';
		SET @TempStr = REPLACE(@Property,''{0}'',@DataType);
		SET @TempStr = REPLACE(@TempStr,''{1}'',@ColumnName)
		SET @TempStr = REPLACE(@TempStr,''{2}'',@Field)
		SET @Properties = @Properties + CHAR(10) + @TempStr;

		DELETE FROM @Table WHERE ColumnName = @ColumnName
	END

	SELECT @Fields AS Fields, @Properties AS Properties;



END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_markFinish]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_markFinish]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_markFinish]
(
	@tableName varchar(200),
	@jobIds varchar(50),
	@qadate datetime,
	@column_data varchar(50),
	@column_runs varchar(50),
	@convert varchar(50)
)
AS
BEGIN
	IF(@convert IS NULL OR @convert='''')
		SET @convert = '':param'';
		
	DECLARE @smt nvarchar(1000);
	SET @smt = ''UPDATE '' +@tableName + '' SET Finished = 0 ''+''
	''+''WHERE '' +@column_data 
	+'' IN (SELECT ''+ replace(@convert,'':param'',@column_runs) +'' FROM dbo.Maj_Runs WHERE QaDate = @param1''+'' AND JobID IN(''+@jobIds+''))''
	+'' 
	AND QaDate <> @param1'';
		
	exec sp_executesql @stmt =@smt,@params=N''@param1 datetime'',@param1 = @qadate
	
			
	SET @smt = ''INSERT ''+@tableName+''(QaDate,''+@column_data+'',Finished)
		SELECT A.QaDate,''+replace(@convert,'':param'',''A.''+@column_runs)+'',1
		FROM dbo.Maj_Runs AS A
		LEFT JOIN ''+@tableName+'' AS B
		ON A.QaDate=B.QaDate AND A.''+@column_data+'' = ''+replace(@convert,'':param'',''B.RunDate'')
		+'' WHERE A.QaDate = @param1 AND A.JobID IN(''+@jobIds+'') AND B.RunDate IS NULL''
		--print(@smt);
	exec sp_executesql @stmt =@smt,@params=N''@param1 datetime'',@param1 = @qadate
	
END

  /*
EXEC [dbo].[sp_markFinish]
		@tableName = N''ANF_Categories'',
		@jobIds = N''21090'',
		@qadate = N''2013-02-04 08:21:12.163'',
		@column_data = N''RunDate'',
		@column_runs = N''RunDate'',
		@convert = null
		--@convert =N''CAST(CONVERT(VARCHAR(26),:param,120) AS DATETIME)''
*/' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_getTreeReportConfig]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_getTreeReportConfig]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'/****** Script for SelectTopNRows command from SSMS  ******/
--SELECT *
--  FROM [dbo].[QA_Report_Tree]
  
CREATE PROC [dbo].[sp_getTreeReportConfig]
	@ReportID int
 AS
 BEGIN
	SET NOCOUNT ON;
	
	SELECT *
	FROM dbo.QA_Report_Tree 
	WHERE ReportID= @ReportID 
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_deleteNode]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_deleteNode]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_deleteNode]
(
	@node int
)
AS
BEGIN

	SET NOCOUNT ON;
	DECLARE @message varchar(200);
	DECLARE @nodeType int;
	DECLARE @refer int;
	SET @message=''success'';
	
	SELECT @nodeType = nodeType,@refer=refer FROM dbo.Nodes WHERE ID = @node;
	
	IF EXISTS(SELECT TOP 1 * FROM dbo.Nodes WHERE parentId = @node)
		BEGIN
			SET @message = ''Please delete children first.'';
		END
	ELSE 
		BEGIN
			DELETE FROM dbo.Nodes WHERE id = @node;
			IF(@nodeType= 13)--Query
				BEGIN
					DELETE FROM dbo.Qa_Queries WHERE ID = @node;
					DELETE FROM dbo.ScheduledScripts WHERE QueryID = @node;
				END
			IF(@nodeType= 15)--Report
				BEGIN
					DELETE FROM dbo.Qa_Queries WHERE ID = @node;
					DELETE FROM dbo.Qa_ReportColumns WHERE ReportID = @node;
					DELETE FROM dbo.Qa_Query_Params WHERE ReportID = @node;
					DELETE FROM dbo.Qa_ReportHeadLineChart WHERE ReportID = @node;
					DELETE FROM dbo.QA_Report_Tree WHERE ReportID = @node;
				END
		END
	SELECT @message AS msg;
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_deleteLinkChart]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_deleteLinkChart]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_deleteLinkChart]
	@ReportID int,
	@LinkColumn varchar(50)
AS
BEGIN
	DELETE FROM dbo.Qa_ReportLinkChartParams 
	WHERE LinkID IN(SELECT ID FROM dbo.Qa_ReportLinkChart WHERE ReportID = @ReportID AND LinkColumn = @LinkColumn)

	
	DELETE FROM dbo.Qa_ReportLinkChart WHERE ReportID = @ReportID AND LinkColumn = @LinkColumn;
END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_deleteBot]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_deleteBot]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_deleteBot]
(
	@BotID INT
)
AS
BEGIN

	SET NOCOUNT ON;
	DELETE FROM dbo.Bots WHERE ID = @BotID;
	DELETE FROM dbo.Qa_ReportColumns WHERE ReportID IN(SELECT ID FROM dbo.Qa_Scripts WHERE ScriptType = 3 AND BotID = @BotID);
	DELETE FROM dbo.Qa_Scripts WHERE BotID = @BotID;
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_addQaFiles]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_addQaFiles]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_addQaFiles]
(
	@parentNode int,
	@name nvarchar(50),
	@path nvarchar(200),
	@fileName nvarchar(100),
	@nodeType int
)
AS
BEGIN
	SET NOCOUNT ON;
	INSERT dbo.Nodes VALUES(@parentNode,@name,@nodeType,1,1,null);
	INSERT dbo.Qa_Files(ID,Name,[Path],[FileName]) VALUES(@@IDENTITY,@name,@path,@fileName);
	
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_addDatabase]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_addDatabase]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

CREATE PROC [dbo].[sp_addDatabase]
	@Database varchar(100)
AS
BEGIN
	DECLARE @ID INT;
	INSERT INTO dbo.Nodes VALUES(1,@Database,5,0,1,19)
	SET @ID = @@IDENTITY;
	INSERT INTO dbo.Nodes VALUES(@ID,''Tables'',-8,0,1,null)
	INSERT INTO dbo.Nodes VALUES(@ID,''StoredProcedures'',-7,0,1,null)
	INSERT INTO dbo.Nodes VALUES(@ID,''Queries'',2,0,1,null)
END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_addBookmark]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_addBookmark]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_addBookmark]
	@refer int,
	@nodeType int,
	@name varchar(100)
AS
BEGIN
	IF NOT EXISTS(SELECT * FROM dbo.Nodes WHERE parentId = 4 AND refer = @refer)
		insert into dbo.Nodes(parentId ,name ,nodeType,leaf,show ,refer) values(4,@name,@nodeType,1,1,@refer); 
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_updateScheduledScripts]    Script Date: 12/31/2013 13:31:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_updateScheduledScripts]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

CREATE PROC [dbo].[sp_updateScheduledScripts]
	 @ID int
	,@BotID int
	,@QueryID int
	,@Name varchar(100)
	,@JoinColumn varchar(100)
	,@JoinRunsColumn varchar(100)
	,@ConvertType int
AS
BEGIN
	SET NOCOUNT ON;
	IF(@ID IS NULL OR @ID = 0)
		BEGIN
			INSERT INTO dbo.ScheduledScripts
				   (BotID
				   ,QueryID
				   ,Name
				   ,JoinColumn
				   ,JoinRunsColumn
				   ,ConvertType)
			 VALUES
				   (@BotID 
					,@QueryID 
					,@Name
					,@JoinColumn
					,@JoinRunsColumn
					,@ConvertType)
			SELECT CAST(@@IDENTITY AS int);
		END
	ELSE
		BEGIN
			UPDATE dbo.ScheduledScripts
				SET Name = @Name,JoinColumn = @JoinColumn,JoinRunsColumn = @JoinRunsColumn,ConvertType = @ConvertType
				WHERE ID = @ID
			SELECT @ID;
		END
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_updateReportType]    Script Date: 12/31/2013 13:31:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_updateReportType]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_updateReportType]
	@node int,
	@type int
AS
BEGIN
	UPDATE dbo.Qa_Queries SET QueryType = @type WHERE ID = @node
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_updateReportLinkChart]    Script Date: 12/31/2013 13:31:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_updateReportLinkChart]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_updateReportLinkChart]
	@ReportID int,
	@SubReportID int,
	@SubReportName varchar(100),
	@LinkColumn varchar(50)
AS
BEGIN
	SET NOCOUNT ON;
	DECLARE @ID int;
	SELECT @ID=ID FROM dbo.Qa_ReportLinkChart WHERE ReportID = @ReportID AND LinkColumn = @LinkColumn
	
	IF (@ID IS NULL)
		BEGIN
			INSERT dbo.Qa_ReportLinkChart VALUES(@ReportID,@LinkColumn,@SubReportID,@SubReportName);
			SELECT @ID = @@IDENTITY;
		END
	ELSE
		BEGIN
			UPDATE dbo.Qa_ReportLinkChart 
			SET SubReportID = @SubReportID ,SubReportName = @SubReportName
			WHERE ID = @ID;
			
			DELETE FROM dbo.Qa_ReportLinkChartParams WHERE LinkID = @ID;
		END
		
	SELECT @ID;
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_updateReportHeadLineChart]    Script Date: 12/31/2013 13:31:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_updateReportHeadLineChart]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_updateReportHeadLineChart]
	 @LinkColumn varchar(100)
	,@Category varchar(100)
	,@TopRows int
	,@LineColumns varchar(200)
	,@ReportID int
AS
BEGIN
	IF(@LineColumns IS NULL OR LEN(@LineColumns)=0 OR @TopRows=0)
		BEGIN
			DELETE FROM [dbo].[Qa_ReportHeadLineChart] WHERE [ReportID]= @ReportID AND [LinkColumn] = @LinkColumn
		END
	ELSE IF EXISTS(SELECT * FROM [dbo].[Qa_ReportHeadLineChart] WHERE [ReportID]= @ReportID AND [LinkColumn] = @LinkColumn)
		BEGIN
			UPDATE [dbo].[Qa_ReportHeadLineChart] SET [Category] = @Category,[LineColumns] = @LineColumns,TopRows = @TopRows
				WHERE [ReportID]= @ReportID AND [LinkColumn] = @LinkColumn
		END
	ELSE
		BEGIN
			INSERT INTO [dbo].[Qa_ReportHeadLineChart]
					   ([LinkColumn]
					   ,[Category]
					   ,[TopRows]
					   ,[LineColumns]
					   ,[ReportID])
				 VALUES
					   (@LinkColumn
						,@Category
						,@TopRows
						,@LineColumns
						,@ReportID)
		END
END


' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_updateReportColumnOrder]    Script Date: 12/31/2013 13:31:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_updateReportColumnOrder]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_updateReportColumnOrder]
 @end INT,
 @id INT,
 @reportId INT
AS
BEGIN
	DECLARE @i INT;
	DECLARE @identity INT;
	DECLARE @order INT;

	SET NOCOUNT ON

	SET @i=1;
  
	DECLARE Cur CURSOR FOR
		SELECT TOP 1000  [ID],ColOrder  FROM [dbo].[Qa_ReportColumns] WHERE ReportID = @reportId AND ID<>@id ORDER BY ColOrder ASC
		FOR UPDATE OF ColOrder
		  
	OPEN Cur
		FETCH NEXT FROM Cur INTO @identity,@order
		WHILE (@@FETCH_STATUS =  0)
			BEGIN
				IF(@end=@i)
					SET @i=@i+1
					
				UPDATE dbo.Qa_ReportColumns SET ColOrder = @i WHERE CURRENT OF Cur
				SET @i=@i+1
				FETCH NEXT FROM Cur INTO @identity,@order
			END
	CLOSE Cur
	DEALLOCATE Cur

	UPDATE dbo.Qa_ReportColumns
		SET ColOrder = @end WHERE ID = @id;

END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_updateReportColumn]    Script Date: 12/31/2013 13:31:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_updateReportColumn]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

CREATE PROCEDURE [dbo].[sp_updateReportColumn]
(
	 @ID int=null
	,@ReportID int
	,@ColumnName varchar(50)
	,@ColOrder smallint
	,@Show bit=1
	,@MinValue float=null
	,@MaxValue float=null
	,@AvgValue float=null
	,@Fluctuation int=null
)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	IF(@ColumnName=''Finished'')
		RETURN;
	IF (@ID IS NOT NULL)
		BEGIN
			UPDATE [dbo].[Qa_ReportColumns]
			   SET [Show] = @Show
				  ,[MinValue] = @MinValue
				  ,[MaxValue] = @MaxValue
				  ,[AvgValue] = @AvgValue
				  ,[Fluctuation] = @Fluctuation
			WHERE ID =@ID
		END
	ELSE IF NOT EXISTS(SELECT * FROM [dbo].[Qa_ReportColumns] WHERE ReportID = @ReportID AND ColumnName = @ColumnName)
		BEGIN
			INSERT INTO [dbo].[Qa_ReportColumns]
				([ReportID]
				,[ColumnName]
				,[ColOrder]
				,[Show]
				,[MinValue]
				,[MaxValue]
				,[AvgValue]
				,[Fluctuation])
			VALUES
				(@ReportID
				,@ColumnName
				,@ColOrder
				,@Show
				,@MinValue
				,@MaxValue
				,@AvgValue
				,@Fluctuation );
		END
END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_updateReport_Tree]    Script Date: 12/31/2013 13:31:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_updateReport_Tree]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
CREATE PROC [dbo].[sp_updateReport_Tree]
(
	 @CatID varchar(50) 
	,@CatName varchar(100) 
	,@CatParentID varchar(50) 
	,@CatLevel varchar(50)  
	,@LastFound varchar(50) 
	,@LostPoint int 
	,@ReportID int 
)
AS 
BEGIN

	IF NOT EXISTS(SELECT * FROM [dbo].[QA_Report_Tree] WHERE [ReportID]=@reportId)
		BEGIN
				INSERT INTO [dbo].[QA_Report_Tree]
						   ([CatID]
						   ,[CatName]
						   ,[CatParentID]
						   ,[CatLevel]
						   ,[LastFound]
						   ,[LostPoint]
						   ,[ReportID])
					 VALUES
							(@CatID 
							,@CatName
							,@CatParentID
							,@CatLevel
							,@LastFound
							,@LostPoint
							,@ReportID )
		END
	ELSE
		BEGIN
			UPDATE [dbo].[QA_Report_Tree] SET [CatID] = @CatID,[CatName]=@CatName,[CatParentID] = @CatParentID,
				[CatLevel] = @CatLevel,[LastFound] = @LastFound,[LostPoint]=@LostPoint
				WHERE [reportId] = @ReportID;
		END
END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_updateQueryParam]    Script Date: 12/31/2013 13:31:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_updateQueryParam]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_updateQueryParam]
	@ReportID int,
	@Name varchar(50),
	@OldName varchar(50),
	@DataType varchar(10),
	@DefaultValue varchar(50),
	@Delete bit=0
AS
BEGIN
	IF(@ReportID=0)
		RETURN;
		
	IF(@Delete=1)
		DELETE FROM dbo.Qa_Query_Params WHERE ReportID = @ReportID AND Name = @Name;
	ELSE IF NOT EXISTS(SELECT * FROM dbo.Qa_Query_Params WHERE ReportID = @ReportID AND Name = @OldName)
		INSERT INTO dbo.Qa_Query_Params VALUES(@ReportID,@Name,@DataType,@DefaultValue)
	ELSE 
		BEGIN
			UPDATE dbo.Qa_Query_Params
				SET DataType=@DataType,DefaultValue = @DefaultValue,Name = @Name
				WHERE ReportID = @ReportID AND Name = @OldName
		END
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_updateQuery]    Script Date: 12/31/2013 13:31:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_updateQuery]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

CREATE PROC [dbo].[sp_updateQuery]
(
	@node int,
	@parentNode int,
	@name varchar(200),
	@queryText ntext,
	@server  varchar(50),
	@database varchar(50),
	@changeServer bit,
	@isReport bit=0,
	@startRow int=1,
	@comments varchar(1000)=null
)
AS
BEGIN
	SET NOCOUNT ON;
	IF(@node=0 AND @parentNode=0)
		return;
		
	IF(@node=0)
		BEGIN
			INSERT dbo.Nodes VALUES(@parentNode,@name,CASE WHEN @isReport =0 THEN 13 ELSE 15 END,1,1,NULL);
			SET @node = @@IDENTITY;
			INSERT INTO [dbo].[Qa_Queries]
			   ([ID]
			   ,[Name]
			   ,[QueryText]
			   ,[Server]
			   ,[Database]
			   ,[StartRow]
			   ,[Comments])
			 VALUES
			   (@node
			   ,@name
			   ,@queryText
			   ,@server
			   ,@database
			   ,@startRow
			   ,@comments)
			   
			  
		END
	ELSE
		BEGIN
			UPDATE [dbo].[Qa_Queries]
				SET [Name] = @name
				,[QueryText] = @queryText
				,[Server]=CASE WHEN @changeServer=1 THEN @server ELSE [Server] END
				,[Database] = @database
				,[StartRow] = @startRow
				,[Comments] = @comments
				WHERE ID = @node
			UPDATE dbo.Nodes SET name = @name WHERE id = @node;
			
			UPDATE ScheduledScripts SET Name = @name WHERE QueryID=@node;
		END
	SELECT @node;
 END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_updateNote]    Script Date: 12/31/2013 13:31:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_updateNote]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_updateNote]
	@ID int,
	@NoteContent ntext
AS
BEGIN
	IF EXISTS(SELECT ID FROM dbo.Notes WHERE ID = @ID)
		UPDATE dbo.Notes SET NoteContent = @NoteContent WHERE ID = @ID
	ELSE
		INSERT  dbo.Notes VALUES(@ID,@NoteContent)
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_updateNode]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_updateNode]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
CREATE PROCEDURE [dbo].[sp_updateNode]
(
	@parentId int,
	@id int=0,
	@name varchar(100),
	@nodeType int,
	@leaf bit=1,
	@show bit=1,
	@refer int=null
)
AS
BEGIN
	SET NOCOUNT ON;
	IF(@id=0)
		BEGIN
			INSERT INTO [dbo].[Nodes]([parentId],[name],[nodeType] ,[leaf] ,[show],[refer])
				VALUES(@parentId,@name,@nodeType,@leaf,@show,@refer)
		END
	ELSE
		BEGIN
			UPDATE [dbo].[Nodes]
				SET name = @name
				WHERE id=@id
		END
	

END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_updateLineChart]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_updateLineChart]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
CREATE PROC [dbo].[sp_updateLineChart]
	 @Category varchar(100)
	,@LineColumns varchar(200)
	,@ReportID int
AS
BEGIN
	IF(@Category IS NULL OR LEN(@Category)=0)
		BEGIN
			DELETE FROM dbo.Qa_LineChart WHERE ReportID= @ReportID 
		END
	ELSE IF EXISTS(SELECT * FROM dbo.Qa_LineChart WHERE ReportID= @ReportID )
		BEGIN
			UPDATE dbo.Qa_LineChart SET Category = @Category,LineColumns = @LineColumns
				WHERE ReportID= @ReportID 
		END
	ELSE
		BEGIN
			INSERT INTO dbo.Qa_LineChart
					   (Category
					   ,LineColumns
					   ,ReportID)
				 VALUES
					   ( @Category
						,@LineColumns
						,@ReportID)
		END
END



' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_updateBot]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_updateBot]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

CREATE PROC [dbo].[sp_updateBot]
(
	@BotID int,
	@BotName varchar(200),
	@JobIDs varchar(200),
	@Sector VARCHAR(50),
	@Disabled bit
)
AS
BEGIN
  SET NOCOUNT ON;
	DECLARE @parentID int;
  DECLARE @Tran varchar(20);
  SET @Tran = ''Tran1''
  IF(@BotID = 0)
		BEGIN
		  IF NOT EXISTS(SELECT * FROM dbo.Bots where BotName=@BotName)
			BEGIN
				BEGIN TRANSACTION @Tran
				BEGIN TRY
					
					SELECT @parentID = id FROM dbo.Nodes WHERE nodeType = 12 AND name = @Sector;
					IF(@parentID IS NULL)
						BEGIN
							INSERT INTO dbo.Nodes VALUES(2,@Sector,12,0,1,null);
							SET @parentID = @@IDENTITY;
						END
						
					INSERT INTO dbo.Bots(BotName,JobIDs,Sector,[Disabled])values(@BotName,@JobIDs,@Sector,@Disabled);
					SET @BotID = @@IDENTITY;
					INSERT INTO dbo.Nodes VALUES(@parentID,@BotName,3,0,1,@BotID);
					SET @parentID = @@IDENTITY;
					INSERT INTO dbo.Nodes VALUES(@parentID,''Documents'',-14,0,1,@BotID);
					INSERT INTO dbo.Nodes VALUES(@parentID,''Reports'',-15,0,1,@BotID);
					INSERT INTO dbo.Nodes VALUES(@parentID,''ScheduledScripts'',16,1,1,@BotID);
					
					COMMIT TRANSACTION @Tran
				END TRY
				BEGIN CATCH
					ROLLBACK TRANSACTION @Tran
				END CATCH
			END
		END
   ELSE
		BEGIN
			UPDATE dbo.Bots SET BotName = @BotName,JobIDs =@JobIDs,Sector = @Sector,[Disabled] = @Disabled
			WHERE ID = @BotID;
			
			SET @parentID = NULL;
			
			SELECT @parentID=id FROM dbo.Nodes
			WHERE name=@Sector AND nodeType = 12
			 
			UPDATE dbo.Nodes
			SET parentId = ISNULL(@parentId,parentId),name = @BotName
			WHERE nodeType = 3 AND refer = @BotID
		END
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_startAllBot]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_startAllBot]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
CREATE PROCEDURE [dbo].[sp_startAllBot]

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
 UPDATE [dbo].[Bots] SET [QaStatus] = 2 
  WHERE QaStatus = 0 AND [Disabled] = 0

END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_getStandardValues]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_getStandardValues]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_getStandardValues]
	@node int
AS
BEGIN
	SET NOCOUNT ON;
	SELECT ID 
		  ,A.ReportID
		  ,ColumnName 
		  ,Show 
		  ,MinValue
		  ,MaxValue 
		  ,AvgValue
		  ,Fluctuation
		  ,ColOrder
		  ,CASE WHEN B.ReportID IS NULL THEN 0 ELSE 1 END AS HasHeadLink
	  FROM Qa_ReportColumns AS A
	  LEFT JOIN dbo.Qa_ReportHeadLineChart AS B
	  ON A.ReportID = B.ReportID AND A.ColumnName = B.LinkColumn
	  WHERE A.ReportID = @node 
	  ORDER BY ColOrder ASC
 END' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_getScheduledScripts]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_getScheduledScripts]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
 
 CREATE PROC [dbo].[sp_getScheduledScripts]
	@BotID int
 AS
 BEGIN
  SET NOCOUNT ON
  SELECT A.*,B.QueryText,B.[Database],B.[Server]
  FROM dbo.ScheduledScripts AS A
  INNER JOIN dbo.Qa_Queries AS B
  ON A.QueryID = B.ID
  WHERE A.BotID = @BotID
 END
  
  ' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_getReportLinkCharts]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_getReportLinkCharts]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_getReportLinkCharts]
	@ReportID int
AS
BEGIN
	SELECT A.SubReportName,A.LinkColumn,A.SubReportID,B.ParamName,B.ParamValueLink
	FROM dbo.Qa_ReportLinkChart AS A
	LEFT JOIN dbo.Qa_ReportLinkChartParams AS B
	ON A.ID = B.LinkID
	WHERE A.ReportID = @ReportID
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_getReportLinkChartParams]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_getReportLinkChartParams]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_getReportLinkChartParams]
	@ReportID int,
	@LinkColumn varchar(50)
AS
BEGIN
	SELECT B.Name,B.DataType,C.ParamValueLink AS ValueColumn
	FROM dbo.Qa_ReportLinkChart AS A
	LEFT JOIN dbo.Qa_Query_Params AS B
	ON A.SubReportID = B.ReportID
	LEFT JOIN dbo.Qa_ReportLinkChartParams AS C
	ON B.Name = C.ParamName AND A.ID = C.LinkID
	WHERE A.ReportID = @ReportID AND A.LinkColumn = @LinkColumn
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_getReportHeadLineChart]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_getReportHeadLineChart]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_getReportHeadLineChart]
	 @LinkColumn varchar(100)
	,@ReportID int
AS
BEGIN
	SET NOCOUNT ON;
		
	SELECT [LinkColumn],[Category] ,[TopRows] ,[LineColumns] ,[ReportID] 
	FROM [dbo].[Qa_ReportHeadLineChart] 
	WHERE [ReportID]= @ReportID AND [LinkColumn] = @LinkColumn
		
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_getQuery]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_getQuery]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
CREATE PROC [dbo].[sp_getQuery]
(
	@node int
)
AS
BEGIN
	SET NOCOUNT ON;
		
	SELECT 
	   ID AS node
	  ,[Name] AS name
	  ,[QueryText] AS queryText
	  ,[Server] AS [server]
	  ,[Database] AS [database]
	  ,QueryType AS queryType
	  ,Comments AS comments
	  ,ISNULL(StartRow,1) AS startRow
	FROM [dbo].[Qa_Queries]
	WHERE ID = @node;

END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_getQaResultNode]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_getQaResultNode]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROC [dbo].[sp_getQaResultNode]
 @parentId int
AS
BEGIN
	IF(@parentId = 2)
		BEGIN
			SELECT *,''folder'' as iconCls FROM dbo.Nodes
				WHERE parentId = @parentId
		END
	ELSE
		BEGIN
			SELECT A.id,parentId,A.name,nodeType,1 AS leaf,refer,''bot'' as iconCls
			FROM dbo.Nodes AS A
			LEFT JOIN dbo.Bots AS B
			ON A.refer = B.ID
			WHERE parentId = @parentId AND B.[Disabled] = 0
			ORDER BY A.name
		END
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[sp_getNodes]    Script Date: 12/31/2013 13:31:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_getNodes]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
CREATE PROCEDURE [dbo].[sp_getNodes]
(
	@parentId int
)
AS
BEGIN
	SET NOCOUNT ON;

	SELECT A.*,B.Cls as iconCls
	FROM dbo.Nodes AS A 
	LEFT JOIN dbo.NodeTypes AS B
	ON A.nodeType = B.Id
	WHERE parentId = @parentId
	ORDER BY leaf,name

END
' 
END
GO
/****** Object:  Default [DF_Bots_Sector]    Script Date: 12/31/2013 13:31:12 ******/
IF Not EXISTS (SELECT * FROM sys.default_constraints WHERE object_id = OBJECT_ID(N'[dbo].[DF_Bots_Sector]') AND parent_object_id = OBJECT_ID(N'[dbo].[Bots]'))
Begin
IF NOT EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_Bots_Sector]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[Bots] ADD  CONSTRAINT [DF_Bots_Sector]  DEFAULT ('Temp') FOR [Sector]
END


End
GO
/****** Object:  Default [DF_Bots_LastQaDate]    Script Date: 12/31/2013 13:31:12 ******/
IF Not EXISTS (SELECT * FROM sys.default_constraints WHERE object_id = OBJECT_ID(N'[dbo].[DF_Bots_LastQaDate]') AND parent_object_id = OBJECT_ID(N'[dbo].[Bots]'))
Begin
IF NOT EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_Bots_LastQaDate]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[Bots] ADD  CONSTRAINT [DF_Bots_LastQaDate]  DEFAULT (dateadd(hour,(-10),getdate())) FOR [LastQaDate]
END


End
GO
/****** Object:  Default [DF_Bots_LastCheckDate]    Script Date: 12/31/2013 13:31:12 ******/
IF Not EXISTS (SELECT * FROM sys.default_constraints WHERE object_id = OBJECT_ID(N'[dbo].[DF_Bots_LastCheckDate]') AND parent_object_id = OBJECT_ID(N'[dbo].[Bots]'))
Begin
IF NOT EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_Bots_LastCheckDate]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[Bots] ADD  CONSTRAINT [DF_Bots_LastCheckDate]  DEFAULT (dateadd(hour,(-10),getdate())) FOR [LastCheckDate]
END


End
GO
/****** Object:  Default [DF_Bots_QaStatus]    Script Date: 12/31/2013 13:31:12 ******/
IF Not EXISTS (SELECT * FROM sys.default_constraints WHERE object_id = OBJECT_ID(N'[dbo].[DF_Bots_QaStatus]') AND parent_object_id = OBJECT_ID(N'[dbo].[Bots]'))
Begin
IF NOT EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_Bots_QaStatus]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[Bots] ADD  CONSTRAINT [DF_Bots_QaStatus]  DEFAULT ((0)) FOR [QaStatus]
END


End
GO
/****** Object:  Default [DF_Bots_RunDate]    Script Date: 12/31/2013 13:31:12 ******/
IF Not EXISTS (SELECT * FROM sys.default_constraints WHERE object_id = OBJECT_ID(N'[dbo].[DF_Bots_RunDate]') AND parent_object_id = OBJECT_ID(N'[dbo].[Bots]'))
Begin
IF NOT EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_Bots_RunDate]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[Bots] ADD  CONSTRAINT [DF_Bots_RunDate]  DEFAULT (dateadd(day,(-7),getdate())) FOR [RunDate]
END


End
GO
/****** Object:  Default [DF_Bots_RunID]    Script Date: 12/31/2013 13:31:12 ******/
IF Not EXISTS (SELECT * FROM sys.default_constraints WHERE object_id = OBJECT_ID(N'[dbo].[DF_Bots_RunID]') AND parent_object_id = OBJECT_ID(N'[dbo].[Bots]'))
Begin
IF NOT EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_Bots_RunID]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[Bots] ADD  CONSTRAINT [DF_Bots_RunID]  DEFAULT ((0)) FOR [RunID]
END


End
GO
/****** Object:  Default [DF_Bots_priority]    Script Date: 12/31/2013 13:31:12 ******/
IF Not EXISTS (SELECT * FROM sys.default_constraints WHERE object_id = OBJECT_ID(N'[dbo].[DF_Bots_priority]') AND parent_object_id = OBJECT_ID(N'[dbo].[Bots]'))
Begin
IF NOT EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_Bots_priority]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[Bots] ADD  CONSTRAINT [DF_Bots_priority]  DEFAULT ((3)) FOR [priority]
END


End
GO
/****** Object:  Default [DF_Bots_Disabled]    Script Date: 12/31/2013 13:31:12 ******/
IF Not EXISTS (SELECT * FROM sys.default_constraints WHERE object_id = OBJECT_ID(N'[dbo].[DF_Bots_Disabled]') AND parent_object_id = OBJECT_ID(N'[dbo].[Bots]'))
Begin
IF NOT EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_Bots_Disabled]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[Bots] ADD  CONSTRAINT [DF_Bots_Disabled]  DEFAULT ((0)) FOR [Disabled]
END


End
GO
/****** Object:  Default [DF_Nodes_show]    Script Date: 12/31/2013 13:31:12 ******/
IF Not EXISTS (SELECT * FROM sys.default_constraints WHERE object_id = OBJECT_ID(N'[dbo].[DF_Nodes_show]') AND parent_object_id = OBJECT_ID(N'[dbo].[Nodes]'))
Begin
IF NOT EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_Nodes_show]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[Nodes] ADD  CONSTRAINT [DF_Nodes_show]  DEFAULT ((1)) FOR [show]
END


End
GO
/****** Object:  Default [DF_Qa_Scripts_ScriptType]    Script Date: 12/31/2013 13:31:12 ******/
IF Not EXISTS (SELECT * FROM sys.default_constraints WHERE object_id = OBJECT_ID(N'[dbo].[DF_Qa_Scripts_ScriptType]') AND parent_object_id = OBJECT_ID(N'[dbo].[Qa_Scripts]'))
Begin
IF NOT EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_Qa_Scripts_ScriptType]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[Qa_Scripts] ADD  CONSTRAINT [DF_Qa_Scripts_ScriptType]  DEFAULT ((1)) FOR [ScriptType]
END


End
GO
/****** Object:  Default [DF_ScheduledScripts_ConvertType]    Script Date: 12/31/2013 13:31:12 ******/
IF Not EXISTS (SELECT * FROM sys.default_constraints WHERE object_id = OBJECT_ID(N'[dbo].[DF_ScheduledScripts_ConvertType]') AND parent_object_id = OBJECT_ID(N'[dbo].[ScheduledScripts]'))
Begin
IF NOT EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_ScheduledScripts_ConvertType]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[ScheduledScripts] ADD  CONSTRAINT [DF_ScheduledScripts_ConvertType]  DEFAULT ((0)) FOR [ConvertType]
END


End
GO
/****** Object:  Check [CK_Qa_Scripts_BotID]    Script Date: 12/31/2013 13:31:12 ******/
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE object_id = OBJECT_ID(N'[dbo].[CK_Qa_Scripts_BotID]') AND parent_object_id = OBJECT_ID(N'[dbo].[Qa_Scripts]'))
ALTER TABLE [dbo].[Qa_Scripts]  WITH CHECK ADD  CONSTRAINT [CK_Qa_Scripts_BotID] CHECK  (([BotID]>(0)))
GO
IF  EXISTS (SELECT * FROM sys.check_constraints WHERE object_id = OBJECT_ID(N'[dbo].[CK_Qa_Scripts_BotID]') AND parent_object_id = OBJECT_ID(N'[dbo].[Qa_Scripts]'))
ALTER TABLE [dbo].[Qa_Scripts] CHECK CONSTRAINT [CK_Qa_Scripts_BotID]
GO
/****** Object:  Check [CK_Qa_Scripts_ID]    Script Date: 12/31/2013 13:31:12 ******/
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE object_id = OBJECT_ID(N'[dbo].[CK_Qa_Scripts_ID]') AND parent_object_id = OBJECT_ID(N'[dbo].[Qa_Scripts]'))
ALTER TABLE [dbo].[Qa_Scripts]  WITH CHECK ADD  CONSTRAINT [CK_Qa_Scripts_ID] CHECK  (([ID]>(0)))
GO
IF  EXISTS (SELECT * FROM sys.check_constraints WHERE object_id = OBJECT_ID(N'[dbo].[CK_Qa_Scripts_ID]') AND parent_object_id = OBJECT_ID(N'[dbo].[Qa_Scripts]'))
ALTER TABLE [dbo].[Qa_Scripts] CHECK CONSTRAINT [CK_Qa_Scripts_ID]
GO
