/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.var;

import java.util.Set;

import com.google.common.collect.Sets;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.AbstractFunction;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.financial.timeseries.analysis.DoubleTimeSeriesStatisticsCalculator;
import com.opengamma.financial.var.NormalLinearVaRCalculator;
import com.opengamma.math.function.Function;
import com.opengamma.math.statistics.descriptive.StatisticsCalculatorFactory;
import com.opengamma.util.timeseries.DoubleTimeSeries;

/**
 * 
 *
 */
public class PortfolioHistoricalVaRCalculatorFunction extends AbstractFunction.NonCompiledInvoker {
  private final DoubleTimeSeriesStatisticsCalculator _stdCalculator;
  private final DoubleTimeSeriesStatisticsCalculator _meanCalculator;
  private final double _confidenceLevel;
  private final NormalLinearVaRCalculator<DoubleTimeSeries<?>> _varCalculator;

  public PortfolioHistoricalVaRCalculatorFunction(final String meanCalculatorName, final String standardDeviationCalculatorName, final String confidenceLevel) {
    final Function<double[], Double> meanCalculator = StatisticsCalculatorFactory.getCalculator(meanCalculatorName);
    final Function<double[], Double> stdCalculator = StatisticsCalculatorFactory.getCalculator(standardDeviationCalculatorName);
    _meanCalculator = new DoubleTimeSeriesStatisticsCalculator(meanCalculator);
    _stdCalculator = new DoubleTimeSeriesStatisticsCalculator(stdCalculator);
    _confidenceLevel = Double.valueOf(confidenceLevel);
    //TODO number of periods per year depends on sampling frequency of P&L series
    _varCalculator = new NormalLinearVaRCalculator<DoubleTimeSeries<?>>(1, 1, _confidenceLevel, _meanCalculator, _stdCalculator);
  }

  @Override
  public Set<ComputedValue> execute(final FunctionExecutionContext executionContext, final FunctionInputs inputs, final ComputationTarget target, final Set<ValueRequirement> desiredValues) {
    final Object pnlSeriesObj = inputs.getValue(ValueRequirementNames.PNL_SERIES);
    if (pnlSeriesObj instanceof DoubleTimeSeries<?>) {
      final DoubleTimeSeries<?> pnlSeries = (DoubleTimeSeries<?>) pnlSeriesObj;
      if (!pnlSeries.isEmpty()) {
        final double var = _varCalculator.evaluate(pnlSeries);
        return Sets.newHashSet(new ComputedValue(new ValueSpecification(new ValueRequirement(ValueRequirementNames.HISTORICAL_VAR, target.getPortfolioNode()),
            getUniqueId()), var));
      }
    }
    return null;
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    return target.getType() == ComputationTargetType.PORTFOLIO_NODE;
  }

  @Override
  public Set<ValueRequirement> getRequirements(final FunctionCompilationContext context, final ComputationTarget target, final ValueRequirement desiredValue) {
    return Sets.newHashSet(new ValueRequirement(ValueRequirementNames.PNL_SERIES, target.getPortfolioNode()));
  }

  @Override
  public Set<ValueSpecification> getResults(final FunctionCompilationContext context, final ComputationTarget target) {
    return Sets.newHashSet(new ValueSpecification(new ValueRequirement(ValueRequirementNames.HISTORICAL_VAR, target.getPortfolioNode()),
        getUniqueId()));
  }

  @Override
  public String getShortName() {
    return "PortfolioHistoricalVaRCalculatorFunction";
  }

  @Override
  public ComputationTargetType getTargetType() {
    return ComputationTargetType.PORTFOLIO_NODE;
  }

}
