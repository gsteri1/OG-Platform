/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.convention.frequency;

/**
 * Convention for frequency.
 * <p>
 * Some financial products have a specific event every so often.
 * This convention defines the frequency of those events relative to a year.
 */
public interface Frequency {

  //TODO: Improve frequency to have a "toPeriod" and a "paymentPerYear".

  /**
   * Never convention name.
   */
  String NEVER_NAME = "Never";
  /**
   * Annual convention name
   */
  String ANNUAL_NAME = "Annual";
  /**
   * Semi-annual convention name
   */
  String SEMI_ANNUAL_NAME = "Semi-annual";
  /**
   * Quarterly convention name
   */
  String QUARTERLY_NAME = "Quarterly";
  /**
   * Bi-monthly convention name
   */
  String BIMONTHLY_NAME = "Bi-monthly";
  /**
   * Monthly convention name
   */
  String MONTHLY_NAME = "Monthly";
  /**
   * Bi-weekly convention name
   */
  String BIWEEKLY_NAME = "Bi-weekly";
  /**
   * Weekly convention name
   */
  String WEEKLY_NAME = "Weekly";
  /**
   * Daily convention name
   */
  String DAILY_NAME = "Daily";
  /**
   * Continuous convention name
   */
  String CONTINUOUS_NAME = "Continuous";

  /**
   * Gets the name of the convention.
   * @return the name, not null
   */
  String getConventionName();

}
