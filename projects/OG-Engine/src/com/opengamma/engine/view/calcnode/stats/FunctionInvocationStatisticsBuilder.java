/**
 * Copyright (C) 2009 - Present by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.view.calcnode.stats;

import org.fudgemsg.FudgeFieldContainer;
import org.fudgemsg.MutableFudgeFieldContainer;
import org.fudgemsg.mapping.FudgeBuilder;
import org.fudgemsg.mapping.FudgeBuilderFor;
import org.fudgemsg.mapping.FudgeDeserializationContext;
import org.fudgemsg.mapping.FudgeSerializationContext;

/**
 * Fudge message builder for {@code FunctionInvocationStatistics}.
 */
@FudgeBuilderFor(FunctionInvocationStatistics.class)
public class FunctionInvocationStatisticsBuilder implements FudgeBuilder<FunctionInvocationStatistics> {

  private static final String FUNCTION_IDENTIFIER_FIELD_NAME = "functionIdentifier";
  private static final String INVOCATION_COST_FIELD_NAME = "invocationCost";
  private static final String DATA_OUTPUT_COST_FIELD_NAME = "dataOutputCost";
  private static final String DATA_INPUT_COST_FIELD_NAME = "dataInputCost";

  @Override
  public MutableFudgeFieldContainer buildMessage(final FudgeSerializationContext context, final FunctionInvocationStatistics statistics) {
    final MutableFudgeFieldContainer message = context.newMessage();
    message.add(FUNCTION_IDENTIFIER_FIELD_NAME, statistics.getFunctionId());
    message.add(INVOCATION_COST_FIELD_NAME, statistics.getInvocationCost());
    message.add(DATA_INPUT_COST_FIELD_NAME, statistics.getDataInputCost());
    message.add(DATA_OUTPUT_COST_FIELD_NAME, statistics.getDataOutputCost());
    return message;
  }

  @Override
  public FunctionInvocationStatistics buildObject(final FudgeDeserializationContext context, final FudgeFieldContainer message) {
    final FunctionInvocationStatistics statistics = new FunctionInvocationStatistics(message.getString(FUNCTION_IDENTIFIER_FIELD_NAME));
    statistics.recordInvocation(1, message.getDouble(INVOCATION_COST_FIELD_NAME), message.getDouble(DATA_INPUT_COST_FIELD_NAME), message.getDouble(DATA_OUTPUT_COST_FIELD_NAME));
    return statistics;
  }

}
