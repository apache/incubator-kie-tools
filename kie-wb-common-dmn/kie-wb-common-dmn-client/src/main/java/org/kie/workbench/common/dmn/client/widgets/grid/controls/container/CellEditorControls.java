/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.container;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Transform;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;

@ApplicationScoped
public class CellEditorControls implements CellEditorControlsView.Presenter {

    private DMNGridPanel gridPanel;
    private CellEditorControlsView view;

    public CellEditorControls() {
        //CDI proxy
    }

    @Inject
    public CellEditorControls(final @DMNEditor DMNGridPanel gridPanel,
                              final CellEditorControlsView view) {
        this.gridPanel = gridPanel;
        this.view = view;
        this.view.init(this);
    }

    @Override
    public void show(final HasCellEditorControls.Editor editor,
                     final int x,
                     final int y) {
        view.show(editor,
                  x,
                  y);
    }

    @Override
    public void hide() {
        view.hide();
    }

    @Override
    public int getTransformedX(final int x) {
        final Transform transform = gridPanel.getViewport().getTransform();
        return (int) ((x * transform.getScaleX()) + transform.getTranslateX()) + gridPanel.getAbsoluteLeft();
    }

    @Override
    public int getTransformedY(final int y) {
        final Transform transform = gridPanel.getViewport().getTransform();
        return (int) ((y * transform.getScaleY()) + transform.getTranslateY()) + gridPanel.getAbsoluteTop();
    }
}
