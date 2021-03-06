/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.web.json;

import com.opengamma.financial.analytics.volatility.cube.VolatilityCubeDefinition;
import com.opengamma.util.ArgumentChecker;

/**
 * Custom JSON builder to convert VolatilityCubeDefinition to JSON object and back again
 */
public final class VolatilityCubeDefinitionJSONBuilder extends AbstractJSONBuilder<VolatilityCubeDefinition> {
     
  /**
   * Singleton
   */
  public static final VolatilityCubeDefinitionJSONBuilder INSTANCE = new VolatilityCubeDefinitionJSONBuilder();
  
  /**
   * JSON template
   */
  private static final String TEMPLATE = createTemplate();
  
  /**
   * Restricted constructor
   */
  private VolatilityCubeDefinitionJSONBuilder() {
  }

  private static String createTemplate() {
    return null;
  }

  @Override
  public VolatilityCubeDefinition fromJSON(String json) {
    ArgumentChecker.notNull(json, "JSON document");
    return fromJSON(VolatilityCubeDefinition.class, json);
  }

  @Override
  public String toJSON(VolatilityCubeDefinition volatilityCubeDefinition) {
    ArgumentChecker.notNull(volatilityCubeDefinition, "volatilityCubeDefinition");
    return toJSON(volatilityCubeDefinition, VolatilityCubeDefinition.class);
  }

  @Override
  public String getTemplate() {
    return TEMPLATE;
  }

}
