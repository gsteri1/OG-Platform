/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.masterdb.position;

import static org.testng.AssertJUnit.assertEquals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import com.opengamma.DataNotFoundException;
import com.opengamma.id.UniqueId;
import com.opengamma.master.position.PositionDocument;
import com.opengamma.util.test.DBTest;

/**
 * Tests QueryPositionDbPositionMasterWorker.
 */
public class QueryPositionDbPositionMasterWorkerGetTest extends AbstractDbPositionMasterWorkerTest {
  // superclass sets up dummy database

  private static final Logger s_logger = LoggerFactory.getLogger(QueryPositionDbPositionMasterWorkerGetTest.class);

  @Factory(dataProvider = "databases", dataProviderClass = DBTest.class)
  public QueryPositionDbPositionMasterWorkerGetTest(String databaseType, String databaseVersion) {
    super(databaseType, databaseVersion);
    s_logger.info("running testcases for {}", databaseType);
  }

  //-------------------------------------------------------------------------
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_get_nullUID() {
    _posMaster.get(null);
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void test_get_versioned_notFoundId() {
    UniqueId uniqueId = UniqueId.of("DbPos", "0", "0");
    _posMaster.get(uniqueId);
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void test_get_versioned_notFoundVersion() {
    UniqueId uniqueId = UniqueId.of("DbPos", "121", "1");
    _posMaster.get(uniqueId);
  }

  @Test
  public void test_getPosition_versioned_oneSecurityKey() {
    UniqueId uniqueId = UniqueId.of("DbPos", "122", "0");
    PositionDocument test = _posMaster.get(uniqueId);
    assert122(test);
  }

  @Test
  public void test_getPosition_versioned_twoSecurityKeys() {
    UniqueId uniqueId = UniqueId.of("DbPos", "121", "0");
    PositionDocument test = _posMaster.get(uniqueId);
    assert121(test);
  }

  @Test
  public void test_getPosition_versioned_notLatest() {
    UniqueId uniqueId = UniqueId.of("DbPos", "221", "0");
    PositionDocument test = _posMaster.get(uniqueId);
    assert221(test);
  }

  @Test
  public void test_getPosition_versioned_latest() {
    UniqueId uniqueId = UniqueId.of("DbPos", "221", "1");
    PositionDocument test = _posMaster.get(uniqueId);
    assert222(test);
  }

  //-------------------------------------------------------------------------
  @Test(expectedExceptions = DataNotFoundException.class)
  public void test_getPosition_unversioned_notFound() {
    UniqueId uniqueId = UniqueId.of("DbPos", "0");
    _posMaster.get(uniqueId);
  }

  @Test
  public void test_getPosition_unversioned() {
    UniqueId oid = UniqueId.of("DbPos", "221");
    PositionDocument test = _posMaster.get(oid);
    assert222(test);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_toString() {
    assertEquals(_posMaster.getClass().getSimpleName() + "[DbPos]", _posMaster.toString());
  }

}
