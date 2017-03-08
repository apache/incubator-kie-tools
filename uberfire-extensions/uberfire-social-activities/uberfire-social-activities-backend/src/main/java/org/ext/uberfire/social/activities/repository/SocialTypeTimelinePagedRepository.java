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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.PagedSocialQuery;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialPaged;
import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialPredicate;
import org.ext.uberfire.social.activities.service.SocialRouterAPI;
import org.ext.uberfire.social.activities.service.SocialTypeTimelinePagedRepositoryAPI;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class SocialTypeTimelinePagedRepository extends SocialPageRepository implements SocialTypeTimelinePagedRepositoryAPI {

    @Inject
    SocialRouterAPI socialRouterAPI;

    @Override
    public PagedSocialQuery getEventTimeline(String adapterName,
                                             SocialPaged socialPage) {
        return getEventTimeline(adapterName,
                                socialPage,
                                new HashMap(),
                                null);
    }

    @Override
    public PagedSocialQuery getEventTimeline(SocialAdapter adapter,
                                             SocialPaged socialPaged) {
        return getEventTimeline(adapter,
                                socialPaged,
                                new HashMap(),
                                null);
    }

    @Override
    public PagedSocialQuery getEventTimeline(String adapterName,
                                             SocialPaged socialPage,
                                             SocialPredicate<SocialActivitiesEvent> predicate) {
        return getEventTimeline(adapterName,
                                socialPage,
                                new HashMap(),
                                predicate);
    }

    @Override
    public PagedSocialQuery getEventTimeline(String adapterName,
                                             SocialPaged socialPaged,
                                             Map commandsMap) {
        SocialAdapter socialAdapter = getSocialAdapter(adapterName);

        return getEventTimeline(socialAdapter,
                                socialPaged,
                                commandsMap,
                                null);
    }

    @Override
    public PagedSocialQuery getEventTimeline(String adapterName,
                                             SocialPaged socialPaged,
                                             Map commandsMap,
                                             SocialPredicate<SocialActivitiesEvent> predicate) {
        SocialAdapter socialAdapter = getSocialAdapter(adapterName);

        return getEventTimeline(socialAdapter,
                                socialPaged,
                                commandsMap,
                                predicate);
    }

    @Override
    public PagedSocialQuery getEventTimeline(SocialAdapter adapter,
                                             SocialPaged socialPaged,
                                             Map commandsMap,
                                             SocialPredicate<SocialActivitiesEvent> predicate) {

        socialPaged = setupQueryDirection(socialPaged);

        List<SocialActivitiesEvent> typeEvents = new ArrayList<SocialActivitiesEvent>();

        if (socialPaged.isANewQuery()) {
            socialPaged = searchForRecentEvents(adapter.socialEventType(),
                                                socialPaged,
                                                typeEvents,
                                                predicate);
        }
        if (!foundEnoughtEvents(socialPaged,
                                typeEvents)) {
            socialPaged = searchForStoredEvents(adapter.socialEventType(),
                                                socialPaged,
                                                typeEvents,
                                                predicate);
        }

        typeEvents = filterTimelineWithAdapters(commandsMap,
                                                typeEvents);

        checkIfICanGoForward(socialPaged,
                             typeEvents);

        PagedSocialQuery query = new PagedSocialQuery(typeEvents,
                                                      socialPaged);

        return query;
    }

    SocialAdapter getSocialAdapter(String adapterName) {
        return socialRouterAPI.getSocialAdapter(adapterName);
    }

    private SocialPaged searchForStoredEvents(SocialEventType type,
                                              SocialPaged socialPaged,
                                              List<SocialActivitiesEvent> events,
                                              SocialPredicate<SocialActivitiesEvent> predicate) {
        if (socialPaged.firstFileRead()) {
            readMostRecentFile(type,
                               socialPaged,
                               events,
                               predicate);
        } else {
            readCurrentFile(type,
                            socialPaged,
                            events,
                            predicate);
        }
        if (!foundEnoughtEvents(socialPaged,
                                events) && shouldIReadMoreFiles(socialPaged)) {
            readMoreFiles(socialPaged,
                          type,
                          events,
                          predicate);
        }
        return socialPaged;
    }

    private boolean shouldIReadMoreFiles(SocialPaged socialPaged) {
        return socialPaged.lastFileReaded() != null && !socialPaged.lastFileReaded().isEmpty() && thereIsMoreFilesToRead(socialPaged.lastFileReaded());
    }

    private void readMoreFiles(SocialPaged socialPaged,
                               SocialEventType type,
                               List<SocialActivitiesEvent> events,
                               SocialPredicate<SocialActivitiesEvent> predicate) {
        String nextFileToRead = socialPaged.getNextFileToRead();
        if (thereIsMoreFilesToRead(nextFileToRead)) {
            addEventsToTimeline(type,
                                socialPaged,
                                events,
                                nextFileToRead,
                                predicate);
            if (!foundEnoughtEvents(socialPaged,
                                    events)) {
                readMoreFiles(socialPaged,
                              type,
                              events,
                              predicate);
            }
        }
        checkIfICanGoForward(socialPaged,
                             events);
    }

    private void readCurrentFile(SocialEventType type,
                                 SocialPaged socialPaged,
                                 List<SocialActivitiesEvent> events,
                                 SocialPredicate<SocialActivitiesEvent> predicate) {
        String lastFileReaded = socialPaged.lastFileReaded();
        addEventsToTimeline(type,
                            socialPaged,
                            events,
                            lastFileReaded,
                            predicate);
    }

    private void addEventsToTimeline(SocialEventType type,
                                     SocialPaged socialPaged,
                                     List<SocialActivitiesEvent> events,
                                     String lastFileReaded,
                                     SocialPredicate<SocialActivitiesEvent> predicate) {
        List<SocialActivitiesEvent> timeline = getSocialTimelinePersistenceAPI().getTimeline(type,
                                                                                             lastFileReaded);
        List<SocialActivitiesEvent> filteredList = filterList(predicate,
                                                              timeline);
        setNumberOfEventsOnFile(socialPaged,
                                type,
                                lastFileReaded);
        addEvents(socialPaged,
                  events,
                  filteredList);
    }

    private void setNumberOfEventsOnFile(SocialPaged socialPaged,
                                         SocialEventType type,
                                         String lastFileReaded) {
        socialPaged.setNumberOfEventsOnFile(getSocialTimelinePersistenceAPI().getNumberOfEventsOnFile(type,
                                                                                                      lastFileReaded));
    }

    private void readMostRecentFile(SocialEventType type,
                                    SocialPaged socialPaged,
                                    List<SocialActivitiesEvent> events,
                                    SocialPredicate<SocialActivitiesEvent> predicate) {
        Integer userMostRecentFileIndex = getSocialTimelinePersistenceAPI().getTypeMostRecentFileIndex(type);
        List<SocialActivitiesEvent> timeline = getSocialTimelinePersistenceAPI().getTimeline(type,
                                                                                             userMostRecentFileIndex.toString());
        List<SocialActivitiesEvent> filteredList = filterList(predicate,
                                                              timeline);
        socialPaged.setLastFileReaded(userMostRecentFileIndex.toString());
        readEvents(socialPaged,
                   events,
                   filteredList);
    }

    private SocialPaged searchForRecentEvents(SocialEventType type,
                                              SocialPaged socialPaged,
                                              List<SocialActivitiesEvent> events,
                                              SocialPredicate<SocialActivitiesEvent> predicate) {
        List<SocialActivitiesEvent> freshEvents = getSocialTimelinePersistenceAPI().getRecentEvents(type);
        List<SocialActivitiesEvent> filteredList = filterList(predicate,
                                                              freshEvents);
        Collections.reverse(filteredList);
        searchEvents(socialPaged,
                     events,
                     filteredList);
        return socialPaged;
    }
}
