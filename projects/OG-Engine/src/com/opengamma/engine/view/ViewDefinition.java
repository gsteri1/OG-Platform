/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.ObjectUtils;

import com.opengamma.id.UniqueIdentifier;
import com.opengamma.livedata.msg.UserPrincipal;
import com.opengamma.util.ArgumentChecker;

/**
 * The encapsulated logic that controls how precisely a view is to be constructed
 * and computed.
 */
public class ViewDefinition implements Serializable {
   
  private final String _name;
  private final UniqueIdentifier _portfolioId;
  private final UserPrincipal _liveDataUser;
  
  private final ResultModelDefinition _resultModelDefinition;
  
  private Long _minDeltaCalculationPeriod;
  private Long _maxDeltaCalculationPeriod;
  
  private Long _minFullCalculationPeriod;
  private Long _maxFullCalculationPeriod;
  
  private final Map<String, ViewCalculationConfiguration> _calculationConfigurationsByName =
    new TreeMap<String, ViewCalculationConfiguration>();
  
  /**
   * If true, when a single computation cycle completes, the outputs are written
   * to a temporary file on the disk. This is not useful in a real production 
   * deployment, but can be useful in tests.
   */
  private boolean _dumpComputationCacheToDisk;
  
  //--------------------------------------------------------------------------
  /**
   * Constructs an instance, including a reference portfolio.
   * 
   * @param name  the name of the view definition
   * @param portfolioId  the unique identifier of the reference portfolio for this view definition
   * @param userName  the name of the user who owns the view definition
   */
  public ViewDefinition(String name, UniqueIdentifier portfolioId, String userName) {
    this(name, portfolioId, UserPrincipal.getLocalUser(userName), new ResultModelDefinition());
  }
  
  /**
   * Constructs an instance, without a reference portfolio.
   * 
   * @param name  the name of the view definition
   * @param userName  the name of the user who owns the view definition
   */
  public ViewDefinition(String name, String userName) {
    this(name, UserPrincipal.getLocalUser(userName));
  }
  
  /**
   * Constructs an instance, without a reference portfolio.
   * 
   * @param name  the name of the view definition
   * @param liveDataUser  the user who owns the view definition
   */
  public ViewDefinition(String name, UserPrincipal liveDataUser) {
    this(name, null, liveDataUser);
  }
  
  /**
   * Constructs an instance, without a reference portfolio.
   * 
   * @param name  the name of the view definition
   * @param liveDataUser  the user who owns the view definition
   * @param resultModelDefinition  configuration of the results from the view
   */
  public ViewDefinition(String name, UserPrincipal liveDataUser, ResultModelDefinition resultModelDefinition) {
    this(name, null, liveDataUser, resultModelDefinition);
  }
  
  /**
   * Constructs an instance
   * 
   * @param name  the name of the view definition
   * @param portfolioId  the unique identifier of the reference portfolio for this view definition, or
   *                     <code>null</code> if no reference portfolio is required
   * @param liveDataUser  the user who owns the view definition
   */
  public ViewDefinition(String name, UniqueIdentifier portfolioId,  UserPrincipal liveDataUser) {
    this(name, portfolioId, liveDataUser, new ResultModelDefinition());
  }

  /**
   * Constructs an instance
   * 
   * @param name  the name of the view definition
   * @param portfolioId  the unique identifier of the reference portfolio for this view definition, or
   *                     <code>null</code> if no reference portfolio is required
   * @param liveDataUser  the user who owns the view definition
   * @param resultModelDefinition  configuration of the results from the view
   */
  public ViewDefinition(String name, UniqueIdentifier portfolioId, UserPrincipal liveDataUser, ResultModelDefinition resultModelDefinition) {
    ArgumentChecker.notNull(name, "View name");
    ArgumentChecker.notNull(liveDataUser, "User name");
    ArgumentChecker.notNull(resultModelDefinition, "Result model definition");
    
    _name = name;
    _portfolioId = portfolioId;
    _liveDataUser = liveDataUser;
    _resultModelDefinition = resultModelDefinition;
  }
  
  //--------------------------------------------------------------------------
  /**
   * Gets a set containing every portfolio output that is required, across all calculation configurations, regardless
   * of the security type(s) on which the output is required. These are outputs produced at the position and aggregate
   * position level, with respect to the reference portfolio. 
   * 
   * @return  a set of every required portfolio output across all calculation configurations, not null
   */
  public Set<String> getAllPortfolioRequirementNames() {
    Set<String> requirements = new TreeSet<String>();
    for (ViewCalculationConfiguration calcConfig : _calculationConfigurationsByName.values()) {
      requirements.addAll(calcConfig.getAllPortfolioRequirements());
    }
    return requirements;
  }

  public String getName() {
    return _name;
  }
  
  /**
   * Gets the unique identifier of the reference portfolio for this view. This is the portfolio on which position-level
   * calculations will be performed.
   * 
   * @return  the unique identifier of the reference portfolio, possibly null.
   */
  public UniqueIdentifier getPortfolioId() {
    return _portfolioId;
  }
  
  /**
   * @return The LiveData user should be used to create 
   * LiveData subscriptions. It is thus a kind of 'super-user'
   * and ensures that the View can be materialized even without
   * any end user trying to use it.
   * <p>
   * Authenticating the end users of the View (of which there can be many) 
   * is a separate matter entirely and has nothing to do with this user.  
   */
  public UserPrincipal getLiveDataUser() {
    return _liveDataUser;
  }
  
  public Collection<ViewCalculationConfiguration> getAllCalculationConfigurations() {
    return new ArrayList<ViewCalculationConfiguration>(_calculationConfigurationsByName.values());
  }
  
  public Set<String> getAllCalculationConfigurationNames() {
    return Collections.unmodifiableSet(_calculationConfigurationsByName.keySet());
  }
  
  public Map<String, ViewCalculationConfiguration> getAllCalculationConfigurationsByName() {
    return Collections.unmodifiableMap(_calculationConfigurationsByName);
  }
  
  public ViewCalculationConfiguration getCalculationConfiguration(String configurationName) {
    return _calculationConfigurationsByName.get(configurationName);
  }
  
  public void addViewCalculationConfiguration(ViewCalculationConfiguration calcConfig) {
    ArgumentChecker.notNull(calcConfig, "calculation configuration");
    ArgumentChecker.notNull(calcConfig.getName(), "Configuration name");
    _calculationConfigurationsByName.put(calcConfig.getName(), calcConfig);
  }
  
  public void addPortfolioRequirement(String calculationConfigurationName, String securityType, String requirementName) {
    ViewCalculationConfiguration calcConfig = _calculationConfigurationsByName.get(calculationConfigurationName);
    if (calcConfig == null) {
      calcConfig = new ViewCalculationConfiguration(this, calculationConfigurationName);
      _calculationConfigurationsByName.put(calculationConfigurationName, calcConfig);
    }
    calcConfig.addPortfolioRequirement(securityType, requirementName);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the minimum period, in milliseconds, which must have elapsed since the start of the last delta calculation
   * when live computations are running. Delta calculations involve only those nodes in the dependency graph whose
   * inputs have changed since the previous calculation.
   * 
   * @return the minimum period between the start of two delta calculations, in milliseconds, or <code>null</code> to
   *         indicate that no minimum period is required to elapse.
   */
  public Long getMinDeltaCalculationPeriod() {
    return _minDeltaCalculationPeriod;
  }

  /**
   * Sets the minimum period, in milliseconds, which must have elapsed since the start of the last delta calculation
   * when live computations are running. Delta calculations involve only those nodes in the dependency graph whose
   * inputs have changed since the previous calculation.
   * 
   * @param minDeltaCalculationPeriod  the minimum period between the start of two delta calculations, in milliseconds,
   *                                   or <code>null</code> to indicate that no minimum period is required to elapse.
   */
  public void setMinDeltaCalculationPeriod(Long minDeltaCalculationPeriod) {
    _minDeltaCalculationPeriod = minDeltaCalculationPeriod;
  }

  /**
   * Gets the maximum period, in milliseconds, which can elapse since the start of the last full or delta calculation
   * before a delta recalculation is forced when live computations are running. In between the minimum and maximum
   * period, any relevant live data changes will immediately trigger a recalculation. The maximum calculation period is
   * therefore a fall-back which can be used to ensure that the view has always been calculated recently, even when no
   * live data changes have occurred. 
   * 
   * @return the maximum period allowed since the start of the last full or delta calculation, in milliseconds, or
   *         <code>null</code> if no maximum period is required.
   */
  public Long getMaxDeltaCalculationPeriod() {
    return _maxDeltaCalculationPeriod;
  }

  /**
   * Sets the maximum period, in milliseconds, which can elapse since the start of the last full or delta calculation
   * before a delta recalculation is forced when live computations are running. In between the minimum and maximum
   * period, any relevant live data changes will immediately trigger a recalculation. The maximum calculation period is
   * therefore a fall-back which can be used to ensure that the view has always been calculated recently, even when no
   * live data changes have occurred. 
   * 
   * @param maxDeltaCalculationPeriod  the maximum period allowed since the start of the last full or delta
   *                                   calculation, in milliseconds, or <code>null</code> if no maximum period is
   *                                   required.
   */
  public void setMaxDeltaCalculationPeriod(Long maxDeltaCalculationPeriod) {
    _maxDeltaCalculationPeriod = maxDeltaCalculationPeriod;
  }

  /**
   * Gets the minimum period, in milliseconds, which must have elapsed since the start of the last full calculation
   * when live computations are running. Full calculations involve recalculating every node in the dependency graph,
   * regardless of whether their inputs have changed.
   * 
   * @return the minimum period between the start of two full calculations, in milliseconds, or <code>null</code> to
   *         indicate that no minimum period is required to elapse.
   */
  public Long getMinFullCalculationPeriod() {
    return _minFullCalculationPeriod;
  }

  /**
   * Sets the minimum period, in milliseconds, which must have elapsed since the start of the last full calculation
   * when live computations are running. Full calculations involve recalculating every node in the dependency graph,
   * regardless of whether their inputs have changed.
   * 
   * @param minFullCalculationPeriod  the minimum period between the start of two full calculations, in milliseconds,
   *                                  or <code>null</code> to indicate that no minimum period is required to elapse.
   */
  public void setMinFullCalculationPeriod(Long minFullCalculationPeriod) {
    _minFullCalculationPeriod = minFullCalculationPeriod;
  }
  
  /**
   * Gets the maximum period, in milliseconds, which can elapse since the start of the last full calculation before a
   * full recalculation is forced when live computations are running. In between the minimum and maximum period, any
   * relevant live data changes will immediately trigger a recalculation. The maximum calculation period is therefore a
   * fall-back which can be used to ensure that the view has always been calculated recently, even when no live data
   * changes have occurred. 
   * 
   * @return the maximum period allowed since the start of the last full calculation, in milliseconds, or
   *         <code>null</code> if no maximum period is required.
   */
  public Long getMaxFullCalculationPeriod() {
    return _maxFullCalculationPeriod;
  }

  /**
   * Sets the maximum period, in milliseconds, which can elapse since the start of the last full calculation before a
   * full recalculation is forced when live computations are running. In between the minimum and maximum period, any
   * relevant live data changes will immediately trigger a recalculation. The maximum calculation period is therefore a
   * fall-back which can be used to ensure that the view has always been calculated recently, even when no live data
   * changes have occurred. 
   * 
   * @param maxFullCalculationPeriod  the maximum period allowed since the start of the last full calculation, in
   *                                  milliseconds, or <code>null</code> if no maximum period is required.
   */
  public void setMaxFullCalculationPeriod(Long maxFullCalculationPeriod) {
    _maxFullCalculationPeriod = maxFullCalculationPeriod;
  }

  //-------------------------------------------------------------------------
  public ResultModelDefinition getResultModelDefinition() {
    return _resultModelDefinition;
  }
  
  public boolean isDumpComputationCacheToDisk() {
    return _dumpComputationCacheToDisk;
  }

  public void setDumpComputationCacheToDisk(boolean dumpComputationCacheToDisk) {
    _dumpComputationCacheToDisk = dumpComputationCacheToDisk;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ObjectUtils.hashCode(getName());
    result = prime * result + ObjectUtils.hashCode(getPortfolioId());
    result = prime * result + ObjectUtils.hashCode(getLiveDataUser());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    
    if (!(obj instanceof ViewDefinition)) {
      return false;
    }
    
    ViewDefinition other = (ViewDefinition) obj;
    boolean basicPropertiesEqual = ObjectUtils.equals(getName(), other.getName()) 
      && ObjectUtils.equals(getPortfolioId(), other.getPortfolioId())
      && ObjectUtils.equals(getResultModelDefinition(), other.getResultModelDefinition())
      && ObjectUtils.equals(getLiveDataUser(), other.getLiveDataUser())
      && ObjectUtils.equals(_minDeltaCalculationPeriod, other._minDeltaCalculationPeriod)
      && ObjectUtils.equals(_maxDeltaCalculationPeriod, other._maxDeltaCalculationPeriod)
      && ObjectUtils.equals(_minFullCalculationPeriod, other._minFullCalculationPeriod)
      && ObjectUtils.equals(_maxFullCalculationPeriod, other._maxFullCalculationPeriod)
      && ObjectUtils.equals(_dumpComputationCacheToDisk, other._dumpComputationCacheToDisk)
      && ObjectUtils.equals(getAllCalculationConfigurationNames(), other.getAllCalculationConfigurationNames());
    if (!basicPropertiesEqual) {
      return false;
    }
    
    for (ViewCalculationConfiguration localCalcConfig : _calculationConfigurationsByName.values()) {
      ViewCalculationConfiguration otherCalcConfig = other.getCalculationConfiguration(localCalcConfig.getName());
      if (!localCalcConfig.equals(otherCalcConfig)) {
        return false;
      }
    }
    
    return true;
  }

}