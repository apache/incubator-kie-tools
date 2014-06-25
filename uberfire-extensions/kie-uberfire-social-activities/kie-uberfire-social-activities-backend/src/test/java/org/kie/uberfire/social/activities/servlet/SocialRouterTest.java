package org.kie.uberfire.social.activities.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kie.uberfire.social.activities.model.ExtendedTypes;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialCommandTypeFilter;

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
        String path = "/follow_user";
        SocialRouter router = createMock();
        SocialAdapter adapter = router.getSocialAdapterByPath( path );
        assertEquals( ExtendedTypes.FOLLOW_USER, adapter.socialEventType() );
    }

    @Test
    public void getAdapterTimeLineFilters() {
        String path = "/follow_user";
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
                        return ExtendedTypes.FOLLOW_USER;
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
