/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.util.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.ZipUtils;
import com.opengamma.util.db.DbHelper;
import com.opengamma.util.db.DbSource;
import com.opengamma.util.db.HSQLDbHelper;
import com.opengamma.util.db.PostgreSQLDbHelper;
import com.opengamma.util.test.DBTool.TableCreationCallback;
import com.opengamma.util.time.DateUtils;

/**
 * Base DB test.
 */
public abstract class DBTest implements TableCreationCallback {

  private static final Logger s_logger = LoggerFactory.getLogger(DBTest.class);
  private static Map<String,String> s_databaseTypeVersion = new HashMap<String,String> ();
  private static final Map<String, DbHelper> s_dbHelpers = new HashMap<String, DbHelper>();

  private static final File SCRIPT_INSTALL_DIR = new File(DBTool.getWorkingDirectory(), "temp/" + DBTest.class.getSimpleName());

  static {
    DateUtils.initTimeZone();
    addDbHelper("hsqldb", new HSQLDbHelper());
    addDbHelper("postgres", new PostgreSQLDbHelper());
  }

  static {
    try {
      extractSQLScript();
    } catch (IOException ex) {
      s_logger.warn("problem with unzip publish sql script to {} rethrowing", SCRIPT_INSTALL_DIR, ex);
      throw new OpenGammaRuntimeException("problem with unzip publish sql script to " + SCRIPT_INSTALL_DIR, ex);
    }
  }

  private final String _databaseType;
  private final String _databaseVersion;
  private final DBTool _dbtool;

  protected DBTest(String databaseType, String databaseVersion) {
    ArgumentChecker.notNull(databaseType, "databaseType");
    _databaseType = databaseType;
    _dbtool = TestProperties.getDbTool(databaseType);
    _dbtool.setJdbcUrl(getDbTool().getTestDatabaseUrl());
    _databaseVersion = databaseVersion;
    if (isScriptPublished()) {
      _dbtool.addDbScriptDirectory(SCRIPT_INSTALL_DIR.getAbsolutePath());
    } else {
      _dbtool.addDbScriptDirectory(DBTool.getWorkingDirectory());
    }
  }

  /**
   * Initialise the database to the required version. This tracks the last initialised version
   * in a static map to avoid duplicate DB operations on bigger test classes. This might not be
   * such a good idea.
   */
  @BeforeMethod
  public void setUp() throws Exception {
    String prevVersion = s_databaseTypeVersion.get(getDatabaseType());
    if ((prevVersion == null) || !prevVersion.equals(getDatabaseVersion())) {
      s_databaseTypeVersion.put(getDatabaseType(), getDatabaseVersion());
      _dbtool.setCreateVersion(getDatabaseVersion());
      _dbtool.dropTestSchema();
      _dbtool.createTestSchema();
      _dbtool.createTestTables(this);
    }
    _dbtool.clearTestTables();
  }

  private static void extractSQLScript() throws IOException {
    if (isScriptPublished()) {
      cleanUp();
      unzipSQLScripts();
    }
  }

  private static String getZipPath() {
    return TestProperties.getTestProperties().getProperty("test.sqlzip.path");
  }

  @SuppressWarnings("unchecked")
  private static void unzipSQLScripts() throws IOException {
    File zipScriptPath = new File(DBTool.getWorkingDirectory(), getZipPath());
    for (File file : (Collection<File>) FileUtils.listFiles(zipScriptPath, new String[] {"zip" }, false)) {
      ZipUtils.unzipArchive(file, SCRIPT_INSTALL_DIR);
    }
  }

  @AfterMethod
  public void tearDown() throws Exception {
    _dbtool.resetTestCatalog(); // avoids locking issues with Derby
  }

  @AfterSuite
  public static void cleanUp() {
    FileUtils.deleteQuietly(SCRIPT_INSTALL_DIR);
  }

  //-------------------------------------------------------------------------
  public static Collection<Object[]> getParameters() {
    int previousVersionCount = getPreviousVersionCount();
    return getParameters(previousVersionCount);
  }

  protected static Collection<Object[]> getParameters(final int previousVersionCount) {
    String databaseType = System.getProperty("test.database.type");
    if (databaseType == null) {
      databaseType = "all";
    }
    return getParameters(databaseType, previousVersionCount);
  }

  protected static Collection<Object[]> getParameters(final String databaseType, final int previousVersionCount) {
    ArrayList<Object[]> returnValue = new ArrayList<Object[]>();
    for (String db : TestProperties.getDatabaseTypes(databaseType)) {
      final DBTool dbTool = TestProperties.getDbTool(db);
      if (isScriptPublished()) {
        dbTool.addDbScriptDirectory(SCRIPT_INSTALL_DIR.getAbsolutePath());
      } else {
        dbTool.addDbScriptDirectory(DBTool.getWorkingDirectory());
      }
      final String[] versions = dbTool.getDatabaseCreatableVersions();
      for (int i = 0; i < versions.length; i++) {
        returnValue.add(new Object[] {db, versions[i] });
        if (i >= previousVersionCount) {
          break;
        }
      }
    }
    return returnValue;
  }

  private static boolean isScriptPublished() {
    String zipPath = getZipPath();
    if (zipPath == null) {
      throw new OpenGammaRuntimeException("missing test.sqlZip.path property in test properties file");
    }
    File zipScriptPath = new File(DBTool.getWorkingDirectory(), zipPath);
    boolean result = false;
    if (zipScriptPath.exists()) {
      @SuppressWarnings("rawtypes")
      Collection zipfiles = FileUtils.listFiles(zipScriptPath, new String[] {"zip" }, false);
      result = !zipfiles.isEmpty();
    }
    return result;
  }

  //-------------------------------------------------------------------------
  @DataProvider(name = "localDatabase")
  public static Object[][] data_localDatabase() {
    Collection<Object[]> parameters = getParameters("hsqldb", 0);
    Object[][] array = new Object[parameters.size()][];
    parameters.toArray(array);
    return array;
  }

  @DataProvider(name = "databases")
  public static Object[][] data_databases() {
    Collection<Object[]> parameters = getParameters();
    Object[][] array = new Object[parameters.size()][];
    parameters.toArray(array);
    return array;
  }

  @DataProvider(name = "databasesMoreVersions")
  public static Object[][] data_databasesMoreVersions() {
    Collection<Object[]> parameters = getParameters(3);
    Object[][] array = new Object[parameters.size()][];
    parameters.toArray(array);
    return array;
  }

  protected static int getPreviousVersionCount() {
    String previousVersionCountString = System.getProperty("test.database.previousVersions");
    int previousVersionCount;
    if (previousVersionCountString == null) {
      previousVersionCount = 0; // If you run from Eclipse, use current version only
    } else {
      previousVersionCount = Integer.parseInt(previousVersionCountString);
    }
    return previousVersionCount;
  }

  public DBTool getDbTool() {
    return _dbtool;
  }

  public String getDatabaseType() {
    return _databaseType;
  }

  public String getDatabaseVersion() {
    return _databaseVersion;
  }

  public DataSourceTransactionManager getTransactionManager() {
    return getDbTool().getTransactionManager();
  }

  public DbSource getDbSource() {
    DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    transactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    
    DbHelper dbHelper = s_dbHelpers.get(getDatabaseType());
    if (dbHelper == null) {
      throw new OpenGammaRuntimeException("config error - no DBHelper setup for " + getDatabaseType());
    }

    DbSource dbSource = new DbSource("DBTest", getTransactionManager().getDataSource(),
        dbHelper, null, transactionDefinition, getTransactionManager());
    return dbSource;
  }

  public static void addDbHelper(String dbType, DbHelper helper) {
    s_dbHelpers.put(dbType, helper);
  }

  /**
   * Override this if you wish to do something with the database while it is in its "upgrading" state - e.g. populate with test data
   * at a particular version to test the data transformations on the next version upgrades.
   */
  public void tablesCreatedOrUpgraded(final String version) {
    // No action 
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getDatabaseType() + ":" + getDatabaseVersion();
  }

}
