/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.bus.client.api.Subscription;
import org.jboss.errai.bus.client.api.base.CommandMessage;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.bus.client.framework.BusState;
import org.jboss.errai.bus.client.framework.ClientMessageBusImpl;
import org.jboss.errai.bus.client.util.BusToolsCli;
import org.jboss.errai.common.client.api.WrappedPortable;
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
  public static final String SERVER_DISPATCHER_SUBJECT = CDI_SERVICE_SUBJECT_PREFIX + "Dispatcher";
  public static final String CLIENT_DISPATCHER_SUBJECT = CDI_SERVICE_SUBJECT_PREFIX + "ClientDispatcher";
  private static final String CLIENT_ALREADY_FIRED_RESOURCE = CDI_SERVICE_SUBJECT_PREFIX + "AlreadyFired";

  private static final Set<String> remoteEvents = new HashSet<>();
  private static boolean active = false;

  private static Map<String, List<AbstractCDIEventCallback<?>>> eventObservers = new HashMap<>();
  private static Set<String> localOnlyObserverTypes = new HashSet<>();
  private static Map<String, Collection<String>> lookupTable = Collections.emptyMap();
  private static Map<String, List<MessageFireDeferral>> fireOnSubscribe = new LinkedHashMap<>();

  private static Logger logger = LoggerFactory.getLogger(CDI.class);

  public static final MessageCallback ROUTING_CALLBACK = new MessageCallback() {
    @Override
    public void callback(final Message message) {
      consumeEventFromMessage(message);
    }
  };

  public static String getSubjectNameByType(final String typeName) {
    return CDI_SUBJECT_PREFIX + typeName;
  }

  /**
   * Should only be called by bootstrapper for testing purposes.
   */
  public void __resetSubsystem() {
    for (final String eventType : new HashSet<>(((ClientMessageBus) ErraiBus.get()).getAllRegisteredSubjects())) {
      if (eventType.startsWith(CDI_SUBJECT_PREFIX)) {
        ErraiBus.get().unsubscribeAll(eventType);
      }
    }

    remoteEvents.clear();
    active = false;
    fireOnSubscribe.clear();
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

    final Object beanRef;
    if (payload instanceof WrappedPortable) {
      beanRef = ((WrappedPortable) payload).unwrap();
      if (beanRef == null) return;
    }
    else {
      beanRef = payload;
    }

    final Map<String, Object> messageMap = new HashMap<>();
    messageMap.put(MessageParts.CommandType.name(), CDICommands.CDIEvent.name());
    messageMap.put(CDIProtocol.BeanType.name(), beanRef.getClass().getName());
    messageMap.put(CDIProtocol.BeanReference.name(), beanRef);
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
      eventObservers.put(eventType, new ArrayList<AbstractCDIEventCallback<?>>());
    }
    eventObservers.get(eventType).add(callback);

    if (isLocalOnly) {
      localOnlyObserverTypes.add(eventType);
    }

    return new Subscription() {
      @Override
      public void remove() {
        unsubscribe(eventType, callback);
      }
    };
  }

  public static Subscription subscribe(final String eventType, final AbstractCDIEventCallback<?> callback) {

    if (isRemoteCommunicationEnabled() && ErraiBus.get() instanceof ClientMessageBusImpl
            && ((ClientMessageBusImpl) ErraiBus.get()).getState().equals(BusState.CONNECTED)) {
      MessageBuilder.createMessage()
          .toSubject(CDI.SERVER_DISPATCHER_SUBJECT)
          .command(CDICommands.RemoteSubscribe)
          .with(CDIProtocol.BeanType, eventType)
          .with(CDIProtocol.Qualifiers, callback.getQualifiers())
          .noErrorHandling().sendNowWith(ErraiBus.get());
    }

    return subscribeLocal(eventType, callback, false);
  }

  private static void unsubscribe(final String eventType, final AbstractCDIEventCallback<?> callback) {
    if (eventObservers.containsKey(eventType)) {
      eventObservers.get(eventType).remove(callback);

      if (!localOnlyObserverTypes.contains(eventType)) {
        boolean shouldUnsubscribe = true;
        for (final AbstractCDIEventCallback<?> cb : eventObservers.get(eventType)) {
          if (cb.getQualifiers().equals(callback.getQualifiers())) {
            // found another matching observer -> do not unsubscribe
            shouldUnsubscribe = false;
            break;
          }
        }

        if (isRemoteCommunicationEnabled() && shouldUnsubscribe) {
          MessageBuilder.createMessage()
              .toSubject(CDI.SERVER_DISPATCHER_SUBJECT)
              .command(CDICommands.RemoteUnsubscribe)
              .with(CDIProtocol.BeanType, eventType)
              .with(CDIProtocol.Qualifiers, callback.getQualifiers())
              .noErrorHandling().sendNowWith(ErraiBus.get());
        }

        if (eventObservers.get(eventType).isEmpty()) {
          eventObservers.remove(eventType);
        }
      }
    }
  }

  /**
   * Informs the server of all active CDI observers currently registered on the
   * client. This is not strictly necessary when the client bus first connects,
   * because observers register themselves with the server as they are created.
   * However, if the QueueSession expires and the bus reconnects, it is
   * essential to inform the server of all existing CDI observers so the
   * server-side event routing can be established for the new session.
   * <p>
   * Application code should never have to call this method directly. The Errai
   * framework calls this method when required.
   */
  public static void resendSubscriptionRequestForAllEventTypes() {
    if (isRemoteCommunicationEnabled()) {
      int remoteEventCount = 0;
      for (final Map.Entry<String, List<AbstractCDIEventCallback<?>>> mapEntry : eventObservers.entrySet()) {
        final String eventType = mapEntry.getKey();
        if (!localOnlyObserverTypes.contains(eventType)) {
          for (final AbstractCDIEventCallback<?> callback : mapEntry.getValue()) {
            remoteEventCount++;
            MessageBuilder.createMessage()
                .toSubject(CDI.SERVER_DISPATCHER_SUBJECT)
                .command(CDICommands.RemoteSubscribe)
                .with(CDIProtocol.BeanType, eventType)
                .with(CDIProtocol.Qualifiers, callback.getQualifiers())
                .noErrorHandling().sendNowWith(ErraiBus.get());
          }
        }
      }
      logger.info("requested server to forward CDI events for " + remoteEventCount + " existing observers");
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

    if (active) {
      fireIfWaiting(remoteEvent);
    }
  }

  private static void fireIfWaiting(final String remoteEvent) {
    if (fireOnSubscribe.containsKey(remoteEvent)) {
      for (final MessageFireDeferral runnable : fireOnSubscribe.get(remoteEvent)) {
        runnable.send();
      }
      fireOnSubscribe.remove(remoteEvent);
    }
  }

  private static void fireAllIfWaiting() {
    for (final String svc : new HashSet<>(fireOnSubscribe.keySet())) {
      fireIfWaiting(svc);
    }
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

      fireAllIfWaiting();

      logger.info("activated CDI eventing subsystem.");
      InitVotes.voteFor(CDI.class);
    }
  }

  static class MessageFireDeferral {
    final Message message;
    final long time;

    MessageFireDeferral(final long time, final Message message) {
      this.time = time;
      this.message = message;
    }

    public Message getMessage() {
      return message;
    }

    public long getTime() {
      return time;
    }

    public void send() {
      ErraiBus.get().send(message);
    }
  }

  private static boolean isRemoteCommunicationEnabled() {
    return BusToolsCli.isRemoteCommunicationEnabled();
  }
}
