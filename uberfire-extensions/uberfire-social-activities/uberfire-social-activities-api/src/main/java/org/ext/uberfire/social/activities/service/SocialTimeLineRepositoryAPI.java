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
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialUser;

@Remote
public interface SocialTimeLineRepositoryAPI {

    List<SocialActivitiesEvent> getLastEventTimeline( String adapterName );


    List<SocialActivitiesEvent> getLastEventTimeline( String adapterName,
                                                      Map commandsMap );

    public List<SocialActivitiesEvent> getLastEventTimeline( SocialAdapter type,
                                                             Map commandsMap );

    public void saveTypeEvent( SocialActivitiesEvent event );

    void saveUserEvent( SocialActivitiesEvent event );

    List<SocialActivitiesEvent> getLastUserTimeline( SocialUser user,
                                                     SocialPredicate<SocialActivitiesEvent> predicate );

    List<SocialActivitiesEvent> getLastUserTimeline( SocialUser user,
                                                     Map parameterMap );

    List<SocialActivitiesEvent> getLastUserTimeline( SocialUser user );

    Integer numberOfPages( SocialEventType type );
}
