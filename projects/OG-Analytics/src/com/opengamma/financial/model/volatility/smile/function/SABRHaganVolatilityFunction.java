/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.volatility.smile.function;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.financial.model.option.pricing.analytic.formula.EuropeanVanillaOption;
import com.opengamma.math.function.Function1D;
import com.opengamma.util.CompareUtils;

/**
 * Class with the Hagan et al SABR volatility function.
 * Reference: Hagan, P.; Kumar, D.; Lesniewski, A. & Woodward, D. "Managing smile risk", Wilmott Magazine, 2002, September, 84-108
 */
public class SABRHaganVolatilityFunction implements VolatilityFunctionProvider<SABRFormulaData> {

  /**
   * Logger.
   */
  private static final Logger s_logger = LoggerFactory.getLogger(SABRHaganVolatilityFunction.class);

  private static final double CUTOFF_MONEYNESS = 1e-6;
  private static final double EPS = 1e-15;

  @Override
  public Function1D<SABRFormulaData, Double> getVolatilityFunction(final EuropeanVanillaOption option) {
    Validate.notNull(option, "option");

    final double t = option.getTimeToExpiry();
    return new Function1D<SABRFormulaData, Double>() {

      @SuppressWarnings("synthetic-access")
      @Override
      public final Double evaluate(final SABRFormulaData data) {
        Validate.notNull(data, "data");
        final double alpha = data.getAlpha();
        final double beta = data.getBeta();
        final double rho = data.getRho();
        final double nu = data.getNu();
        final double f = data.getForward();

        final double k = Math.max(option.getStrike(), f * CUTOFF_MONEYNESS); // Floored
        // TODO: Improve treatment around strike/k=0?
        double vol, z, zOverChi;
        final double beta1 = 1 - beta;
        if (CompareUtils.closeEquals(f, k, EPS)) {
          final double f1 = Math.pow(f, beta1);
          vol = alpha * (1 + t * (beta1 * beta1 * alpha * alpha / 24 / f1 / f1 + rho * alpha * beta * nu / 4 / f1 + nu * nu * (2 - 3 * rho * rho) / 24)) / f1;

        } else {
          if (CompareUtils.closeEquals(beta, 0, EPS)) {
            final double ln = Math.log(f / k);
            z = nu * Math.sqrt(f * k) * ln / alpha;
            zOverChi = getZOverChi(rho, z);
            vol = alpha * ln * zOverChi * (1 + t * (alpha * alpha / f / k + nu * nu * (2 - 3 * rho * rho)) / 24) / (f - k);
          } else if (CompareUtils.closeEquals(beta, 1, EPS)) {
            final double ln = Math.log(f / k);
            z = nu * ln / alpha;
            zOverChi = getZOverChi(rho, z);
            vol = alpha * zOverChi * (1 + t * (rho * alpha * nu / 4 + nu * nu * (2 - 3 * rho * rho) / 24));
          } else {
            final double ln = Math.log(f / k);
            final double f1 = Math.pow(f * k, beta1);
            final double f1Sqrt = Math.sqrt(f1);
            final double lnBetaSq = Math.pow(beta1 * ln, 2);
            z = nu * f1Sqrt * ln / alpha;
            zOverChi = getZOverChi(rho, z);
            final double first = alpha / (f1Sqrt * (1 + lnBetaSq / 24 + lnBetaSq * lnBetaSq / 1920));
            final double second = zOverChi;
            final double third = 1 + t * (beta1 * beta1 * alpha * alpha / 24 / f1 + rho * nu * beta * alpha / 4 / f1Sqrt + nu * nu * (2 - 3 * rho * rho) / 24);
            vol = first * second * third;
          }
        }

        return vol;
      }
    };
  }

  /**
   * Return the Black implied volatility in the SABR model and its derivatives.
   * @param option The option.
   * @param data The SABR data.
   * @return An array with [0] the volatility, [1] Derivative w.r.t the forward, [2] the derivative w.r.t the strike, [3] the derivative w.r.t. to alpha,
   * [4] the derivative w.r.t. to rho, [5] the derivative w.r.t. to nu
   */
  public double[] getVolatilityAdjoint(final EuropeanVanillaOption option, final SABRFormulaData data) {
    /**
     * The array storing the price and derivatives.
     */
    final double[] volatilityAdjoint = new double[6];

    final double forward = data.getForward();
    final double strike = Math.max(option.getStrike(), forward * CUTOFF_MONEYNESS);
    final double timeToExpiry = option.getTimeToExpiry();
    final double alpha = data.getAlpha();
    final double beta = data.getBeta();
    final double rho = data.getRho();
    final double nu = data.getNu();

    // Implementation note: Forward sweep.
    final double sfK = Math.pow(forward * strike, (1 - beta) / 2);
    final double lnrfK = Math.log(forward / strike);
    final double z = nu / alpha * sfK * lnrfK;
    double rzxz;
    double xz = 0;
    if (Math.abs(forward - strike) < 1E-7) {
      rzxz = 1 - rho * z / 2; // order 1
    } else {
      if (CompareUtils.closeEquals(1.0 - rho, 0.0, 1e-8)) {
        if (z >= 1.0) {
          rzxz = 0.0;
        } else {
          rzxz = -z / Math.log(1 - z);
        }
      } else {
        xz = Math.log((Math.sqrt(1 - 2 * rho * z + z * z) + z - rho) / (1 - rho));
        rzxz = z / xz;
      }
    }
    final double sf1 = sfK * (1 + (1 - beta) * (1 - beta) / 24 * (lnrfK * lnrfK) + Math.pow(1 - beta, 4) / 1920 * Math.pow(lnrfK, 4));
    final double sf2 = (1 + (Math.pow((1 - beta) * alpha / sfK, 2) / 24 + (rho * beta * nu * alpha) / (4 * sfK) + (2 - 3 * rho * rho) * nu * nu / 24) * timeToExpiry);
    volatilityAdjoint[0] = alpha / sf1 * rzxz * sf2;

    // Implementation note: Backward sweep.
    final double vBar = 1;
    final double sf2Bar = alpha / sf1 * rzxz * vBar;
    final double sf1Bar = -alpha / (sf1 * sf1) * rzxz * sf2 * vBar;
    final double rzxzBar = alpha / sf1 * sf2 * vBar;
    double zBar;
    double xzBar = 0;
    if (Math.abs(forward - strike) < 1E-7) {
      zBar = -rho / 2 * rzxzBar;
    } else {
      if (CompareUtils.closeEquals(1.0 - rho, 0.0, 1e-8)) {
        if (z >= 1.0) {
          zBar = 0.0;
        } else {
          zBar = -1.0 / Math.log(1 - z) * (1 + z / Math.log(1 - z) / (1 - z)) * rzxzBar;
        }
      } else {
        xzBar = -z / (xz * xz) * rzxzBar;
        zBar = 1 / xz * rzxzBar + 1 / ((Math.sqrt(1 - 2 * rho * z + z * z) + z - rho)) * (0.5 * Math.pow(1 - 2 * rho * z + z * z, -0.5) * (-2 * rho + 2 * z) + 1) * xzBar;
      }
    }
    final double lnrfKBar = sfK * ((1 - beta) * (1 - beta) / 12 * lnrfK + Math.pow(1 - beta, 4) / 1920 * 4 * Math.pow(lnrfK, 3)) * sf1Bar + nu / alpha * sfK * zBar;
    //TODO R white 28/07/2011 This could be written as 
    //    final double sfKBar = nu / alpha * lnrfK * zBar + sf1 / sfK * sf1Bar
    //        - (Math.pow((1 - beta) * alpha, 2) / Math.pow(sfK, 3) / 12 + (rho * beta * nu * alpha) / 4 / (sfK * sfK)) * timeToExpiry * sf2Bar;
    final double sfKBar = nu / alpha * lnrfK * zBar + (1 + (1 - beta) * (1 - beta) / 24 * lnrfK * lnrfK + Math.pow(1 - beta, 4) / 1920 * Math.pow(lnrfK, 4)) * sf1Bar
            + (-Math.pow((1 - beta) * alpha, 2) / Math.pow(sfK, 3) / 12 - (rho * beta * nu * alpha) / 4 / (sfK * sfK)) * timeToExpiry * sf2Bar;
    final double strikeBar = -1 / strike * lnrfKBar + (1 - beta) * sfK / (2 * strike) * sfKBar;
    final double forwardBar = 1 / forward * lnrfKBar + (1 - beta) * sfK / (2 * forward) * sfKBar;
    final double nuBar = 1 / alpha * sfK * lnrfK * zBar + ((rho * beta * alpha) / (4 * sfK) + (2 - 3 * rho * rho) * nu / 12) * timeToExpiry * sf2Bar;
    double rhoBar;
    if (Math.abs(forward - strike) < 1E-7) {
      rhoBar = -z / 2 * rzxzBar;
    } else {
      if (CompareUtils.closeEquals(1.0 - rho, 0.0, 1e-8)) {
        s_logger.error("SABR derivatives are not correct in the degenerate case were rho=1.0.");
        //FIXME: Complete the derivatives in the degenerate case.
        rhoBar = 0.0;
      } else {
        rhoBar = (1 / (Math.sqrt(1 - 2 * rho * z + z * z) + z - rho) * (-Math.pow(1 - 2 * rho * z + z * z, -0.5) * z - 1) + 1 / (1 - rho)) * xzBar;
      }
    }
    rhoBar += ((beta * nu * alpha) / (4 * sfK) - rho * nu * nu / 4) * timeToExpiry * sf2Bar;

    final double alphaBar = -nu / (alpha * alpha) * sfK * lnrfK * zBar + (((1 - beta) * alpha / sfK) * ((1 - beta) / sfK) / 12 + (rho * beta * nu) / (4 * sfK)) * timeToExpiry * sf2Bar + 1 / sf1
        * rzxz * sf2 * vBar;
    volatilityAdjoint[1] = forwardBar;
    volatilityAdjoint[2] = strikeBar;
    volatilityAdjoint[3] = alphaBar;
    volatilityAdjoint[4] = rhoBar;
    volatilityAdjoint[5] = nuBar;

    return volatilityAdjoint;
  }

  /**
   * Computes the first and second order derivatives of the Black implied volatility in the SABR model.
   * @param option The option.
   * @param data The SABR data.
   * @param volatilityD The array used to return the first order derivatives. [0] Derivative w.r.t the forward, [1] the derivative w.r.t the strike
   * @param volatilityD2 The array of array used to return the second order derivative. Only the second order derivative with respect to the forward and strike are implemented.
   * [0][0] forward-forward; [0][1] forward-strike; [1][1] strike-strike. 
   * Implemented by finite difference on the first order derivative.
   * @return The Black implied volatility.
   */
  public double getVolatilityAdjoint2(final EuropeanVanillaOption option, final SABRFormulaData data, final double[] volatilityD, final double[][] volatilityD2) {
    final double k = Math.max(option.getStrike(), 0.000001);
    final double theta = option.getTimeToExpiry();
    final double alpha = data.getAlpha();
    final double beta = data.getBeta();
    final double rho = data.getRho();
    final double nu = data.getNu();
    final double f = data.getForward();
    // Forward
    final double betaO2 = (1 - beta) / 2;
    final double h1 = Math.pow(f * k, betaO2);
    final double h12 = h1 * h1;
    final double h13 = h12 * h1;
    final double h14 = h13 * h1;
    final double h2 = Math.log(f / k);
    final double h22 = h2 * h2;
    final double h23 = h22 * h2;
    final double h24 = h23 * h2;
    final double f1 = h1 * (1 + betaO2 * betaO2 / 6.0 * (h22 + betaO2 * betaO2 / 20.0 * h24));
    final double f2 = nu / alpha * h1 * h2;
    final double f3 = betaO2 * betaO2 / 6.0 * alpha * alpha / h12 + rho * beta * nu * alpha / 4.0 / h1 + (2 - 3 * rho * rho) / 24.0 * nu * nu;
    final double sqrtf2 = Math.sqrt(1 - 2 * rho * f2 + f2 * f2);
    final double x = Math.log((sqrtf2 + f2 - rho) / (1 - rho));
    final double sigma = alpha / f1 * f2 / x * (1 + f3 * theta);
    // First level
    final double sigmaDf1 = -sigma / f1;
    final double xp = ((-2 * rho + 2 * f2) / sqrtf2 / 2.0 + 1) / (sqrtf2 + f2 - rho);
    final double xpp = -((-2 * rho + 2 * f2) / sqrtf2 / 2.0 + 1) * ((-2 * rho + 2 * f2) / sqrtf2 / 2.0 + 1) / ((sqrtf2 + f2 - rho) * (sqrtf2 + f2 - rho))
        + (-(-2 * rho + 2 * f2) * (-2 * rho + 2 * f2) / (sqrtf2 * sqrtf2 * sqrtf2) / 4.0 + 1.0 / sqrtf2) / (sqrtf2 + f2 - rho);
    final double xDr = (-f2 / sqrtf2 - 1 + (sqrtf2 + f2 - rho) / (1 - rho)) / (sqrtf2 + f2 - rho);
    final double sigmaDf2 = alpha / f1 * (1 + f3 * theta) * (1.0 / x - f2 * xp / (x * x));
    final double sigmaDf3 = alpha / f1 * f2 / x * theta;
    final double sigmaDf4 = f2 / x / f1 * (1 + f3 * theta);
    final double sigmaDx = -alpha / f1 * f2 / (x * x) * (1 + f3 * theta);
    final double[][] sigmaD2ff = new double[3][3];
    sigmaD2ff[0][0] = -sigmaDf1 / f1 + sigma / (f1 * f1); //OK
    sigmaD2ff[0][1] = -sigmaDf2 / f1;
    sigmaD2ff[0][2] = -sigmaDf3 / f1;
    sigmaD2ff[1][1] = alpha / f1 * (1 + f3 * theta) * (-2 * xp / (x * x) - f2 * xpp / (x * x) + 2 * f2 * xp * xp / (x * x * x)); // OK
    sigmaD2ff[1][2] = alpha / f1 * theta * (1.0 / x - f2 * xp / (x * x));
    sigmaD2ff[2][2] = 0.0;
    // Second level
    final double[] f1Dh = new double[2];
    final double[] f2Dh = new double[2];
    final double[] f3Dh = new double[2];
    f1Dh[0] = 1 + betaO2 * betaO2 / 6.0 * (h22 + betaO2 * betaO2 / 20.0 * h24);
    f1Dh[1] = h1 * (betaO2 * betaO2 / 6.0 * (2.0 * h2 + 4 * betaO2 * betaO2 / 20.0 * h23));
    f2Dh[0] = nu / alpha * h2;
    f2Dh[1] = nu / alpha * h1;
    f3Dh[0] = -2 * betaO2 * betaO2 / 6.0 * alpha * alpha / h13 - rho * beta * nu * alpha / 4.0 / h12;
    f3Dh[1] = 0.0;
    final double[] f1Dp = new double[3]; // Derivative to sabr parameters
    final double[] f2Dp = new double[3];
    final double[] f3Dp = new double[3];
    final double[] f4Dp = new double[3];
    f1Dp[0] = 0.0;
    f1Dp[1] = 0.0;
    f1Dp[2] = 0.0;
    f2Dp[0] = -f2 / alpha;
    f2Dp[1] = 0.0;
    f2Dp[2] = h1 * h2 / alpha;
    //    double f3 = betaO2 * betaO2 / 6.0 * alpha * alpha / h12 + rho * beta * nu * alpha / 4.0 / h1 + (2 - 3 * rho * rho) / 24.0 * nu * nu;
    f3Dp[0] = betaO2 * betaO2 / 3.0 * alpha / h12 + rho * beta * nu / 4.0 / h1;
    f3Dp[1] = beta * nu * alpha / 4.0 / h1 - rho / 4.0 * nu * nu;
    f3Dp[2] = rho * beta * alpha / 4.0 / h1 + (2 - 3 * rho * rho) / 12.0 * nu;
    f4Dp[0] = 1.0;
    f4Dp[1] = 0.0;
    f4Dp[2] = 0.0;
    final double sigmaDh1 = sigmaDf1 * f1Dh[0] + sigmaDf2 * f2Dh[0] + sigmaDf3 * f3Dh[0];
    final double sigmaDh2 = sigmaDf1 * f1Dh[1] + sigmaDf2 * f2Dh[1] + sigmaDf3 * f3Dh[1];
    final double[][] f1D2hh = new double[2][2];
    final double[][] f2D2hh = new double[2][2];
    final double[][] f3D2hh = new double[2][2];
    f1D2hh[0][0] = 0.0;
    f1D2hh[0][1] = betaO2 * betaO2 / 6.0 * (2.0 * h2 + 4.0 * betaO2 * betaO2 / 20.0 * h23);
    f1D2hh[1][1] = h1 * (betaO2 * betaO2 / 6.0 * (2.0 + 12.0 * betaO2 * betaO2 / 20.0 * h2));
    f2D2hh[0][0] = 0.0;
    f2D2hh[0][1] = nu / alpha;
    f2D2hh[1][1] = 0.0;
    f3D2hh[0][0] = 2.0 * 3.0 * betaO2 * betaO2 / 6.0 * alpha * alpha / h14 + 2.0 * rho * beta * nu * alpha / 4.0 / h13;
    f3D2hh[0][1] = 0.0;
    f3D2hh[1][1] = 0.0;
    final double[][] sigmaD2hh = new double[2][2];
    for (int loopx = 0; loopx < 2; loopx++) {
      for (int loopy = loopx; loopy < 2; loopy++) {
        sigmaD2hh[loopx][loopy] = (sigmaD2ff[0][0] * f1Dh[loopy] + sigmaD2ff[0][1] * f2Dh[loopy] + sigmaD2ff[0][2] * f3Dh[loopy]) * f1Dh[loopx] + sigmaDf1 * f1D2hh[loopx][loopy]
            + (sigmaD2ff[0][1] * f1Dh[loopy] + sigmaD2ff[1][1] * f2Dh[loopy] + sigmaD2ff[1][2] * f3Dh[loopy]) * f2Dh[loopx] + sigmaDf2 * f2D2hh[loopx][loopy]
            + (sigmaD2ff[0][2] * f1Dh[loopy] + sigmaD2ff[1][2] * f2Dh[loopy] + sigmaD2ff[2][2] * f3Dh[loopy]) * f3Dh[loopx] + sigmaDf3 * f3D2hh[loopx][loopy];
      }
    }
    // Third level
    final double h1Df = betaO2 * h1 / f;
    final double h1Dk = betaO2 * h1 / k;
    final double h1D2ff = betaO2 * (h1Df / f - h1 / (f * f));
    final double h1D2kf = betaO2 * h1Dk / f;
    final double h1D2kk = betaO2 * (h1Dk / k - h1 / (k * k));
    final double h2Df = 1.0 / f;
    final double h2Dk = -1.0 / k;
    final double h2D2ff = -1 / (f * f);
    final double h2D2fk = 0.0;
    final double h2D2kk = 1.0 / (k * k);
    volatilityD[0] = sigmaDh1 * h1Df + sigmaDh2 * h2Df;
    volatilityD[1] = sigmaDh1 * h1Dk + sigmaDh2 * h2Dk;
    volatilityD[2] = sigmaDf1 * f1Dp[0] + sigmaDf2 * f2Dp[0] + sigmaDf3 * f3Dp[0] + sigmaDf4 * f4Dp[0];
    volatilityD[3] = sigmaDf1 * f1Dp[1] + sigmaDx * xDr + sigmaDf3 * f3Dp[1] + sigmaDf4 * f4Dp[1];
    volatilityD[4] = sigmaDf1 * f1Dp[2] + sigmaDf2 * f2Dp[2] + sigmaDf3 * f3Dp[2] + sigmaDf4 * f4Dp[2];
    volatilityD2[0][0] = (sigmaD2hh[0][0] * h1Df + sigmaD2hh[0][1] * h2Df) * h1Df + sigmaDh1 * h1D2ff + (sigmaD2hh[0][1] * h1Df + sigmaD2hh[1][1] * h2Df) * h2Df + sigmaDh2 * h2D2ff;
    volatilityD2[0][1] = (sigmaD2hh[0][0] * h1Dk + sigmaD2hh[0][1] * h2Dk) * h1Df + sigmaDh1 * h1D2kf + (sigmaD2hh[0][1] * h1Dk + sigmaD2hh[1][1] * h2Dk) * h2Df + sigmaDh2 * h2D2fk;
    volatilityD2[1][0] = volatilityD2[0][1];
    volatilityD2[1][1] = (sigmaD2hh[0][0] * h1Dk + sigmaD2hh[0][1] * h2Dk) * h1Dk + sigmaDh1 * h1D2kk + (sigmaD2hh[0][1] * h1Dk + sigmaD2hh[1][1] * h2Dk) * h2Dk + sigmaDh2 * h2D2kk;
    return sigma;
  }

  private double getZOverChi(final double rho, final double z) {

    if (CompareUtils.closeEquals(z, 0.0, EPS)) {
      return 1.0;
    }

    final double rhoStar = 1 - rho;
    if (CompareUtils.closeEquals(rhoStar, 0.0, 1e-8)) {
      if (z >= 1.0) {
        return 0.0;
      }
      return -z / Math.log(1 - z);
    }
    // Implementation comment: To avoid numerical instability (0/0) around ATM the first order approximation is used.
    if (CompareUtils.closeEquals(z, 0.0, 1E-7)) {
      return 1.0 - rho * z / 2.0;
    }
    final double chi = Math.log((Math.sqrt(1 - 2 * rho * z + z * z) + z - rho)) - Math.log(rhoStar);
    return z / chi;
  }
}
