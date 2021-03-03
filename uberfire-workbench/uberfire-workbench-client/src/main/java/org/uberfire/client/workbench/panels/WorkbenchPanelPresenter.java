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
package org.uberfire.client.workbench.panels;

import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

/**
 * Top-level interface for all panel presenters within the UberFire MVP framework.
 */
public interface WorkbenchPanelPresenter {

    /**
     * Returns a {@code @Portable} description of the current state of this panel.
     */
    PanelDefinition getDefinition();

    /**
     * Called by the framework when the panel instance is first created. Application code should not call this method
     * directly.
     *
     * @param definition description of the state this panel should put itself in. This panel is also responsible for keeping
     *                   the definition up to date with the panel's current state.
     */
    void setDefinition(final PanelDefinition definition);

    /**
     * Adds the given part to this panel's content area, updating this panel's definition and the part's definition to
     * reflect the new part ownership.
     * <p>
     * Panels each implement their own policy and user interface for part management. Some panels do not support parts
     * at all; others allow only a single part; still others can hold multiple parts at a time. Either way, panels that
     * do display parts typically display them one at a time. Those that support multiple parts include UI widgets
     * (eg. tabs or a dropdown list) that let the user select which one to display.
     * <p>
     * After the panel's border decorations, part switcher UI, title bar, and subpanel space has been accounted for, the
     * part's view typically occupies all remaining space within its parent panel.
     * @param part the part to add. Must not be null, and must not currently belong to any panel.
     * @throws UnsupportedOperationException if this panel does not support parts
     */
    void addPart(final WorkbenchPartPresenter part);

    /**
     * Removes the given part from this panel, updating this panel's definition and the part's definition to reflect
     * that the part no longer belongs to this panel.
     *
     * @return true if the given part was found and removed; false if this call had no effect
     * @see #addPart(WorkbenchPartPresenter)
     */
    boolean removePart(final PartDefinition part);

    /**
     * Returns the view that was given to this panel when it was first created.
     */
    WorkbenchPanelView getPanelView();

    void onResize(final int width,
                  final int height);
}
