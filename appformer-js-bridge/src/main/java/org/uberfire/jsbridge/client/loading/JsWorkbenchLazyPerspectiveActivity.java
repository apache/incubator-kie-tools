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

package org.uberfire.jsbridge.client.loading;

import java.util.function.Consumer;

import com.google.gwt.core.client.JavaScriptObject;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.jsbridge.JsWorkbenchLazyPerspective;
import org.uberfire.client.workbench.panels.impl.ImmutableWorkbenchPanelPresenter;
import org.uberfire.jsbridge.client.loading.AppFormerComponentsRegistry.Entry.PerspectiveParams;
import org.uberfire.jsbridge.client.perspective.JsWorkbenchPerspectiveActivity;
import org.uberfire.jsbridge.client.perspective.JsWorkbenchTemplatedPerspectiveActivity;
import org.uberfire.jsbridge.client.perspective.jsnative.JsNativePerspective;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.ForcedPlaceRequest;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

public class JsWorkbenchLazyPerspectiveActivity extends AbstractWorkbenchPerspectiveActivity implements JsWorkbenchLazyPerspective {

    private final ActivityManager activityManager;

    private final String backedPerspectiveId;
    private final boolean configuredIsDefault;
    PerspectiveActivity backedPerspective;

    private boolean loaded;
    private final Consumer<String> lazyLoadingParentScript;

    public JsWorkbenchLazyPerspectiveActivity(final AppFormerComponentsRegistry.Entry registryEntry,
                                              final PlaceManager placeManager,
                                              final ActivityManager activityManager,
                                              final Consumer<String> lazyLoadingParentScript) {

        super(placeManager);
        this.activityManager = activityManager;

        this.backedPerspectiveId = registryEntry.getComponentId();
        this.lazyLoadingParentScript = lazyLoadingParentScript;

        this.configuredIsDefault = new PerspectiveParams(registryEntry.getParams()).isDefault().orElse(super.isDefault());

        this.loaded = false;
    }

    @Override
    public void updateRealContent(final JavaScriptObject backedPerspectiveJsObject) {

        this.loaded = true;

        backedPerspective = getBackedPerspective(backedPerspectiveJsObject);

        if (activityManager.isStarted(this)) {
            // current activity is started, need to move the backed perspective to started state
            getBackedPerspective().onStartup(place);
        }

        if (open) {
            // lazy perspective is opened, need to move the backed perspective to open state and refresh the page
            getBackedPerspective().onOpen();
            placeManager.goTo(new ForcedPlaceRequest(backedPerspectiveId));
        }
    }

    PerspectiveActivity getBackedPerspective(final JavaScriptObject backedPerspectiveJsObject) {
        final JsNativePerspective jsPerspective = new JsNativePerspective(backedPerspectiveJsObject);
        if (isPerspectiveTemplated(jsPerspective)) {
            return new JsWorkbenchTemplatedPerspectiveActivity(getIdentifier(),
                                                               isDefault(),
                                                               jsPerspective,
                                                               placeManager);
        } else {
            return new JsWorkbenchPerspectiveActivity(jsPerspective,
                                                      placeManager,
                                                      isDefault());
        }
    }

    public boolean isPerspectiveTemplated(final JsNativePerspective jsPerspective) {
        return jsPerspective.isTemplated();
    }

    @Override
    public PerspectiveActivity get() {
        if (isPerspectiveLoaded()) {
            return getBackedPerspective();
        }
        return this;
    }

    // Lifecycle

    @Override
    public void onStartup(final PlaceRequest place) {

        this.place = place;

        if (isPerspectiveLoaded()) {
            getBackedPerspective().onStartup(place);
            return;
        }
        super.onStartup(place);
    }

    @Override
    public void onOpen() {

        if (isPerspectiveLoaded()) {
            getBackedPerspective().onOpen();
        } else {
            super.onOpen();
            onLoaded(); // trigger backed perspective loading
        }

        placeManager.executeOnOpenCallbacks(place);
    }

    @Override
    public void onClose() {

        if (isPerspectiveLoaded()) {
            getBackedPerspective().onClose();
        } else {
            super.onClose();
        }

        placeManager.executeOnCloseCallbacks(place);
    }

    @Override
    public void onShutdown() {

        if (isPerspectiveLoaded()) {
            getBackedPerspective().onShutdown();
            return;
        }
        super.onShutdown();
    }

    void onLoaded() {
        lazyLoadingParentScript.accept(backedPerspectiveId);
    }

    // Properties

    PerspectiveActivity getBackedPerspective() {
        return backedPerspective;
    }

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.PERSPECTIVE;
    }

    @Override
    public String getIdentifier() {
        return backedPerspectiveId;
    }

    @Override
    public boolean isDefault() {
        // we ignore the isDefault() property of the backed perspective.
        return configuredIsDefault;
    }

    @Override
    public boolean isTransient() {

        if (isPerspectiveLoaded()) {
            return getBackedPerspective().isTransient();
        }

        // lazy perspectives are always transient.
        // We don't want to propagate the changes made while the real perspective was loading.
        return true;
    }

    @Override
    public void getMenus(final Consumer<Menus> consumer) {
        if (isPerspectiveLoaded()) {
            getBackedPerspective().getMenus(consumer);
        } else {
            super.getMenus(consumer);
        }
    }

    @Override
    public ToolBar getToolBar() {
        if (isPerspectiveLoaded()) {
            return getBackedPerspective().getToolBar();
        }
        return super.getToolBar();
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {

        if (isPerspectiveLoaded()) {
            return getBackedPerspective().getDefaultPerspectiveLayout();
        }

        return buildEmptyDefinition();
    }

    boolean isPerspectiveLoaded() {
        return loaded;
    }

    private PerspectiveDefinition buildEmptyDefinition() {
        final PerspectiveDefinition def = new PerspectiveDefinitionImpl(ImmutableWorkbenchPanelPresenter.class.getName());
        def.setName(getIdentifier()); // perspective not loaded yet, we don't know its name
        def.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest(LazyLoadingScreen.IDENTIFIER)));
        return def;
    }
}
