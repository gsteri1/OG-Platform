/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.historicaltimeseries;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.master.AbstractMetaDataRequest;
import com.opengamma.util.PublicSPI;

/**
 * Request for meta-data about the time-series master.
 * <p>
 * This will return meta-data valid for the whole master.
 */
@PublicSPI
@BeanDefinition
public class HistoricalTimeSeriesInfoMetaDataRequest extends AbstractMetaDataRequest {

  /**
   * Whether to fetch the data fields meta-data, true by default.
   */
  @PropertyDefinition
  private boolean _dataFields = true;
  /**
   * Whether to fetch the data sources meta-data, true by default.
   */
  @PropertyDefinition
  private boolean _dataSources = true;
  /**
   * Whether to fetch the data providers meta-data, true by default.
   */
  @PropertyDefinition
  private boolean _dataProviders = true;
  /**
   * Whether to fetch the observation times meta-data, true by default.
   */
  @PropertyDefinition
  private boolean _observationTimes = true;

  /**
   * Creates an instance.
   */
  public HistoricalTimeSeriesInfoMetaDataRequest() {
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code HistoricalTimeSeriesInfoMetaDataRequest}.
   * @return the meta-bean, not null
   */
  public static HistoricalTimeSeriesInfoMetaDataRequest.Meta meta() {
    return HistoricalTimeSeriesInfoMetaDataRequest.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(HistoricalTimeSeriesInfoMetaDataRequest.Meta.INSTANCE);
  }

  @Override
  public HistoricalTimeSeriesInfoMetaDataRequest.Meta metaBean() {
    return HistoricalTimeSeriesInfoMetaDataRequest.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 894268163:  // dataFields
        return isDataFields();
      case 791883950:  // dataSources
        return isDataSources();
      case 1942087704:  // dataProviders
        return isDataProviders();
      case -576554374:  // observationTimes
        return isObservationTimes();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 894268163:  // dataFields
        setDataFields((Boolean) newValue);
        return;
      case 791883950:  // dataSources
        setDataSources((Boolean) newValue);
        return;
      case 1942087704:  // dataProviders
        setDataProviders((Boolean) newValue);
        return;
      case -576554374:  // observationTimes
        setObservationTimes((Boolean) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      HistoricalTimeSeriesInfoMetaDataRequest other = (HistoricalTimeSeriesInfoMetaDataRequest) obj;
      return JodaBeanUtils.equal(isDataFields(), other.isDataFields()) &&
          JodaBeanUtils.equal(isDataSources(), other.isDataSources()) &&
          JodaBeanUtils.equal(isDataProviders(), other.isDataProviders()) &&
          JodaBeanUtils.equal(isObservationTimes(), other.isObservationTimes()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(isDataFields());
    hash += hash * 31 + JodaBeanUtils.hashCode(isDataSources());
    hash += hash * 31 + JodaBeanUtils.hashCode(isDataProviders());
    hash += hash * 31 + JodaBeanUtils.hashCode(isObservationTimes());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets whether to fetch the data fields meta-data, true by default.
   * @return the value of the property
   */
  public boolean isDataFields() {
    return _dataFields;
  }

  /**
   * Sets whether to fetch the data fields meta-data, true by default.
   * @param dataFields  the new value of the property
   */
  public void setDataFields(boolean dataFields) {
    this._dataFields = dataFields;
  }

  /**
   * Gets the the {@code dataFields} property.
   * @return the property, not null
   */
  public final Property<Boolean> dataFields() {
    return metaBean().dataFields().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets whether to fetch the data sources meta-data, true by default.
   * @return the value of the property
   */
  public boolean isDataSources() {
    return _dataSources;
  }

  /**
   * Sets whether to fetch the data sources meta-data, true by default.
   * @param dataSources  the new value of the property
   */
  public void setDataSources(boolean dataSources) {
    this._dataSources = dataSources;
  }

  /**
   * Gets the the {@code dataSources} property.
   * @return the property, not null
   */
  public final Property<Boolean> dataSources() {
    return metaBean().dataSources().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets whether to fetch the data providers meta-data, true by default.
   * @return the value of the property
   */
  public boolean isDataProviders() {
    return _dataProviders;
  }

  /**
   * Sets whether to fetch the data providers meta-data, true by default.
   * @param dataProviders  the new value of the property
   */
  public void setDataProviders(boolean dataProviders) {
    this._dataProviders = dataProviders;
  }

  /**
   * Gets the the {@code dataProviders} property.
   * @return the property, not null
   */
  public final Property<Boolean> dataProviders() {
    return metaBean().dataProviders().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets whether to fetch the observation times meta-data, true by default.
   * @return the value of the property
   */
  public boolean isObservationTimes() {
    return _observationTimes;
  }

  /**
   * Sets whether to fetch the observation times meta-data, true by default.
   * @param observationTimes  the new value of the property
   */
  public void setObservationTimes(boolean observationTimes) {
    this._observationTimes = observationTimes;
  }

  /**
   * Gets the the {@code observationTimes} property.
   * @return the property, not null
   */
  public final Property<Boolean> observationTimes() {
    return metaBean().observationTimes().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code HistoricalTimeSeriesInfoMetaDataRequest}.
   */
  public static class Meta extends AbstractMetaDataRequest.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code dataFields} property.
     */
    private final MetaProperty<Boolean> _dataFields = DirectMetaProperty.ofReadWrite(
        this, "dataFields", HistoricalTimeSeriesInfoMetaDataRequest.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code dataSources} property.
     */
    private final MetaProperty<Boolean> _dataSources = DirectMetaProperty.ofReadWrite(
        this, "dataSources", HistoricalTimeSeriesInfoMetaDataRequest.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code dataProviders} property.
     */
    private final MetaProperty<Boolean> _dataProviders = DirectMetaProperty.ofReadWrite(
        this, "dataProviders", HistoricalTimeSeriesInfoMetaDataRequest.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code observationTimes} property.
     */
    private final MetaProperty<Boolean> _observationTimes = DirectMetaProperty.ofReadWrite(
        this, "observationTimes", HistoricalTimeSeriesInfoMetaDataRequest.class, Boolean.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "dataFields",
        "dataSources",
        "dataProviders",
        "observationTimes");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 894268163:  // dataFields
          return _dataFields;
        case 791883950:  // dataSources
          return _dataSources;
        case 1942087704:  // dataProviders
          return _dataProviders;
        case -576554374:  // observationTimes
          return _observationTimes;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends HistoricalTimeSeriesInfoMetaDataRequest> builder() {
      return new DirectBeanBuilder<HistoricalTimeSeriesInfoMetaDataRequest>(new HistoricalTimeSeriesInfoMetaDataRequest());
    }

    @Override
    public Class<? extends HistoricalTimeSeriesInfoMetaDataRequest> beanType() {
      return HistoricalTimeSeriesInfoMetaDataRequest.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code dataFields} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> dataFields() {
      return _dataFields;
    }

    /**
     * The meta-property for the {@code dataSources} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> dataSources() {
      return _dataSources;
    }

    /**
     * The meta-property for the {@code dataProviders} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> dataProviders() {
      return _dataProviders;
    }

    /**
     * The meta-property for the {@code observationTimes} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> observationTimes() {
      return _observationTimes;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
