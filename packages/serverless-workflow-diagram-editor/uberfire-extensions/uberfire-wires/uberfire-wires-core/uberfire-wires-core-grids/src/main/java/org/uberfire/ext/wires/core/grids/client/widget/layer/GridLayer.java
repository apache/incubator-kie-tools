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
package org.uberfire.ext.wires.core.grids.client.widget.layer;

import java.util.Set;

import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseMoveHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IDrawable;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

/**
 * A specialised Layer that supports pass-through of MouseEvents from DOMElements to GridWidgets.
 * This implementation handles  drawing connectors between "linked" grids and acts as a GridSelectionManager.
 */
public interface GridLayer extends GridSelectionManager,
                                   GridPinnedModeManager,
                                   IContainer<Layer, IPrimitive<?>>,
                                   IDrawable<Layer>,
                                   NodeMouseDownHandler,
                                   NodeMouseMoveHandler,
                                   NodeMouseUpHandler {

    /**
     * Get the visible bounds of the Layer in the Viewport
     * @return
     */
    Bounds getVisibleBounds();

    /**
     * Get the state of any Handlers registered to the Grid
     * @return
     */
    GridWidgetDnDHandlersState getGridWidgetHandlersState();

    /**
     * Get the overlay panel.
     * @return
     */
    AbsolutePanel getDomElementContainer();

    /**
     * Set a reference to an AbsolutePanel that overlays the Canvas.
     * This can be used to overlay DOM elements on top of the Canvas.
     * @param getDomElementContainer The overlay panel
     */
    void setDomElementContainer(final AbsolutePanel getDomElementContainer);

    /**
     * Redraw the Grid. All updates are batched into a single draw on the next animation
     * frame. Execute the provided command after the batch redraw has been scheduled.
     * @param command The command to execute
     * @return
     */
    Layer batch(final GridLayerRedrawManager.PrioritizedCommand command);

    /**
     * Gets a collection of all connectors used to connect all {@link GridWidget} together.
     * @return A {@link Set} of connectors.
     */
    Set<IPrimitive<?>> getGridWidgetConnectors();

    /**
     * Refreshes all connectors used to connect all {@link GridWidget} together.
     */
    void refreshGridWidgetConnectors();

    /**
     * Moves the specified GridWidget into view without scrolling the Canvas.
     * If the GridLayer is not in "pinned mode" this method has no operation.
     * @param gridWidget The GridWidget to move into view.
     */
    void flipToGridWidget(final GridWidget gridWidget);

    /**
     * Scrolls the specified GridWidget into view.
     * If the GridLayer is in "pinned mode" this method has no operation.
     * @param gridWidget The GridWidget to scroll into view.
     */
    void scrollToGridWidget(final GridWidget gridWidget);
}
