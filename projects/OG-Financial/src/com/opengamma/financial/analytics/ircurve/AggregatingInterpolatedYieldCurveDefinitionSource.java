/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.ircurve;

import java.util.ArrayList;
import java.util.Collection;

import javax.time.Instant;
import javax.time.InstantProvider;

import com.opengamma.util.money.Currency;

/**
 * Aggregates an ordered set of sources into a single source.
 */
public class AggregatingInterpolatedYieldCurveDefinitionSource implements InterpolatedYieldCurveDefinitionSource {
  
  private final Collection<InterpolatedYieldCurveDefinitionSource> _sources;

  public AggregatingInterpolatedYieldCurveDefinitionSource(final Collection<InterpolatedYieldCurveDefinitionSource> sources) {
    _sources = new ArrayList<InterpolatedYieldCurveDefinitionSource>(sources);
  }

  @Override
  public YieldCurveDefinition getDefinition(final Currency currency, final String name) {
    for (InterpolatedYieldCurveDefinitionSource source : _sources) {
      YieldCurveDefinition definition = source.getDefinition(currency, name);
      if (definition != null) {
        return definition;
      }
    }
    return null;
  }

  @Override
  public YieldCurveDefinition getDefinition(Currency currency, String name, InstantProvider version) {
    version = Instant.of(version);
    for (InterpolatedYieldCurveDefinitionSource source : _sources) {
      YieldCurveDefinition definition = source.getDefinition(currency, name, version);
      if (definition != null) {
        return definition;
      }
    }
    return null;
  }

}
