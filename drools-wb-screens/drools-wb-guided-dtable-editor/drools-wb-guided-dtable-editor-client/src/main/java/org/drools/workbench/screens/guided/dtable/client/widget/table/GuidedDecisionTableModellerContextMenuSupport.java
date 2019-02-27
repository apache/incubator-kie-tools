/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.CellContextMenu;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.RowContextMenu;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridData.SelectedCell;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.IsRowDragHandle;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RangeSelectionStrategy;

@Dependent
public class GuidedDecisionTableModellerContextMenuSupport {

    private final CellContextMenu cellContextMenu;
    private final RowContextMenu rowContextMenu;

    @Inject
    public GuidedDecisionTableModellerContextMenuSupport(final CellContextMenu cellContextMenu,
                                                         final RowContextMenu rowContextMenu) {
        this.cellContextMenu = cellContextMenu;
        this.rowContextMenu = rowContextMenu;
    }

    public ContextMenuHandler getContextMenuHandler(final GuidedDecisionTableModellerView.Presenter modellerPresenter) {
        return new GuidedDecisionTableModellerContextMenuHandler(modellerPresenter,
                                                                 cellContextMenu,
                                                                 rowContextMenu);
    }

    public MouseDownHandler getContextMenuMouseDownHandler() {
        return new GuidedDecisionTableModellerContextMenuMouseDownHandler(cellContextMenu,
                                                                          rowContextMenu);
    }

    private static class GuidedDecisionTableModellerContextMenuHandler implements ContextMenuHandler {

        private final GuidedDecisionTableModellerView.Presenter modellerPresenter;
        private final CellContextMenu cellContextMenu;
        private final RowContextMenu rowContextMenu;

        public GuidedDecisionTableModellerContextMenuHandler(final GuidedDecisionTableModellerView.Presenter modellerPresenter,
                                                             final CellContextMenu cellContextMenu,
                                                             final RowContextMenu rowContextMenu) {
            this.modellerPresenter = modellerPresenter;
            this.cellContextMenu = cellContextMenu;
            this.rowContextMenu = rowContextMenu;
        }

        @Override
        public void onContextMenu(final ContextMenuEvent event) {
            event.preventDefault();
            event.stopPropagation();
            final boolean isShiftKeyDown = event.getNativeEvent().getShiftKey();
            final boolean isControlKeyDown = event.getNativeEvent().getCtrlKey();
            final int eventX = event.getNativeEvent().getClientX();
            final int eventY = event.getNativeEvent().getClientY();
            final int canvasX = getRelativeX(event);
            final int canvasY = getRelativeY(event);

            for (GuidedDecisionTableView.Presenter dtPresenter : modellerPresenter.getAvailableDecisionTables()) {
                final GuidedDecisionTableView gridView = dtPresenter.getView();
                final GridData gridModel = gridView.getModel();

                final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(gridView,
                                                                                  new Point2D(canvasX,
                                                                                              canvasY));
                final Integer uiRowIndex = CoordinateUtilities.getUiRowIndex(gridView,
                                                                             ap.getY());
                final Integer uiColumnIndex = CoordinateUtilities.getUiColumnIndex(gridView,
                                                                                   ap.getX());
                if (uiRowIndex == null || uiColumnIndex == null) {
                    continue;
                }

                final GridColumn<?> column = gridModel.getColumns().get(uiColumnIndex);
                if (column instanceof IsRowDragHandle) {
                    rowContextMenu.show(eventX,
                                        eventY);
                } else {
                    cellContextMenu.show(eventX,
                                         eventY);
                }

                // the original column index before any moves
                final int modelColumnIndex = column.getIndex();
                // selected cells of UIModel whose column index is that of the context menu selected cell
                final Stream<SelectedCell> modelColumnSelectedCells = gridModel.getSelectedCells().stream().filter(cell -> cell.getColumnIndex() == modelColumnIndex);
                // does row index of context menu selected cell match one in UIModel selected cells?
                final boolean isContextMenuCellSelectedCell = modelColumnSelectedCells.map(GridData.SelectedCell::getRowIndex).anyMatch(rowIndex -> uiRowIndex.equals(rowIndex));
                // if cell selected for context menu is not one of the selected cells from CTRL or SHIFT then handle the selected cell
                if (!isContextMenuCellSelectedCell) {
                    selectCell(uiRowIndex,
                               uiColumnIndex,
                               gridView,
                               isShiftKeyDown,
                               isControlKeyDown);
                }
            }
        }

        private int getRelativeX(final ContextMenuEvent event) {
            final NativeEvent e = event.getNativeEvent();
            final Element target = event.getRelativeElement();
            return e.getClientX() - target.getAbsoluteLeft() + target.getScrollLeft() + target.getOwnerDocument().getScrollLeft();
        }

        private int getRelativeY(final ContextMenuEvent event) {
            final NativeEvent e = event.getNativeEvent();
            final Element target = event.getRelativeElement();
            return e.getClientY() - target.getAbsoluteTop() + target.getScrollTop() + target.getOwnerDocument().getScrollTop();
        }

        private void selectCell(final int uiRowIndex,
                                final int uiColumnIndex,
                                final GuidedDecisionTableView gridView,
                                final boolean isShiftKeyDown,
                                final boolean isControlKeyDown) {
            // Lookup CellSelectionManager for cell
            final GridData gridModel = gridView.getModel();

            CellSelectionStrategy selectionStrategy;
            final GridCell<?> cell = gridModel.getCell(uiRowIndex,
                                                       uiColumnIndex);
            if (cell == null) {
                selectionStrategy = RangeSelectionStrategy.INSTANCE;
            } else {
                selectionStrategy = cell.getSelectionStrategy();
            }
            if (selectionStrategy == null) {
                return;
            }

            // Handle selection
            if (selectionStrategy.handleSelection(gridModel,
                                                  uiRowIndex,
                                                  uiColumnIndex,
                                                  isShiftKeyDown,
                                                  isControlKeyDown)) {
                gridView.getLayer().batch();
            }
        }
    }

    private static class GuidedDecisionTableModellerContextMenuMouseDownHandler implements MouseDownHandler {

        private final CellContextMenu cellContextMenu;
        private final RowContextMenu rowContextMenu;

        public GuidedDecisionTableModellerContextMenuMouseDownHandler(final CellContextMenu cellContextMenu,
                                                                      final RowContextMenu rowContextMenu) {
            this.cellContextMenu = cellContextMenu;
            this.rowContextMenu = rowContextMenu;
        }

        @Override
        public void onMouseDown(final MouseDownEvent event) {
            if (!eventTargetsPopup(event.getNativeEvent(),
                                   cellContextMenu.asWidget().getElement())) {
                cellContextMenu.hide();
            }
            if (!eventTargetsPopup(event.getNativeEvent(),
                                   rowContextMenu.asWidget().getElement())) {
                rowContextMenu.hide();
            }
        }

        private boolean eventTargetsPopup(final NativeEvent event, final Element element) {
            final EventTarget target = event.getEventTarget();
            if (Element.is(target)) {
                return element.isOrHasChild(Element.as(target));
            }
            return false;
        }
    }
}
