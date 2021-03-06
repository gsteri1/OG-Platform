/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.msg;

import org.testng.annotations.Test;

import com.opengamma.livedata.LiveDataSpecification;

/**
 * This class is primarily here to allow us to eliminate the dummy classes.
 */
public class LiveDataSubscriptionResponseTest {

  @Test
  public void simpleConstruction() {
    new LiveDataSubscriptionResponse(new LiveDataSpecification("Foo"),
        LiveDataSubscriptionResult.SUCCESS,
        null,
        new LiveDataSpecification("Foo"),
        null,
        null);
  }

}
