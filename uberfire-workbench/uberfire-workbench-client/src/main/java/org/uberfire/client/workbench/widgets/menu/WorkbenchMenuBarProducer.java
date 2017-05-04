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

import com.google.gwt.user.client.Window;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.security.authz.AuthorizationManager;

@ApplicationScoped
public class WorkbenchMenuBarProducer {

    private AuthorizationManager authzManager;
    private PerspectiveManager perspectiveManager;
    private ActivityManager activityManager;
    private User identity;
    private WorkbenchMenuBarPresenter.View view;
    private WorkbenchMenuBarPresenter instance = null;

    public WorkbenchMenuBarProducer() {
        //CDI proxy
    }

    @Inject
    public WorkbenchMenuBarProducer(final AuthorizationManager authzManager,
                                    final PerspectiveManager perspectiveManager,
                                    final ActivityManager activityManager,
                                    final User identity,
                                    final WorkbenchMenuBarPresenter.View view) {
        this.authzManager = authzManager;
        this.perspectiveManager = perspectiveManager;
        this.activityManager = activityManager;
        this.identity = identity;
        this.view = view;
    }

    @Produces
    public WorkbenchMenuBarPresenter getWorkbenchMenuBar() {
        if (instance == null) {
            if (!isStandalone()) {
                instance = makeDefaultMenuBarPresenter();
            } else {
                instance = makeStandaloneMenuBarPresenter();
            }
        }
        return instance;
    }

    WorkbenchMenuBarPresenter makeDefaultMenuBarPresenter() {
        return new WorkbenchMenuBarPresenter(authzManager,
                                             perspectiveManager,
                                             activityManager,
                                             identity,
                                             view);
    }

    WorkbenchMenuBarPresenter makeStandaloneMenuBarPresenter() {
        return new WorkbenchMenuBarStandalonePresenter(authzManager,
                                                       perspectiveManager,
                                                       activityManager,
                                                       identity,
                                                       view);
    }

    protected void onPerspectiveChange(final @Observes PerspectiveChange perspectiveChange) {
        if (instance != null) {
            instance.onPerspectiveChange(perspectiveChange);
        }
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

    boolean isStandalone() {
        return Window.Location.getParameterMap().containsKey("standalone");
    }
}
