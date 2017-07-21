/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.client.demo.grids;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

/**
 * View and Presenter definition for WiresGridDemo
 */
public interface WiresGridsDemoView extends IsWidget,
                                            GridSelectionManager,
                                            HasKeyDownHandlers,
                                            RequiresResize {

    /**
     * Adds a GridWidget to the View.
     * @param gridWidget
     */
    void add(final GridWidget gridWidget);

    /**
     * Refreshes the View reflecting changes to any associated GridWidget.
     */
    void refresh();

    /**
     * Gets the underlying GridLayer associated with the View. A reference to the GridLayer is
     * needed to construct DOM based Column definitions since they overlay HTML DOM elements
     * over the Canvas.
     * @return
     */
    GridLayer getGridLayer();

    /**
     * Gets the underlying GridLienzoPanel associated with the View.
     * @return The associated GridLienzoPanel.
     */
    GridLienzoPanel getGridPanel();

    /**
     * Adds a handler for when the Zoom level in the View is changed.
     * @param handler
     * @return
     */
    HandlerRegistration addZoomChangeHandler(final ChangeHandler handler);

    /**
     * Gets the selected Zoom level from the View.
     * @return
     */
    int getSelectedZoomLevel();

    /**
     * Sets the Zoom level of the View.
     * @param zoom An int based percentage, where for example 100 represents 100%. Must be positive.
     */
    void setZoom(final int zoom);

    /**
     * Adds a handler for when the Theme associated with View rendering is changed.
     * @param handler
     * @return
     */
    HandlerRegistration addThemeChangeHandler(final ChangeHandler handler);

    /**
     * Gets the selected Theme from the View.
     * @return
     */
    GridRendererTheme getSelectedTheme();

    /**
     * Adds a handler for when the merged state of the View is changed.
     * @param handler
     * @return
     */
    HandlerRegistration addMergedStateValueChangeHandler(final ValueChangeHandler<Boolean> handler);

    /**
     * Sets the merged state of the View.
     * @param isMerged
     */
    void setMergedState(final boolean isMerged);

    /**
     * Adds a handler for when the User, interacting with the View, requests a row to be appended.
     * @param handler
     * @return
     */
    HandlerRegistration addAppendRowClickHandler(final ClickHandler handler);

    /**
     * Adds a handler for when the User, interacting with the View, requests a row to be deleted.
     * @param handler The handler. Cannot be null.
     * @return A registration for the handler.
     */
    HandlerRegistration addDeleteRowClickHandler(final ClickHandler handler);

    /**
     * Presenter definition.
     */
    interface Presenter extends GridSelectionManager {

    }
}
