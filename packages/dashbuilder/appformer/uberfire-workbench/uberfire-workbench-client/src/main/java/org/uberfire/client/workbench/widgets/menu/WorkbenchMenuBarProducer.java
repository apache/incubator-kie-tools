/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.workbench.widgets.menu;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;

@ApplicationScoped
public class WorkbenchMenuBarProducer extends AbstractWorkbenchMenuProducer<WorkbenchMenuBarPresenter, WorkbenchMenuBarPresenter.View> {

    public WorkbenchMenuBarProducer() {
        //CDI proxy
    }

    @Inject
    public WorkbenchMenuBarProducer(final PerspectiveManager perspectiveManager,
                                    final PlaceManager placeManager,
                                    final ActivityManager activityManager,
                                    final WorkbenchMenuBarPresenter.View view) {
        super(perspectiveManager, placeManager, activityManager, view);
    }

    @Produces
    public WorkbenchMenuBarPresenter getInstance() {
        return getWorbenchMenu();
    }

    @Override
    protected WorkbenchMenuBarPresenter makeDefaultPresenter() {
        return new WorkbenchMenuBarPresenter(perspectiveManager,
                                             placeManager,
                                             activityManager,
                                             view);
    }

    protected WorkbenchMenuBarPresenter makeStandalonePresenter() {
        return new WorkbenchMenuBarStandalonePresenter(perspectiveManager,
                                                       placeManager,
                                                       activityManager,
                                                       view);
    }

    protected void onPlaceMinimized(final @Observes PlaceMinimizedEvent event) {
        if (instance != null) {
            instance.onPlaceMinimized(event);
        }
    }

    protected void onPlaceMaximized(final @Observes PlaceMaximizedEvent event) {
        if (instance != null) {
            instance.onPlaceMaximized(event);
        }
    }
}
