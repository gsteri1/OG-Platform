/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.util.db;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

/**
 * Test DerbyDbHelper.
 */
@Test
public class DerbyDbHelperTest extends DbHelperTest {

  public DerbyDbHelperTest() {
    _helper = DerbyDbHelper.INSTANCE;
  }

  //-------------------------------------------------------------------------
  public void test_getJDBCDriver() {
    assertEquals(org.apache.derby.jdbc.EmbeddedDriver.class, _helper.getJDBCDriverClass());
  }

  public void test_getHibernateDialect() {
    assertEquals(org.hibernate.dialect.DerbyDialect.class, _helper.getHibernateDialect().getClass());
  }

  public void test_getName() {
    assertEquals("Derby", _helper.getName());
  }

  //-------------------------------------------------------------------------
  public void test_sqlNextSequenceValueSelect() {
    assertEquals("SELECT NEXT VALUE FOR MySeq FROM sysibm.sysdummy1", _helper.sqlNextSequenceValueSelect("MySeq"));
  }

}
