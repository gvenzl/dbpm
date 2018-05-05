// ***************************************************************************
//
// Author: gvenzl
// Created: 28/02/2018
//
// Name: DBExecutorTest.java
// Description: JUnit test file for DBExecutor
//
// Copyright 2018 - Gerald Venzl
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

import org.junit.Test;

import java.sql.SQLException;

public class DBExecutorTest {

    private final String userNameMySQL = "root";
    private final String userNamePostgres = "postgres";
    private final String userNameOracle = "system";
    private final String portMySQL = "3306";
    private final String portPostgres = "5432";
    private final String portOracle = "1521";
    private final String passwordMySQL = "test";
    private final String passwordPostgres = "test";
    private final String passwordOracle = "oracle";
    private final String hostName = "localhost";
    private final String dbNameMySQL = "mysql";
    private final String dbNamePostgres = "postgres";
    private final String dbNameOracle = "xe";

    /**
     * Tests the instantiation of a database connection to MySQL.
     * @throws SQLException
     */
    @Test
    public void test_MySQL_Instantiation() throws SQLException {
        new DBExecutor(DbType.MYSQL, userNameMySQL, passwordMySQL, hostName, portMySQL, dbNameMySQL);
    }

    /**
     * Tests the successful execution of a MySQL command.
     * @throws SQLException
     */
    @Test
    public void test_MySQL_runCommand() throws SQLException {
        DBExecutor exec = new DBExecutor(DbType.MYSQL, userNameMySQL, passwordMySQL, hostName,  portMySQL, dbNameMySQL);
        exec.execute("SELECT version()");
    }

    /**
     * Tests the instantiation of a database connection to Postgres.
     * @throws SQLException
     */
    @Test
    public void test_PostgreSQL_Instantiation() throws SQLException {
        new DBExecutor(DbType.POSTGRES, userNamePostgres, passwordPostgres, hostName, portPostgres, dbNamePostgres);
    }

    /**
     * Tests the successful execution of a Postgres command.
     * @throws SQLException
     */
    @Test
    public void test_PostgreSQL_runCommand() throws SQLException {
        DBExecutor exec = new DBExecutor(DbType.POSTGRES, userNamePostgres, passwordPostgres, hostName,  portPostgres, dbNamePostgres);
        exec.execute("SELECT version()");
    }

    /**
     * Tests the instantiation of a database connection to Oracle.
     * @throws SQLException
     */
    @Test
    public void test_Oracle_Instantiation() throws SQLException {
        new DBExecutor(DbType.ORACLE, userNameOracle, passwordOracle, hostName, portOracle, dbNameOracle);
    }

    /**
     * Tests the successful execution of an Oracle command.
     * @throws SQLException
     */
    @Test
    public void test_Oracle_runCommand() throws SQLException {
        DBExecutor exec = new DBExecutor(DbType.ORACLE, userNameOracle, passwordOracle, hostName,  portOracle, dbNameOracle);
        exec.execute("SELECT banner from v$version");
    }
}
