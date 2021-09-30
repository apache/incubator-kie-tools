/*
 * Copyright 2016 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.widgets.table.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.*;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.uberfire.ext.widgets.table.client.resources.UFTableResources;

/**
 * A composite Widget that shows rows of data (not-paged) and a "column picker"
 * to allow columns to be hidden from view. Columns can also be sorted.
 */
public class UberfireSimpleTable<T>
        extends Composite
        implements HasData<T> {

    private static Binder uiBinder = GWT.create(Binder.class);
    @UiField(provided = true)
    public Button columnPickerButton;
    @UiField(provided = true)
    public DataGrid<T> dataGrid;
    @UiField
    public ComplexPanel toolbarContainer;
    @UiField
    public ComplexPanel rightToolbar;
    @UiField
    public FlowPanel rightActionsToolbar;
    @UiField
    public FlowPanel leftToolbar;
    @UiField
    public FlowPanel centerToolbar;
    protected UberfireColumnPicker<T> columnPicker;
    private String emptyTableCaption;

    public UberfireSimpleTable() {
        setupDataGrid(null);
        setupGridTable();
    }

    public UberfireSimpleTable(final ProvidesKey<T> providesKey) {
        setupDataGrid(providesKey);
        setupGridTable();
    }

    protected static native void addDataGridStyles(final JavaScriptObject grid,
                                                   final String header,
                                                   final String content)/*-{
        $wnd.jQuery(grid).find('table:first').addClass(header);
        $wnd.jQuery(grid).find('table:last').addClass(content);
    }-*/;

    public void addDataGridStyles(final String header,
                                  final String content) {
        addDataGridStyles(dataGrid.getElement(),
                          header,
                          content);
    }

    protected void setupGridTable() {
        setupDataGrid();
        setEmptyTableWidget();

        setupColumnPicker();

        columnPickerButton = getColumnPicker().createToggleButton();

        initWidget(makeWidget());
    }

    protected void setupColumnPicker() {
        setColumnPicker(new UberfireColumnPicker<>(dataGrid));
    }

    protected UberfireColumnPicker<T> getColumnPicker() {
        return columnPicker;
    }

    protected void setColumnPicker(UberfireColumnPicker<T> columnPicker) {
        this.columnPicker = columnPicker;
    }

    protected void setupDataGrid(ProvidesKey<T> providesKey) {
        if (providesKey != null) {
            dataGrid = new DataGrid<T>(providesKey);
        } else {
            dataGrid = new DataGrid<T>();
        }
    }

    public void setEmptyTableCaption(final String emptyTableCaption) {
        this.emptyTableCaption = emptyTableCaption;
        setEmptyTableWidget();
    }

    protected void setupDataGrid() {
        dataGrid.setSkipRowHoverCheck(false);
        dataGrid.setSkipRowHoverStyleUpdate(false);
        dataGrid.addStyleName(UFTableResources.INSTANCE.CSS().dataGridMain());
        dataGrid.addStyleName(UFTableResources.INSTANCE.CSS().dataGrid());
        dataGrid.setRowStyles((row, rowIndex) -> UFTableResources.INSTANCE.CSS().dataGridRow());
        addDataGridStyles(UFTableResources.INSTANCE.CSS().dataGridHeader(),
                          UFTableResources.INSTANCE.CSS().dataGridContent());
    }

    protected void setEmptyTableWidget() {
        String caption = "-----";
        if (!emptyCaptionIsDefined()) {
            caption = emptyTableCaption;
        }
        dataGrid.setEmptyTableWidget(new Label(caption));
    }

    private boolean emptyCaptionIsDefined() {
        return emptyTableCaption == null || emptyTableCaption.trim().isEmpty();
    }

    protected Widget makeWidget() {
        return uiBinder.createAndBindUi(this);
    }

    public void redraw() {
        dataGrid.redraw();
        dataGrid.flush();
    }

    public void refresh() {
        dataGrid.setVisibleRangeAndClearData(dataGrid.getVisibleRange(),
                                             true);
    }

    @Override
    public HandlerRegistration addCellPreviewHandler(final CellPreviewEvent.Handler<T> handler) {
        return dataGrid.addCellPreviewHandler(handler);
    }

    @Override
    public HandlerRegistration addRangeChangeHandler(final RangeChangeEvent.Handler handler) {
        return dataGrid.addRangeChangeHandler(handler);
    }

    @Override
    public HandlerRegistration addRowCountChangeHandler(final RowCountChangeEvent.Handler handler) {
        return dataGrid.addRowCountChangeHandler(handler);
    }

    public int getColumnIndex(final Column<T, ?> column) {
        return dataGrid.getColumnIndex(column);
    }

    public HandlerRegistration addColumnSortHandler(final ColumnSortEvent.Handler handler) {
        return this.dataGrid.addColumnSortHandler(handler);
    }

    @Override
    public int getRowCount() {
        return dataGrid.getRowCount();
    }

    @Override
    public void setRowCount(final int count) {
        dataGrid.setRowCount(count);
    }

    @Override
    public Range getVisibleRange() {
        return dataGrid.getVisibleRange();
    }

    @Override
    public void setVisibleRange(final Range range) {
        dataGrid.setVisibleRange(range);
    }

    @Override
    public boolean isRowCountExact() {
        return dataGrid.isRowCountExact();
    }

    @Override
    public void setRowCount(final int count,
                            final boolean isExact) {
        dataGrid.setRowCount(count,
                             isExact);
    }

    @Override
    public void setVisibleRange(final int start,
                                final int length) {
        dataGrid.setVisibleRange(start,
                                 length);
    }

    @Override
    public SelectionModel<? super T> getSelectionModel() {
        return dataGrid.getSelectionModel();
    }

    @Override
    public void setSelectionModel(final SelectionModel<? super T> selectionModel) {
        dataGrid.setSelectionModel(selectionModel);
    }

    @Override
    public T getVisibleItem(final int indexOnPage) {
        return dataGrid.getVisibleItem(indexOnPage);
    }

    @Override
    public int getVisibleItemCount() {
        return dataGrid.getVisibleItemCount();
    }

    @Override
    public Iterable<T> getVisibleItems() {
        return dataGrid.getVisibleItems();
    }

    @Override
    public void setRowData(final int start,
                           final List<? extends T> values) {
        dataGrid.setRowData(start,
                            values);
        redraw();
    }

    public void setRowData(final List<? extends T> values) {
        dataGrid.setRowData(values);
        redraw();
    }

    public void setSelectionModel(final SelectionModel<? super T> selectionModel,
                                  final CellPreviewEvent.Handler<T> selectionEventManager) {
        dataGrid.setSelectionModel(selectionModel,
                                   selectionEventManager);
    }

    @Override
    public void setVisibleRangeAndClearData(final Range range,
                                            final boolean forceRangeChangeEvent) {
        dataGrid.setVisibleRangeAndClearData(range,
                                             forceRangeChangeEvent);
    }

    @Override
    public void setHeight(final String height) {
        dataGrid.setHeight(height);
    }

    @Override
    public void setPixelSize(final int width,
                             final int height) {
        dataGrid.setPixelSize(width,
                              height);
    }

    @Override
    public void setSize(final String width,
                        final String height) {
        dataGrid.setSize(width,
                         height);
    }

    @Override
    public void setWidth(final String width) {
        dataGrid.setWidth(width);
    }

    public void setColumnWidth(final Column<T, ?> column,
                               final double width,
                               final Style.Unit unit) {
        dataGrid.setColumnWidth(column,
                                width,
                                unit);
        getColumnPicker().adjustColumnWidths();
    }

    public void setToolBarVisible(final boolean visible) {
        toolbarContainer.setVisible(visible);
    }

    public ColumnSortList getColumnSortList() {
        return dataGrid.getColumnSortList();
    }

    public HasWidgets getToolbar() {
        return toolbarContainer;
    }

    public HasWidgets getRightToolbar() {
        return rightToolbar;
    }

    public HasWidgets getRightActionsToolbar() {
        return rightActionsToolbar;
    }

    public HasWidgets getLeftToolbar() {
        return leftToolbar;
    }

    public HasWidgets getCenterToolbar() {
        return centerToolbar;
    }

    public void setRowStyles(final RowStyles<T> styles) {
        dataGrid.setRowStyles(styles);
    }

    public void addTableTitle(String tableTitle) {
        getLeftToolbar().add(new HTML("<h4>" + tableTitle + "</h4>"));
    }

    public void setAlwaysShowScrollBars(boolean alwaysShowScrollBars) {
        dataGrid.setAlwaysShowScrollBars(alwaysShowScrollBars);
    }

    public void addColumn(final Column<T, ?> column,
                          final String caption) {
        addColumn(column,
                  caption,
                  true);
    }

    public void addColumn(final Column<T, ?> column,
                          final String caption,
                          final boolean visible) {
        ColumnMeta<T> columnMeta = new ColumnMeta<T>(column,
                                                     caption,
                                                     visible);
        addColumn(columnMeta);
    }

    public void addColumns(final List<ColumnMeta<T>> columnMetas) {
        for (ColumnMeta columnMeta : columnMetas) {
            if (columnMeta.getHeader() == null) {
                columnMeta.setHeader(getColumnHeader(columnMeta.getCaption(),
                                                     columnMeta.getColumn()));
            }
        }
        getColumnPicker().addColumns(columnMetas);
    }

    protected void addColumn(final ColumnMeta<T> columnMeta) {
        if (columnMeta.getHeader() == null) {
            columnMeta.setHeader(getColumnHeader(columnMeta.getCaption(),
                                                 columnMeta.getColumn()));
        }
        getColumnPicker().addColumn(columnMeta);
    }

    protected ResizableMovableHeader<T> getColumnHeader(final String caption,
                                                        final Column column) {
        final ResizableMovableHeader header = new ResizableMovableHeader<T>(caption,
                                                                            dataGrid,
                                                                            columnPicker,
                                                                            column) {
            @Override
            protected int getTableBodyHeight() {
                return dataGrid.getOffsetHeight();
            }
        };
        header.addColumnChangedHandler(new ColumnChangedHandler() {
            @Override
            public void afterColumnChanged() {
                afterColumnChangedHandler();
            }

            @Override
            public void beforeColumnChanged() {

            }
        });
        return header;
    }

    public void setColumnPickerButtonVisible(final boolean show) {
        columnPickerButton.setVisible(show);
    }

    protected void afterColumnChangedHandler() {

    }

    interface Binder
            extends
            UiBinder<Widget, UberfireSimpleTable> {

    }
}
