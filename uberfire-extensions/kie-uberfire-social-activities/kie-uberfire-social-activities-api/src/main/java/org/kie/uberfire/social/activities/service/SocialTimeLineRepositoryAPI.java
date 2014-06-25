package org.kie.uberfire.social.activities.service;

import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;

@Remote
public interface SocialTimeLineRepositoryAPI {

    List<SocialActivitiesEvent> getLastEventTimeline( String adapterName,
                                                      Map commandsMap );

    public List<SocialActivitiesEvent> getLastEventTimeline( SocialAdapter type,
                                                             Map commandsMap );

    public void saveTypeEvent( SocialActivitiesEvent event );

    void saveUserEvent( SocialActivitiesEvent event );

    List<SocialActivitiesEvent> getLastUserTimeline( SocialUser user,
                                                     Map parameterMap );

    List<SocialActivitiesEvent> getLastUserTimeline( SocialUser user );

    Integer numberOfPages( SocialEventType type );
}
