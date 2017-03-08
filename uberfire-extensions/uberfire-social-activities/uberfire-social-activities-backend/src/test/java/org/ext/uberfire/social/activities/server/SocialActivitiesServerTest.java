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

package org.ext.uberfire.social.activities.server;

import java.util.Date;
import java.util.HashMap;

import org.ext.uberfire.social.activities.model.DefaultTypes;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.repository.SocialTimeLineRepository;
import org.ext.uberfire.social.activities.repository.SocialTimeLineRepositoryUnitTestWrapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SocialActivitiesServerTest {

    SocialActivitiesServer server;
    SocialTimeLineRepository repository;

    @Before
    public void setup() {
        repository = new SocialTimeLineRepositoryUnitTestWrapper();
        server = new SocialActivitiesServer(repository);
    }

    @Test
    public void registerSocialActivity_retrieveByType() {
        SocialUser user = new SocialUser("user");
        String type = DefaultTypes.DUMMY_EVENT.name();
        SocialActivitiesEvent event = new SocialActivitiesEvent(user,
                                                                type,
                                                                new Date());
        assertTrue(repository.getLastEventTimeline(type,
                                                   new HashMap()).size() == 0);
        server.register(event);
        assertTrue(repository.getLastEventTimeline(type,
                                                   new HashMap()).size() == 1);
        server.register(event);
        assertTrue(repository.getLastEventTimeline(type,
                                                   new HashMap()).size() == 2);
    }

    @Test
    public void registerSocialActivity_retrieveByUser() {
        SocialUser user = new SocialUser("user");
        String type = DefaultTypes.DUMMY_EVENT.name();
        SocialActivitiesEvent event = new SocialActivitiesEvent(user,
                                                                type,
                                                                new Date());
        assertTrue(repository.getLastUserTimeline(user).size() == 0);
        server.register(event);
        assertTrue(repository.getLastUserTimeline(user).size() == 1);
        server.register(event);
        assertTrue(repository.getLastUserTimeline(user).size() == 2);
    }
}
