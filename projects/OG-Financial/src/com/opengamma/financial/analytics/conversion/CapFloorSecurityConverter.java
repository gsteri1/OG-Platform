/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.conversion;

import javax.time.calendar.Period;
import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.Validate;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.core.holiday.HolidaySource;
import com.opengamma.financial.convention.ConventionBundle;
import com.opengamma.financial.convention.ConventionBundleSource;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.frequency.Frequency;
import com.opengamma.financial.instrument.FixedIncomeInstrumentConverter;
import com.opengamma.financial.instrument.index.IborIndex;
import com.opengamma.financial.instrument.payment.CapFloorIborDefinition;
import com.opengamma.financial.security.capfloor.CapFloorSecurity;
import com.opengamma.financial.security.capfloor.CapFloorSecurityVisitor;
import com.opengamma.id.ExternalId;
import com.opengamma.util.money.Currency;

/**
 * 
 */
public class CapFloorSecurityConverter implements CapFloorSecurityVisitor<FixedIncomeInstrumentConverter<?>> {
  private final HolidaySource _holidaySource;
  private final ConventionBundleSource _conventionSource;

  public CapFloorSecurityConverter(final HolidaySource holidaySource, final ConventionBundleSource conventionSource) {
    Validate.notNull(holidaySource, "holiday source");
    Validate.notNull(conventionSource, "convention source");
    _holidaySource = holidaySource;
    _conventionSource = conventionSource;
  }

  @Override
  public FixedIncomeInstrumentConverter<?> visitCapFloorSecurity(final CapFloorSecurity capFloorSecurity) {
    Validate.notNull(capFloorSecurity, "cap/floor security");
    final double notional = capFloorSecurity.getNotional();
    final ZonedDateTime fixingDate = capFloorSecurity.getStartDate(); //TODO is this right?
    final double strike = capFloorSecurity.getStrike();
    final boolean isCap = capFloorSecurity.isCap();
    final ExternalId underlyingId = capFloorSecurity.getUnderlyingId();
    final Currency currency = capFloorSecurity.getCurrency();
    final Frequency tenor = capFloorSecurity.getFrequency();
    final ConventionBundle indexConvention = _conventionSource.getConventionBundle(underlyingId);
    if (indexConvention == null) {
      throw new OpenGammaRuntimeException("Could not get ibor index convention for " + currency);
    }
    final Calendar calendar = CalendarUtils.getCalendar(_holidaySource, currency);
    final IborIndex index = new IborIndex(currency, getTenor(tenor), indexConvention.getSettlementDays(), calendar,
        indexConvention.getDayCount(), indexConvention.getBusinessDayConvention(), indexConvention.isEOMConvention());
    return CapFloorIborDefinition.from(notional, fixingDate, index, strike, isCap);
  }

  // FIXME: convert frequency to period in a better way
  private Period getTenor(final Frequency freq) {
    Period tenor;
    if (Frequency.ANNUAL_NAME.equals(freq.getConventionName())) {
      tenor = Period.ofMonths(12);
    } else if (Frequency.SEMI_ANNUAL_NAME.equals(freq.getConventionName())) {
      tenor = Period.ofMonths(6);
    } else if (Frequency.QUARTERLY_NAME.equals(freq.getConventionName())) {
      tenor = Period.ofMonths(3);
    } else if (Frequency.MONTHLY_NAME.equals(freq.getConventionName())) {
      tenor = Period.ofMonths(1);
    } else {
      throw new OpenGammaRuntimeException(
          "Can only handle annual, semi-annual, quarterly and monthly frequencies for cap/floors");
    }
    return tenor;
  }
}
