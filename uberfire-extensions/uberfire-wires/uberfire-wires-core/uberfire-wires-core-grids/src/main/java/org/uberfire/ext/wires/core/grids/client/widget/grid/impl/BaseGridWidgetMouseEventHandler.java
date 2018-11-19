/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.List;
import java.util.Optional;

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;

/**
 * Base {@link AbstractNodeMouseEvent} handler that delegates handling of events to registered delegates.
 */
public abstract class BaseGridWidgetMouseEventHandler {

    protected GridWidget gridWidget;
    protected List<NodeMouseEventHandler> handlers;

    public BaseGridWidgetMouseEventHandler(final GridWidget gridWidget,
                                           final List<NodeMouseEventHandler> handlers) {
        this.gridWidget = gridWidget;
        this.handlers = handlers;
    }

    public void doEventDispatch(final AbstractNodeMouseEvent event) {
        if (!gridWidget.isVisible()) {
            return;
        }
        //Convert Canvas co-ordinate to Grid co-ordinate
        final Point2D relativeLocation = CoordinateUtilities.convertDOMToGridCoordinate(gridWidget,
                                                                                        new Point2D(event.getX(),
                                                                                                    event.getY()));

        //Extract Header row and column indexes
        final Integer uiHeaderRowIndex = CoordinateUtilities.getUiHeaderRowIndex(gridWidget,
                                                                                 relativeLocation);
        final Integer uiHeaderColumnIndex = CoordinateUtilities.getUiColumnIndex(gridWidget,
                                                                                 relativeLocation.getX());

        //Extract Body row and column indexes
        final Integer uiRowIndex = CoordinateUtilities.getUiRowIndex(gridWidget,
                                                                     relativeLocation.getY());
        final Integer uiColumnIndex = CoordinateUtilities.getUiColumnIndex(gridWidget,
                                                                           relativeLocation.getX());

        handlers.stream().forEach(handler -> handler.onNodeMouseEvent(gridWidget,
                                                                      relativeLocation,
                                                                      Optional.ofNullable(uiHeaderRowIndex),
                                                                      Optional.ofNullable(uiHeaderColumnIndex),
                                                                      Optional.ofNullable(uiRowIndex),
                                                                      Optional.ofNullable(uiColumnIndex),
                                                                      event));
    }
}