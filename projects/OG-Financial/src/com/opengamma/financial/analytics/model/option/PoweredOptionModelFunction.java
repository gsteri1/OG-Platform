/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.option;

import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.PoweredOptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.option.pricing.analytic.AnalyticOptionModel;
import com.opengamma.financial.model.option.pricing.analytic.PoweredOptionModel;
import com.opengamma.financial.security.option.OptionSecurity;
import com.opengamma.financial.security.option.OptionType;
import com.opengamma.financial.security.option.PoweredPayoffStyle;

/**
 * 
 */
public class PoweredOptionModelFunction extends StandardOptionDataAnalyticOptionModelFunction {
  private final AnalyticOptionModel<PoweredOptionDefinition, StandardOptionDataBundle> _model = new PoweredOptionModel();

  @SuppressWarnings("unchecked")
  @Override
  protected AnalyticOptionModel<PoweredOptionDefinition, StandardOptionDataBundle> getModel() {
    return _model;
  }

  @Override
  protected OptionDefinition getOptionDefinition(final OptionSecurity option) {
    final PoweredPayoffStyle payoff = (PoweredPayoffStyle) option.getPayoffStyle();
    return new PoweredOptionDefinition(option.getStrike(), option.getExpiry(), payoff.getPower(), option.getOptionType() == OptionType.CALL);
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    if (target.getType() != ComputationTargetType.SECURITY) {
      return false;
    }
    if (target.getSecurity() instanceof OptionSecurity && ((OptionSecurity) target.getSecurity()).getPayoffStyle() instanceof PoweredPayoffStyle) {
      return true;
    }
    return false;
  }

  @Override
  public String getShortName() {
    return null;
  }

}