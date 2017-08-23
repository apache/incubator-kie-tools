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

package org.kie.workbench.common.services.backend.social;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.PagedSocialQuery;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialPaged;
import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialAdapterRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialTimeLineRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialTimelineRulesQueryAPI;
import org.ext.uberfire.social.activities.service.SocialTypeTimelinePagedRepositoryAPI;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

@Service
@ApplicationScoped
public class SocialTimelineRulesQuery implements SocialTimelineRulesQueryAPI {

    private SocialTimeLineRepositoryAPI socialTimeLineRepositoryAPI;

    private SocialTypeTimelinePagedRepositoryAPI socialTypeTimelinePagedRepositoryAPI;

    private SocialAdapterRepositoryAPI socialAdapterRepositoryAPI;

    public SocialTimelineRulesQuery() {
    }

    @Inject
    public SocialTimelineRulesQuery(SocialTimeLineRepositoryAPI socialTimeLineRepositoryAPI,
                                    SocialTypeTimelinePagedRepositoryAPI socialTypeTimelinePagedRepositoryAPI,
                                    SocialAdapterRepositoryAPI socialAdapterRepositoryAPI) {
        this.socialTimeLineRepositoryAPI = socialTimeLineRepositoryAPI;
        this.socialTypeTimelinePagedRepositoryAPI = socialTypeTimelinePagedRepositoryAPI;
        this.socialAdapterRepositoryAPI = socialAdapterRepositoryAPI;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SocialActivitiesEvent> executeAllRules() {

        List<SocialActivitiesEvent> events = new ArrayList<>();
        try {

            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();

            KieSession kSession = kContainer.newKieSession("social-session");
            List<SocialActivitiesEvent> socialEvents = new ArrayList<>();
            kSession.setGlobal("socialEvents",
                               socialEvents);
            kSession.setGlobal("maxResults",
                               Integer.MAX_VALUE);
            kSession.setGlobal("queryAPI",
                               this);
            kSession.fireAllRules();

            events = (List<SocialActivitiesEvent>) kSession.getGlobal("socialEvents");
        } catch (Exception e) {
            throw new RulesExecutionQueryException(e);
        }
        return events;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SocialActivitiesEvent> executeSpecificRule(Map<String, String> globals,
                                                           final String drlName,
                                                           String maxResults) {

        List<SocialActivitiesEvent> events = new ArrayList<>();
        try {

            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();

            KieSession kSession = kContainer.newKieSession("social-session");
            List<SocialActivitiesEvent> socialEvents = new ArrayList<>();
            kSession.setGlobal("socialEvents",
                               socialEvents);
            kSession.setGlobal("queryAPI",
                               this);
            kSession.setGlobal("maxResults",
                               new Integer(maxResults));
            for (String key : globals.keySet()) {
                kSession.setGlobal(key,
                                   globals.get(key));
            }
            kSession.fireAllRules(new AgendaFilter() {
                @Override
                public boolean accept(Match match) {
                    String rulename = match.getRule().getName();

                    if (rulename.equals(drlName)) {
                        return true;
                    }

                    return false;
                }
            });

            events = (List<SocialActivitiesEvent>) kSession.getGlobal("socialEvents");
        } catch (Exception e) {
            throw new RulesExecutionQueryException(e);
        }
        return events;
    }

    @Override
    public List<SocialActivitiesEvent> getAllCached() {

        List<SocialActivitiesEvent> events = new ArrayList<>();
        Map<Class, SocialAdapter> socialAdapters = socialAdapterRepositoryAPI.getSocialAdapters();

        for (SocialAdapter adapter : socialAdapters.values()) {
            events.addAll(socialTimeLineRepositoryAPI.getLastEventTimeline(adapter,
                                                                           new HashMap()));
        }

        return events;
    }

    @Override
    public List<SocialActivitiesEvent> getTypeCached(String... typeNames) {

        List<SocialActivitiesEvent> events = new ArrayList<>();

        for (String type : typeNames) {
            events.addAll(socialTimeLineRepositoryAPI.getLastEventTimeline(type,
                                                                           new HashMap()));
        }

        return events;
    }

    @Override
    public List<SocialActivitiesEvent> getNEventsFromEachType(int numberOfEvents,
                                                              String... typeNames) {
        List<SocialActivitiesEvent> events = new ArrayList<>();

        for (String type : typeNames) {
            PagedSocialQuery query = socialTypeTimelinePagedRepositoryAPI
                    .getEventTimeline(type,
                                      new SocialPaged(numberOfEvents),
                                      new HashMap());
            events.addAll(query.socialEvents());
        }

        return events;
    }

    class RulesExecutionQueryException extends RuntimeException {

        public RulesExecutionQueryException(Exception e) {
            super(e);
        }
    }
}
