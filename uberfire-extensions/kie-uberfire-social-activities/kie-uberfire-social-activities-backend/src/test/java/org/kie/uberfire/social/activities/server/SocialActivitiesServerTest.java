package org.kie.uberfire.social.activities.server;

import java.util.Date;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.kie.uberfire.social.activities.model.DefaultTypes;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.repository.SocialTimeLineRepository;
import org.kie.uberfire.social.activities.repository.SocialTimeLineRepositoryUnitTestWrapper;

import static org.junit.Assert.*;

public class SocialActivitiesServerTest {

    SocialActivitiesServer server;
    SocialTimeLineRepository repository;

    @Before
    public void setup() {
        repository = new SocialTimeLineRepositoryUnitTestWrapper();
        server = new SocialActivitiesServer( repository );
    }

    @Test
    public void registerSocialActivity_retrieveByType() {
        SocialUser user = new SocialUser( "user" );
        String type = DefaultTypes.DUMMY_EVENT.name();
        SocialActivitiesEvent event = new SocialActivitiesEvent( user, type, new Date() );
        assertTrue( repository.getLastEventTimeline( type, new HashMap() ).size() == 0 );
        server.register( event );
        assertTrue( repository.getLastEventTimeline( type, new HashMap() ).size() == 1 );
        server.register( event );
        assertTrue( repository.getLastEventTimeline( type, new HashMap() ).size() == 2 );
    }

    @Test
    public void registerSocialActivity_retrieveByUser() {
        SocialUser user = new SocialUser( "user" );
        String type = DefaultTypes.DUMMY_EVENT.name();
        SocialActivitiesEvent event = new SocialActivitiesEvent( user, type, new Date() );
        assertTrue( repository.getLastUserTimeline( user ).size() == 0 );
        server.register( event );
        assertTrue( repository.getLastUserTimeline( user ).size() == 1 );
        server.register( event );
        assertTrue( repository.getLastUserTimeline( user ).size() == 2 );
    }

}
