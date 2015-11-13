/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.client.mvp;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchSplashScreen;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.widgets.splash.SplashView;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.SplashScreenFilter;

/**
 * Implementation of behaviour common to all splash screen activities. Concrete implementations are typically not written by
 * hand; rather, they are generated from classes annotated with {@link WorkbenchSplashScreen}.
 */
public abstract class AbstractSplashScreenActivity extends AbstractActivity implements SplashScreenActivity {

    @Inject
    private WorkbenchServicesProxy wbServices;

    private final SplashView splash;

    private Boolean showAgain;
    private SplashScreenFilter splashFilter;

    @Inject
    public AbstractSplashScreenActivity( final PlaceManager placeManager,
                                         final SplashView splash ) {
        super( placeManager );
        this.splash = checkNotNull( "splash", splash );
    }

    @PostConstruct
    private void initialize() {
        this.splashFilter = getFilter();
        splash.addCloseHandler( new CloseHandler<SplashView>() {
            @Override
            public void onClose( final CloseEvent<SplashView> event ) {
                AbstractSplashScreenActivity.this.onClose();
            }
        } );
    }

    @Override
    public void onOpen() {
        super.onOpen();
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        wbServices.loadSplashScreenFilter( getFilter().getName(), new ParameterizedCommand<SplashScreenFilter>() {
            @Override
            public void execute( final SplashScreenFilter response ) {
                if ( response != null ) {
                    splashFilter = response;
                }
                if ( splashFilter.displayNextTime() ) {
                    forceShow();
                }
            }
        } );
    }

    @Override
    public abstract String getTitle();

    @Override
    public abstract IsWidget getWidget();

    @Override
    public Integer getBodyHeight() {
        return null;
    }

    @Override
    public abstract SplashScreenFilter getFilter();

    @Override
    public IsWidget getTitleDecoration() {
        return null;
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
        saveState();
        super.onClose();
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
            wbServices.save( splashFilter );
        }
    }

}