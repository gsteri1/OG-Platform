/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics;

import com.opengamma.util.money.Currency;

/**
 * 
 */
public class CurrencyLabelledMatrix1D extends LabelledMatrix1D<Currency, Currency> {

  public CurrencyLabelledMatrix1D(final Currency[] keys, final double[] values) {
    super(keys, values, null);
  }

  public CurrencyLabelledMatrix1D(final Currency[] keys, final Object[] labels, final double[] values) {
    super(keys, labels, values, null);
  }

  @Override
  public int compare(final Currency key1, final Currency key2, final Currency tolerance) {
    return key1.compareTo(key2);
  }

  @Override
  protected LabelledMatrix1D<Currency, Currency> getMatrix(final Currency[] keys, final Object[] labels, final double[] values) {
    return new CurrencyLabelledMatrix1D(keys, labels, values);
  }

  @Override
  protected LabelledMatrix1D<Currency, Currency> getMatrix(final Currency[] keys, final double[] values) {
    return new CurrencyLabelledMatrix1D(keys, values);
  }

}
