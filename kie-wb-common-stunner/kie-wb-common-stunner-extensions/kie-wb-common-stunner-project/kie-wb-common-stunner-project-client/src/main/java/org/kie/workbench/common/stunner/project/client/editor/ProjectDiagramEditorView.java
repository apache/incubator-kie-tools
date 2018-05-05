/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.project.client.editor;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

@Dependent
@Templated
public class ProjectDiagramEditorView
        extends KieEditorViewImpl
        implements AbstractProjectDiagramEditor.View {

    @DataField
    private FlowPanel editorPanel;

    private AbstractProjectDiagramEditor presenter;

    public ProjectDiagramEditorView() {
        //CDI proxy
    }

    @Inject
    public ProjectDiagramEditorView(final FlowPanel editorPanel) {
        this.editorPanel = editorPanel;
    }

    @Override
    public void init(final AbstractProjectDiagramEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if (parent != null) {
            final double w = parent.getOffsetWidth();
            final double h = parent.getOffsetHeight();
            setPixelSize((int) w, (int) h);
        }

        if (editorPanel.getWidgetCount() > 0) {
            final IsWidget widget = editorPanel.getWidget(0);
            if (widget instanceof RequiresResize) {
                ((RequiresResize) widget).onResize();
            }
        }
    }

    @Override
    public void setWidget(final IsWidget widget) {
        editorPanel.clear();
        editorPanel.add(widget);
    }

    @PreDestroy
    public void destroy() {
        editorPanel.clear();
        editorPanel.removeFromParent();
        presenter = null;
    }
}
