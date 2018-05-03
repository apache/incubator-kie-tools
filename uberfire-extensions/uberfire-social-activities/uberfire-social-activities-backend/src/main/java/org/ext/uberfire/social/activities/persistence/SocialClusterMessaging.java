/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.ext.uberfire.social.activities.persistence;

import java.util.List;
import java.util.UUID;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialTimelinePersistenceAPI;
import org.ext.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.cluster.ClusterJMSService;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.services.cdi.Startup;

@ApplicationScoped
@Startup
public class SocialClusterMessaging {

    public static final String CHANNEL_NAME = "SOCIAL_CLUSTER_MESSAGE";

    private SocialTimelinePersistenceAPI socialTimelinePersistence;

    @Inject
    private SocialEventTypeRepositoryAPI socialEventTypeRepository;

    private SocialUserPersistenceAPI socialUserPersistenceAPI;

    private ClusterService clusterService;

    private String nodeId = UUID.randomUUID().toString();

    public void setup(ClusterService clusterService,
                      SocialTimelinePersistenceAPI socialTimelinePersistenceAPI,
                      SocialUserPersistenceAPI socialUserPersistenceAPI) {
        this.clusterService = clusterService;
        this.socialTimelinePersistence = socialTimelinePersistenceAPI;
        this.socialUserPersistenceAPI = socialUserPersistenceAPI;

        if (this.clusterService.isAppFormerClustered()) {
            this.clusterService.connect();
            this.clusterService.createConsumer(ClusterJMSService.DestinationType.PubSub,
                                               CHANNEL_NAME,
                                               SocialMessageWrapper.class,
                                               message -> {
                                                   if (!message.getNodeId().equals(nodeId)) {
                                                       SocialClusterMessage strType = message.getMessageType();
                                                       if (strType.equals(SocialClusterMessage.SOCIAL_EVENT.name())) {
                                                           handleSocialEvent(message);
                                                       }
                                                       if (strType.equals(SocialClusterMessage.SOCIAL_FILE_SYSTEM_PERSISTENCE.name())) {
                                                           handleSocialPersistenceEvent(message);
                                                       }
                                                       if (strType.equals(SocialClusterMessage.CLUSTER_SHUTDOWN.name())) {
                                                           handleClusterShutdown();
                                                       }
                                                   }
                                               });
        }
    }

    @PreDestroy
    public void shutdown(){
        if(this.clusterService.isAppFormerClustered()){
            this.clusterService.close();
        }
    }

    private void handleClusterShutdown() {
        SocialTimelineCacheClusterPersistence cacheClusterPersistence = (SocialTimelineCacheClusterPersistence) socialTimelinePersistence;
        cacheClusterPersistence.someNodeShutdownAndPersistEvents();
    }

    private void handleSocialPersistenceEvent(SocialMessageWrapper message) {
        SocialActivitiesEvent eventTypeName = null;
        SocialUser user = null;
        SocialTimelineCacheClusterPersistence cacheClusterPersistence = (SocialTimelineCacheClusterPersistence) socialTimelinePersistence;
        if (message.getSubMessageType().equals(SocialClusterMessage.UPDATE_TYPE_EVENT)) {
            eventTypeName = message.getEvent();
        }
        if (message.getSubMessageType().equals(SocialClusterMessage.UPDATE_USER_EVENT)) {
            user = message.getUser();
        }
        if (user == null || user.getUserName() == null) {
            SocialEventType typeEvent = socialEventTypeRepository.findType(eventTypeName.getType());
            cacheClusterPersistence.persist(SocialActivitiesEvent.getDummyLastWrittenMarker(),
                                            typeEvent,
                                            false);
        } else {
            cacheClusterPersistence.persist(user,
                                            SocialActivitiesEvent.getDummyLastWrittenMarker());
        }
    }

    private void handleSocialEvent(SocialMessageWrapper message) {
        SocialActivitiesEvent event = message.getEvent();
        SocialUser user = message.getUser();
        if (event != null) {
            SocialEventType typeEvent = socialEventTypeRepository.findType(event.getType());
            SocialTimelineCacheClusterPersistence cacheClusterPersistence = (SocialTimelineCacheClusterPersistence) socialTimelinePersistence;

            cacheClusterPersistence.persist(event,
                                            typeEvent,
                                            false);
            if (user != null) {
                cacheClusterPersistence.persist(user,
                                                event);
                for (String followerName : user.getFollowersName()) {
                    SocialUser follower = socialUserPersistenceAPI.getSocialUser(followerName);
                    cacheClusterPersistence.persist(follower,
                                                    event);
                }
            }
        }
    }

    public void notify(SocialActivitiesEvent event) {
        if (!clusterService.isAppFormerClustered()) {
            return;
        }
        clusterService.broadcast(ClusterService.DestinationType.PubSub,
                                 CHANNEL_NAME,
                                 new SocialMessageWrapper(nodeId,
                                                          SocialClusterMessage.SOCIAL_EVENT,
                                                          event,
                                                          event.getSocialUser()));
    }

    public void notifyTimeLineUpdate(SocialActivitiesEvent event) {
        if (!clusterService.isAppFormerClustered()) {
            return;
        }
        clusterService.broadcast(ClusterService.DestinationType.PubSub,
                                 CHANNEL_NAME,
                                 new SocialMessageWrapper(nodeId,
                                                          SocialClusterMessage.SOCIAL_FILE_SYSTEM_PERSISTENCE,
                                                          event,
                                                          null,
                                                          SocialClusterMessage.UPDATE_TYPE_EVENT));
    }

    public void notifyTimeLineUpdate(SocialUser user,
                                     List<SocialActivitiesEvent> storedEvents) {
        if (!clusterService.isAppFormerClustered()) {
            return;
        }
        clusterService.broadcast(ClusterService.DestinationType.PubSub,
                                 CHANNEL_NAME,
                                 new SocialMessageWrapper(nodeId,
                                                          SocialClusterMessage.SOCIAL_FILE_SYSTEM_PERSISTENCE,
                                                          null,
                                                          user,
                                                          SocialClusterMessage.UPDATE_USER_EVENT));
    }

    public void notifySomeInstanceIsOnShutdown() {
        if (!clusterService.isAppFormerClustered()) {
            return;
        }
        clusterService.broadcast(ClusterService.DestinationType.PubSub,
                                 CHANNEL_NAME,
                                 new SocialMessageWrapper(nodeId,
                                                          SocialClusterMessage.CLUSTER_SHUTDOWN));
    }
}
