/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.definition;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;

import com.opengamma.financial.interestrate.YieldCurveBundle;

/**
 * Class describing the data required to price interest rate derivatives with SABR (curves and parameters).
 */
//TODO as the constructor is written, this is more of a decorator
public class SABRInterestRateDataBundle extends YieldCurveBundle {

  /**
   * The surfaces of SABR parameters.
   */
  private final SABRInterestRateParameters _sabrParameter;

  /**
   * Constructor from SABR parameters and curve bundle.
   * @param sabrParameter SABR parameters.
   * @param curves Curve bundle.
   */
  public SABRInterestRateDataBundle(final SABRInterestRateParameters sabrParameter, final YieldCurveBundle curves) {
    super(curves);
    Validate.notNull(sabrParameter, "SABR parameters");
    _sabrParameter = sabrParameter;
  }

  public SABRInterestRateDataBundle(final SABRInterestRateDataBundle data) {
    super(data);
    _sabrParameter = data.getSABRParameter();
  }

  /**
   * Gets the _sabrParameter field.
   * @return The SABR parameters.
   */
  public SABRInterestRateParameters getSABRParameter() {
    return _sabrParameter;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + _sabrParameter.hashCode();
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SABRInterestRateDataBundle other = (SABRInterestRateDataBundle) obj;
    return ObjectUtils.equals(_sabrParameter, other._sabrParameter);
  }

}
