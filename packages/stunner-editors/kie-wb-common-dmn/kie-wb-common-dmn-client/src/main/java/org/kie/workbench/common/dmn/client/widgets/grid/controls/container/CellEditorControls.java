/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.grid.controls.container;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Transform;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;

@Dependent
public class CellEditorControls implements CellEditorControlsView.Presenter {

    private static final int BIGGEST_MENU_HEIGHT_PX = 250;

    private Optional<Supplier<DMNGridPanel>> gridPanelSupplier;
    private CellEditorControlsView view;

    public CellEditorControls() {
        //CDI proxy
    }

    @Inject
    public CellEditorControls(final CellEditorControlsView view) {
        this.view = view;
    }

    @Override
    public void setGridPanelSupplier(final Optional<Supplier<DMNGridPanel>> gridPanelSupplier) {
        this.gridPanelSupplier = gridPanelSupplier;
    }

    @Override
    public void show(final HasCellEditorControls.Editor<?> editor,
                     final int x,
                     final int y) {
        if (!gridPanelSupplier.isPresent()) {
            throw new IllegalStateException("A DMNGridPanel needs to have been set.");
        }

        final int tx = getTransformedX(x);
        final int ty = getTransformedY(y);

        view.show(editor,
                  tx,
                  ty);
    }

    @Override
    public void hide() {
        view.hide();
    }

    private int getTransformedX(final int x) {
        final DMNGridPanel gridPanel = gridPanelSupplier.get().get();
        final Transform transform = gridPanel.getViewport().getTransform();
        return (int) ((x * transform.getScaleX()) + transform.getTranslateX()) + gridPanel.getAbsoluteLeft();
    }

    private int getTransformedY(final int y) {
        final DMNGridPanel gridPanel = gridPanelSupplier.get().get();
        final Transform transform = gridPanel.getViewport().getTransform();
        int yCoordinate = (int) ((y * transform.getScaleY()) + transform.getTranslateY()) + gridPanel.getAbsoluteTop();
        final int absoluteGridBottom = gridPanel.getElement().getAbsoluteBottom();

        if (yCoordinate + BIGGEST_MENU_HEIGHT_PX > absoluteGridBottom) {
            yCoordinate -= BIGGEST_MENU_HEIGHT_PX;
        }

        return yCoordinate;
    }
}
