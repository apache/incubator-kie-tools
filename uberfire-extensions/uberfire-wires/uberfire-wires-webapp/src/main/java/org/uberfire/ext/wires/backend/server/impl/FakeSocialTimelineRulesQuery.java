/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.backend.server.impl;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.service.SocialTimelineRulesQueryAPI;
import org.jboss.errai.bus.server.annotations.Service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@ApplicationScoped
public class FakeSocialTimelineRulesQuery implements SocialTimelineRulesQueryAPI {

    private List<SocialActivitiesEvent> events = new ArrayList<>();


    public void onEvent( @Observes SocialActivitiesEvent event ) {
        events.add( event );
    }

    @Override
    public List<SocialActivitiesEvent> executeAllRules() {
        return events;
    }

    @Override
    public List<SocialActivitiesEvent> executeSpecificRule( Map<String, String> globals,
                                                            final String drlName,
                                                            String maxResults ) {
        return events;
    }

    @Override
    public List<SocialActivitiesEvent> getAllCached() {
        return events;
    }

    @Override
    public List<SocialActivitiesEvent> getTypeCached( String... typeNames ) {
        return events;
    }

    @Override
    public List<SocialActivitiesEvent> getNEventsFromEachType( int numberOfEvents,
                                                               String... typeNames ) {
        return events;
    }

}
