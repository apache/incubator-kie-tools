/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.enterprise.client.cdi.api;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.extension.InitVotes;
import org.jboss.errai.common.client.protocols.MessageParts;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.CDICommands;
import org.jboss.errai.enterprise.client.cdi.CDIEventTypeLookup;
import org.jboss.errai.enterprise.client.cdi.CDIProtocol;
import org.jboss.errai.enterprise.client.cdi.EventQualifierSerializer;
import org.jboss.errai.enterprise.client.cdi.JsTypeEventObserver;
import org.jboss.errai.enterprise.client.cdi.WindowEventObservers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CDI client interface.
 *
 * @author Heiko Braun <hbraun@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Mike Brock <cbrock@redhat.com>
 */
public class CDI {
  public static final String CDI_SUBJECT_PREFIX = "cdi.event:";

  public static final String CDI_SERVICE_SUBJECT_PREFIX = "cdi.event:";
  private static final String CLIENT_ALREADY_FIRED_RESOURCE = CDI_SERVICE_SUBJECT_PREFIX + "AlreadyFired";

  private static final Set<String> remoteEvents = new HashSet<>();
  private static boolean active = false;

  private static Map<String, List<AbstractCDIEventCallback<?>>> eventObservers = new HashMap<>();
  private static Set<String> localOnlyObserverTypes = new HashSet<>();
  private static Map<String, Collection<String>> lookupTable = Collections.emptyMap();

  private static Logger logger = LoggerFactory.getLogger(CDI.class);

  public static String getSubjectNameByType(final String typeName) {
    return CDI_SUBJECT_PREFIX + typeName;
  }

  /**
   * Should only be called by bootstrapper for testing purposes.
   */
  public void __resetSubsystem() {
    remoteEvents.clear();
    active = false;
    eventObservers.clear();
    localOnlyObserverTypes.clear();
    lookupTable = Collections.emptyMap();
  }

  public void initLookupTable(final CDIEventTypeLookup lookup) {
    lookupTable = lookup.getTypeLookupMap();
  }

  /**
   * Return a list of string representations for the qualifiers.
   *
   * @param qualifiers -
   *
   * @return
   */
  public static Set<String> getQualifiersPart(final Annotation[] qualifiers) {
    Set<String> qualifiersPart = null;
    if (qualifiers != null) {
      for (final Annotation qualifier : qualifiers) {
        if (qualifiersPart == null)
          qualifiersPart = new HashSet<>(qualifiers.length);

        qualifiersPart.add(asString(qualifier));
      }
    }
    return qualifiersPart == null ? Collections.<String>emptySet() : qualifiersPart;

  }

  private static String asString(final Annotation qualifier) {
    return EventQualifierSerializer.get().serialize(qualifier);
  }

  public static void fireEvent(final Object payload, final Annotation... qualifiers) {
    fireEvent(false, payload, qualifiers);
  }


  public static void fireEvent(final boolean local,
                               final Object payload,
                               final Annotation... qualifiers) {

    if (payload == null) return;

    final Map<String, Object> messageMap = new HashMap<>();
    messageMap.put(MessageParts.CommandType.name(), CDICommands.CDIEvent.name());
    messageMap.put(CDIProtocol.BeanType.name(), payload.getClass().getName());
    messageMap.put(CDIProtocol.BeanReference.name(), payload);
    messageMap.put(CDIProtocol.FromClient.name(), "1");

    if (qualifiers != null && qualifiers.length > 0) {
      messageMap.put(CDIProtocol.Qualifiers.name(), getQualifiersPart(qualifiers));
    }

    consumeEventFromMessage(CommandMessage.createWithParts(messageMap));
  }

  public static Subscription subscribeLocal(final String eventType, final AbstractCDIEventCallback<?> callback) {
    return subscribeLocal(eventType, callback, true);
  }

  public static Subscription subscribeJsType(final String eventType, final JsTypeEventObserver<?> callback) {
    WindowEventObservers.createOrGet().add(eventType, callback);
     return new Subscription() {
       @Override
       public void remove() {
         // TODO can't unsubscribe per module atm.
       }
     };
  }

  private static Subscription subscribeLocal(final String eventType, final AbstractCDIEventCallback<?> callback,
          final boolean isLocalOnly) {

    if (!eventObservers.containsKey(eventType)) {
      eventObservers.put(eventType, new ArrayList<>());
    }

    eventObservers.get(eventType).add(callback);

    if (isLocalOnly) {
      localOnlyObserverTypes.add(eventType);
    }

    return () -> unsubscribe(eventType, callback);
  }

  public static Subscription subscribe(final String eventType, final AbstractCDIEventCallback<?> callback) {
    return subscribeLocal(eventType, callback, false);
  }

  private static void unsubscribe(final String eventType, final AbstractCDIEventCallback<?> callback) {
    if (eventObservers.containsKey(eventType)) {
      eventObservers.get(eventType).remove(callback);

      if (!localOnlyObserverTypes.contains(eventType)) {
        if (eventObservers.get(eventType).isEmpty()) {
          eventObservers.remove(eventType);
        }
      }
    }
  }



  public static void consumeEventFromMessage(final Message message) {
    final String beanType = message.get(String.class, CDIProtocol.BeanType);
    final Object beanRef = message.get(Object.class, CDIProtocol.BeanReference);

    final Set<String> firedBeanTypes = new HashSet<>();
    final Deque<String> beanTypeQueue = new LinkedList<>();
    beanTypeQueue.addLast(beanType);
    firedBeanTypes.add(beanType);
    while (!beanTypeQueue.isEmpty()) {
      final String curType = beanTypeQueue.poll();
      WindowEventObservers.createOrGet().fireEvent(curType, beanRef);
      _fireEvent(curType, message);
      if (lookupTable.containsKey(curType)) {
        for (final String superType : lookupTable.get(curType)) {
          if (!firedBeanTypes.contains(superType)) {
            beanTypeQueue.addLast(superType);
            firedBeanTypes.add(superType);
          }
        }
      }
    }
  }

  private static void _fireEvent(final String beanType, final Message message) {
    if (eventObservers.containsKey(beanType)) {
      for (final MessageCallback callback : new ArrayList<MessageCallback>(eventObservers.get(beanType))) {
        try {
          fireIfNotFired(callback, message);
        } catch (final Exception e) {
          final String potentialTarget = callbackOwnerClass(callback);
          String actualTarget = potentialTarget.equalsIgnoreCase("undefined.undefined") ? "[unavailable]" : potentialTarget;

          throw new RuntimeException("CDI Event exception: " + message + " sent to " + actualTarget, e);
        }
      }
    }
  }

  private static native String callbackOwnerClass(final Object o) /*-{

    var pkg, clazzName;

    for (var protoKey in o.__proto__) {
        if (protoKey.startsWith("___clazz")) {
            for (var clazzKey in o[protoKey]) {
                if (clazzKey.startsWith("package")) {
                    pkg = o[protoKey][clazzKey];
                }
                if (clazzKey.startsWith("compound")) {
                    clazzName = o[protoKey][clazzKey];
                }
            }
        }
    }
    return pkg + "." + clazzName;
  }-*/;

  @SuppressWarnings("unchecked")
  private static void fireIfNotFired(final MessageCallback callback, final Message message) {
    if (!message.hasResource(CLIENT_ALREADY_FIRED_RESOURCE)) {
      message.setResource(CLIENT_ALREADY_FIRED_RESOURCE, new IdentityHashMap<>());
    }

    if (!message.getResource(Map.class, CLIENT_ALREADY_FIRED_RESOURCE).containsKey(callback)) {
      callback.callback(message);
      message.getResource(Map.class, CLIENT_ALREADY_FIRED_RESOURCE).put(callback, "");
    }
  }

  public static void addRemoteEventType(final String remoteEvent) {
    remoteEvents.add(remoteEvent);
  }


  public static void addRemoteEventTypes(final String[] remoteEvent) {
    for (final String s : remoteEvent) {
      addRemoteEventType(s);
    }
  }

  public static void addPostInitTask(final Runnable runnable) {
    InitVotes.registerOneTimeDependencyCallback(CDI.class, runnable);
  }


  public static void activate(final String... remoteTypes) {
    if (!active) {
      addRemoteEventTypes(remoteTypes);
      active = true;

      logger.info("activated CDI eventing subsystem.");
      InitVotes.voteFor(CDI.class);
    }
  }

  public static boolean isRemoteCommunicationEnabled() {
    return false;
  }
}
