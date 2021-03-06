/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view.compilation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.time.Instant;

import com.opengamma.core.position.Portfolio;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.engine.view.ViewDefinition;

/**
 * Provides access to a snapshot of the basic state required for computation of a view, valid for a period of valuation
 * times.
 */
public interface CompiledViewDefinition {
  
  /**
   * Gets the view definition which was compiled.
   * 
   * @return the view definition, not {@code null}
   */
  ViewDefinition getViewDefinition();
  
  /**
   * Gets the fully-resolved portfolio associated with the view definition.
   * 
   * @return the fully-resolved portfolio, or {@code null} if no portfolio is associated with the view definition
   */
  Portfolio getPortfolio();
  
  /**
   * Gets a compiled view calculation configuration.
   * 
   * @param viewCalculationConfiguration  the name of the calculation configuration, not {@code null}
   * @return the compiled view calculation configuration, or {@code null} if no calculation configuration exists with
   *         that name. 
   */
  CompiledViewCalculationConfiguration getCompiledCalculationConfiguration(String viewCalculationConfiguration);
  
  /**
   * Gets all compiled view calculation configurations.
   * 
   * @return a collection of all compiled view calcualtion configurations, not {@code null}
   */
  Collection<CompiledViewCalculationConfiguration> getCompiledCalculationConfigurations();
  
  /**
   * Gets the combined market data requirements of all calculation configurations.
   * 
   * @return a map from each stated value requirement to the resolved value specification for all market data, not
   *         {@code null}  
   */
  Map<ValueRequirement, ValueSpecification> getMarketDataRequirements();
  
  /**
   * Gets the combined computation targets across every calculation configuration.
   * 
   * @return a set of all computation targets, not {@code null}
   */
  Set<ComputationTarget> getComputationTargets();
  
  /**
   * Gets the instant from which the compiled view definition is valid, inclusive.
   *  
   * @return the instant from which the evaluation model is valid, or {@code null} to indicate no restriction
   */
  Instant getValidFrom();
  
  /**
   * Gets the instant to which the compiled view definition is valid, inclusive.
   * 
   * @return the instant to which the evaluation model is valid, or {@code null} to indicate no restriction 
   */
  Instant getValidTo();
  
}
