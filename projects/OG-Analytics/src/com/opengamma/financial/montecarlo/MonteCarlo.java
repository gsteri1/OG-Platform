/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.montecarlo;

import com.opengamma.financial.model.stochastic.StochasticProcess;

/**
 * 
 * @param <U>
 * @param <S>
 * @param <T>
 */
public interface MonteCarlo<U extends StochasticProcess<S, T>, S, T> {

  double[] getPath(U process, int n);
}
