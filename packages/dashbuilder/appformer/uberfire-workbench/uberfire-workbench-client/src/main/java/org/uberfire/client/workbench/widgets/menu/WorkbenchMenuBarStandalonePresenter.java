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

import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.model.menu.Menus;

public class WorkbenchMenuBarStandalonePresenter extends WorkbenchMenuBarPresenter {

    WorkbenchMenuBarStandalonePresenter(final PerspectiveManager perspectiveManager,
                                        final PlaceManager placeManager,
                                        final ActivityManager activityManager,
                                        final View view) {
        super(perspectiveManager,
              placeManager,
              activityManager,
              view);
    }

    @Override
    public void addMenus(final Menus menus) {
        //Do nothing. Standalone mode does not use top-level menu items
    }

    @Override
    protected void addPerspectiveMenus(final PerspectiveActivity perspective) {
        perspective.getMenus(super::addMenus);
    }
}
