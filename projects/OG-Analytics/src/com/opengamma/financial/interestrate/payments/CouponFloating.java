/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.payments;

import org.apache.commons.lang.Validate;

/**
 * Class describing a generic floating coupon with a uniaue fixing date.
 */
public class CouponFloating extends Coupon {

  private final double _fixingTime;

  /**
   * Constructor from all the details.
   * @param paymentTime Time (in years) up to the payment.
   * @param fundingCurveName Name of the funding curve.
   * @param paymentYearFraction The year fraction (or accrual factor) for the coupon payment.
   * @param notional Coupon notional.
   * @param fixingTime Time (in years) up to fixing.
   */
  public CouponFloating(double paymentTime, String fundingCurveName, double paymentYearFraction, double notional, double fixingTime) {
    super(paymentTime, fundingCurveName, paymentYearFraction, notional);
    Validate.isTrue(fixingTime >= 0.0, "fixing time < 0");
    _fixingTime = fixingTime;
  }

  /**
   * Gets the _fixingTime field.
   * @return the _fixingTime
   */
  public double getFixingTime() {
    return _fixingTime;
  }

  @Override
  public String toString() {
    return super.toString() + ", fixing time = " + _fixingTime;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    long temp;
    temp = Double.doubleToLongBits(_fixingTime);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    CouponFloating other = (CouponFloating) obj;
    if (Double.doubleToLongBits(_fixingTime) != Double.doubleToLongBits(other._fixingTime)) {
      return false;
    }
    return true;
  }

}