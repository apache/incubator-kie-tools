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
package org.uberfire.client.mvp;

import java.util.function.Consumer;

import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

/**
 * Implementation of behaviour common to all perspective activities. Concrete implementations are typically not written by
 * hand; rather, they are generated from classes annotated with {@link WorkbenchPerspective}.
 */
public abstract class AbstractWorkbenchPerspectiveActivity extends AbstractActivity implements PerspectiveActivity {

    public AbstractWorkbenchPerspectiveActivity(final PlaceManager placeManager) {
        super(placeManager);
    }

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.PERSPECTIVE;
    }

    @Override
    public abstract PerspectiveDefinition getDefaultPerspectiveLayout();

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    @Override
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(null);
    }

    @Override
    public ToolBar getToolBar() {
        return null;
    }
}
