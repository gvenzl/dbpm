/*
*
* author:  gvenzl
* created: 19 Nov 2017
*
* name: DBExecutor.java
*
*/
package com.dbpm.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBExecutor {

    private final String userName;
    private final String hostName;
    private final String password;
    private final String port;
    private final String dbName;

    private final DBTYPE dbType;

    private Connection conn;
    private Connection adminConn;

    /**
     * Creates a new DBExecutor object.
     * @param dbType The database type to create a connection for
     * @param userName The user name for the database user
     * @param password The user password for the database user
     * @param hostName The host name of the database server to connect
     * @param port The port of the database
     * @param dbName The database name
     * @throws SQLException Any error occurring during the connection attempt
     */
    public DBExecutor(DBTYPE dbType, String userName, String password, String hostName, String port, String dbName)
            throws SQLException {
        this.dbType = dbType;
        this.userName = userName;
        this.password = password;
        this.hostName = hostName;
        this.port = port;
        this.dbName = dbName;

        this.conn = getConnection(this.userName, this.password);
    }

    private Connection getConnection(String username, String password) throws SQLException {

        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:");

        switch (dbType) {
            case ORACLE: {
                sb.append("oracle:thin:@");
                break;
            }
            case MYSQL: {
                sb.append("mysql:");
                break;
            }
            case POSTGRES: {
                sb.append("postgresql:");
                break;
            }
        }

        sb.append("//");
        sb.append(hostName);
        sb.append(":");
        sb.append(port);
        sb.append("/");
        sb.append(dbName);

        Connection conn = DriverManager.getConnection(sb.toString(), username, password);
        conn.setAutoCommit(false);

        return conn;
    }

    public void runCommands(String commands) throws SQLException {

        conn.createStatement().execute(commands);
    }

}
