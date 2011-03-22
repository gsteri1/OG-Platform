/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.normalization;

import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.Test;
import java.util.HashSet;
import java.util.Set;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.MutableFudgeFieldContainer;
import com.opengamma.livedata.server.FieldHistoryStore;

/**
 * 
 */
public class FieldFilterTest {
  
  @Test
  public void normalCase() {
    Set<String> fieldsToAccept = new HashSet<String>();
    fieldsToAccept.add("Foo");    
    fieldsToAccept.add("Bar");
    FieldFilter filter = new FieldFilter(fieldsToAccept);
    
    MutableFudgeFieldContainer msg = FudgeContext.GLOBAL_DEFAULT.newMessage();
    msg.add("Foo", "1");
    msg.add("Bar", 2.0);
    msg.add("Baz", 500);
    
    MutableFudgeFieldContainer normalized = filter.apply(msg, new FieldHistoryStore());
    assertEquals("1", normalized.getString("Foo"));
    assertEquals(2.0, normalized.getDouble("Bar"), 0.0001);
    assertNull(normalized.getByName("Baz"));
  }
  
  @Test
  public void extinguishmentWithNonEmptyFieldsToAccept() {
    Set<String> fieldsToAccept = new HashSet<String>();
    fieldsToAccept.add("Foo");    
    fieldsToAccept.add("Bar");
    FieldFilter filter = new FieldFilter(fieldsToAccept);
    
    MutableFudgeFieldContainer msg = FudgeContext.GLOBAL_DEFAULT.newMessage();
    msg.add("Foo2", "1");
    
    MutableFudgeFieldContainer normalized = filter.apply(msg, new FieldHistoryStore());
    assertNull(normalized);
  }
  
  @Test
  public void extinguishmentWithEmptyFieldsToAccept() {
    Set<String> fieldsToAccept = new HashSet<String>();
    FieldFilter filter = new FieldFilter(fieldsToAccept);
    
    MutableFudgeFieldContainer msg = FudgeContext.GLOBAL_DEFAULT.newMessage();
    msg.add("Foo", "1");
    
    MutableFudgeFieldContainer normalized = filter.apply(msg, new FieldHistoryStore());
    assertNull(normalized);
  }
  
}
