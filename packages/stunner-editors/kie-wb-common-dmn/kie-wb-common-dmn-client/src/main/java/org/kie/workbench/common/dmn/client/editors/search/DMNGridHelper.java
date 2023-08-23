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

package org.kie.workbench.common.dmn.client.editors.search;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.util.GridHighlightHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

public class DMNGridHelper {

    private final SessionManager sessionManager;

    @Inject
    public DMNGridHelper(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void highlightCell(final int row,
                              final int column,
                              final GridWidget gridWidget) {
        highlightHelper(gridWidget)
                .withPaddingX(getIdColumnWidth(gridWidget, column))
                .withPaddingY(getHeaderRowHeight(gridWidget, row))
                .withPinnedGrid()
                .highlight(row, column);
    }

    public void focusGridPanel() {
        getGridPanel().setFocus(true);
    }

    private double getIdColumnWidth(final GridWidget gridWidget,
                                    final int column) {

        final List<GridColumn<?>> columns = gridWidget.getModel().getColumns();
        final double headerColumnWidth = getWidth(columns, 0);
        final double currentColumnWidth = getWidth(columns, column);

        return headerColumnWidth + currentColumnWidth;
    }

    private double getHeaderRowHeight(final GridWidget gridWidget,
                                      final int row) {

        final List<GridRow> rows = gridWidget.getModel().getRows();
        final double headerRowHeight = getHeight(rows, 0);
        final double currentRowHeight = getHeight(rows, row);

        return headerRowHeight + currentRowHeight;
    }

    public void clearSelections() {
        getGridWidgets().forEach(gridWidget -> highlightHelper(gridWidget).clearSelections());
    }

    public void clearCellHighlights() {
        getGridWidgets().forEach(gridWidget -> highlightHelper(gridWidget).clearHighlight());
    }

    public Set<GridWidget> getGridWidgets() {
        return getDefaultGridLayer()
                .getGridWidgets()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private DefaultGridLayer getDefaultGridLayer() {
        return getGridPanel().getDefaultGridLayer();
    }

    private DMNGridPanel getGridPanel() {
        return getCurrentSession().getGridPanel();
    }

    private DMNSession getCurrentSession() {
        return sessionManager.getCurrentSession();
    }

    GridHighlightHelper highlightHelper(final GridWidget gridWidget) {
        return new GridHighlightHelper(getGridPanel(), gridWidget);
    }

    private double getWidth(final List<GridColumn<?>> columns,
                            final int index) {
        if (index < columns.size()) {
            return columns.get(index).getWidth();
        }
        return 0;
    }

    private double getHeight(final List<GridRow> rows,
                             final int index) {
        if (index < rows.size()) {
            return rows.get(index).getHeight();
        }
        return 0;
    }
}
