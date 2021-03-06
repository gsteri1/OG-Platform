/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.bond;

import org.apache.commons.lang.Validate;

import com.opengamma.financial.interestrate.YieldCurveBundle;
import com.opengamma.financial.interestrate.bond.definition.BondForward;
import com.opengamma.financial.interestrate.payments.CouponFixed;

/**
 * 
 */
public final class BondForwardDirtyPriceCalculator extends BondForwardCalculator {
  private static final BondCalculator DIRTY_PRICE_CALCULATOR = BondDirtyPriceCalculator.getInstance();
  private static final BondForwardDirtyPriceCalculator INSTANCE = new BondForwardDirtyPriceCalculator();

  private BondForwardDirtyPriceCalculator() {
  }

  public static BondForwardDirtyPriceCalculator getInstance() {
    return INSTANCE;
  }

  @Override
  public Double calculate(final BondForward bondForward, final YieldCurveBundle curves, final double fundingRate) {
    final double dirtyPrice = DIRTY_PRICE_CALCULATOR.calculate(bondForward.getBond(), curves);
    return calculate(bondForward, dirtyPrice - bondForward.getAccruedInterest(), fundingRate);
  }

  @Override
  public Double calculate(final BondForward bondForward, final double bondCleanPrice, final double fundingRate) {
    Validate.notNull(bondForward, "bond forward");
    Validate.isTrue(bondCleanPrice > 0, "bond dirty price is positive");
    Validate.isTrue(fundingRate > 0, "funding rate is positive");
    final double bondDirtyPrice = bondCleanPrice + bondForward.getAccruedInterest();
    final double repoPeriod = bondForward.getForwardTime();
    final CouponFixed[] expiredCoupons = bondForward.getTimeBetweenExpiredCoupons();
    double valueOfExpiredCoupons = 0;
    for (final CouponFixed payment : expiredCoupons) {
      valueOfExpiredCoupons += payment.getAmount() * (1 + fundingRate * payment.getPaymentTime());
    }
    return bondDirtyPrice * (1 + fundingRate * repoPeriod) - valueOfExpiredCoupons;
  }

}
