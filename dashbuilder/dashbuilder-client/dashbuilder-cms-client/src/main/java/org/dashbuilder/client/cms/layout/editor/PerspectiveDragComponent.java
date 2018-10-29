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
package org.dashbuilder.client.cms.layout.editor;

import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerConstants;
import org.dashbuilder.client.cms.widget.PerspectiveWidget;
import org.gwtbootstrap3.client.ui.Modal;
import org.uberfire.ext.layout.editor.client.api.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorCoreComponent;
import org.uberfire.ext.plugin.model.Plugin;

import static org.dashbuilder.navigation.layout.NavDragComponentSettings.PERSPECTIVE_ID;

/**
 * Runtime perspective drag component.
 */
@Dependent
public class PerspectiveDragComponent implements PerspectiveEditorCoreComponent,
                                                 HasModalConfiguration {

    PerspectiveDragConfigModal perspectiveDragConfigModal;
    PerspectiveWidget perspectiveWidget;

    @Inject
    public PerspectiveDragComponent(PerspectiveDragConfigModal perspectiveDragConfigModal, PerspectiveWidget perspectiveWidget) {
        this.perspectiveDragConfigModal = perspectiveDragConfigModal;
        this.perspectiveWidget = perspectiveWidget;
    }

    @Override
    public String getDragComponentTitle() {
        return ContentManagerConstants.INSTANCE.perspectiveDragComponent();
    }

    @Override
    public String getDragComponentIconClass() {
        return "fa fa-file-o";
    }

    @Override
    public IsWidget getPreviewWidget(RenderingContext ctx) {
        return getShowWidget(ctx);
    }

    @Override
    public IsWidget getShowWidget(RenderingContext ctx) {
        Map<String, String> properties = ctx.getComponent().getProperties();
        String perspectiveId = properties.get(PERSPECTIVE_ID);
        perspectiveWidget.showPerspective(perspectiveId);
        return perspectiveWidget;
    }

    @Override
    public Modal getConfigurationModal(ModalConfigurationContext ctx) {
        Map<String, String> properties = ctx.getComponentProperties();
        String perspectiveId = properties.get(PERSPECTIVE_ID);
        perspectiveDragConfigModal.setOnOk(() -> perspectiveSelectionOk(ctx));
        perspectiveDragConfigModal.setOnCancel(() -> perspectiveSelectionCancel(ctx));
        perspectiveDragConfigModal.show(perspectiveId);
        return ((PerspectiveDragConfigModalView) perspectiveDragConfigModal.getView()).getModal();
    }

    protected void perspectiveSelectionOk(ModalConfigurationContext ctx) {
        Plugin perspective = perspectiveDragConfigModal.getSelectedItem();
        ctx.setComponentProperty(PERSPECTIVE_ID, perspective.getName());
        ctx.configurationFinished();
    }

    protected void perspectiveSelectionCancel(ModalConfigurationContext ctx) {
        ctx.configurationCancelled();
    }
}
