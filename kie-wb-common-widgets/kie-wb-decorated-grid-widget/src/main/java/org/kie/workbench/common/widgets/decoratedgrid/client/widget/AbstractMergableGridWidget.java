/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicData;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicDataRow;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.GroupedDynamicDataRow;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.RowMapper;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.CellStateChangedEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.ColumnResizeEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.CopyRowsEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteColumnEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertInternalColumnEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.MoveColumnsEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.PasteRowsEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.RowGroupingChangeEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SelectedCellChangeEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SetColumnVisibilityEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SetInternalModelEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SortDataEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.ToggleMergingEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDefinitionEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateModelEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateSelectedCellsEvent;

/**
 * An abstract grid of data. Implementations can choose the orientation to
 * render "rows" and "columns" (e.g. some may transpose the normal meaning to
 * provide a horizontal implementation of normally vertical tabular data)
 */
public abstract class AbstractMergableGridWidget<M, T> extends Widget
        implements
        ToggleMergingEvent.Handler,
        DeleteRowEvent.Handler,
        InsertRowEvent.Handler,
        AppendRowEvent.Handler,
        CopyRowsEvent.Handler,
        PasteRowsEvent.Handler,
        DeleteColumnEvent.Handler,
        SetInternalModelEvent.Handler<M, T>,
        InsertInternalColumnEvent.Handler<T>,
        SetColumnVisibilityEvent.Handler,
        UpdateColumnDataEvent.Handler,
        UpdateColumnDefinitionEvent.Handler,
        ColumnResizeEvent.Handler,
        MoveColumnsEvent.Handler,
        SortDataEvent.Handler,
        UpdateSelectedCellsEvent.Handler,
        CellStateChangedEvent.Handler {

    /**
     * Container for a details of a selected cell
     */
    public static class CellSelectionDetail {

        private Coordinate c;
        private int offsetX;
        private int offsetY;
        private int height;
        private int width;

        CellSelectionDetail(Coordinate c,
                            int offsetX,
                            int offsetY,
                            int height,
                            int width) {
            this.c = c;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.height = height;
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }

        public int getWidth() {
            return width;
        }

        public Coordinate getCoordinate() {
            return c;
        }
    }

    // Enum to support keyboard navigation
    public enum MOVE_DIRECTION {
        LEFT,
        RIGHT,
        UP,
        DOWN,
        NONE
    }

    //GWT disable text selection in an HTMLTable. 
    //event.stopPropogation() doesn't prevent text selection
    private native static void disableTextSelectInternal(Element e,
                                                         boolean disable)/*-{
        if (disable) {
            e.ondrag = function () {
                return false;
            };
            e.onselectstart = function () {
                return false;
            };
            e.style.MozUserSelect = "none"
        } else {
            e.ondrag = null;
            e.onselectstart = null;
            e.style.MozUserSelect = "text"
        }
    }-*/;

    // Selections store the actual grid data selected (irrespective of
    // merged cells). So a merged cell spanning 2 rows is stored as 2
    // selections. Selections are ordered by row number so we can
    // iterate top to bottom.
    protected TreeSet<CellValue<? extends Comparable<?>>> selections = new TreeSet<CellValue<? extends Comparable<?>>>(
            new Comparator<CellValue<? extends Comparable<?>>>() {

                public int compare(CellValue<? extends Comparable<?>> o1,
                                   CellValue<? extends Comparable<?>> o2) {
                    return o1.getPhysicalCoordinate().getRow()
                            - o2.getPhysicalCoordinate().getRow();
                }
            });

    // TABLE elements
    protected TableElement table;

    protected TableSectionElement tbody;

    // Resources
    protected ResourcesProvider<T> resources;
    protected EventBus eventBus;

    protected String selectorGroupedCellsHtml;
    protected String selectorUngroupedCellsHtml;

    // Data and columns to render
    protected List<DynamicColumn<T>> columns;
    protected DynamicData data;
    protected RowMapper rowMapper;
    protected AbstractCellFactory<T> cellFactory;
    protected AbstractCellValueFactory<T, ?> cellValueFactory;
    protected CellTableDropDownDataValueMapProvider dropDownManager;

    //Properties for multi-cell selection
    protected CellValue<?> rangeOriginCell;
    protected CellValue<?> rangeExtentCell;

    protected MOVE_DIRECTION rangeDirection = MOVE_DIRECTION.NONE;
    protected boolean bDragOperationPrimed = false;

    //Rows that have been copied in a copy-paste operation
    private List<DynamicDataRow> copiedRows = new ArrayList<DynamicDataRow>();

    protected static final RowGroupingChangeEvent ROW_GROUPING_EVENT = new RowGroupingChangeEvent();

    protected final boolean isReadOnly;

    /**
     * A grid of cells.
     */
    public AbstractMergableGridWidget(ResourcesProvider<T> resources,
                                      AbstractCellFactory<T> cellFactory,
                                      AbstractCellValueFactory<T, ?> cellValueFactory,
                                      CellTableDropDownDataValueMapProvider dropDownManager,
                                      boolean isReadOnly,
                                      EventBus eventBus) {
        this.resources = resources;
        this.cellFactory = cellFactory;
        this.cellValueFactory = cellValueFactory;
        this.dropDownManager = dropDownManager;
        this.isReadOnly = isReadOnly;
        this.eventBus = eventBus;

        ImageResource selectorGroupedCells = resources.collapseCellsIcon();
        ImageResource selectorUngroupedCells = resources.expandCellsIcon();
        this.selectorGroupedCellsHtml = makeImageHtml(selectorGroupedCells);
        this.selectorUngroupedCellsHtml = makeImageHtml(selectorUngroupedCells);

        // Create some elements to contain the grid
        table = Document.get().createTableElement();
        tbody = Document.get().createTBodyElement();
        table.setClassName(resources.cellTable());
        table.setCellPadding(0);
        table.setCellSpacing(0);
        setElement(table);

        table.appendChild(tbody);

        // Events in which we're interested (note, if a Cell<?> appears not to
        // work I've probably forgotten some events. Might be a better way of
        // doing this, but I copied CellTable<?, ?>'s lead
        sinkEvents(Event.getTypeInt("click")
                           | Event.getTypeInt("dblclick")
                           | Event.getTypeInt("mousedown")
                           | Event.getTypeInt("mouseup")
                           | Event.getTypeInt("mousemove")
                           | Event.getTypeInt("mouseout")
                           | Event.getTypeInt("change")
                           | Event.getTypeInt("keypress")
                           | Event.getTypeInt("keydown"));

        //Prevent text selection
        disableTextSelectInternal(table,
                                  true);

        //Wire-up events
        eventBus.addHandler(ToggleMergingEvent.TYPE,
                            this);
        eventBus.addHandler(DeleteRowEvent.TYPE,
                            this);
        eventBus.addHandler(InsertRowEvent.TYPE,
                            this);
        eventBus.addHandler(AppendRowEvent.TYPE,
                            this);
        eventBus.addHandler(CopyRowsEvent.TYPE,
                            this);
        eventBus.addHandler(PasteRowsEvent.TYPE,
                            this);
        eventBus.addHandler(DeleteColumnEvent.TYPE,
                            this);
        eventBus.addHandler(SetColumnVisibilityEvent.TYPE,
                            this);
        eventBus.addHandler(UpdateColumnDataEvent.TYPE,
                            this);
        eventBus.addHandler(UpdateColumnDefinitionEvent.TYPE,
                            this);
        eventBus.addHandler(ColumnResizeEvent.TYPE,
                            this);
        eventBus.addHandler(MoveColumnsEvent.TYPE,
                            this);
        eventBus.addHandler(SortDataEvent.TYPE,
                            this);
        eventBus.addHandler(UpdateSelectedCellsEvent.TYPE,
                            this);
        eventBus.addHandler(CellStateChangedEvent.TYPE,
                            this);
    }

    private static String makeImageHtml(ImageResource image) {
        return AbstractImagePrototype.create(image).getHTML();
    }

    /**
     * Redraw the whole table
     */
    abstract void redraw();

    /**
     * Redraw table columns. Partial redraw
     * @param startRedrawIndex Start column index (inclusive)
     * @param endRedrawIndex End column index (inclusive)
     */
    abstract void redrawColumns(int startRedrawIndex,
                                int endRedrawIndex);

    //Apply grouping by collapsing applicable rows
    private void applyModelGrouping(CellValue<?> startCell,
                                    boolean bRedraw) {

        data.applyModelGrouping(startCell);

        //Partial redraw
        if (bRedraw) {
            int startRowIndex = startCell.getCoordinate().getRow();
            GroupedDynamicDataRow groupedRow = (GroupedDynamicDataRow) data.get(startRowIndex);
            int minRedrawRow = findMinRedrawRow(startRowIndex - (startRowIndex > 0 ? 1 : 0));
            int maxRedrawRow = findMaxRedrawRow(startRowIndex + (startRowIndex < data.size() - 1 ? 1 : 0));
            for (int iRow = 0; iRow < groupedRow.getChildRows().size() - 1; iRow++) {
                deleteRowElement(startRowIndex);
            }
            redrawRows(minRedrawRow,
                       maxRedrawRow);
            eventBus.fireEvent(ROW_GROUPING_EVENT);
        }
    }

    //Check whether two values are equal or both null
    private boolean equalOrNull(Object o1,
                                Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        }
        if (o1 != null && o2 == null) {
            return false;
        }
        if (o1 == null && o2 != null) {
            return false;
        }
        return o1.equals(o2);
    }

    // Given a base row find the maximum row that needs to be re-rendered based
    // upon each columns merged cells; where each merged cell passes through the
    // base row
    private int findMaxRedrawRow(int baseRowIndex) {

        if (data.size() == 0) {
            return 0;
        }

        // These should never happen, but it's a safe-guard
        if (baseRowIndex < 0) {
            baseRowIndex = 0;
        }
        if (baseRowIndex > data.size() - 1) {
            baseRowIndex = data.size() - 1;
        }

        int maxRedrawRow = baseRowIndex;
        DynamicDataRow baseRow = data.get(baseRowIndex);
        for (int iCol = 0; iCol < baseRow.size(); iCol++) {
            int iRow = baseRowIndex;
            CellValue<? extends Comparable<?>> cell = baseRow.get(iCol);
            while (cell.getRowSpan() != 1 && iRow < data.size() - 1) {
                iRow++;
                DynamicDataRow row = data.get(iRow);
                cell = row.get(iCol);
            }
            maxRedrawRow = (iRow > maxRedrawRow ? iRow : maxRedrawRow);
        }
        return maxRedrawRow;
    }

    //Find the bottom coordinate of a merged cell
    private Coordinate findMergedCellExtent(Coordinate c) {
        if (c.getRow() == data.size() - 1) {
            return c;
        }
        Coordinate nc = new Coordinate(c.getRow() + 1,
                                       c.getCol());
        CellValue<?> newCell = data.get(nc);
        while (newCell.getRowSpan() == 0 && nc.getRow() < data.size() - 1) {
            nc = new Coordinate(nc.getRow() + 1,
                                nc.getCol());
            newCell = data.get(nc);
        }
        if (newCell.getRowSpan() != 0) {
            nc = new Coordinate(nc.getRow() - 1,
                                nc.getCol());
        }
        return nc;
    }

    // Given a base row find the minimum row that needs to be re-rendered based
    // upon each columns merged cells; where each merged cell passes through the
    // base row
    private int findMinRedrawRow(int baseRowIndex) {

        if (data.size() == 0) {
            return 0;
        }

        // These should never happen, but it's a safe-guard
        if (baseRowIndex < 0) {
            baseRowIndex = 0;
        }
        if (baseRowIndex > data.size() - 1) {
            baseRowIndex = data.size() - 1;
        }

        int minRedrawRow = baseRowIndex;
        DynamicDataRow baseRow = data.get(baseRowIndex);
        for (int iCol = 0; iCol < baseRow.size(); iCol++) {
            int iRow = baseRowIndex;
            CellValue<? extends Comparable<?>> cell = baseRow.get(iCol);
            while (cell.getRowSpan() != 1 && iRow > 0) {
                iRow--;
                DynamicDataRow row = data.get(iRow);
                cell = row.get(iCol);
            }
            minRedrawRow = (iRow < minRedrawRow ? iRow : minRedrawRow);
        }
        return minRedrawRow;
    }

    //Get the next cell when selection moves in the specified direction
    private Coordinate getNextCell(Coordinate c,
                                   MOVE_DIRECTION dir) {

        int step = 0;
        Coordinate nc = c;

        switch (dir) {
            case LEFT:

                // Move left
                step = c.getCol() > 0 ? 1 : 0;
                if (step > 0) {
                    nc = new Coordinate(c.getRow(),
                                        c.getCol() - step);

                    // Skip hidden columns
                    while (nc.getCol() > 0
                            && !columns.get(nc.getCol()).isVisible()) {
                        nc = new Coordinate(c.getRow(),
                                            nc.getCol() - step);
                    }

                    //Move to top of a merged cells
                    CellValue<?> newCell = data.get(nc);
                    while (newCell.getRowSpan() == 0) {
                        nc = new Coordinate(nc.getRow() - 1,
                                            nc.getCol());
                        newCell = data.get(nc);
                    }
                }
                break;
            case RIGHT:

                // Move right
                step = c.getCol() < columns.size() - 1 ? 1 : 0;
                if (step > 0) {
                    nc = new Coordinate(c.getRow(),
                                        c.getCol() + step);

                    // Skip hidden columns
                    while (nc.getCol() < columns.size() - 2
                            && !columns.get(nc.getCol()).isVisible()) {
                        nc = new Coordinate(c.getRow(),
                                            nc.getCol() + step);
                    }
                    //If the next column is not visible don't move
                    if (!columns.get(nc.getCol()).isVisible()) {
                        nc = c;
                        break;
                    }

                    //Move to top of a merged cells
                    CellValue<?> newCell = data.get(nc);
                    while (newCell.getRowSpan() == 0) {
                        nc = new Coordinate(nc.getRow() - 1,
                                            nc.getCol());
                        newCell = data.get(nc);
                    }
                }
                break;
            case UP:

                // Move up
                step = c.getRow() > 0 ? 1 : 0;
                if (step > 0) {
                    nc = new Coordinate(c.getRow() - step,
                                        c.getCol());

                    //Move to top of a merged cells
                    CellValue<?> newCell = data.get(nc);
                    while (newCell.getRowSpan() == 0) {
                        nc = new Coordinate(nc.getRow() - step,
                                            nc.getCol());
                        newCell = data.get(nc);
                    }
                }
                break;
            case DOWN:

                // Move down
                step = c.getRow() < data.size() - 1 ? 1 : 0;
                if (step > 0) {
                    nc = new Coordinate(c.getRow() + step,
                                        c.getCol());

                    //Move to top of a merged cells
                    CellValue<?> newCell = data.get(nc);
                    while (newCell.getRowSpan() == 0 && nc.getRow() < data.size() - 1) {
                        nc = new Coordinate(nc.getRow() + step,
                                            nc.getCol());
                        newCell = data.get(nc);
                    }
                    if (newCell.getRowSpan() == 0 && nc.getRow() == data.size() - 1) {
                        nc = c;
                    }
                }
        }
        return nc;
    }

    // Re-index columns
    private void reindexColumns() {
        for (int iCol = 0; iCol < columns.size(); iCol++) {
            DynamicColumn<T> col = columns.get(iCol);
            col.setColumnIndex(iCol);
        }
    }

    //Remove grouping by expanding applicable rows
    private void removeModelGrouping(CellValue<?> startCell,
                                     boolean bRedraw) {

        List<DynamicDataRow> expandedRow = data.removeModelGrouping(startCell);

        //Partial redraw
        if (bRedraw) {
            int startRowIndex = startCell.getCoordinate().getRow();
            int minRedrawRow = findMinRedrawRow(startRowIndex - (startRowIndex > 0 ? 1 : 0));
            int maxRedrawRow = findMaxRedrawRow(startRowIndex + (startRowIndex < data.size() - 2 ? 1 : 0));
            for (int iRow = 0; iRow < expandedRow.size() - 1; iRow++) {
                createEmptyRowElement(startRowIndex);
            }
            redrawRows(minRedrawRow,
                       maxRedrawRow);
            eventBus.fireEvent(ROW_GROUPING_EVENT);
        }
    }

    //Clear all selections
    protected void clearSelection() {
        // De-select any previously selected cells
        for (CellValue<? extends Comparable<?>> cell : this.selections) {
            cell.removeState(CellValue.CellState.SELECTED);
            deselectCell(cell);
        }

        // Clear collection
        selections.clear();
        rangeDirection = MOVE_DIRECTION.NONE;
        SelectedCellChangeEvent scce = new SelectedCellChangeEvent();
        eventBus.fireEvent(scce);
    }

    abstract void createEmptyRowElement(int index);

    abstract void createRowElement(int index,
                                   DynamicDataRow rowData);

    abstract void deleteRowElement(int index);

    //Check whether "Grouping" widget has been clicked
    protected boolean isGroupWidgetClicked(Event event,
                                           Element target) {
        String eventType = event.getType();
        if (eventType.equals("mousedown")) {
            String tagName = target.getTagName();
            if ("img".equalsIgnoreCase(tagName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Redraw table rows. Partial redraw
     * @param startRedrawIndex Start row index (inclusive)
     * @param endRedrawIndex End row index (inclusive)
     */
    abstract void redrawRows(int startRedrawIndex,
                             int endRedrawIndex);

    abstract void removeRowElement(int index);

    /**
     * Remove styling indicating a selected state
     * @param cell
     */
    abstract void deselectCell(CellValue<? extends Comparable<?>> cell);

    /**
     * Extend selection from the first cell selected to the cell specified
     * @param end Extent of selection
     */
    void extendSelection(Coordinate end) {
        if (rangeOriginCell == null) {
            throw new IllegalArgumentException("origin has not been set. Unable to extend selection");
        }
        if (end == null) {
            throw new IllegalArgumentException("end cannot be null");
        }
        clearSelection();
        CellValue<?> endCell = data.get(end);
        selectRange(rangeOriginCell,
                    endCell);
        if (rangeOriginCell.getCoordinate().getRow() > endCell.getCoordinate().getRow()) {
            rangeExtentCell = selections.first();
            rangeDirection = MOVE_DIRECTION.UP;
        } else {
            rangeExtentCell = selections.last();
            rangeDirection = MOVE_DIRECTION.DOWN;
        }
    }

    /**
     * Extend selection in the specified direction
     * @param dir Direction to extend the selection
     */
    void extendSelection(MOVE_DIRECTION dir) {
        if (selections.size() > 0) {
            CellValue<?> activeCell = (rangeExtentCell == null ? rangeOriginCell : rangeExtentCell);
            Coordinate nc = getNextCell(activeCell.getCoordinate(),
                                        dir);
            clearSelection();
            rangeDirection = dir;
            rangeExtentCell = data.get(nc);
            selectRange(rangeOriginCell,
                        rangeExtentCell);
        }
    }

    /**
     * Retrieve the extents of a cell
     * @param cv The cell for which to retrieve the extents
     * @return
     */
    CellSelectionDetail getSelectedCellExtents(CellValue<? extends Comparable<?>> cv) {

        if (cv == null) {
            throw new IllegalArgumentException("cv cannot be null");
        }

        // Cells in hidden columns do not have extents
        if (!columns.get(cv.getCoordinate().getCol()).isVisible()) {
            return null;
        }

        Coordinate hc = cv.getHtmlCoordinate();
        TableRowElement tre = tbody.getRows().getItem(hc.getRow()).<TableRowElement>cast();
        TableCellElement tce = tre.getCells().getItem(hc.getCol()).<TableCellElement>cast();
        int offsetX = tce.getOffsetLeft();
        int offsetY = tce.getOffsetTop();
        int w = tce.getOffsetWidth();
        int h = tce.getOffsetHeight();
        CellSelectionDetail e = new CellSelectionDetail(cv.getCoordinate(),
                                                        offsetX,
                                                        offsetY,
                                                        h,
                                                        w);
        return e;
    }

    /**
     * Group a merged cell. If the cell is not merged across at least two rows
     * or the cell is not the top of the merged range no action is taken.
     * @param start Coordinate of top of merged group.
     */
    void groupCells(Coordinate start) {
        if (start == null) {
            throw new IllegalArgumentException("start cannot be null");
        }
        CellValue<?> startCell = data.get(start);

        //Start cell needs to be top of a merged range
        if (startCell.getRowSpan() <= 1 && !startCell.isGrouped()) {
            return;
        }

        clearSelection();
        if (startCell.isGrouped()) {
            removeModelGrouping(startCell,
                                true);
        } else {
            applyModelGrouping(startCell,
                               true);
        }
    }

    /**
     * Hide a column
     */
    abstract void hideColumn(int index);

    /**
     * Move the selected cell
     * @param dir Direction to move the selection
     */
    void moveSelection(MOVE_DIRECTION dir) {
        if (selections.size() > 0) {
            CellValue<?> activeCell = (rangeExtentCell == null ? rangeOriginCell : rangeExtentCell);
            Coordinate nc = getNextCell(activeCell.getCoordinate(),
                                        dir);
            startSelecting(nc);
            rangeDirection = dir;
        }
    }

    /**
     * Resize a column
     * @param col
     * @param width
     */
    abstract void resizeColumn(DynamicColumn<?> col,
                               int width);

    /**
     * Add styling to cell to indicate a selected state
     * @param cell
     */
    abstract void selectCell(CellValue<? extends Comparable<?>> cell);

    /**
     * Select a range of cells
     * @param startCell The first cell to select
     * @param endCell The last cell to select
     */
    void selectRange(CellValue<?> startCell,
                     CellValue<?> endCell) {
        int col = startCell.getCoordinate().getCol();

        //Ensure startCell precedes endCell
        if (startCell.getCoordinate().getRow() > endCell.getCoordinate().getRow()) {
            CellValue<?> swap = startCell;
            startCell = endCell;
            endCell = swap;
        }

        //Ensure startCell is at the top of a merged cell
        while (startCell.getRowSpan() == 0) {
            startCell = data.get(startCell.getCoordinate().getRow() - 1).get(col);
        }

        //Ensure endCell is at the bottom of a merged cell
        Coordinate nc = findMergedCellExtent(endCell.getCoordinate());
        endCell = data.get(nc);

        //Select range
        for (int iRow = startCell.getCoordinate().getRow(); iRow <= endCell.getCoordinate().getRow(); iRow++) {
            CellValue<?> cell = data.get(iRow).get(col);
            selections.add(cell);

            // Redraw selected cell
            cell.addState(CellValue.CellState.SELECTED);
            selectCell(cell);
        }

        //Set extent of selected range according to the direction of selection
        switch (rangeDirection) {
            case DOWN:
                this.rangeExtentCell = this.selections.last();
                break;
            case UP:
                this.rangeExtentCell = this.selections.first();
                break;
        }
    }

    /**
     * Show a column
     */
    abstract void showColumn(int index);

    /**
     * Select a single cell. If the cell is merged the selection is extended to
     * include all merged cells.
     * @param start The physical coordinate of the cell
     */
    void startSelecting(Coordinate start) {
        if (start == null) {
            throw new IllegalArgumentException("start cannot be null");
        }

        clearSelection();

        //Raise event signalling change in selection
        CellSelectionDetail ce = getSelectedCellExtents(data.get(start));
        SelectedCellChangeEvent scce = new SelectedCellChangeEvent(ce);
        eventBus.fireEvent(scce);

        CellValue<?> startCell = data.get(start);
        selectRange(startCell,
                    startCell);
        rangeOriginCell = startCell;
        rangeExtentCell = null;
    }

    public void setData(DynamicData data) {
        this.data = data;
        this.rowMapper = new RowMapper(data);
    }

    public void setColumns(List<DynamicColumn<T>> columns) {
        this.columns = columns;
        reindexColumns();
    }

    public void onToggleMerging(ToggleMergingEvent event) {
        clearSelection();
        if (event.isMerged()) {
            if (!data.isMerged()) {
                data.setMerged(true);
                redraw();
            }
        } else {
            if (data.isMerged()) {
                data.setMerged(false);
                redraw();
                eventBus.fireEvent(ROW_GROUPING_EVENT);
            }
        }
    }

    public void onDeleteRow(DeleteRowEvent event) {
        int index = rowMapper.mapToMergedRow(event.getIndex());

        // Clear any selections
        clearSelection();

        //Delete row data
        data.deleteRow(index);

        removeRowElement(index);

        // Partial redraw
        if (data.isMerged()) {
            if (data.size() > 0) {
                int minRedrawRow = findMinRedrawRow(index - 1);
                int maxRedrawRow = findMaxRedrawRow(index - 1) + 1;
                if (maxRedrawRow > data.size() - 1) {
                    maxRedrawRow = data.size() - 1;
                }
                redrawRows(minRedrawRow,
                           maxRedrawRow);
            }
        }
    }

    public void onInsertRow(InsertRowEvent event) {
        int index = rowMapper.mapToMergedRow(event.getIndex());
        DynamicDataRow rowData = cellValueFactory.makeUIRowData();
        insertRow(index,
                  rowData);
    }

    public void onAppendRow(AppendRowEvent event) {
        int index = data.size();
        DynamicDataRow rowData = cellValueFactory.makeUIRowData();
        insertRow(index,
                  rowData);
    }

    public void onCopyRows(CopyRowsEvent event) {
        copiedRows.clear();
        //Determine set of *unique* logical (grouped) rows from absolute indexes
        SortedSet<Integer> uniqueLogicalRowIndexes = new TreeSet<Integer>();
        for (Integer iRow : event.getRowIndexes()) {
            uniqueLogicalRowIndexes.add(rowMapper.mapToMergedRow(iRow));
        }
        for (Integer iRow : uniqueLogicalRowIndexes) {
            copiedRows.add(data.get(iRow));
        }
    }

    public void onPasteRows(PasteRowsEvent event) {
        if (copiedRows == null || copiedRows.size() == 0) {
            return;
        }
        int iRow = rowMapper.mapToMergedRow(event.getTargetRowIndex());
        for (DynamicDataRow sourceRowData : copiedRows) {
            //Clone the row, other than RowNumber column
            insertRow(iRow,
                      cloneRow(sourceRowData));
            iRow++;
        }
    }

    private DynamicDataRow cloneRow(DynamicDataRow sourceRowData) {
        if (sourceRowData instanceof GroupedDynamicDataRow) {
            return cloneDynamicDataRow((GroupedDynamicDataRow) sourceRowData);
        }
        return cloneDynamicDataRow(sourceRowData);
    }

    private DynamicDataRow cloneDynamicDataRow(DynamicDataRow sourceRowData) {
        DynamicDataRow rowData = cellValueFactory.makeUIRowData();
        for (int iCol = 0; iCol < sourceRowData.size(); iCol++) {
            CellValue<? extends Comparable<?>> cell = sourceRowData.get(iCol);
            rowData.get(iCol).setValue(cell.getValue());
        }
        return rowData;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private DynamicDataRow cloneDynamicDataRow(GroupedDynamicDataRow sourceRowData) {
        GroupedDynamicDataRow rowData = new GroupedDynamicDataRow();
        for (int iCol = 0; iCol < sourceRowData.size(); iCol++) {
            CellValue<? extends Comparable<?>> sourceCell = sourceRowData.get(iCol);
            CellValue.GroupedCellValue cell = sourceCell.convertToGroupedCell();
            if (sourceCell instanceof CellValue.GroupedCellValue) {
                CellValue.GroupedCellValue groupedSourceCell = (CellValue.GroupedCellValue) sourceCell;
                if (groupedSourceCell.isGrouped()) {
                    cell.addState(CellValue.CellState.GROUPED);
                }
                if (groupedSourceCell.isOtherwise()) {
                    cell.addState(CellValue.CellState.OTHERWISE);
                }
            }
            rowData.add(cell);
        }
        for (DynamicDataRow childRow : sourceRowData.getChildRows()) {
            rowData.addChildRow(cloneRow(childRow));
        }
        return rowData;
    }

    private void insertRow(int index,
                           DynamicDataRow rowData) {

        // Clear any selections
        clearSelection();

        int minRedrawRow = index;
        int maxRedrawRow = index;

        // Find rows that need to be (re)drawn before the new row is inserted
        if (data.isMerged()) {
            if (index < data.size()) {
                minRedrawRow = findMinRedrawRow(index);
                maxRedrawRow = findMaxRedrawRow(index);
            }
        }

        data.addRow(index,
                    rowData);

        // Check extents as these could have changed after the row is inserted
        if (data.isMerged()) {
            if (index < data.size()) {
                minRedrawRow = Math.min(minRedrawRow,
                                        findMinRedrawRow(index));
                maxRedrawRow = Math.max(maxRedrawRow,
                                        findMaxRedrawRow(index));
            } else {
                minRedrawRow = Math.min(minRedrawRow,
                                        findMinRedrawRow(index));
                maxRedrawRow = index;
            }
        }

        // Partial redraw
        if (!data.isMerged()) {
            // Only new row when not merged
            createRowElement(index,
                             rowData);
        } else {
            // Affected rows when merged
            createEmptyRowElement(index);
            redrawRows(minRedrawRow,
                       maxRedrawRow);
        }
    }

    public void onDeleteColumn(DeleteColumnEvent event) {

        int firstColumnIndex = event.getFirstColumnIndex();
        boolean bRedraw = event.redraw();
        boolean bRedrawSidebar = false;

        //Expand any merged cells in column
        for (int iCol = 0; iCol < event.getNumberOfColumns(); iCol++) {
            for (int iRow = 0; iRow < data.size(); iRow++) {
                CellValue<?> cv = data.get(iRow).get(firstColumnIndex + iCol);
                if (cv.isGrouped()) {
                    removeModelGrouping(cv,
                                        false);
                    bRedrawSidebar = true;
                }
            }
        }

        // Clear any selections
        clearSelection();

        // Delete columns and data from grid
        for (int iCol = 0; iCol < event.getNumberOfColumns(); iCol++) {
            columns.remove(firstColumnIndex);
            data.deleteColumn(firstColumnIndex);
        }
        reindexColumns();

        // Redraw
        if (bRedraw) {
            redraw();
            if (bRedrawSidebar) {
                eventBus.fireEvent(ROW_GROUPING_EVENT);
            }
        }
    }

    public void onInsertInternalColumn(InsertInternalColumnEvent<T> event) {

        int index = event.getIndex();
        boolean bRedraw = event.redraw();

        // Clear any selections
        clearSelection();

        // Add column definitions and data
        for (int iCol = 0; iCol < event.getColumns().size(); iCol++) {
            DynamicColumn<T> column = event.getColumns().get(iCol);
            List<CellValue<? extends Comparable<?>>> columnData = event.getColumnsData().get(iCol);
            columns.add(index + iCol,
                        column);
            data.addColumn(index + iCol,
                           columnData,
                           column.isVisible());
        }
        reindexColumns();

        // Redraw
        if (bRedraw) {
            redrawColumns(index,
                          columns.size() - 1);
        }
    }

    public void onSetInternalModel(SetInternalModelEvent<M, T> event) {
        this.dropDownManager.setData(event.getData());
        this.setColumns(event.getColumns());
        this.setData(event.getData());
        this.redraw();
    }

    public void onSetColumnVisibility(SetColumnVisibilityEvent event) {

        int index = event.getIndex();
        boolean isVisible = event.isVisible();

        if (isVisible && !columns.get(index).isVisible()) {
            columns.get(index).setVisible(isVisible);
            data.setColumnVisibility(index,
                                     isVisible);
            showColumn(index);
        } else if (!isVisible && columns.get(index).isVisible()) {
            hideColumn(index);
            columns.get(index).setVisible(isVisible);
            data.setColumnVisibility(index,
                                     isVisible);
        }
    }

    public void onUpdateColumnData(UpdateColumnDataEvent event) {
        int iRowIndex = 0;
        int iColIndex = event.getIndex();
        List<CellValue<? extends Comparable<?>>> columnData = event.getColumnData();

        for (int iRow = 0; iRow < data.size(); iRow++) {
            DynamicDataRow row = data.get(iRow);
            CellValue<? extends Comparable<?>> cell = columnData.get(iRowIndex);
            if (row instanceof GroupedDynamicDataRow) {
                GroupedDynamicDataRow groupedRow = (GroupedDynamicDataRow) row;

                //Setting value on a GroupedCellValue causes all children to assume the same value
                groupedRow.get(iColIndex).setValue(cell.getValue());

                //So set the children's values accordingly
                for (int iGroupedRow = 0; iGroupedRow < groupedRow.getChildRows().size(); iGroupedRow++) {
                    cell = columnData.get(iRowIndex);
                    groupedRow.getChildRows().get(iGroupedRow).get(iColIndex).setValue(cell.getValue());
                    iRowIndex++;
                }
            } else {
                row.get(iColIndex).setValue(cell.getValue());
                iRowIndex++;
            }
        }

        data.assertModelMerging();

        redrawColumns(iColIndex,
                      columns.size() - 1);
    }

    public void onUpdateColumnDefinition(UpdateColumnDefinitionEvent event) {
        int index = event.getColumnIndex();
        DynamicColumn<T> column = columns.get(index);
        column.setCell(event.getCell());
        column.setSystemControlled(event.isSystemControlled());
        column.setSortable(event.isSortable());
    }

    public void onColumnResize(final ColumnResizeEvent event) {
        resizeColumn(event.getColumn(),
                     event.getWidth());
    }

    public void onMoveColumns(MoveColumnsEvent event) {
        int sourceColumnIndex = event.getSourceColumnIndex();
        int targetColumnIndex = event.getTargetColumnIndex();
        int numberOfColumns = event.getNumberOfColumns();

        //Work-out what columns need redrawing
        int startRedrawIndex = sourceColumnIndex;
        int endRedrawIndex = targetColumnIndex;
        if (targetColumnIndex < sourceColumnIndex) {
            startRedrawIndex = targetColumnIndex;
            endRedrawIndex = sourceColumnIndex + numberOfColumns - 1;
        }

        //Move source columns and data to destination
        if (targetColumnIndex > sourceColumnIndex) {
            for (int iCol = 0; iCol < numberOfColumns; iCol++) {
//                this.data.addColumn( targetColumnIndex,
//                                     this.data.removeColumn( sourceColumnIndex ),
//                                     this.columns.get( sourceColumnIndex ).isVisible() );
                this.columns.add(targetColumnIndex,
                                 this.columns.remove(sourceColumnIndex));
                this.data.moveColumn(sourceColumnIndex,
                                     targetColumnIndex);
            }
        } else if (targetColumnIndex < sourceColumnIndex) {
            for (int iCol = 0; iCol < numberOfColumns; iCol++) {
//                this.data.addColumn( targetColumnIndex,
//                                     this.data.removeColumn( sourceColumnIndex ),
//                                     this.columns.get( sourceColumnIndex ).isVisible() );
                this.columns.add(targetColumnIndex,
                                 this.columns.remove(sourceColumnIndex));
                this.data.moveColumn(sourceColumnIndex,
                                     targetColumnIndex);
                sourceColumnIndex++;
                targetColumnIndex++;
            }
        }

        //Redraw the affected columns
        reindexColumns();
        data.assertModelMerging();
        redrawColumns(startRedrawIndex,
                      endRedrawIndex);
    }

    public void onSortData(SortDataEvent event) {

        //Remove grouping, if applicable
        if (data.isGrouped()) {
            ToggleMergingEvent tme = new ToggleMergingEvent(false);
            eventBus.fireEvent(tme);
        }

        //Sort data
        List<SortConfiguration> sortConfiguration = event.getSortConfiguration();
        data.sort(sortConfiguration);
        redraw();

        //Copy data and raise event for underlying model to update itself
        List<List<CellValue<? extends Comparable<?>>>> changedData = new ArrayList<List<CellValue<? extends Comparable<?>>>>();
        for (DynamicDataRow row : data) {
            List<CellValue<? extends Comparable<?>>> changedRow = new ArrayList<CellValue<? extends Comparable<?>>>();
            changedData.add(changedRow);

            for (int iCol = 0; iCol < row.size(); iCol++) {
                CellValue<? extends Comparable<?>> changedCell = row.get(iCol);
                changedRow.add(changedCell);
            }
        }
        UpdateModelEvent dce = new UpdateModelEvent(new Coordinate(0,
                                                                   0),
                                                    changedData);
        eventBus.fireEvent(dce);
    }

    @SuppressWarnings("rawtypes")
    public void onUpdateSelectedCells(UpdateSelectedCellsEvent event) {

        Comparable<?> value = event.getValue();
        Map<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> changedData = new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>();
        List<List<CellValue<? extends Comparable<?>>>> changedBlock;
        List<CellValue<? extends Comparable<?>>> changedRow;
        Coordinate firstSelection = selections.first().getCoordinate();

        //If selections span multiple cells, any of which are grouped we should ungroup them
        boolean bUngroupCells = false;
        if (selections.size() > 1) {
            for (CellValue<? extends Comparable<?>> cell : selections) {
                if (cell instanceof CellValue.GroupedCellValue) {
                    bUngroupCells = true;
                    break;
                }
            }
        }

        //---Update selected cells (before ungrouping otherwise selections would need to be expanded too)---
        changedBlock = new ArrayList<List<CellValue<? extends Comparable<?>>>>();
        for (CellValue<? extends Comparable<?>> cell : selections) {
            changedRow = new ArrayList<CellValue<? extends Comparable<?>>>();
            Coordinate c = cell.getCoordinate();
            if (!columns.get(c.getCol()).isSystemControlled()) {
                data.set(c,
                         value);
                if (value != null) {
                    cell.removeState(CellValue.CellState.OTHERWISE);
                }

                //Copy data that is changing for an event to update the underlying model
                if (cell instanceof CellValue.GroupedCellValue) {
                    CellValue.GroupedCellValue gcv = (CellValue.GroupedCellValue) cell;
                    for (int iChildValueIndex = 0; iChildValueIndex < gcv.getGroupedCells().size(); iChildValueIndex++) {
                        changedRow.add(data.get(c));
                        changedBlock.add(changedRow);
                    }
                } else {
                    changedRow.add(data.get(c));
                    changedBlock.add(changedRow);
                }
            }
        }
        Coordinate originSelected = new Coordinate(rowMapper.mapToAbsoluteRow(firstSelection.getRow()),
                                                   firstSelection.getCol());
        changedData.put(originSelected,
                        changedBlock);

        //---Clear dependent cells' values---
        final Context context = new Context(0,
                                            firstSelection.getCol(),
                                            null);
        final Set<Integer> dependentColumnIndexes = this.dropDownManager.getDependentColumnIndexes(context);
        for (Integer dependentColumnIndex : dependentColumnIndexes) {
            changedBlock = new ArrayList<List<CellValue<? extends Comparable<?>>>>();
            for (CellValue<? extends Comparable<?>> cell : selections) {
                changedRow = new ArrayList<CellValue<? extends Comparable<?>>>();
                Coordinate dc = new Coordinate(cell.getCoordinate().getRow(),
                                               dependentColumnIndex);
                if (!columns.get(dc.getCol()).isSystemControlled()) {
                    data.set(dc,
                             null);
                    if (value != null) {
                        cell.removeState(CellValue.CellState.OTHERWISE);
                    }

                    //Copy data that is changing for an event to update the underlying model
                    if (cell instanceof CellValue.GroupedCellValue) {
                        CellValue.GroupedCellValue gcv = (CellValue.GroupedCellValue) cell;
                        for (int iChildValueIndex = 0; iChildValueIndex < gcv.getGroupedCells().size(); iChildValueIndex++) {
                            changedRow.add(data.get(dc));
                            changedBlock.add(changedRow);
                        }
                    } else {
                        changedRow.add(data.get(dc));
                        changedBlock.add(changedRow);
                    }
                }
            }
            Coordinate originDependent = new Coordinate(rowMapper.mapToAbsoluteRow(firstSelection.getRow()),
                                                        dependentColumnIndex);
            changedData.put(originDependent,
                            changedBlock);
        }

        //Ungroup if applicable
        if (bUngroupCells) {
            for (CellValue<? extends Comparable<?>> cell : selections) {
                if (cell instanceof CellValue.GroupedCellValue) {
                    //Removing merging partially redraws the grid
                    removeModelGrouping(cell,
                                        true);
                }
            }
        } else if (data.isMerged() || selections.size() > 1) {

            //If the data is merged changes to the cells' value can cause the need for a greater range of 
            //rows to be redrawn as a cell's new value could cause the merged span to increase. This is also 
            //the only mechanism available to update multiple individual cells' values when multiple 
            //cells are selected.
            data.assertModelMerging();

            // Partial redraw
            int baseRowIndex = selections.first().getCoordinate().getRow();
            int minRedrawRow = findMinRedrawRow(baseRowIndex);
            int maxRedrawRow = findMaxRedrawRow(baseRowIndex);

            // When merged cells become unmerged (if their value is
            // cleared need to ensure the re-draw range is at least
            // as large as the selection range
            if (maxRedrawRow < selections.last().getCoordinate().getRow()) {
                maxRedrawRow = selections.last().getCoordinate().getRow();
            }
            redrawRows(minRedrawRow,
                       maxRedrawRow);
        } else {

            //Redraw a single row
            int baseRowIndex = selections.first().getCoordinate().getRow();
            redrawRows(baseRowIndex,
                       baseRowIndex);
        }

        //Re-select applicable cells, following change to merge
        startSelecting(firstSelection);

        //Raise event for underlying model to update itself, converting logical row to physical
        UpdateModelEvent dce = new UpdateModelEvent(changedData);
        eventBus.fireEvent(dce);
    }

    @SuppressWarnings("rawtypes")
    public void onCellStateChanged(CellStateChangedEvent event) {
        Set<CellStateChangedEvent.CellStateOperation> states = event.getStates();

        boolean bUngroupCells = false;
        Coordinate selection = selections.first().getCoordinate();
        List<List<CellValue<? extends Comparable<?>>>> changedData = new ArrayList<List<CellValue<? extends Comparable<?>>>>();

        //If selections span multiple cells, any of which are grouped we should ungroup them
        if (selections.size() > 1) {
            for (CellValue<? extends Comparable<?>> cell : selections) {
                if (cell instanceof CellValue.GroupedCellValue) {
                    bUngroupCells = true;
                    break;
                }
            }
        }

        // Update underlying data (update before ungrouping as selections would need to be expanded too)
        for (CellValue<? extends Comparable<?>> cell : selections) {
            Coordinate c = cell.getCoordinate();
            if (!columns.get(c.getCol()).isSystemControlled()) {
                CellValue<? extends Comparable<?>> cv = data.get(c);
                for (CellStateChangedEvent.CellStateOperation state : states) {
                    switch (state.getOperation()) {
                        case ADD:
                            cv.addState(state.getState());
                            if (state.getState() == CellValue.CellState.OTHERWISE) {
                                cv.setValue(null);
                            }
                            break;
                        case REMOVE:
                            cv.removeState(state.getState());
                            break;
                    }
                }

                //Copy data that is changing for an event to update the underlying model
                if (cell instanceof CellValue.GroupedCellValue) {
                    CellValue.GroupedCellValue gcv = (CellValue.GroupedCellValue) cell;
                    for (int iChildValueIndex = 0; iChildValueIndex < gcv.getGroupedCells().size(); iChildValueIndex++) {
                        List<CellValue<? extends Comparable<?>>> changedRow = new ArrayList<CellValue<? extends Comparable<?>>>();
                        changedRow.add(data.get(c));
                        changedData.add(changedRow);
                    }
                } else {
                    List<CellValue<? extends Comparable<?>>> changedRow = new ArrayList<CellValue<? extends Comparable<?>>>();
                    changedRow.add(data.get(c));
                    changedData.add(changedRow);
                }
            }
        }

        //Ungroup if applicable
        if (bUngroupCells) {
            for (CellValue<? extends Comparable<?>> cell : selections) {
                if (cell instanceof CellValue.GroupedCellValue) {
                    //Removing merging partially redraws the grid
                    removeModelGrouping(cell,
                                        true);
                }
            }
        } else {
            data.assertModelMerging();

            // Partial redraw
            int baseRowIndex = selections.first().getCoordinate().getRow();
            int minRedrawRow = findMinRedrawRow(baseRowIndex);
            int maxRedrawRow = findMaxRedrawRow(baseRowIndex);

            // When merged cells become unmerged (if their value is
            // cleared need to ensure the re-draw range is at least
            // as large as the selection range
            if (maxRedrawRow < selections.last().getCoordinate().getRow()) {
                maxRedrawRow = selections.last().getCoordinate().getRow();
            }
            redrawRows(minRedrawRow,
                       maxRedrawRow);
        }

        //Re-select applicable cells, following change to merge
        startSelecting(selection);

        //Raise event for underlying model to update itself
        UpdateModelEvent dce = new UpdateModelEvent(selections.first().getCoordinate(),
                                                    changedData);
        eventBus.fireEvent(dce);
    }
}
