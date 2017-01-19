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

package org.ext.uberfire.social.activities.repository;

import org.junit.Before;
import org.junit.Test;
import org.ext.uberfire.social.activities.model.*;
import org.ext.uberfire.social.activities.persistence.SocialTimelineCacheInstancePersistenceUnitTestWrapper;
import org.ext.uberfire.social.activities.security.SocialSecurityConstraintsManager;
import org.ext.uberfire.social.activities.service.SocialTimelinePersistenceAPI;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SocialUserTimelinePagedRepositoryTest {

    private SocialUserTimelinePagedRepository repository;
    private SocialUser socialUser;
    private SocialTimelinePersistenceAPI socialTimelinePersistenceFake;

    @Before
    public void setUp() throws Exception {
        SocialSecurityConstraintsManager socialSecurityConstraintsManager = new SocialSecurityConstraintsManager(){
            @Override
            public List<SocialActivitiesEvent> applyConstraints( List<SocialActivitiesEvent> events ) {
                return events;
            }
        };

        socialTimelinePersistenceFake = new SocialTimelineCacheInstancePersistenceUnitTestWrapper( socialSecurityConstraintsManager );
        repository = new SocialUserTimelinePagedRepository() {
            @Override
            SocialTimelinePersistenceAPI getSocialTimelinePersistenceAPI() {
                return socialTimelinePersistenceFake;
            }

        };
        socialUser = new SocialUser( "dorinha" );
    }

    @Test
    public void get10EventsFromFreshEvents() {
        createFreshCacheEventsEvents( 10 );
        queryAndAssertNumberOfEvents( 10, new SocialPaged( 10 ) );
    }

    @Test
    public void thereIsntEnoughtEventsOnFreshSoIHaveToReadLastFile() {

        createFreshCacheEventsEvents( 3 );

        SocialPaged socialPaged = new SocialPaged( 5 );

        queryAndAssertNumberOfEvents( 5, socialPaged );

    }

    @Test
    public void nextPageOnFileShouldContinueReading() {

        createFreshCacheEventsEvents( 3 );

        SocialPaged socialPaged = new SocialPaged( 5 );

        PagedSocialQuery query = repository.getUserTimeline( socialUser, socialPaged );
        assertFreshEvents( query );
        assertStoredEvent( "5", "0", 3, query.socialEvents() );
        assertStoredEvent( "5", "1", 4, query.socialEvents() );

        query = repository.getUserTimeline( socialUser, socialPaged );
        assertStoredEvent( "5", "2", 0, query.socialEvents() );
        assertStoredEvent( "5", "3", 1, query.socialEvents() );
        assertStoredEvent( "5", "4", 2, query.socialEvents() );

    }

    @Test
    public void backAndForwardReadTest() {

        createFreshCacheEventsEvents( 3 );
        SocialPaged socialPaged = new SocialPaged( 5 );
        PagedSocialQuery query = repository.getUserTimeline( socialUser, socialPaged );

        assertFreshEvents( query );
        assertStoredEvent( "5", "0", 3, query.socialEvents() );
        assertStoredEvent( "5", "1", 4, query.socialEvents() );
        assertTrue( !socialPaged.canIGoBackward() );
        query = repository.getUserTimeline( socialUser, socialPaged );
        //file 5
        assertStoredEvent( "5", "2", 0, query.socialEvents() );
        assertStoredEvent( "5", "3", 1, query.socialEvents() );
        assertStoredEvent( "5", "4", 2, query.socialEvents() );
        //file 4
        assertStoredEvent( "4", "0", 3, query.socialEvents() );
        assertStoredEvent( "4", "1", 4, query.socialEvents() );
        assertTrue( socialPaged.canIGoForward() );

        assertTrue( socialPaged.canIGoBackward() );

        query = repository.getUserTimeline( socialUser, socialPaged );

        assertStoredEvent( "4", "2", 0, query.socialEvents() );
        assertStoredEvent( "4", "3", 1, query.socialEvents() );
        assertStoredEvent( "4", "4", 2, query.socialEvents() );
        //file number 3
        assertStoredEvent( "3", "0", 3, query.socialEvents() );
        assertStoredEvent( "3", "1", 4, query.socialEvents() );
        assertTrue( socialPaged.canIGoForward() );

        socialPaged.backward();
        query = repository.getUserTimeline( socialUser, socialPaged );
        assertStoredEvent( "5", "2", 0, query.socialEvents() );
        assertStoredEvent( "5", "3", 1, query.socialEvents() );
        assertStoredEvent( "5", "4", 2, query.socialEvents() );
        //file 4
        assertStoredEvent( "4", "0", 3, query.socialEvents() );
        assertStoredEvent( "4", "1", 4, query.socialEvents() );
        assertTrue( socialPaged.canIGoForward() );

        assertTrue( socialPaged.canIGoBackward() );
        socialPaged = query.socialPaged();
        socialPaged.forward();
        query = repository.getUserTimeline( socialUser, socialPaged );
        assertStoredEvent( "4", "2", 0, query.socialEvents() );
        assertStoredEvent( "4", "3", 1, query.socialEvents() );
        assertStoredEvent( "4", "4", 2, query.socialEvents() );
        //file number 3
        assertStoredEvent( "3", "0", 3, query.socialEvents() );
        assertStoredEvent( "3", "1", 4, query.socialEvents() );
    }

    @Test
    public void fullForwardReadTest() {

        createFreshCacheEventsEvents( 3 );

        SocialPaged socialPaged = new SocialPaged( 5 );

        PagedSocialQuery query = repository.getUserTimeline( socialUser, socialPaged );
        assertFreshEvents( query );
        assertStoredEvent( "5", "0", 3, query.socialEvents() );
        assertStoredEvent( "5", "1", 4, query.socialEvents() );

        query = repository.getUserTimeline( socialUser, socialPaged );
        //file 5
        assertStoredEvent( "5", "2", 0, query.socialEvents() );
        assertStoredEvent( "5", "3", 1, query.socialEvents() );
        assertStoredEvent( "5", "4", 2, query.socialEvents() );
        //file 4
        assertStoredEvent( "4", "0", 3, query.socialEvents() );
        assertStoredEvent( "4", "1", 4, query.socialEvents() );
        assertTrue( socialPaged.canIGoForward() );
        query = repository.getUserTimeline( socialUser, socialPaged );

        assertStoredEvent( "4", "2", 0, query.socialEvents() );
        assertStoredEvent( "4", "3", 1, query.socialEvents() );
        assertStoredEvent( "4", "4", 2, query.socialEvents() );
        //file number 3
        assertStoredEvent( "3", "0", 3, query.socialEvents() );
        assertStoredEvent( "3", "1", 4, query.socialEvents() );
        assertTrue( socialPaged.canIGoForward() );

        query = repository.getUserTimeline( socialUser, socialPaged );
        assertStoredEvent( "3", "2", 0, query.socialEvents() );
        assertStoredEvent( "3", "3", 1, query.socialEvents() );
        assertStoredEvent( "3", "4", 2, query.socialEvents() );

        assertStoredEvent( "2", "0", 3, query.socialEvents() );
        assertStoredEvent( "2", "1", 4, query.socialEvents() );

        query = repository.getUserTimeline( socialUser, socialPaged );
        assertStoredEvent( "2", "2", 0, query.socialEvents() );
        assertStoredEvent( "2", "3", 1, query.socialEvents() );
        assertStoredEvent( "2", "4", 2, query.socialEvents() );
        assertTrue( socialPaged.canIGoForward() );
        //one is empty - error to read file

        assertStoredEvent( "0", "0", 3, query.socialEvents() );
        assertStoredEvent( "0", "1", 4, query.socialEvents() );

        query = repository.getUserTimeline( socialUser, socialPaged );
        assertStoredEvent( "0", "2", 0, query.socialEvents() );
        assertStoredEvent( "0", "3", 1, query.socialEvents() );
        assertStoredEvent( "0", "4", 2, query.socialEvents() );
        assertTrue( query.socialEvents().size() == 3 );
        assertTrue( !socialPaged.canIGoForward() );

    }

    private void assertStoredEvent( String fileName,
                                    String expected,
                                    int index,
                                    List<SocialActivitiesEvent> events ) {
        SocialActivitiesEvent event = events.get( index );
        assertEquals( fileName, event.getSocialUser().getUserName() );
        assertEquals( expected, event.getAdditionalInfo()[0] );
    }

    private void assertFreshEvents( PagedSocialQuery query ) {
        assertEquals( "2", query.socialEvents().get( 0 ).getAdditionalInfo()[0] );
        assertEquals( "1", query.socialEvents().get( 1 ).getAdditionalInfo()[0] );
        assertEquals( "0", query.socialEvents().get( 2 ).getAdditionalInfo()[0] );
    }

    @Test
    public void assertOrderOfEvents() {

        createFreshCacheEventsEvents( 3 );

        SocialPaged socialPaged = new SocialPaged( 1 );
        PagedSocialQuery query = repository.getUserTimeline( socialUser, socialPaged );
        assertEquals( "2", query.socialEvents().get( 0 ).getAdditionalInfo()[0] );

        query = repository.getUserTimeline( socialUser, socialPaged );
        assertEquals( "1", query.socialEvents().get( 0 ).getAdditionalInfo()[0] );

        query = repository.getUserTimeline( socialUser, socialPaged );
        assertEquals( "0", query.socialEvents().get( 0 ).getAdditionalInfo()[0] );

    }

    @Test
    public void bugOnQueryPagination() {

        SocialPaged socialPaged = new SocialPaged( 5 );
        PagedSocialQuery query = repository.getUserTimeline( socialUser, socialPaged );
        //page1
        assertTrue( !query.socialPaged().canIGoBackward() );
        assertTrue( query.socialPaged().canIGoForward() );

        //page2
        query = repository.getUserTimeline( socialUser, query.socialPaged() );
        assertTrue( query.socialPaged().canIGoBackward() );
        assertTrue( query.socialPaged().canIGoForward() );
        //page3
        query = repository.getUserTimeline( socialUser, query.socialPaged() );
        assertTrue( query.socialPaged().canIGoBackward() );
        assertTrue( query.socialPaged().canIGoForward() );
        query.socialPaged().backward();
        //page2
        query = repository.getUserTimeline( socialUser, query.socialPaged() );
        assertTrue( query.socialPaged().canIGoBackward() );
        assertTrue( query.socialPaged().canIGoForward() );
        query.socialPaged().backward();
        //page1
        query = repository.getUserTimeline( socialUser, query.socialPaged() );
        assertTrue( !query.socialPaged().canIGoBackward() );
        assertTrue( query.socialPaged().canIGoForward() );
        query.socialPaged().forward();
        //page2
        query = repository.getUserTimeline( socialUser, query.socialPaged() );
        assertTrue( query.socialPaged().canIGoBackward() );
        assertTrue( query.socialPaged().canIGoForward() );

    }

    private PagedSocialQuery queryAndAssertNumberOfEvents( int numberOfEvents,
                                                           SocialPaged socialPaged ) {
        PagedSocialQuery query = repository.getUserTimeline( socialUser, socialPaged );
        assertEquals( numberOfEvents, query.socialEvents().size() );
        return query;
    }

    private void createFreshCacheEventsEvents( int numberOfEvents ) {
        for ( int i = 0; i < numberOfEvents; i++ ) {
            socialTimelinePersistenceFake.persist( socialUser, new SocialActivitiesEvent( new SocialUser( "fresh" ), DefaultTypes.DUMMY_EVENT, new Date() ).withAdicionalInfo( i + "" ) );
        }
    }
}
