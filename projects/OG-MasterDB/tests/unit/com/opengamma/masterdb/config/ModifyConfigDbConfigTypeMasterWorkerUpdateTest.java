/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.masterdb.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.TimeZone;

import javax.time.Instant;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;

import com.opengamma.DataNotFoundException;
import com.opengamma.id.Identifier;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.master.config.ConfigDocument;
import com.opengamma.master.config.ConfigHistoryRequest;
import com.opengamma.master.config.ConfigHistoryResult;

/**
 * Tests ModifyConfigDbConfigMasterWorker.
 */
public class ModifyConfigDbConfigTypeMasterWorkerUpdateTest extends AbstractDbConfigTypeMasterWorkerTest {
  // superclass sets up dummy database

  private static final Logger s_logger = LoggerFactory.getLogger(ModifyConfigDbConfigTypeMasterWorkerUpdateTest.class);

  public ModifyConfigDbConfigTypeMasterWorkerUpdateTest(String databaseType, String databaseVersion) {
    super(databaseType, databaseVersion);
    s_logger.info("running testcases for {}", databaseType);
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  //-------------------------------------------------------------------------
  @Test(expected = IllegalArgumentException.class)
  public void test_updateConfig_nullDocument() {
    _cfgMaster.update(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_update_noConfigId() {
    ConfigDocument<Identifier> doc = new ConfigDocument<Identifier>();
    doc.setName("Name");
    doc.setValue(Identifier.of("A", "B"));
    _cfgMaster.update(doc);
  }

  @Test(expected = DataNotFoundException.class)
  public void test_update_notFound() {
    UniqueIdentifier uid = UniqueIdentifier.of("DbCfg", "0", "0");
    ConfigDocument<Identifier> doc = new ConfigDocument<Identifier>();
    doc.setUniqueId(uid);
    doc.setName("Name");
    doc.setValue(Identifier.of("A", "B"));
    _cfgMaster.update(doc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_update_notLatestVersion() {
    UniqueIdentifier uid = UniqueIdentifier.of("DbCfg", "201", "0");
    ConfigDocument<Identifier> doc = new ConfigDocument<Identifier>();
    doc.setUniqueId(uid);
    doc.setName("Name");
    doc.setValue(Identifier.of("A", "B"));
    _cfgMaster.update(doc);
  }

  @Test
  public void test_update_getUpdateGet() {
    Instant now = Instant.now(_cfgMaster.getTimeSource());
    
    UniqueIdentifier uid = UniqueIdentifier.of("DbCfg", "101", "0");
    ConfigDocument<Identifier> base = _cfgMaster.get(uid);
    ConfigDocument<Identifier> input = new ConfigDocument<Identifier>();
    input.setUniqueId(uid);
    input.setName("Name");
    input.setValue(Identifier.of("A", "B"));
    
    ConfigDocument<Identifier> updated = _cfgMaster.update(input);
    assertEquals(false, base.getUniqueId().equals(updated.getUniqueId()));
    assertEquals(now, updated.getVersionFromInstant());
    assertEquals(null, updated.getVersionToInstant());
    assertEquals(now, updated.getCorrectionFromInstant());
    assertEquals(null, updated.getCorrectionToInstant());
    assertEquals(input.getValue(), updated.getValue());
    
    ConfigDocument<Identifier> old = _cfgMaster.get(uid);
    assertEquals(base.getUniqueId(), old.getUniqueId());
    assertEquals(base.getVersionFromInstant(), old.getVersionFromInstant());
    assertEquals(now, old.getVersionToInstant());  // old version ended
    assertEquals(base.getCorrectionFromInstant(), old.getCorrectionFromInstant());
    assertEquals(base.getCorrectionToInstant(), old.getCorrectionToInstant());
    assertEquals(base.getValue(), old.getValue());
    
    ConfigHistoryRequest search = new ConfigHistoryRequest(base.getUniqueId(), null, now);
    ConfigHistoryResult<Identifier> searchResult = _cfgMaster.history(search);
    assertEquals(2, searchResult.getDocuments().size());
  }

  @Test
  public void test_update_rollback() {
    DbConfigTypeMaster<Identifier> w = new DbConfigTypeMaster<Identifier>(Identifier.class, _cfgMaster.getDbSource()) {
      @Override
      protected String sqlInsertConfig() {
        return "INSERT";  // bad sql
      }
    };
    final ConfigDocument<Identifier> base = _cfgMaster.get(UniqueIdentifier.of("DbCfg", "101", "0"));
    UniqueIdentifier uid = UniqueIdentifier.of("DbCfg", "101", "0");
    ConfigDocument<Identifier> input = new ConfigDocument<Identifier>();
    input.setUniqueId(uid);
    input.setName("Name");
    input.setValue(Identifier.of("A", "B"));
    try {
      w.update(input);
      fail();
    } catch (BadSqlGrammarException ex) {
      // expected
    }
    final ConfigDocument<Identifier> test = _cfgMaster.get(UniqueIdentifier.of("DbCfg", "101", "0"));
    
    assertEquals(base, test);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_toString() {
    assertEquals(_cfgMaster.getClass().getSimpleName() + "[DbCfg]", _cfgMaster.toString());
  }

}