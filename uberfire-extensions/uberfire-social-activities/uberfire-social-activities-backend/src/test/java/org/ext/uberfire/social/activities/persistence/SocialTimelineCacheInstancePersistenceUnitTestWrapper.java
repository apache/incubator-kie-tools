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

package org.ext.uberfire.social.activities.persistence;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import org.ext.uberfire.social.activities.model.DefaultTypes;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.repository.SocialEventTypeRepository;
import org.ext.uberfire.social.activities.security.SocialSecurityConstraintsManager;
import org.uberfire.io.IOService;

public class SocialTimelineCacheInstancePersistenceUnitTestWrapper extends SocialTimelineCacheInstancePersistence {

    public SocialTimelineCacheInstancePersistenceUnitTestWrapper() {
        super(null,
              null,
              null,
              null,
              null,
              null,
              null,
              null);
    }

    public SocialTimelineCacheInstancePersistenceUnitTestWrapper(SocialSecurityConstraintsManager socialSecurityConstraintsManager) {
        super(null,
              null,
              null,
              null,
              null,
              null,
              null,
              socialSecurityConstraintsManager);
    }

    public SocialTimelineCacheInstancePersistenceUnitTestWrapper(Gson gson,
                                                                 Type gsonCollectionType,
                                                                 IOService ioService,
                                                                 SocialEventTypeRepository socialEventTypeRepository,
                                                                 SocialUserClusterPersistence socialUserService) {
        super(gson,
              gsonCollectionType,
              ioService,
              socialEventTypeRepository,
              socialUserService,
              null,
              null,
              null);
    }

    @Override
    void cacheControl(SocialUser user) {
    }

    @Override
    void cacheControl(SocialActivitiesEvent event) {
    }

    @Override
    public Integer getUserMostRecentFileIndex(SocialUser user) {
        return 5;
    }

    public Integer getTypeMostRecentFileIndex(SocialEventType type) {
        return 5;
    }

    SocialEventType findType(SocialActivitiesEvent event) {
        return DefaultTypes.DUMMY_EVENT;
    }

    @Override
    public List<SocialActivitiesEvent> getTimeline(SocialUser socialUser,
                                                   String timelineFile) {

        return createFakeTimeline(timelineFile);
    }

    @Override
    public List<SocialActivitiesEvent> getTimeline(SocialEventType type,
                                                   String timelineFile) {

        return createFakeTimeline(timelineFile);
    }

    private List<SocialActivitiesEvent> createFakeTimeline(String timelineFile) {
        if (timelineFile.equals("5")) {
            return createTimeline("5",
                                  5);
        }
        if (timelineFile.equals("4")) {
            return createTimeline("4",
                                  5);
        }
        if (timelineFile.equals("3")) {
            return createTimeline("3",
                                  5);
        }
        if (timelineFile.equals("2")) {
            return createTimeline("2",
                                  5);
        }
        if (timelineFile.equals("1")) {
            //empty timeline
            return new ArrayList<SocialActivitiesEvent>();
        }
        if (timelineFile.equals("0")) {
            return createTimeline("0",
                                  5);
        }
        throw new RuntimeException();
    }

    private List<SocialActivitiesEvent> createTimeline(String name,
                                                       int numEvents) {
        List<SocialActivitiesEvent> events = new ArrayList<SocialActivitiesEvent>();
        for (int i = 0; i < numEvents; i++) {
            events.add(new SocialActivitiesEvent(new SocialUser(name),
                                                 DefaultTypes.DUMMY_EVENT,
                                                 new Date()).withAdicionalInfo(i + ""));
        }
        return events;
    }

    @Override
    public Integer getNumberOfEventsOnFile(SocialUser socialUser,
                                           String originalFilename) {
        return -1;
    }

    @Override
    public Integer getNumberOfEventsOnFile(SocialEventType type,
                                           String originalFilename) {
        return -1;
    }
}
