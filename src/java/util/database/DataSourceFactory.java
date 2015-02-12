package util.database;

// DataSource factory for the MiniConnectionPoolManager test programs.
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.io.PrintWriter;
import javax.sql.ConnectionPoolDataSource;

public class DataSourceFactory {

    public static final String DRIVER_MYSQL = "mysql";
    public static final String DRIVER_MSSQL = "mssql";
    public static final String DRIVER_SINASQL = "sinasql";

    public static ConnectionPoolDataSource createDataSource() {
        return null;
    }

    public static ConnectionPoolDataSource createDataSource(String serverName, String databaseName, int port, String userName, String password, String driver) {
        if (DRIVER_MYSQL.equals(driver)) {
            return createDataSourceMySQL(serverName, databaseName, port, userName, password);
        } else if (DRIVER_MSSQL.equals(driver)) {
            return createDataSourceMS(serverName, databaseName, port, userName, password);
        }
        return null;
    }

    public static ConnectionPoolDataSource createDataSourceH2() throws Exception {
        // Version for H2:
        //org.h2.jdbcx.JdbcDataSource dataSource = new org.h2.jdbcx.JdbcDataSource();
        // dataSource.setURL("jdbc:h2:file:c:/temp/tempTestMiniConnectionPoolManagerDb");
        //dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");    // in-memory database
        return null;
    }

    public static ConnectionPoolDataSource createDataSourceDerby() throws Exception {
        // Version for Apache Derby:
   /*
         org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource dataSource = new org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource();
         dataSource.setDatabaseName("c:/temp/tempTestMiniConnectionPoolManagerDb");
         dataSource.setCreateDatabase("create");
         dataSource.setLogWriter(new PrintWriter(System.out));
         */
        return null;
    }

    public static ConnectionPoolDataSource createDataSourceJTDS() throws Exception {
        // Version for JTDS:
   /*
         net.sourceforge.jtds.jdbcx.JtdsDataSource dataSource = new net.sourceforge.jtds.jdbcx.JtdsDataSource();
         dataSource.setAppName("TestMiniConnectionPoolManager");
         dataSource.setDatabaseName("Northwind");
         dataSource.setServerName("localhost");
         dataSource.setUser("sa");
         dataSource.setPassword(System.getProperty("saPassword"));
         */
        return null;
    }

    public static ConnectionPoolDataSource createDataSourceMS(String serverName, String databaseName, int port, String userName, String password) {
        // Version for the Microsoft SQL Server driver (sqljdbc.jar):
        com.microsoft.sqlserver.jdbc.SQLServerXADataSource dataSource = new com.microsoft.sqlserver.jdbc.SQLServerXADataSource();
        //dataSource.setApplicationName("TestMiniConnectionPoolManager");
        dataSource.setDatabaseName(databaseName);
        dataSource.setServerName(serverName);
        if (port > 0) {
            dataSource.setPortNumber(port);
        }
        dataSource.setUser(userName);
        dataSource.setPassword(password);
        dataSource.setLogWriter(new PrintWriter(System.out));

        return dataSource;
    }

    public static ConnectionPoolDataSource createDataSourceOracle() throws Exception {

        // Version for Oracle:
   /*
         oracle.jdbc.pool.OracleConnectionPoolDataSource dataSource = new oracle.jdbc.pool.OracleConnectionPoolDataSource();
         dataSource.setDriverType("thin");
         dataSource.setServerName("vm1");
         dataSource.setPortNumber(1521);
         dataSource.setServiceName("vm1.inventec.ch");
         dataSource.setUser("system");
         dataSource.setPassword("x");
         */
        return null;
    }

    public static ConnectionPoolDataSource createDataSourceMySQL(String serverName, String databaseName, int port, String userName, String password) {
        // Version for the MySQL
        MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setServerName(serverName);
        dataSource.setDatabaseName(databaseName);
        if (port > 0) {
            dataSource.setPortNumber(port);
        }
        dataSource.setUser(userName);
        dataSource.setPassword(password);
        return dataSource;
    }
} // end class DataSourceFactory