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

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import com.ait.lienzo.client.core.shape.IDrawable;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.google.gwt.i18n.client.DateTimeFormat;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.search.common.Searchable;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.util.GridHighlightHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.mvp.Command;

public class GuidedDecisionTableSearchableElement implements Searchable {

    private static final String NONE = "";

    private final DateTimeFormat formatter = getFormat();

    private DTCellValue52 cellValue52;

    private Integer row;

    private Integer column;

    private GuidedDecisionTableModellerView.Presenter modeller;

    public void setCellValue52(final DTCellValue52 cellValue52) {
        this.cellValue52 = cellValue52;
    }

    @Override
    public boolean matches(final String text) {
        return getValue().toUpperCase().contains(text.toUpperCase());
    }

    public String getValue() {
        return convertDTCellValueToString(cellValue52);
    }

    @Override
    public Command onFound() {
        return () -> {
            double paddingX = getPaddingX(column);
            double paddingY = getPaddingY(row);
            highlightHelper()
                    .withMinX(getMinX())
                    .withMinY(getMinY())
                    .withPaddingX(paddingX)
                    .withPaddingY(paddingY)
                    .highlight(row, column);
        };
    }

    private double getPaddingY(final Integer row) {

        final List<GridRow> rows = getGridWidget().getModel().getRows();
        final double titleRowHeight = getHeight(rows, 0);
        final double headerRowHeight = getHeight(rows, 1);
        final double subHeaderRowHeight = getHeight(rows, 2);
        final double currentRowHeight = getHeight(rows, row);

        return titleRowHeight + headerRowHeight + subHeaderRowHeight + currentRowHeight;
    }

    private double getPaddingX(final Integer column) {

        final List<GridColumn<?>> columns = getGridWidget().getModel().getColumns();
        final double idColumnWidth = getWidth(columns, 0);
        final double descriptionColumnWidth = getWidth(columns, 1);
        final double currentColumnWidth = getWidth(columns, column);

        return idColumnWidth + descriptionColumnWidth + currentColumnWidth;
    }

    String convertDTCellValueToString(final DTCellValue52 cellValue52) {

        final String stringValue = cellValue52.getStringValue();
        final String numericValue = getStringValue(cellValue52.getNumericValue());
        final String booleanValue = getStringValue(cellValue52.getBooleanValue());
        final String dateValue = getStringValue(cellValue52.getDateValue());

        return nonNull(stringValue, numericValue, booleanValue, dateValue);
    }

    private String nonNull(final String... values) {
        return Stream.of(values).filter(Objects::nonNull).findFirst().orElse(NONE);
    }

    private String getStringValue(final Number number) {
        return number == null ? null : number.toString();
    }

    private String getStringValue(final Boolean bool) {
        return bool == null ? null : bool.toString();
    }

    private String getStringValue(final Date date) {
        return date == null ? null : formatter.format(date);
    }

    private DateTimeFormat getFormat() {
        return DateTimeFormat.getFormat(ApplicationPreferences.getDroolsDateFormat());
    }

    public void setRow(final int row) {
        this.row = row;
    }

    public void setColumn(final int column) {
        this.column = column;
    }

    private Double getMinX() {
        return mapVisibleGridWidgetsAndGetMin(IPrimitive::getX);
    }

    private Double getMinY() {
        return mapVisibleGridWidgetsAndGetMin(IPrimitive::getY);
    }

    private Double mapVisibleGridWidgetsAndGetMin(final Function<GridWidget, Double> mapper) {
        return getVisibleGridWidgets()
                .map(mapper)
                .reduce(Double::min)
                .orElseThrow(UnsupportedOperationException::new);
    }

    private Stream<GridWidget> getVisibleGridWidgets() {
        return getView()
                .getGridLayerView()
                .getGridWidgets()
                .stream()
                .filter(IDrawable::isVisible);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GuidedDecisionTableSearchableElement that = (GuidedDecisionTableSearchableElement) o;

        if (!cellValue52.equals(that.cellValue52)) {
            return false;
        }
        if (!row.equals(that.row)) {
            return false;
        }
        return column.equals(that.column);
    }

    @Override
    public int hashCode() {
        int result = cellValue52.hashCode();
        result = ~~result;
        result = 31 * result + row.hashCode();
        result = ~~result;
        result = 31 * result + column.hashCode();
        result = ~~result;
        return result;
    }

    GridHighlightHelper highlightHelper() {
        return makeGridHighlightHelper(getGridPanel(), getGridWidget());
    }

    GridHighlightHelper makeGridHighlightHelper(final GridLienzoPanel gridPanel,
                                                final GridWidget gridWidget) {
        return new GridHighlightHelper(gridPanel, gridWidget);
    }

    private GridWidget getGridWidget() {
        return getView()
                .getGridWidgets()
                .stream()
                .filter(GridWidget::isSelected)
                .findFirst()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private GridLienzoPanel getGridPanel() {
        return getView().getGridPanel();
    }

    private GuidedDecisionTableModellerView getView() {
        return getModeller().getView();
    }

    public void setModeller(final GuidedDecisionTableModellerView.Presenter modeller) {
        this.modeller = modeller;
    }

    public GuidedDecisionTableModellerView.Presenter getModeller() {
        return modeller;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
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
