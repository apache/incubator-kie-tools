/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.jsbridge.client.perspective;

import java.util.function.Consumer;

import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.jsbridge.client.perspective.jsnative.JsNativeContextDisplay;
import org.uberfire.jsbridge.client.perspective.jsnative.JsNativePerspective;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.ContextDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

public class JsWorkbenchPerspectiveActivity extends AbstractWorkbenchPerspectiveActivity {

    private JsNativePerspective realPerspective;
    private final boolean isDefault;

    public JsWorkbenchPerspectiveActivity(final JsNativePerspective realPerspective,
                                          final PlaceManager placeManager,
                                          final boolean isDefault) {
        super(placeManager);
        this.realPerspective = realPerspective;
        this.isDefault = isDefault;
    }

    // TODO: CDI Event subscriptions?

    /**
     * This method is called when this perspective is instantiated
     * @param place
     */
    @Override
    public void onStartup(final PlaceRequest place) {
        super.onStartup(place);

        this.place = place;
        this.realPerspective.onStartup();
    }

    @Override
    public void onOpen() {
        super.onOpen();

        this.realPerspective.onOpen();
        placeManager.executeOnOpenCallbacks(place);
    }

    @Override
    public void onClose() {
        super.onClose();

        this.realPerspective.onClose();
        placeManager.executeOnCloseCallbacks(place);
    }

    @Override
    public void onShutdown() {
        super.onShutdown();

        this.realPerspective.onShutdown();
    }

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.PERSPECTIVE;
    }

    @Override
    public String getIdentifier() {
        return realPerspective.componentId();
    }

    @Override
    public boolean isDefault() {
        return this.isDefault;
    }

    @Override
    public boolean isTransient() {
        return realPerspective.isTransient();
    }

    @Override
    public void getMenus(final Consumer<Menus> consumer) {
        consumer.accept(realPerspective.menus());
    }

    @Override
    public ToolBar getToolBar() {
        return realPerspective.toolbar();
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {

        final PerspectiveDefinition def = new PerspectiveDefinitionImpl(realPerspective.defaultPanelType());
        def.setName(realPerspective.name());

        final JsNativeContextDisplay contextDisplay = this.realPerspective.contextDisplay();
        def.setContextDisplayMode(contextDisplay.mode());
        if (contextDisplay.contextId() != null) {
            def.setContextDefinition(new ContextDefinitionImpl(new DefaultPlaceRequest(contextDisplay.contextId())));
        }

        final PanelDefinition rootPanel = def.getRoot();

        realPerspective.view().parts().stream()
                .map(part -> new JsWorkbenchPartConverter(part).toPartDefinition())
                .forEach(rootPanel::addPart);

        realPerspective.view().panels().stream()
                .map(panel -> new JsWorkbenchPanelConverter(panel).toPanelDefinition())
                .forEach(panel -> rootPanel.insertChild(panel.getPosition(), panel));

        return def;
    }
}
