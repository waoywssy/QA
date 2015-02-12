USE Qa_DataStatistic1

DECLARE @tableName varchar(100);
DECLARE @stmt nvarchar(1000);
SET @stmt='ALTER TABLE {Table} ADD Finished bit';
DECLARE Cur CURSOR FOR
SELECT name FROM sys.tables

OPEN Cur
FETCH NEXT FROM Cur INTO @tableName
WHILE(@@FETCH_STATUS=0)
BEGIN
	SET @stmt='ALTER TABLE '+@tableName+' ADD Finished bit default(1)';
	exec sp_executesql @stmt;
	FETCH NEXT FROM Cur INTO @tableName
END
CLOSE Cur
DEALLOCATE Cur

GO

UPDATE dbo.Maj_Runs SET Finished = 1
FROM dbo.Maj_Runs AS A
INNER JOIN
(
	SELECT MAX(QaDate) AS QaDate,RunID
	FROM dbo.Maj_Runs
	GROUP BY RunID
)AS B
ON A.QaDate = B.QaDate AND A.RunID = B.RunID

--SELECT * FROM dbo.Maj_Runs
--WHERE JobID = 5 AND Finished=1
--ORDER BY RunID DESC