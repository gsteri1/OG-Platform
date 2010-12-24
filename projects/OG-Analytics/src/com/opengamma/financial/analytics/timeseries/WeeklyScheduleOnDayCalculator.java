/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.timeseries;

import java.util.ArrayList;
import java.util.List;

import javax.time.calendar.DateAdjusters;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.LocalDate;
import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.Validate;

/**
 * 
 */
public class WeeklyScheduleOnDayCalculator extends Schedule {
  private final DayOfWeek _dayOfWeek;

  public WeeklyScheduleOnDayCalculator(final DayOfWeek dayOfWeek) {
    _dayOfWeek = dayOfWeek;
  }

  @Override
  public LocalDate[] getSchedule(final LocalDate startDate, final LocalDate endDate, final boolean fromEnd, final boolean generateRecursive) {
    return getSchedule(startDate, endDate);
  }

  public LocalDate[] getSchedule(final LocalDate startDate, final LocalDate endDate) {
    Validate.notNull(startDate, "start date");
    Validate.notNull(endDate, "end date");
    Validate.isTrue(startDate.isBefore(endDate) || startDate.equals(endDate));
    if (startDate.equals(endDate)) {
      if (startDate.getDayOfWeek() == _dayOfWeek) {
        return new LocalDate[] {startDate};
      }
      throw new IllegalArgumentException("Start date and end date were the same but their day of week was not the same as that required");
    }
    final List<LocalDate> dates = new ArrayList<LocalDate>();
    LocalDate date = startDate;
    date = date.with(DateAdjusters.nextOrCurrent(_dayOfWeek));
    while (!date.isAfter(endDate)) {
      dates.add(date);
      date = date.with(DateAdjusters.next(_dayOfWeek));
    }
    return dates.toArray(EMPTY_LOCAL_DATE_ARRAY);
  }

  @Override
  public ZonedDateTime[] getSchedule(final ZonedDateTime startDate, final ZonedDateTime endDate, final boolean fromEnd, final boolean generateRecursive) {
    return getSchedule(startDate, endDate);
  }

  public ZonedDateTime[] getSchedule(final ZonedDateTime startDate, final ZonedDateTime endDate) {
    Validate.notNull(startDate, "start date");
    Validate.notNull(endDate, "end date");
    Validate.isTrue(startDate.isBefore(endDate) || startDate.equals(endDate));
    if (startDate.equals(endDate)) {
      if (startDate.getDayOfWeek() == _dayOfWeek) {
        return new ZonedDateTime[] {startDate};
      }
      throw new IllegalArgumentException("Start date and end date were the same but their day of week was not the same as that required");
    }
    final List<ZonedDateTime> dates = new ArrayList<ZonedDateTime>();
    ZonedDateTime date = startDate;
    date = date.with(DateAdjusters.nextOrCurrent(_dayOfWeek));
    while (!date.isAfter(endDate)) {
      dates.add(date);
      date = date.with(DateAdjusters.next(_dayOfWeek));
    }
    return dates.toArray(EMPTY_ZONED_DATE_TIME_ARRAY);
  }
}