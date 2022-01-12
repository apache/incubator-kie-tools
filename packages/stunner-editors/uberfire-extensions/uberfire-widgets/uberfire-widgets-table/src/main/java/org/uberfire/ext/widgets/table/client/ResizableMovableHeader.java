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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.kie.soup.commons.validation.PortablePreconditions;

import static com.google.gwt.dom.client.Style.Unit.PX;

/**
 * A column header that supports resizing and moving
 * See https://github.com/gchatelet/GwtResizableDraggableColumns/blob/master/src/fr/mikrosimage/gwt/client/ResizableHeader.java
 *
 * @param <T>
 */
public abstract class ResizableMovableHeader<T> extends Header<String> {

    private static final Cursor MOVE_CURSOR = Cursor.MOVE;
    private static final String MOVE_COLOR = "gray";
    private static final int MOVE_HANDLE_WIDTH = 32;

    private static final Cursor RESIZE_CURSOR = Cursor.COL_RESIZE;
    private static final String RESIZE_COLOR = "gray";
    private static final int RESIZE_HANDLE_WIDTH = 8;

    private static final double GHOST_OPACITY = .3;

    private static final int MINIMUM_COLUMN_WIDTH = 30;

    private final Document document = Document.get();

    private final String title;
    private final DataGrid<T> table;
    private final UberfireColumnPicker columnPicker;
    private final Column<T, ?> column;

    private final Element tableElement;
    private HeaderHelper current;
    private List<ColumnChangedHandler> columnChangedHandlers = new ArrayList<ColumnChangedHandler>();

    public ResizableMovableHeader(final String title,
                                  final DataGrid<T> table,
                                  final UberfireColumnPicker columnPicker,
                                  final Column<T, ?> column) {
        super(new HeaderCell());
        this.title = PortablePreconditions.checkNotNull("title",
                                                        title);
        this.table = PortablePreconditions.checkNotNull("table",
                                                        table);
        this.columnPicker = PortablePreconditions.checkNotNull("columnPicker",
                                                               columnPicker);
        this.column = PortablePreconditions.checkNotNull("column",
                                                         column);
        this.tableElement = table.getElement();
    }

    private static NativeEvent getEventAndPreventPropagation(final NativePreviewEvent event) {
        final NativeEvent nativeEvent = event.getNativeEvent();
        nativeEvent.preventDefault();
        nativeEvent.stopPropagation();
        return nativeEvent;
    }

    private static void setLine(final Style style,
                                final int width,
                                final int top,
                                final int height,
                                final String color) {
        style.setPosition(Position.ABSOLUTE);
        style.setTop(top,
                     PX);
        style.setHeight(height,
                        PX);
        style.setWidth(width,
                       PX);
        style.setBackgroundColor(color);
        style.setZIndex(Integer.MAX_VALUE);
    }

    @Override
    public String getValue() {
        return title;
    }

    @Override
    public void onBrowserEvent(final Context context,
                               final Element target,
                               final NativeEvent event) {
        if (current == null) {
            current = new HeaderHelper(target,
                                       event);
        }
    }

    protected void columnResized(final int newWidth) {
        table.setColumnWidth(column,
                             newWidth + "px");
        columnPicker.adjustColumnWidths();
        for (ColumnChangedHandler handler : columnChangedHandlers) {
            handler.afterColumnChanged();
        }
    }

    protected void columnMoved(final int fromIndex,
                               final int beforeIndex) {
        columnPicker.columnMoved(fromIndex,
                                 beforeIndex);
        table.removeColumn(fromIndex);
        table.insertColumn(beforeIndex,
                           column,
                           this);
        for (ColumnChangedHandler handler : columnChangedHandlers) {
            handler.afterColumnChanged();
        }
    }

    protected abstract int getTableBodyHeight();

    public void addColumnChangedHandler(ColumnChangedHandler handler) {
        if (handler != null) {
            columnChangedHandlers.add(handler);
        }
    }

    interface IDragCallback {

        void dragFinished();
    }

    private static class HeaderCell extends AbstractCell<String> {

        public HeaderCell() {
            super("mousemove");
        }

        @Override
        public void render(final Context context,
                           final String value,
                           final SafeHtmlBuilder sb) {
            sb.append(SafeHtmlUtils.fromString(value));
        }
    }

    private class HeaderHelper implements NativePreviewHandler,
                                          IDragCallback {

        private final HandlerRegistration handler = Event.addNativePreviewHandler(this);
        private final Element source;
        private final Element handles;
        private final Element moveHandle;
        private final Element resizeHandle;
        private boolean dragging;

        public HeaderHelper(final Element target,
                            final NativeEvent event) {
            event.preventDefault();
            event.stopPropagation();
            this.source = target;
            this.handles = document.createDivElement();

            final int leftBound = target.getOffsetLeft() + target.getOffsetWidth();
            this.moveHandle = createSpanElement(MOVE_CURSOR,
                                                leftBound - RESIZE_HANDLE_WIDTH - MOVE_HANDLE_WIDTH,
                                                MOVE_HANDLE_WIDTH);
            this.resizeHandle = createSpanElement(RESIZE_CURSOR,
                                                  leftBound - RESIZE_HANDLE_WIDTH,
                                                  RESIZE_HANDLE_WIDTH);
            handles.appendChild(moveHandle);
            handles.appendChild(resizeHandle);
            source.appendChild(handles);
        }

        private SpanElement createSpanElement(final Cursor cursor,
                                              final double left,
                                              final double width) {
            final SpanElement span = document.createSpanElement();
            span.setAttribute("title",
                              title);
            final Style style = span.getStyle();
            style.setCursor(cursor);
            style.setPosition(Position.ABSOLUTE);
            style.setBottom(0,
                            PX);
            style.setHeight(source.getOffsetHeight(),
                            PX);
            style.setTop(source.getOffsetTop(),
                         PX);
            style.setWidth(width,
                           PX);
            style.setLeft(left,
                          PX);
            return span;
        }

        @Override
        public void onPreviewNativeEvent(final NativePreviewEvent event) {
            final NativeEvent natEvent = event.getNativeEvent();
            final Element element = natEvent.getEventTarget().cast();
            final String eventType = natEvent.getType();
            if (!(element == moveHandle || element == resizeHandle)) {
                if ("mousedown".equals(eventType)) {
                    //No need to do anything, the event will be passed on to the column sort handler
                } else if (!dragging && "mouseover".equals(eventType)) {
                    cleanUp();
                }
                return;
            }
            final NativeEvent nativeEvent = getEventAndPreventPropagation(event);
            if ("mousedown".equals(eventType)) {
                if (element == resizeHandle) {
                    moveHandle.removeFromParent();
                    new ColumnResizeHelper(this,
                                           source,
                                           nativeEvent);
                } else {
                    new ColumnMoverHelper(this,
                                          source,
                                          nativeEvent);
                }
                dragging = true;
            }
        }

        private void cleanUp() {
            handler.removeHandler();
            handles.removeFromParent();
            current = null;
        }

        public void dragFinished() {
            dragging = false;
            cleanUp();
        }
    }

    private class ColumnResizeHelper implements NativePreviewHandler {

        private final HandlerRegistration handler = Event.addNativePreviewHandler(this);
        private final DivElement resizeLine = document.createDivElement();
        private final Style resizeLineStyle = resizeLine.getStyle();
        private final Element header;
        private final IDragCallback dragCallback;

        private ColumnResizeHelper(final IDragCallback dragCallback,
                                   final Element header,
                                   final NativeEvent event) {
            this.dragCallback = dragCallback;
            this.header = header;
            setLine(resizeLineStyle,
                    2,
                    0,
                    getTableBodyHeight(),
                    RESIZE_COLOR);
            moveLine(event.getClientX());
            tableElement.appendChild(resizeLine);
        }

        @Override
        public void onPreviewNativeEvent(final NativePreviewEvent event) {
            final NativeEvent nativeEvent = getEventAndPreventPropagation(event);
            final int clientX = nativeEvent.getClientX();
            final String eventType = nativeEvent.getType();
            if ("mousemove".equals(eventType)) {
                moveLine(clientX);
            } else if ("mouseup".equals(eventType)) {
                handler.removeHandler();
                resizeLine.removeFromParent();
                dragCallback.dragFinished();
                columnResized(Math.max(clientX - header.getAbsoluteLeft(),
                                       MINIMUM_COLUMN_WIDTH));
            }
        }

        private void moveLine(final int clientX) {
            final int xPos = clientX - table.getAbsoluteLeft();
            resizeLineStyle.setLeft(xPos,
                                    PX);
        }
    }

    private class ColumnMoverHelper implements NativePreviewHandler {

        private static final int ghostLineWidth = 4;
        private final HandlerRegistration handler = Event.addNativePreviewHandler(this);
        private final DivElement ghostLine = document.createDivElement();
        private final Style ghostLineStyle = ghostLine.getStyle();
        private final DivElement ghostColumn = document.createDivElement();
        private final Style ghostColumnStyle = ghostColumn.getStyle();
        private final int columnWidth;
        private final int[] columnXPositions;
        private final IDragCallback dragCallback;
        private int fromIndex = -1;
        private int toIndex;

        private ColumnMoverHelper(final IDragCallback dragCallback,
                                  final Element target,
                                  final NativeEvent event) {
            final int clientX = event.getClientX();
            final Element tr = getRowElement(target);
            final int columns = tr.getChildCount();

            this.dragCallback = dragCallback;
            this.columnWidth = target.getOffsetWidth();
            this.columnXPositions = new int[columns + 1];
            this.columnXPositions[0] = tr.getAbsoluteLeft();
            for (int i = 0; i < columns; ++i) {
                final int xPos = columnXPositions[i] + ((Element) tr.getChild(i)).getOffsetWidth();
                if (xPos > clientX && fromIndex == -1) {
                    fromIndex = i;
                }
                columnXPositions[i + 1] = xPos;
            }
            toIndex = fromIndex;
            final int bodyHeight = getTableBodyHeight();
            setLine(ghostColumnStyle,
                    columnWidth,
                    0,
                    bodyHeight,
                    MOVE_COLOR);
            setLine(ghostLineStyle,
                    ghostLineWidth,
                    0,
                    bodyHeight,
                    RESIZE_COLOR);
            ghostColumnStyle.setOpacity(GHOST_OPACITY);
            moveColumn(clientX);
            tableElement.appendChild(ghostColumn);
            tableElement.appendChild(ghostLine);
        }

        protected Element getRowElement(Element target) {
            Element parent = target.getParentElement();
            while (parent != null) {
                if (parent.getTagName().equalsIgnoreCase("tr")) {
                    return parent;
                }
                parent = parent.getParentElement();
            }
            return target.getParentElement();
        }

        @Override
        public void onPreviewNativeEvent(final NativePreviewEvent event) {
            final NativeEvent nativeEvent = getEventAndPreventPropagation(event);
            final String eventType = nativeEvent.getType();
            if ("mousemove".equals(eventType)) {
                moveColumn(nativeEvent.getClientX());
            } else if ("mouseup".equals(eventType)) {
                handler.removeHandler();
                ghostColumn.removeFromParent();
                ghostLine.removeFromParent();
                if (fromIndex != toIndex) {
                    columnMoved(fromIndex,
                                toIndex);
                }
                dragCallback.dragFinished();
            }
        }

        private void moveColumn(final int clientX) {
            final int pointer = clientX - columnWidth / 2;
            ghostColumnStyle.setLeft(pointer - table.getAbsoluteLeft(),
                                     PX);
            for (int i = 0; i < columnXPositions.length - 1; ++i) {
                if (clientX < columnXPositions[i + 1]) {
                    final int adjustedIndex = i > fromIndex ? i + 1 : i;
                    int lineXPos = columnXPositions[adjustedIndex] - table.getAbsoluteLeft();
                    if (adjustedIndex == columnXPositions.length - 1) {
                        lineXPos -= ghostLineWidth;
                    } else if (adjustedIndex > 0) {
                        lineXPos -= ghostLineWidth / 2;
                    }
                    ghostLineStyle.setLeft(lineXPos,
                                           PX);
                    toIndex = i;
                    break;
                }
            }
        }
    }
}
