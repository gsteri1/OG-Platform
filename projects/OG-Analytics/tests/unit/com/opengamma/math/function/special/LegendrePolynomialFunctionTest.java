/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.function.special;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

import com.opengamma.math.function.DoubleFunction1D;

/**
 * 
 */
public class LegendrePolynomialFunctionTest {
  private static final DoubleFunction1D P0 = new DoubleFunction1D() {

    @Override
    public Double evaluate(final Double x) {
      return 1.;
    }

  };
  private static final DoubleFunction1D P1 = new DoubleFunction1D() {

    @Override
    public Double evaluate(final Double x) {
      return x;
    }

  };
  private static final DoubleFunction1D P2 = new DoubleFunction1D() {

    @Override
    public Double evaluate(final Double x) {
      return 0.5 * (3 * x * x - 1);
    }

  };
  private static final DoubleFunction1D P3 = new DoubleFunction1D() {

    @Override
    public Double evaluate(final Double x) {
      return 0.5 * x * (5 * x * x - 3);
    }

  };
  private static final DoubleFunction1D P4 = new DoubleFunction1D() {

    @Override
    public Double evaluate(final Double x) {
      return 0.125 * (35 * x * x * x * x - 30 * x * x + 3);
    }

  };
  private static final DoubleFunction1D P5 = new DoubleFunction1D() {

    @Override
    public Double evaluate(final Double x) {
      return 0.125 * x * (63 * x * x * x * x - 70 * x * x + 15);
    }

  };
  private static final DoubleFunction1D P6 = new DoubleFunction1D() {

    @Override
    public Double evaluate(final Double x) {
      return 0.0625 * (231 * x * x * x * x * x * x - 315 * x * x * x * x + 105 * x * x - 5);
    }

  };
  private static final DoubleFunction1D P7 = new DoubleFunction1D() {

    @Override
    public Double evaluate(final Double x) {
      return 0.0625 * x * (429 * x * x * x * x * x * x - 693 * x * x * x * x + 315 * x * x - 35);
    }

  };
  private static final DoubleFunction1D P8 = new DoubleFunction1D() {

    @Override
    public Double evaluate(final Double x) {
      final double xSq = x * x;
      return 0.0078125 * (6435 * xSq * xSq * xSq * xSq - 12012 * xSq * xSq * xSq + 6930 * xSq * xSq - 1260 * xSq + 35);
    }

  };
  private static final DoubleFunction1D P9 = new DoubleFunction1D() {

    @Override
    public Double evaluate(final Double x) {
      final double xSq = x * x;
      return 0.0078125 * x * (12155 * xSq * xSq * xSq * xSq - 25740 * xSq * xSq * xSq + 18018 * xSq * xSq - 4620 * xSq + 315);
    }

  };
  private static final DoubleFunction1D P10 = new DoubleFunction1D() {

    @Override
    public Double evaluate(final Double x) {
      final double xSq = x * x;
      return 0.00390625 * (46189 * xSq * xSq * xSq * xSq * xSq - 109395 * xSq * xSq * xSq * xSq + 90090 * xSq * xSq * xSq - 30030 * xSq * xSq + 3465 * xSq - 63);
    }

  };
  private static final DoubleFunction1D[] P = new DoubleFunction1D[] {P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10};
  private static final LegendrePolynomialFunction LEGENDRE = new LegendrePolynomialFunction();
  private static final double EPS = 1e-12;

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testBadN() {
    LEGENDRE.getPolynomials(-3);
  }

  @Test
  public void test() {
    DoubleFunction1D[] p = LEGENDRE.getPolynomials(0);
    assertEquals(p.length, 1);
    final double x = 1.23;
    assertEquals(p[0].evaluate(x), 1, EPS);
    p = LEGENDRE.getPolynomials(1);
    assertEquals(p.length, 2);
    assertEquals(p[1].evaluate(x), x, EPS);
    for (int i = 0; i <= 10; i++) {
      p = LEGENDRE.getPolynomials(i);
      for (int j = 0; j <= i; j++) {
        assertEquals(P[j].evaluate(x), p[j].evaluate(x), EPS);
      }
    }
  }
}
