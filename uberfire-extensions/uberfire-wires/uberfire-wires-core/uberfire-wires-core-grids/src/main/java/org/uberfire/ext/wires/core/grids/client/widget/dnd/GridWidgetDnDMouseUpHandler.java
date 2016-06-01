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
import com.google.gwt.dom.client.Style;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * MouseUpHandler to handle completion of drag operations and release resources.
 */
public class GridWidgetDnDMouseUpHandler implements NodeMouseUpHandler {

    private final GridLayer layer;
    private final GridWidgetDnDHandlersState state;

    public GridWidgetDnDMouseUpHandler( final GridLayer layer,
                                        final GridWidgetDnDHandlersState state ) {
        this.layer = layer;
        this.state = state;
    }

    @Override
    public void onNodeMouseUp( final NodeMouseUpEvent event ) {
        switch ( state.getOperation() ) {
            case NONE:
            case COLUMN_MOVE_PENDING:
            case COLUMN_RESIZE_PENDING:
            case COLUMN_RESIZE:
            case ROW_MOVE_PENDING:
                break;
            case COLUMN_MOVE:
            case ROW_MOVE:
                //Clean-up the GridWidgetDnDProxy
                layer.remove( state.getEventColumnHighlight() );
                layer.batch();
        }

        //Reset state
        state.clearActiveGridWidget();
        state.clearActiveGridColumns();
        state.clearActiveHeaderMetaData();
        state.clearActiveGridRows();
        state.setOperation( GridWidgetDnDHandlersState.GridWidgetHandlersOperation.NONE );
        state.setCursor( Style.Cursor.DEFAULT );
        layer.getViewport().getElement().getStyle().setCursor( state.getCursor() );
    }

}
