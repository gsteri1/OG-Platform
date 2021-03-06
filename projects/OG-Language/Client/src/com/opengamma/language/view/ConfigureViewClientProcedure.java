/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.language.view;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.engine.marketdata.MarketDataInjector;
import com.opengamma.language.config.ConfigurationDelta;
import com.opengamma.language.config.ConfigurationItem;
import com.opengamma.language.config.ConfigurationItemVisitor;
import com.opengamma.language.config.MarketDataOverride;
import com.opengamma.language.config.ValueProperty;
import com.opengamma.language.config.ViewCalculationRate;
import com.opengamma.language.context.SessionContext;
import com.opengamma.language.definition.DefinitionAnnotater;
import com.opengamma.language.definition.JavaTypeInfo;
import com.opengamma.language.definition.MetaParameter;
import com.opengamma.language.procedure.AbstractProcedureInvoker;
import com.opengamma.language.procedure.MetaProcedure;
import com.opengamma.language.procedure.PublishedProcedure;

/**
 * Makes a configuration change to a view client.
 */
public class ConfigureViewClientProcedure extends AbstractProcedureInvoker.NoResult implements PublishedProcedure {

  private static final Logger s_logger = LoggerFactory.getLogger(ConfigureViewClientProcedure.class);

  /**
   * Default instance.
   */
  public static final ConfigureViewClientProcedure INSTANCE = new ConfigureViewClientProcedure();

  private final MetaProcedure _meta;

  private static List<MetaParameter> parameters() {
    final MetaParameter viewClient = new MetaParameter("viewClient", JavaTypeInfo.builder(ViewClientHandle.class).get());
    final MetaParameter configuration = new MetaParameter("configuration", JavaTypeInfo.builder(Set.class).allowNull().parameter(JavaTypeInfo.builder(ConfigurationItem.class).get()).get());
    return Arrays.asList(viewClient, configuration);
  }

  private ConfigureViewClientProcedure(final DefinitionAnnotater info) {
    super(info.annotate(parameters()));
    _meta = info.annotate(new MetaProcedure("ConfigureViewClient", getParameters(), this));
  }

  protected ConfigureViewClientProcedure() {
    this(new DefinitionAnnotater(ConfigureViewClientProcedure.class));
  }

  private abstract static class ConfigurationVisitor implements ConfigurationItemVisitor<Boolean> {

    private final UserViewClient _viewClient;

    public ConfigurationVisitor(final UserViewClient viewClient) {
      _viewClient = viewClient;
    }

    protected UserViewClient getViewClient() {
      return _viewClient;
    }

    @Override
    public final Boolean visitValueProperty(final ValueProperty valueProperty) {
      s_logger.debug("Ignoring {}", valueProperty);
      return Boolean.FALSE;
    }

    @Override
    public final Boolean visitViewCalculationRate(final ViewCalculationRate viewCalculationRate) {
      s_logger.debug("Ignoring {}", viewCalculationRate);
      return Boolean.FALSE;
    }

  }

  private static final class AddConfiguration extends ConfigurationVisitor {

    public AddConfiguration(final UserViewClient viewClient) {
      super(viewClient);
    }

    @Override
    public Boolean visitMarketDataOverride(final MarketDataOverride marketDataOverride) {
      s_logger.debug("Applying {}", marketDataOverride);
      final MarketDataInjector injector = getViewClient().getViewClient().getLiveDataOverrideInjector();
      if (marketDataOverride.getValueRequirement() != null) {
        injector.addValue(marketDataOverride.getValueRequirement(), marketDataOverride.getValue());
      } else {
        injector.addValue(marketDataOverride.getIdentifier(), marketDataOverride.getValueName(), marketDataOverride.getValue());
      }
      return Boolean.TRUE;
    }

  }

  private static final class RemoveConfiguration extends ConfigurationVisitor {

    public RemoveConfiguration(final UserViewClient viewClient) {
      super(viewClient);
    }

    @Override
    public Boolean visitMarketDataOverride(final MarketDataOverride marketDataOverride) {
      s_logger.debug("Removing {}", marketDataOverride);
      final MarketDataInjector injector = getViewClient().getViewClient().getLiveDataOverrideInjector();
      if (marketDataOverride.getValueRequirement() != null) {
        injector.removeValue(marketDataOverride.getValueRequirement());
      } else {
        injector.removeValue(marketDataOverride.getIdentifier(), marketDataOverride.getValueName());
      }
      return Boolean.TRUE;
    }

  }

  public static void invoke(final UserViewClient viewClient, final Set<ConfigurationItem> configuration) {
    final Set<ConfigurationItem> previousConfiguration = viewClient.getAndSetConfiguration(configuration);
    final ConfigurationDelta delta = ConfigurationDelta.of(previousConfiguration, configuration);
    int itemsAdded = 0;
    int itemsRemoved = 0;
    if (delta.hasChanged()) {
      Set<ConfigurationItem> items = delta.getAdded();
      if (!items.isEmpty()) {
        final AddConfiguration visitor = new AddConfiguration(viewClient);
        for (ConfigurationItem item : items) {
          if (item.accept(visitor) == Boolean.TRUE) {
            itemsAdded++;
          }
        }
      }
      items = delta.getRemoved();
      if (!items.isEmpty()) {
        final RemoveConfiguration visitor = new RemoveConfiguration(viewClient);
        for (ConfigurationItem item : items) {
          if (item.accept(visitor) == Boolean.TRUE) {
            itemsRemoved++;
          }
        }
      }
    }
    s_logger.info("{} items added, {} items removed from view client configuration", itemsAdded, itemsRemoved);
  }

  // AbstractProcedureInvoker.NoResult

  @SuppressWarnings("unchecked")
  @Override
  protected void invokeImpl(final SessionContext sessionContext, final Object[] parameters) {
    final ViewClientHandle viewClient = (ViewClientHandle) parameters[0];
    final Set<ConfigurationItem> configuration = (Set<ConfigurationItem>) parameters[1];
    invoke(viewClient.get(), configuration);
    viewClient.unlock();
  }

  // PublishedProcedure

  @Override
  public MetaProcedure getMetaProcedure() {
    return _meta;
  }

}
