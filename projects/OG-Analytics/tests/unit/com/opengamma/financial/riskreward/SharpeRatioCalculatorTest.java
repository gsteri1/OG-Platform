/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.riskreward;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

import com.opengamma.financial.timeseries.analysis.DoubleTimeSeriesStatisticsCalculator;
import com.opengamma.math.function.Function;
import com.opengamma.util.timeseries.DoubleTimeSeries;
import com.opengamma.util.timeseries.fast.DateTimeNumericEncoding;
import com.opengamma.util.timeseries.fast.longint.FastArrayLongDoubleTimeSeries;

/**
 * 
 */
public class SharpeRatioCalculatorTest {
  private static final double RETURN_PERIODS_PER_YEAR = 1;
  private static final long[] T = new long[] {1};
  private static final double STD_DEV = 0.15;
  private static final DoubleTimeSeries<?> ASSET_RETURN = new FastArrayLongDoubleTimeSeries(DateTimeNumericEncoding.DATE_EPOCH_DAYS, T, new double[] {0.12});
  private static final DoubleTimeSeries<?> RISK_FREE = new FastArrayLongDoubleTimeSeries(DateTimeNumericEncoding.DATE_EPOCH_DAYS, T, new double[] {0.03});
  private static final DoubleTimeSeriesStatisticsCalculator EXCESS_RETURN = new DoubleTimeSeriesStatisticsCalculator(new Function<double[], Double>() {

    @Override
    public Double evaluate(final double[]... x) {
      return x[0][0];
    }

  });
  private static final DoubleTimeSeriesStatisticsCalculator STD = new DoubleTimeSeriesStatisticsCalculator(new Function<double[], Double>() {

    @Override
    public Double evaluate(final double[]... x) {
      return STD_DEV;
    }

  });
  private static final SharpeRatioCalculator SHARPE = new SharpeRatioCalculator(RETURN_PERIODS_PER_YEAR, EXCESS_RETURN, STD);

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNegativeReturnPeriodsCalculator() {
    new SharpeRatioCalculator(-RETURN_PERIODS_PER_YEAR, EXCESS_RETURN, STD);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullExcessReturnCalculator() {
    new SharpeRatioCalculator(RETURN_PERIODS_PER_YEAR, null, EXCESS_RETURN);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullStdDevCalculator() {
    new SharpeRatioCalculator(RETURN_PERIODS_PER_YEAR, EXCESS_RETURN, null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullTSArray() {
    SHARPE.evaluate(null, null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullFirstElement() {
    SHARPE.evaluate(null, RISK_FREE);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullSecondElement() {
    SHARPE.evaluate(ASSET_RETURN, null);
  }

  @Test
  public void test() {
    assertEquals(SHARPE.evaluate(ASSET_RETURN, RISK_FREE), 0.6, 0);
  }
}
