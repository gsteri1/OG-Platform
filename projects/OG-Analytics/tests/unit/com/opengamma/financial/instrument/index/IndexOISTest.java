/**
 * Copyright (C) 2011 - present by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.instrument.index;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.Test;

import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.util.money.Currency;

/**
 * Tests the IndexOIS class.
 */
public class IndexOISTest {

  // USD OIS
  private static final String US_OIS_NAME = "US OIS";
  private static final Currency US_CUR = Currency.USD;
  private static final DayCount US_DAY_COUNT = DayCountFactory.INSTANCE.getDayCount("Actual/360");
  private static final int US_PUBLICATION_LAG = 1;
  private static final Calendar US_CALENDAR = new MondayToFridayCalendar("A");
  private static final IndexOIS US_OIS = new IndexOIS(US_OIS_NAME, US_CUR, US_DAY_COUNT, US_PUBLICATION_LAG, US_CALENDAR);
  //EUR Eonia
  private static final String EUR_OIS_NAME = "EUR EONIA";
  private static final Currency EUR_CUR = Currency.EUR;
  private static final Calendar EUR_CALENDAR = new MondayToFridayCalendar("EUR");
  private static final int EUR_PUBLICATION_LAG = 0;
  private static final DayCount EUR_DAY_COUNT = DayCountFactory.INSTANCE.getDayCount("Actual/360");
  private static final IndexOIS EUR_OIS = new IndexOIS(EUR_OIS_NAME, EUR_CUR, EUR_DAY_COUNT, EUR_PUBLICATION_LAG, EUR_CALENDAR);

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullName() {
    new IndexOIS(null, US_CUR, US_DAY_COUNT, US_PUBLICATION_LAG, US_CALENDAR);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullCurrency() {
    new IndexOIS(US_OIS_NAME, null, US_DAY_COUNT, US_PUBLICATION_LAG, US_CALENDAR);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullDayCount() {
    new IndexOIS(US_OIS_NAME, US_CUR, null, US_PUBLICATION_LAG, US_CALENDAR);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullCalendar() {
    new IndexOIS(US_OIS_NAME, US_CUR, US_DAY_COUNT, US_PUBLICATION_LAG, null);
  }

  @Test
  public void getterUS() {
    assertEquals(US_OIS.getName(), US_OIS_NAME);
    assertEquals(US_OIS.getCurrency(), US_CUR);
    assertEquals(US_OIS.getDayCount(), US_DAY_COUNT);
    assertEquals(US_OIS.getPublicationLag(), US_PUBLICATION_LAG);
    assertEquals(US_OIS.getCalendar(), US_CALENDAR);
  }

  @Test
  public void getterEUR() {
    assertEquals(EUR_OIS.getName(), EUR_OIS_NAME);
    assertEquals(EUR_OIS.getCurrency(), EUR_CUR);
    assertEquals(EUR_OIS.getDayCount(), EUR_DAY_COUNT);
    assertEquals(EUR_OIS.getPublicationLag(), EUR_PUBLICATION_LAG);
    assertEquals(EUR_OIS.getCalendar(), EUR_CALENDAR);
  }

  @Test
  /**
   * Tests the equal and hashCode methods.
   */
  public void equalHash() {
    assertEquals("OIS Index: equal/hash code", US_OIS, US_OIS);
    assertFalse("OIS Index: equal/hash code", US_OIS.equals(EUR_OIS));
    IndexOIS other = new IndexOIS(US_OIS_NAME, US_CUR, US_DAY_COUNT, US_PUBLICATION_LAG, US_CALENDAR);
    assertEquals("OIS Index: equal/hash code", US_OIS, other);
    assertEquals("OIS Index: equal/hash code", US_OIS.hashCode(), other.hashCode());
    IndexOIS modified;
    modified = new IndexOIS("test", US_CUR, US_DAY_COUNT, US_PUBLICATION_LAG, US_CALENDAR);
    assertFalse("OIS Index: equal/hash code", US_OIS.equals(modified));
    modified = new IndexOIS(US_OIS_NAME, EUR_CUR, US_DAY_COUNT, US_PUBLICATION_LAG, US_CALENDAR);
    assertFalse("OIS Index: equal/hash code", US_OIS.equals(modified));
    modified = new IndexOIS(US_OIS_NAME, US_CUR, DayCountFactory.INSTANCE.getDayCount("Actual/365"), US_PUBLICATION_LAG, US_CALENDAR);
    assertFalse("OIS Index: equal/hash code", US_OIS.equals(modified));
    modified = new IndexOIS(US_OIS_NAME, US_CUR, US_DAY_COUNT, 12, US_CALENDAR);
    assertFalse("OIS Index: equal/hash code", US_OIS.equals(modified));
    modified = new IndexOIS(US_OIS_NAME, US_CUR, US_DAY_COUNT, US_PUBLICATION_LAG, EUR_CALENDAR);
    assertFalse("OIS Index: equal/hash code", US_OIS.equals(modified));
  }

}
