package org.kie.uberfire.social.activities.persistence;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.server.SocialUserServicesExtendedBackEndImpl;
import org.kie.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialTimelinePersistenceAPI;
import org.kie.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.uberfire.io.IOService;

public class SocialTimelineCacheClusterPersistence extends SocialTimelineCachePersistence implements SocialTimelinePersistenceAPI {

    private SocialClusterMessaging socialClusterMessaging;

    public SocialTimelineCacheClusterPersistence( Gson gson,
                                                  Type gsonCollectionType,
                                                  IOService ioService,
                                                  SocialEventTypeRepositoryAPI socialEventTypeRepository,
                                                  SocialUserPersistenceAPI socialUserPersistenceAPI,
                                                  SocialClusterMessaging socialClusterMessaging,
                                                  SocialUserServicesExtendedBackEndImpl userServicesBackend ) {

        this.gson = gson;
        this.gsonCollectionType = gsonCollectionType;
        this.ioService = ioService;
        this.socialEventTypeRepository = socialEventTypeRepository;
        this.socialUserPersistenceAPI = socialUserPersistenceAPI;
        this.socialClusterMessaging = socialClusterMessaging;
        this.userServicesBackend = userServicesBackend;
    }

    @Override
    public void persist( SocialActivitiesEvent event ) {
        SocialEventType type = socialEventTypeRepository.findType( event.getType() );
        persistEvent( event, type, true );
    }

    @Override
    public void persist( SocialUser user,
                         SocialActivitiesEvent event ) {
        if ( !clusterSyncEvent( event ) ) {
            registerNewEvent( user, event );
        } else {
            syncCluster( user );
        }

    }

    @Override
    public void saveAllEvents() {
        boolean iLockTheFileSystem = false;
        try {
            if ( !socialClusterMessaging.canILockFileSystem() ) {
                iLockTheFileSystem = true;
                socialClusterMessaging.notifySomeInstanceisTakingCareOfShutdown();
                saveAllTypeEvents();
                saveAllUserTimelines();
            }
        } finally {
            if ( iLockTheFileSystem ) {
                socialClusterMessaging.unlockFileSystem();
            }
        }
    }

    private void registerNewEvent( SocialUser user,
                                   SocialActivitiesEvent event ) {
        List<SocialActivitiesEvent> userEvents = userEventsTimelineFreshEvents.get( user.getUserName() );
        if ( userEvents == null ) {
            userEvents = new ArrayList<SocialActivitiesEvent>();
        }
        userEvents.add( event );
        userEventsTimelineFreshEvents.put( user.getUserName(), userEvents );
        cacheControl( user );
    }

    private void syncCluster( SocialUser user ) {
        List<SocialActivitiesEvent> myFreshEvents = userEventsTimelineFreshEvents.get( user.getUserName() );
        SocialCacheControl socialCacheControl = userEventsCacheControl.get( user.getUserName() );
        socialCacheControl.reset();
        List<SocialActivitiesEvent> actualTypeTimeline = createOrGetUserTimeline( user.getUserName() );
        refreshCache( user, actualTypeTimeline );
        syncMyStaleItems( myFreshEvents, actualTypeTimeline, user );
    }

    private void syncCluster( SocialEventType eventType ) {
        List<SocialActivitiesEvent> myFreshEvents = typeEventsFreshEvents.get( eventType );
        SocialCacheControl socialCacheControl = typeEventsCacheControl.get( eventType );
        socialCacheControl.reset();
        List<SocialActivitiesEvent> actualTypeTimeline = createOrGetTypeTimeline( eventType );
        refreshCache( eventType, actualTypeTimeline );
        syncMyStaleItems( myFreshEvents, actualTypeTimeline, eventType );
    }

    void persist( SocialActivitiesEvent event,
                  SocialEventType type,
                  boolean sendClusterMsg ) {
        persistEvent( event, type, sendClusterMsg );
    }

    private void persistEvent( SocialActivitiesEvent event,
                               SocialEventType eventType,
                               boolean sendClusterMsg ) {
        if ( !clusterSyncEvent( event ) ) {
            registerNewEvent( event, eventType, sendClusterMsg );
        } else {
            syncCluster( eventType );
        }

    }

    private void registerNewEvent( SocialActivitiesEvent event,
                                   SocialEventType eventType,
                                   boolean sendClusterMsg ) {
        List<SocialActivitiesEvent> typeEvents = typeEventsFreshEvents.get( eventType );
        typeEvents.add( event );
        typeEventsFreshEvents.put( eventType, typeEvents );
        cacheControl( event, eventType );

        if ( sendClusterMsg ) {
            socialClusterMessaging.notify( event );
        }
    }

    private boolean clusterSyncEvent( SocialActivitiesEvent event ) {
        return event.isDummyEvent();
    }

    private void syncMyStaleItems( List<SocialActivitiesEvent> myFreshEvents,
                                   List<SocialActivitiesEvent> storedTimeline,
                                   SocialEventType eventType ) {
        List<SocialActivitiesEvent> unsavedEvents = findStaleEvents( myFreshEvents, storedTimeline );

        if ( !unsavedEvents.isEmpty() ) {
            List<SocialActivitiesEvent> cacheEvents = typeEventsFreshEvents.get( eventType );
            cacheEvents.addAll( unsavedEvents );
            typeEventsFreshEvents.put( eventType, cacheEvents );
        }
    }

    private void syncMyStaleItems( List<SocialActivitiesEvent> myFreshEvents,
                                   List<SocialActivitiesEvent> storedTimeline,
                                   SocialUser user ) {
        List<SocialActivitiesEvent> unsavedEvents = findStaleEvents( myFreshEvents, storedTimeline );
        if ( !unsavedEvents.isEmpty() ) {
            List<SocialActivitiesEvent> cacheEvents = userEventsTimelineFreshEvents.get( user.getUserName() );
            cacheEvents.addAll( unsavedEvents );
            userEventsTimelineFreshEvents.put( user.getUserName(), cacheEvents );
        }

    }

    private List<SocialActivitiesEvent> findStaleEvents( List<SocialActivitiesEvent> myFreshEvents,
                                                         List<SocialActivitiesEvent> storedTimeline
                                                       ) {
        List<SocialActivitiesEvent> unsavedEvents = new ArrayList<SocialActivitiesEvent>();
        for ( SocialActivitiesEvent myEvent : myFreshEvents ) {
            boolean hasEvent = false;
            for ( SocialActivitiesEvent storedEvents : storedTimeline ) {
                if ( storedEvents.equals( myEvent ) ) {
                    hasEvent = true;
                    break;
                }
            }
            if ( !hasEvent ) {
                unsavedEvents.add( myEvent );
            }
        }
        return unsavedEvents;
    }

    private void cacheControl( SocialUser user ) {
        SocialCacheControl socialCacheControl = userEventsCacheControl.get( user.getUserName() );
        if ( socialCacheControl == null ) {
            socialCacheControl = new SocialCacheControl();
            userEventsCacheControl.put( user.getUserName(), socialCacheControl );
        }
        socialCacheControl.registerNewEvent();
        boolean iLockTheFileSystem = false;
        try {
            if ( socialCacheControl.needToPersist() && !socialClusterMessaging.canILockFileSystem() ) {
                iLockTheFileSystem = true;
                List<SocialActivitiesEvent> storedEvents = storeTimeLineInFile( user );
                socialClusterMessaging.notifyTimeLineUpdate( user, storedEvents );
                socialCacheControl.reset();
            }
        } finally {
            if ( iLockTheFileSystem ) {
                socialClusterMessaging.unlockFileSystem();
            }
        }
    }

    private void cacheControl( SocialActivitiesEvent event,
                               SocialEventType eventType ) {
        SocialEventType type = socialEventTypeRepository.findType( event.getType() );
        SocialCacheControl socialCacheControl = typeEventsCacheControl.get( type );
        socialCacheControl.registerNewEvent();
        boolean iLockTheFileSystem = false;
        try {
            if ( socialCacheControl.needToPersist() && !socialClusterMessaging.canILockFileSystem() ) {
                iLockTheFileSystem = true;
                storeTimeLineInFile( eventType );
                socialClusterMessaging.notifyTimeLineUpdate( event );
                socialCacheControl.reset();
            }
        } finally {
            if ( iLockTheFileSystem ) {
                socialClusterMessaging.unlockFileSystem();
            }
        }
    }

    public void clusterShutDown() {
        typeEventsFreshEvents = new HashMap<SocialEventType, List<SocialActivitiesEvent>>();
        userEventsTimelineFreshEvents = new HashMap<String, List<SocialActivitiesEvent>>();
    }
}
