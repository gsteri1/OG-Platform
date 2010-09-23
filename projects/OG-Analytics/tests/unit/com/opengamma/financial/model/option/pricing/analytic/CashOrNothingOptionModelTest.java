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
import com.opengamma.financial.model.option.definition.CashOrNothingOptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.financial.model.volatility.surface.VolatilitySurface;
import com.opengamma.math.function.Function1D;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class CashOrNothingOptionModelTest {
  private static final double R = 0.06;
  private static final YieldAndDiscountCurve CURVE = new ConstantYieldCurve(R);
  private static final double B = 0;
  private static final VolatilitySurface SURFACE = new ConstantVolatilitySurface(0.35);
  private static final double SPOT = 100;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 7, 1);
  private static final double STRIKE = 80;
  private static final double T = 0.75;
  private static final Expiry EXPIRY = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, T));
  private static final boolean IS_CALL = false;
  private static final double PAYMENT = 10;
  private static final CashOrNothingOptionDefinition PUT = new CashOrNothingOptionDefinition(STRIKE, EXPIRY, IS_CALL, PAYMENT);
  private static final StandardOptionDataBundle DATA = new StandardOptionDataBundle(CURVE, B, SURFACE, SPOT, DATE);
  private static final AnalyticOptionModel<CashOrNothingOptionDefinition, StandardOptionDataBundle> MODEL = new CashOrNothingOptionModel();
  private static final double EPS = 1e-12;

  @Test(expected = IllegalArgumentException.class)
  public void testNullDefinition() {
    MODEL.getPricingFunction(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    MODEL.getPricingFunction(PUT).evaluate((StandardOptionDataBundle) null);
  }

  @Test
  public void testZeroVol() {
    final double delta = 20;
    final StandardOptionDataBundle data = DATA.withVolatilitySurface(new ConstantVolatilitySurface(0));
    Function1D<StandardOptionDataBundle, Double> f = MODEL.getPricingFunction(PUT);
    final double df = Math.exp(-R * T);
    assertEquals(f.evaluate(data.withSpot(STRIKE - delta)), df * PAYMENT, EPS);
    assertEquals(f.evaluate(data.withSpot(STRIKE + delta)), 0, EPS);
    final CashOrNothingOptionDefinition call = new CashOrNothingOptionDefinition(STRIKE, EXPIRY, true, PAYMENT);
    f = MODEL.getPricingFunction(call);
    assertEquals(f.evaluate(data.withSpot(STRIKE + delta)), df * PAYMENT, EPS);
    assertEquals(f.evaluate(data.withSpot(STRIKE - delta)), 0, EPS);
  }

  @Test
  public void test() {
    assertEquals(MODEL.getPricingFunction(PUT).evaluate(DATA), 2.6710, 1e-4);
  }
}