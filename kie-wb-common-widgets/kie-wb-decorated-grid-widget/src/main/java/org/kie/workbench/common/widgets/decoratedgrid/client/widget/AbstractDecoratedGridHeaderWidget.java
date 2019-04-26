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
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AfterColumnDeleted;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AfterColumnInserted;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteColumnEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertInternalColumnEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.MoveColumnsEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SetColumnVisibilityEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SetInternalModelEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDefinitionEvent;

/**
 * An abstract "Header" widget to decorate a <code>DecoratedGridWidget</code>
 * @param <M> The domain model represented by the Header
 * @param <T> The type of domain columns represented by the Header
 */
public abstract class AbstractDecoratedGridHeaderWidget<M, T> extends CellPanel
        implements
        HasResizeHandlers,
        SetInternalModelEvent.Handler<M, T>,
        DeleteColumnEvent.Handler,
        InsertInternalColumnEvent.Handler<T>,
        SetColumnVisibilityEvent.Handler,
        UpdateColumnDefinitionEvent.Handler,
        MoveColumnsEvent.Handler {

    private Optional<HandlerRegistration> rootPanelMouseMoveHandlerRegistration = Optional.empty();
    private Optional<HandlerRegistration> rootPanelMouseDownHandlerRegistration = Optional.empty();
    private Optional<HandlerRegistration> rootPanelMouseUpHandlerRegistration = Optional.empty();
    private Optional<HandlerRegistration> rootPanelMouseOutHandlerRegistration = Optional.empty();

    /**
     * Container class for information relating to re-size operations
     */
    public class ResizerInformation {

        private boolean isResizePrimed = false;
        private boolean isResizing = false;
        private int resizeColumnLeft = 0;
        private DynamicColumn<T> resizeColumn = null;
        private int resizeColumnWidth = 0;

        public boolean isResizePrimed() {
            return isResizePrimed;
        }

        /**
         * @param isResizePrimed the bResizePrimed to set
         */
        public void setResizePrimed(boolean isResizePrimed) {
            this.isResizePrimed = isResizePrimed;
        }

        public boolean isResizing() {
            return isResizing;
        }

        public void setResizing(boolean resizing) {
            isResizing = resizing;
        }

        public DynamicColumn<T> getResizeColumn() {
            return resizeColumn;
        }

        /**
         * @param resizeColumn the resizeColumn to set
         */
        public void setResizeColumn(DynamicColumn<T> resizeColumn) {
            this.resizeColumn = resizeColumn;
        }

        public int getResizeColumnLeft() {
            return resizeColumnLeft;
        }

        /**
         * @param resizeColumnLeft the resizeColumnLeft to set
         */
        public void setResizeColumnLeft(int resizeColumnLeft) {
            this.resizeColumnLeft = resizeColumnLeft;
        }

        public int getResizeColumnWidth() {
            return resizeColumnWidth;
        }

        public void setResizeColumnWidth(int resizeColumnWidth) {
            this.resizeColumnWidth = resizeColumnWidth;
        }
    }

    private static final int MIN_COLUMN_WIDTH = 16;

    protected Panel panel;

    //Model
    protected M model;
    protected List<DynamicColumn<T>> sortableColumns = new ArrayList<DynamicColumn<T>>();

    protected final boolean isReadOnly;

    // Resources
    protected ResourcesProvider<T> resources;
    protected EventBus eventBus;

    // Column resizing
    private ResizerInformation resizerInfo = new ResizerInformation();
    private DivElement resizer;
    private UIObject parent;

    /**
     * Construct a "Header" for the provided DecoratedGridWidget
     * @param resources
     * @param eventBus
     */
    public AbstractDecoratedGridHeaderWidget(ResourcesProvider<T> resources,
                                             boolean isReadOnly,
                                             EventBus eventBus) {
        if (resources == null) {
            throw new IllegalArgumentException("resources cannot be null");
        }
        if (eventBus == null) {
            throw new IllegalArgumentException("eventBus cannot be null");
        }
        this.resources = resources;
        this.isReadOnly = isReadOnly;
        this.eventBus = eventBus;

        // Container DIV in which the components will live
        TableRowElement tre = Document.get().createTRElement();
        TableCellElement tce = Document.get().createTDElement();
        DivElement div = Document.get().createDivElement();
        div.getStyle().setPosition(Position.RELATIVE);
        getBody().getParentElement().<TableElement>cast().setCellSpacing(0);
        getBody().getParentElement().<TableElement>cast().setCellPadding(0);
        tce.appendChild(div);
        tre.appendChild(tce);
        getBody().appendChild(tre);

        // Widgets within the container
        panel = GWT.create(ScrollPanel.class);

        // We don't want scroll bars on the ScrollPanel so hide any overflow
        panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
        panel.add(getHeaderWidget());
        add(panel,
            div);

        // Column resizing
        resizer = Document.get().createDivElement();
        resizer.addClassName(resources.headerResizer());
        resizer.getStyle().setTop(0,
                                  Unit.PX);

        // Add the resizer to the outer most container, otherwise it gets
        // truncated by the ScrollPanel as it hides any overflow
        div.appendChild(resizer);

        //Wire-up other event handlers
        eventBus.addHandler(DeleteColumnEvent.TYPE,
                            this);
        eventBus.addHandler(SetColumnVisibilityEvent.TYPE,
                            this);
        eventBus.addHandler(UpdateColumnDefinitionEvent.TYPE,
                            this);
        eventBus.addHandler(MoveColumnsEvent.TYPE,
                            this);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        this.rootPanelMouseMoveHandlerRegistration = Optional.of(rootPanel().addDomHandler((event) -> {
                                                                                               //Emulate 7.22 (and older) behaviour by only observing MouseMoveEvent *over* the Header
                                                                                               int my = event.getClientY();
                                                                                               if (my < panel.getAbsoluteTop()) {
                                                                                                   return;
                                                                                               }
                                                                                               if (my > panel.getAbsoluteTop() + panel.getOffsetHeight()) {
                                                                                                   return;
                                                                                               }

                                                                                               int mx = event.getClientX();
                                                                                               if (resizerInfo.isResizing()) {
                                                                                                   if (mx - resizerInfo.getResizeColumnLeft() < MIN_COLUMN_WIDTH) {
                                                                                                       event.preventDefault();
                                                                                                       return;
                                                                                                   }
                                                                                                   setResizerDimensions(event.getX());
                                                                                                   resizerInfo.setResizeColumnWidth(mx - resizerInfo.getResizeColumnLeft());
                                                                                                   resizeColumn(resizerInfo.getResizeColumn(),
                                                                                                                resizerInfo.getResizeColumnWidth());

                                                                                                   // Second call to set dimensions as a column resize can add (or remove) a scroll bar
                                                                                                   // to (or from) the Decision Table and our resizer needs to be redrawn accordingly.
                                                                                                   // Just having the call to set dimensions after the column has been resized added
                                                                                                   // excess flicker to movement of the resizer.
                                                                                                   setResizerDimensions(event.getX());
                                                                                                   event.preventDefault();
                                                                                               } else {
                                                                                                   resizerInfo = getResizerInformation(mx);
                                                                                               }
                                                                                           },
                                                                                           MouseMoveEvent.getType()));

        this.rootPanelMouseDownHandlerRegistration = Optional.of(rootPanel().addDomHandler((event) -> {
                                                                                               if (!resizerInfo.isResizePrimed()) {
                                                                                                   return;
                                                                                               }
                                                                                               resizerInfo.setResizing(true);
                                                                                               setResizerDimensions(event.getX());
                                                                                               resizer.getStyle().setVisibility(Visibility.VISIBLE);
                                                                                               event.preventDefault();
                                                                                           },
                                                                                           MouseDownEvent.getType()));

        this.rootPanelMouseUpHandlerRegistration = Optional.of(rootPanel().addDomHandler(this::resetResizerInfo,
                                                                                         MouseUpEvent.getType()));

        this.rootPanelMouseOutHandlerRegistration = Optional.of(rootPanel().addDomHandler(this::resetResizerInfo,
                                                                                          MouseOutEvent.getType()));
    }

    private void resetResizerInfo(final DomEvent event) {
        if (!resizerInfo.isResizing()) {
            return;
        }
        resizerInfo.setResizing(false);
        resizerInfo.setResizePrimed(false);
        resizer.getStyle().setVisibility(Visibility.HIDDEN);
        event.preventDefault();
    }

    RootPanel rootPanel() {
        return RootPanel.get();
    }

    @Override
    protected void onUnload() {
        super.onUnload();

        this.rootPanelMouseMoveHandlerRegistration.ifPresent(HandlerRegistration::removeHandler);
        this.rootPanelMouseDownHandlerRegistration.ifPresent(HandlerRegistration::removeHandler);
        this.rootPanelMouseUpHandlerRegistration.ifPresent(HandlerRegistration::removeHandler);
        this.rootPanelMouseOutHandlerRegistration.ifPresent(HandlerRegistration::removeHandler);

        this.rootPanelMouseMoveHandlerRegistration = Optional.empty();
        this.rootPanelMouseDownHandlerRegistration = Optional.empty();
        this.rootPanelMouseUpHandlerRegistration = Optional.empty();
        this.rootPanelMouseOutHandlerRegistration = Optional.empty();
    }

    public HandlerRegistration addResizeHandler(ResizeHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null");
        }

        return addHandler(handler,
                          ResizeEvent.getType());
    }

    /**
     * Redraw entire header
     */
    public abstract void redraw();

    /**
     * Set scroll position to enable some degree of synchronisation between
     * DecisionTable and DecisionTableHeader
     * @param position
     */
    public abstract void setScrollPosition(int position);

    @Override
    public void setWidth(String width) {
        // Set the width of our ScrollPanel too; to prevent the containing
        // DIV from extending it's width to accommodate the increase in size
        super.setWidth(width);
        panel.setWidth(width);
    }

    // Bit of a hack to ensure the resizer is the correct size. The
    // Decision Table itself could be contained in an outer most DIV
    // that hides any overflow however the horizontal scrollbar
    // would be rendered inside the DIV and hence still be covered
    // by the resizer.
    void setResizerDimensions(final int position) {
        resizer.getStyle().setHeight(parent.getElement().getClientHeight(),
                                     Unit.PX);
        resizer.getStyle().setLeft(position - panel.getAbsoluteLeft(),
                                   Unit.PX);
    }

    void setSidebar(UIObject parent) {
        this.parent = parent;
    }

    /**
     * Get the Widget that should be wrapped by the scroll panel and resize
     * handlers. The widget renders the actual "header" embedded within the
     * decorations provided by this class: scroll-bars and resizing support.
     * @return
     */
    protected abstract Widget getHeaderWidget();

    /**
     * Given the X-coordinate check whether resizing of any column should be
     * enabled. The ResizerInformation return value contains necessary
     * information for this decorating class to perform column-resizing.
     * @param mx the MouseMoveEvent.event.getClientX() coordinate
     * @return
     */
    protected abstract ResizerInformation getResizerInformation(int mx);

    /**
     * Resize the Header column
     * @param resizeColumn
     * @param resizeColumnWidth
     */
    protected abstract void resizeColumn(DynamicColumn<T> resizeColumn,
                                         int resizeColumnWidth);

    /**
     * Update sort order. The column clicked becomes the primary sort column.
     * and the other, previously sorted, columns degrade in priority
     * @param column
     */
    protected void updateSortOrder(DynamicColumn<T> column) {

        int sortIndex;
        TreeMap<Integer, DynamicColumn<T>> sortedColumns = new TreeMap<Integer, DynamicColumn<T>>();
        switch (column.getSortIndex()) {
            case -1:

                //A new column is added to the sort group
                for (DynamicColumn<T> sortedColumn : sortableColumns) {
                    if (sortedColumn.getSortDirection() != SortDirection.NONE) {
                        sortedColumns.put(sortedColumn.getSortIndex(),
                                          sortedColumn);
                    }
                }
                sortIndex = 1;
                for (DynamicColumn<T> sortedColumn : sortedColumns.values()) {
                    sortedColumn.setSortIndex(sortIndex);
                    sortIndex++;
                }
                column.setSortIndex(0);
                column.setSortDirection(SortDirection.ASCENDING);
                break;

            case 0:

                //The existing "lead" column's sort direction is changed
                if (column.getSortDirection() == SortDirection.ASCENDING) {
                    column.setSortDirection(SortDirection.DESCENDING);
                } else if (column.getSortDirection() == SortDirection.DESCENDING) {
                    column.setSortDirection(SortDirection.NONE);
                    column.clearSortIndex();
                    for (DynamicColumn<T> sortedColumn : sortableColumns) {
                        if (sortedColumn.getSortDirection() != SortDirection.NONE) {
                            sortedColumns.put(sortedColumn.getSortIndex(),
                                              sortedColumn);
                        }
                    }
                    sortIndex = 0;
                    for (DynamicColumn<T> sortedColumn : sortedColumns.values()) {
                        sortedColumn.setSortIndex(sortIndex);
                        sortIndex++;
                    }
                }
                break;

            default:

                //An existing column is promoted to "lead"
                for (DynamicColumn<T> sortedColumn : sortableColumns) {
                    if (sortedColumn.getSortDirection() != SortDirection.NONE) {
                        if (!sortedColumn.equals(column)) {
                            sortedColumns.put(sortedColumn.getSortIndex() + 1,
                                              sortedColumn);
                        }
                    }
                }
                column.setSortIndex(0);
                sortIndex = 1;
                for (DynamicColumn<T> sortedColumn : sortedColumns.values()) {
                    sortedColumn.setSortIndex(sortIndex);
                    sortIndex++;
                }
                break;
        }
    }

    /**
     * Get the column sorting configuration. The list contains an entry for each
     * column on which the data should be sorted.
     * @return
     */
    protected List<SortConfiguration> getSortConfiguration() {
        List<SortConfiguration> sortConfiguration = new ArrayList<SortConfiguration>();
        List<DynamicColumn<T>> columns = sortableColumns;
        for (DynamicColumn<T> column : columns) {
            SortConfiguration sc = column.getSortConfiguration();
            if (sc.getSortIndex() != -1) {
                sortConfiguration.add(sc);
            }
        }
        return sortConfiguration;
    }

    public void onDeleteColumn(final DeleteColumnEvent event) {
        for (int iCol = 0; iCol < event.getNumberOfColumns(); iCol++) {
            sortableColumns.remove(event.getFirstColumnIndex());
        }
        Scheduler.get().scheduleFinally(new Command() {

            public void execute() {
                redraw();
                eventBus.fireEvent(new AfterColumnDeleted(
                        event.getFirstColumnIndex(),
                        event.getNumberOfColumns()
                ));
            }
        });
    }

    public void onInsertInternalColumn(final InsertInternalColumnEvent<T> event) {
        int iCol = event.getIndex();
        for (DynamicColumn<T> column : event.getColumns()) {
            sortableColumns.add(iCol++,
                                column);
        }
        Scheduler.get().scheduleFinally(new Command() {

            public void execute() {
                redraw();
                eventBus.fireEvent(new AfterColumnInserted(event.getIndex()));
            }
        });
    }

    public void onSetColumnVisibility(SetColumnVisibilityEvent event) {
        Scheduler.get().scheduleFinally(new Command() {

            public void execute() {
                redraw();
            }
        });
    }

    public void onUpdateColumnDefinition(UpdateColumnDefinitionEvent event) {
        Scheduler.get().scheduleFinally(new Command() {

            public void execute() {
                redraw();
            }
        });
    }

    public void onMoveColumns(MoveColumnsEvent event) {
        int sourceColumnIndex = event.getSourceColumnIndex();
        int targetColumnIndex = event.getTargetColumnIndex();
        int numberOfColumns = event.getNumberOfColumns();

        //Move source columns to destination
        if (targetColumnIndex > sourceColumnIndex) {
            for (int iCol = 0; iCol < numberOfColumns; iCol++) {
                this.sortableColumns.add(targetColumnIndex,
                                         this.sortableColumns.remove(sourceColumnIndex));
            }
        } else if (targetColumnIndex < sourceColumnIndex) {
            for (int iCol = 0; iCol < numberOfColumns; iCol++) {
                this.sortableColumns.add(targetColumnIndex,
                                         this.sortableColumns.remove(sourceColumnIndex));
                sourceColumnIndex++;
                targetColumnIndex++;
            }
        }

        //Redraw
        Scheduler.get().scheduleFinally(new Command() {

            public void execute() {
                redraw();
            }
        });
    }
}
