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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Optional;
import java.util.function.Consumer;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.NameAndDataTypeDOMElementColumnRenderer;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNSimpleGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.HasSingletonDOMElementResource;

public class RelationColumn extends DMNSimpleGridColumn<RelationGrid, String> implements HasSingletonDOMElementResource {

    private final TextAreaSingletonDOMElementFactory factory;

    public RelationColumn(final HeaderMetaData headerMetaData,
                          final TextAreaSingletonDOMElementFactory factory,
                          final RelationGrid gridWidget) {
        super(headerMetaData,
              new NameAndDataTypeDOMElementColumnRenderer<>(factory),
              gridWidget);
        this.factory = PortablePreconditions.checkNotNull("factory",
                                                          factory);
        setMovable(true);
        setResizable(false);
    }

    @Override
    public Double getMinimumWidth() {
        final double minimumWidth = super.getMinimumWidth();
        final double minimumWidthOfPeers = getMinimumWidthOfPeers();
        final double widthOfThisEditor = gridWidget.getWidth();
        final double widthOfThisColumn = getWidth();

        return Math.max(minimumWidth,
                        minimumWidthOfPeers - (widthOfThisEditor - widthOfThisColumn));
    }

    private double getMinimumWidthOfPeers() {
        final GridCellTuple parent = gridWidget.getParentInformation();
        final GridData parentUiModel = parent.getGridWidget().getModel();
        final int parentUiRowIndex = parent.getRowIndex();
        final int parentUiColumnIndex = parent.getColumnIndex();

        double minimumWidth = super.getMinimumWidth();

        for (int uiRowIndex = 0; uiRowIndex < parentUiModel.getRowCount(); uiRowIndex++) {
            if (uiRowIndex != parentUiRowIndex) {
                final GridRow row = parentUiModel.getRow(uiRowIndex);
                final GridCell<?> cell = row.getCells().get(parentUiColumnIndex);
                if (cell != null) {
                    final GridCellValue<?> value = cell.getValue();
                    if (value instanceof ExpressionCellValue) {
                        final ExpressionCellValue ecv = (ExpressionCellValue) value;
                        final Optional<BaseExpressionGrid> editor = ecv.getValue();
                        final double padding = editor.map(BaseExpressionGrid::getPadding).get();
                        minimumWidth = Math.max(minimumWidth,
                                                ecv.getMinimumWidth().orElse(0.0) + padding * 2);
                    }
                }
            }
        }

        return minimumWidth;
    }

    @Override
    public void edit(final GridCell<String> cell,
                     final GridBodyCellRenderContext context,
                     final Consumer<GridCellValue<String>> callback) {
        factory.attachDomElement(context,
                                 (e) -> e.setValue(assertCellValue(assertCell(cell).getValue()).getValue()),
                                 (e) -> e.setFocus(true));
    }

    @Override
    protected GridCellValue<String> makeDefaultCellValue() {
        return new BaseGridCellValue<>("");
    }

    @Override
    public void flush() {
        factory.flush();
    }

    @Override
    public void destroyResources() {
        factory.destroyResources();
        getHeaderMetaData().stream()
                .filter(md -> md instanceof HasDOMElementResources)
                .map(md -> (HasDOMElementResources) md)
                .forEach(HasDOMElementResources::destroyResources);
    }

    @Override
    public void setWidth(final double width) {
        super.setWidth(width);
        updateWidthOfPeers();
    }
}
