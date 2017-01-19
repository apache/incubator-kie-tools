/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.discussion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialCommandTypeFilter;
import org.ext.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.kie.workbench.common.services.shared.discussion.CommentAddedEvent;
import org.kie.workbench.common.services.shared.discussion.DiscussionEventTypes;

@ApplicationScoped
public class DiscussionAddedEventAdapter
        implements SocialAdapter<CommentAddedEvent> {

    @Inject
    private SocialUserRepositoryAPI socialUserRepositoryAPI;

    @Override
    public Class<CommentAddedEvent> eventToIntercept() {
        return CommentAddedEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return DiscussionEventTypes.COMMENT_ADDED;
    }

    @Override
    public boolean shouldInterceptThisEvent(Object event) {
        if (event.getClass().getSimpleName().equals(eventToIntercept().getSimpleName())) {
            return true;
        } else {
            return false;
        }
    }

    public void howdy(@Observes CommentAddedEvent event) {
        /*
        Since we do not have any other places that observe this event.
        The event for some weird reason never shows up in the server side if we don't add this mock listener.
         */
    }

    @Override
    public SocialActivitiesEvent toSocial(Object object) {
        CommentAddedEvent event = (CommentAddedEvent) object;
        SocialUser socialUser = socialUserRepositoryAPI.findSocialUser(event.getUserName());
        String additionalInfo = "commented";
        return new SocialActivitiesEvent(
                socialUser,
                DiscussionEventTypes.COMMENT_ADDED.name(),
                new Date(event.getTimestamp())
        ).withLink( event.getPath().getFileName(), event.getPath().toURI() )
                .withAdicionalInfo(additionalInfo);
    }

    @Override
    public List<SocialCommandTypeFilter> getTimelineFilters() {
        return new ArrayList<SocialCommandTypeFilter>();
    }

    @Override
    public List<String> getTimelineFiltersNames() {
        return new ArrayList<String>();
    }
}
