/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.core.holiday.impl;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.core.AbstractLink;
import com.opengamma.core.holiday.Holiday;
import com.opengamma.core.holiday.HolidayLink;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.ObjectId;
import com.opengamma.id.UniqueId;
import com.opengamma.util.ArgumentChecker;

/**
 * A flexible link between an object and a holiday.
 * <p>
 * A holiday link represents a connection from an entity to a holiday.
 * The connection can be held by an {@code ObjectId} or an {@code ExternalIdBundle}.
 * <p>
 * This class is mutable and not thread-safe.
 * It is intended to be used in the engine via the read-only {@code HolidayLink} interface.
 */
@BeanDefinition
public class SimpleHolidayLink extends AbstractLink<Holiday>
    implements HolidayLink {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * Obtains an instance from a holiday, storing the object identifier.
   * 
   * @param holiday  the holiday to store, not null
   * @return the link with one identifier set, not null
   */
  public static SimpleHolidayLink of(Holiday holiday) {
    ArgumentChecker.notNull(holiday, "holiday");
    SimpleHolidayLink link = new SimpleHolidayLink();
    if (holiday.getUniqueId() != null) {
      link.setObjectId(holiday.getUniqueId().getObjectId());
    }
    return link;
  }

  //-------------------------------------------------------------------------
  /**
   * Creates an new instance.
   */
  public SimpleHolidayLink() {
    super();
  }

  /**
   * Creates a link from an object identifier.
   * 
   * @param objectId  the object identifier, not null
   */
  public SimpleHolidayLink(final ObjectId objectId) {
    super(objectId);
  }

  /**
   * Creates a link from a unique identifier, only storing the object identifier.
   * 
   * @param uniqueId  the unique identifier, not null
   */
  public SimpleHolidayLink(final UniqueId uniqueId) {
    super(uniqueId);
  }

  /**
   * Creates a link from an external identifier.
   * 
   * @param externalId  the external identifier, not null
   */
  public SimpleHolidayLink(final ExternalId externalId) {
    super(externalId);
  }

  /**
   * Creates a link from an external identifier bundle.
   * 
   * @param bundle  the identifier bundle, not null
   */
  public SimpleHolidayLink(final ExternalIdBundle bundle) {
    super(bundle);
  }

  /**
   * Clones the specified link, sharing the target holiday.
   * 
   * @param linkToClone  the link to clone, not null
   */
  public SimpleHolidayLink(HolidayLink linkToClone) {
    super();
    setObjectId(linkToClone.getObjectId());
    setExternalId(linkToClone.getExternalId());
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SimpleHolidayLink}.
   * @return the meta-bean, not null
   */
  @SuppressWarnings("unchecked")
  public static SimpleHolidayLink.Meta meta() {
    return SimpleHolidayLink.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(SimpleHolidayLink.Meta.INSTANCE);
  }

  @Override
  public SimpleHolidayLink.Meta metaBean() {
    return SimpleHolidayLink.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      return super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SimpleHolidayLink}.
   */
  public static class Meta extends AbstractLink.Meta<Holiday> {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap());

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    public BeanBuilder<? extends SimpleHolidayLink> builder() {
      return new DirectBeanBuilder<SimpleHolidayLink>(new SimpleHolidayLink());
    }

    @Override
    public Class<? extends SimpleHolidayLink> beanType() {
      return SimpleHolidayLink.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
