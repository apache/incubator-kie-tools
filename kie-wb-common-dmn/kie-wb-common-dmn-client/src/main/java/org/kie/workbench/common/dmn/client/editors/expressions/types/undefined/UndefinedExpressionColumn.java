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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;

public class UndefinedExpressionColumn extends DMNGridColumn<UndefinedExpressionGrid, String> implements HasListSelectorControl {

    private final CellEditorControlsView.Presenter cellEditorControls;
    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    public UndefinedExpressionColumn(final HeaderMetaData headerMetaData,
                                     final UndefinedExpressionGrid gridWidget,
                                     final CellEditorControlsView.Presenter cellEditorControls,
                                     final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier) {
        this(Collections.singletonList(headerMetaData),
             gridWidget,
             cellEditorControls,
             expressionEditorDefinitionsSupplier);
    }

    public UndefinedExpressionColumn(final List<HeaderMetaData> headerMetaData,
                                     final UndefinedExpressionGrid gridWidget,
                                     final CellEditorControlsView.Presenter cellEditorControls,
                                     final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier) {
        super(headerMetaData,
              new UndefinedExpressionColumnRenderer(),
              gridWidget);
        this.cellEditorControls = cellEditorControls;
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void edit(final GridCell<String> cell,
                     final GridBodyCellEditContext context,
                     final Consumer<GridCellValue<String>> callback) {
        final int uiRowIndex = context.getRowIndex();
        final int uiColumnIndex = context.getColumnIndex();
        final double absoluteCellX = context.getAbsoluteCellX();
        final double absoluteCellY = context.getAbsoluteCellY();

        if (cell == null) {
            return;
        }

        if (cell instanceof HasCellEditorControls) {
            final HasCellEditorControls hasControls = (HasCellEditorControls) cell;
            final Optional<HasCellEditorControls.Editor> editor = hasControls.getEditor();
            editor.ifPresent(e -> {
                e.bind(this, uiRowIndex, uiColumnIndex);
                final double[] dxy = {absoluteCellX, absoluteCellY};
                final Optional<com.ait.lienzo.client.core.types.Point2D> rx = context.getRelativeLocation();
                rx.ifPresent(r -> {
                    dxy[0] = r.getX();
                    dxy[1] = r.getY();
                });
                cellEditorControls.show(e,
                                        Optional.empty(),
                                        (int) (dxy[0]),
                                        (int) (dxy[1]));
            });
        }
    }

    @Override
    public void setWidth(final double width) {
        super.setWidth(width);
        updateWidthOfPeers();
    }

    @Override
    @SuppressWarnings("unused")
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        final List<ListSelectorItem> items = new ArrayList<>();
        items.addAll(expressionEditorDefinitionsSupplier
                             .get()
                             .stream()
                             .filter(definition -> definition.getModelClass().isPresent())
                             .map(this::makeListSelectorItem)
                             .collect(Collectors.toList()));

        return items;
    }

    ListSelectorTextItem makeListSelectorItem(final ExpressionEditorDefinition definition) {
        return ListSelectorTextItem.build(definition.getName(),
                                          true,
                                          () -> {
                                              cellEditorControls.hide();
                                              gridWidget.onExpressionTypeChanged(definition.getType());
                                          });
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        if (item instanceof ListSelectorTextItem) {
            final ListSelectorTextItem li = (ListSelectorTextItem) item;
            li.getCommand().execute();
        }
    }
}
