/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.ircurve;

import javax.time.calendar.DateAdjuster;
import javax.time.calendar.LocalDate;
import javax.time.calendar.MonthOfYear;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.id.IdentificationScheme;
import com.opengamma.id.Identifier;
import com.opengamma.util.time.Tenor;

/**
 * 
 */
public class BloombergFutureCurveInstrumentProvider implements CurveInstrumentProvider {

  private static BiMap<MonthOfYear, Character> s_monthCode;
  private String _futurePrefix;
  private String _marketSector;
  
  static {
    s_monthCode = HashBiMap.create();
    s_monthCode.put(MonthOfYear.JANUARY, 'F');
    s_monthCode.put(MonthOfYear.FEBRUARY, 'G');
    s_monthCode.put(MonthOfYear.MARCH, 'H');
    s_monthCode.put(MonthOfYear.APRIL, 'J');
    s_monthCode.put(MonthOfYear.MAY, 'K');
    s_monthCode.put(MonthOfYear.JUNE, 'M');
    s_monthCode.put(MonthOfYear.JULY, 'N');
    s_monthCode.put(MonthOfYear.AUGUST, 'Q');
    s_monthCode.put(MonthOfYear.SEPTEMBER, 'U');
    s_monthCode.put(MonthOfYear.OCTOBER, 'V');
    s_monthCode.put(MonthOfYear.NOVEMBER, 'X');
    s_monthCode.put(MonthOfYear.DECEMBER, 'Z');
  }

  public BloombergFutureCurveInstrumentProvider(String futurePrefix, String marketSector) {
    _futurePrefix = futurePrefix;
    _marketSector = marketSector;
  }
  
  @Override
  public Identifier getInstrument(LocalDate curveDate, Tenor tenor) {
    throw new OpenGammaRuntimeException("Only futures supported");
  }
  
  @Override
  public Identifier getInstrument(LocalDate curveDate, Tenor tenor, int numQuarterlyFuturesFromTenor) {
    return createQuarterlyIRFutureStrips(curveDate, tenor, numQuarterlyFuturesFromTenor, _futurePrefix, " " + _marketSector);
  }
    
  private static final DateAdjuster s_nextExpiryAdjuster = new NextExpiryAdjuster();
    
  private static final IdentificationScheme SCHEME = IdentificationScheme.BLOOMBERG_TICKER; 
  
  private Identifier createQuarterlyIRFutureStrips(LocalDate curveDate, Tenor tenor, int numQuartlyFuturesFromTenor, String prefix, String postfix) {
    LocalDate curveFutureStartDate = curveDate.plus(tenor.getPeriod());
    LocalDate futureExpiryDate = curveFutureStartDate.with(s_nextExpiryAdjuster);
    for (int i = 1; i < numQuartlyFuturesFromTenor; i++) {
      futureExpiryDate = futureExpiryDate.plusDays(7); // get us clear of expiry, shouldn't be necessary.
      futureExpiryDate = futureExpiryDate.with(s_nextExpiryAdjuster);
    }
    StringBuilder futureCode = new StringBuilder();
    futureCode.append(prefix);
    futureCode.append(s_monthCode.get(futureExpiryDate.getMonthOfYear()));
    LocalDate today = LocalDate.nowSystemClock();
    if (futureExpiryDate.isBefore(today)) {
      futureCode.append(Integer.toString(futureExpiryDate.getYear() % 100));
    } else {
      futureCode.append(Integer.toString(futureExpiryDate.getYear() % 10));
    }
    futureCode.append(postfix);
    return Identifier.of(SCHEME, futureCode.toString());
  }
  
  // for serialisation only
  public String getFuturePrefix() {
    return _futurePrefix;
  }
  
  // for serialisation only  
  public String getMarketSector() {
    return _marketSector;
  }

  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (!(o instanceof BloombergFutureCurveInstrumentProvider)) {
      return false;
    }
    BloombergFutureCurveInstrumentProvider other = (BloombergFutureCurveInstrumentProvider) o;
    return getFuturePrefix().equals(other.getFuturePrefix()) && getMarketSector().equals(other.getMarketSector());
  }
  
  public int hashCode() {
    return getFuturePrefix().hashCode() ^ getMarketSector().hashCode() * (2 ^ 16);
  }
}