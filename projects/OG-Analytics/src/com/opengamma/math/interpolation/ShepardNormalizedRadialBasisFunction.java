/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.interpolation;

import com.opengamma.math.function.Function1D;

/**
 * 
 */
public class ShepardNormalizedRadialBasisFunction extends Function1D<Double, Double> {
  private final double _power;

  public ShepardNormalizedRadialBasisFunction(final double power) {
    _power = power;
  }

  @Override
  public Double evaluate(final Double x) {
    return Math.pow(x, _power);
  }

}