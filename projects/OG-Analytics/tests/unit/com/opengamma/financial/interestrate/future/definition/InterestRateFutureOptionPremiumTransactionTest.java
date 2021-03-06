/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.future.definition;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.instrument.future.InterestRateFutureSecurityDefinition;
import com.opengamma.financial.instrument.index.IborIndex;
import com.opengamma.financial.schedule.ScheduleCalculator;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtils;

import javax.time.calendar.Period;
import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.Test;

/**
 * Tests for the constructor of transaction on interest rate future options.
 */
public class InterestRateFutureOptionPremiumTransactionTest {
  //EURIBOR 3M Index
  private static final Period TENOR = Period.ofMonths(3);
  private static final int SETTLEMENT_DAYS = 2;
  private static final Calendar CALENDAR = new MondayToFridayCalendar("A");
  private static final DayCount DAY_COUNT_INDEX = DayCountFactory.INSTANCE.getDayCount("Actual/360");
  private static final BusinessDayConvention BUSINESS_DAY = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Modified Following");
  private static final boolean IS_EOM = true;
  private static final Currency CUR = Currency.USD;
  private static final IborIndex IBOR_INDEX = new IborIndex(CUR, TENOR, SETTLEMENT_DAYS, CALENDAR, DAY_COUNT_INDEX, BUSINESS_DAY, IS_EOM);
  // Future
  private static final ZonedDateTime SPOT_LAST_TRADING_DATE = DateUtils.getUTCDate(2012, 9, 19);
  private static final ZonedDateTime LAST_TRADING_DATE = ScheduleCalculator.getAdjustedDate(SPOT_LAST_TRADING_DATE, CALENDAR, -SETTLEMENT_DAYS);
  private static final double NOTIONAL = 1000000.0; // 1m
  private static final double FUTURE_FACTOR = 0.25;
  private static final double REFERENCE_PRICE = 0.0;
  private static final String NAME = "EDU2";
  private static final InterestRateFutureSecurityDefinition EDU2_DEFINITION = new InterestRateFutureSecurityDefinition(LAST_TRADING_DATE, IBOR_INDEX, REFERENCE_PRICE, NOTIONAL, FUTURE_FACTOR, NAME);
  private static final ZonedDateTime REFERENCE_DATE = DateUtils.getUTCDate(2010, 8, 18);
  private static final String DISCOUNTING_CURVE_NAME = "Funding";
  private static final String FORWARD_CURVE_NAME = "Forward";
  private static final String[] CURVES = {DISCOUNTING_CURVE_NAME, FORWARD_CURVE_NAME };
  private static final InterestRateFuture EDU2 = EDU2_DEFINITION.toDerivative(REFERENCE_DATE, CURVES);
  // Option 
  private static final ZonedDateTime EXPIRATION_DATE = DateUtils.getUTCDate(2011, 9, 16);
  private static final DayCount ACT_ACT = DayCountFactory.INSTANCE.getDayCount("Actual/Actual ISDA");
  private static final double EXPIRATION_TIME = ACT_ACT.getDayCountFraction(REFERENCE_DATE, EXPIRATION_DATE);
  private static final double STRIKE = 0.9850;
  private static final boolean IS_CALL = true;
  private static final InterestRateFutureOptionPremiumSecurity OPTION_EDU2 = new InterestRateFutureOptionPremiumSecurity(EDU2, EXPIRATION_TIME, STRIKE, IS_CALL);
  // Transaction
  private static final int QUANTITY = -123;
  private static final ZonedDateTime PREMIUM_DATE = DateUtils.getUTCDate(2011, 5, 12);
  private static final double PREMIUM_TIME = ACT_ACT.getDayCountFraction(PREMIUM_DATE, EXPIRATION_DATE);
  private static final double TRADE_PRICE = 0.0050;
  private static final InterestRateFutureOptionPremiumTransaction OPTION_TRANSACTION = new InterestRateFutureOptionPremiumTransaction(OPTION_EDU2, QUANTITY, PREMIUM_TIME, TRADE_PRICE);

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullUnderlying() {
    new InterestRateFutureOptionPremiumTransaction(null, QUANTITY, PREMIUM_TIME, TRADE_PRICE);
  }

  @Test
  public void getter() {
    assertEquals(OPTION_EDU2, OPTION_TRANSACTION.getUnderlyingOption());
    assertEquals(QUANTITY, OPTION_TRANSACTION.getQuantity());
    assertEquals(PREMIUM_TIME, OPTION_TRANSACTION.getPremium().getPaymentTime());
    assertEquals(-TRADE_PRICE * QUANTITY * NOTIONAL * EDU2.getPaymentAccrualFactor(), OPTION_TRANSACTION.getPremium().getAmount());
    assertEquals(TRADE_PRICE, OPTION_TRANSACTION.getTradePrice());
  }

  @Test
  /**
   * Tests the equal and hash code methods.
   */
  public void equalHash() {
    InterestRateFutureOptionPremiumTransaction newTransaction = new InterestRateFutureOptionPremiumTransaction(OPTION_EDU2, QUANTITY, PREMIUM_TIME, TRADE_PRICE);
    assertTrue(OPTION_TRANSACTION.equals(newTransaction));
    assertEquals(OPTION_TRANSACTION.hashCode(), newTransaction.hashCode());
    InterestRateFutureOptionPremiumTransaction modifiedTransaction;
    modifiedTransaction = new InterestRateFutureOptionPremiumTransaction(OPTION_EDU2, QUANTITY + 1, PREMIUM_TIME, TRADE_PRICE);
    assertFalse(OPTION_TRANSACTION.equals(modifiedTransaction));
    modifiedTransaction = new InterestRateFutureOptionPremiumTransaction(OPTION_EDU2, QUANTITY, PREMIUM_TIME - 0.1, TRADE_PRICE);
    assertFalse(OPTION_TRANSACTION.equals(modifiedTransaction));
    modifiedTransaction = new InterestRateFutureOptionPremiumTransaction(OPTION_EDU2, QUANTITY, PREMIUM_TIME, TRADE_PRICE + 0.001);
    assertFalse(OPTION_TRANSACTION.equals(modifiedTransaction));
  }

}
