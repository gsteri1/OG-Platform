/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.view.cache;

import java.util.Collection;

import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.view.calcnode.DeferredInvocationStatistics;

/**
 *  A {@link DelayedViewComputationCache} which doesn't reschule the puts
 */
public class NonDelayedViewComputationCache extends DelayedViewComputationCache {

  public NonDelayedViewComputationCache(ViewComputationCache cache, CacheSelectHint filter) {
    super(cache, filter);
  }

  @Override
  public void putValues(Collection<ComputedValue> values, DeferredInvocationStatistics statistics) {
    super.putValues(values);
    for (ComputedValue computedValue : values) {
      statistics.addDataOutputBytes(estimateValueSize(computedValue));
    }
  }

  @Override
  public void waitForPendingWrites() {
    //No-op
  }
}
