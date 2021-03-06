/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.forex;

import java.util.Set;

import com.google.common.collect.Sets;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.financial.analytics.ircurve.YieldCurveFunction;
import com.opengamma.financial.analytics.volatility.surface.RawVolatilitySurfaceDataFunction;
import com.opengamma.financial.forex.calculator.ForexConverter;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.financial.security.fx.FXUtils;
import com.opengamma.financial.security.option.FXOptionSecurity;
import com.opengamma.id.ExternalId;
import com.opengamma.livedata.normalization.MarketDataRequirementNames;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.UnorderedCurrencyPair;

/**
 * 
 */
public abstract class ForexVanillaOptionFunction extends ForexOptionFunction {

  public ForexVanillaOptionFunction(final String putFundingCurveName, final String putForwardCurveName, final String callFundingCurveName, 
      final String callForwardCurveName, final String surfaceName) {
    super(putFundingCurveName, putForwardCurveName, callFundingCurveName, callForwardCurveName, surfaceName);
  }

  @Override
  protected ForexConverter<?> getDefinition(final FinancialSecurity target) {
    final FXOptionSecurity security = (FXOptionSecurity) target;
    return getVisitor().visitFXOptionSecurity(security);
  }

  @Override
  protected Currency getPutCurrency(final FinancialSecurity target) {
    final FXOptionSecurity security = (FXOptionSecurity) target;
    return security.getPutCurrency();
  }

  @Override
  protected Currency getCallCurrency(final FinancialSecurity target) {
    final FXOptionSecurity security = (FXOptionSecurity) target;
    return security.getCallCurrency();
  }

  @Override
  protected ExternalId getSpotIdentifier(final FinancialSecurity target) {
    final FXOptionSecurity security = (FXOptionSecurity) target;
    return FXUtils.getSpotIdentifier(security);
  }

  @Override
  protected ExternalId getInverseSpotIdentifier(final FinancialSecurity target) {
    final FXOptionSecurity security = (FXOptionSecurity) target;
    return FXUtils.getInverseSpotIdentifier(security);
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    if (target.getType() != ComputationTargetType.SECURITY) {
      return false;
    }
    return target.getSecurity() instanceof FXOptionSecurity;
  }

  @Override
  public Set<ValueRequirement> getRequirements(final FunctionCompilationContext context, final ComputationTarget target, final ValueRequirement desiredValue) {
    final FXOptionSecurity fxOption = (FXOptionSecurity) target.getSecurity();
    final String putFundingCurveName = getPutFundingCurveName();
    final String putForwardCurveName = getPutForwardCurveName();
    final String callFundingCurveName = getCallFundingCurveName();
    final String callForwardCurveName = getCallForwardCurveName();
    final String surfaceName = getSurfaceName();
    final ValueRequirement putFundingCurve = YieldCurveFunction.getCurveRequirement(fxOption.getPutCurrency(), putFundingCurveName, putForwardCurveName, putFundingCurveName);
    final ValueRequirement putForwardCurve = YieldCurveFunction.getCurveRequirement(fxOption.getPutCurrency(), putForwardCurveName, putForwardCurveName, putFundingCurveName);
    final ValueRequirement callFundingCurve = YieldCurveFunction.getCurveRequirement(fxOption.getCallCurrency(), callFundingCurveName, callForwardCurveName, callFundingCurveName);
    final ValueRequirement callForwardCurve = YieldCurveFunction.getCurveRequirement(fxOption.getCallCurrency(), callForwardCurveName, callForwardCurveName, callFundingCurveName);
    final ValueProperties surfaceProperties = ValueProperties.with(ValuePropertyNames.SURFACE, surfaceName)
        .with(RawVolatilitySurfaceDataFunction.PROPERTY_SURFACE_INSTRUMENT_TYPE, "FX_VANILLA_OPTION").get();
    final UnorderedCurrencyPair currenciesTarget = UnorderedCurrencyPair.of(fxOption.getPutCurrency(), fxOption.getCallCurrency());
    final ValueRequirement fxVolatilitySurface = new ValueRequirement(ValueRequirementNames.VOLATILITY_SURFACE_DATA, currenciesTarget, surfaceProperties);
    final ExternalId spotIdentifier = FXUtils.getSpotIdentifier(fxOption);
    final ValueRequirement spotRequirement = new ValueRequirement(MarketDataRequirementNames.MARKET_VALUE, spotIdentifier);
    final ExternalId inverseSpotIdentifier = FXUtils.getSpotIdentifier(fxOption);
    final ValueRequirement inverseSpotRequirement = new ValueRequirement(MarketDataRequirementNames.MARKET_VALUE, inverseSpotIdentifier);
    return Sets.newHashSet(putFundingCurve, putForwardCurve, callFundingCurve, callForwardCurve, fxVolatilitySurface, spotRequirement, inverseSpotRequirement);
  }
}
