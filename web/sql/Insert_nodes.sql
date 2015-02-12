USE mj_auto_qa

RETURN;

TRUNCATE TABLE dbo.NodeTypes
TRUNCATE TABLE dbo.Nodes
TRUNCATE TABLE dbo.Qa_Files
TRUNCATE TABLE dbo.Qa_Queries
TRUNCATE TABLE dbo.ScheduledScripts

INSERT INTO dbo.NodeTypes VALUES(1,'File','file');
INSERT INTO dbo.NodeTypes VALUES(2,'Folder','folder');
INSERT INTO dbo.NodeTypes VALUES(3,'Bot','bot');
INSERT INTO dbo.NodeTypes VALUES(4,'Column','column');
INSERT INTO dbo.NodeTypes VALUES(5,'Database','database');
INSERT INTO dbo.NodeTypes VALUES(6,'Server','server');
INSERT INTO dbo.NodeTypes VALUES(7,'StoredProcedure','storedProcedure');
INSERT INTO dbo.NodeTypes VALUES(8,'Table','table');
INSERT INTO dbo.NodeTypes VALUES(9,'ReportColumn','rcolumn');
INSERT INTO dbo.NodeTypes VALUES(12,'Sector','folder');
INSERT INTO dbo.NodeTypes VALUES(13,'Query','query');
INSERT INTO dbo.NodeTypes VALUES(14,'Document','file');
INSERT INTO dbo.NodeTypes VALUES(15,'Report','report');
INSERT INTO dbo.NodeTypes VALUES(16,'ScheduledQuery','file');
INSERT INTO dbo.NodeTypes VALUES(17,'BirtReport','file');
INSERT INTO dbo.NodeTypes VALUES(18,'Component','file');
---DROP PrimaryKey


IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[Bots]') AND name = N'PK_Bots')
ALTER TABLE [dbo].[Bots] DROP CONSTRAINT [PK_Bots]

UPDATE dbo.DB_Servers SET ServerName = ServerIP

IF NOT EXISTS(SELECT * FROM [dbo].[DB_Servers] WHERE ServerIP='127.0.0.1')
INSERT [dbo].[DB_Servers] VALUES(19,'127.0.0.1','127.0.0.1',null,'sa','P@ssw0rd')
------1---------------============================================----------------------


SET IDENTITY_INSERT dbo.Nodes ON

insert into dbo.Nodes(id,parentId ,name ,nodeType,leaf,show ,refer) values(1,0,'Databases',-5,0,1,null);
insert into dbo.Nodes(id,parentId ,name ,nodeType,leaf,show ,refer) values(2,0,'Bots',-12,0,1,null);
insert into dbo.Nodes(id,parentId ,name ,nodeType,leaf,show ,refer) values(3,0,'Servers',-6,0,1,null);
insert into dbo.Nodes(id,parentId ,name ,nodeType,leaf,show ,refer) values(4,-1,'Bookmarks',2,0,1,null);
insert into dbo.Nodes(id,parentId ,name ,nodeType,leaf,show ,refer) values(5,-1,'Tools',2,0,1,null);
-- DELETE FROM dbo.Nodes WHERE ID = 4

SET IDENTITY_INSERT dbo.Nodes OFF


insert into dbo.Nodes(parentId ,name ,nodeType,leaf,show ,refer) values(5,'ImportDataPanel',18,1,1,null);
insert into dbo.Nodes(parentId ,name ,nodeType,leaf,show ,refer) values(5,'LogPanel',18,1,1,null);
insert into dbo.Nodes(parentId ,name ,nodeType,leaf,show ,refer) values(5,'OnTime',18,1,1,null);
insert into dbo.Nodes(parentId ,name ,nodeType,leaf,show ,refer) values(5,'ITGReport',18,1,1,null);
insert into dbo.Nodes(parentId ,name ,nodeType,leaf,show ,refer) values(5,'QueryDesign',18,1,1,null);
--UPDATE dbo.Nodes SET name = 'LogPanel' WHERE parentId = 5 and name='LogConsolePanel'
-------2----------------------
--insert servers
insert into dbo.Nodes
select 3,ServerName,6,2,1,id from dbo.DB_Servers


--insert databases
insert into dbo.Nodes
select 1,DatabaseName,5,0,1,id 
from dbo.DB_Databases 

insert into dbo.Nodes
select id,'Tables',-8,0,1,refer
from dbo.Nodes
where nodeType = 5

insert into dbo.Nodes
select id,'StoredProcedures',-7,0,1 ,refer
from dbo.Nodes
where nodeType = 5

insert into dbo.Nodes
select id,'Queries',2,0,1 ,refer
from dbo.Nodes
where nodeType = 5
-----------------insert tables============
insert into dbo.Nodes
select B.Id,TableName ,8,0,1,A.ID
from dbo.DB_Tables AS A
inner join dbo.Nodes AS B
on A.Database_ID = B.refer
where B.nodeType = -8

-----------------insert columns============
insert into dbo.Nodes
select B.Id
,Columnname +'('+DataType+','+CASE WHEN NullAble=1 THEN 'null' ELSE 'not null' END+')'
,4,1,1,A.ID
from dbo.DB_Columns AS A
inner join dbo.Nodes AS B
on A.Table_ID = B.refer
where B.nodeType = 8

-----------------insert sps============
insert into dbo.Nodes
select B.Id
,A.Name
,7,1,1,A.ID
from dbo.DB_StoredProcedures AS A
inner join dbo.Nodes AS B
on A.Database_ID = B.refer
where B.nodeType = -7


-------------insert bots folder=============
insert into dbo.Nodes
select B.Id,A.BotName,3,0,1,A.ID
from dbo.Bots AS A
inner join dbo.Nodes AS B
on A.Database_ID = B.refer
where B.nodeType = 2 and B.name = 'Queries'

-------------insert auto folder=============
insert into dbo.Nodes
select Id
,'AutoScript'
,2,0,1,refer
FROM dbo.Nodes
where nodeType = 3 

-------------insert auto scripts=============
insert into dbo.Nodes
select B.Id
,A.ScriptName
,13,1,1,A.ID
from dbo.Qa_Scripts AS A
inner join dbo.Nodes AS B
on A.BotID = B.refer
where B.name = 'AutoScript' and A.ScriptType = 2

-------------insert manual folder=============
insert into dbo.Nodes
select Id
,'ManualScript'
,2,0,1,refer
FROM dbo.Nodes
where nodeType = 3 

-------------insert manual scripts=============
insert into dbo.Nodes
select B.Id
,A.ScriptName
,13,1,1,A.ID
from dbo.Qa_Scripts AS A
inner join dbo.Nodes AS B
on A.BotID = B.refer
where B.name = 'ManualScript' and A.ScriptType = 1


UPDATE dbo.Nodes SET nodeType = 2 where nodeType = 3 


------------------Init Bot_Nodes-------------


INSERT dbo.Nodes
SELECT B.ID,A.Sector,12,0,1,null
FROM
(
	SELECT DISTINCT Sector FROM dbo.Bots
)AS A
INNER JOIN dbo.Nodes AS B
ON 1=1
WHERE B.nodeType = -12


INSERT dbo.Nodes
SELECT B.ID,A.BotName,3,0,1,A.ID FROM dbo.Bots AS A
LEFT JOIN dbo.Nodes AS B
ON A.Sector = B.Name
WHERE B.nodeType = 12


----insert folder


INSERT dbo.Nodes
SELECT ID,'Documents',-14,0,1,refer 
FROM dbo.Nodes 
WHERE nodeType = 3 

INSERT dbo.Nodes
SELECT ID,'Reports',-15,0,1,refer 
FROM dbo.Nodes 
WHERE nodeType = 3 

INSERT dbo.Nodes
SELECT ID,'ScheduledQuery',16,1,1,refer 
FROM dbo.Nodes 
WHERE nodeType = 3 






-----3------------------------------=======================

INSERT dbo.Qa_Queries(ID,Name,QueryType,QueryText,[Server],[Database])
	SELECT  B.ID,ScriptName,ScriptType ,Script,D.ServerName,C.DatabaseName
	FROM dbo.Bots AS A
	LEFT JOIN dbo.Qa_Scripts AS B
	ON A.ID = B.BotID
	LEFT JOIN dbo.DB_Databases AS C
	ON A.Database_ID = C.ID
	LEFT JOIN dbo.DB_Servers AS D
	ON C.ServerID = D.ID
	WHERE B.ID IS NOT NULL

UPDATE dbo.Qa_Queries SET [Server] = '127.0.0.1',[Database] = 'Qa_DataStatistic1'
WHERE ID IN(SELECT ID FROM dbo.Qa_Scripts WHERE ScriptType =3)



--Import qa scripts
--INSERT dbo.Report_BaseInfo(name,queryID,reportType)
--	SELECT ScriptName,ID,1 FROM dbo.Qa_Scripts
--	WHERE ScriptType =3 
	
--Insert report nodes
INSERT dbo.Nodes
	SELECT C.id,A.ScriptName,15,1,1,A.ID
	FROM dbo.Qa_Scripts AS A
	INNER JOIN dbo.Nodes AS C
	ON A.BotID = C.refer AND C.nodeType = -15
	WHERE A.ScriptType = 3


--Import scheduled scripts
INSERT dbo.ScheduledScripts
	SELECT A.ID AS BotID,D.ID,D.ScriptName,null,null,null--B.DatabaseName,C.ServerName,
		FROM dbo.Bots AS A
		LEFT JOIN dbo.DB_Databases AS B
		ON A.Database_ID = B.ID
		LEFT JOIN dbo.DB_Servers AS C
		ON B.ServerID = C.ID
		INNER JOIN dbo.Qa_Scripts AS D
		ON A.ID = D.BotID AND D.ScriptType = 2


----------------------Fix query id=======================
  --UPDATE dbo.Qa_ReportColumns
	 -- SET ReportID = B.ID
	 -- FROM dbo.Qa_ReportColumns AS A
	 -- INNER JOIN dbo.Report_BaseInfo AS B
	 -- ON A.ReportID = B.queryID
  
  --SELECT COUNT(*) FROM dbo.Qa_Queries
  
  UPDATE dbo.Qa_Queries
	  SET ID = B.ID
	  FROM dbo.Qa_Queries AS A
	  INNER JOIN dbo.Nodes AS B
	  ON A.ID = B.refer
	  WHERE B.nodeType IN(13,15)


  --SELECT COUNT(*) FROM Qa_ReportColumns
  
  DELETE FROM dbo.Qa_ReportColumns
	WHERE ReportID NOT IN(SELECT ID FROM dbo.Qa_Scripts WHERE ScriptType = 3)
		AND ReportID NOT IN(SELECT -ID FROM dbo.Bots )
		
   UPDATE dbo.Qa_ReportColumns
	  SET ReportID = B.ID
	  FROM dbo.Qa_ReportColumns AS A
	  INNER JOIN dbo.Nodes AS B
	  ON A.ReportID = B.refer
	  WHERE B.nodeType = 15
	  
	  
	UPDATE dbo.ScheduledScripts
	  SET QueryID = B.ID
	  FROM dbo.ScheduledScripts AS A
	  INNER JOIN dbo.Nodes AS B
	  ON A.QueryID = B.refer
	  WHERE B.nodeType = 13 
GO