package org.kie.uberfire.social.activities.persistence;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.server.SocialUserServicesExtendedBackEndImpl;
import org.kie.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.uberfire.io.IOService;

public class SocialTimelineCacheInstancePersistence extends SocialTimelineCachePersistence {

    public SocialTimelineCacheInstancePersistence( Gson gson,
                                                   Type gsonCollectionType,
                                                   IOService ioService,
                                                   SocialEventTypeRepositoryAPI socialEventTypeRepository,
                                                   SocialUserPersistenceAPI socialUserService,
                                                   SocialUserServicesExtendedBackEndImpl userServicesBackend ) {

        this.gson = gson;
        this.gsonCollectionType = gsonCollectionType;
        this.ioService = ioService;
        this.socialEventTypeRepository = socialEventTypeRepository;
        this.socialUserPersistenceAPI = socialUserService;
        this.userServicesBackend = userServicesBackend;
    }

    @Override
    public void persist( SocialActivitiesEvent event ) {
        SocialEventType type = findType( event );
        List<SocialActivitiesEvent> typeEvents = typeEventsFreshEvents.get( type );
        if ( typeEvents == null ) {
            typeEvents = new ArrayList<SocialActivitiesEvent>();
        }
        typeEvents.add( event );
        typeEventsFreshEvents.put( type, typeEvents );
        cacheControl( event );
    }

    SocialEventType findType( SocialActivitiesEvent event ) {
        return socialEventTypeRepository.findType( event.getType() );
    }

    @Override
    public void persist( SocialUser user,
                         SocialActivitiesEvent event ) {
        List<SocialActivitiesEvent> userEvents = userEventsTimelineFreshEvents.get( user.getUserName() );
        if ( userEvents == null ) {
            userEvents = new ArrayList<SocialActivitiesEvent>();
        }
        userEvents.add( event );
        userEventsTimelineFreshEvents.put( user.getUserName(), userEvents );
        cacheControl( user );
    }

    @Override
    public void saveAllEvents() {
        saveAllTypeEvents();
        saveAllUserTimelines();
    }

    void cacheControl( SocialUser user ) {
        SocialCacheControl socialCacheControl = userEventsCacheControl.get( user.getUserName() );
        if ( socialCacheControl == null ) {
            socialCacheControl = new SocialCacheControl();
            userEventsCacheControl.put( user.getUserName(), socialCacheControl );
        }
        socialCacheControl.registerNewEvent();
        if ( socialCacheControl.needToPersist() ) {
            storeTimeLineInFile( user );

            socialCacheControl.reset();

        }
    }

    void cacheControl( SocialActivitiesEvent event ) {
        SocialEventType type = findType( event );
        SocialCacheControl socialCacheControl = typeEventsCacheControl.get( type );
        socialCacheControl.registerNewEvent();
        if ( socialCacheControl.needToPersist() ) {
            storeTimeLineInFile( type );
            socialCacheControl.reset();
        }
    }

}
