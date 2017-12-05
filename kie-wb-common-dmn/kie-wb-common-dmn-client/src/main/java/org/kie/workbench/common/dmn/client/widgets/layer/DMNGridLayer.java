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

package org.kie.workbench.common.dmn.client.widgets.layer;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.Command;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.dnd.DelegatingGridWidgetDndMouseMoveHandler;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDMouseMoveHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;

public class DMNGridLayer extends DefaultGridLayer {

    private TransformMediator defaultTransformMediator;

    public void setDefaultTransformMediator(final TransformMediator defaultTransformMediator) {
        this.defaultTransformMediator = defaultTransformMediator;
    }

    @Override
    public TransformMediator getDefaultTransformMediator() {
        return defaultTransformMediator;
    }

    @Override
    public Set<GridWidget> getGridWidgets() {
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
    public void exitPinnedMode(final Command onCompleteCommand) {
        //Do nothing. ExpressionEditor grid is a place-holder for the real content.
    }

    @Override
    public void updatePinnedContext(final GridWidget gridWidget) throws IllegalStateException {
        //Do nothing. ExpressionEditor grid is a place-holder for the real content.
    }

    @Override
    protected GridWidgetDnDMouseMoveHandler getGridWidgetDnDMouseMoveHandler() {
        return new DelegatingGridWidgetDndMouseMoveHandler(this,
                                                           getGridWidgetHandlersState());
    }

    public void clearAllSelections() {
        getGridWidgets().forEach(grid -> grid.getModel().clearSelections());
    }
}
