/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.language.value;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.core.position.PortfolioNode;
import com.opengamma.core.position.Position;
import com.opengamma.core.position.PositionSource;
import com.opengamma.core.position.Trade;
import com.opengamma.core.security.Security;
import com.opengamma.core.security.SecuritySource;
import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.id.ExternalId;
import com.opengamma.id.UniqueId;
import com.opengamma.language.context.SessionContext;
import com.opengamma.language.definition.DefinitionAnnotater;
import com.opengamma.language.definition.JavaTypeInfo;
import com.opengamma.language.definition.MetaParameter;
import com.opengamma.language.function.AbstractFunctionInvoker;
import com.opengamma.language.function.MetaFunction;
import com.opengamma.language.function.PublishedFunction;

/**
 * Expands a Fudge representation of a {@link List} of {@link ComputedValue} objects into a 2D structure.
 */
public class ExpandComputedValuesFunction extends AbstractFunctionInvoker implements PublishedFunction {

  private static final Logger s_logger = LoggerFactory.getLogger(ExpandComputedValuesFunction.class);

  private final MetaFunction _meta;

  private static List<MetaParameter> parameters() {
    return Arrays.asList(
        new MetaParameter("values", JavaTypeInfo.builder(List.class).parameter(ComputedValue.class).get()),
        new MetaParameter("includeIdentifier", JavaTypeInfo.builder(Boolean.class).defaultValue(false).get()),
        new MetaParameter("includeName", JavaTypeInfo.builder(Boolean.class).defaultValue(true).get()),
        new MetaParameter("includeValue", JavaTypeInfo.builder(Boolean.class).defaultValue(true).get()),
        new MetaParameter("includeType", JavaTypeInfo.builder(Boolean.class).defaultValue(false).get()));
  }

  private ExpandComputedValuesFunction(final DefinitionAnnotater info) {
    super(info.annotate(parameters()));
    _meta = info.annotate(new MetaFunction("ExpandComputedValues", getParameters(), this));
  }

  public ExpandComputedValuesFunction() {
    this(new DefinitionAnnotater(ExpandComputedValuesFunction.class));
  }

  @Override
  public MetaFunction getMetaFunction() {
    return _meta;
  }

  private static String getName(final SessionContext sessionContext, final ComputationTargetSpecification targetSpec) {
    switch (targetSpec.getType()) {
      case PORTFOLIO_NODE: {
        final PositionSource positions = sessionContext.getGlobalContext().getPositionSource();
        if (positions != null) {
          final PortfolioNode node = positions.getPortfolioNode(targetSpec.getUniqueId());
          if (node != null) {
            return node.getName();
          } else {
            s_logger.warn("Node {} not found in position source", targetSpec);
          }
        }
        return "Node " + targetSpec.getUniqueId();
      }
      case POSITION: {
        final PositionSource positions = sessionContext.getGlobalContext().getPositionSource();
        final SecuritySource securities = sessionContext.getGlobalContext().getSecuritySource();
        if ((positions != null) && (securities != null)) {
          final Position position = positions.getPosition(targetSpec.getUniqueId());
          if (position != null) {
            final Security security = position.getSecurityLink().resolve(securities);
            if (security != null) {
              return position.getQuantity() + " x " + security.getName();
            } else {
              s_logger.warn("Security {} not found in security source for position {}", position.getSecurityLink(), targetSpec);
            }
          } else {
            s_logger.warn("Position {} not found in position source", targetSpec);
          }
        }
        return "Position " + targetSpec.getUniqueId();
      }
      case SECURITY: {
        final SecuritySource securities = sessionContext.getGlobalContext().getSecuritySource();
        if (securities != null) {
          final Security security = securities.getSecurity(targetSpec.getUniqueId());
          if (security != null) {
            return security.getName();
          } else {
            s_logger.warn("Security {} not found in security source", targetSpec);
          }
        }
        return "Security " + targetSpec.getUniqueId();
      }
      case PRIMITIVE:
        return "PRIMITIVE";
      case TRADE: {
        final PositionSource positions = sessionContext.getGlobalContext().getPositionSource();
        final SecuritySource securities = sessionContext.getGlobalContext().getSecuritySource();
        if ((positions != null) && (securities != null)) {
          final Trade trade = positions.getTrade(targetSpec.getUniqueId());
          if (trade != null) {
            final Security security = trade.getSecurityLink().resolve(securities);
            if (security != null) {
              return trade.getQuantity() + " x " + security.getName() + " (" + trade.getTradeDate() + ")";
            } else {
              s_logger.warn("Security {} not found in security source for trade {}", trade.getSecurityLink(), targetSpec);
            }
          } else {
            s_logger.warn("Trade {} not found in position source", targetSpec);
          }
        }
        return "Trade " + targetSpec.getUniqueId();
      }
      default:
        throw new IllegalStateException();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  protected Object invokeImpl(final SessionContext sessionContext, final Object[] parameters) {
    final List<ComputedValue> values = (List<ComputedValue>) parameters[0];
    final boolean includeIdentifier = (Boolean) parameters[1];
    final boolean includeName = (Boolean) parameters[2];
    final boolean includeValue = (Boolean) parameters[3];
    final boolean includeType = (Boolean) parameters[4];
    int columns = 0;
    if (includeIdentifier) {
      columns++;
    }
    if (includeName) {
      columns++;
    }
    if (includeValue) {
      columns++;
    }
    if (includeType) {
      columns++;
    }
    final Object[][] result = new Object[values.size()][columns];
    int row = 0;
    for (ComputedValue value : values) {
      final Object[] resultRow = result[row++];
      columns = 0;
      if (includeIdentifier) {
        final UniqueId uid = value.getSpecification().getTargetSpecification().getUniqueId();
        if (uid != null) {
          resultRow[columns++] = uid;
        } else {
          final ExternalId eid = value.getSpecification().getTargetSpecification().getIdentifier();
          if (eid != null) {
            resultRow[columns++] = eid;
          } else {
            resultRow[columns++] = null;
          }
        }
      }
      if (includeName) {
        resultRow[columns++] = getName(sessionContext, value.getSpecification().getTargetSpecification());
      }
      if (includeValue) {
        resultRow[columns++] = value.getValue();
      }
      if (includeType) {
        resultRow[columns++] = value.getSpecification().getTargetSpecification().getType();
      }
    }
    return result;
  }

}
