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

package org.drools.guvnor.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.guvnor.client.resources.RoundedCornersResource;
import org.drools.guvnor.client.workbench.Workbench;
import org.jboss.errai.enterprise.client.jaxrs.api.RestClient;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@EntryPoint
public class DefaultEntryPoint {

    @Inject
    PerspectiveActivityMapper activityMapper;

    private final PlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
    private final SimplePanel appWidget = new SimplePanel();
    private PlaceHistoryHandler historyHandler;
    @Inject
    private PlaceController placeController;
    @Inject
    private EventBus eventBus;

    @PostConstruct
    public void init() {
        RestClient.setApplicationRoot("/");

        final ActivityManager activityManager = new ActivityManager(activityMapper,
                eventBus);
        final Workbench workbench = new Workbench();
        appWidget.add(workbench);

        activityManager.setDisplay(appWidget);

        historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController,
                eventBus,
                null);
    }

    @AfterInitialization
    public void startApp() {
        loadStyles();

        hideLoadingPopup();

        RootLayoutPanel.get().add(appWidget);
        historyHandler.handleCurrentHistory();
    }

    private void loadStyles() {
        //Ensure CSS has been loaded
        GuvnorResources.INSTANCE.guvnorCss().ensureInjected();
        GuvnorResources.INSTANCE.headerCss().ensureInjected();
        RoundedCornersResource.INSTANCE.roundCornersCss().ensureInjected();
    }

    //Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get("loading").getElement();

        new Animation() {

            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity(1.0 - progress);
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility(Style.Visibility.HIDDEN);
            }
        }.run(500);
    }

}
