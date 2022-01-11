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
package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.List;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;

/**
 * Base {@link NodeMouseClickHandler} to handle clicks to either the GridWidgets Header or Body.
 * This delegates event handling to the {@link NodeMouseEventHandler} provided in the {@link List}
 * parameter of the constructor. Delegation follows the order of {@link NodeMouseEventHandler} in the {@link List}.
 */
public class BaseGridWidgetMouseClickHandler extends BaseGridWidgetMouseEventHandler implements NodeMouseClickHandler {

    public BaseGridWidgetMouseClickHandler(final GridWidget gridWidget,
                                           final List<NodeMouseEventHandler> handlers) {
        super(gridWidget,
              handlers);
    }

    @Override
    public void onNodeMouseClick(final NodeMouseClickEvent event) {
        doEventDispatch(event);
    }
}