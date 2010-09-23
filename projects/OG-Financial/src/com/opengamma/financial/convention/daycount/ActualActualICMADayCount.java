/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.convention.daycount;

import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.NotImplementedException;

/**
 * The Actual/Actual ICMA day count convention.
 * <p>
 * The day count fraction is the "number of days accrued" / "number of days" in a year,
 * (where the two terms are used as defined in Rule 251 of the International Capital
 * Market Association (ICMA)) calculated as defined by Rule 251 of the ICMA rule
 * book as applied to non-US dollar-denominated straight and convertible bonds
 * issued after December 31, 1998.
 * <p>
 * This convention is also known as "Act/Act (ICMA)".
 */
public class ActualActualICMADayCount extends StatelessDayCount {

  @Override
  public double getBasis(final ZonedDateTime date) {
    throw new NotImplementedException();
  }

  @Override
  public double getDayCountFraction(final ZonedDateTime firstDate, final ZonedDateTime secondDate) {
    throw new NotImplementedException();
  }

  @Override
  public String getConventionName() {
    return "Actual/Actual (ICMA)";
  }

}