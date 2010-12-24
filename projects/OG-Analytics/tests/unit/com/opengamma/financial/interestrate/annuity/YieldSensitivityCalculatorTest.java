/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.annuity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.opengamma.financial.interestrate.annuity.YieldSensitivityCalculator;
import com.opengamma.financial.interestrate.annuity.definition.GenericAnnuity;
import com.opengamma.financial.interestrate.payments.FixedCouponPayment;

/**
 * 
 */
public class YieldSensitivityCalculatorTest {
  private static YieldSensitivityCalculator YSC = YieldSensitivityCalculator.getInstance();

  @Test(expected = IllegalArgumentException.class)
  public void testNullAnnuity1() {
    YSC.calculateYield(null, 1.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullAnnuity2() {
    YSC.calculatePriceForYield(null, 0.05);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullAnnuity3() {
    YSC.calculateNthOrderSensitivity(null, 1.0, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullAnnuity4() {
    YSC.calculateNthOrderSensitivityFromYield(null, 0.04, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeOrder() {
    YSC.calculateNthOrderSensitivity(new GenericAnnuity<FixedCouponPayment>(new FixedCouponPayment[] {new FixedCouponPayment(2, 2, 0.4, "A")}), 1, -1);
  }

  @Test
  public void testSinglePaymentYield() {
    final int n = 10;
    final double pv = 0.875;
    final FixedCouponPayment[] payments = new FixedCouponPayment[n];
    final double tau = 0.5;
    for (int i = 0; i < n - 1; i++) {
      payments[i] = new FixedCouponPayment((i + 1) * tau, tau, 0.0, "");
    }
    payments[n - 1] = new FixedCouponPayment(n * tau, tau, 2.0, "");

    final GenericAnnuity<FixedCouponPayment> annuity = new GenericAnnuity<FixedCouponPayment>(payments);
    final double yield = YieldSensitivityCalculator.getInstance().calculateYield(annuity, pv);
    assertEquals(Math.log(2.0 * tau / pv) / 10.0 / tau, yield, 1e-8);
  }

  @Test
  public void testSinglePaymentSensitivity() {
    final int n = 10;
    final double pv = 0.78945;
    final FixedCouponPayment[] payments = new FixedCouponPayment[n];
    final double tau = 0.5;
    for (int i = 0; i < n - 1; i++) {
      payments[i] = new FixedCouponPayment((i + 1) * tau, tau, 0.0, "");
    }
    payments[n - 1] = new FixedCouponPayment(n * tau, tau, 2.0, "");

    final GenericAnnuity<FixedCouponPayment> annuity = new GenericAnnuity<FixedCouponPayment>(payments);

    for (int order = 1; order < 5; order++) {
      final double sense = YieldSensitivityCalculator.getInstance().calculateNthOrderSensitivity(annuity, pv, order);
      assertEquals(Math.pow(tau * n, order) * pv, sense, 1e-8);
    }
  }

  @Test
  public void testSinglePaymentSensitivityFromYield() {
    final int n = 10;
    final double yield = 0.06;
    final FixedCouponPayment[] payments = new FixedCouponPayment[n];
    final double tau = 0.5;
    for (int i = 0; i < n - 1; i++) {
      payments[i] = new FixedCouponPayment((i + 1) * tau, tau, 0.0, "");
    }
    payments[n - 1] = new FixedCouponPayment(n * tau, tau, 2.0, "");

    final GenericAnnuity<FixedCouponPayment> annuity = new GenericAnnuity<FixedCouponPayment>(payments);

    final double pv = YieldSensitivityCalculator.getInstance().calculatePriceForYield(annuity, yield);
    for (int order = 1; order < 5; order++) {
      final double sense = YieldSensitivityCalculator.getInstance().calculateNthOrderSensitivityFromYield(annuity, yield, order);
      assertEquals(Math.pow(tau * n, order) * pv, sense, 1e-8);
    }
  }

}