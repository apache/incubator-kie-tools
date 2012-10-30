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
package org.uberfire.client.workbench.widgets.popups.activities.multiple;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartBeforeCloseEvent;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 *
 */
@ApplicationScoped
@WorkbenchPopup(identifier = "workbench.activities.multiple")
public class MultipleActivitiesFoundPresenter {

    public interface View
            extends
            UberView<MultipleActivitiesFoundPresenter> {

        void setRequestedPlaceIdentifier( final String requestedPlaceIdentifier );

    }

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<WorkbenchPartBeforeCloseEvent> closePlaceEvent;

    private PlaceRequest place;

    @PostConstruct
    public void init() {
        view.init( this );
    }

    @OnStart
    public void onStart( final PlaceRequest place ) {
        this.place = place;
    }

    @OnReveal
    public void onReveal() {
        final String requestedPlaceIdentifier = placeManager.getCurrentPlaceRequest().getParameter( "requestedPlaceIdentifier",
                                                                                                    null );
        view.setRequestedPlaceIdentifier( requestedPlaceIdentifier );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Multiple activities detected";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public void close() {
        closePlaceEvent.fire( new WorkbenchPartBeforeCloseEvent( this.place ) );
    }

}
