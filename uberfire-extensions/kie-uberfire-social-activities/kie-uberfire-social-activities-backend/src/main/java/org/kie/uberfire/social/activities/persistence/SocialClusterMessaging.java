package org.kie.uberfire.social.activities.persistence;

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
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialTimelinePersistenceAPI;
import org.uberfire.backend.server.cluster.ClusterServiceFactoryProducer;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.message.MessageHandler;
import org.uberfire.commons.message.MessageHandlerResolver;
import org.uberfire.commons.message.MessageType;

@ApplicationScoped
public class SocialClusterMessaging {

    private Gson gson;

    private Type gsonCollectionType;

    private String cluster = "default";

    @Inject
    ClusterServiceFactoryProducer clusterServiceFactoryProducer;

    @Inject
    @Named("socialTimelinePersistence")
    SocialTimelinePersistenceAPI socialTimelinePersistence;

    @Inject
    SocialEventTypeRepositoryAPI socialEventTypeRepository;

    private ClusterService clusterService;

    @PostConstruct
    public void setup() {
        gsonFactory();
        clusterService = clusterServiceFactoryProducer.clusterServiceFactory().build( new MessageHandlerResolver() {
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
                        if ( type.equals( SocialClusterMessage.SOCIAL_EVENT ) ) {
                            handleSocialEvent( content );
                        }

                        if ( type.equals( SocialClusterMessage.SOCIAL_FILE_SYSTEM_PERSISTENCE ) ) {
                            handleSocialPersistenceEvent( content );
                        }
                        if ( type.equals( SocialClusterMessage.CLUSTER_SHUTDOWN ) ) {
                            handleClusterShutdown();
                        }

                        return new Pair<MessageType, Map<String, String>>( type, content );
                    }
                };
            }
        } );
    }

    private void handleClusterShutdown() {
        SocialTimelineCacheClusterPersistence cacheClusterPersistence = (SocialTimelineCacheClusterPersistence) socialTimelinePersistence;
        cacheClusterPersistence.clusterShutDown();
    }

    private void handleSocialPersistenceEvent( Map<String, String> content ) {
        SocialActivitiesEvent eventTypeName = null;
        SocialUser user = null;
        List<SocialActivitiesEvent> events = new ArrayList<SocialActivitiesEvent>();
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
        String eventJson = gson.toJson( event );
        Map<String, String> content = new HashMap<String, String>();
        content.put( SocialClusterMessage.NEW_EVENT.name(), eventJson );
        content.put( SocialClusterMessage.NEW_EVENT_USER.name(), eventJson );
        clusterService.broadcast( cluster, SocialClusterMessage.SOCIAL_EVENT,
                                  content );
    }

    public void notifyTimeLineUpdate( SocialActivitiesEvent event ) {
        Map<String, String> content = new HashMap<String, String>();
        String json = gson.toJson( event );
        content.put( SocialClusterMessage.UPDATE_TYPE_EVENT.name(), json );

        clusterService.broadcast( cluster, SocialClusterMessage.SOCIAL_FILE_SYSTEM_PERSISTENCE,
                                  content );
    }

    public void notifyTimeLineUpdate( SocialUser user,
                                      List<SocialActivitiesEvent> storedEvents ) {
        Map<String, String> content = new HashMap<String, String>();
        String json = gson.toJson( user );
        content.put( SocialClusterMessage.UPDATE_USER_EVENT.name(), json );
        clusterService.broadcast( cluster, SocialClusterMessage.SOCIAL_FILE_SYSTEM_PERSISTENCE,
                                  content );
    }

    public boolean canILockFileSystem() {
        if ( clusterService.isLocked() ) {
            return false;
        }
        clusterService.lock();
        return true;
    }

    public void unlockFileSystem() {
        clusterService.unlock();
    }

    public void notifySomeInstanceisTakingCareOfShutdown() {
        clusterService.broadcast( cluster, SocialClusterMessage.CLUSTER_SHUTDOWN,
                                  new HashMap<String, String>() );
    }

    private enum SocialClusterMessage implements MessageType {
        NEW_EVENT, NEW_EVENT_USER, UPDATE_TYPE_EVENT, UPDATE_USER_EVENT, SOCIAL_EVENT, SOCIAL_FILE_SYSTEM_PERSISTENCE, CLUSTER_SHUTDOWN;

    }
}
