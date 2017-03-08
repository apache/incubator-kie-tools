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

import org.ext.uberfire.social.activities.model.PagedSocialQuery;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialPaged;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialPredicate;
import org.ext.uberfire.social.activities.service.SocialUserTimelinePagedRepositoryAPI;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class SocialUserTimelinePagedRepository extends SocialPageRepository implements SocialUserTimelinePagedRepositoryAPI {

    @Override
    public PagedSocialQuery getUserTimeline(SocialUser socialUser,
                                            SocialPaged socialPaged) {
        return getUserTimeline(socialUser,
                               socialPaged,
                               new HashMap(),
                               null);
    }

    @Override
    public PagedSocialQuery getUserTimeline(SocialUser socialUser,
                                            SocialPaged socialPaged,
                                            SocialPredicate<SocialActivitiesEvent> predicate) {
        return getUserTimeline(socialUser,
                               socialPaged,
                               new HashMap(),
                               predicate);
    }

    @Override
    public PagedSocialQuery getUserTimeline(SocialUser socialUser,
                                            SocialPaged socialPaged,
                                            Map commandsMap,
                                            SocialPredicate<SocialActivitiesEvent> predicate) {

        List<SocialActivitiesEvent> userEvents = new ArrayList<SocialActivitiesEvent>();

        socialPaged = setupQueryDirection(socialPaged);

        if (socialPaged.isANewQuery()) {
            socialPaged = searchForRecentEvents(socialUser,
                                                socialPaged,
                                                userEvents,
                                                predicate);
        }
        if (!foundEnoughtEvents(socialPaged,
                                userEvents)) {
            socialPaged = searchForStoredEvents(socialUser,
                                                socialPaged,
                                                userEvents,
                                                predicate);
        }

        userEvents = filterTimelineWithAdapters(commandsMap,
                                                userEvents);

        checkIfICanGoForward(socialPaged,
                             userEvents);

        PagedSocialQuery query = new PagedSocialQuery(userEvents,
                                                      socialPaged);

        return query;
    }

    private SocialPaged searchForStoredEvents(SocialUser socialUser,
                                              SocialPaged socialPaged,
                                              List<SocialActivitiesEvent> events,
                                              SocialPredicate<SocialActivitiesEvent> predicate) {
        if (socialPaged.firstFileRead()) {
            readMostRecentFile(socialUser,
                               socialPaged,
                               events,
                               predicate);
        } else {
            readCurrentFile(socialUser,
                            socialPaged,
                            events,
                            predicate);
        }
        if (!foundEnoughtEvents(socialPaged,
                                events) && shouldIReadMoreFiles(socialPaged)) {
            readMoreFiles(socialPaged,
                          socialUser,
                          events,
                          predicate);
        }
        return socialPaged;
    }

    private boolean shouldIReadMoreFiles(SocialPaged socialPaged) {
        return socialPaged.lastFileReaded() != null && !socialPaged.lastFileReaded().isEmpty() && thereIsMoreFilesToRead(socialPaged.lastFileReaded());
    }

    private void readMoreFiles(SocialPaged socialPaged,
                               SocialUser socialUser,
                               List<SocialActivitiesEvent> events,
                               SocialPredicate<SocialActivitiesEvent> predicate) {
        String nextFileToRead = socialPaged.getNextFileToRead();
        if (thereIsMoreFilesToRead(nextFileToRead)) {
            addEventsToTimeline(socialUser,
                                socialPaged,
                                events,
                                nextFileToRead,
                                predicate);
            if (!foundEnoughtEvents(socialPaged,
                                    events)) {
                readMoreFiles(socialPaged,
                              socialUser,
                              events,
                              predicate);
            }
        }
    }

    private void readCurrentFile(SocialUser socialUser,
                                 SocialPaged socialPaged,
                                 List<SocialActivitiesEvent> events,
                                 SocialPredicate<SocialActivitiesEvent> predicate) {
        String lastFileReaded = socialPaged.lastFileReaded();
        addEventsToTimeline(socialUser,
                            socialPaged,
                            events,
                            lastFileReaded,
                            predicate);
    }

    private void addEventsToTimeline(SocialUser socialUser,
                                     SocialPaged socialPaged,
                                     List<SocialActivitiesEvent> events,
                                     String lastFileReaded,
                                     SocialPredicate<SocialActivitiesEvent> predicate) {
        List<SocialActivitiesEvent> timeline = getSocialTimelinePersistenceAPI().getTimeline(socialUser,
                                                                                             lastFileReaded);
        List<SocialActivitiesEvent> filteredList = filterList(predicate,
                                                              timeline);
        setNumberOfEventsOnFile(socialPaged,
                                socialUser,
                                lastFileReaded);
        addEvents(socialPaged,
                  events,
                  filteredList);
    }

    private void setNumberOfEventsOnFile(SocialPaged socialPaged,
                                         SocialUser socialUser,
                                         String lastFileReaded) {
        socialPaged.setNumberOfEventsOnFile(getSocialTimelinePersistenceAPI().getNumberOfEventsOnFile(socialUser,
                                                                                                      lastFileReaded));
    }

    private void readMostRecentFile(SocialUser socialUser,
                                    SocialPaged socialPaged,
                                    List<SocialActivitiesEvent> events,
                                    SocialPredicate<SocialActivitiesEvent> predicate) {
        Integer userMostRecentFileIndex = getSocialTimelinePersistenceAPI().getUserMostRecentFileIndex(socialUser);
        if (thereIsNothingToRead(userMostRecentFileIndex)) {
            return;
        }
        List<SocialActivitiesEvent> timeline = getSocialTimelinePersistenceAPI().getTimeline(socialUser,
                                                                                             userMostRecentFileIndex.toString());
        List<SocialActivitiesEvent> filteredList = filterList(predicate,
                                                              timeline);
        socialPaged.setLastFileReaded(userMostRecentFileIndex.toString());
        readEvents(socialPaged,
                   events,
                   filteredList);
    }

    private boolean thereIsNothingToRead(Integer userMostRecentFileIndex) {
        return userMostRecentFileIndex < 0;
    }

    private SocialPaged searchForRecentEvents(SocialUser socialUser,
                                              SocialPaged socialPaged,
                                              List<SocialActivitiesEvent> events,
                                              SocialPredicate<SocialActivitiesEvent> predicate) {
        List<SocialActivitiesEvent> freshEvents = getSocialTimelinePersistenceAPI().getRecentEvents(socialUser);
        List<SocialActivitiesEvent> filteredList = filterList(predicate,
                                                              freshEvents);
        Collections.reverse(filteredList);
        searchEvents(socialPaged,
                     events,
                     filteredList);
        return socialPaged;
    }
}
