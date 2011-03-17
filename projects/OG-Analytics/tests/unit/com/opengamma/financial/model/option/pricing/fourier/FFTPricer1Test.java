/**
 * Copyright (C) 2009 - 2011 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.fourier;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.opengamma.financial.model.option.pricing.analytic.formula.BlackFunctionData;
import com.opengamma.financial.model.option.pricing.analytic.formula.EuropeanVanillaOption;
import com.opengamma.financial.model.volatility.BlackImpliedVolatilityFormula;
import com.opengamma.math.function.Function1D;
import com.opengamma.math.integration.RungeKuttaIntegrator1D;
import com.opengamma.math.interpolation.Interpolator1D;
import com.opengamma.math.interpolation.Interpolator1DFactory;
import com.opengamma.math.interpolation.data.Interpolator1DDataBundle;
import com.opengamma.math.number.ComplexNumber;

/**
 * 
 */
public class FFTPricer1Test {
  private static final Interpolator1D<Interpolator1DDataBundle> INTERPOLATOR = Interpolator1DFactory.getInterpolator("DoubleQuadratic");
  private static final double FORWARD = 1;
  private static final double T = 1 / 52.0;
  private static final double DF = 0.96;
  private static final double SIGMA = 0.2;
  private static final double MU = -0.5 * SIGMA * SIGMA;
  private static final BlackFunctionData DATA = new BlackFunctionData(FORWARD, DF, SIGMA);
  private static final BlackImpliedVolatilityFormula BLACK_IMPLIED_VOL = new BlackImpliedVolatilityFormula();
  private static final CharacteristicExponent1 CEF = new GaussianCharacteristicExponent1(MU, SIGMA);
  private static final FFTPricer1 PRICER = new FFTPricer1();
  private static final EuropeanVanillaOption OPTION = new EuropeanVanillaOption(FORWARD, T, true);
  private static final double ALPHA = -0.5;
  private static final double TOL = 1e-8;

  @Test(expected = IllegalArgumentException.class)
  public void testNullData1() {
    PRICER.price(null, OPTION, CEF, 10, 10, ALPHA, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullOption1() {
    PRICER.price(DATA, null, CEF, 10, 10, ALPHA, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCharacteristicExponent1() {
    PRICER.price(DATA, OPTION, null, 10, 10, ALPHA, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeTolerance1() {
    PRICER.price(DATA, OPTION, CEF, 10, 10, ALPHA, -TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeNStrikes() {
    PRICER.price(DATA, OPTION, CEF, -10, 10, ALPHA, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeMaxDeltaMoneyness() {
    PRICER.price(DATA, OPTION, CEF, 10, -10, ALPHA, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeVol1() {
    PRICER.price(new BlackFunctionData(FORWARD, DF, -0.5), OPTION, CEF, 10, 10, ALPHA, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testZeroAlpha1() {
    PRICER.price(DATA, OPTION, CEF, 10, 10, 0, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData2() {
    PRICER.price(null, OPTION, CEF, 10, 110, 10, ALPHA, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullOption2() {
    PRICER.price(DATA, null, CEF, 10, 110, 10, ALPHA, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCharacteristicExponent2() {
    PRICER.price(DATA, OPTION, null, 10, 110, 10, ALPHA, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeTolerance2() {
    PRICER.price(DATA, OPTION, CEF, 10, 110, 10, ALPHA, -TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeVol2() {
    PRICER.price(new BlackFunctionData(FORWARD, DF, -0.5), OPTION, CEF, 10, 110, 10, ALPHA, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testZeroAlpha2() {
    PRICER.price(DATA, OPTION, CEF, 10, 110, 10, 0, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongLowStrike() {
    PRICER.price(DATA, OPTION, CEF, FORWARD + 10, 110, 10, ALPHA, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongHighStrike() {
    PRICER.price(DATA, OPTION, CEF, 10, FORWARD, 10, ALPHA, TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData3() {
    PRICER.price(null, OPTION, CEF, 10, 10, ALPHA, 0.5, 64, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullOption3() {
    PRICER.price(DATA, null, CEF, 10, 10, ALPHA, 0.5, 64, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCharacteristicExponent3() {
    PRICER.price(DATA, OPTION, null, 10, 10, ALPHA, 0.5, 64, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testZeroAlpha3() {
    PRICER.price(DATA, OPTION, CEF, 10, 10, 0, 0.5, 64, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeStrikesAboveATM() {
    PRICER.price(DATA, OPTION, CEF, 10, -10, ALPHA, 0.5, 64, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeStrikesBelowATM() {
    PRICER.price(DATA, OPTION, CEF, 10, -10, ALPHA, 0.5, 64, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeDelta() {
    PRICER.price(DATA, OPTION, CEF, 10, 10, ALPHA, -0.5, 64, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeM() {
    PRICER.price(DATA, OPTION, CEF, 10, 10, ALPHA, 0.5, 64, -10);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongN() {
    PRICER.price(DATA, OPTION, CEF, 10, 10, ALPHA, 0.5, 64, 128);
  }

  @Test
  public void testLargeNumberOfStrikes() {
    final boolean isCall = true;
    final int n = 1024;
    final int m = 100;
    final double delta = 0.1;
    final int nL = 550;
    final int nH = 600;
    final double alpha = -0.5;
    final EuropeanVanillaOption option = new EuropeanVanillaOption(100, T, isCall);
    final double[][] temp = PRICER.price(DATA, option, CEF, nL, nH, alpha, delta, n, m);
    assertEquals(n + 1, temp.length);
  }

  @Test
  public void test() {
    final boolean isCall = true;
    final int nStrikes = 21;
    final double deltaMoneyness = 0.01;
    final double alpha = -0.5;
    final double tol = 1e-10;
    final EuropeanVanillaOption option = new EuropeanVanillaOption(100, T, isCall);

    final double[][] strikeNprice = PRICER.price(DATA, option, CEF, nStrikes, deltaMoneyness, alpha, tol);

    assertEquals(nStrikes, strikeNprice.length);
    double k;
    double price;
    for (int i = 0; i < nStrikes; i++) {
      price = strikeNprice[i][1];
      k = strikeNprice[i][0];

      double impVol = 0;
      try {
        final EuropeanVanillaOption o = new EuropeanVanillaOption(k, T, true);
        impVol = BLACK_IMPLIED_VOL.getImpliedVolatility(DATA, o, price);
      } catch (final Exception e) {
      }
      assertEquals(SIGMA, impVol, 1e-5);
    }
  }

  @Test
  public void testPutsAndCalls() {
    boolean isCall = true;
    final double tol = 1e-10;
    for (int i = 0; i < 5; i++) {
      final double alpha = -1.6 + i * 0.5;
      for (int j = 0; j < 1; j++) {
        isCall = (j == 0);
        final BlackFunctionData data = new BlackFunctionData(FORWARD, DF, 0.3);
        final EuropeanVanillaOption option = new EuropeanVanillaOption(FORWARD, T, isCall);
        final double[][] strikeNprice = PRICER.price(data, option, CEF, FORWARD, FORWARD, 1, alpha, tol);
        assertEquals(FORWARD, strikeNprice[0][0], 1e-9);
        assertEquals(SIGMA, BLACK_IMPLIED_VOL.getImpliedVolatility(DATA, option, strikeNprice[0][1]), 1e-7);
      }
    }
  }

  /**
   * This test that the same price is produced when the alpha, tolerance and limitSigma are changed 
   */
  @Test
  public void testStability() {
    final boolean isCall = false;
    final int nStrikes = 21;
    final double kappa = 1.2;
    final double theta = 0.1;
    final double vol0 = 1.8 * theta;
    final double omega = 0.4;
    final double rho = -0.7;
    final double t = 2.0;

    BlackFunctionData data = new BlackFunctionData(FORWARD, DF, 0.3);
    EuropeanVanillaOption option = new EuropeanVanillaOption(100, t, isCall);
    final CharacteristicExponent1 heston = new HestonCharacteristicExponent1(kappa, theta, vol0, omega, rho);
    final double[][] strikeNPrice = PRICER.price(data, option, heston, 0.7 * FORWARD, 1.5 * FORWARD, nStrikes, -0.5, 1e-10);

    final int n = strikeNPrice.length;

    final double[] k = new double[n];
    final double[] vol = new double[n];
    for (int i = 0; i < n; i++) {
      k[i] = strikeNPrice[i][0];
      option = new EuropeanVanillaOption(k[i], t, isCall);
      vol[i] = BLACK_IMPLIED_VOL.getImpliedVolatility(data, option, strikeNPrice[i][1]);
    }

    final Interpolator1DDataBundle dataBundle = INTERPOLATOR.getDataBundleFromSortedArrays(k, vol);
    data = new BlackFunctionData(FORWARD, DF, 0.2);
    final double[][] strikeNPrice2 = PRICER.price(data, option, heston, 0.7 * FORWARD, 1.5 * FORWARD, nStrikes, 0.75, 1e-8);
    final int m = strikeNPrice2.length;
    for (int i = 0; i < m; i++) {
      final double strike = strikeNPrice2[i][0];
      option = new EuropeanVanillaOption(strike, t, isCall);
      final double sigma = BLACK_IMPLIED_VOL.getImpliedVolatility(data, option, strikeNPrice2[i][1]);
      assertEquals(sigma, INTERPOLATOR.interpolate(dataBundle, strike), 1e-5);
    }

  }

  @Test
  public void testDirect() {
    final double alpha = 0.5;
    final EuropeanCallFourierTransform callFT = new EuropeanCallFourierTransform(CEF);
    final Function1D<ComplexNumber, ComplexNumber> callFunction = callFT.getFunction(T);
    final Function1D<Double, Double> f = new Function1D<Double, Double>() {

      @Override
      public Double evaluate(final Double x) {
        final ComplexNumber z = new ComplexNumber(x, -1.0 - alpha);
        final ComplexNumber u = callFunction.evaluate(z);
        return u.getReal();
      }
    };

    final RungeKuttaIntegrator1D integrator = new RungeKuttaIntegrator1D();
    final double integral = integrator.integrate(f, 0.0, 1000.0) / Math.PI;

    final double price = DF * FORWARD * integral;
    double impVol = 0;
    try {
      final EuropeanVanillaOption option = new EuropeanVanillaOption(FORWARD, T, true);
      final BlackFunctionData data = new BlackFunctionData(FORWARD, DF, 0);
      impVol = BLACK_IMPLIED_VOL.getImpliedVolatility(data, option, price);
    } catch (final Exception e) {
    }
    assertEquals(SIGMA, impVol, 1e-5);
  }

  @Test
  public void testEuropeanCallFT() {

    final Function1D<ComplexNumber, ComplexNumber> callFT = new EuropeanCallFourierTransform(CEF).getFunction(T);
    for (int i = 0; i < 101; i++) {
      final double x = -10.0 + 20.0 * i / 100;
      final ComplexNumber z = new ComplexNumber(x, -1.5);
      final ComplexNumber u = callFT.evaluate(z);
      //System.out.println(x + "\t" + u.getReal() + "\t" + u.getImaginary());
    }

  }
}