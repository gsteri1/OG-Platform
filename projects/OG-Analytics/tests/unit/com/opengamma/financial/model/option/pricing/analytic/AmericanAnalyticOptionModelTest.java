/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Set;

import javax.time.calendar.ZonedDateTime;

import com.opengamma.financial.greeks.Greek;
import com.opengamma.financial.greeks.GreekResultCollection;
import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.financial.model.option.definition.AmericanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.financial.model.volatility.surface.VolatilitySurface;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

public class AmericanAnalyticOptionModelTest extends AnalyticOptionModelTest {
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2009, 1, 1);
  private static final Expiry NINE_MONTHS = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.75));
  private static final Expiry TENTH_YEAR = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.1));
  private static final Expiry SIX_MONTHS = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.5));
  private static final double STRIKE = 100;
  private static final YieldAndDiscountCurve CURVE = new ConstantYieldCurve(0.1);
  private static final double B = 0;
  private static final double SPOT1 = 90;
  private static final double SPOT2 = 100;
  private static final double SPOT3 = 110;
  private static final double SIGMA1 = 0.15;
  private static final double SIGMA2 = 0.25;
  private static final double SIGMA3 = 0.35;
  private static final Set<Greek> GREEK_SET = Collections.singleton(Greek.FAIR_PRICE);

  public void test(final AnalyticOptionModel<AmericanVanillaOptionDefinition, StandardOptionDataBundle> model, final double eps) {
    final AmericanVanillaOptionDefinition definition = new AmericanVanillaOptionDefinition(1., NINE_MONTHS, true);
    super.testInputs(model, definition);
    AmericanVanillaOptionDefinition call = new AmericanVanillaOptionDefinition(STRIKE, TENTH_YEAR, true);
    AmericanVanillaOptionDefinition put = new AmericanVanillaOptionDefinition(STRIKE, TENTH_YEAR, false);
    final VolatilitySurface surface = new ConstantVolatilitySurface(SIGMA1);
    StandardOptionDataBundle vars = new StandardOptionDataBundle(CURVE, B, surface, SPOT1, DATE);
    testResult(model.getGreeks(call, vars, GREEK_SET), 0.0205, eps);
    vars = vars.withSpot(SPOT2);
    testResult(model.getGreeks(call, vars, GREEK_SET), 1.8757, eps);
    vars = vars.withSpot(SPOT3);
    testResult(model.getGreeks(call, vars, GREEK_SET), 10, eps);
    vars = vars.withVolatilitySurface(new ConstantVolatilitySurface(SIGMA2)).withSpot(SPOT1);
    testResult(model.getGreeks(call, vars, GREEK_SET), 0.3151, eps);
    vars = vars.withSpot(SPOT2);
    testResult(model.getGreeks(call, vars, GREEK_SET), 3.1256, eps);
    vars = vars.withSpot(SPOT3);
    testResult(model.getGreeks(call, vars, GREEK_SET), 10.3725, eps);
    vars = vars.withVolatilitySurface(new ConstantVolatilitySurface(SIGMA3)).withSpot(SPOT1);
    testResult(model.getGreeks(call, vars, GREEK_SET), 0.9479, eps);
    vars = vars.withSpot(SPOT2);
    testResult(model.getGreeks(call, vars, GREEK_SET), 4.3746, eps);
    vars = vars.withSpot(SPOT3);
    testResult(model.getGreeks(call, vars, GREEK_SET), 11.1578, eps);

    call = new AmericanVanillaOptionDefinition(STRIKE, SIX_MONTHS, true);
    vars = vars.withVolatilitySurface(new ConstantVolatilitySurface(SIGMA1)).withSpot(SPOT1);
    testResult(model.getGreeks(call, vars, GREEK_SET), 0.8099, eps);
    vars = vars.withSpot(SPOT2);
    testResult(model.getGreeks(call, vars, GREEK_SET), 4.0628, eps);
    vars = vars.withSpot(SPOT3);
    testResult(model.getGreeks(call, vars, GREEK_SET), 10.7898, eps);
    vars = vars.withVolatilitySurface(new ConstantVolatilitySurface(SIGMA2)).withSpot(SPOT1);
    testResult(model.getGreeks(call, vars, GREEK_SET), 2.7180, eps);
    vars = vars.withSpot(SPOT2);
    testResult(model.getGreeks(call, vars, GREEK_SET), 6.7661, eps);
    vars = vars.withSpot(SPOT3);
    testResult(model.getGreeks(call, vars, GREEK_SET), 12.9814, eps);
    vars = vars.withVolatilitySurface(new ConstantVolatilitySurface(SIGMA3)).withSpot(SPOT1);
    testResult(model.getGreeks(call, vars, GREEK_SET), 4.9665, eps);
    vars = vars.withSpot(SPOT2);
    testResult(model.getGreeks(call, vars, GREEK_SET), 9.4608, eps);
    vars = vars.withSpot(SPOT3);
    testResult(model.getGreeks(call, vars, GREEK_SET), 15.5137, eps);

    vars = vars.withVolatilitySurface(new ConstantVolatilitySurface(SIGMA1)).withSpot(SPOT1);
    testResult(model.getGreeks(put, vars, GREEK_SET), 10.0000, eps);
    vars = vars.withSpot(SPOT2);
    testResult(model.getGreeks(put, vars, GREEK_SET), 1.8757, eps);
    vars = vars.withSpot(SPOT3);
    testResult(model.getGreeks(put, vars, GREEK_SET), 0.0408, eps);
    vars = vars.withVolatilitySurface(new ConstantVolatilitySurface(SIGMA2)).withSpot(SPOT1);
    testResult(model.getGreeks(put, vars, GREEK_SET), 10.2280, eps);
    vars = vars.withSpot(SPOT2);
    testResult(model.getGreeks(put, vars, GREEK_SET), 3.1256, eps);
    vars = vars.withSpot(SPOT3);
    testResult(model.getGreeks(put, vars, GREEK_SET), 0.4552, eps);
    vars = vars.withVolatilitySurface(new ConstantVolatilitySurface(SIGMA3)).withSpot(SPOT1);
    testResult(model.getGreeks(put, vars, GREEK_SET), 10.8663, eps);
    vars = vars.withSpot(SPOT2);
    testResult(model.getGreeks(put, vars, GREEK_SET), 4.3746, eps);
    vars = vars.withSpot(SPOT3);
    testResult(model.getGreeks(put, vars, GREEK_SET), 1.2383, eps);

    put = new AmericanVanillaOptionDefinition(STRIKE, SIX_MONTHS, false);
    vars = vars.withVolatilitySurface(new ConstantVolatilitySurface(SIGMA1)).withSpot(SPOT1);
    testResult(model.getGreeks(put, vars, GREEK_SET), 10.54, eps);
    vars = vars.withSpot(SPOT2);
    testResult(model.getGreeks(put, vars, GREEK_SET), 4.0628, eps);
    vars = vars.withSpot(SPOT3);
    testResult(model.getGreeks(put, vars, GREEK_SET), 1.0689, eps);
    vars = vars.withVolatilitySurface(new ConstantVolatilitySurface(SIGMA2)).withSpot(SPOT1);
    testResult(model.getGreeks(put, vars, GREEK_SET), 12.4097, eps);
    vars = vars.withSpot(SPOT2);
    testResult(model.getGreeks(put, vars, GREEK_SET), 6.7661, eps);
    vars = vars.withSpot(SPOT3);
    testResult(model.getGreeks(put, vars, GREEK_SET), 3.2932, eps);
    vars = vars.withVolatilitySurface(new ConstantVolatilitySurface(SIGMA3)).withSpot(SPOT1);
    testResult(model.getGreeks(put, vars, GREEK_SET), 14.6445, eps);
    vars = vars.withSpot(SPOT2);
    testResult(model.getGreeks(put, vars, GREEK_SET), 9.4608, eps);
    vars = vars.withSpot(SPOT3);
    testResult(model.getGreeks(put, vars, GREEK_SET), 5.8374, eps);
  }

  private void testResult(final GreekResultCollection result, final double value, final double eps) {
    assertEquals(result.get(Greek.FAIR_PRICE), value, eps);
  }

}