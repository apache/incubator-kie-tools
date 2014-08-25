package org.kie.uberfire.social.activities.service;

import java.util.List;

import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.model.SocialEventType;

public interface SocialTimelinePersistenceAPI {

    void setup();

    List<SocialActivitiesEvent> getLastEvents( SocialEventType type );

    List<SocialActivitiesEvent> getTimeline( SocialEventType type,
                                             String timelineFile );

    List<SocialActivitiesEvent> getLastEvents( SocialUser user );

    List<SocialActivitiesEvent> getRecentEvents( SocialUser user );

    void persist( SocialActivitiesEvent event );

    void persist( SocialUser user,
                  SocialActivitiesEvent event );

    Integer numberOfPages( SocialEventType type );

    List<SocialActivitiesEvent> getTimeline( SocialUser socialUser,
                                             String timelineFile );

    List<SocialActivitiesEvent> getRecentEvents( SocialEventType type );

    Integer getUserMostRecentFileIndex( SocialUser user );

    Integer getTypeMostRecentFileIndex( SocialEventType type );

    void saveAllEvents();

    Integer getNumberOfEventsOnFile( SocialEventType type,
                                     String file );

    Integer getNumberOfEventsOnFile( SocialUser socialUser,
                                     String file );
}
