package org.kie.uberfire.social.activities.repository;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.uberfire.social.activities.model.DefaultTypes;
import org.kie.uberfire.social.activities.model.PagedSocialQuery;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialPaged;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.persistence.SocialTimelineCacheInstancePersistenceUnitTestWrapper;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialCommandTypeFilter;
import org.kie.uberfire.social.activities.service.SocialTimelinePersistenceAPI;

import static org.junit.Assert.*;

public class SocialTypeTimelinePagedRepositoryTest {

    private SocialTypeTimelinePagedRepository repository;
    private SocialEventType type = DefaultTypes.DUMMY_EVENT;
    private SocialTimelinePersistenceAPI socialTimelinePersistenceFake;

    @Before
    public void setUp() throws Exception {
        socialTimelinePersistenceFake = new SocialTimelineCacheInstancePersistenceUnitTestWrapper();
        repository = new SocialTypeTimelinePagedRepository() {

            @Override
            SocialTimelinePersistenceAPI getSocialTimelinePersistenceAPI() {
                return socialTimelinePersistenceFake;
            }
            @Override
            SocialAdapter getSocialAdapter( String adapterName ) {
                return new SocialAdapter() {
                    @Override
                    public Class eventToIntercept() {
                        return null;
                    }

                    @Override
                    public SocialEventType socialEventType() {
                        return DefaultTypes.DUMMY_EVENT;
                    }

                    @Override
                    public boolean shouldInterceptThisEvent( Object event ) {
                        return false;
                    }

                    @Override
                    public SocialActivitiesEvent toSocial( Object object ) {
                        return null;
                    }

                    @Override
                    public List<SocialCommandTypeFilter> getTimelineFilters() {
                        return null;
                    }

                    @Override
                    public List<String> getTimelineFiltersNames() {
                        return null;
                    }
                };
            }
        };
    }

    @Test
    public void fullReadTest() {

        createFreshCacheEventsEvents( 3 );

        SocialPaged socialPaged = new SocialPaged( 5 );

        PagedSocialQuery query = repository.getEventTimeline( type.name(), socialPaged );
        assertFreshEvents( query );
        assertStoredEvent( "5", "0", 3, query.socialEvents() );
        assertStoredEvent( "5", "1", 4, query.socialEvents() );

        query = repository.getEventTimeline( type.name(), socialPaged );
        //file 5
        assertStoredEvent( "5", "2", 0, query.socialEvents() );
        assertStoredEvent( "5", "3", 1, query.socialEvents() );
        assertStoredEvent( "5", "4", 2, query.socialEvents() );
        //file 4
        assertStoredEvent( "4", "0", 3, query.socialEvents() );
        assertStoredEvent( "4", "1", 4, query.socialEvents() );
        assertTrue(socialPaged.canIGoForward());
        query = repository.getEventTimeline( type.name(), socialPaged );

        assertStoredEvent( "4", "2", 0, query.socialEvents() );
        assertStoredEvent( "4", "3", 1, query.socialEvents() );
        assertStoredEvent( "4", "4", 2, query.socialEvents() );
        //file number 3
        assertStoredEvent( "3", "0", 3, query.socialEvents() );
        assertStoredEvent( "3", "1", 4, query.socialEvents() );
        assertTrue(socialPaged.canIGoForward());

        query = repository.getEventTimeline( type.name(), socialPaged );
        assertStoredEvent( "3", "2", 0, query.socialEvents() );
        assertStoredEvent( "3", "3", 1, query.socialEvents() );
        assertStoredEvent( "3", "4", 2, query.socialEvents() );

        assertStoredEvent( "2", "0", 3, query.socialEvents() );
        assertStoredEvent( "2", "1", 4, query.socialEvents() );

        query = repository.getEventTimeline( type.name(), socialPaged );
        assertStoredEvent( "2", "2", 0, query.socialEvents() );
        assertStoredEvent( "2", "3", 1, query.socialEvents() );
        assertStoredEvent( "2", "4", 2, query.socialEvents() );
        assertTrue(socialPaged.canIGoForward());
        //one is empty - error to read file

        assertStoredEvent( "0", "0", 3, query.socialEvents() );
        assertStoredEvent( "0", "1", 4, query.socialEvents() );

        query = repository.getEventTimeline( type.name(), socialPaged );
        assertStoredEvent( "0", "2", 0, query.socialEvents() );
        assertStoredEvent( "0", "3", 1, query.socialEvents() );
        assertStoredEvent( "0", "4", 2, query.socialEvents() );
        assertTrue( query.socialEvents().size() == 3 );
        assertTrue(!socialPaged.canIGoForward());

    }

    private void assertStoredEvent( String fileName,
                                    String expected,
                                    int index,
                                    List<SocialActivitiesEvent> events ) {
        SocialActivitiesEvent event = events.get( index );
        assertEquals( fileName, event.getSocialUser().getName() );
        assertEquals( expected, event.getAdicionalInfo()[ 0 ] );
    }

    private void assertFreshEvents( PagedSocialQuery query ) {
        assertEquals( "0", query.socialEvents().get( 0 ).getAdicionalInfo()[ 0 ] );
        assertEquals( "1", query.socialEvents().get( 1 ).getAdicionalInfo()[ 0 ] );
        assertEquals( "2", query.socialEvents().get( 2 ).getAdicionalInfo()[ 0 ] );
    }

    private PagedSocialQuery queryAndAssertNumberOfEvents( int numberOfEvents,
                                                           SocialPaged socialPaged ) {
        PagedSocialQuery query = repository.getEventTimeline( type.name(), socialPaged );
        assertEquals( numberOfEvents, query.socialEvents().size() );
        return query;
    }

    private void createFreshCacheEventsEvents( int numberOfEvents ) {
        for ( int i = 0; i < numberOfEvents; i++ ) {
            socialTimelinePersistenceFake.persist(  new SocialActivitiesEvent( new SocialUser( "fresh" ), DefaultTypes.DUMMY_EVENT, new Date(), i + "" ) );
        }
    }
}
