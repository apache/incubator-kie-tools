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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import elemental2.dom.Attr;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import jsinterop.base.Js;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.TemplatedActivity;
import org.uberfire.client.workbench.panels.impl.TemplatedWorkbenchPanelPresenter;
import org.uberfire.jsbridge.client.perspective.jsnative.JsNativePerspective;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.NamedPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

import static java.util.stream.Collectors.toMap;
import static org.uberfire.workbench.model.PanelDefinition.PARENT_CHOOSES_TYPE;

public class JsWorkbenchTemplatedPerspectiveActivity extends AbstractWorkbenchPerspectiveActivity implements TemplatedActivity {

    private static final String UF_PERSPECTIVE_COMPONENT = "uf-perspective-component";
    private static final String UF_PERSPECTIVE_CONTAINER = "uf-perspective-container";
    private static final String STARTUP_PARAM_ATTR = "data-startup-";

    private final String componentId;
    private final boolean isDefault;

    private final HTMLElement container;

    private final JsNativePerspective realPerspective;
    private Map<String, HTMLElement> componentContainersById;

    public JsWorkbenchTemplatedPerspectiveActivity(final String componentId,
                                                   final boolean isDefault,
                                                   final JsNativePerspective realPerspective,
                                                   final PlaceManager placeManager) {
        super(placeManager);

        this.componentId = componentId;
        this.isDefault = isDefault;

        this.realPerspective = realPerspective;

        this.container = (HTMLElement) DomGlobal.document.createElement("div");
        this.container.classList.add(UF_PERSPECTIVE_CONTAINER);
        this.componentContainersById = new HashMap<>();
    }

    // Lifecycle

    @Override
    public void onStartup(final PlaceRequest place) {
        this.place = place;
        super.onStartup(place);
        realPerspective.onStartup();
    }

    @Override
    public void onOpen() {
        super.onOpen();

        // update local references to the DOM elements
        realPerspective.renderNative(container);
        componentContainersById = loadTemplateComponents(container);

        realPerspective.onOpen();
        placeManager.executeOnOpenCallbacks(place);
    }

    @Override
    public void onClose() {
        super.onClose();
        realPerspective.onClose(container);
        placeManager.executeOnCloseCallbacks(place);
    }

    @Override
    public void onShutdown() {
        super.onShutdown();
        this.realPerspective.onShutdown();
    }

    // Properties

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.PERSPECTIVE;
    }

    @Override
    public String getIdentifier() {
        return componentId;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
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
        final PerspectiveDefinition perspectiveDefinition = new PerspectiveDefinitionImpl(TemplatedWorkbenchPanelPresenter.class.getName());
        perspectiveDefinition.setName(realPerspective.name());

        componentContainersById.forEach((key, value) -> {

            final Map<String, String> placeParams = retrieveStartUpParams(value);
            final PanelDefinition panelDefinition = new PanelDefinitionImpl(PARENT_CHOOSES_TYPE);

            panelDefinition.addPart(new PartDefinitionImpl(new DefaultPlaceRequest(key, placeParams)));
            perspectiveDefinition.getRoot().appendChild(new NamedPosition(key), panelDefinition);
        });

        return perspectiveDefinition;
    }

    // Templated interface methods

    @Override
    public org.jboss.errai.common.client.dom.HTMLElement resolvePosition(final NamedPosition namedPosition) {
        final String fieldName = namedPosition.getName();
        final HTMLElement element = componentContainersById.get(fieldName);
        return element == null ? null : Js.cast(element);
    }

    @Override
    public org.jboss.errai.common.client.dom.HTMLElement getRootElement() {
        return Js.cast(container);
    }

    private Map<String, HTMLElement> loadTemplateComponents(final HTMLElement container) {

        final Map<String, HTMLElement> templateComponents = realPerspective.getContainerComponents(container)
                .stream()
                .collect(toMap(e -> e.getAttribute("af-js-component"), e -> e));

        templateComponents.values().forEach(component -> this.recursivelyMarkComponentContainers(container, component));

        return templateComponents;
    }

    private void recursivelyMarkComponentContainers(final Node root, final Node leaf) {

        // Run through every node between the root container and the component node marking it as an uf-perspective-component.
        // This is needed to make the TemplatedPresenter display the correct elements in the screen when it opens.

        if (!(leaf instanceof HTMLElement)) {
            return;
        }

        if (leaf == root) {
            return;
        }

        final HTMLElement htmlElement = (HTMLElement) leaf;
        if (!htmlElement.classList.contains(UF_PERSPECTIVE_COMPONENT)) {
            htmlElement.classList.add(UF_PERSPECTIVE_COMPONENT);
        }

        recursivelyMarkComponentContainers(root, leaf.parentNode);
    }

    private Map<String, String> retrieveStartUpParams(final HTMLElement component) {

        final Map<String, String> params = new HashMap<>();

        for (int i = 0; i < component.attributes.length; i++) {

            final Attr attr = component.attributes.getAt(i);
            if (!attr.name.startsWith(STARTUP_PARAM_ATTR)) {
                continue;
            }

            final String key = attr.name.replaceFirst(STARTUP_PARAM_ATTR, "");
            if (key.length() > 0) {
                params.put(key, attr.value);
            }
        }

        return params;
    }
}