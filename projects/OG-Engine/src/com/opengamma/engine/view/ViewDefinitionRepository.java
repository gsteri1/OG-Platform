/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view;

import java.util.Map;
import java.util.Set;

import com.opengamma.core.change.ChangeProvider;
import com.opengamma.id.UniqueId;

/**
 * Allows modules, specifically the {@link ViewProcessor}, access to
 * {@link ViewDefinition}s.
 *
 * @author kirk
 */
public interface ViewDefinitionRepository extends ChangeProvider {
  
  /**
   * Returns the set of Ids of all currently defined views.
   * 
   * @return the definition Ids, not null
   */
  Set<UniqueId> getDefinitionIds();
    
  /**
   * Returns the current set of ViewId/ViewName pairs
   * 
   * @return the current set of ViewId/ViewName pairs
   */
  Map<UniqueId, String> getDefinitionEntries();
  
  /**
   * Returns a view definition matching the supplied name (KV: this should really return a matching set)
   * 
   * @param definitionName the name of the view definitions to be returned
   * @return a view definitions matching the supplied name, or null if none found
   */
  ViewDefinition getDefinition(String definitionName);
  
  /**
   * Returns the view definition with the supplied unique id.
   * 
   * @param definitionId the unique id of the view, not null
   * @return the view definition, or null if the name does not exist
   */
  ViewDefinition getDefinition(UniqueId definitionId);

}
