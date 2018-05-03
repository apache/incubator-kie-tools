/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.commons.cluster.events;

import java.util.UUID;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.spi.EventMetadata;
import javax.inject.Inject;

import org.jboss.errai.marshalling.server.ServerMarshalling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.cluster.ClusterJMSService;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.clusterapi.Clustered;
import org.uberfire.commons.services.cdi.Startup;

@Startup
@ApplicationScoped
public class ClusterEventObserver {

    public static final String CHANNEL_NAME = "CLUSTER_CDI_EVENTS";

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterEventObserver.class);

    private String nodeId = UUID.randomUUID().toString();

    private Event<Object> eventBus;
    private ClusterService clusterService;

    public ClusterEventObserver() {

    }

    @Inject
    public ClusterEventObserver(Event<Object> eventBus) {
        this.clusterService = new ClusterJMSService();
        this.eventBus = eventBus;
        if (this.clusterService.isAppFormerClustered()) {
            this.clusterService.connect();
            this.clusterService.createConsumer(ClusterJMSService.DestinationType.PubSub,
                                               CHANNEL_NAME,
                                               ClusterSerializedCDIMessageWrapper.class,
                                               message -> consumeMessage(eventBus,
                                                                         message));
        }
    }

    @PreDestroy
    public void shutdown(){
        if(this.clusterService.isAppFormerClustered()){
            this.clusterService.close();
        }
    }

    ClusterService getClusterService() {
        return clusterService;
    }

    void consumeMessage(Event<Object> eventBus,
                        ClusterSerializedCDIMessageWrapper message) {
        if (!message.getNodeId().equals(nodeId)) {
            try {
                Object event = fromJSON(message);
                eventBus.fire(event);
            } catch (Exception e) {
                LOGGER.error("Error consuming cluster event:  " + e.getMessage());
            }
        }
    }

    Object fromJSON(ClusterSerializedCDIMessageWrapper message) {
        return ServerMarshalling.fromJSON(message.getJson());
    }

    public void observeAllEvents(@Observes(notifyObserver = Reception.IF_EXISTS) Object event,
                                 EventMetadata metaData) {
        if (shouldObserveThisEvent(event,
                                   metaData)) {
            broadcast(event);
        }
    }

    public void broadcast(Object event) {
        if (!getClusterService().isAppFormerClustered()) {
            return;
        }

        ClusterSerializedCDIMessageWrapper wrapper = new ClusterSerializedCDIMessageWrapper(nodeId,
                                                                                            toJSON(event),
                                                                                            event.getClass().getName());

        getClusterService().broadcast(ClusterService.DestinationType.PubSub,
                                      CHANNEL_NAME,
                                      wrapper);
    }

    String toJSON(Object event) {
        return ServerMarshalling.toJSON(event);
    }

    boolean shouldObserveThisEvent(Object event,
                                   EventMetadata metaData) {
        return event.getClass().isAnnotationPresent(Clustered.class) && !createdOnThisClass(metaData);
    }

    private boolean createdOnThisClass(EventMetadata metaData) {
        if (metaData == null || metaData.getInjectionPoint() == null) {
            return false;
        } else {
            return metaData.getInjectionPoint().getBean().getBeanClass().equals(this.getClass());
        }
    }

    String getNodeId() {
        return nodeId;
    }
}
