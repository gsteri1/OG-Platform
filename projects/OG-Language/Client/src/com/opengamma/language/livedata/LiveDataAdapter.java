/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.language.livedata;

import com.opengamma.language.connector.LiveData;

/**
 * Pass through adapter to allow filtering of all incoming live-data messages.
 *
 * @param <T1>  the return type
 * @param <T2>  the data type
 */
public class LiveDataAdapter<T1, T2> implements LiveDataVisitor<T1, T2> {

  private final LiveDataVisitor<T1, T2> _underlying;

  protected LiveDataAdapter(final LiveDataVisitor<T1, T2> underlying) {
    _underlying = underlying;
  }

  protected LiveDataVisitor<T1, T2> getUnderlying() {
    return _underlying;
  }

  @Override
  public T1 visitCustom(final Custom message, final T2 data) {
    return getUnderlying().visitCustom(message, data);
  }

  @Override
  public T1 visitQueryAvailable(final QueryAvailable message, final T2 data) {
    return getUnderlying().visitQueryAvailable(message, data);
  }

  @Override
  public T1 visitUnexpected(final LiveData message, final T2 data) {
    return getUnderlying().visitUnexpected(message, data);
  }

}
