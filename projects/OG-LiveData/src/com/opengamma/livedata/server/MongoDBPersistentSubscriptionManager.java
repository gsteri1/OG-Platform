/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeMsg;
import org.fudgemsg.MutableFudgeMsg;
import org.fudgemsg.mapping.FudgeDeserializer;
import org.fudgemsg.mapping.FudgeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.livedata.LiveDataSpecification;
import com.opengamma.util.MongoDBConnectionSettings;

/**
 * Stores persistent subscriptions in MongoDB.  
 */
public class MongoDBPersistentSubscriptionManager extends AbstractPersistentSubscriptionManager {
  
  private static final Logger s_logger = LoggerFactory.getLogger(MongoDBPersistentSubscriptionManager.class);
  
  private static final String MONGO_COLLECTION = "PersistentSubscription";
  
  private final Mongo _mongo;
  private final DB _mongoDB;
  
  public MongoDBPersistentSubscriptionManager(AbstractLiveDataServer server, MongoDBConnectionSettings mongoSettings) {
    super(server);
    
    s_logger.info("Connecting to {}", mongoSettings);
    try {
      _mongo = new Mongo(mongoSettings.getHost(), mongoSettings.getPort());
      _mongoDB = _mongo.getDB(mongoSettings.getDatabase());
    } catch (Exception e) {
      throw new OpenGammaRuntimeException("Unable to connect to MongoDB at " + mongoSettings, e);
    }
  }

  @Override
  protected void readFromStorage() {
    FudgeSerializer serializer = new FudgeSerializer(FudgeContext.GLOBAL_DEFAULT);
    FudgeDeserializer deserializer = new FudgeDeserializer(FudgeContext.GLOBAL_DEFAULT);
    DBCollection dbCollection = _mongoDB.getCollection(MONGO_COLLECTION);
    
    DBCursor cursor = dbCollection.find();
    while (cursor.hasNext()) {
      DBObject mainObject = cursor.next();
      DBObject fieldData = (DBObject) mainObject.get("fieldData");
      MutableFudgeMsg msg = serializer.objectToFudgeMsg(fieldData);
      LiveDataSpecification spec = LiveDataSpecification.fromFudgeMsg(deserializer, msg);
      addPersistentSubscription(new PersistentSubscription(spec));
    }
    
  }

  @Override
  public void saveToStorage(Set<PersistentSubscription> newState) {
    clean();
    
    FudgeSerializer serializer = new FudgeSerializer(FudgeContext.GLOBAL_DEFAULT);
    FudgeDeserializer deserializer = new FudgeDeserializer(FudgeContext.GLOBAL_DEFAULT);
    DBCollection dbCollection = _mongoDB.getCollection(MONGO_COLLECTION);
    
    List<DBObject> objects = new ArrayList<DBObject>();
    for (PersistentSubscription sub : newState) {
      FudgeMsg msg = sub.getFullyQualifiedSpec().toFudgeMsg(serializer);
      DBObject fieldData = deserializer.fudgeMsgToObject(DBObject.class, msg);
      BasicDBObject mainObject = new BasicDBObject();
      mainObject.append("fieldData", fieldData);
      objects.add(mainObject);
    }
    dbCollection.insert(objects);
  }
  
  void clean() {
    DBCollection dbCollection = _mongoDB.getCollection(MONGO_COLLECTION);
    dbCollection.drop();
  }
  
}
