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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialUser;

public class SocialTimeLineRepositoryUnitTestWrapper extends SocialTimeLineRepository {

    private HashMap<String, List<SocialActivitiesEvent>> mockMap = new HashMap<String, List<SocialActivitiesEvent>>();

    public SocialTimeLineRepositoryUnitTestWrapper() {
    }

    @Override
    public List<SocialActivitiesEvent> getLastUserTimeline(SocialUser user,
                                                           Map commandsMap) {
        if (mockMap.get(user.getUserName()) == null) {
            mockMap.put(user.getUserName(),
                        new ArrayList());
        }
        return mockMap.get(user.getUserName());
    }

    @Override
    public List<SocialActivitiesEvent> getLastEventTimeline(String type,
                                                            Map commandsMap) {
        if (mockMap.get(type) == null) {
            mockMap.put(type,
                        new ArrayList());
        }
        return mockMap.get(type);
    }

    @Override
    public void saveTypeEvent(SocialActivitiesEvent event) {
        if (mockMap.get(event.getType()) == null) {
            mockMap.put(event.getType(),
                        new ArrayList());
        }
        List<SocialActivitiesEvent> socialActivitiesEvents = mockMap.get(event.getType());
        socialActivitiesEvents.add(event);
        mockMap.put(event.getType(),
                    socialActivitiesEvents);
    }

    @Override
    public void saveUserEvent(SocialActivitiesEvent event) {
        String userName = event.getSocialUser().getUserName();
        if (mockMap.get(userName) == null) {
            mockMap.put(userName,
                        new ArrayList());
        }
        List<SocialActivitiesEvent> socialActivitiesEvents = mockMap.get(userName);
        socialActivitiesEvents.add(event);
        mockMap.put(userName,
                    socialActivitiesEvents);
    }
}
