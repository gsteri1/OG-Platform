/**
 * Copyright (C) 2009 - 2011 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.id;

import com.opengamma.util.PublicAPI;

/**
 * The type of identifier search.
 */
@PublicAPI
public enum IdentifierSearchType {

  /**
   * Match requires that the target must contain exactly the same set of identifiers.
   */
  EXACT,
  /**
   * Match requires that the target must contain all of the search identifiers.
   */
  ALL,
  /**
   * Match requires that the target must contain any of the search identifiers.
   */
  ANY,
  /**
   * Match requires that the target must contain none of the search identifiers.
   */
  NONE,

}