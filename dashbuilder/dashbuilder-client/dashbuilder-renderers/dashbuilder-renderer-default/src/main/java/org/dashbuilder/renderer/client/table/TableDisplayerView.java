/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.client.table;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.client.AbstractGwtDisplayerView;
import org.dashbuilder.displayer.client.export.ExportFormat;
import org.dashbuilder.renderer.client.resources.i18n.TableConstants;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOVER;

public class TableDisplayerView extends AbstractGwtDisplayerView<TableDisplayer> implements TableDisplayer.View {

    protected HTML titleHtml = new HTML();
    protected TableProvider tableProvider = new TableProvider();
    protected VerticalPanel rootPanel = new VerticalPanel();
    protected PagedTable<Integer> table;
    protected Button exportToCsvButton;
    protected Button exportToXlsButton;

    @Override
    public void init(TableDisplayer presenter) {
        super.setPresenter(presenter);
        super.setVisualization(rootPanel);
        rootPanel.add(titleHtml);
    }

    @Override
    public void showTitle(String title) {
        titleHtml.setText(title);
    }

    @Override
    public String getGroupsTitle() {
        return TableConstants.INSTANCE.tableDisplayer_groupsTitle();
    }

    @Override
    public String getColumnsTitle() {
        return TableConstants.INSTANCE.tableDisplayer_columnsTitle();
    }

    @Override
    public void createTable(int pageSize, FilterLabelSet filterLabelSet) {
        table = new PagedTable<>(pageSize);
        table.pageSizesSelector.setVisible(false);
        table.setEmptyTableCaption(TableConstants.INSTANCE.tableDisplayer_noDataAvailable());
        table.pageSizesSelector.setForceDropup(true);
        table.pageSizesSelector.setDropupAuto(false);
        tableProvider.addDataDisplay(table);
        tableProvider.lastPageSize = pageSize;

        HTMLElement element = filterLabelSet.getElement();
        element.getStyle().setProperty("margin-bottom", "5px");
        table.getLeftToolbar().add(ElementWrapperWidget.getWidget(filterLabelSet.getElement()));

        exportToCsvButton = new Button("", IconType.FILE_TEXT, e -> getPresenter().export(ExportFormat.CSV));
        exportToXlsButton = new Button("", IconType.FILE_EXCEL_O, e -> getPresenter().export(ExportFormat.XLS));
        exportToCsvButton.setTitle(TableConstants.INSTANCE.tableDisplayer_export_to_csv());
        exportToXlsButton.setTitle(TableConstants.INSTANCE.tableDisplayer_export_to_xls());

        setupToolbar();
        rootPanel.add(table);
    }

    protected void setupToolbar() {
        HasWidgets rightToolbar = table.getRightToolbar();
        if (rightToolbar instanceof HorizontalPanel) {
            ((HorizontalPanel) rightToolbar).insert(exportToCsvButton,
                                                    0);
            ((HorizontalPanel) rightToolbar).insert(exportToXlsButton,
                                                    1);
        } else {
            rightToolbar.add(exportToCsvButton);
            rightToolbar.add(exportToXlsButton);
        }
    }

    @Override
    public void redrawTable() {
        table.redraw();
    }

    @Override
    public void setWidth(int width) {
        table.setWidth(width + "px");
    }

    @Override
    public void setSortEnabled(boolean enabled) {
        table.addColumnSortHandler(new ColumnSortEvent.AsyncHandler(table) {
            public void onColumnSort(ColumnSortEvent event) {
                String column = ((DataColumnCell) event.getColumn().getCell()).columnId;
                SortOrder order = event.isSortAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING;
                getPresenter().sortBy(column, order);
            }
        });
    }

    @Override
    public void setTotalRows(int rows, boolean isExact) {
        if(table != null) {
            table.setRowCount(rows, isExact);
        }
    }

    @Override
    public void setPagerEnabled(boolean enabled) {
        table.pager.setVisible(enabled);
    }

    @Override
    public void setColumnPickerEnabled(boolean enabled) {
        table.setColumnPickerButtonVisible(enabled);
    }

    @Override
    public void setExportToCsvEnabled(boolean enabled) {
        exportToCsvButton.setVisible(enabled);
    }

    @Override
    public void setExportToXlsEnabled(boolean enabled) {
        exportToXlsButton.setVisible(enabled);
    }

    @Override
    public void addColumn(ColumnType columnType, String columnId, String columnName, int index, boolean selectEnabled, boolean sortEnabled) {
        Column<Integer,?> column = createColumn(columnType, columnId, selectEnabled, index);
        if (column != null) {
            column.setSortable(sortEnabled);
            table.addColumn(column, columnName);
        }
    }

    @Override
    public void gotoFirstPage() {
        tableProvider.gotoFirstPage();
    }

    @Override
    public int getLastOffset() {
        return tableProvider.lastOffset;
    }

    @Override
    public int getPageSize() {
        return tableProvider.lastPageSize;
    }

    @Override
    public void exportNoData() {
        Window.alert(TableConstants.INSTANCE.tableDisplayer_export_no_data());
    }

    @Override
    public void exportTooManyRows(int rowNum, int limit) {
        Window.alert(TableConstants.INSTANCE.tableDisplayer_export_too_many_rows(rowNum, limit));
    }

    @Override
    public void exportFileUrl(String url) {
        Window.open(url, "downloading", "resizable=no,scrollbars=yes,status=no");
    }

    // Table internals

    protected Column<Integer,?> createColumn(ColumnType type,
                                             String columnId,
                                             final boolean selectable,
                                             final int columnNumber) {

        switch (type) {
            case LABEL: return new Column<Integer,String>(new DataColumnCell(columnId, selectable)) {
                            public String getValue(Integer row) {
                                return getPresenter().formatValue(row, columnNumber);
                            }
                        };

            case NUMBER:
            case DATE:
            case TEXT: return new Column<Integer,String>(new DataColumnCell(columnId, selectable)) {
                            public String getValue(Integer row) {
                                return getPresenter().formatValue(row, columnNumber);
                            }
                        };
        }
        return null;
    }

    protected class DataColumnCell extends TextCell {

        private String columnId;
        private boolean selectable = false;

        DataColumnCell(String columnId, boolean selectable) {
            this.columnId = columnId;
            this.selectable = selectable;
        }

        @Override
        public Set<String> getConsumedEvents() {
            Set<String> consumedEvents = new HashSet<String>();
            if (selectable) {
                consumedEvents.add(CLICK);
                consumedEvents.add(MOUSEOVER);
            }
            return consumedEvents;
        }

        @Override
        public void onBrowserEvent(Context context,
                                   Element parent,
                                   String value,
                                   NativeEvent event,
                                   ValueUpdater<String> valueUpdater) {

            if (selectable) {
                String eventType = event.getType();
                switch (eventType) {

                    case MOUSEOVER:
                        parent.getStyle().setCursor(Style.Cursor.POINTER);
                        break;

                    case CLICK:
                        int rowIndexInPage = context.getIndex() - table.getPageStart();
                        getPresenter().selectCell(columnId, rowIndexInPage);
                        break;
                }
            }
        }
    }

    /**
     * The table data provider
     */
    protected class TableProvider extends AsyncDataProvider<Integer> {

        protected int lastOffset = 0;
        protected int lastPageSize = 0;

        protected List<Integer> getCurrentPageRows(HasData<Integer> display) {
            final int start = display.getVisibleRange().getStart();
            int pageSize =  display.getVisibleRange().getLength();
            int items = Integer.min(pageSize, table.getRowCount() > start ? table.getRowCount() - start : table.getRowCount());
            return IntStream.range(0, items).boxed().collect(Collectors.toList());
        }

        /**
         * Both filter & sort invoke this method from redraw()
         */
        public void gotoFirstPage() {
            // Avoid fetching the data set again
            lastOffset = 0;
            lastPageSize = table.getVisibleRange().getLength();

            // This calls internally to onRangeChanged() when the page changes
            table.pager.setPage(0);

            int start = table.getVisibleRange().getStart();
            final List<Integer> rows = getCurrentPageRows(table);
            updateRowData(start, rows);
        }

        /**
         * Invoked from createWidget just after the data set has been fetched.
         */
        public void addDataDisplay(HasData<Integer> display) {
            // Avoid fetching the data set again
            lastOffset = 0;
            lastPageSize = table.getVisibleRange().getLength();

            // This calls internally to onRangeChanged()
            super.addDataDisplay(display);
        }

        /**
         * This is invoked internally by the PagedTable on navigation actions.
         */
        protected void onRangeChanged(final HasData<Integer> display) {
            final Range range = display.getVisibleRange();
            if (lastOffset == range.getStart() && range.getLength() <= lastPageSize) {
                lastPageSize = range.getLength();
                if(table.getRowCount() > range.getLength()) {
                    setPagerEnabled(true);
                }
                updateRowData(lastOffset, getCurrentPageRows(display));
            }  else {
                lastOffset = range.getStart();
                lastPageSize = range.getLength();
                getPresenter().lookupCurrentPage(rowsFetched -> {
                    final List<Integer> rows = getCurrentPageRows(display);
                    updateRowData(lastOffset, rows);
                });
            }
        }
    }
}
