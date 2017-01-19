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

import org.ext.uberfire.social.activities.client.widgets.pagination.Next;
import org.ext.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.ext.uberfire.social.activities.model.SocialPaged;
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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@ApplicationScoped
@WorkbenchScreen( identifier = "SimpleTimelinePresenter" )
public class SimpleTimelinePresenter {

    public interface View extends UberElement<SimpleTimelinePresenter> {

        void setupWidget( SimpleSocialTimelineWidgetModel model );
    }

    @Inject
    private View view;

    @Inject
    private User loggedUser;

    @Inject
    PlaceManager placeManager;

    @Inject
    private Caller<SocialUserRepositoryAPI> socialUserRepositoryAPI;

    @Inject
    private Event<ShowcaseSocialUserEvent> event;

    @OnOpen
    public void onOpen() {
        updateTimeline();
    }

    public void fireEvent() {
        event.fire( new ShowcaseSocialUserEvent( loggedUser.getIdentifier() ) );
        updateTimeline();
    }

    public void updateTimeline() {
        final SocialPaged socialPaged = new SocialPaged( 5 );
        socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
            public void callback( SocialUser socialUser ) {
                SimpleSocialTimelineWidgetModel model = new SimpleSocialTimelineWidgetModel( socialUser,
                                                                                             null,
                                                                                             placeManager,
                                                                                             socialPaged )
                        .withOnlyMorePagination( new Next() {{
                            setText( ">" );
                        }} );
                view.setupWidget( model );
            }
        } ).findSocialUser( loggedUser.getIdentifier() );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Simple TimelineScreen";
    }

    @WorkbenchPartView
    public UberElement<SimpleTimelinePresenter> getView() {
        return view;
    }

}
