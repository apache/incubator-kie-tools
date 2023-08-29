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

package org.kie.workbench.common.dmn.client.widgets.layer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionContainerGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.dnd.DMNGridWidgetDnDMouseUpHandler;
import org.kie.workbench.common.dmn.client.widgets.dnd.DelegatingGridWidgetDndMouseMoveHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDMouseMoveHandler;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDMouseUpHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;

public class DMNGridLayer extends DefaultGridLayer {

    private final GridLayerRedrawManager.PrioritizedCommand batch = new GridLayerRedrawManager.PrioritizedCommand(Integer.MIN_VALUE) {
        @Override
        public void execute() {
            doBatch();
        }
    };

    private TransformMediator defaultTransformMediator;

    private Optional<GridWidget> selectedGridWidget = Optional.empty();

    public void setDefaultTransformMediator(final TransformMediator defaultTransformMediator) {
        this.defaultTransformMediator = defaultTransformMediator;
    }

    @Override
    //This is overridden as Lienzo calls to draw() when the LienzoPanel is resized
    //which causes flickering of the 'ghosting' when an Expression type is selected
    //from the UndefinedExpressionGrid.
    public Layer draw() {
        return batch();
    }

    @Override
    public Layer batch() {
        return batch(batch);
    }

    Layer doBatch() {
        final Layer layer = super.draw();
        findExpressionContainer().ifPresent(container -> selectedGridWidget.ifPresent(gridWidget -> addGhost(container, gridWidget)));
        return layer;
    }

    Optional<ExpressionContainerGrid> findExpressionContainer() {
        return getGridWidgets().stream()
                .filter(gw -> gw instanceof ExpressionContainerGrid)
                .map(gw -> (ExpressionContainerGrid) gw)
                .findFirst();
    }

    void addGhost(final ExpressionContainerGrid container,
                  final GridWidget gridWidget) {
        //Rectangle the size of the ExpressionContainerGrid
        final Rectangle r = getGhostRectangle();
        r.setWidth(container.getWidth() + BaseExpressionGridTheme.STROKE_WIDTH);
        r.setHeight(container.getHeight() + BaseExpressionGridTheme.STROKE_WIDTH);
        r.setFillColor(ColorName.WHITE);
        r.setAlpha(0.50);
        r.setListening(false);

        final Group g = GWT.create(Group.class);
        final Transform transform = getViewport().getTransform();
        g.setX(container.getX() + transform.getTranslateX());
        g.setY(container.getY() + transform.getTranslateY());
        g.add(r);

        // LiteralExpression and UndefinedExpression are not handled as grids in
        // their own right. In these circumstances use their parent GridWidget.
        GridWidget gw = gridWidget;
        if (gw instanceof LiteralExpressionGrid) {
            gw = ((LiteralExpressionGrid) gw).getParentInformation().getGridWidget();
        } else if (gw instanceof UndefinedExpressionGrid) {
            gw = ((UndefinedExpressionGrid) gw).getParentInformation().getGridWidget();
        }
        //Clip the inner GridWidget so everything outside of it is ghosted
        final IPathClipper clipper = new InverseGridWidgetClipper(container, gw);
        clipper.setActive(true);
        g.setPathClipper(clipper);

        g.drawWithTransforms(getContext(),
                             1.0,
                             getStorageBounds());
    }

    // Moved to method for Unit Testing. Rectangle has no
    // zero-argument Constructor so unable to use GWT.create(..)
    Rectangle getGhostRectangle() {
        return new Rectangle(0, 0);
    }

    Optional<GridWidget> getSelectedGridWidget() {
        return selectedGridWidget;
    }

    @Override
    public TransformMediator getDefaultTransformMediator() {
        return defaultTransformMediator;
    }

    @Override
    public void exitPinnedMode(final Command onCompleteCommand) {
        //Do nothing. ExpressionEditor grid is a place-holder for the real content.
    }

    @Override
    public void updatePinnedContext(final GridWidget gridWidget) throws IllegalStateException {
        //Do nothing. ExpressionEditor grid is a place-holder for the real content.
    }

    @Override
    public void select(final GridWidget selectedGridWidget) {
        this.selectedGridWidget = Optional.of(selectedGridWidget);
        boolean selectionChanged = false;
        for (GridWidget gridWidget : getAllGridWidgets()) {
            if (gridWidget.isSelected() && !Objects.equals(gridWidget, selectedGridWidget)) {
                selectionChanged = true;
                gridWidget.deselect();
            } else if (!gridWidget.isSelected() && Objects.equals(gridWidget, selectedGridWidget)) {
                selectionChanged = true;
                gridWidget.select();
            }
        }
        if (selectionChanged) {
            batch();
        }
    }

    private Set<GridWidget> getAllGridWidgets() {
        final Set<GridWidget> gridWidgets = super.getGridWidgets();
        final Set<GridWidget> allGridWidgets = new HashSet<>();
        gridWidgets.forEach(grid -> allGridWidgets.addAll(collectGridWidgets(grid)));
        return allGridWidgets;
    }

    private Set<GridWidget> collectGridWidgets(final GridWidget gridWidget) {
        final Set<GridWidget> allGridWidgets = new HashSet<>();
        allGridWidgets.add(gridWidget);

        gridWidget.getModel()
                .getRows()
                .stream()
                .forEach(row -> row.getCells().values()
                        .stream()
                        .filter(cell -> cell != null && cell.getValue() != null)
                        .map(GridCell::getValue)
                        .filter(value -> value instanceof ExpressionCellValue)
                        .map(value -> (ExpressionCellValue) value)
                        .filter(value -> value.getValue().isPresent())
                        .map(value -> value.getValue().get())
                        .forEach(editor -> allGridWidgets.addAll(collectGridWidgets(editor))));

        return allGridWidgets;
    }

    @Override
    public void deregister(final GridWidget gridWidget) {
        if (selectedGridWidget.isPresent()) {
            if (selectedGridWidget.get().equals(gridWidget)) {
                selectedGridWidget = Optional.empty();
            }
        }
        super.deregister(gridWidget);
    }

    @Override
    public Layer removeAll() {
        selectedGridWidget = Optional.empty();
        return super.removeAll();
    }

    @Override
    protected GridWidgetDnDMouseMoveHandler getGridWidgetDnDMouseMoveHandler() {
        return new DelegatingGridWidgetDndMouseMoveHandler(this,
                                                           getGridWidgetHandlersState());
    }

    @Override
    protected GridWidgetDnDMouseUpHandler getGridWidgetDnDMouseUpHandler() {
        return new DMNGridWidgetDnDMouseUpHandler(this,
                                                  getGridWidgetHandlersState());
    }
}
