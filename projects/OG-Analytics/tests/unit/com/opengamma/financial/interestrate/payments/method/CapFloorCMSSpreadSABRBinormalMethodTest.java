/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.payments.method;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import it.unimi.dsi.fastutil.doubles.DoubleAVLTreeSet;

import java.util.List;

import javax.time.calendar.LocalDateTime;
import javax.time.calendar.Period;
import javax.time.calendar.TimeZone;
import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.Test;

import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.instrument.index.CMSIndex;
import com.opengamma.financial.instrument.index.IborIndex;
import com.opengamma.financial.instrument.payment.CapFloorCMSSpreadDefinition;
import com.opengamma.financial.instrument.swap.SwapFixedIborDefinition;
import com.opengamma.financial.interestrate.ParRateCalculator;
import com.opengamma.financial.interestrate.PresentValueCurveSensitivitySABRCalculator;
import com.opengamma.financial.interestrate.PresentValueSABRCalculator;
import com.opengamma.financial.interestrate.PresentValueSABRSensitivityDataBundle;
import com.opengamma.financial.interestrate.PresentValueSABRSensitivitySABRCalculator;
import com.opengamma.financial.interestrate.InterestRateCurveSensitivity;
import com.opengamma.financial.interestrate.TestsDataSets;
import com.opengamma.financial.interestrate.YieldCurveBundle;
import com.opengamma.financial.interestrate.method.SensitivityFiniteDifference;
import com.opengamma.financial.interestrate.payments.CapFloorCMS;
import com.opengamma.financial.interestrate.payments.CapFloorCMSSpread;
import com.opengamma.financial.interestrate.payments.CouponCMS;
import com.opengamma.financial.interestrate.payments.CouponIbor;
import com.opengamma.financial.interestrate.payments.Payment;
import com.opengamma.financial.interestrate.swap.definition.FixedCouponSwap;
import com.opengamma.financial.model.option.definition.SABRInterestRateCorrelationParameters;
import com.opengamma.financial.model.option.definition.SABRInterestRateDataBundle;
import com.opengamma.financial.model.option.definition.SABRInterestRateParameters;
import com.opengamma.financial.model.option.pricing.analytic.formula.BlackFunctionData;
import com.opengamma.financial.model.option.pricing.analytic.formula.EuropeanVanillaOption;
import com.opengamma.financial.model.option.pricing.analytic.formula.NormalPriceFunction;
import com.opengamma.financial.model.volatility.NormalImpliedVolatilityFormula;
import com.opengamma.math.function.DoubleFunction1D;
import com.opengamma.math.function.Function1D;
import com.opengamma.math.function.RealPolynomialFunction1D;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.CurrencyAmount;
import com.opengamma.util.time.DateUtils;
import com.opengamma.util.tuple.DoublesPair;

/**
 * Tests the pricing of CMS spread option in binormal with correlation by strike approach.
 */
public class CapFloorCMSSpreadSABRBinormalMethodTest {

  //Swaps
  private static final Currency CUR = Currency.USD;
  private static final Calendar CALENDAR = new MondayToFridayCalendar("A");
  private static final BusinessDayConvention BUSINESS_DAY = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Modified Following");
  private static final boolean IS_EOM = true;
  private static final ZonedDateTime SETTLEMENT_DATE = DateUtils.getUTCDate(2011, 3, 17);
  private static final Period FIXED_PAYMENT_PERIOD = Period.ofMonths(6);
  private static final DayCount FIXED_DAY_COUNT = DayCountFactory.INSTANCE.getDayCount("30/360");
  private static final boolean FIXED_IS_PAYER = true; // Irrelevant for the underlying
  private static final double RATE = 0.0; // Irrelevant for the underlying
  private static final Period INDEX_TENOR = Period.ofMonths(3);
  private static final int SETTLEMENT_DAYS = 2;
  private static final DayCount DAY_COUNT = DayCountFactory.INSTANCE.getDayCount("Actual/360");
  private static final IborIndex IBOR_INDEX = new IborIndex(CUR, INDEX_TENOR, SETTLEMENT_DAYS, CALENDAR, DAY_COUNT, BUSINESS_DAY, IS_EOM);
  // Swap 10Y
  private static final Period ANNUITY_TENOR_1 = Period.ofYears(10);
  private static final CMSIndex CMS_INDEX_1 = new CMSIndex(FIXED_PAYMENT_PERIOD, FIXED_DAY_COUNT, IBOR_INDEX, ANNUITY_TENOR_1);
  private static final SwapFixedIborDefinition SWAP_DEFINITION_1 = SwapFixedIborDefinition.from(SETTLEMENT_DATE, CMS_INDEX_1, 1.0, RATE, FIXED_IS_PAYER);
  // Swap 2Y
  private static final Period ANNUITY_TENOR_2 = Period.ofYears(2);
  private static final CMSIndex CMS_INDEX_2 = new CMSIndex(FIXED_PAYMENT_PERIOD, FIXED_DAY_COUNT, IBOR_INDEX, ANNUITY_TENOR_2);
  private static final SwapFixedIborDefinition SWAP_DEFINITION_2 = SwapFixedIborDefinition.from(SETTLEMENT_DATE, CMS_INDEX_2, 1.0, RATE, FIXED_IS_PAYER);
  // CMS spread coupon
  private static final double NOTIONAL = 100000000;
  private static final ZonedDateTime PAYMENT_DATE = DateUtils.getUTCDate(2011, 4, 6);
  private static final ZonedDateTime FIXING_DATE = DateUtils.getUTCDate(2010, 12, 30);
  private static final ZonedDateTime ACCRUAL_START_DATE = DateUtils.getUTCDate(2011, 1, 5);
  private static final ZonedDateTime ACCRUAL_END_DATE = DateUtils.getUTCDate(2011, 4, 5);
  private static final DayCount PAYMENT_DAY_COUNT = DayCountFactory.INSTANCE.getDayCount("Actual/360");
  private static final double PAYMENT_ACCRUAL_FACTOR = PAYMENT_DAY_COUNT.getDayCountFraction(ACCRUAL_START_DATE, ACCRUAL_END_DATE);
  private static final double STRIKE = 0.0010; // 10 bps
  private static final boolean IS_CAP = true;
  // to derivatives
  private static final ZonedDateTime REFERENCE_DATE = DateUtils.getUTCDate(2008, 8, 18);
  private static final String FUNDING_CURVE_NAME = "Funding";
  private static final String FORWARD_CURVE_NAME = "Forward";
  private static final String[] CURVES_NAME = {FUNDING_CURVE_NAME, FORWARD_CURVE_NAME};
  private static final FixedCouponSwap<? extends Payment> SWAP_1 = SWAP_DEFINITION_1.toDerivative(REFERENCE_DATE, CURVES_NAME);
  private static final FixedCouponSwap<? extends Payment> SWAP_2 = SWAP_DEFINITION_2.toDerivative(REFERENCE_DATE, CURVES_NAME);
  private static final DayCount ACT_ACT = DayCountFactory.INSTANCE.getDayCount("Actual/Actual ISDA");
  private static final ZonedDateTime REFERENCE_DATE_ZONED = ZonedDateTime.of(LocalDateTime.ofMidnight(REFERENCE_DATE), TimeZone.UTC);
  private static final double PAYMENT_TIME = ACT_ACT.getDayCountFraction(REFERENCE_DATE_ZONED, PAYMENT_DATE);
  private static final double FIXING_TIME = ACT_ACT.getDayCountFraction(REFERENCE_DATE_ZONED, FIXING_DATE);
  private static final double SETTLEMENT_TIME = ACT_ACT.getDayCountFraction(REFERENCE_DATE_ZONED, SWAP_DEFINITION_1.getFixedLeg().getNthPayment(0).getAccrualStartDate());

  private static final CapFloorCMSSpreadDefinition CMS_SPREAD_DEFINITION = new CapFloorCMSSpreadDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, PAYMENT_ACCRUAL_FACTOR, NOTIONAL,
      FIXING_DATE, SWAP_DEFINITION_1, CMS_INDEX_1, SWAP_DEFINITION_2, CMS_INDEX_2, STRIKE, IS_CAP);

  private static final CapFloorCMSSpread CMS_SPREAD = new CapFloorCMSSpread(CUR, PAYMENT_TIME, PAYMENT_ACCRUAL_FACTOR, NOTIONAL, FIXING_TIME, SWAP_1, CMS_INDEX_1, SWAP_2, CMS_INDEX_2,
      SETTLEMENT_TIME, STRIKE, IS_CAP, FUNDING_CURVE_NAME);

  private static final YieldCurveBundle CURVES = TestsDataSets.createCurves1();
  private static final SABRInterestRateParameters SABR_PARAMETERS = TestsDataSets.createSABR1();
  private static final SABRInterestRateDataBundle SABR_BUNDLE = new SABRInterestRateDataBundle(SABR_PARAMETERS, CURVES);
  private static final double CORRELATION = 0.80;
  private static final DoubleFunction1D CORRELATION_FUNCTION = new RealPolynomialFunction1D(new double[] {CORRELATION}); // Constant function
  private static final CapFloorCMSSpreadSABRBinormalMethod METHOD = new CapFloorCMSSpreadSABRBinormalMethod(CORRELATION_FUNCTION);

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNotNullCorrelation() {
    new CapFloorCMSSpreadSABRBinormalMethod(null);
  }

  @Test
  public void getter() {
    final double correlation = 0.80;
    final DoubleFunction1D correlationFunction = new RealPolynomialFunction1D(new double[] {correlation}); // Constant function
    final CapFloorCMSSpreadSABRBinormalMethod method = new CapFloorCMSSpreadSABRBinormalMethod(correlationFunction);
    assertEquals("CMS spread binormal method: correlation function getter", correlationFunction, method.getCorrelation());
  }

  @Test
  /**
   * Tests the present value against the price explicitly computed for constant correlation. 
   */
  public void presentValue() {
    final double correlation = 0.80;
    final DoubleFunction1D correlationFunction = new RealPolynomialFunction1D(new double[] {correlation}); // Constant function
    final CapFloorCMSSpreadSABRBinormalMethod method = new CapFloorCMSSpreadSABRBinormalMethod(correlationFunction);
    final double cmsSpreadPrice = method.presentValue(CMS_SPREAD, SABR_BUNDLE).getAmount();
    final double discountFactorPayment = CURVES.getCurve(FUNDING_CURVE_NAME).getDiscountFactor(PAYMENT_TIME);
    final CouponCMSSABRReplicationMethod methodCms = CouponCMSSABRReplicationMethod.getDefaultInstance();
    final CapFloorCMSSABRReplicationMethod methodCmsCap = CapFloorCMSSABRReplicationMethod.getDefaultInstance();
    final NormalImpliedVolatilityFormula impliedVolatility = new NormalImpliedVolatilityFormula();
    final NormalPriceFunction normalPrice = new NormalPriceFunction();
    final ParRateCalculator parRate = ParRateCalculator.getInstance();
    final CouponCMS cmsCoupon1 = CouponCMS.from(CMS_SPREAD, SWAP_1, SETTLEMENT_TIME);
    final CouponCMS cmsCoupon2 = CouponCMS.from(CMS_SPREAD, SWAP_2, SETTLEMENT_TIME);
    final double cmsCoupon1Price = methodCms.presentValue(cmsCoupon1, SABR_BUNDLE);
    final double cmsCoupon2Price = methodCms.presentValue(cmsCoupon2, SABR_BUNDLE);
    final double expectedRate1 = cmsCoupon1Price / discountFactorPayment / cmsCoupon1.getNotional() / cmsCoupon1.getPaymentYearFraction();
    final double expectedRate2 = cmsCoupon2Price / discountFactorPayment / cmsCoupon2.getNotional() / cmsCoupon2.getPaymentYearFraction();
    final double forward1 = parRate.visit(SWAP_1, CURVES);
    final double forward2 = parRate.visit(SWAP_2, CURVES);
    final CapFloorCMS cmsCap1 = CapFloorCMS.from(cmsCoupon1, forward1, true);
    final CapFloorCMS cmsCap2 = CapFloorCMS.from(cmsCoupon2, forward2, true);
    final double cmsCap1Price = methodCmsCap.presentValue(cmsCap1, SABR_BUNDLE).getAmount();
    final double cmsCap2Price = methodCmsCap.presentValue(cmsCap2, SABR_BUNDLE).getAmount();
    final EuropeanVanillaOption optionCap1 = new EuropeanVanillaOption(forward1, FIXING_TIME, true);
    final BlackFunctionData dataCap1 = new BlackFunctionData(expectedRate1, 1.0, 0.0);
    final double cmsCap1IV = impliedVolatility.getImpliedVolatility(dataCap1, optionCap1, cmsCap1Price / discountFactorPayment / cmsCoupon1.getNotional() / cmsCoupon1.getPaymentYearFraction());
    final EuropeanVanillaOption optionCap2 = new EuropeanVanillaOption(forward2, FIXING_TIME, true);
    final BlackFunctionData dataCap2 = new BlackFunctionData(expectedRate2, 1.0, 0.0);
    final double cmsCap2IV = impliedVolatility.getImpliedVolatility(dataCap2, optionCap2, cmsCap2Price / discountFactorPayment / cmsCoupon2.getNotional() / cmsCoupon2.getPaymentYearFraction());
    double spreadVol = cmsCap1IV * cmsCap1IV - 2 * correlation * cmsCap1IV * cmsCap2IV + cmsCap2IV * cmsCap2IV;
    spreadVol = Math.sqrt(spreadVol);
    final EuropeanVanillaOption optionSpread = new EuropeanVanillaOption(STRIKE, FIXING_TIME, IS_CAP);
    final BlackFunctionData dataSpread = new BlackFunctionData(expectedRate1 - expectedRate2, 1.0, spreadVol);
    final Function1D<BlackFunctionData, Double> priceFunction = normalPrice.getPriceFunction(optionSpread);
    final double cmsSpreadPriceExpected = discountFactorPayment * priceFunction.evaluate(dataSpread) * CMS_SPREAD.getNotional() * CMS_SPREAD.getPaymentYearFraction();
    assertEquals("CMS spread: price with constant correlation", cmsSpreadPriceExpected, cmsSpreadPrice, 1.0E-2);
  }

  @Test
  /**
   * Tests the present value against the price explicitly computed for constant correlation. 
   */
  public void presentValueMethodVsCalculator() {
    final PresentValueSABRCalculator calculator = PresentValueSABRCalculator.getInstance();
    SABRInterestRateCorrelationParameters sabrCorrelation = SABRInterestRateCorrelationParameters.from(SABR_PARAMETERS, CORRELATION_FUNCTION);
    SABRInterestRateDataBundle sabrBundleCor = new SABRInterestRateDataBundle(sabrCorrelation, CURVES);
    CurrencyAmount pvMethod = METHOD.presentValue(CMS_SPREAD, sabrBundleCor);
    double pvCalculator = calculator.visit(CMS_SPREAD, sabrBundleCor);
    assertEquals("CMS spread: present value Method vs Calculator", pvMethod.getAmount(), pvCalculator, 1.0E-2);
  }

  @Test
  /**
   * Tests the implied correlation computation for a range of correlations.
   */
  public void impliedCorrelation() {
    final YieldCurveBundle curves = TestsDataSets.createCurves1();
    final SABRInterestRateParameters sabrParameter = TestsDataSets.createSABR1();
    final SABRInterestRateDataBundle sabrBundle = new SABRInterestRateDataBundle(sabrParameter, curves);
    final double[] correlation = new double[] {-0.50, 0.00, 0.50, 0.75, 0.80, 0.85, 0.90, 0.95, 0.99};
    final int nbCor = correlation.length;
    final double[] impliedCorrelation = new double[nbCor];
    for (int loopcor = 0; loopcor < nbCor; loopcor++) {
      final DoubleFunction1D correlationFunction = new RealPolynomialFunction1D(new double[] {correlation[loopcor]}); // Constant function
      final CapFloorCMSSpreadSABRBinormalMethod method = new CapFloorCMSSpreadSABRBinormalMethod(correlationFunction);
      final double cmsSpreadPrice = method.presentValue(CMS_SPREAD, sabrBundle).getAmount();
      impliedCorrelation[loopcor] = method.impliedCorrelation(CMS_SPREAD, sabrBundle, cmsSpreadPrice);
      assertEquals("CMS spread cap/floor: implied correlation", correlation[loopcor], impliedCorrelation[loopcor], 1.0E-12);
    }
  }

  @Test
  /**
   * Tests the price curve sensitivity of CMS coupon and cap/floor using replication in the SABR framework. Values are tested against finite difference values.
   */
  public void presentValueCurveSensitivity() {
    InterestRateCurveSensitivity pvcsCap = METHOD.presentValueSensitivity(CMS_SPREAD, SABR_BUNDLE);
    pvcsCap = pvcsCap.clean();
    final double deltaTolerancePrice = 2.5E+6;
    //Testing note: The computed sensitivity does not change the strike of the ATM strike with the curves.
    final double deltaShift = 1.0E-6;
    String bumpedCurveName = "Bumped Curve";
    // 1. Forward curve sensitivity
    final String[] CurveNameBumpedForward = {FUNDING_CURVE_NAME, bumpedCurveName};
    final CapFloorCMSSpread capBumpedForward = (CapFloorCMSSpread) CMS_SPREAD_DEFINITION.toDerivative(REFERENCE_DATE, CurveNameBumpedForward);
    DoubleAVLTreeSet forwardTime = new DoubleAVLTreeSet();
    for (int loopcpn = 0; loopcpn < CMS_SPREAD.getUnderlyingSwap1().getSecondLeg().getNumberOfPayments(); loopcpn++) {
      CouponIbor cpn = (CouponIbor) CMS_SPREAD.getUnderlyingSwap1().getSecondLeg().getNthPayment(loopcpn);
      forwardTime.add(cpn.getFixingPeriodStartTime());
      forwardTime.add(cpn.getFixingPeriodEndTime());
    }
    for (int loopcpn = 0; loopcpn < CMS_SPREAD.getUnderlyingSwap2().getSecondLeg().getNumberOfPayments(); loopcpn++) {
      CouponIbor cpn = (CouponIbor) CMS_SPREAD.getUnderlyingSwap2().getSecondLeg().getNthPayment(loopcpn);
      forwardTime.add(cpn.getFixingPeriodStartTime());
      forwardTime.add(cpn.getFixingPeriodEndTime());
    }
    double[] nodeTimesForward = forwardTime.toDoubleArray();
    double[] sensiForwardMethod = SensitivityFiniteDifference.curveSensitivity(capBumpedForward, SABR_BUNDLE, FORWARD_CURVE_NAME, bumpedCurveName, nodeTimesForward, deltaShift, METHOD);
    List<DoublesPair> sensiPvForward = pvcsCap.getSensitivities().get(FORWARD_CURVE_NAME);
    for (int loopnode = 0; loopnode < sensiForwardMethod.length; loopnode++) {
      final DoublesPair pairPv = sensiPvForward.get(loopnode);
      assertEquals("Sensitivity CMS cap/floor pv to forward curve: Node " + loopnode, nodeTimesForward[loopnode], pairPv.getFirst(), 1E-8);
      assertEquals("Sensitivity finite difference method: node sensitivity " + loopnode + " - Difference " + (sensiForwardMethod[loopnode] - pairPv.second), sensiForwardMethod[loopnode],
          pairPv.second, deltaTolerancePrice);
    }
    // 2. Discounting curve sensitivity
    final String[] CurveNameBumpedDisc = {bumpedCurveName, FORWARD_CURVE_NAME};
    final CapFloorCMSSpread capBumpedDisc = (CapFloorCMSSpread) CMS_SPREAD_DEFINITION.toDerivative(REFERENCE_DATE, CurveNameBumpedDisc);
    DoubleAVLTreeSet discTime = new DoubleAVLTreeSet();
    discTime.add(capBumpedDisc.getPaymentTime());
    for (int loopcpn = 0; loopcpn < CMS_SPREAD.getUnderlyingSwap1().getSecondLeg().getNumberOfPayments(); loopcpn++) {
      CouponIbor cpn = (CouponIbor) CMS_SPREAD.getUnderlyingSwap1().getSecondLeg().getNthPayment(loopcpn);
      discTime.add(cpn.getPaymentTime());
    }
    for (int loopcpn = 0; loopcpn < CMS_SPREAD.getUnderlyingSwap2().getSecondLeg().getNumberOfPayments(); loopcpn++) {
      CouponIbor cpn = (CouponIbor) CMS_SPREAD.getUnderlyingSwap2().getSecondLeg().getNthPayment(loopcpn);
      discTime.add(cpn.getPaymentTime());
    }
    double[] nodeTimesDisc = discTime.toDoubleArray();
    double[] sensiDiscMethod = SensitivityFiniteDifference.curveSensitivity(capBumpedDisc, SABR_BUNDLE, FUNDING_CURVE_NAME, bumpedCurveName, nodeTimesDisc, deltaShift, METHOD);
    List<DoublesPair> sensiPvDisc = pvcsCap.getSensitivities().get(FUNDING_CURVE_NAME);
    for (int loopnode = 0; loopnode < sensiDiscMethod.length; loopnode++) {
      final DoublesPair pairPv = sensiPvDisc.get(loopnode);
      assertEquals("Sensitivity CMS cap/floor pv to forward curve: Node " + loopnode, nodeTimesDisc[loopnode], pairPv.getFirst(), 1E-8);
      assertEquals("Sensitivity finite difference method: node sensitivity " + loopnode, pairPv.second, sensiDiscMethod[loopnode], deltaTolerancePrice);
    }
  }

  @Test
  /**
   * Tests the present value against the price explicitly computed for constant correlation. 
   */
  public void presentValueCurveSensitivityMethodVsCalculator() {
    final PresentValueCurveSensitivitySABRCalculator calculator = PresentValueCurveSensitivitySABRCalculator.getInstance();
    SABRInterestRateCorrelationParameters sabrCorrelation = SABRInterestRateCorrelationParameters.from(SABR_PARAMETERS, CORRELATION_FUNCTION);
    SABRInterestRateDataBundle sabrBundleCor = new SABRInterestRateDataBundle(sabrCorrelation, CURVES);
    InterestRateCurveSensitivity pvcsMethod = METHOD.presentValueSensitivity(CMS_SPREAD, sabrBundleCor);
    InterestRateCurveSensitivity pvcsCalculator = new InterestRateCurveSensitivity(calculator.visit(CMS_SPREAD, sabrBundleCor));
    assertEquals("CMS spread: curve sensitivity Method vs Calculator", pvcsMethod, pvcsCalculator);
  }

  @Test
  /**
   * Tests the present value SABR parameters sensitivity vs finite difference.
   */
  public void presentValueSABRSensitivity() {
    final double pv = METHOD.presentValue(CMS_SPREAD, SABR_BUNDLE).getAmount();
    final PresentValueSABRSensitivityDataBundle pvsCapLong = METHOD.presentValueSABRSensitivity(CMS_SPREAD, SABR_BUNDLE);
    // SABR sensitivity vs finite difference
    final double shift = 0.0001;
    final double shiftAlpha = 0.00001;
    double maturity1 = CMS_SPREAD.getUnderlyingSwap1().getFixedLeg().getNthPayment(CMS_SPREAD.getUnderlyingSwap1().getFixedLeg().getNumberOfPayments() - 1).getPaymentTime()
        - CMS_SPREAD.getSettlementTime();
    final DoublesPair expectedExpiryTenor1 = new DoublesPair(CMS_SPREAD.getFixingTime(), maturity1);
    double maturity2 = CMS_SPREAD.getUnderlyingSwap2().getFixedLeg().getNthPayment(CMS_SPREAD.getUnderlyingSwap2().getFixedLeg().getNumberOfPayments() - 1).getPaymentTime()
        - CMS_SPREAD.getSettlementTime();
    final DoublesPair expectedExpiryTenor2 = new DoublesPair(CMS_SPREAD.getFixingTime(), maturity2);
    // Alpha sensitivity vs finite difference computation
    final SABRInterestRateParameters sabrParameterAlphaBumped = TestsDataSets.createSABR1AlphaBumped(shiftAlpha);
    final SABRInterestRateDataBundle sabrBundleAlphaBumped = new SABRInterestRateDataBundle(sabrParameterAlphaBumped, CURVES);
    final double pvLongPayerAlphaBumped = METHOD.presentValue(CMS_SPREAD, sabrBundleAlphaBumped).getAmount();
    final double expectedAlphaSensi = (pvLongPayerAlphaBumped - pv) / shiftAlpha;
    assertEquals("Number of alpha sensitivity", pvsCapLong.getAlpha().keySet().size(), 2);
    assertEquals("Alpha sensitivity expiry/tenor", pvsCapLong.getAlpha().keySet().contains(expectedExpiryTenor1), true);
    assertEquals("Alpha sensitivity expiry/tenor", pvsCapLong.getAlpha().keySet().contains(expectedExpiryTenor2), true);
    assertEquals("Alpha sensitivity value", expectedAlphaSensi, pvsCapLong.getAlpha().get(expectedExpiryTenor1) + pvsCapLong.getAlpha().get(expectedExpiryTenor2), 5.0E+3);
    // Rho sensitivity vs finite difference computation
    final SABRInterestRateParameters sabrParameterRhoBumped = TestsDataSets.createSABR1RhoBumped();
    final SABRInterestRateDataBundle sabrBundleRhoBumped = new SABRInterestRateDataBundle(sabrParameterRhoBumped, CURVES);
    final double pvLongPayerRhoBumped = METHOD.presentValue(CMS_SPREAD, sabrBundleRhoBumped).getAmount();
    final double expectedRhoSensi = (pvLongPayerRhoBumped - pv) / shift;
    assertEquals("Number of rho sensitivity", pvsCapLong.getRho().keySet().size(), 2);
    assertEquals("Rho sensitivity expiry/tenor", pvsCapLong.getRho().keySet().contains(expectedExpiryTenor1), true);
    assertEquals("Rho sensitivity expiry/tenor", pvsCapLong.getRho().keySet().contains(expectedExpiryTenor2), true);
    assertEquals("Rho sensitivity value", expectedRhoSensi, pvsCapLong.getRho().get(expectedExpiryTenor1) + pvsCapLong.getRho().get(expectedExpiryTenor2), 3.0E+1);
    // Alpha sensitivity vs finite difference computation
    final SABRInterestRateParameters sabrParameterNuBumped = TestsDataSets.createSABR1NuBumped();
    final SABRInterestRateDataBundle sabrBundleNuBumped = new SABRInterestRateDataBundle(sabrParameterNuBumped, CURVES);
    final double pvLongPayerNuBumped = METHOD.presentValue(CMS_SPREAD, sabrBundleNuBumped).getAmount();
    final double expectedNuSensi = (pvLongPayerNuBumped - pv) / shift;
    assertEquals("Number of nu sensitivity", pvsCapLong.getNu().keySet().size(), 2);
    assertTrue("Nu sensitivity expiry/tenor", pvsCapLong.getNu().keySet().contains(expectedExpiryTenor1));
    assertTrue("Nu sensitivity expiry/tenor", pvsCapLong.getNu().keySet().contains(expectedExpiryTenor2));
    assertEquals("Nu sensitivity value", expectedNuSensi, pvsCapLong.getNu().get(expectedExpiryTenor1) + pvsCapLong.getNu().get(expectedExpiryTenor2), 2.0E+2);
  }

  @Test
  /**
   * Tests the present value against the price explicitly computed for constant correlation. 
   */
  public void presentValueSABRSensitivityMethodVsCalculator() {
    final PresentValueSABRSensitivitySABRCalculator calculator = PresentValueSABRSensitivitySABRCalculator.getInstance();
    SABRInterestRateCorrelationParameters sabrCorrelation = SABRInterestRateCorrelationParameters.from(SABR_PARAMETERS, CORRELATION_FUNCTION);
    SABRInterestRateDataBundle sabrBundleCor = new SABRInterestRateDataBundle(sabrCorrelation, CURVES);
    PresentValueSABRSensitivityDataBundle pvcsMethod = METHOD.presentValueSABRSensitivity(CMS_SPREAD, sabrBundleCor);
    PresentValueSABRSensitivityDataBundle pvcsCalculator = calculator.visit(CMS_SPREAD, sabrBundleCor);
    assertEquals("CMS spread: SABR sensitivity Method vs Calculator", pvcsMethod, pvcsCalculator);
  }

  @Test(enabled = false)
  /**
   * Tests of performance. "enabled = false" for the standard testing.
   */
  public void performance() {
    long startTime, endTime;
    final int nbTest = 100;
    double[] pv = new double[nbTest];
    PresentValueSABRSensitivityDataBundle[] pvss = new PresentValueSABRSensitivityDataBundle[nbTest];
    InterestRateCurveSensitivity[] pvcs = new InterestRateCurveSensitivity[nbTest];
    startTime = System.currentTimeMillis();
    for (int looptest = 0; looptest < nbTest; looptest++) {
      pv[looptest] = METHOD.presentValue(CMS_SPREAD, SABR_BUNDLE).getAmount();
    }
    endTime = System.currentTimeMillis();
    System.out.println(nbTest + " CMS spread cap by replication (price): " + (endTime - startTime) + " ms");

    startTime = System.currentTimeMillis();
    for (int looptest = 0; looptest < nbTest; looptest++) {
      pvcs[looptest] = METHOD.presentValueSensitivity(CMS_SPREAD, SABR_BUNDLE);
    }
    endTime = System.currentTimeMillis();
    System.out.println(nbTest + " CMS spread cap by replication (curve risk): " + (endTime - startTime) + " ms");

    startTime = System.currentTimeMillis();
    for (int looptest = 0; looptest < nbTest; looptest++) {
      pvss[looptest] = METHOD.presentValueSABRSensitivity(CMS_SPREAD, SABR_BUNDLE);
    }
    endTime = System.currentTimeMillis();
    System.out.println(nbTest + " CMS spread cap by replication (sabr risk): " + (endTime - startTime) + " ms");
  }

}
