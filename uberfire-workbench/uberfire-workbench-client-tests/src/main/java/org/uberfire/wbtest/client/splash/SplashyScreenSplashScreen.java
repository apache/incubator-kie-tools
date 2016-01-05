/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.wbtest.client.splash;

import static java.util.Arrays.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.IsSplashScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.splash.SplashView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestSplashScreenActivity;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.workbench.model.impl.SplashScreenFilterImpl;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

@ApplicationScoped
@Named("org.uberfire.wbtest.client.splash.SplashyScreenSplashScreen")
@IsSplashScreen
public class SplashyScreenSplashScreen extends AbstractTestSplashScreenActivity {

    Label view = new Label( "Not started" );

    @Inject
    public SplashyScreenSplashScreen( final PlaceManager placeManager,
                                      final SplashView view ) {
        super( placeManager, view );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        String debugId = place.getParameter( "debugId", null );
        if ( debugId != null ) {
            view.getElement().setId( "SplashyScreenSplashScreen-" + debugId );
            view.setText( "Splash Screen for " + debugId );
        }
    }

    @Override
    public IsWidget getWidget() {
        return view;
    }

    @Override
    public SplashScreenFilter getFilter() {
        return new SplashScreenFilterImpl( getClass().getName(), true, asList( SplashyScreen.class.getName() ) );
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
