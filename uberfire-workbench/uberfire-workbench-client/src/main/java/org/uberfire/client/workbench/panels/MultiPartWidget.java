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
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;

import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;

public interface MultiPartWidget extends IsWidget,
RequiresResize,
HasBeforeSelectionHandlers<PartDefinition>,
HasSelectionHandlers<PartDefinition> {

    void setPresenter( final WorkbenchPanelPresenter presenter );

    void setDndManager( final WorkbenchDragAndDropManager dndManager );

    /**
     * Removes all contained WorkbenchParts from this multi-part panel. Part Activities are not closed and part Presenters
     * are not freed.
     */
    void clear();

    void addPart( final WorkbenchPartPresenter.View view );

    void changeTitle( final PartDefinition part,
                      final String title,
                      final IsWidget titleDecoration );

    /**
     * Makes the given part visible if it is a direct child of this widget.
     *
     * @param part
     *            the direct child part to select. Must not be null.
     * @return true if the part was found as a direct child of this widget, and it was therefore selected. False if the
     *         part was not found, in which case this method had no effect.
     */
    boolean selectPart( final PartDefinition part );

    /**
     * Removes the given part from this widget. If the part was currently selected (visible) when removed, another part
     * will be selected to take its place.
     *
     * @param part
     *            the part to remove. Must not be null.
     * @return True if the given part was found as a direct child of this widget, in which case it has been removed.
     *         False if the given part was not found, in which case this method had no effect.
     */
    boolean remove( final PartDefinition part );

    /**
     * Informs this widget that its containing panel view has gained or lost panel focus. Views within focused panels
     * may respond by updating their style to look more prominent than unfocused views.
     *
     * @param hasFocus
     *            if true, the containing panel now has focus. If false, the panel does not have focus.
     */
    void setFocus( final boolean hasFocus );

    /**
     * Registers the given Command to be called each time something happens (for example, a UI gesture or parhaps an API
     * call) to make this widget believe its containing panel should be the focused panel.
     * <p>
     * Restated for clarity: the given command is called when this view thinks its panel should get focus. The command
     * is <i>not</i> called as a side effect of calls to {@link #setFocus(boolean)}.
     *
     * @param doWhenFocused
     *            the command to call when this widget believes its panel should become focused. Typically, the given
     *            command will call into the PanelManager to give focus to the containing panel.
     */
    void addOnFocusHandler( final Command doWhenFocused );

    /**
     * Returns the number of parts currently held by this widget.
     */
    int getPartsSize();
}
