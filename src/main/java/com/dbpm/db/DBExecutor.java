// ***************************************************************************
//
// Author: gvenzl
// Created: 19/11/2017
//
// Name: DBExecutor.java
// Description: The DBExecutor is responsible for executing commands inside the database.
//
// Copyright 2017 - Gerald Venzl
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// ***************************************************************************

package com.dbpm.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBExecutor implements Executor {

    private final String userName;
    private final String hostName;
    private final String password;
    private final String port;
    private final String dbName;

    private final DbType dbType;

    private Connection conn;

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
    public DBExecutor(DbType dbType, String userName, String password, String hostName, String port, String dbName)
            throws SQLException {
        this.dbType = dbType;
        this.userName = userName;
        this.password = password;
        this.hostName = hostName;
        this.port = port;
        this.dbName = dbName;

        this.conn = getConnection();
    }

    private Connection getConnection() throws SQLException {

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

        Connection conn = DriverManager.getConnection(sb.toString(), userName, password);
        conn.setAutoCommit(false);

        return conn;
    }

    /**
     * Executes given SQL commands
     * @param commands The commands to be executed
     * @return True on successful execution
     * @throws SQLException Any database error during execution of the command
     */
    public boolean execute(String commands) throws SQLException {
        conn.createStatement().execute(commands);
        return true;
    }

}
