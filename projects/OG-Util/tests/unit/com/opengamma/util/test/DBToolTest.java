/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.util.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.hibernate.mapping.Table;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.opengamma.OpenGammaRuntimeException;

/**
 * Test DBTool.
 */
@Test
public class DBToolTest {

  private static final String TEST_TABLE = "db_tool_unit_test_table";
  
  private DBTool _tool;
  private String _dbHost;
  private String _user;
  private String _password;

  @BeforeMethod
  public void setUp() throws Exception {
    Properties props = new Properties();
    props.load(getClass().getResourceAsStream("/tests.properties"));

    _dbHost = props.getProperty("jdbc.url");
    _user = props.getProperty("jdbc.username");
    _password = props.getProperty("jdbc.password");

    _tool = new DBTool(_dbHost, _user, _password);
    _tool.initialize();
  }

  public void testDrop() throws Exception {

    _tool.createTestSchema();
    createTestTable();
    _tool.dropTestSchema();

    // The table should no longer be there
    try {
      _tool.executeSql(_tool.getTestCatalog(), _tool.getTestSchema(), "SELECT * FROM " + TEST_TABLE);
      fail();
    } catch (OpenGammaRuntimeException e) {
      // Ok - no table should be there!
    }
  }

  public void testClear() throws Exception {

    _tool.createTestSchema();
    createTestTable();
    _tool.executeSql(_tool.getTestCatalog(), _tool.getTestSchema(), "INSERT INTO " + TEST_TABLE + " (test_column) VALUES ('test')");

    _tool.clearTestTables();

    Connection connection = getConnection();
    Statement statement = connection.createStatement();
    ResultSet rs = statement
        .executeQuery("SELECT COUNT(*) FROM " + TEST_TABLE);
    if (rs.next()) {
      int count = rs.getInt(1);
      assertEquals(0, count);
    } else {
      fail();
    }

    rs.close();
    statement.close();
    connection.close();

    _tool.dropTestSchema();
  }

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(_dbHost + "/" + _tool.getTestCatalog(),
        _user, _password);
  }

  private void createTestTable() throws SQLException {
    Table table = new Table(TEST_TABLE);
    try {
      String dropSql = table.sqlDropString(_tool.getHibernateDialect(), null, _tool
          .getTestSchema());
      _tool.executeSql(_tool.getTestCatalog(), _tool.getTestSchema(), dropSql);
    } catch (OpenGammaRuntimeException e) {
      // It might not exist, that's OK
    }
    String createSql = "CREATE TABLE "
        + table.getQualifiedName(_tool.getHibernateDialect(), null, _tool
            .getTestSchema()) + " (test_column char(50))";
    _tool.executeSql(_tool.getTestCatalog(), _tool.getTestSchema(), createSql);
  }

}
