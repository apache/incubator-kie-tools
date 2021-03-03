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

package org.uberfire.client.workbench;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CustomPanelDefinition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

/**
 * Internal framework component that handles the creation, destruction, layout, and composition (parent-child nesting)
 * of all panels that make up a perspective. Also orchestrates adding and removing parts to/from panels. The outer most
 * workbench panels (header, footer, perspective container) are managed by the
 * {@link org.uberfire.client.workbench.WorkbenchLayout}.
 * <p>
 * <b>Application code should not invoke any of the methods of this class directly.</b> Doing so will corrupt the state
 * of the PlaceManager, ActivityManager, and potentially other stateful framework components. Applications should always
 * initiate Workbench actions through the public methods on {@link PlaceManager}.
 */
public interface PanelManager {

    /**
     * Adds the given part to the given panel, which must already be part of the visible workbench layout.
     * @param place The PlaceRequest that the part was resolved from. Not null.
     * @param part The description of the part to add. Not null.
     * @param panel definition of the panel to add the part to (must describe a panel that is already present in the
     * layout). Not null.
     * @param widget The widget.
     * @param minInitialWidth minimum pixel width of the part's activity, or null if there is no known minimum width. The target
     * panel will expand to the this width if the panel is not already at least as wide, and only if it
     * supports resizing on the horizontal axis.
     * @param minInitialHeight minimum pixel height of the part's activity, or null if there is no known minimum height. The target
     * panel will expand to this height if the panel is not already at least as tall, and only if it supports
     * resizing on the vertical axis.
     */
    void addWorkbenchPart(final PlaceRequest place,
                          final PartDefinition part,
                          final PanelDefinition panel,
                          final IsWidget widget,
                          final Integer minInitialWidth,
                          final Integer minInitialHeight);

    /**
     * Creates an UberFire panel and installs its view in the given widget container.
     * <p>
     * <h3>Custom Panel Lifecycle</h3>
     * <p>
     * Custom panels can be disposed like any other panel: by calling {@link #removeWorkbenchPanel(PanelDefinition)}.
     * Additionally, custom panels are monitored for DOM detachment. When a custom panel's view is removed from the DOM
     * (whether directly removed from its parent or some ancestor is removed,) all the panel's parts are closed and then
     * the associated panel is disposed.
     * @return the definition for the newly constructed panel. Never null. The panel's type will be {@code panelType};
     * its parent will be null; {@code isRoot()} will return false.
     */
    CustomPanelDefinition addCustomPanel(HasWidgets container);

    /**
     * Removes the panel associated with the given definition, removing the panel's presenter and view from the
     * workbench, and freeing any resources associated with them. The panel must have no parts and no child panels.
     * @param toRemove the panel to remove from the workbench layout. Must not be null.
     * @throws IllegalStateException if the panel contains parts or child panels
     * @throws IllegalArgumentException if no panel presenter is currently associated with the given definition
     */
    void removeWorkbenchPanel(final PanelDefinition toRemove) throws IllegalStateException;

    /**
     * Removes the part associated with the given PlaceRequest from the panel that contains it. If this operation
     * removes the last part from the panel, and the panel is not the root panel, it will be removed from the workbench
     * layout. Child panels are preserved by reparenting them to the removed panel's parent. Application code should not
     * call this method directly; it is called by PlaceManager as part of the overall procedure in closing a place.
     * @param toRemove the place that is closing. Must not be null.
     * @return true if the associated part was found and removed; false if no matching part could be found.
     */
    boolean removePartForPlace(final PlaceRequest toRemove);

    /**
     * Clears all existing panel structure from the user interface, then installs a new root panel according to the
     * specifications in the given {@link PanelDefinition}. Only installs the root panel; does not build the child
     * panel/part structure recursively.
     * @param root description of the new root panel to install. Must not be null.
     */
    void setRoot(PanelDefinition root);
}