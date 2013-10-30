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
package org.uberfire.client.mvp;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.workbench.widgets.splash.SplashView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.workbench.services.WorkbenchServices;

/**
 * Base class for Pop-up Activities
 */
public abstract class AbstractSplashScreenActivity extends AbstractActivity
        implements SplashScreenActivity {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<WorkbenchServices> wbServices;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private SplashView splash = new SplashView();

    private Boolean showAgain;
    private SplashScreenFilter splashFilter;

    @Inject
    public AbstractSplashScreenActivity( final PlaceManager placeManager ) {
        super( placeManager );
    }

    @PostConstruct
    private void initialize() {
        this.splashFilter = getFilter();
    }

    @Override
    public void launch( final PlaceRequest place,
                        final Command callback ) {
        super.launch( place,
                      callback );

        wbServices.call( new RemoteCallback<SplashScreenFilter>() {
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
    public abstract String getTitle();

    @Override
    public abstract IsWidget getWidget();

    @Override
    public abstract SplashScreenFilter getFilter();

    @Override
    public IsWidget getTitleDecoration() {
        return null;
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
                AbstractSplashScreenActivity.this.onClose();
            }
        } );

        onOpen();
    }

    @Override
    public void onStartup() {
        //Do nothing.
    }

    @Override
    public void onStartup( final PlaceRequest place ) {
        //Do nothing.  
    }

    @Override
    public void onClose() {
        saveState();
    }

    @Override
    public void onShutdown() {
        //Do nothing.
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
            wbServices.call().save( splashFilter );
        }
    }
}
