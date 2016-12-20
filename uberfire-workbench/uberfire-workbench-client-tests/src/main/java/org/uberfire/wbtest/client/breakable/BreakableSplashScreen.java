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

package org.uberfire.wbtest.client.breakable;

import java.util.Collections;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.ActivityLifecycleError.LifecyclePhase;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.splash.SplashView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestSplashScreenActivity;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.workbench.model.impl.SplashScreenFilterImpl;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

@Dependent
@Named("org.uberfire.wbtest.client.breakable.BreakableSplashScreen")
public class BreakableSplashScreen extends AbstractTestSplashScreenActivity {

    private LifecyclePhase brokenLifecycle;
    private final Label widget = new Label( "Not started" );

    @Inject
    public BreakableSplashScreen( PlaceManager placeManager, SplashView view ) {
        super( placeManager, view );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        String brokenParam = place.getParameter( "splash.broken", null );
        if ( brokenParam != null && brokenParam.length() > 0 ) {
            brokenLifecycle = LifecyclePhase.valueOf( brokenParam );
        }

        if ( brokenParam == null ) {
            widget.setText( "Splash Screen with no broken methods" );
        } else {
            widget.setText( "Splash Screen with broken " + brokenLifecycle + " method" );
        }

        if ( brokenLifecycle == LifecyclePhase.STARTUP ) {
            throw new RuntimeException( "This screen has a broken startup callback" );
        }
    }

    @Override
    public void onOpen() {
        super.onOpen();
        if ( brokenLifecycle == LifecyclePhase.OPEN ) {
            throw new RuntimeException( "This splash screen has a broken open callback" );
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if ( brokenLifecycle == LifecyclePhase.CLOSE ) {
            throw new RuntimeException( "This splash screen has a broken close callback" );
        }
    }

    @Override
    public void onShutdown() {
        super.onShutdown();
        if ( brokenLifecycle == LifecyclePhase.SHUTDOWN ) {
            throw new RuntimeException( "This splash screen has a broken shutdown callback" );
        }
    }

    @Override
    public IsWidget getWidget() {
        return widget;
    }

    @Override
    public Boolean intercept( PlaceRequest intercepted ) {
        return intercepted.getParameter( "breakable.splash", null ) != null;
    }

    @Override
    public SplashScreenFilter getFilter() {
        return new SplashScreenFilterImpl( "ThisFilterNotUsed", true, Collections.<String>emptyList() );
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
