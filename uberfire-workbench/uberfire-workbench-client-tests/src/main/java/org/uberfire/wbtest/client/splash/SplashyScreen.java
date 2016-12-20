/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.splash;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.menu.SplashScreenMenuPresenter;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

@Dependent
@Named("org.uberfire.wbtest.client.splash.SplashyScreen")
public class SplashyScreen extends AbstractTestScreenActivity {

    private final FlowPanel panel = new FlowPanel();
    private final Label label = new Label();
    private String debugId;

    @Inject
    private SplashScreenMenuPresenter splashList;

    @Inject
    public SplashyScreen( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        debugId = place.getParameter( "debugId", "default" );
        splashList.asWidget().getElement().setId( "SplashyScreen-" + debugId + "-SplashList" );
        panel.getElement().setId( "SplashyScreen-" + debugId );

        label.setText( "Splashy screen " + debugId );

        panel.add( splashList );
        panel.add( label );
    }

    @Override
    public String getTitle() {
        return "Splashy-" + debugId;
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }

}
