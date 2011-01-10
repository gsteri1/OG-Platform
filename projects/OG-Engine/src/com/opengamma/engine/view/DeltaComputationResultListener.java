/**
 * Copyright (C) 2009 - Present by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view;

import com.opengamma.util.PublicAPI;

/**
 * A callback for code that only wants to receive delta updates
 * on view recomputation.
 *
 * @author kirk
 */
@PublicAPI
public interface DeltaComputationResultListener extends ComputationListener {

  void deltaResultAvailable(ViewDeltaResultModel deltaModel);
  
}
