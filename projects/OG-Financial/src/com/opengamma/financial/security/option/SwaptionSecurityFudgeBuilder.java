/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.option;

import org.fudgemsg.FudgeMsg;
import org.fudgemsg.MutableFudgeMsg;
import org.fudgemsg.mapping.FudgeBuilder;
import org.fudgemsg.mapping.FudgeBuilderFor;
import org.fudgemsg.mapping.FudgeDeserializer;
import org.fudgemsg.mapping.FudgeSerializer;

import com.opengamma.financial.security.FinancialSecurityFudgeBuilder;
import com.opengamma.financial.security.LongShort;
import com.opengamma.id.ExternalIdFudgeBuilder;
import com.opengamma.util.fudgemsg.AbstractFudgeBuilder;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.ExpiryFudgeBuilder;

/**
 * A Fudge builder for {@code SwaptionSecurity}.
 */
@FudgeBuilderFor(SwaptionSecurity.class)
public class SwaptionSecurityFudgeBuilder extends AbstractFudgeBuilder implements FudgeBuilder<SwaptionSecurity> {

  /** Field name. */
  public static final String IS_PAYER_FIELD_NAME = "isPayer";
  /** Field name. */
  public static final String UNDERLYING_IDENTIFIER_FIELD_NAME = "underlyingIdentifier";
  /** Field name. */
  public static final String IS_LONG_FIELD_NAME = "isLong";
  /** Field name. */
  public static final String EXPIRY_FIELD_NAME = "expiry";
  /** Field name. */
  public static final String IS_CASH_SETTLED_FIELD_NAME = "isCashSettled";
  /** Field name. */
  public static final String CURRENCY_FIELD_NAME = "currency";

  @Override
  public MutableFudgeMsg buildMessage(FudgeSerializer serializer, SwaptionSecurity object) {
    final MutableFudgeMsg msg = serializer.newMessage();
    SwaptionSecurityFudgeBuilder.toFudgeMsg(serializer, object, msg);
    return msg;
  }

  public static void toFudgeMsg(FudgeSerializer serializer, SwaptionSecurity object, final MutableFudgeMsg msg) {
    FinancialSecurityFudgeBuilder.toFudgeMsg(serializer, object, msg);
    addToMessage(msg, IS_PAYER_FIELD_NAME, object.isPayer());
    addToMessage(msg, UNDERLYING_IDENTIFIER_FIELD_NAME, ExternalIdFudgeBuilder.toFudgeMsg(serializer, object.getUnderlyingId()));
    addToMessage(msg, IS_LONG_FIELD_NAME, object.isLong());
    addToMessage(msg, EXPIRY_FIELD_NAME, ExpiryFudgeBuilder.toFudgeMsg(serializer, object.getExpiry()));
    addToMessage(msg, IS_CASH_SETTLED_FIELD_NAME, object.isCashSettled());
    addToMessage(msg, CURRENCY_FIELD_NAME, object.getCurrency());
  }

  @Override
  public SwaptionSecurity buildObject(FudgeDeserializer deserializer, FudgeMsg msg) {
    SwaptionSecurity object = new SwaptionSecurity();
    SwaptionSecurityFudgeBuilder.fromFudgeMsg(deserializer, msg, object);
    return object;
  }

  public static void fromFudgeMsg(FudgeDeserializer deserializer, FudgeMsg msg, SwaptionSecurity object) {
    FinancialSecurityFudgeBuilder.fromFudgeMsg(deserializer, msg, object);
    object.setPayer(msg.getBoolean(IS_PAYER_FIELD_NAME));
    object.setUnderlyingId(ExternalIdFudgeBuilder.fromFudgeMsg(deserializer, msg.getMessage(UNDERLYING_IDENTIFIER_FIELD_NAME)));
    object.setLongShort(LongShort.ofLong(msg.getBoolean(IS_LONG_FIELD_NAME)));
    object.setExpiry(ExpiryFudgeBuilder.fromFudgeMsg(deserializer, msg.getMessage(EXPIRY_FIELD_NAME)));
    object.setCashSettled(msg.getBoolean(IS_CASH_SETTLED_FIELD_NAME));
    object.setCurrency(msg.getValue(Currency.class, CURRENCY_FIELD_NAME));
  }

}
