/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.language;

import org.fudgemsg.FudgeFieldContainer;

import com.opengamma.util.ArgumentChecker;

/**
 * Utility methods for converting to/from the {@link Value} type.
 */
public final class ValueUtil {

  /**
   * Prevent instantiation.
   */
  private ValueUtil() {
  }

  public static Value of(final boolean boolValue) {
    final Value value = new Value();
    value.setBoolValue(boolValue);
    return value;
  }

  public static Value of(final double doubleValue) {
    final Value value = new Value();
    value.setDoubleValue(doubleValue);
    return value;
  }

  public static Value ofError(final int errorValue) {
    final Value value = new Value();
    value.setErrorValue(errorValue);
    return value;
  }

  public static Value of(final int intValue) {
    final Value value = new Value();
    value.setIntValue(intValue);
    return value;
  }

  public static Value of(final FudgeFieldContainer messageValue) {
    ArgumentChecker.notNull(messageValue, "messageValue");
    final Value value = new Value();
    value.setMessageValue(messageValue);
    return value;
  }

  public static Value of(final String stringValue) {
    ArgumentChecker.notNull(stringValue, "stringValue");
    final Value value = new Value();
    value.setStringValue(stringValue);
    return value;
  }

}