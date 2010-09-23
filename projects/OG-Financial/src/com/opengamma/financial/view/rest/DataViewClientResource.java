/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.view.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.opengamma.engine.view.client.ViewClient;
import com.opengamma.transport.jms.JmsByteArrayMessageSenderService;
import com.opengamma.util.fudge.OpenGammaFudgeContext;

/**
 * RESTful resource for a view client.
 */
@Path("/data/viewclients/clients/{clientId}")
public class DataViewClientResource {

  //CSOFF: just constants
  public static final String PATH_UNIQUE_IDENTIFIER = "uniqueIdentifier";
  public static final String PATH_USER = "user";
  public static final String PATH_RESULT_AVAILABLE = "resultAvailable";
  public static final String PATH_LATEST_RESULT = "latestResult";
  public static final String PATH_STATE = "state";
  public static final String PATH_START = "start";
  public static final String PATH_PAUSE = "pause";
  public static final String PATH_STOP = "stop";
  public static final String PATH_SHUTDOWN = "shutdown";
  public static final String PATH_UPDATE_PERIOD = "updatePeriod";
  
  public static final String PATH_START_JMS_RESULT_STREAM = "startJmsResultStream";
  public static final String PATH_STOP_JMS_RESULT_STREAM = "endJmsResultStream";
  public static final String PATH_START_JMS_DELTA_STREAM = "startJmsDeltaStream";
  public static final String PATH_STOP_JMS_DELTA_STREAM = "endJmsDeltaStream";
  //CSON: just constants
  
  private final ViewClient _viewClient;
  private final JmsResultPublisher _resultPublisher;
  
  public DataViewClientResource(ViewClient viewClient, JmsByteArrayMessageSenderService messageSenderService, String topicPrefix) {
    _viewClient = viewClient;
    _resultPublisher = new JmsResultPublisher(viewClient, OpenGammaFudgeContext.getInstance(), topicPrefix,
        messageSenderService);
  }
  
  private ViewClient getViewClient() {
    return _viewClient;
  }
  
  //-------------------------------------------------------------------------
  @GET
  @Path(PATH_UNIQUE_IDENTIFIER)
  public Response getUniqueIdentifier() {
    return Response.ok(getViewClient().getUniqueIdentifier()).build();
  }
  
  //-------------------------------------------------------------------------
  @GET
  @Path(PATH_USER)
  public Response getUser() {
    return Response.ok(getViewClient().getUser()).build();
  }
  
  @GET
  @Path(PATH_RESULT_AVAILABLE)
  public Response isResultAvailable() {
    return Response.ok(getViewClient().isResultAvailable()).build();
  }
  
  @GET
  @Path(PATH_LATEST_RESULT)
  public Response getLastResult() {
    return Response.ok(getViewClient().getLatestResult()).build();
  }
  
  @GET
  @Path(PATH_STATE)
  public Response getState() {
    return Response.ok(getViewClient().getState()).build();
  }
  
  @POST
  @Path(PATH_START)
  public Response startLive() {
    getViewClient().startLive();
    return Response.ok().build();
  }
  
  @POST
  @Path(PATH_PAUSE)
  public Response pauseLive() {
    getViewClient().pauseLive();
    return Response.ok().build();
  }
  
  @POST
  @Path(PATH_STOP)
  public Response stopLive() {
    getViewClient().stopLive();
    return Response.ok().build();
  }
  
  @POST
  @Path(PATH_SHUTDOWN)
  public Response shutdown() {
    getViewClient().shutdown();
    return Response.ok().build();
  }
  
  @PUT
  @Path(PATH_UPDATE_PERIOD)
  public Response setLiveUpdatePeriod(long periodMillis) {
    getViewClient().setLiveUpdatePeriod(periodMillis);
    return Response.ok().build();
  }
  
  @POST
  @Path(PATH_START_JMS_RESULT_STREAM)
  public Response startResultStream() {
    return Response.ok(_resultPublisher.startPublishingResults()).build();
  }
  
  @POST
  @Path(PATH_STOP_JMS_RESULT_STREAM)
  public Response stopResultStream() {
    _resultPublisher.stopPublishingResults();
    return Response.ok().build();
  }
  
  @POST
  @Path(PATH_START_JMS_DELTA_STREAM)
  public Response startDeltaStream() {
    return Response.ok(_resultPublisher.startPublishingDeltas()).build();
  }
  
  @POST
  @Path(PATH_STOP_JMS_DELTA_STREAM)
  public Response endDeltaStream() {
    _resultPublisher.stopPublishingDeltas();
    return Response.ok().build();
  }
  
}