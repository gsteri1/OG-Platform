/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.view.calcnode;

import com.opengamma.engine.view.calcnode.stats.FunctionInvocationStatisticsGatherer;

/**
 * 
 */
public class DeferredInvocationStatistics {

  private final FunctionInvocationStatisticsGatherer _gatherer;
  private final String _configuration;
  private String _functionIdentifier;
  private long _invocationTime;
  private double _dataInputBytes;
  private int _dataOutputBytes;
  private int _dataOutputSamples;
  private int _expectedDataOutputSamples;

  protected DeferredInvocationStatistics(final FunctionInvocationStatisticsGatherer gatherer, final String configuration) {
    _gatherer = gatherer;
    _configuration = configuration;
  }

  protected void setFunctionIdentifier(final String functionIdentifier) {
    _functionIdentifier = functionIdentifier;
  }

  protected void beginInvocation() {
    _invocationTime = System.nanoTime();
  }

  protected void endInvocation() {
    _invocationTime = System.nanoTime() - _invocationTime;
  }

  protected void setDataInputBytes(final int bytes, final int samples) {
    if (samples > 0) {
      _dataInputBytes = (double) bytes / (double) samples;
    } else {
      _dataInputBytes = Double.NaN;
    }
  }

  protected void setExpectedDataOutputSamples(final int samples) {
    _expectedDataOutputSamples = samples;
  }

  /**
   * 
   * @param bytes size of output sample
   * @return {@code true} if this was the last one expected, {@code false} if expecting more
   */
  public boolean addDataOutputBytes(final int bytes) {
    _dataOutputBytes += bytes;
    _dataOutputSamples++;
    if (_dataOutputSamples < _expectedDataOutputSamples) {
      return false;
    }
    _gatherer.functionInvoked(_configuration, _functionIdentifier, 1, _invocationTime, _dataInputBytes, _dataOutputBytes / _expectedDataOutputSamples);
    return true;
  }

}
