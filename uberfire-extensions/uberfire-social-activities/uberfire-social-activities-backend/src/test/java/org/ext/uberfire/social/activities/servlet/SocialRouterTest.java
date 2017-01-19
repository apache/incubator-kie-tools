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

package org.ext.uberfire.social.activities.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.ext.uberfire.social.activities.model.DefaultTypes;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialCommandTypeFilter;

import static org.junit.Assert.*;

public class SocialRouterTest {

    @Test
    public void pathHandleTest() {
        String path = "/follow_user";
        SocialRouter router = new SocialRouter();
        assertEquals( "follow_user", router.extractPath( path ) );

    }

    @Test
    public void findSpecificAdapter() {
        String path = "/dummy_event";
        SocialRouter router = createMock();
        SocialAdapter adapter = router.getSocialAdapterByPath( path );
        assertEquals( DefaultTypes.DUMMY_EVENT, adapter.socialEventType() );
    }

    @Test
    public void getAdapterTimeLineFilters() {
        String path = "/dummy_event";
        SocialRouter router = createMock();
        SocialAdapter adapter = router.getSocialAdapterByPath( path );
        assertFalse( adapter.getTimelineFilters().isEmpty() );
    }

    private SocialRouter createMock() {
        return new SocialRouter() {
            public Map<Class, SocialAdapter> getSocialAdapters() {
                Map<Class, SocialAdapter> socialAdapters = new HashMap<Class, SocialAdapter>();
                socialAdapters.put( Object.class, new SocialAdapter() {
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
                        ArrayList<SocialCommandTypeFilter> predicates = new ArrayList<SocialCommandTypeFilter>();
                        predicates.add( new SocialCommandTypeFilter() {
                            @Override
                            public List<SocialActivitiesEvent> execute( String parameterValue,
                                                                        List<SocialActivitiesEvent> events ) {
                                return null;
                            }

                            @Override
                            public String getCommandName() {
                                return null;
                            }
                        } );
                        return predicates;
                    }

                    @Override
                    public List<String> getTimelineFiltersNames() {
                        return null;
                    }
                } );
                return socialAdapters;
            }
        };
    }
}
