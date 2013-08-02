/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.widgets.popups.activities.notfound;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

/**
 * Popup presenter for when an Activity cannot be found
 */
@ApplicationScoped
@WorkbenchPopup(identifier = "workbench.activity.notfound")
public class ActivityNotFoundPresenter {

    public interface View
            extends
            UberView<ActivityNotFoundPresenter> {

        void setRequestedPlaceIdentifier( final String requestedPlaceIdentifier );

    }

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {
        final String identifier = place.getParameter( "requestedPlaceIdentifier", null );
        view.setRequestedPlaceIdentifier( identifier );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Activity not found";
    }

    @WorkbenchPartView
    public UberView<ActivityNotFoundPresenter> getView() {
        return view;
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }

}
