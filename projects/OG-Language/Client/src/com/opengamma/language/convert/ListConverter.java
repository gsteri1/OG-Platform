/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.language.convert;

import static com.opengamma.language.convert.TypeMap.ZERO_LOSS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.opengamma.language.Value;
import com.opengamma.language.definition.JavaTypeInfo;
import com.opengamma.language.invoke.AbstractTypeConverter;

/**
 * Converts a list of A to Value[]
 */
public class ListConverter extends AbstractTypeConverter {

  private static final JavaTypeInfo<Value> VALUE = JavaTypeInfo.builder(Value.class).get();
  private static final JavaTypeInfo<Value[]> VALUES = JavaTypeInfo.builder(Value[].class).get();
  @SuppressWarnings("unchecked")
  private static final JavaTypeInfo<List> LIST = JavaTypeInfo.builder(List.class).get();

  private static final TypeMap TO_LIST = TypeMap.of(ZERO_LOSS, VALUES);
  private static final TypeMap FROM_LIST = TypeMap.of(ZERO_LOSS, LIST);

  @Override
  public boolean canConvertTo(final JavaTypeInfo<?> targetType) {
    return (targetType.getRawClass() == List.class) || (targetType.getRawClass() == Value[].class);
  }

  @Override
  public void convertValue(final ValueConversionContext conversionContext, final Object value, final JavaTypeInfo<?> type) {
    if (type.getRawClass() == List.class) {
      // Converting from Values[] to List
      final Value[] values = (Value[]) value;
      final JavaTypeInfo<?> listType = type.getParameterizedType(0);
      final List<Object> result = new ArrayList<Object>(values.length);
      for (Value entry : values) {
        conversionContext.convertValue(entry, listType);
        if (conversionContext.isFailed()) {
          conversionContext.setFail();
          return;
        }
        result.add(conversionContext.getResult());
      }
      conversionContext.setResult(result);
    } else {
      // Converting from List to Values[]
      final List<?> list = (List<?>) value;
      final Value[] result = new Value[list.size()];
      int i = 0;
      for (Object entry : list) {
        if (entry == null) {
          result[i++] = new Value();
        } else {
          conversionContext.convertValue(entry, VALUE);
          if (conversionContext.isFailed()) {
            conversionContext.setFail();
            return;
          }
          result[i++] = conversionContext.getResult();
        }
      }
      conversionContext.setResult(result);
    }
  }

  @Override
  public Map<JavaTypeInfo<?>, Integer> getConversionsTo(final JavaTypeInfo<?> targetType) {
    if (targetType.getRawClass() == List.class) {
      return TO_LIST;
    } else {
      return FROM_LIST;
    }
  }

}
