package org.kie.uberfire.social.activities.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.uberfire.social.activities.adapters.CommandTimelineFilter;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialRouterAPI;
import org.kie.uberfire.social.activities.service.SocialTimeLineRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialTimelinePersistenceAPI;

@Service
@ApplicationScoped
public class SocialTimeLineRepository implements SocialTimeLineRepositoryAPI {

    @Inject
    @Named("socialTimelinePersistence")
    SocialTimelinePersistenceAPI socialTimelinePersistence;

    @Inject
    SocialUserRepository socialUserRepository;

    @Inject
    SocialRouterAPI socialRouterAPI;

    @Inject
    CommandTimelineFilter commandTimelineFilter;

    @Override
    public List<SocialActivitiesEvent> getLastEventTimeline( String adapterName,
                                                             Map commandsMap ) {

        SocialAdapter socialAdapter = socialRouterAPI.getSocialAdapter( adapterName );

        return getLastEventTimeline( socialAdapter, commandsMap );
    }

    @Override
    public List<SocialActivitiesEvent> getLastEventTimeline( SocialAdapter type,
                                                             Map commandsMap ) {
        List<SocialActivitiesEvent> socialActivitiesEvents = socialTimelinePersistence.getLastEvents( type.socialEventType() );

        if ( socialActivitiesEvents == null ) {
            socialActivitiesEvents = new ArrayList<SocialActivitiesEvent>();
        }
        if ( shouldExecuteAdapters( commandsMap, socialActivitiesEvents ) ) {
            socialActivitiesEvents = commandTimelineFilter.executeTypeCommandsOn( type, commandsMap, socialActivitiesEvents );
        }

        sortListByDate( socialActivitiesEvents );

        return socialActivitiesEvents;
    }

    private void sortListByDate( List<SocialActivitiesEvent> socialActivitiesEvents ) {
        Collections.sort( socialActivitiesEvents, new Comparator<SocialActivitiesEvent>() {
            @Override
            public int compare( SocialActivitiesEvent o1,
                                SocialActivitiesEvent o2 ) {
                return o1.getTimestamp().compareTo( o2.getTimestamp() );
            }
        } );
    }

    @Override
    public void saveTypeEvent( SocialActivitiesEvent event ) {
        socialTimelinePersistence.persist( event );
    }

    @Override
    public void saveUserEvent( SocialActivitiesEvent event ) {
        SocialUser eventUser = event.getSocialUser();
        addEventToTimeline( event, eventUser );
        for ( String followerName : eventUser.getFollowersName() ) {
            SocialUser follower = socialUserRepository.findSocialUser( followerName );
            addEventToTimeline( event, follower );
        }
    }

    private void addEventToTimeline( SocialActivitiesEvent event,
                                     SocialUser user ) {
        socialTimelinePersistence.persist( user, event );
    }



    @Override
    public Integer numberOfPages( SocialEventType type ) {
        return socialTimelinePersistence.numberOfPages( type );
    }

    @Override
    public List<SocialActivitiesEvent> getLastUserTimeline( SocialUser user ) {
        return getLastUserTimeline( user, new HashMap() );
    }

    @Override
    public List<SocialActivitiesEvent> getLastUserTimeline( SocialUser user,
                                                            Map commandsMap ) {
        List<SocialActivitiesEvent> userEvents = socialTimelinePersistence.getLastEvents( user );
        if ( userEvents == null ) {
            userEvents = new ArrayList<SocialActivitiesEvent>();
        }
        if ( shouldExecuteAdapters( commandsMap, userEvents ) ) {
            userEvents = commandTimelineFilter.executeUserCommandsOn( userEvents, commandsMap );
        }
        sortListByDate( userEvents );
        return userEvents;
    }

    private boolean shouldExecuteAdapters( Map commandsMap,
                                           List<SocialActivitiesEvent> events ) {
        return !events.isEmpty() && commandsMap.size() > 0;
    }

}
