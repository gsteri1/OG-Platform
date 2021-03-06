/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.security.rest;

import com.opengamma.util.rest.AbstractResourceService;

/**
 * 
 */
public final class SecurityMasterServiceNames {
  
  /**
   * 
   */
  /* package */static final String DEFAULT_SECURITYMASTER_NAME = AbstractResourceService.DEFAULT_RESOURCE_NAME;

  /**
   * For add (POST), get (GET), update/correct (PUT) and remove (DELETE).
   */
  public static final String SECURITYMASTER_SECURITY = "security";
  /**
   * For meta-data (GET).
   */
  public static final String SECURITYMASTER_METADATA = "metaData";
  /**
   * For search (GET).
   */
  public static final String SECURITYMASTER_SEARCH = "search";
  /**
   * For history (GET).
   */
  public static final String SECURITYMASTER_HISTORIC = "historic";

  private SecurityMasterServiceNames() {
  }

}
