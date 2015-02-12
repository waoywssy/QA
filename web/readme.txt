node type:
Id	TypeName	Cls
1	File	file
2	Folder	folder
3	Bot	bot
4	Column	column
5	Database	database
6	Server	server
7	StoredProcedure	storedProcedure
8	Table	table
9	ReportColumn	rcolumn
12	Sector	folder
13	Query	query
14	Document	file
15	Report	report
16	ScheduledQuery	file
17	BirtReport	file
18	Component	file



chart type:
3 Table
4 Bar Chart    
5 Pie Chart
6 Line Chart
7 Pivot Table
8 Tree

USE mj_auto_qa
GO

--update referID
SELECT A.[Database]
FROM dbo.Qa_Queries AS A
INNER JOIN dbo.Nodes  AS B
ON A.ID = B.id
WHERE nodeType=15 AND [Database]='Qa_DataStatistic1'

--update group column
SELECT A.[Database]
FROM dbo.Qa_Queries AS A
INNER JOIN dbo.Nodes AS B
ON A.ID = B.id
WHERE B.parentId IN(SELECT id FROM dbo.Nodes WHERE name='AutoScript')
