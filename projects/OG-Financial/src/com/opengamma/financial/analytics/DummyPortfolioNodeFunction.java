/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.AbstractFunction;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.financial.analytics.ircurve.YieldCurveFunction;

/**
 * 
 */
public class DummyPortfolioNodeFunction extends AbstractFunction.NonCompiledInvoker {

  protected static String[] getPreservedProperties() {
    return new String[] {ValuePropertyNames.CURRENCY,
                         ValuePropertyNames.CURVE,
                         YieldCurveFunction.PROPERTY_FORWARD_CURVE,
                         YieldCurveFunction.PROPERTY_FUNDING_CURVE,
                         ValuePropertyNames.CALCULATION_METHOD};
  }

  private final String _valueRequirement;
  private final Double _value;

  public DummyPortfolioNodeFunction(final String valueRequirement, final String value) {
    _valueRequirement = valueRequirement;
    _value = Double.parseDouble(value);
  }

  @Override
  public Set<ComputedValue> execute(final FunctionExecutionContext executionContext, final FunctionInputs inputs,
      final ComputationTarget target, final Set<ValueRequirement> desiredValues) {
    final Set<ComputedValue> result = new HashSet<ComputedValue>();
    for (final ValueRequirement desiredValue : desiredValues) {
      result.add(new ComputedValue(new ValueSpecification(desiredValue, getUniqueId()), _value));
    }
    return result;
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    return target.getType() == ComputationTargetType.PORTFOLIO_NODE;
  }

  @Override
  public Set<ValueRequirement> getRequirements(final FunctionCompilationContext context,
      final ComputationTarget target, final ValueRequirement desiredValue) {
    return Collections.emptySet();
  }

  @Override
  public Set<ValueSpecification> getResults(final FunctionCompilationContext context, final ComputationTarget target) {
    final Set<ValueSpecification> results = new HashSet<ValueSpecification>();
    final ValueProperties.Builder props = createValueProperties();
    for (final String prop : getPreservedProperties()) {
      props.withAny(prop);
    }
    results.add(new ValueSpecification(_valueRequirement, target.toSpecification(), props.get()));
    return results;
  }

  @Override
  public String getShortName() {
    return "DummyPortfolioNodeModel";
  }

  @Override
  public ComputationTargetType getTargetType() {
    return ComputationTargetType.PORTFOLIO_NODE;
  }
}
