/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.schedule;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import javax.time.calendar.LocalDate;
import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.schedule.FirstOfMonthScheduleCalculator;
import com.opengamma.financial.schedule.Schedule;
import com.opengamma.util.time.DateUtil;

/**
 * 
 */
public class FirstOfMonthScheduleCalculatorTest extends ScheduleCalculatorTestCase {
  private static final FirstOfMonthScheduleCalculator CALCULATOR = new FirstOfMonthScheduleCalculator();

  @Override
  public Schedule getScheduleCalculator() {
    return CALCULATOR;
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartAndEndSameButInvalid1() {
    CALCULATOR.getSchedule(LocalDate.of(2001, 2, 3), LocalDate.of(2001, 2, 3), false, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartAndEndSameButInvalid2() {
    CALCULATOR.getSchedule(DateUtil.getUTCDate(2001, 2, 3), DateUtil.getUTCDate(2001, 2, 3), false, true);
  }

  @Test
  public void test1() {
    LocalDate startDate = LocalDate.of(2000, 1, 1);
    LocalDate endDate = LocalDate.of(2000, 1, 30);
    LocalDate[] forward = CALCULATOR.getSchedule(startDate, endDate, false, true);
    assertEquals(forward.length, 1);
    assertEquals(forward[0], startDate);
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, true));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, false, false));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, false));
    startDate = LocalDate.of(2002, 2, 2);
    endDate = LocalDate.of(2002, 2, 9);
    forward = CALCULATOR.getSchedule(startDate, endDate, false, true);
    assertEquals(forward.length, 0);
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, true));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, false, false));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, false));
    startDate = LocalDate.of(2000, 1, 1);
    endDate = LocalDate.of(2002, 2, 9);
    final int months = 26;
    forward = CALCULATOR.getSchedule(startDate, endDate, false, true);
    assertEquals(forward.length, months);
    assertEquals(forward[0], startDate);
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, true));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, false, false));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, false));
    final LocalDate lastDate = LocalDate.of(2002, 2, 1);
    assertEquals(forward[months - 1], lastDate);
    for (int i = 1; i < months; i++) {
      if (forward[i].getYear() == forward[i - 1].getYear()) {
        assertEquals(forward[i].getMonthOfYear().getValue() - forward[i - 1].getMonthOfYear().getValue(), 1);
      } else {
        assertEquals(forward[i].getMonthOfYear().getValue() - forward[i - 1].getMonthOfYear().getValue(), -11);
      }
      assertEquals(forward[i].getDayOfMonth(), 1);
    }
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, true));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, false, false));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, false));
  }

  @Test
  public void test2() {
    ZonedDateTime startDate = DateUtil.getUTCDate(2000, 1, 1);
    ZonedDateTime endDate = DateUtil.getUTCDate(2000, 1, 30);
    ZonedDateTime[] forward = CALCULATOR.getSchedule(startDate, endDate, false, true);
    assertEquals(forward.length, 1);
    assertEquals(forward[0], startDate);
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, true));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, false, false));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, false));
    startDate = DateUtil.getUTCDate(2002, 2, 2);
    endDate = DateUtil.getUTCDate(2002, 2, 9);
    forward = CALCULATOR.getSchedule(startDate, endDate, false, true);
    assertEquals(forward.length, 0);
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, true));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, false, false));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, false));
    startDate = DateUtil.getUTCDate(2000, 1, 1);
    endDate = DateUtil.getUTCDate(2002, 2, 9);
    final int months = 26;
    forward = CALCULATOR.getSchedule(startDate, endDate, false, true);
    assertEquals(forward.length, months);
    assertEquals(forward[0], startDate);
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, true));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, false, false));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, false));
    final ZonedDateTime lastDate = DateUtil.getUTCDate(2002, 2, 1);
    assertEquals(forward[months - 1], lastDate);
    for (int i = 1; i < months; i++) {
      if (forward[i].getYear() == forward[i - 1].getYear()) {
        assertEquals(forward[i].getMonthOfYear().getValue() - forward[i - 1].getMonthOfYear().getValue(), 1);
      } else {
        assertEquals(forward[i].getMonthOfYear().getValue() - forward[i - 1].getMonthOfYear().getValue(), -11);
      }
      assertEquals(forward[i].getDayOfMonth(), 1);
    }
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, true));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, false, false));
    assertArrayEquals(forward, CALCULATOR.getSchedule(startDate, endDate, true, false));
  }
}