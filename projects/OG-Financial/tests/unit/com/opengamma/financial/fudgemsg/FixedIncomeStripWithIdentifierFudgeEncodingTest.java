/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.fudgemsg;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

import com.opengamma.core.security.SecurityUtils;
import com.opengamma.financial.analytics.ircurve.FixedIncomeStrip;
import com.opengamma.financial.analytics.ircurve.FixedIncomeStripWithIdentifier;
import com.opengamma.financial.analytics.ircurve.StripInstrumentType;
import com.opengamma.id.ExternalId;
import com.opengamma.util.time.Tenor;

public class FixedIncomeStripWithIdentifierFudgeEncodingTest extends FinancialTestBase {

  @Test
  public void testCycle() {
    FixedIncomeStripWithIdentifier strip = new FixedIncomeStripWithIdentifier(new FixedIncomeStrip(StripInstrumentType.CASH, Tenor.DAY, "DEFAULT"), ExternalId.of(SecurityUtils.BLOOMBERG_TICKER, "USDR5 Curncy"));
    assertEquals(strip, cycleObject(FixedIncomeStripWithIdentifier.class, strip));
    strip = new FixedIncomeStripWithIdentifier(new FixedIncomeStrip(StripInstrumentType.LIBOR, Tenor.ONE_MONTH, "DEFAULT"), ExternalId.of(SecurityUtils.BLOOMBERG_TICKER, "US0001M Curncy"));
    assertEquals(strip, cycleObject(FixedIncomeStripWithIdentifier.class, strip));
    strip = new FixedIncomeStripWithIdentifier(new FixedIncomeStrip(StripInstrumentType.EURIBOR, Tenor.ONE_MONTH, "DEFAULT"), ExternalId.of(SecurityUtils.BLOOMBERG_TICKER, "US0001M Curncy"));
    assertEquals(strip, cycleObject(FixedIncomeStripWithIdentifier.class, strip));
    strip = new FixedIncomeStripWithIdentifier(new FixedIncomeStrip(StripInstrumentType.FUTURE, Tenor.YEAR, 3, "DEFAULT"), ExternalId.of(SecurityUtils.BLOOMBERG_TICKER, "L Z3 Comdty"));
    assertEquals(strip, cycleObject(FixedIncomeStripWithIdentifier.class, strip));
    strip = new FixedIncomeStripWithIdentifier(new FixedIncomeStrip(StripInstrumentType.FRA_3M, Tenor.YEAR, "DEFAULT"), ExternalId.of(SecurityUtils.BLOOMBERG_TICKER, "USFR01C Curncy"));
    assertEquals(strip, cycleObject(FixedIncomeStripWithIdentifier.class, strip));
    strip = new FixedIncomeStripWithIdentifier(new FixedIncomeStrip(StripInstrumentType.FRA_6M, Tenor.YEAR, "DEFAULT"), ExternalId.of(SecurityUtils.BLOOMBERG_TICKER, "USFR01C Curncy"));
    assertEquals(strip, cycleObject(FixedIncomeStripWithIdentifier.class, strip));
    strip = new FixedIncomeStripWithIdentifier(new FixedIncomeStrip(StripInstrumentType.SWAP_3M, Tenor.YEAR, "DEFAULT"), ExternalId.of(SecurityUtils.BLOOMBERG_TICKER, "USSW1 Curncy"));
    assertEquals(strip, cycleObject(FixedIncomeStripWithIdentifier.class, strip));
    strip = new FixedIncomeStripWithIdentifier(new FixedIncomeStrip(StripInstrumentType.SWAP_3M, Tenor.YEAR, "DEFAULT"), ExternalId.of(SecurityUtils.BLOOMBERG_TICKER, "USSW1 Curncy"));
    assertEquals(strip, cycleObject(FixedIncomeStripWithIdentifier.class, strip));
    strip = new FixedIncomeStripWithIdentifier(new FixedIncomeStrip(StripInstrumentType.TENOR_SWAP, Tenor.YEAR, "DEFAULT"), ExternalId.of(SecurityUtils.BLOOMBERG_TICKER, "USBG1 Curncy"));
    assertEquals(strip, cycleObject(FixedIncomeStripWithIdentifier.class, strip));
    strip = new FixedIncomeStripWithIdentifier(new FixedIncomeStrip(StripInstrumentType.BASIS_SWAP, Tenor.YEAR, "DEFAULT"), ExternalId.of(SecurityUtils.BLOOMBERG_TICKER, "USBG1 Curncy"));
    assertEquals(strip, cycleObject(FixedIncomeStripWithIdentifier.class, strip));
    strip = new FixedIncomeStripWithIdentifier(new FixedIncomeStrip(StripInstrumentType.OIS_SWAP, Tenor.YEAR, "DEFAULT"), ExternalId.of(SecurityUtils.BLOOMBERG_TICKER, "USSO1 Curncy"));
    assertEquals(strip, cycleObject(FixedIncomeStripWithIdentifier.class, strip));
  }

}
