/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.editor.external;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.dashbuilder.client.editor.DisplayerDragComponent;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.PerspectiveCoordinator;
import org.dashbuilder.displayer.client.events.DisplayerSettingsChangedEvent;
import org.dashbuilder.displayer.client.prototypes.DisplayerPrototypes;
import org.dashbuilder.displayer.client.widgets.DisplayerEditorPopup;
import org.dashbuilder.displayer.client.widgets.DisplayerViewer;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;

import static org.dashbuilder.displayer.DisplayerType.EXTERNAL_COMPONENT;
import static org.dashbuilder.external.model.ExternalComponent.COMPONENT_ID_KEY;

@Dependent
public class ExternalDisplayerDragComponent extends DisplayerDragComponent implements ExternalComponentDragDef {

    String componentId;

    private String componentName;

    private String componentIcon;

    @Inject
    Event<DisplayerSettingsChangedEvent> displayerSettingsChangedEvent;

    @Inject
    DisplayerPrototypes displayerPrototypes;

    @Inject
    public ExternalDisplayerDragComponent(SyncBeanManager beanManager,
                                          DisplayerViewer viewer,
                                          PlaceManager placeManager,
                                          PerspectiveCoordinator perspectiveCoordinator) {
        super(beanManager, viewer, placeManager, perspectiveCoordinator);
    }

    @Override
    public void setDragInfo(String componentName, String componentIcon) {
        this.componentName = componentName;
        this.componentIcon = componentIcon;
    }

    @Override
    public Modal getConfigurationModal(ModalConfigurationContext ctx) {
        DisplayerEditorPopup editorPopUp = buildEditorPopUp(ctx);
        editorPopUp.setTypeSelectorEnabled(false);
        editorPopUp.setExternalDisplayerEnabled(true);
        return editorPopUp;
    }

    @Override
    protected DisplayerSettings initialSettings(ModalConfigurationContext ctx) {
        DisplayerSettings settings = displayerPrototypes.getProto(EXTERNAL_COMPONENT);
        String storedComponentId = ctx.getComponentProperty(COMPONENT_ID_KEY);
        settings.setComponentId(storedComponentId);
        return settings;
    }

    @Override
    public DisplayerType getDisplayerType() {
        return DisplayerType.EXTERNAL_COMPONENT;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    @Override
    public String getComponentName() {
        return componentName;
    }

    @Override
    public String getComponentIcon() {
        return componentIcon;
    }

    @Override
    public String getComponentId() {
        return componentId;
    }

    @Override
    public String getDragComponentTitle() {
        return ExternalComponentDragDef.super.getDragComponentTitle();
    }

}