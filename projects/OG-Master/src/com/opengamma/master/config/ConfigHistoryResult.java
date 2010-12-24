/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.master.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.beans.BeanDefinition;
import org.joda.beans.MetaProperty;

import com.opengamma.master.AbstractHistoryResult;

/**
 * Result providing the history of a piece of configuration.
 * <p>
 * The returned documents may be a mixture of versions and corrections.
 * The document instant fields are used to identify which are which.
 * See {@link ConfigHistoryRequest} for more details.
 * 
 * @param <T>  the document type
 */
@BeanDefinition
public class ConfigHistoryResult<T> extends AbstractHistoryResult<ConfigDocument<T>> {

  /**
   * Creates an instance.
   */
  public ConfigHistoryResult() {
  }

  /**
   * Creates an instance.
   * @param coll  the collection of documents to add, not null
   */
  public ConfigHistoryResult(Collection<ConfigDocument<T>> coll) {
    super(coll);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the returned configuration values from within the documents.
   * 
   * @return the configuration values, not null
   */
  public List<T> getValues() {
    List<T> result = new ArrayList<T>();
    if (getDocuments() != null) {
      for (ConfigDocument<T> doc : getDocuments()) {
        result.add(doc.getValue());
      }
    }
    return result;
  }

  /**
   * Gets the first configuration document value, or null if no documents.
   * 
   * @return the first configuration value, null if none
   */
  public T getFirstValue() {
    return getDocuments().size() > 0 ? getDocuments().get(0).getValue() : null;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ConfigHistoryResult}.
   * @param <R>  the bean's generic type
   * @return the meta-bean, not null
   */
  @SuppressWarnings("unchecked")
  public static <R> ConfigHistoryResult.Meta<R> meta() {
    return ConfigHistoryResult.Meta.INSTANCE;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ConfigHistoryResult.Meta<T> metaBean() {
    return ConfigHistoryResult.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName) {
    switch (propertyName.hashCode()) {
    }
    return super.propertyGet(propertyName);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue) {
    switch (propertyName.hashCode()) {
    }
    super.propertySet(propertyName, newValue);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ConfigHistoryResult}.
   */
  public static class Meta<T> extends AbstractHistoryResult.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    @SuppressWarnings("rawtypes")
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map;

    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Meta() {
      LinkedHashMap temp = new LinkedHashMap(super.metaPropertyMap());
      _map = Collections.unmodifiableMap(temp);
    }

    @Override
    public ConfigHistoryResult<T> createBean() {
      return new ConfigHistoryResult<T>();
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    @Override
    public Class<? extends ConfigHistoryResult<T>> beanType() {
      return (Class) ConfigHistoryResult.class;
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