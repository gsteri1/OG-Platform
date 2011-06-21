/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.finitedifference.applications;

import org.apache.commons.lang.Validate;

import com.opengamma.financial.model.finitedifference.BoundaryCondition;
import com.opengamma.financial.model.finitedifference.CoupledFiniteDifference;
import com.opengamma.financial.model.finitedifference.CoupledPDEDataBundle;
import com.opengamma.financial.model.finitedifference.DirichletBoundaryCondition;
import com.opengamma.financial.model.finitedifference.NeumannBoundaryCondition;
import com.opengamma.financial.model.finitedifference.PDEFullResults1D;
import com.opengamma.financial.model.finitedifference.PDEGrid1D;
import com.opengamma.financial.model.finitedifference.PDEResults1D;
import com.opengamma.financial.model.interestrate.curve.ForwardCurve;
import com.opengamma.financial.model.volatility.surface.AbsoluteLocalVolatilitySurface;
import com.opengamma.math.function.Function1D;

/**
 * Solves a coupled forward PDE for the price of a call option when the process is CEV with vol levels determined by a two state Markov chain.  
 */
public class TwoStateMarkovChainPricer {
  private static final PDEDataBundleProvider BUNDLE_PROVIDER = new PDEDataBundleProvider();

  private final CoupledPDEDataBundle _data[];

  private final ForwardCurve _forward;
  private final TwoStateMarkovChainDataBundle _chainDB;

  //  private final double _lambda12;
  //  private final double _lambda21;
  //  private final double _p0;

  public TwoStateMarkovChainPricer(final ForwardCurve forward, TwoStateMarkovChainDataBundle chainDB) {
    Validate.notNull(forward, "null forward curve");
    Validate.notNull(chainDB, "null MC DB");

    _forward = forward;
    _chainDB = chainDB;
    _data = BUNDLE_PROVIDER.getCoupledForwardPair(forward, chainDB);
  }

  /**
   * Solves a coupled forward PDE for the price of a call option when the process is CEV with vol levels determined by a two state Markov chain
   * @param forward The forward curve of the underlying asset 
   * @param vol1 The volatility in the 'normal' state
   * @param vol2 The volatility in the 'excited' state
   * @param lambda12 Rate (probably per unit time (years)) to go from 'normal' to 'excited' state
   * @param lambda21 Rate (probably per unit time (years)) to go from 'excited' to 'normal' state
   * @param probS1 Probability of stating in the 'normal' state 
   * @param beta1 CEV parameter in 'normal' state
   * @param beta2 CEV parameter in 'excited' state
   */
  public TwoStateMarkovChainPricer(final ForwardCurve forward, TwoStateMarkovChainDataBundle chainDB, AbsoluteLocalVolatilitySurface localVolOverlay) {
    Validate.notNull(forward, "null forward curve");
    Validate.notNull(chainDB, "null MC DB");
    Validate.notNull(localVolOverlay, "null local vol");

    _forward = forward;
    _chainDB = chainDB;
    _data = BUNDLE_PROVIDER.getCoupledForwardPair(forward, chainDB, localVolOverlay);
  }

  PDEFullResults1D solve(final PDEGrid1D grid, final double theta) {
    Validate.notNull(grid, "null grid");
    Validate.isTrue(0 <= theta && theta <= 1.0, "theta must be in range 0 to 1");

    Validate.isTrue(grid.getSpaceNode(0) == 0.0, "space grid must start at zero");

    Function1D<Double, Double> strikeZeroPrice1 = new Function1D<Double, Double>() {
      @Override
      public Double evaluate(Double t) {
        return probState1(t) * _forward.getSpot();
      }
    };

    Function1D<Double, Double> strikeZeroPrice2 = new Function1D<Double, Double>() {
      @Override
      public Double evaluate(Double t) {
        return (1 - probState1(t)) * _forward.getSpot();
      }
    };

    BoundaryCondition lower1 = new DirichletBoundaryCondition(strikeZeroPrice1, 0.0);
    BoundaryCondition lower2 = new DirichletBoundaryCondition(strikeZeroPrice2, 0.0);

    double kMax = grid.getSpaceNode(grid.getNumSpaceNodes() - 1);

    BoundaryCondition upper = new NeumannBoundaryCondition(0, kMax, false);
    CoupledFiniteDifference solver = new CoupledFiniteDifference(theta, true);
    PDEResults1D[] res = solver.solve(_data[0], _data[1], grid, lower1, upper, lower2, upper, null);
    PDEFullResults1D res1 = (PDEFullResults1D) res[0];
    PDEFullResults1D res2 = (PDEFullResults1D) res[1];

    double[][] prices = new double[grid.getNumTimeNodes()][grid.getNumSpaceNodes()];
    for (int i = 0; i < grid.getNumTimeNodes(); i++) {
      for (int j = 0; j < grid.getNumSpaceNodes(); j++) {
        prices[i][j] = res1.getFunctionValue(j, i) + res2.getFunctionValue(j, i);
      }
    }
    return new PDEFullResults1D(grid, prices);

  }

  private double probState1(final double t) {
    double sum = _chainDB.getLambda12() + _chainDB.getLambda21();
    return _chainDB.getSteadyStateProb() + (_chainDB.getP0() - _chainDB.getSteadyStateProb()) * Math.exp(-sum * t);
  }

}
