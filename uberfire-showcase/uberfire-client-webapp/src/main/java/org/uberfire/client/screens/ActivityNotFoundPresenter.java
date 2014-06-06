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
package org.uberfire.client.screens;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

/**
 * Popup presenter for when an Activity cannot be found.
 * <p>
 * XXX this class is here temporarily (until UF-100 gets fixed).
 */
@ApplicationScoped
@WorkbenchPopup(identifier = "workbench.activity.notfound")
public class ActivityNotFoundPresenter {

    private PlaceRequest place;

    private final Label view = new Label("Activity not found");

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {
        final String identifier = place.getParameter( "requestedPlaceIdentifier", null );
        view.setText( "Activity not found: " + identifier );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Not Found";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

}
