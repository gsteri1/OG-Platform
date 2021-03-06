/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.currency;

import com.opengamma.engine.ComputationTargetType;

/**
 * Injects a default currency requirement into the graph at a position.
 */
public class PositionDefaultCurrencyFunction extends DefaultCurrencyFunction {

  public PositionDefaultCurrencyFunction(final String valueName) {
    super(ComputationTargetType.POSITION, valueName);
  }

  public PositionDefaultCurrencyFunction(final String... valueNames) {
    super(ComputationTargetType.POSITION, valueNames);
  }

}
