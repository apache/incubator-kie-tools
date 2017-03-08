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

package org.ext.uberfire.social.activities.service;

import java.util.List;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.uberfire.commons.lifecycle.PriorityDisposable;

public interface SocialTimelinePersistenceAPI extends PriorityDisposable {

    void setup();

    List<SocialActivitiesEvent> getLastEvents(SocialEventType type);

    List<SocialActivitiesEvent> getTimeline(SocialEventType type,
                                            String timelineFile);

    List<SocialActivitiesEvent> getLastEvents(SocialUser user);

    List<SocialActivitiesEvent> getRecentEvents(SocialUser user);

    void persist(SocialActivitiesEvent event);

    void persist(SocialUser user,
                 SocialActivitiesEvent event);

    Integer numberOfPages(SocialEventType type);

    List<SocialActivitiesEvent> getTimeline(SocialUser socialUser,
                                            String timelineFile);

    List<SocialActivitiesEvent> getRecentEvents(SocialEventType type);

    Integer getUserMostRecentFileIndex(SocialUser user);

    Integer getTypeMostRecentFileIndex(SocialEventType type);

    void saveAllEvents();

    Integer getNumberOfEventsOnFile(SocialEventType type,
                                    String file);

    Integer getNumberOfEventsOnFile(SocialUser socialUser,
                                    String file);
}
