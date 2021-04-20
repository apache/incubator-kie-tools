/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.widget.dnd;

import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * MouseUpHandler to handle completion of drag operations and release resources.
 */
public class GridWidgetDnDMouseUpHandler implements NodeMouseUpHandler {

    protected final GridLayer layer;
    protected final GridWidgetDnDHandlersState state;

    public GridWidgetDnDMouseUpHandler(final GridLayer layer,
                                       final GridWidgetDnDHandlersState state) {
        this.layer = layer;
        this.state = state;
    }

    @Override
    public void onNodeMouseUp(final NodeMouseUpEvent event) {
        switch (state.getOperation()) {
            case NONE:
            case COLUMN_MOVE_PENDING:
            case COLUMN_RESIZE_PENDING:
            case ROW_MOVE_PENDING:
            case COLUMN_RESIZE:
                break;
            case COLUMN_MOVE:
            case COLUMN_MOVE_INITIATED:
            case ROW_MOVE:
            case ROW_MOVE_INITIATED:
                //Clean-up the GridWidgetDnDProxy
                layer.remove(state.getEventColumnHighlight());
                layer.batch();
                break;
        }

        //Reset state. Defer until the next browser event loop iteration to enable ClickEvents to be processed.
        scheduleDeferred(() -> {
            state.reset();
            layer.getViewport().getElement().getStyle().setCursor(state.getCursor());
        });
    }

    void scheduleDeferred(final Command command) {
        Scheduler.get().scheduleDeferred(command::execute);
    }
}
