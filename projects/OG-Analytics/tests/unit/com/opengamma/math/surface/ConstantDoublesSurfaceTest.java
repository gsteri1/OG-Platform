/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.surface;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.opengamma.util.tuple.DoublesPair;

/**
 * 
 */
public class ConstantDoublesSurfaceTest {
  private static final double Z1 = 12;
  private static final double Z2 = 34;
  private static final String NAME1 = "a";
  private static final String NAME2 = "s";
  private static final ConstantDoublesSurface SURFACE = new ConstantDoublesSurface(Z1, NAME1);

  @Test(expected = UnsupportedOperationException.class)
  public void testGetXData() {
    SURFACE.getXData();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetYData() {
    SURFACE.getYData();
  }

  @Test
  public void testEqualsAndHashCode() {
    ConstantDoublesSurface other = new ConstantDoublesSurface(Z1, NAME1);
    assertEquals(SURFACE, other);
    assertEquals(SURFACE.hashCode(), other.hashCode());
    other = new ConstantDoublesSurface(Z2, NAME1);
    assertFalse(SURFACE.equals(other));
    other = new ConstantDoublesSurface(Z1, NAME2);
    assertFalse(SURFACE.equals(other));
    other = new ConstantDoublesSurface(Z1);
    assertFalse(SURFACE.equals(other));
  }

  @Test
  public void testGetters() {
    assertEquals(SURFACE.getName(), NAME1);
    assertArrayEquals(SURFACE.getZData(), new Double[] {Z1});
    assertEquals(SURFACE.size(), 1);
    assertEquals(SURFACE.getZValue(1., 2.), Z1, 0);
    assertEquals(SURFACE.getZValue(DoublesPair.of(1., 4.)), Z1, 0);
    final Double[] z = SURFACE.getZData();
    assertTrue(z == SURFACE.getZData());
  }

  @Test
  public void testStaticConstruction() {
    ConstantDoublesSurface surface = new ConstantDoublesSurface(Z1);
    ConstantDoublesSurface other = ConstantDoublesSurface.from(Z1);
    assertArrayEquals(surface.getZData(), other.getZData());
    assertFalse(surface.equals(other));
    surface = new ConstantDoublesSurface(Z1, NAME1);
    other = ConstantDoublesSurface.from(Z1, NAME1);
    assertEquals(surface, other);
  }
}