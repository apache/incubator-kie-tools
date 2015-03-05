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
package org.uberfire.client.splash;

import java.util.Collection;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.workbench.widgets.splash.SplashView;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.SplashScreenFilter;

public class JSSplashScreenActivity implements SplashScreenActivity {

    private Boolean showAgain;
    private Boolean isEnabled;
    private SplashScreenFilter splashFilter;

    private final JSNativeSplashScreen nativeSplashScreen;
    private PlaceRequest place;
    private final SplashView splash = new SplashView();

    public JSSplashScreenActivity( final JSNativeSplashScreen nativeSplashScreen ) {
        this.nativeSplashScreen = nativeSplashScreen;
        this.isEnabled = nativeSplashScreen.isEnabled();
        this.splashFilter = nativeSplashScreen.buildFilter();

        splash.addCloseHandler( new CloseHandler<SplashView>() {
            @Override
            public void onClose( final CloseEvent<SplashView> event ) {
                JSSplashScreenActivity.this.onClose();
            }
        } );
    }

    @Override
    public void onStartup( final PlaceRequest place ) {
        this.place = place;

        nativeSplashScreen.getWbServices().loadSplashScreenFilter( getFilter().getName(), new ParameterizedCommand<SplashScreenFilter>() {
            @Override
            public void execute( final SplashScreenFilter response ) {
                if ( response != null ) {
                    splashFilter = response;
                }
                init();
            }
        } );

        nativeSplashScreen.onStartup( place );
    }

    @Override
    public PlaceRequest getPlace() {
        return place;
    }

    public void init() {
        if ( !splashFilter.displayNextTime() ) {
            return;
        }
        forceShow();
    }

    @Override
    public String getTitle() {
        return nativeSplashScreen.getTitle();
    }

    @Override
    public IsWidget getWidget() {
        return new HTML( nativeSplashScreen.getElement().getInnerHTML() );
    }

    @Override
    public Integer getBodyHeight() {
        return nativeSplashScreen.getBodyHeight();
    }

    @Override
    public SplashScreenFilter getFilter() {
        return splashFilter;
    }

    @Override
    public IsWidget getTitleDecoration() {
        return null;
    }

    @Override
    public void onOpen() {
        nativeSplashScreen.onOpen();
    }

    @Override
    public void closeIfOpen() {
        if ( splash.isAttached() ) {
            splash.hide();
            onClose();
        }
    }

    @Override
    public void forceShow() {
        final IsWidget widget = getWidget();

        splash.setContent( widget, getBodyHeight() );
        splash.setTitle( getTitle() );
        splash.show();
    }

    @Override
    public void onClose() {
        nativeSplashScreen.onClose();
        saveState();
    }

    @Override
    public void onShutdown() {
        nativeSplashScreen.onShutdown();
    }

    @Override
    public Boolean intercept( final PlaceRequest intercepted ) {
        if ( splashFilter == null ) {
            return false;
        }
        for ( final String interceptPoint : splashFilter.getInterceptionPoints() ) {
            if ( intercepted.getIdentifier().equals( interceptPoint ) ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    private void saveState() {
        showAgain = splash.showAgain();
        if ( showAgain != null ) {
            splashFilter.setDisplayNextTime( showAgain );
            nativeSplashScreen.getWbServices().save( splashFilter );
        }
    }

    @Override
    public String getSignatureId() {
        return nativeSplashScreen.getId();
    }

    @Override
    public Collection<String> getRoles() {
        return nativeSplashScreen.getRoles();
    }

    @Override
    public Collection<String> getTraits() {
        return nativeSplashScreen.getTraits();
    }
}
