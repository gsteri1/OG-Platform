/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.region.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

import javax.time.Instant;
import javax.time.calendar.TimeZone;

import org.testng.annotations.Test;

import com.opengamma.DataNotFoundException;
import com.opengamma.core.region.Region;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.ObjectId;
import com.opengamma.id.UniqueId;
import com.opengamma.id.VersionCorrection;
import com.opengamma.master.region.ManageableRegion;
import com.opengamma.master.region.RegionDocument;
import com.opengamma.master.region.RegionMaster;
import com.opengamma.master.region.RegionSearchRequest;
import com.opengamma.master.region.RegionSearchResult;
import com.opengamma.util.PagingRequest;
import com.opengamma.util.i18n.Country;
import com.opengamma.util.money.Currency;

/**
 * Test {@link MasterRegionSource}.
 */
@Test
public class MasterRegionSourceTest {

  private static final ObjectId OID = ObjectId.of("A", "B");
  private static final UniqueId UID = UniqueId.of("A", "B", "V");
  private static final ExternalId ID = ExternalId.of("C", "D");
  private static final ExternalIdBundle BUNDLE = ExternalIdBundle.of(ID);
  private static final Instant NOW = Instant.now();
  private static final VersionCorrection VC = VersionCorrection.of(NOW.minusSeconds(2), NOW.minusSeconds(1));

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_constructor_1arg_nullMaster() throws Exception {
    new MasterRegionSource(null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_constructor_2arg_nullMaster() throws Exception {
    new MasterRegionSource(null, null);
  }

  //-------------------------------------------------------------------------
  public void test_getRegion_UniqueId_noOverride_found() throws Exception {
    RegionMaster mock = mock(RegionMaster.class);
    
    RegionDocument doc = new RegionDocument(example());
    when(mock.get(UID)).thenReturn(doc);
    MasterRegionSource test = new MasterRegionSource(mock);
    Region testResult = test.getRegion(UID);
    verify(mock, times(1)).get(UID);
    
    assertEquals(example(), testResult);
  }

  public void test_getRegion_UniqueId_found() throws Exception {
    RegionMaster mock = mock(RegionMaster.class);
    
    RegionDocument doc = new RegionDocument(example());
    when(mock.get(OID, VC)).thenReturn(doc);
    MasterRegionSource test = new MasterRegionSource(mock, VC);
    Region testResult = test.getRegion(UID);
    verify(mock, times(1)).get(OID, VC);
    
    assertEquals(example(), testResult);
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void test_getRegion_UniqueId_notFound() throws Exception {
    RegionMaster mock = mock(RegionMaster.class);
    
    when(mock.get(OID, VC)).thenThrow(new DataNotFoundException(""));
    MasterRegionSource test = new MasterRegionSource(mock, VC);
    try {
      test.getRegion(UID);
    } finally {
      verify(mock, times(1)).get(OID, VC);
    }
  }

  //-------------------------------------------------------------------------
  public void test_getRegion_ObjectId_found() throws Exception {
    RegionMaster mock = mock(RegionMaster.class);
    
    RegionDocument doc = new RegionDocument(example());
    when(mock.get(OID, VC)).thenReturn(doc);
    MasterRegionSource test = new MasterRegionSource(mock, VC);
    Region testResult = test.getRegion(OID, VC);
    verify(mock, times(1)).get(OID, VC);
    
    assertEquals(example(), testResult);
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void test_getRegion_ObjectId_notFound() throws Exception {
    RegionMaster mock = mock(RegionMaster.class);
    
    when(mock.get(OID, VC)).thenThrow(new DataNotFoundException(""));
    MasterRegionSource test = new MasterRegionSource(mock, VC);
    try {
      test.getRegion(OID, VC);
    } finally {
      verify(mock, times(1)).get(OID, VC);
    }
  }

  //-------------------------------------------------------------------------
  public void test_getRegion_ExternalId_found() throws Exception {
    RegionMaster mock = mock(RegionMaster.class);
    RegionSearchRequest request = new RegionSearchRequest(ID);
    request.setPagingRequest(PagingRequest.ONE);
    request.setVersionCorrection(VC);
    
    RegionSearchResult result = new RegionSearchResult();
    result.getDocuments().add(new RegionDocument(example()));
    
    when(mock.search(request)).thenReturn(result);
    MasterRegionSource test = new MasterRegionSource(mock, VC);
    Region testResult = test.getHighestLevelRegion(ID);
    verify(mock, times(1)).search(request);
    
    assertEquals(example(), testResult);
  }

  public void test_getRegion_ExternalId_notFound() throws Exception {
    RegionMaster mock = mock(RegionMaster.class);
    RegionSearchRequest request = new RegionSearchRequest(ID);
    request.setPagingRequest(PagingRequest.ONE);
    request.setVersionCorrection(VC);
    
    RegionSearchResult result = new RegionSearchResult();
    
    when(mock.search(request)).thenReturn(result);
    MasterRegionSource test = new MasterRegionSource(mock, VC);
    Region testResult = test.getHighestLevelRegion(ID);
    verify(mock, times(1)).search(request);
    
    assertEquals(null, testResult);
  }

  //-------------------------------------------------------------------------
  public void test_getRegion_ExternalIdBundle_found() throws Exception {
    RegionMaster mock = mock(RegionMaster.class);
    RegionSearchRequest request = new RegionSearchRequest(BUNDLE);
    request.setPagingRequest(PagingRequest.ONE);
    request.setVersionCorrection(VC);
    
    RegionSearchResult result = new RegionSearchResult();
    result.getDocuments().add(new RegionDocument(example()));
    
    when(mock.search(request)).thenReturn(result);
    MasterRegionSource test = new MasterRegionSource(mock, VC);
    Region testResult = test.getHighestLevelRegion(BUNDLE);
    verify(mock, times(1)).search(request);
    
    assertEquals(example(), testResult);
  }

  //-------------------------------------------------------------------------
  protected ManageableRegion example() {
    ManageableRegion region = new ManageableRegion();
    region.setUniqueId(UID);
    region.setName("United Kingdom");
    region.setCurrency(Currency.GBP);
    region.setCountry(Country.GB);
    region.setTimeZone(TimeZone.of("Europe/London"));
    return region;
  }

}
