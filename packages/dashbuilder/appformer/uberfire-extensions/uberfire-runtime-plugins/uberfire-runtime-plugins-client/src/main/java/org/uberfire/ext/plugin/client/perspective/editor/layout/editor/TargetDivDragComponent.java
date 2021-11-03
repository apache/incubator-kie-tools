/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import org.gwtbootstrap3.client.ui.Modal;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.layout.editor.client.api.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorNavComponent;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups.EditTargetDiv;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

@ApplicationScoped
public class TargetDivDragComponent implements PerspectiveEditorNavComponent,
                                               HasModalConfiguration {

    public static final String ID_PARAMETER = "ID_PARAMETER";
    @Inject
    private PlaceManager placeManager;
    private ModalConfigurationContext configContext;

    @PostConstruct
    public void setup() {
    }

    @Override
    public String getDragComponentTitle() {
        return CommonConstants.INSTANCE.TargetDivComponent();
    }

    @Override
    public IsWidget getPreviewWidget(RenderingContext ctx) {
        String id = ctx.getComponent().getProperties().get(ID_PARAMETER);
        FlowPanel panel = createDiv(id);
        Label l = GWT.create(Label.class);
        l.setText(CommonConstants.INSTANCE.TargetDivPlaceHolder() + " " + id);
        panel.add(l);
        return panel;
    }

    @Override
    public IsWidget getShowWidget(RenderingContext ctx) {
        String id = ctx.getComponent().getProperties().get(ID_PARAMETER);
        return createDiv(id);
    }

    private FlowPanel createDiv(String id) {
        FlowPanel panel = GWT.create(FlowPanel.class);
        panel.asWidget().getElement().addClassName("uf-perspective-col");
        panel.asWidget().getElement().addClassName("screen dnd component");
        panel.getElement().setId(id);
        return panel;
    }

    @Override
    public Modal getConfigurationModal(ModalConfigurationContext ctx) {
        this.configContext = ctx;
        return new EditTargetDiv(ctx);
    }

    public void observeEditComponentEventFromPropertyEditor(@Observes PropertyEditorChangeEvent event) {
        PropertyEditorFieldInfo property = event.getProperty();
        if (property.getEventId().equalsIgnoreCase(EditTargetDiv.PROPERTY_EDITOR_KEY)) {
            configContext.setComponentProperty(ID_PARAMETER,
                                               property.getCurrentStringValue());
        }
    }
}
