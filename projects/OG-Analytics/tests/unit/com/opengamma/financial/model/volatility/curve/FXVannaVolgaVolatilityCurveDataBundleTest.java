/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.volatility.curve;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.util.time.DateUtil;

/**
 * 
 */
public class FXVannaVolgaVolatilityCurveDataBundleTest {
  private static final double DELTA = 0.1;
  private static final double RR = 0.01;
  private static final double ATM = 0.2;
  private static final double VWB = 0.05;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 7, 1);
  private static final FXVannaVolgaVolatilityCurveDataBundle DATA = new FXVannaVolgaVolatilityCurveDataBundle(DELTA, RR, ATM, VWB, DATE);

  @Test(expected = IllegalArgumentException.class)
  public void testNullMaturity() {
    new FXVannaVolgaVolatilityCurveDataBundle(DELTA, RR, ATM, VWB, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeATMVol() {
    new FXVannaVolgaVolatilityCurveDataBundle(DELTA, RR, -ATM, VWB, DATE);
  }

  @Test
  public void testGetters() {
    assertEquals(ATM, DATA.getAtTheMoney(), 0);
    assertEquals(DELTA, DATA.getDelta(), 0);
    assertEquals(DATE, DATA.getMaturity());
    assertEquals(RR, DATA.getRiskReversal(), 0);
    assertEquals(VWB, DATA.getVegaWeightedButterfly(), 0);
  }

  @Test
  public void testEqualsAndHashCode() {
    FXVannaVolgaVolatilityCurveDataBundle other = new FXVannaVolgaVolatilityCurveDataBundle(DELTA, RR, ATM, VWB, DATE);
    assertEquals(DATA, other);
    assertEquals(DATA.hashCode(), other.hashCode());
    other = new FXVannaVolgaVolatilityCurveDataBundle(-DELTA, RR, ATM, VWB, DATE);
    assertFalse(other.equals(DATA));
    other = new FXVannaVolgaVolatilityCurveDataBundle(DELTA, -RR, ATM, VWB, DATE);
    assertFalse(other.equals(DATA));
    other = new FXVannaVolgaVolatilityCurveDataBundle(DELTA, RR, ATM + 1, VWB, DATE);
    assertFalse(other.equals(DATA));
    other = new FXVannaVolgaVolatilityCurveDataBundle(DELTA, RR, ATM, -VWB, DATE);
    assertFalse(other.equals(DATA));
    other = new FXVannaVolgaVolatilityCurveDataBundle(DELTA, RR, ATM, VWB, DateUtil.getDateOffsetWithYearFraction(DATE, 1));
    assertFalse(other.equals(DATA));
  }
}