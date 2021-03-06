/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.web.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.engine.function.config.FunctionConfiguration;
import com.opengamma.engine.function.config.ParameterizedFunctionConfiguration;
import com.opengamma.engine.function.config.RepositoryConfiguration;
import com.opengamma.engine.function.config.RepositoryConfigurationSource;
import com.opengamma.financial.analytics.ircurve.MarketInstrumentImpliedYieldCurveFunction;
import com.opengamma.financial.analytics.ircurve.YieldCurveDefinition;
import com.opengamma.financial.analytics.ircurve.YieldCurveInterpolatingFunction;
import com.opengamma.financial.analytics.ircurve.YieldCurveMarketDataFunction;
import com.opengamma.financial.analytics.volatility.cube.BloombergVolatilityCubeDefinitionSource;
import com.opengamma.financial.analytics.volatility.cube.VolatilityCubeFunction;
import com.opengamma.financial.analytics.volatility.cube.VolatilityCubeInstrumentProvider;
import com.opengamma.financial.analytics.volatility.cube.VolatilityCubeMarketDataFunction;
import com.opengamma.financial.convention.ConventionBundleSource;
import com.opengamma.master.config.ConfigDocument;
import com.opengamma.master.config.ConfigMaster;
import com.opengamma.master.config.ConfigSearchRequest;
import com.opengamma.master.config.ConfigSearchResult;
import com.opengamma.util.SingletonFactoryBean;
import com.opengamma.util.money.Currency;

/**
 * Creates function repository configuration for curve supplying functions.
 * 
 * Note [PLAT-1094] - the functions should really be built by scanning the curves and currencies available. 
 */
public class DemoCurveFunctionConfiguration extends SingletonFactoryBean<RepositoryConfigurationSource> {

  private static final Logger s_logger = LoggerFactory.getLogger(DemoCurveFunctionConfiguration.class);

  private ConfigMaster _configMaster;
  @SuppressWarnings("unused")
  private ConventionBundleSource _conventionBundleSource; //TODO not sure if we'll need this in the future

  public void setConfigMaster(final ConfigMaster configMaster) {
    _configMaster = configMaster;
  }

  public void setConventionBundleSource(final ConventionBundleSource conventionBundleSource) {
    _conventionBundleSource = conventionBundleSource;
  }

  public RepositoryConfiguration constructRepositoryConfiguration() {
    final List<FunctionConfiguration> configs = new ArrayList<FunctionConfiguration>();

    if (_configMaster != null) {
      // [PLAT-1094] Scan the config master for documents. This is probably in the wrong place; the code should live in OG-Financial as it is
      // tightly coupled to the ConfigDbInterpolatedYieldCurveSource and MarketInstrumentImpliedYieldCurveFunction classes
      final ConfigSearchRequest<YieldCurveDefinition> searchRequest = new ConfigSearchRequest<YieldCurveDefinition>();
      searchRequest.setType(YieldCurveDefinition.class);

      final ConfigSearchResult<YieldCurveDefinition> searchResult = _configMaster.search(searchRequest);
      final Map<String, Set<String>> currencyToCurves = new HashMap<String, Set<String>>();
      for (ConfigDocument<YieldCurveDefinition> configDocument : searchResult.getDocuments()) {
        final String documentName = configDocument.getName();
        final int underscore = documentName.lastIndexOf('_');
        if (underscore <= 0) {
          continue;
        }
        final String curveName = documentName.substring(0, underscore);
        final String currencyISO = documentName.substring(underscore + 1);
        s_logger.debug("Found {} curve for {}", curveName, currencyISO);
        if (!currencyToCurves.containsKey(currencyISO)) {
          currencyToCurves.put(currencyISO, new HashSet<String>());
        }
        currencyToCurves.get(currencyISO).add(curveName);
      }
      for (Map.Entry<String, Set<String>> currencyCurves : currencyToCurves.entrySet()) {
        final String currencyISO = currencyCurves.getKey();
        final Set<String> curveNames = currencyCurves.getValue();
        if (curveNames.contains("SECONDARY")) {
          addYieldCurveFunction(configs, currencyISO, "SECONDARY", MarketInstrumentImpliedYieldCurveFunction.PAR_RATE_STRING);
          addYieldCurveFunction(configs, currencyISO, "SECONDARY", MarketInstrumentImpliedYieldCurveFunction.PRESENT_VALUE_STRING);
        }
        if (curveNames.contains("FUNDING") && curveNames.contains("FORWARD_3M")) {
          addYieldCurveFunction(configs, currencyISO, "FUNDING", "FORWARD_3M", MarketInstrumentImpliedYieldCurveFunction.PRESENT_VALUE_STRING);
          addYieldCurveFunction(configs, currencyISO, "FUNDING", "FORWARD_3M", MarketInstrumentImpliedYieldCurveFunction.PAR_RATE_STRING);
        } else if (curveNames.contains("FUNDING") && curveNames.contains("FORWARD_6M")) {
          addYieldCurveFunction(configs, currencyISO, "FUNDING", "FORWARD_6M", MarketInstrumentImpliedYieldCurveFunction.PRESENT_VALUE_STRING);
          addYieldCurveFunction(configs, currencyISO, "FUNDING", "FORWARD_6M", MarketInstrumentImpliedYieldCurveFunction.PAR_RATE_STRING);
        }
      }
    } else {
      //       [PLAT-1094] This is the wrong approach and should be disposed of at the earliest opportunity
      s_logger.warn("[PLAT-1094] Using hardcoded curve definitions");
      addYieldCurveFunction(configs, "USD", "FUNDING", "FORWARD_3M", MarketInstrumentImpliedYieldCurveFunction.PRESENT_VALUE_STRING);
      addYieldCurveFunction(configs, "GBP", "FUNDING", "FORWARD_6M", MarketInstrumentImpliedYieldCurveFunction.PRESENT_VALUE_STRING);
      addYieldCurveFunction(configs, "USD", "FUNDING", "FORWARD_3M", MarketInstrumentImpliedYieldCurveFunction.PAR_RATE_STRING);
      addYieldCurveFunction(configs, "GBP", "FUNDING", "FORWARD_6M", MarketInstrumentImpliedYieldCurveFunction.PAR_RATE_STRING);
    }

    //These need to be replaced with meaningful cube defns
    addVolatilityCubeFunction(configs, "USD", "BLOOMBERG");

    Set<Currency> volCubeCurrencies = VolatilityCubeInstrumentProvider.BLOOMBERG.getAllCurrencies();
    for (Currency currency : volCubeCurrencies) {
      addVolatilityCubeFunction(configs, currency.getCode(), BloombergVolatilityCubeDefinitionSource.DEFINITION_NAME);
    }

    s_logger.info("Created repository configuration with {} curve provider functions", configs.size());
    return new RepositoryConfiguration(configs);
  }

  private void addVolatilityCubeFunction(List<FunctionConfiguration> configs, String... parameters) {
    addVolatilityCubeFunction(configs, Arrays.asList(parameters));
  }

  private void addVolatilityCubeFunction(final List<FunctionConfiguration> configs, List<String> parameters) {
    if (parameters.size() != 2) {
      throw new IllegalArgumentException();
    }

    configs.add(new ParameterizedFunctionConfiguration(VolatilityCubeFunction.class.getName(), parameters));
    configs.add(new ParameterizedFunctionConfiguration(VolatilityCubeMarketDataFunction.class.getName(), parameters));
  }

  private void addYieldCurveFunction(final List<FunctionConfiguration> configs, String... parameters) {
    addYieldCurveFunction(configs, Arrays.asList(parameters));
  }

  private void addYieldCurveFunction(final List<FunctionConfiguration> configs, List<String> parameters) {
    if (parameters.size() < 2) {
      throw new IllegalArgumentException();
    }

    configs.add(new ParameterizedFunctionConfiguration(MarketInstrumentImpliedYieldCurveFunction.class.getName(), parameters));
    for (int i = 1; i < parameters.size() - 1; i++) {
      configs.add(new ParameterizedFunctionConfiguration(YieldCurveMarketDataFunction.class.getName(), Arrays.asList(parameters.get(0), parameters.get(i))));
      configs.add(new ParameterizedFunctionConfiguration(YieldCurveInterpolatingFunction.class.getName(), Arrays.asList(parameters.get(0), parameters.get(i))));
    }
  }

  public RepositoryConfigurationSource constructRepositoryConfigurationSource() {
    return new RepositoryConfigurationSource() {
      private final RepositoryConfiguration _config = constructRepositoryConfiguration();

      @Override
      public RepositoryConfiguration getRepositoryConfiguration() {
        return _config;
      }
    };
  }

  @Override
  protected RepositoryConfigurationSource createObject() {
    return constructRepositoryConfigurationSource();
  }

}
