/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.future.definition;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import javax.time.calendar.Period;
import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.Test;

import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.convention.yield.YieldConvention;
import com.opengamma.financial.convention.yield.YieldConventionFactory;
import com.opengamma.financial.instrument.bond.BondFixedSecurityDefinition;
import com.opengamma.financial.interestrate.bond.definition.BondFixedSecurity;
import com.opengamma.financial.schedule.ScheduleCalculator;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtils;

/**
 * Tests related to bond futures security Derivative construction.
 */
public class BondFutureSecurityTest {
  // 5-Year U.S. Treasury Note Futures: FVU1
  private static final Currency CUR = Currency.USD;
  private static final Period PAYMENT_TENOR = Period.ofMonths(6);
  private static final Calendar CALENDAR = new MondayToFridayCalendar("A");
  private static final DayCount DAY_COUNT = DayCountFactory.INSTANCE.getDayCount("Actual/Actual ISDA");
  private static final BusinessDayConvention BUSINESS_DAY = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Following");
  private static final boolean IS_EOM = false;
  private static final int SETTLEMENT_DAYS = 1;
  private static final YieldConvention YIELD_CONVENTION = YieldConventionFactory.INSTANCE.getYieldConvention("STREET CONVENTION");
  private static final int NB_BOND = 7;
  private static final Period[] BOND_TENOR = new Period[] {Period.ofYears(5), Period.ofYears(5), Period.ofYears(5), Period.ofYears(8), Period.ofYears(5), Period.ofYears(5), Period.ofYears(5)};
  private static final ZonedDateTime[] START_ACCRUAL_DATE = new ZonedDateTime[] {DateUtils.getUTCDate(2010, 11, 30), DateUtils.getUTCDate(2010, 12, 31), DateUtils.getUTCDate(2011, 1, 31),
      DateUtils.getUTCDate(2008, 2, 29), DateUtils.getUTCDate(2011, 3, 31), DateUtils.getUTCDate(2011, 4, 30), DateUtils.getUTCDate(2011, 5, 31)};
  private static final double[] RATE = new double[] {0.01375, 0.02125, 0.0200, 0.02125, 0.0225, 0.0200, 0.0175};
  private static final double[] CONVERSION_FACTOR = new double[] {.8317, .8565, .8493, .8516, .8540, .8417, .8292};
  private static final ZonedDateTime[] MATURITY_DATE = new ZonedDateTime[NB_BOND];
  private static final BondFixedSecurityDefinition[] BASKET_DEFINITION = new BondFixedSecurityDefinition[NB_BOND];
  static {
    for (int loopbasket = 0; loopbasket < NB_BOND; loopbasket++) {
      MATURITY_DATE[loopbasket] = START_ACCRUAL_DATE[loopbasket].plus(BOND_TENOR[loopbasket]);
      BASKET_DEFINITION[loopbasket] = BondFixedSecurityDefinition.from(CUR, MATURITY_DATE[loopbasket], START_ACCRUAL_DATE[loopbasket], PAYMENT_TENOR, RATE[loopbasket], SETTLEMENT_DAYS, CALENDAR,
          DAY_COUNT, BUSINESS_DAY, YIELD_CONVENTION, IS_EOM);
    }
  }
  private static final ZonedDateTime LAST_TRADING_DATE = DateUtils.getUTCDate(2011, 9, 21);
  private static final ZonedDateTime FIRST_NOTICE_DATE = DateUtils.getUTCDate(2011, 8, 31);
  private static final ZonedDateTime LAST_NOTICE_DATE = DateUtils.getUTCDate(2011, 9, 29);
  private static final ZonedDateTime FIRST_DELIVERY_DATE = ScheduleCalculator.getAdjustedDate(FIRST_NOTICE_DATE, CALENDAR, SETTLEMENT_DAYS);
  private static final ZonedDateTime LAST_DELIVERY_DATE = ScheduleCalculator.getAdjustedDate(LAST_NOTICE_DATE, CALENDAR, SETTLEMENT_DAYS);
  private static final double NOTIONAL = 100000;

  private static final ZonedDateTime REFERENCE_DATE = DateUtils.getUTCDate(2011, 6, 20);
  private static final DayCount ACT_ACT = DayCountFactory.INSTANCE.getDayCount("Actual/Actual ISDA");
  private static final double LAST_TRADING_TIME = ACT_ACT.getDayCountFraction(REFERENCE_DATE, LAST_TRADING_DATE);
  private static final double FIRST_NOTICE_TIME = ACT_ACT.getDayCountFraction(REFERENCE_DATE, FIRST_NOTICE_DATE);
  private static final double LAST_NOTICE_TIME = ACT_ACT.getDayCountFraction(REFERENCE_DATE, LAST_NOTICE_DATE);
  private static final double FIRST_DELIVERY_TIME = ACT_ACT.getDayCountFraction(REFERENCE_DATE, FIRST_DELIVERY_DATE);
  private static final double LAST_DELIVERY_TIME = ACT_ACT.getDayCountFraction(REFERENCE_DATE, LAST_DELIVERY_DATE);
  private static final String CREDIT_CURVE_NAME = "Credit";
  private static final String REPO_CURVE_NAME = "Repo";
  private static final String[] CURVES_NAME = {CREDIT_CURVE_NAME, REPO_CURVE_NAME};
  private static final BondFixedSecurity[] BASKET = new BondFixedSecurity[NB_BOND];
  static {
    for (int loopbasket = 0; loopbasket < NB_BOND; loopbasket++) {
      BASKET[loopbasket] = BASKET_DEFINITION[loopbasket].toDerivative(REFERENCE_DATE, LAST_DELIVERY_DATE, CURVES_NAME);
    }
  }

  private static final BondFutureSecurity BOND_FUTURE_SECURITY = new BondFutureSecurity(LAST_TRADING_TIME, FIRST_NOTICE_TIME, LAST_NOTICE_TIME, FIRST_DELIVERY_TIME, LAST_DELIVERY_TIME, NOTIONAL,
      BASKET, CONVERSION_FACTOR);

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullBasket() {
    new BondFutureSecurity(LAST_TRADING_TIME, FIRST_NOTICE_TIME, LAST_NOTICE_TIME, FIRST_DELIVERY_TIME, LAST_DELIVERY_TIME, NOTIONAL, null, CONVERSION_FACTOR);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullConversion() {
    new BondFutureSecurity(LAST_TRADING_TIME, FIRST_NOTICE_TIME, LAST_NOTICE_TIME, FIRST_DELIVERY_TIME, LAST_DELIVERY_TIME, NOTIONAL, BASKET, null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void zeroBasket() {
    new BondFutureSecurity(LAST_TRADING_TIME, FIRST_NOTICE_TIME, LAST_NOTICE_TIME, FIRST_DELIVERY_TIME, LAST_DELIVERY_TIME, NOTIONAL, new BondFixedSecurity[0], CONVERSION_FACTOR);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void sizeConversionFactor() {
    final double[] incorrectConversionFactor = new double[NB_BOND - 1];
    new BondFutureSecurity(LAST_TRADING_TIME, FIRST_NOTICE_TIME, LAST_NOTICE_TIME, FIRST_DELIVERY_TIME, LAST_DELIVERY_TIME, NOTIONAL, BASKET, incorrectConversionFactor);
  }

  @Test
  public void getter() {
    assertEquals("Bond future security derivative: last trading date", LAST_TRADING_TIME, BOND_FUTURE_SECURITY.getTradingLastTime());
    assertEquals("Bond future security derivative: first notice date", FIRST_NOTICE_TIME, BOND_FUTURE_SECURITY.getNoticeFirstTime());
    assertEquals("Bond future security derivative: last notice date", LAST_NOTICE_TIME, BOND_FUTURE_SECURITY.getNoticeLastTime());
    assertEquals("Bond future security derivative: first delivery date", FIRST_DELIVERY_TIME, BOND_FUTURE_SECURITY.getDeliveryFirstTime());
    assertEquals("Bond future security derivative: last delivery date", LAST_DELIVERY_TIME, BOND_FUTURE_SECURITY.getDeliveryLastTime());
    assertEquals("Bond future security derivative: basket", BASKET, BOND_FUTURE_SECURITY.getDeliveryBasket());
    assertEquals("Bond future security derivative: conversion factor", CONVERSION_FACTOR, BOND_FUTURE_SECURITY.getConversionFactor());
    assertEquals("Bond future security derivative: notional", NOTIONAL, BOND_FUTURE_SECURITY.getNotional());
    assertEquals("Bond future security derivative: currency", CUR, BOND_FUTURE_SECURITY.getCurrency());
  }

  @Test
  /**
   * Tests the equal and hashCode methods.
   */
  public void equalHash() {
    assertTrue(BOND_FUTURE_SECURITY.equals(BOND_FUTURE_SECURITY));
    BondFutureSecurity other = new BondFutureSecurity(LAST_TRADING_TIME, FIRST_NOTICE_TIME, LAST_NOTICE_TIME, FIRST_DELIVERY_TIME, LAST_DELIVERY_TIME, NOTIONAL, BASKET, CONVERSION_FACTOR);
    assertTrue(BOND_FUTURE_SECURITY.equals(other));
    assertTrue(BOND_FUTURE_SECURITY.hashCode() == other.hashCode());
    BondFutureSecurity modifiedFuture;
    modifiedFuture = new BondFutureSecurity(LAST_TRADING_TIME + 0.1, FIRST_NOTICE_TIME, LAST_NOTICE_TIME, FIRST_DELIVERY_TIME, LAST_DELIVERY_TIME, NOTIONAL, BASKET, CONVERSION_FACTOR);
    assertFalse(BOND_FUTURE_SECURITY.equals(modifiedFuture));
    modifiedFuture = new BondFutureSecurity(LAST_TRADING_TIME, FIRST_NOTICE_TIME + 0.1, LAST_NOTICE_TIME, FIRST_DELIVERY_TIME, LAST_DELIVERY_TIME, NOTIONAL, BASKET, CONVERSION_FACTOR);
    assertFalse(BOND_FUTURE_SECURITY.equals(modifiedFuture));
    modifiedFuture = new BondFutureSecurity(LAST_TRADING_TIME, FIRST_NOTICE_TIME, LAST_NOTICE_TIME + 0.1, FIRST_DELIVERY_TIME, LAST_DELIVERY_TIME, NOTIONAL, BASKET, CONVERSION_FACTOR);
    assertFalse(BOND_FUTURE_SECURITY.equals(modifiedFuture));
    modifiedFuture = new BondFutureSecurity(LAST_TRADING_TIME, FIRST_NOTICE_TIME, LAST_NOTICE_TIME, FIRST_DELIVERY_TIME + 0.1, LAST_DELIVERY_TIME, NOTIONAL, BASKET, CONVERSION_FACTOR);
    assertFalse(BOND_FUTURE_SECURITY.equals(modifiedFuture));
    modifiedFuture = new BondFutureSecurity(LAST_TRADING_TIME, FIRST_NOTICE_TIME, LAST_NOTICE_TIME, FIRST_DELIVERY_TIME, LAST_DELIVERY_TIME + 0.1, NOTIONAL, BASKET, CONVERSION_FACTOR);
    assertFalse(BOND_FUTURE_SECURITY.equals(modifiedFuture));
    modifiedFuture = new BondFutureSecurity(LAST_TRADING_TIME, FIRST_NOTICE_TIME, LAST_NOTICE_TIME, FIRST_DELIVERY_TIME, LAST_DELIVERY_TIME, NOTIONAL + 100000, BASKET, CONVERSION_FACTOR);
    assertFalse(BOND_FUTURE_SECURITY.equals(modifiedFuture));
    BondFixedSecurity[] otherBasket = new BondFixedSecurity[NB_BOND];
    for (int loopbasket = 0; loopbasket < NB_BOND; loopbasket++) {
      otherBasket[loopbasket] = BASKET_DEFINITION[loopbasket].toDerivative(REFERENCE_DATE, LAST_NOTICE_DATE, CURVES_NAME);
    }
    modifiedFuture = new BondFutureSecurity(LAST_TRADING_TIME, FIRST_NOTICE_TIME, LAST_NOTICE_TIME, FIRST_DELIVERY_TIME, LAST_DELIVERY_TIME, NOTIONAL, otherBasket, CONVERSION_FACTOR);
    assertFalse(BOND_FUTURE_SECURITY.equals(modifiedFuture));
    double[] otherConversionFactor = new double[] {.9000, .8565, .8493, .8516, .8540, .8417, .8292};
    modifiedFuture = new BondFutureSecurity(LAST_TRADING_TIME, FIRST_NOTICE_TIME, LAST_NOTICE_TIME, FIRST_DELIVERY_TIME, LAST_DELIVERY_TIME, NOTIONAL, BASKET, otherConversionFactor);
    assertFalse(BOND_FUTURE_SECURITY.equals(modifiedFuture));
    assertFalse(BOND_FUTURE_SECURITY.equals(LAST_TRADING_DATE));
    assertFalse(BOND_FUTURE_SECURITY.equals(null));
  }

}
