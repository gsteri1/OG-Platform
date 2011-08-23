/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.language.convert;

import java.util.HashMap;
import java.util.Map;

import com.opengamma.language.definition.JavaTypeInfo;
import com.opengamma.language.invoke.TypeConverter;
import com.opengamma.util.tuple.Pair;

/**
 * Partial implementation of a {@link TypeConverter} that handles a 1:1 mapping of types.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMappedConverter implements TypeConverter {

  /**
   * Converts from type {@code F} to type {@code T}.
   * 
   * @param <F> from type
   * @param <T> to type
   */
  public abstract static class Action<F, T> {

    /**
     * Cast the value to the correct from type, or return {@code null} if it is unsuitable.
     * 
     * @param value the value to cast
     * @return the cast value
     */
    protected F cast(Object value) {
      return (F) value;
    }

    /**
     * Convert the value.
     * 
     * @param value the value to convert
     * @return the converted value
     */
    protected abstract T convert(F value);

  }

  private final Map<JavaTypeInfo<?>, Pair<Map<JavaTypeInfo<?>, Integer>, Action<?, ?>>> _conversions = new HashMap<JavaTypeInfo<?>, Pair<Map<JavaTypeInfo<?>, Integer>, Action<?, ?>>>();
  private final String _key;

  protected AbstractMappedConverter(final String key) {
    _key = key;
  }

  protected <F, T> void conversion(final int cost, final JavaTypeInfo<F> sourceType, final JavaTypeInfo<T> targetType, final Action<F, T> action) {
    _conversions.put(targetType, (Pair) Pair.of(TypeMap.of(cost, sourceType), action));
  }

  protected <A, B> void conversion(final int cost, final JavaTypeInfo<A> typeA, final JavaTypeInfo<B> typeB, final Action<A, B> aToB, final Action<B, A> bToA) {
    conversion(cost, typeA, typeB, aToB);
    conversion(cost, typeB, typeA, bToA);
  }

  @Override
  public final boolean canConvertTo(final JavaTypeInfo<?> targetType) {
    return _conversions.containsKey(targetType);
  }

  @Override
  public final void convertValue(final ValueConversionContext conversionContext, final Object value, final JavaTypeInfo<?> type) {
    final Action<Object, Object> action = (Action<Object, Object>) _conversions.get(type).getSecond();
    final Object cast = action.cast(value);
    if (cast != null) {
      conversionContext.setResult(action.convert(cast));
    } else {
      conversionContext.setFail();
    }
  }

  @Override
  public final Map<JavaTypeInfo<?>, Integer> getConversionsTo(final JavaTypeInfo<?> targetType) {
    return _conversions.get(targetType).getFirst();
  }

  @Override
  public String getTypeConverterKey() {
    return _key;
  }

}
