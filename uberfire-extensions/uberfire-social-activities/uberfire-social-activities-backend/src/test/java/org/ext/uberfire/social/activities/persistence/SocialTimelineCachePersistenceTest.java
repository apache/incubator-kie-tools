/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ext.uberfire.social.activities.persistence;

import org.junit.Before;
import org.junit.Test;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.security.SocialSecurityConstraintsManager;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SocialTimelineCachePersistenceTest {


    private SocialTimelineCachePersistence socialTimelineCachePersistence;
    private SocialSecurityConstraintsManager socialSecurityConstraintsManager;
    private List<SocialActivitiesEvent> oneEventList = new ArrayList<SocialActivitiesEvent>();

    @Before
    public void setUp() throws Exception {
        socialTimelineCachePersistence = createFakeSocialTimelineCachePersistence();
        this.socialSecurityConstraintsManager = mock( SocialSecurityConstraintsManager.class );
        socialTimelineCachePersistence.socialSecurityConstraintsManager = socialSecurityConstraintsManager;
        oneEventList.add( new SocialActivitiesEvent() );
    }


    @Test
    public void getLastEventsShouldCallSocialConstraintsManagerTest() throws Exception {
        final SocialEventType type = mock( SocialEventType.class );
        socialTimelineCachePersistence.typeEventsTimelineCache.put( type, oneEventList );
        socialTimelineCachePersistence.typeEventsFreshEvents.put( type, oneEventList );
        socialTimelineCachePersistence.getLastEvents( type );

        verify( socialSecurityConstraintsManager ).applyConstraints( any( List.class ) );
    }

    @Test
    public void getRecentEventsShouldCallSocialConstraintsManagerTest() throws Exception {
        socialTimelineCachePersistence.getRecentEvents( mock( SocialEventType.class ) );

        verify( socialSecurityConstraintsManager ).applyConstraints( any( List.class ) );
    }

    @Test
    public void getLastUserEventsShouldCallSocialConstraintsManagerTest() throws Exception {
        final SocialUser user = new SocialUser( "dora" );
        socialTimelineCachePersistence.userEventsTimelineCache.put( user.getUserName(), oneEventList );
        socialTimelineCachePersistence.userEventsTimelineFreshEvents.put( user.getUserName(), oneEventList );
        socialTimelineCachePersistence.getLastEvents( user );

        verify( socialSecurityConstraintsManager ).applyConstraints( any( List.class ) );
    }

    @Test
    public void getUserRecentEventsShouldCallSocialConstraintsManagerTest() throws Exception {
        final SocialUser user = new SocialUser( "dora" );
        socialTimelineCachePersistence.userEventsTimelineCache.put( user.getUserName(), oneEventList );
        socialTimelineCachePersistence.userEventsTimelineFreshEvents.put( user.getUserName(), oneEventList );
        socialTimelineCachePersistence.getRecentEvents( user );

        verify( socialSecurityConstraintsManager ).applyConstraints( any( List.class ) );
    }

    @Test
    public void createOrGetTimelineShouldCallSocialConstraintsManagerTest() throws Exception {

        socialTimelineCachePersistence.createOrGetTimeline( mock( Path.class ) );

        verify( socialSecurityConstraintsManager ).applyConstraints( any( List.class ) );
    }

    @Test
    public void getTimelineShouldCallSocialConstraintsManagerTest() throws Exception {
        socialTimelineCachePersistence.getTimeline( mock( Path.class ), "" );

        verify( socialSecurityConstraintsManager ).applyConstraints( any( List.class ) );
    }

    private SocialTimelineCachePersistence createFakeSocialTimelineCachePersistence() {
        return new SocialTimelineCachePersistence() {

            @Override
            public void persist( SocialActivitiesEvent event ) {

            }

            @Override
            public void persist( SocialUser user, SocialActivitiesEvent event ) {

            }

            @Override
            public void saveAllEvents() {

            }

            @Override
            IOService getIoService() {
                final IOService mock = mock( IOService.class );
                when( mock.exists( any( Path.class ) ) ).thenReturn( false );
                return mock;
            }

            @Override
            void createPersistenceStructure( Path timelineDir ) {
            }

            @Override
            String getItemsMetadata( Path timeLineDir, String originalFilename ) {
                return "-1";
            }

            @Override
            SocialFile createSocialFile( Path fileTimeline ) {
                return mock( SocialFile.class );
            }
        };
    }
}