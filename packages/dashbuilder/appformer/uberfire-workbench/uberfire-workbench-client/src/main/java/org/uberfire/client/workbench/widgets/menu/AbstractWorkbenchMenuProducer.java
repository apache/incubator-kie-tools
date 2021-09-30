/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench.widgets.menu;

import javax.enterprise.event.Observes;

import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.widgets.menu.base.WorkbenchBaseMenuPresenter;
import org.uberfire.client.workbench.widgets.menu.base.WorkbenchBaseMenuView;
import org.uberfire.client.workbench.widgets.menu.events.PerspectiveVisibiltiyChangeEvent;

import com.google.gwt.user.client.Window;

public abstract class AbstractWorkbenchMenuProducer<PRESENTER extends WorkbenchBaseMenuPresenter, VIEW extends WorkbenchBaseMenuView> {

    protected PerspectiveManager perspectiveManager;
    protected PlaceManager placeManager;
    protected ActivityManager activityManager;

    protected VIEW view;
    protected PRESENTER instance = null;

    public AbstractWorkbenchMenuProducer() {
    }

    public AbstractWorkbenchMenuProducer(final PerspectiveManager perspectiveManager, final PlaceManager placeManager, final ActivityManager activityManager, final VIEW view) {
        this.perspectiveManager = perspectiveManager;
        this.placeManager = placeManager;
        this.activityManager = activityManager;
        this.view = view;
    }

    protected abstract PRESENTER makeDefaultPresenter();

    protected abstract PRESENTER makeStandalonePresenter();

    public PRESENTER getWorbenchMenu() {
        if (instance == null) {
            if (!isStandalone()) {
                instance = makeDefaultPresenter();
            } else {
                instance = makeStandalonePresenter();
            }
        }
        return instance;
    }

    protected void onPerspectiveChange(final @Observes PerspectiveChange perspectiveChange) {
        if (instance != null) {
            instance.onPerspectiveChange(perspectiveChange);
        }
    }

    protected void onPerspectiveHide(final @Observes PerspectiveVisibiltiyChangeEvent setPerspectiveVisibleEvent) {
        if (instance != null) {
            instance.onPerspectiveVisibilityChange(setPerspectiveVisibleEvent);
        }
    }

    protected boolean isStandalone() {
        return Window.Location.getParameterMap().containsKey("standalone");
    }
}
