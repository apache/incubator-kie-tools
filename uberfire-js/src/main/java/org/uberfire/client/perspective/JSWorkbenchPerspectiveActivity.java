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

package org.uberfire.client.perspective;

import java.util.function.Consumer;
import javax.enterprise.inject.Alternative;

import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

@Alternative
public class JSWorkbenchPerspectiveActivity implements PerspectiveActivity {

    private final JSNativePerspective nativePerspective;
    private PlaceRequest place;

    public JSWorkbenchPerspectiveActivity(final JSNativePerspective nativePerspective) {
        this.nativePerspective = nativePerspective;
    }

    @Override
    public PlaceRequest getPlace() {
        return place;
    }

    @Override
    public void onStartup(final PlaceRequest place) {
        this.place = place;
        nativePerspective.onStartup(place);
    }

    @Override
    public void onOpen() {
        nativePerspective.onOpen();
    }

    @Override
    public void onClose() {
        nativePerspective.onClose();
    }

    @Override
    public void onShutdown() {
        nativePerspective.onShutdown();
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        return nativePerspective.buildPerspective();
    }

    @Override
    public String getIdentifier() {
        return nativePerspective.getId();
    }

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.PERSPECTIVE;
    }

    @Override
    public boolean isDefault() {
        return nativePerspective.isDefault();
    }

    @Override
    public boolean isTransient() {
        return nativePerspective.isTransient();
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
