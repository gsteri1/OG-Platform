/**
 * Copyright (C) 2009 - 2011 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.fixedincome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.opengamma.core.common.Currency;
import com.opengamma.financial.analytics.DoubleLabelledMatrix1D;

/**
 * 
 */
public class YieldCurveNodeSensitivityDataBundleTest {
  private static final Double[] T = new Double[] {1., 2., 3., 4.};
  private static final Object[] LABELS = new Object[] {"1Y", "2Y", "3Y", "4Y"};
  private static final double[] X = new double[] {5, 6, 7, 8};
  private static final DoubleLabelledMatrix1D M = new DoubleLabelledMatrix1D(T, X);
  private static final Currency CCY = Currency.getInstance("USD");
  private static final String NAME = "SINGLE";

  @Test(expected = IllegalArgumentException.class)
  public void testNullMatrix() {
    new YieldCurveNodeSensitivityDataBundle(null, CCY, NAME);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurrency() {
    new YieldCurveNodeSensitivityDataBundle(M, null, NAME);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullName() {
    new YieldCurveNodeSensitivityDataBundle(M, CCY, null);
  }

  @Test
  public void test() {
    final YieldCurveNodeSensitivityDataBundle data = new YieldCurveNodeSensitivityDataBundle(M, CCY, NAME);
    YieldCurveNodeSensitivityDataBundle other = new YieldCurveNodeSensitivityDataBundle(M, CCY, NAME);
    assertEquals(data, other);
    assertEquals(data.hashCode(), other.hashCode());
    assertEquals(data.getCurrency(), CCY);
    assertEquals(data.getLabelledMatrix(), M);
    assertEquals(data.getYieldCurveName(), NAME);
    other = new YieldCurveNodeSensitivityDataBundle(new DoubleLabelledMatrix1D(T, LABELS, X), CCY, NAME);
    assertFalse(other.equals(data));
    other = new YieldCurveNodeSensitivityDataBundle(M, Currency.getInstance("GBP"), NAME);
    assertFalse(other.equals(data));
    other = new YieldCurveNodeSensitivityDataBundle(M, CCY, "PPP");
    assertFalse(other.equals(data));
  }
}