/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.math.matrix;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

/**
 * 
 */
public class OGMatrixAlgebraTest {

  private static final MatrixAlgebra ALGEBRA = MatrixAlgebraFactory.getMatrixAlgebra("OG");
  private static final DoubleMatrix2D A = new DoubleMatrix2D(new double[][] { {1., 2., 3.}, {-1., 1., 0.}, {-2., 1., -2.}});
  private static final DoubleMatrix2D B = new DoubleMatrix2D(new double[][] { {1, 1}, {2, -2}, {3, 1}});
  private static final DoubleMatrix2D C = new DoubleMatrix2D(new double[][] { {14, 0}, {1, -3}, {-6, -6}});
  private static final DoubleMatrix1D D = new DoubleMatrix1D(new double[] {1, 1, 1});
  private static final DoubleMatrix1D E = new DoubleMatrix1D(new double[] {-1, 2, 3});
  private static final DoubleMatrix1D F = new DoubleMatrix1D(new double[] {2, -2, 1});

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testMatrixSizeMismatch() {
    ALGEBRA.multiply(B, A);
  }

  @Test
  public void testDotProduct() {
    double res = ALGEBRA.getInnerProduct(E, F);
    assertEquals(-3.0, res, 1e-15);
    res = ALGEBRA.getNorm2(E);
    assertEquals(Math.sqrt(14.0), res, 1e-15);
  }

  @Test
  public void testOuterProduct() {
    final DoubleMatrix2D res = ALGEBRA.getOuterProduct(E, F);
    final int rows = res.getNumberOfRows();
    final int cols = res.getNumberOfColumns();
    int i, j;
    for (i = 0; i < rows; i++) {
      for (j = 0; j < cols; j++) {
        assertEquals(res.getEntry(i, j), E.getEntry(i) * F.getEntry(j), 1e-15);
      }
    }

  }

  @Test
  public void testMultiply() {
    final DoubleMatrix2D c = (DoubleMatrix2D) ALGEBRA.multiply(A, B);
    final int rows = c.getNumberOfRows();
    final int cols = c.getNumberOfColumns();
    int i, j;
    for (i = 0; i < rows; i++) {
      for (j = 0; j < cols; j++) {
        assertEquals(c.getEntry(i, j), C.getEntry(i, j), 1e-15);
      }
    }

    final DoubleMatrix1D d = (DoubleMatrix1D) ALGEBRA.multiply(A, D);
    assertEquals(6, d.getEntry(0), 1e-15);
    assertEquals(0, d.getEntry(1), 1e-15);
    assertEquals(-3, d.getEntry(2), 1e-15);
  }

}
