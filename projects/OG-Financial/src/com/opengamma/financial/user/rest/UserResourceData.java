/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.user.rest;

import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.financial.user.ClientTracker;
import com.opengamma.financial.user.UserDataTracker;

/**
 * Data class for user resource management.
 */
@BeanDefinition
public class UserResourceData extends DirectBean {
  /**
   * The JSR-311 URI information.
   */
  @PropertyDefinition
  private UriInfo _uriInfo;
  /**
   * The clients tracker
   */
  @PropertyDefinition
  private ClientTracker _clientTracker;
  /**
   * The users data tracker
   */
  @PropertyDefinition
  private UserDataTracker _userDataTracker;
  /**
   * The users resource context
   */
  @PropertyDefinition
  private UsersResourceContext _context;

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code UserResourceData}.
   * @return the meta-bean, not null
   */
  public static UserResourceData.Meta meta() {
    return UserResourceData.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(UserResourceData.Meta.INSTANCE);
  }

  @Override
  public UserResourceData.Meta metaBean() {
    return UserResourceData.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -173275078:  // uriInfo
        return getUriInfo();
      case -647122707:  // clientTracker
        return getClientTracker();
      case -633902237:  // userDataTracker
        return getUserDataTracker();
      case 951530927:  // context
        return getContext();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -173275078:  // uriInfo
        setUriInfo((UriInfo) newValue);
        return;
      case -647122707:  // clientTracker
        setClientTracker((ClientTracker) newValue);
        return;
      case -633902237:  // userDataTracker
        setUserDataTracker((UserDataTracker) newValue);
        return;
      case 951530927:  // context
        setContext((UsersResourceContext) newValue);
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
      UserResourceData other = (UserResourceData) obj;
      return JodaBeanUtils.equal(getUriInfo(), other.getUriInfo()) &&
          JodaBeanUtils.equal(getClientTracker(), other.getClientTracker()) &&
          JodaBeanUtils.equal(getUserDataTracker(), other.getUserDataTracker()) &&
          JodaBeanUtils.equal(getContext(), other.getContext());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getUriInfo());
    hash += hash * 31 + JodaBeanUtils.hashCode(getClientTracker());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUserDataTracker());
    hash += hash * 31 + JodaBeanUtils.hashCode(getContext());
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the JSR-311 URI information.
   * @return the value of the property
   */
  public UriInfo getUriInfo() {
    return _uriInfo;
  }

  /**
   * Sets the JSR-311 URI information.
   * @param uriInfo  the new value of the property
   */
  public void setUriInfo(UriInfo uriInfo) {
    this._uriInfo = uriInfo;
  }

  /**
   * Gets the the {@code uriInfo} property.
   * @return the property, not null
   */
  public final Property<UriInfo> uriInfo() {
    return metaBean().uriInfo().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the clients tracker
   * @return the value of the property
   */
  public ClientTracker getClientTracker() {
    return _clientTracker;
  }

  /**
   * Sets the clients tracker
   * @param clientTracker  the new value of the property
   */
  public void setClientTracker(ClientTracker clientTracker) {
    this._clientTracker = clientTracker;
  }

  /**
   * Gets the the {@code clientTracker} property.
   * @return the property, not null
   */
  public final Property<ClientTracker> clientTracker() {
    return metaBean().clientTracker().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the users data tracker
   * @return the value of the property
   */
  public UserDataTracker getUserDataTracker() {
    return _userDataTracker;
  }

  /**
   * Sets the users data tracker
   * @param userDataTracker  the new value of the property
   */
  public void setUserDataTracker(UserDataTracker userDataTracker) {
    this._userDataTracker = userDataTracker;
  }

  /**
   * Gets the the {@code userDataTracker} property.
   * @return the property, not null
   */
  public final Property<UserDataTracker> userDataTracker() {
    return metaBean().userDataTracker().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the users resource context
   * @return the value of the property
   */
  public UsersResourceContext getContext() {
    return _context;
  }

  /**
   * Sets the users resource context
   * @param context  the new value of the property
   */
  public void setContext(UsersResourceContext context) {
    this._context = context;
  }

  /**
   * Gets the the {@code context} property.
   * @return the property, not null
   */
  public final Property<UsersResourceContext> context() {
    return metaBean().context().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code UserResourceData}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code uriInfo} property.
     */
    private final MetaProperty<UriInfo> _uriInfo = DirectMetaProperty.ofReadWrite(
        this, "uriInfo", UserResourceData.class, UriInfo.class);
    /**
     * The meta-property for the {@code clientTracker} property.
     */
    private final MetaProperty<ClientTracker> _clientTracker = DirectMetaProperty.ofReadWrite(
        this, "clientTracker", UserResourceData.class, ClientTracker.class);
    /**
     * The meta-property for the {@code userDataTracker} property.
     */
    private final MetaProperty<UserDataTracker> _userDataTracker = DirectMetaProperty.ofReadWrite(
        this, "userDataTracker", UserResourceData.class, UserDataTracker.class);
    /**
     * The meta-property for the {@code context} property.
     */
    private final MetaProperty<UsersResourceContext> _context = DirectMetaProperty.ofReadWrite(
        this, "context", UserResourceData.class, UsersResourceContext.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map = new DirectMetaPropertyMap(
        this, null,
        "uriInfo",
        "clientTracker",
        "userDataTracker",
        "context");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -173275078:  // uriInfo
          return _uriInfo;
        case -647122707:  // clientTracker
          return _clientTracker;
        case -633902237:  // userDataTracker
          return _userDataTracker;
        case 951530927:  // context
          return _context;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends UserResourceData> builder() {
      return new DirectBeanBuilder<UserResourceData>(new UserResourceData());
    }

    @Override
    public Class<? extends UserResourceData> beanType() {
      return UserResourceData.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code uriInfo} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UriInfo> uriInfo() {
      return _uriInfo;
    }

    /**
     * The meta-property for the {@code clientTracker} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ClientTracker> clientTracker() {
      return _clientTracker;
    }

    /**
     * The meta-property for the {@code userDataTracker} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UserDataTracker> userDataTracker() {
      return _userDataTracker;
    }

    /**
     * The meta-property for the {@code context} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UsersResourceContext> context() {
      return _context;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
