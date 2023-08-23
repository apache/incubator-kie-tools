/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.dnd;

import org.kie.workbench.common.dmn.client.widgets.grid.model.HasRowDragRestrictions;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDMouseMoveHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class DelegatingGridWidgetDndMouseMoveHandler extends GridWidgetDnDMouseMoveHandler {

    public DelegatingGridWidgetDndMouseMoveHandler(final GridLayer layer,
                                                   final GridWidgetDnDHandlersState state) {
        super(layer,
              state);
    }

    @Override
    protected void findMovableRows(final GridWidget view,
                                   final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                   final double cx,
                                   final double cy) {
        super.findMovableRows(view,
                              renderingInformation,
                              cx,
                              cy);
        if (view instanceof HasRowDragRestrictions) {
            final HasRowDragRestrictions hasRowDragRestrictions = (HasRowDragRestrictions) view;
            if (!hasRowDragRestrictions.isRowDragPermitted(state)) {
                state.reset();
                layer.getViewport().getElement().style.cursor = state.getCursor().getCssName();
            }
        }
    }
}
