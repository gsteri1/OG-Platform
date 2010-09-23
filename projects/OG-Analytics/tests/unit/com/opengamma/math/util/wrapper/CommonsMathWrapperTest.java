/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.util.wrapper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.junit.Test;

import com.opengamma.math.function.Function1D;
import com.opengamma.math.function.FunctionND;
import com.opengamma.math.function.RealPolynomialFunction1D;
import com.opengamma.math.matrix.DoubleMatrix1D;
import com.opengamma.math.matrix.DoubleMatrix2D;
import com.opengamma.math.number.ComplexNumber;

/**
 * 
 */
public class CommonsMathWrapperTest {
  private static final DoubleMatrix1D OG_VECTOR = new DoubleMatrix1D(new double[] {1, 2, 3});
  private static final DoubleMatrix2D OG_MATRIX = new DoubleMatrix2D(new double[][] {new double[] {1, 2, 3}, new double[] {4, 5, 6}, new double[] {7, 8, 9}});
  private static final Function1D<Double, Double> OG_FUNCTION_1D = new Function1D<Double, Double>() {

    @Override
    public Double evaluate(final Double x) {
      return x * x + 7 * x + 12;
    }

  };
  private static final ComplexNumber OG_COMPLEX = new ComplexNumber(1, 2);
  private static final FunctionND<Double, Double> OG_FUNCTION_ND = new FunctionND<Double, Double>(4) {

    @Override
    protected Double evaluateFunction(final Double[] x) {
      return x[0] * x[0] + 2 * x[1] - 3 * x[2] + x[3];
    }
  };
  private static final RealPolynomialFunction1D OG_POLYNOMIAL = new RealPolynomialFunction1D(new double[] {3, 4, -1, 5, -3});

  @Test(expected = IllegalArgumentException.class)
  public void testNull1DMatrix() {
    CommonsMathWrapper.wrap((DoubleMatrix1D) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullVector() {
    CommonsMathWrapper.unwrap((RealVector) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNull1DFunction() {
    CommonsMathWrapper.wrap((Function1D<Double, Double>) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullNDFunction() {
    CommonsMathWrapper.wrap((FunctionND<Double, Double>) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullMatrix() {
    CommonsMathWrapper.wrap((DoubleMatrix2D) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullRealMatrix() {
    CommonsMathWrapper.unwrap((RealMatrix) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullComplexNumber() {
    CommonsMathWrapper.wrap((ComplexNumber) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullLagrange() {
    CommonsMathWrapper.unwrap((PolynomialFunctionLagrangeForm) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullRealPointVectorPair() {
    CommonsMathWrapper.unwrap((RealPointValuePair) null);
  }

  @Test
  public void testVector() {
    final RealVector commons = CommonsMathWrapper.wrap(OG_VECTOR);
    assertEquals(CommonsMathWrapper.unwrap(commons), OG_VECTOR);
  }

  @Test
  public void testVectorAsMatrix() {
    final RealMatrix commons = CommonsMathWrapper.wrapAsMatrix(OG_VECTOR);
    final double[][] data = commons.getData();
    assertEquals(data.length, OG_VECTOR.getNumberOfElements());
    assertEquals(data[0].length, 1);
  }

  @Test
  public void test1DFunction() throws FunctionEvaluationException {
    final UnivariateRealFunction commons = CommonsMathWrapper.wrap(OG_FUNCTION_1D);
    for (int i = 0; i < 100; i++) {
      assertEquals(OG_FUNCTION_1D.evaluate((double) i), commons.value(i), 1e-15);
    }
  }

  @Test
  public void testNDFunction() throws FunctionEvaluationException {
    final Double[] x1 = new Double[4];
    final double[] x2 = new double[4];
    final MultivariateRealFunction commons = CommonsMathWrapper.wrap(OG_FUNCTION_ND);
    for (int i = 0; i < 100; i++) {
      for (int j = 0; j < 4; j++) {
        x1[j] = (double) i;
        x2[j] = x1[j];
      }
      assertEquals(OG_FUNCTION_ND.evaluate(x1), commons.value(x2), 1e-15);
    }
  }

  @Test
  public void testMatrix() {
    final RealMatrix commons = CommonsMathWrapper.wrap(OG_MATRIX);
    final double[][] unwrapped = CommonsMathWrapper.unwrap(commons).getData();
    final double[][] ogData = OG_MATRIX.getData();
    final int n = unwrapped.length;
    assertEquals(n, ogData.length);
    for (int i = 0; i < n; i++) {
      assertArrayEquals(unwrapped[i], ogData[i], 1e-15);
    }
  }

  @Test
  public void testComplexNumber() {
    final Complex commons = CommonsMathWrapper.wrap(OG_COMPLEX);
    assertEquals(commons.getReal(), OG_COMPLEX.getReal(), 0);
    assertEquals(commons.getImaginary(), OG_COMPLEX.getImaginary(), 0);
  }

  @Test
  public void testLagrange() {
    final int n = OG_POLYNOMIAL.getCoefficients().length;
    final double[] x = new double[n];
    final double[] y = new double[n];
    for (int i = 0; i < n; i++) {
      x[i] = i;
      y[i] = OG_POLYNOMIAL.evaluate(x[i]);
    }
    final Function1D<Double, Double> unwrapped = CommonsMathWrapper.unwrap(new PolynomialFunctionLagrangeForm(x, y));
    for (int i = 0; i < 100; i++) {
      assertEquals(unwrapped.evaluate(i + 0.5), OG_POLYNOMIAL.evaluate(i + 0.5), 1e-9);
    }
  }

  @Test
  public void testRealPointValuePair() {
    final double[] x = new double[] {1, 2, 3};
    final double[] y = CommonsMathWrapper.unwrap(new RealPointValuePair(x, 0));
    assertArrayEquals(x, y, 0);
  }
}