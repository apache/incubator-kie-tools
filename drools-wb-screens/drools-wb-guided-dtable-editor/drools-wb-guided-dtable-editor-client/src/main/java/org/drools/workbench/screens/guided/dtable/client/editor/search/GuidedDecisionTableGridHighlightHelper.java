/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.search;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import com.ait.lienzo.client.core.shape.IDrawable;
import com.ait.lienzo.client.core.shape.IPrimitive;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.util.GridHighlightHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

@ApplicationScoped
public class GuidedDecisionTableGridHighlightHelper {

    void highlight(final Integer row,
                   final Integer column,
                   final GuidedDecisionTableModellerView.Presenter modeller) {
        double paddingX = getPaddingX(column, modeller);
        double paddingY = getPaddingY(row, modeller);
        highlightHelper(modeller)
                .withMinX(getMinX(modeller))
                .withMinY(getMinY(modeller))
                .withPaddingX(paddingX)
                .withPaddingY(paddingY)
                .highlight(row, column);
    }

    private double getPaddingY(final Integer row,
                               final GuidedDecisionTableModellerView.Presenter modeller) {

        final List<GridRow> rows = getGridWidget(modeller).getModel().getRows();
        final double titleRowHeight = getHeight(rows, 0);
        final double headerRowHeight = getHeight(rows, 1);
        final double subHeaderRowHeight = getHeight(rows, 2);
        final double currentRowHeight = getHeight(rows, row);

        return titleRowHeight + headerRowHeight + subHeaderRowHeight + currentRowHeight;
    }

    private double getPaddingX(final Integer column,
                               final GuidedDecisionTableModellerView.Presenter modeller) {

        final List<GridColumn<?>> columns = getGridWidget(modeller).getModel().getColumns();
        final double idColumnWidth = getWidth(columns, 0);
        final double descriptionColumnWidth = getWidth(columns, 1);
        final double currentColumnWidth = getWidth(columns, column);

        return idColumnWidth + descriptionColumnWidth + currentColumnWidth;
    }

    private Double getMinX(final GuidedDecisionTableModellerView.Presenter modeller) {
        return mapVisibleGridWidgetsAndGetMin(IPrimitive::getX, modeller);
    }

    private Double getMinY(final GuidedDecisionTableModellerView.Presenter modeller) {
        return mapVisibleGridWidgetsAndGetMin(IPrimitive::getY, modeller);
    }

    private Double mapVisibleGridWidgetsAndGetMin(final Function<GridWidget, Double> mapper,
                                                  final GuidedDecisionTableModellerView.Presenter modeller) {
        return getVisibleGridWidgets(modeller)
                .map(mapper)
                .reduce(Double::min)
                .orElseThrow(UnsupportedOperationException::new);
    }

    private GridWidget getGridWidget(final GuidedDecisionTableModellerView.Presenter modeller) {
        return modeller
                .getView()
                .getGridWidgets()
                .stream()
                .filter(GridWidget::isSelected)
                .findFirst()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private Stream<GridWidget> getVisibleGridWidgets(final GuidedDecisionTableModellerView.Presenter modeller) {
        return modeller
                .getView()
                .getGridLayerView()
                .getGridWidgets()
                .stream()
                .filter(IDrawable::isVisible);
    }

    Double getWidth(final List<GridColumn<?>> columns,
                    final int index) {
        if (index < columns.size()) {
            return columns.get(index).getWidth();
        }
        return 0d;
    }

    Double getHeight(final List<GridRow> rows,
                     final int index) {
        if (index < rows.size()) {
            return rows.get(index).getHeight();
        }
        return 0d;
    }

    GridLienzoPanel getGridPanel(final GuidedDecisionTableModellerView.Presenter modeller) {
        return modeller.getView().getGridPanel();
    }

    GridHighlightHelper highlightHelper(final GuidedDecisionTableModellerView.Presenter modeller) {
        return new GridHighlightHelper(getGridPanel(modeller), getGridWidget(modeller));
    }
}
