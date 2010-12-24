/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.annuity;

import org.apache.commons.lang.Validate;

import com.opengamma.financial.interestrate.annuity.definition.GenericAnnuity;
import com.opengamma.financial.interestrate.payments.FixedPayment;
import com.opengamma.math.function.Function1D;
import com.opengamma.math.rootfinding.BracketRoot;
import com.opengamma.math.rootfinding.RealSingleRootFinder;
import com.opengamma.math.rootfinding.VanWijngaardenDekkerBrentSingleRootFinder;

/**
 * 
 */
public final class YieldSensitivityCalculator {
  private static final BracketRoot BRACKETER = new BracketRoot();
  private static final RealSingleRootFinder ROOT_FINDER = new VanWijngaardenDekkerBrentSingleRootFinder();
  private static final YieldSensitivityCalculator INSTANCE = new YieldSensitivityCalculator();

  private YieldSensitivityCalculator() {
  }

  public static YieldSensitivityCalculator getInstance() {
    return INSTANCE;
  }

  /**
   * For a set of future cash flows with an assumed present value (dirty price), calculates the continuously compounded constant interest rate that gives the 
   * same present value
   * @param annuity Set of known cash flows 
   * @param pv The present value of the future cash flows. Also know as dirty or full price
   * @return continuously compounded yield (as a fraction) 
   */
  public double calculateYield(final GenericAnnuity<? extends FixedPayment> annuity, final double pv) {
    Validate.notNull(annuity, "annuity");
    final Function1D<Double, Double> f = new Function1D<Double, Double>() {

      @Override
      public Double evaluate(final Double y) {
        return calculatePriceForYield(annuity, y) - pv;
      }

    };

    final double[] range = BRACKETER.getBracketedPoints(f, 0.0, 0.2);
    return ROOT_FINDER.getRoot(f, range[0], range[1]);
  }

  /**
   * Calculate the present value of a set of cash flows given a yield 
   * @param annuity  Set of known cash flows 
   * @param yield Continuously compounded constant interest rate 
   * @return Present value (dirty price)
   */
  public double calculatePriceForYield(final GenericAnnuity<? extends FixedPayment> annuity, final double yield) {
    Validate.notNull(annuity, "annuity");
    double sum = 0;

    final int n = annuity.getNumberOfPayments();
    FixedPayment temp;
    for (int i = 0; i < n; i++) {
      temp = annuity.getNthPayment(i);
      sum += temp.getAmount() * Math.exp(-yield * temp.getPaymentTime());
    }
    return sum;
  }

  /**
   * For a set of cash flows calculates the nth derivative of its PV with respect to its continuously compounded yield multiplied by the 
   * factor (-1)^n which just keeps the sign positive when cash flows are positive 
   * @param annuity Set of known cash flows  
   * @param pv The present value of the future cash flows. Also know as dirty or full price
   *@param order The order of the derivative 
   * @return nth order yield sensitivity (times (-1)^n
   */
  public double calculateNthOrderSensitivity(final GenericAnnuity<? extends FixedPayment> annuity, final double pv, final int order) {
    Validate.notNull(annuity, "annuity");
    final double yield = calculateYield(annuity, pv);
    return calculateNthOrderSensitivityFromYield(annuity, yield, order);
  }

  /**
   *  For a set of cash flows calculates the nth derivative of its PV with respect to its continuously compounded yield multiplied by the 
   *  factor (-1)^n which just keeps the sign positive when cash flows are positive 
   * @param annuity Set of known cash flows 
   * @param yield Continuously compounded constant interest rate 
   * @param order The order of the derivative 
   * @return nth order yield sensitivity (times (-1)^n)
   */
  public double calculateNthOrderSensitivityFromYield(final GenericAnnuity<? extends FixedPayment> annuity, final double yield, final int order) {
    Validate.notNull(annuity, "annuity");
    Validate.isTrue(order >= 0, "order must be positive");
    double sum = 0;

    double t;
    double tPower;
    final int n = annuity.getNumberOfPayments();
    FixedPayment temp;
    for (int i = 0; i < n; i++) {
      temp = annuity.getNthPayment(i);
      t = temp.getPaymentTime();
      tPower = Math.pow(t, order);
      sum += temp.getAmount() * tPower * Math.exp(-yield * t);
    }
    return sum;
  }

}