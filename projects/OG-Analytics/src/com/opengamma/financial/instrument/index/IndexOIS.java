/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.instrument.index;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;

import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.util.money.Currency;

/**
 * Class describing an OIS-like index. The fixing period is always one business day.
 */
public class IndexOIS extends IndexDeposit {

  /**
   * The day count convention associated to the overnight rate. Not null.
   */
  private final DayCount _dayCount;
  /**
   * The number of days between start of the fixing period and the publication of the index value. It is usually 0 day (EUR) or 1 day (USD). 
   * It does not represent the standard number of days between the trade date and the settlement date. 
   */
  private final int _publicationLag;

  /**
   * Index constructor from all the details.
   * @param name The name of the index. Not null.
   * @param currency The index currency. Not null.
   * @param dayCount The day count convention associated to the overnight rate. Not null.
   * @param publicationLag The number of days between start of the fixing period and the publication of the index value.
   * @param calendar The calendar associated to the index. Not null.
   */
  public IndexOIS(String name, Currency currency, DayCount dayCount, int publicationLag, Calendar calendar) {
    super(name, currency, calendar);
    Validate.notNull(dayCount, "OIS index: day count");
    _publicationLag = publicationLag;
    _dayCount = dayCount;
  }

  /**
   * Gets the day count convention associated to the overnight rate.
   * @return The day count convention.
   */
  public DayCount getDayCount() {
    return _dayCount;
  }

  /**
   * Gets the number of days between start of the fixing period and the publication of the index value.
   * @return The number of lag days.
   */
  public int getPublicationLag() {
    return _publicationLag;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + _dayCount.hashCode();
    result = prime * result + _publicationLag;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    IndexOIS other = (IndexOIS) obj;
    if (!ObjectUtils.equals(_dayCount, other._dayCount)) {
      return false;
    }
    if (_publicationLag != other._publicationLag) {
      return false;
    }
    return true;
  }

}
