/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.util.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.id.enhanced.SequenceStructure;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.util.tuple.FirstThenSecondPairComparator;
import com.opengamma.util.tuple.Pair;

/**
 * Abstract implementation of DBDialect.
 */
public abstract class AbstractDBDialect implements DBDialect {

  /**
   * The database server.
   */
  private String _dbServerHost;
  /**
   * The user name.
   */
  private String _user;
  /**
   * The password.
   */
  private String _password;

  //-------------------------------------------------------------------------
  @Override
  public void initialise(String dbServerHost, String user, String password) {
    _dbServerHost = dbServerHost;
    _user = user;
    _password = password;
    
    try {
      getJDBCDriverClass().newInstance(); // load driver.
    } catch (Exception e) {
      throw new OpenGammaRuntimeException("Cannot load JDBC driver", e);
    }
  }
  
  @Override
  public String getTestCatalog() {
    return "test_" + System.getProperty("user.name").replace('.', '_');    
  }
  
  @Override
  public String getTestSchema() {
    return null; // use default    
  }
  
  @Override
  public void reset(String catalog) {
    // by default, do nothing
  }

  @Override
  public void shutdown(String catalog) {
    // by default, do nothing
  }

  public String getDbHost() {
    return _dbServerHost;
  }

  public String getUser() {
    return _user;
  }

  public String getPassword() {
    return _password;
  }

  //-------------------------------------------------------------------------
  /**
   * Generic representation of a column.
   */
  protected class ColumnDefinition implements Comparable<ColumnDefinition> {
    
    private final String _name;
    private final String _dataType;
    private final String _defaultValue;
    private final String _allowsNull;
    
    protected ColumnDefinition(final String name, final String dataType, final String defaultValue, final String allowsNull) {
      _name = name;
      _dataType = dataType;
      _defaultValue = defaultValue;
      _allowsNull = allowsNull;
    }
    
    public String getName() {
      return _name;
    }
    
    public String getDataType() {
      return _dataType;
    }
    
    public String getDefaultValue() {
      return _defaultValue;
    }
    
    public String getAllowsNull() {
      return _allowsNull;
    }
    
    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append(getName().toUpperCase()).append('=').append(getDataType().toUpperCase());
      if (getAllowsNull() != null) {
        sb.append(";NULL=").append(getAllowsNull());
      }
      if (getDefaultValue() != null) {
        sb.append(";DEFAULT=").append(getDefaultValue());
      }
      return sb.toString(); 
    }
    
    @Override
    public boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (o == null) {
        return false;
      }
      if (!(o instanceof ColumnDefinition)) {
        return false;
      }
      ColumnDefinition c = (ColumnDefinition) o;
      return ObjectUtils.equals(getName(), c.getName())
          && ObjectUtils.equals(getDataType(), c.getDataType())
          && ObjectUtils.equals(getAllowsNull(), c.getAllowsNull())
          && ObjectUtils.equals(getDefaultValue(), c.getDefaultValue());
    }
    
    @Override
    public int hashCode() {
      int hc = 1;
      hc = hc * 17 + ObjectUtils.hashCode(getName());
      hc = hc * 17 + ObjectUtils.hashCode(getDataType());
      hc = hc * 17 + ObjectUtils.hashCode(getAllowsNull());
      hc = hc * 17 + ObjectUtils.hashCode(getDefaultValue());
      return hc;
    }
    
    @Override
    public int compareTo(final ColumnDefinition c) {
      return getName().compareTo(c.getName());
    }
    
  }
  
  public abstract String getAllSchemasSQL(String catalog);
  public abstract String getAllTablesSQL(String catalog, String schema);
  public abstract String getAllViewsSQL(String catalog, String schema);
  public abstract String getAllColumnsSQL(String catalog, String schema, String table);
  public abstract String getAllSequencesSQL(String catalog, String schema);
  public abstract String getAllForeignKeyConstraintsSQL(String catalog, String schema);
  public abstract String getCreateSchemaSQL(String catalog, String schema);
  public abstract CatalogCreationStrategy getCatalogCreationStrategy();
  
  public void setActiveSchema(Connection connection, String schema) throws SQLException {
    // override in subclasses as necessary
  }
  
  protected Connection connect(String catalog) throws SQLException {
    Connection conn = DriverManager.getConnection(getCatalogToConnectTo(catalog), 
        _user, _password);
    conn.setAutoCommit(true);
    return conn;
  }

  protected String getCatalogToConnectTo(String catalog) {
    return getDbHost() + "/" + catalog;
  }
  
  private List<String> getAllTables(String catalog, String schema, Statement statement) throws SQLException {
    List<String> tables = new LinkedList<String>();
    ResultSet rs = statement.executeQuery(getAllTablesSQL(catalog, schema));
    while (rs.next()) {
      tables.add(rs.getString("name"));
    }
    rs.close();
    return tables;
  }
  
  private List<String> getAllViews(String catalog, String schema, Statement statement) throws SQLException {
    List<String> tables = new LinkedList<String>();
    ResultSet rs = statement.executeQuery(getAllViewsSQL(catalog, schema));
    while (rs.next()) {
      tables.add(rs.getString("name"));
    }
    rs.close();
    return tables;
  }
  
  private List<ColumnDefinition> getAllColumns(String catalog, String schema, String table, Statement statement) throws SQLException {
    List<ColumnDefinition> columns = new LinkedList<ColumnDefinition>();
    ResultSet rs = statement.executeQuery(getAllColumnsSQL(catalog, schema, table));
    while (rs.next()) {
      columns.add(new ColumnDefinition(rs.getString("name"), rs.getString("datatype"), rs.getString("defaultvalue"), rs.getString("allowsnull")));
    }
    rs.close();
    return columns;
  }
  
  @Override
  public void clearTables(String catalog, String schema, Collection<String> ignoredTables) {
    LinkedList<String> script = new LinkedList<String>();
    
    Connection conn = null;
    try {
      if (!getCatalogCreationStrategy().catalogExists(catalog)) {
        return; // nothing to clear
      }
      
      conn = connect(catalog);
      setActiveSchema(conn, schema);
      Statement statement = conn.createStatement();
      
      // Clear tables SQL
      for (String name : getAllTables(catalog, schema, statement)) {
        if (ignoredTables.contains(name.toLowerCase())) {
          continue;
        }
        
        Table table = new Table(name);
        script.add("DELETE FROM " + table.getQualifiedName(getHibernateDialect(), null, schema));
        
        if (table.getName().toLowerCase().indexOf("hibernate_sequence") != -1) { // if it's a sequence table, reset it 
          script.add("INSERT INTO " + table.getQualifiedName(getHibernateDialect(), null, schema) + " values ( 1 )");
        }
      }
            
      // Now execute it all. Constraints are taken into account by retrying the failed statement after all 
      // dependent tables have been cleared first.
      int i = 0;
      int maxAttempts = script.size() * 3; // make sure the loop eventually terminates. Important if there's a cycle in the table dependency graph
      SQLException latestException = null;
      while (i < maxAttempts && !script.isEmpty()) {
        String sql = script.remove();
        try {
          statement.executeUpdate(sql);
        } catch (SQLException e) {
          // assume it failed because of a constraint violation
          // try deleting other tables first - make this the new last statement
          latestException = e;
          script.add(sql);                              
        }
        i++;
      }
      statement.close();
      
      if (i == maxAttempts && !script.isEmpty()) {
        throw new OpenGammaRuntimeException("Failed to clear tables - is there a cycle in the table dependency graph?", latestException); 
      }
      
      
    } catch (SQLException e) {
      throw new OpenGammaRuntimeException("Failed to clear tables", e);
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException e) {
      }
    }           
    
  }
  
  protected List<String> getAllSchemas(final String catalog, final Statement stmt) throws SQLException {
    final List<String> schemas = new LinkedList<String>();
    ResultSet rs = stmt.executeQuery(getAllSchemasSQL(catalog));
    while (rs.next()) {
      schemas.add(rs.getString("name"));
    }
    rs.close();
    return schemas;
  }
  
  @Override
  public void createSchema(String catalog, String schema) {
    Connection conn = null;
    try {
      getCatalogCreationStrategy().create(catalog);
      
      if (schema != null) {
        // Connect to the new catalog and create the schema
        conn = connect(catalog);
        Statement statement = conn.createStatement(); 
        
        Collection<String> schemas = getAllSchemas(catalog, statement);
        if (!schemas.contains(schema)) {
          String createSchemaSql = getCreateSchemaSQL(catalog, schema);
          statement.executeUpdate(createSchemaSql);
        }
        
        statement.close();
      }
      
    } catch (SQLException e) {
      throw new OpenGammaRuntimeException("Failed to clear tables", e);
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException e) {
      }
    }  
    
  }
  
  protected List<String> getAllSequences(final String catalog, final String schema, final Statement stmt) throws SQLException {
    final List<String> sequences = new LinkedList<String>();
    final String sql = getAllSequencesSQL(catalog, schema);
    if (sql != null) {
      final ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        sequences.add(rs.getString("name"));
      }
      rs.close();
    }
    return sequences;
  }

  protected List<Pair<String, String>> getAllForeignKeyConstraints(final String catalog, final String schema, final Statement stmt) throws SQLException {
    final List<Pair<String, String>> sequences = new LinkedList<Pair<String, String>>();
    final String sql = getAllForeignKeyConstraintsSQL(catalog, schema);
    if (sql != null) {
      final ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        sequences.add(Pair.of(rs.getString("name"), rs.getString("table_name")));
      }
      rs.close();
    }
    return sequences;
  }

  @Override
  public void dropSchema(String catalog, String schema) {
    // Does not handle triggers or stored procedures yet
    
    ArrayList<String> script = new ArrayList<String>();
    
    Connection conn = null;
    try {
      if (!getCatalogCreationStrategy().catalogExists(catalog)) {
        System.out.println("Catalog " + catalog + " does not exist");
        return; // nothing to drop
      }
      
      conn = connect(catalog);
      
      if (schema != null) {
        Statement statement = conn.createStatement(); 
        Collection<String> schemas = getAllSchemas(catalog, statement);
        statement.close();
        
        if (!schemas.contains(schema)) {
          System.out.println("Schema " + schema + " does not exist");
          return; // nothing to drop
        }
      }

      setActiveSchema(conn, schema);
      Statement statement = conn.createStatement();
      
      // Drop constraints SQL
      if (getHibernateDialect().dropConstraints()) {
        for (Pair<String, String> constraint : getAllForeignKeyConstraints(catalog, schema, statement)) {
          String name = constraint.getFirst();
          String table = constraint.getSecond();
          ForeignKey fk = new ForeignKey();
          fk.setName(name);
          fk.setTable(new Table(table));
          
          String dropConstraintSql = fk.sqlDropString(getHibernateDialect(), null, schema);
          script.add(dropConstraintSql);
        }
      }
      
      // Drop views SQL
      for (String name : getAllViews(catalog, schema, statement)) {
        Table table = new Table(name);
        String dropViewStr = table.sqlDropString(getHibernateDialect(), null, schema);
        dropViewStr = dropViewStr.replaceAll("drop table", "drop view");
        script.add(dropViewStr);
      }
      
      // Drop tables SQL
      for (String name : getAllTables(catalog, schema, statement)) {
        Table table = new Table(name);
        String dropTableStr = table.sqlDropString(getHibernateDialect(), null, schema);
        script.add(dropTableStr);
      }
      
      // Now execute it all
      statement.close();
      statement = conn.createStatement();
      for (String sql : script) {
        //System.out.println("Executing \"" + sql + "\"");
        statement.executeUpdate(sql);
      }
      
      statement.close();
      statement = conn.createStatement();
      
      // Drop sequences SQL
      script.clear();
      for (String name : getAllSequences(catalog, schema, statement)) {
        final SequenceStructure sequenceStructure = new SequenceStructure(getHibernateDialect(), name, 0, 1, Long.class);
        String[] dropSequenceStrings = sequenceStructure.sqlDropStrings(getHibernateDialect());
        script.addAll(Arrays.asList(dropSequenceStrings));
      }
      
      //now execute drop sequence
      statement.close();
      statement = conn.createStatement();
      for (String sql : script) {
        //System.out.println("Executing \"" + sql + "\"");
        statement.executeUpdate(sql);
      }
      
      statement.close();
    
    } catch (SQLException e) {
      throw new OpenGammaRuntimeException("Failed to drop schema", e);
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException e) {
      }
    }
  }

  @Override
  public void executeSql(String catalog, String schema, String sql) {
    
    ArrayList<String> sqlStatements = new ArrayList<String>();
    
    for (String statement : sql.split(";")) {
      String[] lines = statement.split("\r\n|\r|\n");
      StringBuffer fixedSql = new StringBuffer();
      for (String line : lines) {
        String strippedLine = line;
        
        int commentIndex1 = line.indexOf("//"); 
        int commentIndex2 = line.indexOf("--");
        int firstCommentIndex = Integer.MAX_VALUE;
        if (commentIndex1 != -1) {
          firstCommentIndex = Math.min(firstCommentIndex, commentIndex1);
        }
        if (commentIndex2 != -1) {
          firstCommentIndex = Math.min(firstCommentIndex, commentIndex2);
        }
        if (firstCommentIndex != Integer.MAX_VALUE) {
          strippedLine = line.substring(0, firstCommentIndex);
        }
        
        fixedSql.append(strippedLine + " ");        
      }
      
      String fixedSqlStr = fixedSql.toString().trim();
      
      if (!fixedSqlStr.isEmpty()) {      
        sqlStatements.add(fixedSqlStr);
      }
    }
    
    Connection conn = null;
    try {
      conn = connect(catalog);
      setActiveSchema(conn, schema);
      
      Statement statement = conn.createStatement();
      for (String sqlStatement : sqlStatements) {
        try {
          statement.execute(sqlStatement);
        } catch (SQLException e) {
          throw new OpenGammaRuntimeException("Failed to execute statement (" + getDbHost() + ") " + sqlStatement, e);
        }
      }
      statement.close();
      
    } catch (SQLException e) {
      throw new OpenGammaRuntimeException("Failed to execute statement", e);
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException e) {
      }
    }
  }
  
  @Override
  public String describeDatabase(final String catalog) {
    final StringBuilder description = new StringBuilder();
    Connection conn = null;
    try {
      conn = connect(catalog);
      final Statement stmt = conn.createStatement();
      final List<String> schemas = getAllSchemas(catalog, stmt);
      Collections.sort(schemas);
      if (schemas.size() == 0) {
        schemas.add(null);
      }
      for (String schema : schemas) {
        description.append("schema: ").append(schema).append("\r\n");
        final List<String> tables = getAllTables(catalog, schema, stmt);
        Collections.sort(tables);
        for (String table : tables) {
          description.append("table: ").append(table).append("\r\n");
          final List<ColumnDefinition> columns = getAllColumns(catalog, schema, table, stmt);
          Collections.sort(columns);
          for (ColumnDefinition column : columns) {
            description.append("column: ").append(column).append("\r\n");
          }
        }
        final List<String> sequences = getAllSequences(catalog, schema, stmt);
        Collections.sort(sequences);
        for (String sequence : sequences) {
          description.append("sequence: ").append(sequence).append("\r\n");
        }
        final List<Pair<String, String>> foreignKeys = getAllForeignKeyConstraints(catalog, schema, stmt);
        Collections.sort(foreignKeys, FirstThenSecondPairComparator.INSTANCE);
        for (Pair<String, String> foreignKey : foreignKeys) {
          description.append("foreign key: ").append(foreignKey.getFirst()).append('.').append(foreignKey.getSecond()).append("\r\n");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println("e.getMessage: " + e.getMessage());
      throw new OpenGammaRuntimeException("SQL exception", e);
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException e) {
      }
    }
    return description.toString();
  }
  
}
