/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate;

import com.opengamma.financial.interestrate.annuity.definition.AnnuityCouponFixed;
import com.opengamma.financial.interestrate.annuity.definition.AnnuityCouponIbor;
import com.opengamma.financial.interestrate.bond.definition.Bond;
import com.opengamma.financial.interestrate.cash.definition.Cash;
import com.opengamma.financial.interestrate.fra.ForwardRateAgreement;
import com.opengamma.financial.interestrate.fra.method.ForwardRateAgreementDiscountingMethod;
import com.opengamma.financial.interestrate.future.definition.InterestRateFuture;
import com.opengamma.financial.interestrate.future.method.InterestRateFutureDiscountingMethod;
import com.opengamma.financial.interestrate.payments.CapFloorIbor;
import com.opengamma.financial.interestrate.payments.CouponIbor;
import com.opengamma.financial.interestrate.payments.CouponIborFixed;
import com.opengamma.financial.interestrate.payments.CouponIborGearing;
import com.opengamma.financial.interestrate.payments.Payment;
import com.opengamma.financial.interestrate.payments.ZZZCouponOIS;
import com.opengamma.financial.interestrate.payments.derivative.CouponOIS;
import com.opengamma.financial.interestrate.payments.method.CouponOISDiscountingMethod;
import com.opengamma.financial.interestrate.swap.definition.CrossCurrencySwap;
import com.opengamma.financial.interestrate.swap.definition.FixedCouponSwap;
import com.opengamma.financial.interestrate.swap.definition.FixedFloatSwap;
import com.opengamma.financial.interestrate.swap.definition.FloatingRateNote;
import com.opengamma.financial.interestrate.swap.definition.ForexForward;
import com.opengamma.financial.interestrate.swap.definition.OISSwap;
import com.opengamma.financial.interestrate.swap.definition.TenorSwap;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.util.CompareUtils;

import org.apache.commons.lang.Validate;

/**
 * Get the single fixed rate that makes the PV of the instrument zero. For  fixed-float swaps this is the swap rate, for FRAs it is the forward etc. For instruments that 
 * cannot PV to zero, e.g. bonds, a single payment of -1.0 is assumed at zero (i.e. the bond must PV to 1.0)
 */
public final class ParRateCalculator extends AbstractInterestRateDerivativeVisitor<YieldCurveBundle, Double> {
  private static final PresentValueCalculator PVC = PresentValueCalculator.getInstance();
  private static final RateReplacingInterestRateDerivativeVisitor REPLACE_RATE = RateReplacingInterestRateDerivativeVisitor.getInstance();
  /**
   * The method used for OIS coupons.
   */
  private static final CouponOISDiscountingMethod METHOD_OIS = new CouponOISDiscountingMethod();

  private static final ParRateCalculator s_instance = new ParRateCalculator();

  public static ParRateCalculator getInstance() {
    return s_instance;
  }

  private ParRateCalculator() {
  }

  @Override
  public Double visit(final InterestRateDerivative derivative, final YieldCurveBundle curves) {
    Validate.notNull(curves);
    Validate.notNull(derivative);
    return derivative.accept(this, curves);
  }

  @Override
  public Double visitCash(final Cash cash, final YieldCurveBundle curves) {
    final YieldAndDiscountCurve curve = curves.getCurve(cash.getYieldCurveName());
    final double ta = cash.getTradeTime();
    final double tb = cash.getMaturity();
    final double yearFrac = cash.getYearFraction();
    // TODO need a getForwardRate method on YieldAndDiscountCurve
    if (yearFrac == 0.0) {
      if (!CompareUtils.closeEquals(ta, tb, 1e-16)) {
        throw new IllegalArgumentException("Year fraction is zero, but payment time greater than trade time");
      }
      final double eps = 1e-8;
      final double rate = curve.getInterestRate(ta);
      final double dRate = curve.getInterestRate(ta + eps);
      return rate + ta * (dRate - rate) / eps;
    }
    return (curve.getDiscountFactor(ta) / curve.getDiscountFactor(tb) - 1) / yearFrac;
  }

  @Override
  public Double visitForwardRateAgreement(final ForwardRateAgreement fra, final YieldCurveBundle curves) {
    Validate.notNull(curves);
    Validate.notNull(fra);
    return ForwardRateAgreementDiscountingMethod.getInstance().parRate(fra, curves);
  }

  @Override
  /**
   * Compute the future rate (1-price) without convexity adjustment.
   */
  public Double visitInterestRateFuture(final InterestRateFuture future, final YieldCurveBundle curves) {
    final InterestRateFutureDiscountingMethod method = InterestRateFutureDiscountingMethod.getInstance();
    return method.parRate(future, curves);
  }

  /**
   * Computes the par rate of a swap with one fixed leg.
   * @param swap The Fixed coupon swap.
   * @param curves The curves.
   * @return The par swap rate. If the fixed leg has been set up with some fixed payments these are ignored for the purposes of finding the swap rate
   */
  @Override
  public Double visitFixedCouponSwap(final FixedCouponSwap<?> swap, final YieldCurveBundle curves) {
    final double pvSecond = PVC.visit(swap.getSecondLeg(), curves);
    final double pvFixed = PVC.visit(REPLACE_RATE.visit(swap.getFixedLeg(), 1.0), curves);
    return -pvSecond / pvFixed;
  }

  @Override
  public Double visitOISSwap(final OISSwap swap, final YieldCurveBundle curves) {
    return visitFixedCouponSwap(swap, curves);
  }

  /**
   * The assumption is that spread is on the second leg.
   * @param swap The tenor swap.
   * @param curves The valuation curves.
   * @return The spread on the second leg of a basis swap 
   */
  @Override
  public Double visitTenorSwap(final TenorSwap<? extends Payment> swap, final YieldCurveBundle curves) {
    final AnnuityCouponIbor firstLeg = (AnnuityCouponIbor) swap.getFirstLeg();
    final AnnuityCouponIbor secondLeg = (AnnuityCouponIbor) swap.getSecondLeg();
    final double pvFirst = PVC.visit(firstLeg.withZeroSpread(), curves);
    final double pvSecond = PVC.visit(secondLeg.withZeroSpread(), curves);
    final double pvSpread = PVC.visit(secondLeg.withUnitCoupons(), curves);
    if (pvSpread == 0.0) {
      throw new IllegalArgumentException("Cannot calculate spread. Please check setup");
    }
    return (-pvFirst - pvSecond) / pvSpread;
  }

  /**
   * This assumes that the spread is pay on the foreign leg (i.e. foreign ibor + spread against domestic ibor)
   * @param ccs The Cross Currency Swap.
   * @param curves The valuation curves.
   * @return The spread on the foreign leg of a CCS
   */
  @Override
  public Double visitCrossCurrencySwap(final CrossCurrencySwap ccs, final YieldCurveBundle curves) {
    FloatingRateNote dFRN = ccs.getDomesticLeg();
    FloatingRateNote fFRN = ccs.getForeignLeg();
    AnnuityCouponIbor dFloatLeg = dFRN.getFloatingLeg().withZeroSpread();
    AnnuityCouponIbor fFloatLeg = fFRN.getFloatingLeg().withZeroSpread();
    AnnuityCouponFixed fAnnuity = fFRN.getFloatingLeg().withUnitCoupons();

    double dPV = PVC.visit(dFloatLeg, curves) + PVC.visit(dFRN.getFirstLeg(), curves); //this is in domestic currency
    double fPV = PVC.visit(fFloatLeg, curves) + PVC.visit(fFRN.getFirstLeg(), curves); //this is in foreign currency
    double fAnnuityPV = PVC.visit(fAnnuity, curves); //this is in foreign currency

    double fx = ccs.getSpotFX(); //TODO remove having CCS holding spot FX rate 

    return (dPV - fx * fPV) / fx / fAnnuityPV;
  }

  @Override
  public Double visitForexForward(final ForexForward fx, final YieldCurveBundle curves) {
    //TODO this is not a par rate, it is a forward FX rate
    final YieldAndDiscountCurve curve1 = curves.getCurve(fx.getPaymentCurrency1().getFundingCurveName());
    final YieldAndDiscountCurve curve2 = curves.getCurve(fx.getPaymentCurrency2().getFundingCurveName());
    final double t = fx.getPaymentTime();
    return fx.getSpotForexRate() * curve2.getDiscountFactor(t) / curve1.getDiscountFactor(t);
  }

  /**
   * This gives you the bond coupon, for a given yield curve, that renders the bond par (present value of all cash flows equal to 1.0)
   * For a bonds yield use ??????????????? //TODO
   * @param bond the bond
   * @param curves the input curves
   * @return the par rate
   */
  @Override
  public Double visitBond(final Bond bond, final YieldCurveBundle curves) {
    final double pvann = PVC.visit(bond.getUnitCouponAnnuity(), curves);
    final double matPV = PVC.visit(bond.getPrinciplePayment(), curves);
    return (1 - matPV) / pvann;
  }

  @Override
  public Double visitCouponIbor(final CouponIbor payment, final YieldCurveBundle data) {
    final YieldAndDiscountCurve curve = data.getCurve(payment.getForwardCurveName());
    return (curve.getDiscountFactor(payment.getFixingPeriodStartTime()) / curve.getDiscountFactor(payment.getFixingPeriodEndTime()) - 1.0) / payment.getFixingYearFraction();
  }

  @Override
  public Double visitCouponIborGearing(final CouponIborGearing payment, final YieldCurveBundle data) {
    final YieldAndDiscountCurve curve = data.getCurve(payment.getForwardCurveName());
    return (curve.getDiscountFactor(payment.getFixingPeriodStartTime()) / curve.getDiscountFactor(payment.getFixingPeriodEndTime()) - 1.0) / payment.getFixingAccrualFactor();
  }

  @Override
  public Double visitCouponOIS(final CouponOIS payment, final YieldCurveBundle data) {
    return METHOD_OIS.parRate(payment, data);
  }

  @Override
  public Double visitCapFloorIbor(final CapFloorIbor payment, final YieldCurveBundle data) {
    return visitCouponIbor(payment, data);
  }

  @Override
  public Double visitZZZCouponOIS(final ZZZCouponOIS payment, final YieldCurveBundle data) {
    final YieldAndDiscountCurve fundingCurve = data.getCurve(payment.getFundingCurveName());
    final double ta = payment.getStartTime();
    final double tb = payment.getEndTime();
    return (fundingCurve.getInterestRate(tb) * tb - fundingCurve.getInterestRate(ta) * ta) / payment.getRateYearFraction();
  }

  @Override
  public Double visitFixedFloatSwap(final FixedFloatSwap swap, final YieldCurveBundle data) {
    return visitFixedCouponSwap(swap, data);
  }

  @Override
  public Double visitCouponIborFixed(final CouponIborFixed payment, final YieldCurveBundle data) {
    return visitCouponIbor(payment.toCouponIbor(), data);
  }

}
