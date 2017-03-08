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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ext.uberfire.social.activities.model.DefaultTypes;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialCommandTypeFilter;

public class SampleSocialUserEventAdapter implements SocialAdapter<SampleSocialUserEvent> {

    @Override
    public Class<SampleSocialUserEvent> eventToIntercept() {
        return SampleSocialUserEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return DefaultTypes.DUMMY_EVENT;
    }

    @Override
    public boolean shouldInterceptThisEvent(Object event) {
        if (event.getClass().getSimpleName().equals(eventToIntercept().getSimpleName())) {
            return true;
        }
        return false;
    }

    @Override
    public SocialActivitiesEvent toSocial(Object object) {
        SampleSocialUserEvent event = (SampleSocialUserEvent) object;
        return new SocialActivitiesEvent();
    }

    @Override
    public List<SocialCommandTypeFilter> getTimelineFilters() {
        ArrayList<SocialCommandTypeFilter> socialCommandTypeFilters = new ArrayList<SocialCommandTypeFilter>();
        socialCommandTypeFilters.add(new SocialCommandTypeFilter() {
            @Override
            public List<SocialActivitiesEvent> execute(String parameter,
                                                       List<SocialActivitiesEvent> events) {
                List<SocialActivitiesEvent> newList = new ArrayList<SocialActivitiesEvent>();
                Integer maxResults = new Integer(parameter);
                for (int i = 0; i < maxResults; i++) {
                    newList.add(events.get(i));
                }
                return newList;
            }

            @Override
            public String getCommandName() {
                return "max-results";
            }
        });

        socialCommandTypeFilters.add(new SocialCommandTypeFilter() {
            @Override
            public List<SocialActivitiesEvent> execute(String parameter,
                                                       List<SocialActivitiesEvent> events) {
                List<SocialActivitiesEvent> newList = new ArrayList<SocialActivitiesEvent>();
                for (SocialActivitiesEvent event : events) {
                    String name = event.getSocialUser().getUserName();
                    SocialUser socialUser = new SocialUser(name + " - filtered");
                    SocialActivitiesEvent socialEvent = new SocialActivitiesEvent(socialUser,
                                                                                  event.getType(),
                                                                                  new Date());
                    newList.add(socialEvent);
                }
                return newList;
            }

            @Override
            public String getCommandName() {
                return "another-filter";
            }
        });
        return socialCommandTypeFilters;
    }

    @Override
    public List<String> getTimelineFiltersNames() {
        List<String> names = new ArrayList<String>();
        names.add("max-results");
        names.add("another-filter");
        return names;
    }
}
