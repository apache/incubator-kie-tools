/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.screens.miscfeatures;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;

/**
 * A stand-alone Presenter annotated to hook into the Workbench
 */
@WorkbenchScreen(identifier = "MiscellaneousFeatures")
public class MiscFeaturesPresenter {

    public interface View
            extends
            UberView<MiscFeaturesPresenter> {

    }

    private String title = "Miscellaneous features";

    @Inject
    public View view;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    @Inject
    private PlaceManager placeManager;

    private PlaceRequest placeRequest;

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.placeRequest = placeRequest;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

    @WorkbenchPartView
    public UberView<MiscFeaturesPresenter> getView() {
        return view;
    }

    public void launchUnknownPlace() {
        final PlaceRequest place = new DefaultPlaceRequest( "somewhere.that.does.not.exist" );
        placeManager.goTo( place );
    }

    public void setNewTitle( final String newCoolTitle ) {
        title = "Cool!";
        changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( placeRequest, title, null ) );
    }

}