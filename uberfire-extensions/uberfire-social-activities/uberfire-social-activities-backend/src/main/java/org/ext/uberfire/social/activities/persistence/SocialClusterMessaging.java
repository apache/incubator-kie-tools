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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialTimelinePersistenceAPI;
import org.ext.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.message.MessageHandler;
import org.uberfire.commons.message.MessageHandlerResolver;
import org.uberfire.commons.message.MessageType;
import org.uberfire.commons.services.cdi.Startup;

@ApplicationScoped
@Startup
public class SocialClusterMessaging {

    private Gson gson;

    private Type gsonCollectionType;

    private String cluster = "social-service";

    @Inject
    private ClusterServiceFactory clusterServiceFactory;

    @Inject
    @Named("socialTimelinePersistence")
    private SocialTimelinePersistenceAPI socialTimelinePersistence;

    @Inject
    private SocialEventTypeRepositoryAPI socialEventTypeRepository;

    @Inject
    @Named( "socialUserPersistenceAPI" )
    private SocialUserPersistenceAPI socialUserPersistenceAPI;

    private ClusterService clusterService;

    @PostConstruct
    public void setup() {
        gsonFactory();
        if ( clusterServiceFactory != null ) {
            clusterService = clusterServiceFactory.build( new MessageHandlerResolver() {
                @Override
                public String getServiceId() {
                    return cluster;
                }

                @Override
                public MessageHandler resolveHandler( String serviceId,
                                                      MessageType type ) {
                    return new MessageHandler() {
                        @Override
                        public Pair<MessageType, Map<String, String>> handleMessage( MessageType type,
                                                                                     Map<String, String> content ) {
                            if ( type != null ) {
                                String strType = type.toString();
                                if ( strType.equals( SocialClusterMessage.SOCIAL_EVENT.name() ) ) {
                                    handleSocialEvent( content );
                                }
                                if ( strType.equals( SocialClusterMessage.SOCIAL_FILE_SYSTEM_PERSISTENCE.name() ) ) {
                                    handleSocialPersistenceEvent( content );
                                }
                                if ( strType.equals( SocialClusterMessage.CLUSTER_SHUTDOWN.name() ) ) {
                                    handleClusterShutdown();
                                }
                            }

                            return new Pair<MessageType, Map<String, String>>( type, content );
                        }
                    };
                }
            } );
        } else {
            clusterService = null;
        }
    }

    private void handleClusterShutdown() {
        SocialTimelineCacheClusterPersistence cacheClusterPersistence = (SocialTimelineCacheClusterPersistence) socialTimelinePersistence;
        cacheClusterPersistence.someNodeShutdownAndPersistEvents();
    }

    private void handleSocialPersistenceEvent( Map<String, String> content ) {
        SocialActivitiesEvent eventTypeName = null;
        SocialUser user = null;
        SocialTimelineCacheClusterPersistence cacheClusterPersistence = (SocialTimelineCacheClusterPersistence) socialTimelinePersistence;
        for ( final Map.Entry<String, String> entry : content.entrySet() ) {
            if ( entry.getKey().equalsIgnoreCase( SocialClusterMessage.UPDATE_TYPE_EVENT.name() ) ) {
                eventTypeName = gson.fromJson( entry.getValue(), SocialActivitiesEvent.class );
            }
            if ( entry.getKey().equalsIgnoreCase( SocialClusterMessage.UPDATE_USER_EVENT.name() ) ) {
                user = gson.fromJson( entry.getValue(), SocialUser.class );
            }
        }
        if ( user == null || user.getUserName() == null ) {
            SocialEventType typeEvent = socialEventTypeRepository.findType( eventTypeName.getType() );
            cacheClusterPersistence.persist( SocialActivitiesEvent.getDummyLastWrittenMarker(), typeEvent, false );
        } else {
            cacheClusterPersistence.persist( user, SocialActivitiesEvent.getDummyLastWrittenMarker() );
        }
    }

    private void handleSocialEvent( Map<String, String> content ) {
        SocialActivitiesEvent event = null;
        SocialUser user = null;
        for ( final Map.Entry<String, String> entry : content.entrySet() ) {
            if ( entry.getKey().equalsIgnoreCase( SocialClusterMessage.NEW_EVENT.name() ) ) {
                event = gson.fromJson( entry.getValue(), SocialActivitiesEvent.class );
            }
            if ( entry.getKey().equalsIgnoreCase( SocialClusterMessage.NEW_EVENT_USER.name() ) ) {
                user = gson.fromJson( entry.getValue(), SocialUser.class );
            }
        }
        if ( event != null ) {
            SocialEventType typeEvent = socialEventTypeRepository.findType( event.getType() );
            SocialTimelineCacheClusterPersistence cacheClusterPersistence = (SocialTimelineCacheClusterPersistence) socialTimelinePersistence;

            cacheClusterPersistence.persist( event, typeEvent, false );
            if ( user != null ) {
                cacheClusterPersistence.persist( user, event );
                for ( String followerName : user.getFollowersName() ) {
                    SocialUser follower = socialUserPersistenceAPI.getSocialUser( followerName );
                    cacheClusterPersistence.persist( follower, event );
                }
            }
        }

    }

    void gsonFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        gsonCollectionType = new TypeToken<Collection<SocialActivitiesEvent>>() {
        }.getType();
    }

    public void notify( SocialActivitiesEvent event ) {
        if ( clusterService == null ) {
            return;
        }
        String eventJson = gson.toJson( event );
        String userJson = gson.toJson( event.getSocialUser() );
        Map<String, String> content = new HashMap<String, String>();
        content.put( SocialClusterMessage.NEW_EVENT.name(), eventJson );
        content.put( SocialClusterMessage.NEW_EVENT_USER.name(), userJson );
        clusterService.broadcast( cluster, SocialClusterMessage.SOCIAL_EVENT,
                                  content );
    }

    public void notifyTimeLineUpdate( SocialActivitiesEvent event ) {
        if ( clusterService == null ) {
            return;
        }
        Map<String, String> content = new HashMap<String, String>();
        String json = gson.toJson( event );
        content.put( SocialClusterMessage.UPDATE_TYPE_EVENT.name(), json );

        clusterService.broadcast( cluster, SocialClusterMessage.SOCIAL_FILE_SYSTEM_PERSISTENCE,
                                  content );
    }

    public void notifyTimeLineUpdate( SocialUser user,
                                      List<SocialActivitiesEvent> storedEvents ) {
        if ( clusterService == null ) {
            return;
        }
        Map<String, String> content = new HashMap<String, String>();
        String json = gson.toJson( user );
        content.put( SocialClusterMessage.UPDATE_USER_EVENT.name(), json );
        clusterService.broadcast( cluster, SocialClusterMessage.SOCIAL_FILE_SYSTEM_PERSISTENCE,
                                  content );
    }

    public void notifySomeInstanceisOnShutdown() {
        if ( clusterService == null ) {
            return;
        }
        clusterService.broadcast( cluster, SocialClusterMessage.CLUSTER_SHUTDOWN,
                                  new HashMap<String, String>() );
    }

    private enum SocialClusterMessage implements MessageType {
        NEW_EVENT, NEW_EVENT_USER, UPDATE_TYPE_EVENT, UPDATE_USER_EVENT, SOCIAL_EVENT, SOCIAL_FILE_SYSTEM_PERSISTENCE, CLUSTER_SHUTDOWN;

    }
    public void lockFileSystem() {
        clusterService.lock();
    }
    public void unlockFileSystem() {
        clusterService.unlock();
    }
}
