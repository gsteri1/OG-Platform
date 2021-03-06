/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.volatility.surface;

import com.opengamma.math.surface.Surface;
import com.opengamma.util.tuple.DoublesPair;

/**
 *  A surface with gives the absolute local volatility (I.e. for a SDE dx = sigma(t,x)dw) as a function of time to maturity and value of the
 *   underlying 
 */
public class AbsoluteLocalVolatilitySurface extends VolatilitySurface {

  /**
   * @param surface The time to maturity should be the first coordinate and the strike the second 
   */
  public AbsoluteLocalVolatilitySurface(Surface<Double, Double, Double> surface) {
    super(surface);
  }

  /**
   * 
   * @param t time to maturity
   * @param s value of the underlying
   * @return The Dupire local volatility 
   */
  @Override
  public double getVolatility(final double t, final double s) {
    DoublesPair temp = new DoublesPair(t, s);
    return getVolatility(temp);
  }

}
