/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.integration;

import org.apache.commons.lang.Validate;

import com.opengamma.math.function.DoubleFunction1D;
import com.opengamma.math.function.special.LegendrePolynomialFunction;
import com.opengamma.math.rootfinding.NewtonRaphsonSingleRootFinder;
import com.opengamma.util.tuple.Pair;

/**
 * Class that generates weights and abscissas for Gauss-Legendre quadrature. The weights {@latex.inline $w_i$} are given by:
 * {@latex.ilb %preamble{\\usepackage{amsmath}}
 * \\begin{align*}
 * w_i = \\frac{2}{(1 - x_i^2) L_i'(x_i)^2}
 * \\end{align*}
 * }
 * where {@latex.inline $x_i$} is the {@latex.inline $i^{th}$} root of the orthogonal polynomial and {@latex.inline $L_i'$} is
 * the first derivative of the {@latex.inline $i^{th}$} polynomial. The orthogonal polynomial is generated by
 * {@link com.opengamma.math.function.special.LegendrePolynomialFunction}.
 */
public class GaussLegendreWeightAndAbscissaFunction implements QuadratureWeightAndAbscissaFunction {
  private static final LegendrePolynomialFunction LEGENDRE = new LegendrePolynomialFunction();
  private static final NewtonRaphsonSingleRootFinder ROOT_FINDER = new NewtonRaphsonSingleRootFinder(1e-15);

  /**
   * {@inheritDoc}
   */
  @Override
  public GaussianQuadratureData generate(final int n) {
    Validate.isTrue(n > 0);
    final int mid = (n + 1) / 2;
    final double[] x = new double[n];
    final double[] w = new double[n];
    final Pair<DoubleFunction1D, DoubleFunction1D>[] polynomials = LEGENDRE.getPolynomialsAndFirstDerivative(n);
    final Pair<DoubleFunction1D, DoubleFunction1D> pair = polynomials[n];
    final DoubleFunction1D function = pair.getFirst();
    final DoubleFunction1D derivative = pair.getSecond();
    for (int i = 0; i < mid; i++) {
      final double root = ROOT_FINDER.getRoot(function, derivative, getInitialRootGuess(i, n));
      x[i] = -root;
      x[n - i - 1] = root;
      final double dp = derivative.evaluate(root);
      w[i] = 2 / ((1 - root * root) * dp * dp);
      w[n - i - 1] = w[i];
    }
    return new GaussianQuadratureData(x, w);
  }

  private double getInitialRootGuess(final int i, final int n) {
    return Math.cos(Math.PI * (i + 0.75) / (n + 0.5));
  }
}