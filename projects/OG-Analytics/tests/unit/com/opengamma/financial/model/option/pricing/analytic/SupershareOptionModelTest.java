/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic;

import static org.junit.Assert.assertEquals;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.option.definition.SupershareOptionDefinition;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.financial.model.volatility.surface.VolatilitySurface;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class SupershareOptionModelTest {
  private static final YieldAndDiscountCurve CURVE = new ConstantYieldCurve(0.1);
  private static final double B = 0.;
  private static final VolatilitySurface SURFACE = new ConstantVolatilitySurface(0.2);
  private static final double SPOT = 100;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 7, 1);
  private static final double LOWER = 90;
  private static final double UPPER = 110;
  private static final Expiry EXPIRY = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.25));
  private static final SupershareOptionDefinition OPTION = new SupershareOptionDefinition(EXPIRY, LOWER, UPPER);
  private static final StandardOptionDataBundle DATA = new StandardOptionDataBundle(CURVE, B, SURFACE, SPOT, DATE);
  private static final AnalyticOptionModel<SupershareOptionDefinition, StandardOptionDataBundle> MODEL = new SupershareOptionModel();

  @Test(expected = IllegalArgumentException.class)
  public void testNullDefinition() {
    MODEL.getPricingFunction(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    MODEL.getPricingFunction(OPTION).evaluate((StandardOptionDataBundle) null);
  }

  @Test
  public void testZeroVol() {
    StandardOptionDataBundle data = DATA.withVolatilitySurface(new ConstantVolatilitySurface(1e-15)).withSpot(LOWER - 1);
    assertEquals(MODEL.getPricingFunction(OPTION).evaluate(data), 0, 0);
    data = data.withSpot(UPPER + 1);
    assertEquals(MODEL.getPricingFunction(OPTION).evaluate(data), 0, 0);
  }

  @Test
  public void test() {
    assertEquals(MODEL.getPricingFunction(OPTION).evaluate(DATA), 0.7389, 1e-4);
  }
}