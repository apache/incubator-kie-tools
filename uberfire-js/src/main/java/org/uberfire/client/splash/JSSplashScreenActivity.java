/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.splash;

import javax.enterprise.inject.Alternative;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.workbench.widgets.splash.SplashView;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.SplashScreenFilter;

@Alternative
public class JSSplashScreenActivity implements SplashScreenActivity {

    private final SplashView splash;
    private Boolean showAgain = Boolean.TRUE;
    private Boolean isEnabled = Boolean.TRUE;
    private SplashScreenFilter splashFilter;
    private JSNativeSplashScreen nativeSplashScreen;
    private PlaceRequest place;

    public JSSplashScreenActivity(final JSNativeSplashScreen nativeSplashScreen,
                                  final SplashView splashView) {
        this.nativeSplashScreen = nativeSplashScreen;
        this.splash = splashView;
        this.isEnabled = nativeSplashScreen.isEnabled();
        this.splashFilter = nativeSplashScreen.buildFilter();
    }

    @Override
    public void onStartup(final PlaceRequest place) {
        this.place = place;

        nativeSplashScreen.getWbServices().loadSplashScreenFilter(getFilter().getName(),
                                                                  new ParameterizedCommand<SplashScreenFilter>() {
                                                                      @Override
                                                                      public void execute(final SplashScreenFilter response) {
                                                                          if (response != null) {
                                                                              splashFilter = response;
                                                                          }
                                                                          init();
                                                                      }
                                                                  });

        nativeSplashScreen.onStartup(place);
    }

    public void setNativeSplashScreen(JSNativeSplashScreen nativeSplashScreen) {
        this.nativeSplashScreen = nativeSplashScreen;
    }

    @Override
    public PlaceRequest getPlace() {
        return place;
    }

    @Override
    public String getIdentifier() {
        return nativeSplashScreen.getId();
    }

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.SPLASH;
    }

    public void init() {
        if (!splashFilter.displayNextTime()) {
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
        return new HTML(nativeSplashScreen.getElement().getInnerHTML());
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
        if (splash.isAttached()) {
            splash.hide();
            onClose();
        }
    }

    @Override
    public void forceShow() {
        final IsWidget widget = getWidget();

        splash.setContent(widget,
                          getBodyHeight());
        splash.setTitle(getTitle());
        splash.show();
        splash.addCloseHandler(new CloseHandler<SplashView>() {
            @Override
            public void onClose(final CloseEvent<SplashView> event) {
                JSSplashScreenActivity.this.onClose();
            }
        });
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
    public Boolean intercept(final PlaceRequest intercepted) {
        if (splashFilter == null) {
            return false;
        }
        for (final String interceptPoint : splashFilter.getInterceptionPoints()) {
            if (intercepted.getIdentifier().equals(interceptPoint)) {
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
        if (showAgain != null) {
            splashFilter.setDisplayNextTime(showAgain);
            nativeSplashScreen.getWbServices().save(splashFilter);
        }
    }
}
