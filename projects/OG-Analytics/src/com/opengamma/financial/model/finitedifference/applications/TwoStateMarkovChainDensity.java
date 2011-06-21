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
import com.opengamma.math.function.Function;
import com.opengamma.math.function.Function1D;
import com.opengamma.math.statistics.distribution.NormalDistribution;
import com.opengamma.math.surface.FunctionalDoublesSurface;

/**
 *  Solves a coupled forward PDE (i.e. coupled Fokker-Plank) for the density of an asset when the process is CEV with vol levels determined by a
 *  two state Markov chain. The densities, p(t,s,state1) & p(t,s,state2), are such that int_{0}^{\infty} p(t,s,stateX) ds gives the probability
 *  of being in state X at time t, and (p(t,s,state1)+p(t,s,state2))*ds is the probability that the asset with be between s and s + ds at time t.   
 */
public class TwoStateMarkovChainDensity {
  private final static double THETA = 1.0;

  private final CoupledPDEDataBundle _data1;
  private final CoupledPDEDataBundle _data2;

  public TwoStateMarkovChainDensity(final ForwardCurve forward, final double vol1, final double deltaVol,
      final double lambda12, final double lambda21, final double probS1, final double beta1, final double beta2) {
    this(forward, new TwoStateMarkovChainDataBundle(vol1, vol1 + deltaVol, lambda12, lambda21, probS1, beta1, beta2));
  }

  public TwoStateMarkovChainDensity(final ForwardCurve forward, final TwoStateMarkovChainDataBundle data) {
    Validate.notNull(forward, "null forward");
    Validate.notNull(data, "null data");

    _data1 = getCoupledPDEDataBundle(forward, data.getVol1(), data.getLambda12(), data.getLambda21(), data.getP0(), data.getBeta1());
    _data2 = getCoupledPDEDataBundle(forward, data.getVol2(), data.getLambda21(), data.getLambda12(), 1.0 - data.getP0(), data.getBeta2());
  }

  PDEFullResults1D[] solve(PDEGrid1D grid) {

    //BoundaryCondition lower = new FixedSecondDerivativeBoundaryCondition(0, grid.getSpaceNode(0), true);
    BoundaryCondition lower = new NeumannBoundaryCondition(0.0, grid.getSpaceNode(0), true);
    //BoundaryCondition lower = new DirichletBoundaryCondition(0.0, grid.getSpaceNode(0));//TODO for beta < 0.5 zero is accessible and thus there will be non-zero 
    //density there
    BoundaryCondition upper = new DirichletBoundaryCondition(0.0, grid.getSpaceNode(grid.getNumSpaceNodes() - 1));

    CoupledFiniteDifference solver = new CoupledFiniteDifference(THETA, true);
    PDEResults1D[] res = solver.solve(_data1, _data2, grid, lower, upper, lower, upper, null);
    //handle this with generics  
    PDEFullResults1D res1 = (PDEFullResults1D) res[0];
    PDEFullResults1D res2 = (PDEFullResults1D) res[1];
    return new PDEFullResults1D[] {res1, res2 };
  }

  private CoupledPDEDataBundle getCoupledPDEDataBundle(final ForwardCurve forward, final double vol, final double lambda1, final double lambda2,
      final double initialProb, final double beta) {

    final Function<Double, Double> a = new Function<Double, Double>() {
      @Override
      public Double evaluate(final Double... ts) {
        Validate.isTrue(ts.length == 2);
        double s = ts[1];
        if (s <= 0.0) {//TODO review how to handle absorption 
          s = -s;
        }
        return -Math.pow(s, 2 * beta) * vol * vol / 2;
      }
    };

    final Function<Double, Double> b = new Function<Double, Double>() {
      @Override
      public Double evaluate(final Double... ts) {
        Validate.isTrue(ts.length == 2);
        double t = ts[0];
        double s = ts[1];
        if (s < 0.0) {
          s = -s;
        }
        double temp = (s < 0.0 ? 0.0 : 2 * vol * vol * beta * Math.pow(s, 2 * (beta - 1)));
        return s * (forward.getDrift(t) - temp);
      }
    };

    final Function<Double, Double> c = new Function<Double, Double>() {
      @Override
      public Double evaluate(final Double... ts) {
        Validate.isTrue(ts.length == 2);
        double t = ts[0];
        double s = ts[1];

        if (s < 0.) {
          s = -s;
        }
        double temp = (beta == 1.0 ? 1.0 : Math.pow(s, 2 * (beta - 1)));
        if (s < 0) {
          temp = 0.0;
        }
        return lambda1 + forward.getDrift(t) - vol * vol * beta * (2 * beta - 1) * temp;
      }
    };

    //using a log-normal distribution with a very small Standard deviation as a proxy for a Dirac delta
    final Function1D<Double, Double> initialCondition = new Function1D<Double, Double>() {
      private final double _volRootTOffset = 0.01;

      @Override
      public Double evaluate(Double s) {
        if (s <= 0 || initialProb == 0) {
          return 0.0;
        }
        double x = Math.log(s / forward.getSpot());
        NormalDistribution dist = new NormalDistribution(0, _volRootTOffset);
        return initialProb * dist.getPDF(x) / s;
      }
    };

    return new CoupledPDEDataBundle(FunctionalDoublesSurface.from(a), FunctionalDoublesSurface.from(b), FunctionalDoublesSurface.from(c), -lambda2, initialCondition);
  }
}
