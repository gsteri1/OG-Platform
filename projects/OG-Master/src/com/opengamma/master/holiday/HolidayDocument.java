/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.holiday;

import java.io.Serializable;
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

import com.opengamma.core.holiday.Holiday;
import com.opengamma.id.ExternalId;
import com.opengamma.id.UniqueId;
import com.opengamma.master.AbstractDocument;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.PublicSPI;

/**
 * A document used to pass into and out of the holiday master.
 * <p>
 * The holiday master provides full management of the holiday database.
 * Each element is stored in a document.
 */
@PublicSPI
@BeanDefinition
public class HolidayDocument extends AbstractDocument implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The holiday unique identifier.
   * This field is managed by the master but must be set for updates.
   */
  @PropertyDefinition
  private UniqueId _uniqueId;
  /**
   * The name of the holiday.
   */
  @PropertyDefinition
  private String _name;
  /**
   * The provider external identifier for the data.
   * This optional field can be used to capture the identifier used by the data provider.
   * This can be useful when receiving updates from the same provider.
   */
  @PropertyDefinition
  private ExternalId _providerId;
  /**
   * The holiday.
   */
  @PropertyDefinition
  private ManageableHoliday _holiday;

  /**
   * Creates an instance.
   */
  public HolidayDocument() {
  }

  /**
   * Creates an instance from a holiday.
   * <p>
   * This will call {@link #createName()} to build a suitable name.
   * 
   * @param holiday  the holiday, not null
   */
  public HolidayDocument(final Holiday holiday) {
    ArgumentChecker.notNull(holiday, "holiday");
    setUniqueId(holiday.getUniqueId());
    setHoliday(new ManageableHoliday(holiday));
    createName();
  }

  //-------------------------------------------------------------------------
  /**
   * Creates a name based on the holiday.
   */
  public void createName() {
    ManageableHoliday holiday = getHoliday();
    switch (holiday.getType()) {
      case BANK:
        setName(holiday.getRegionExternalId().getValue());
        break;
      case CURRENCY:
        setName(holiday.getCurrency().getCode());
        break;
      case SETTLEMENT:
      case TRADING:
        setName(holiday.getExchangeExternalId().getValue());
        break;
      default:
        throw new IllegalArgumentException("Unsupported holiday type");
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code HolidayDocument}.
   * @return the meta-bean, not null
   */
  public static HolidayDocument.Meta meta() {
    return HolidayDocument.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(HolidayDocument.Meta.INSTANCE);
  }

  @Override
  public HolidayDocument.Meta metaBean() {
    return HolidayDocument.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -294460212:  // uniqueId
        return getUniqueId();
      case 3373707:  // name
        return getName();
      case 205149932:  // providerId
        return getProviderId();
      case 1091905624:  // holiday
        return getHoliday();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -294460212:  // uniqueId
        setUniqueId((UniqueId) newValue);
        return;
      case 3373707:  // name
        setName((String) newValue);
        return;
      case 205149932:  // providerId
        setProviderId((ExternalId) newValue);
        return;
      case 1091905624:  // holiday
        setHoliday((ManageableHoliday) newValue);
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
      HolidayDocument other = (HolidayDocument) obj;
      return JodaBeanUtils.equal(getUniqueId(), other.getUniqueId()) &&
          JodaBeanUtils.equal(getName(), other.getName()) &&
          JodaBeanUtils.equal(getProviderId(), other.getProviderId()) &&
          JodaBeanUtils.equal(getHoliday(), other.getHoliday()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getUniqueId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getName());
    hash += hash * 31 + JodaBeanUtils.hashCode(getProviderId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getHoliday());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the holiday unique identifier.
   * This field is managed by the master but must be set for updates.
   * @return the value of the property
   */
  public UniqueId getUniqueId() {
    return _uniqueId;
  }

  /**
   * Sets the holiday unique identifier.
   * This field is managed by the master but must be set for updates.
   * @param uniqueId  the new value of the property
   */
  public void setUniqueId(UniqueId uniqueId) {
    this._uniqueId = uniqueId;
  }

  /**
   * Gets the the {@code uniqueId} property.
   * This field is managed by the master but must be set for updates.
   * @return the property, not null
   */
  public final Property<UniqueId> uniqueId() {
    return metaBean().uniqueId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the name of the holiday.
   * @return the value of the property
   */
  public String getName() {
    return _name;
  }

  /**
   * Sets the name of the holiday.
   * @param name  the new value of the property
   */
  public void setName(String name) {
    this._name = name;
  }

  /**
   * Gets the the {@code name} property.
   * @return the property, not null
   */
  public final Property<String> name() {
    return metaBean().name().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the provider external identifier for the data.
   * This optional field can be used to capture the identifier used by the data provider.
   * This can be useful when receiving updates from the same provider.
   * @return the value of the property
   */
  public ExternalId getProviderId() {
    return _providerId;
  }

  /**
   * Sets the provider external identifier for the data.
   * This optional field can be used to capture the identifier used by the data provider.
   * This can be useful when receiving updates from the same provider.
   * @param providerId  the new value of the property
   */
  public void setProviderId(ExternalId providerId) {
    this._providerId = providerId;
  }

  /**
   * Gets the the {@code providerId} property.
   * This optional field can be used to capture the identifier used by the data provider.
   * This can be useful when receiving updates from the same provider.
   * @return the property, not null
   */
  public final Property<ExternalId> providerId() {
    return metaBean().providerId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the holiday.
   * @return the value of the property
   */
  public ManageableHoliday getHoliday() {
    return _holiday;
  }

  /**
   * Sets the holiday.
   * @param holiday  the new value of the property
   */
  public void setHoliday(ManageableHoliday holiday) {
    this._holiday = holiday;
  }

  /**
   * Gets the the {@code holiday} property.
   * @return the property, not null
   */
  public final Property<ManageableHoliday> holiday() {
    return metaBean().holiday().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code HolidayDocument}.
   */
  public static class Meta extends AbstractDocument.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code uniqueId} property.
     */
    private final MetaProperty<UniqueId> _uniqueId = DirectMetaProperty.ofReadWrite(
        this, "uniqueId", HolidayDocument.class, UniqueId.class);
    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<String> _name = DirectMetaProperty.ofReadWrite(
        this, "name", HolidayDocument.class, String.class);
    /**
     * The meta-property for the {@code providerId} property.
     */
    private final MetaProperty<ExternalId> _providerId = DirectMetaProperty.ofReadWrite(
        this, "providerId", HolidayDocument.class, ExternalId.class);
    /**
     * The meta-property for the {@code holiday} property.
     */
    private final MetaProperty<ManageableHoliday> _holiday = DirectMetaProperty.ofReadWrite(
        this, "holiday", HolidayDocument.class, ManageableHoliday.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "uniqueId",
        "name",
        "providerId",
        "holiday");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -294460212:  // uniqueId
          return _uniqueId;
        case 3373707:  // name
          return _name;
        case 205149932:  // providerId
          return _providerId;
        case 1091905624:  // holiday
          return _holiday;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends HolidayDocument> builder() {
      return new DirectBeanBuilder<HolidayDocument>(new HolidayDocument());
    }

    @Override
    public Class<? extends HolidayDocument> beanType() {
      return HolidayDocument.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code uniqueId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueId> uniqueId() {
      return _uniqueId;
    }

    /**
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> name() {
      return _name;
    }

    /**
     * The meta-property for the {@code providerId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> providerId() {
      return _providerId;
    }

    /**
     * The meta-property for the {@code holiday} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ManageableHoliday> holiday() {
      return _holiday;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
