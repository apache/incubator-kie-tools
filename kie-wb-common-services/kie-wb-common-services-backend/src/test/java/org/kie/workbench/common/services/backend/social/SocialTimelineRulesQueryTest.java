/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.social;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.ext.uberfire.social.activities.model.DefaultTypes;
import org.ext.uberfire.social.activities.model.PagedSocialQuery;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialPaged;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialAdapterRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialCommandTypeFilter;
import org.ext.uberfire.social.activities.service.SocialTimeLineRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialTypeTimelinePagedRepositoryAPI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SocialTimelineRulesQueryTest {

    @Mock
    private SocialTimeLineRepositoryAPI socialTimeLineRepositoryAPI;

    @Mock
    private SocialTypeTimelinePagedRepositoryAPI socialTypeTimelinePagedRepositoryAPI;

    @Mock
    private SocialAdapterRepositoryAPI socialAdapterRepositoryAPI;

    @Mock
    private PagedSocialQuery pagedSocialQuery;

    private SocialTimelineRulesQuery query;

    @Before
    public void setup() {
        this.query = new SocialTimelineRulesQuery(socialTimeLineRepositoryAPI,
                                                  socialTypeTimelinePagedRepositoryAPI,
                                                  socialAdapterRepositoryAPI);
        when(socialTimeLineRepositoryAPI.getLastEventTimeline(anyString(),
                                                              anyMap())).thenReturn(Collections.singletonList(makeMockEvent()));
        when(socialAdapterRepositoryAPI.getSocialAdapters()).thenReturn(new HashMap<Class, SocialAdapter>() {{
            put(TestSocialEvent.class,
                new TestSocialAdaptor());
        }});
        when(socialTypeTimelinePagedRepositoryAPI.getEventTimeline(anyString(),
                                                                   any(SocialPaged.class),
                                                                   anyMap())).thenReturn(pagedSocialQuery);
        when(pagedSocialQuery.socialEvents()).thenReturn(Collections.singletonList(makeMockEvent()));
    }

    @Test
    public void checkKSessionInstantiation() {
        final List<SocialActivitiesEvent> events = query.executeAllRules();
        assertNotNull(events);
    }

    private SocialActivitiesEvent makeMockEvent() {
        return new SocialActivitiesEvent().withAdicionalInfo("info");
    }

    private static class TestSocialEvent {

    }

    private static class TestSocialAdaptor implements SocialAdapter<TestSocialEvent> {

        @Override
        public Class<TestSocialEvent> eventToIntercept() {
            return TestSocialEvent.class;
        }

        @Override
        public SocialEventType socialEventType() {
            return DefaultTypes.DUMMY_EVENT;
        }

        @Override
        public boolean shouldInterceptThisEvent(final Object event) {
            return event.getClass().getSimpleName().equals(eventToIntercept().getSimpleName());
        }

        @Override
        public SocialActivitiesEvent toSocial(final Object object) {
            return new SocialActivitiesEvent(new SocialUser("user"),
                                             socialEventType().name(),
                                             new Date()
            ).withDescription("test adaptor").withAdicionalInfo("info");
        }

        @Override
        public List<SocialCommandTypeFilter> getTimelineFilters() {
            return new ArrayList<>();
        }

        @Override
        public List<String> getTimelineFiltersNames() {
            return new ArrayList<>();
        }
    }
}
