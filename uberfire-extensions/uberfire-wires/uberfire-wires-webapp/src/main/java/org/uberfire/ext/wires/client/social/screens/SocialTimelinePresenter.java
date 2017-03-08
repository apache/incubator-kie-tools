/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.wires.client.social.screens;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.client.widgets.timeline.regular.model.SocialTimelineWidgetModel;
import org.ext.uberfire.social.activities.model.DefaultTypes;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.wires.shared.social.ShowcaseSocialUserEvent;
import org.uberfire.lifecycle.OnOpen;

@ApplicationScoped
@WorkbenchScreen(identifier = "SocialTimelinePresenter")
public class SocialTimelinePresenter {

    @Inject
    PlaceManager placeManager;
    @Inject
    private View view;

    @Inject
    private User loggedUser;
    @Inject
    private Caller<SocialUserRepositoryAPI> socialUserRepositoryAPI;
    @Inject
    private Event<ShowcaseSocialUserEvent> event;

    @PostConstruct
    public void init() {
    }

    @OnOpen
    public void onOpen() {
        updateTimeline();
    }

    public void fireEvent() {
        event.fire(new ShowcaseSocialUserEvent(loggedUser.getIdentifier()));
        updateTimeline();
    }

    public void updateTimeline() {

        socialUserRepositoryAPI.call(new RemoteCallback<SocialUser>() {
            public void callback(SocialUser socialUser) {
                final SocialTimelineWidgetModel model = new SocialTimelineWidgetModel(DefaultTypes.DUMMY_EVENT,
                                                                                      socialUser,
                                                                                      placeManager)
                        .droolsQuery(null,
                                     "anyrule",
                                     null);
                view.setupWidget(model);
            }
        }).findSocialUser(loggedUser.getIdentifier());
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Social TimelineScreen";
    }

    @WorkbenchPartView
    public UberElement<SocialTimelinePresenter> getView() {
        return view;
    }

    public interface View extends UberElement<SocialTimelinePresenter> {

        void setupWidget(SocialTimelineWidgetModel model);
    }
}