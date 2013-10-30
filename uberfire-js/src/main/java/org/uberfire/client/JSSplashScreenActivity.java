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
package org.uberfire.client;

import java.util.Collection;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.workbench.widgets.splash.SplashView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.SplashScreenFilter;

public class JSSplashScreenActivity implements SplashScreenActivity {

    private Boolean showAgain;
    private SplashScreenFilter splashFilter;

    private final JSNativeSplashScreen nativeSplashScreen;
    private PlaceRequest place;
    private SplashView splash = new SplashView();

    public JSSplashScreenActivity( final JSNativeSplashScreen nativeSplashScreen ) {
        this.nativeSplashScreen = nativeSplashScreen;
        this.splashFilter = nativeSplashScreen.buildFilter();
    }

    @Override
    public void launch( final PlaceRequest place,
                        final Command callback ) {
        this.place = place;

        nativeSplashScreen.getWbServices().call( new RemoteCallback<SplashScreenFilter>() {
                                                     @Override
                                                     public void callback( final SplashScreenFilter response ) {
                                                         if ( response != null ) {
                                                             splashFilter = response;
                                                         }
                                                         init();
                                                     }
                                                 }, new ErrorCallback<Object>() {
                                                     @Override
                                                     public boolean error( Object o,
                                                                           Throwable throwable ) {
                                                         init();
                                                         return false;
                                                     }
                                                 }
                                               ).loadSplashScreenFilter( getFilter().getName() );
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
    public void forceShow() {
        onStartup( place );

        final IsWidget widget = getWidget();

        splash.setContent( widget );
        splash.setTitle( getTitle() );
        splash.show();
        splash.addCloseHandler( new CloseHandler<SplashView>() {
            @Override
            public void onClose( final CloseEvent<SplashView> event ) {
                JSSplashScreenActivity.this.onClose();
            }
        } );

        onOpen();
    }

    @Override
    public void onStartup() {
        nativeSplashScreen.onStartup();
    }

    @Override
    public void onStartup( final PlaceRequest place ) {
        nativeSplashScreen.onStartup( place );
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

    private void saveState() {
        showAgain = splash.showAgain();
        if ( showAgain != null ) {
            splashFilter.setDisplayNextTime( showAgain );
            nativeSplashScreen.getWbServices().call().save( splashFilter );
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
