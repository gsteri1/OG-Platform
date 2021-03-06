/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.master.historicaltimeseries;

import javax.time.calendar.LocalDate;

import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.UniqueId;
import com.opengamma.util.PublicSPI;

/**
 * Resolves an identifier, such as a security, to the appropriate historical time-series information.
 * <p>
 * Time-series information includes data source, data provider, data field and observation time.
 * This can be used to lookup the time-series itself.
 */
@PublicSPI
public interface HistoricalTimeSeriesResolver {

  /**
   * Find the best matching time-series for an identifier and data field.
   * <p>
   * The desired series is specified by identifier bundle, typically a security,
   * and the data type, such as "price" or "volume".
   * However, the underlying sources of data may contain multiple matching time-series.
   * The resolver allows the preferred series to be chosen based on a key.
   * The meaning of the key is resolver specific, and it might be treated as a DSL or a configuration key.
   * 
   * @param dataField  the type of data that the time-series represents, not null
   * @param identifierBundle  the bundle of identifiers to resolve, not null
   * @param identifierValidityDate  the date that the identifier must be valid on, null to use all identifiers
   * @param resolutionKey  a key defining how the resolution is to occur, null for the default best match
   * @return the best matching time-series unique identifier, null if unable to find a match
   */
  UniqueId resolve(String dataField, ExternalIdBundle identifierBundle, LocalDate identifierValidityDate, String resolutionKey);

}
