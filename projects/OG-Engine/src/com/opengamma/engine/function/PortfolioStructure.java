/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.function;

import java.util.ArrayList;
import java.util.List;

import com.opengamma.core.position.PortfolioNode;
import com.opengamma.core.position.Position;
import com.opengamma.core.position.PositionSource;
import com.opengamma.id.UniqueId;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.PublicAPI;

/**
 * Service to interrogate the portfolio structure. 
 */
@PublicAPI
public class PortfolioStructure {

  /**
   * The portfolio source.
   */
  private final PositionSource _positionSource;

  /**
   * Constructs a portfolio structure querying service using the underlying position
   * source for portfolio information.
   * 
   * @param positionSource the underlying position source, not null
   */
  public PortfolioStructure(final PositionSource positionSource) {
    ArgumentChecker.notNull(positionSource, "positionSource");
    _positionSource = positionSource;
  }

  /**
   * Returns the position source used by the querying service.
   * 
   * @return the position source
   */
  public PositionSource getPositionSource() {
    return _positionSource;
  }

  //-------------------------------------------------------------------------
  /**
   * Returns the portfolio node that is the immediate parent of the given node.
   * This is equivalent to resolving the unique identifier reported by a portfolio node as its parent.
   * 
   * @param node the node to search for, not null
   * @return the parent node, null if the parent cannot be resolved or the node is a root node
   */
  public PortfolioNode getParentNode(final PortfolioNode node) {
    ArgumentChecker.notNull(node, "node");
    return getParentNodeImpl(node);
  }

  private PortfolioNode getParentNodeImpl(final PortfolioNode node) {
    final UniqueId parent = node.getParentNodeId();
    if (parent == null) {
      return null;
    }
    return getPositionSource().getPortfolioNode(parent);
  }

  /**
   * Returns the portfolio node that a position is underneath.
   * This is equivalent to resolving the unique identifier reported by the position object.
   * 
   * @param position the position to search for, not null
   * @return the portfolio node, null if the node cannot be resolved
   */
  public PortfolioNode getParentNode(final Position position) {
    ArgumentChecker.notNull(position, "position");
    final UniqueId parent = position.getParentNodeId();
    if (parent == null) {
      return null;
    }
    return getPositionSource().getPortfolioNode(parent);
  }

  //-------------------------------------------------------------------------
  /**
   * Returns the root node for the portfolio containing the given node.
   * This is equivalent to traversing up the tree until the root is found.
   * 
   * @param node the node to search for, not null
   * @return the root node, null if parent node hierarchy incomplete
   */
  public PortfolioNode getRootPortfolioNode(final PortfolioNode node) {
    ArgumentChecker.notNull(node, "node");
    return getRootPortfolioNodeImpl(node);
  }

  /**
   * Returns the root node for the portfolio containing the given position.
   * This is equivalent to traversing up the tree from the position's portfolio node until the root is found.
   * 
   * @param position the position to search for, not null
   * @return the root node, null if parent node hierarchy incomplete
   */
  public PortfolioNode getRootPortfolioNode(final Position position) {
    final PortfolioNode node = getParentNode(position);
    if (node == null) {
      return null;
    }
    return getRootPortfolioNodeImpl(node);
  }

  private PortfolioNode getRootPortfolioNodeImpl(PortfolioNode node) {
    UniqueId parent = node.getParentNodeId();
    while (parent != null) {
      node = getPositionSource().getPortfolioNode(parent);
      if (node == null) {
        // Position source is broken
        return null;
      }
      parent = node.getParentNodeId();
    }
    return node;
  }

  //-------------------------------------------------------------------------
  /**
   * Returns <strong>all</strong> positions underneath a portfolio node.
   * This is equivalent to traversing down the tree from the current node to all leaf nodes.
   * 
   * @param node  the node to search for, not null
   * @return the list of all positions found, not null
   */
  public List<Position> getAllPositions(final PortfolioNode node) {
    ArgumentChecker.notNull(node, "node");
    final List<Position> result = new ArrayList<Position>();
    getAllPositionsImpl(node, result);
    return result;
  }

  private void getAllPositionsImpl(final PortfolioNode node, final List<Position> result) {
    result.addAll(node.getPositions());
    for (PortfolioNode child : node.getChildNodes()) {
      getAllPositionsImpl(child, result);
    }
  }

}
