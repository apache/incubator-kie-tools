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

import java.util.Map;

import org.uberfire.client.workbench.WorkbenchLayout;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Top-level interface for all panel presenters within the UberFire MVP framework.
 */
public interface WorkbenchPanelPresenter {

    /**
     * Returns the current parent of this panel presenter.
     *
     * @return the parent panel presenter. If this panel is the root, or it is not attached to a parent, the return
     *         value is null.
     */
    public WorkbenchPanelPresenter getParent();

    /**
     * Sets the current parent of this panel presenter. This method should only be called by another
     * WorkbenchPanelPresenter when adding or removing this panel as a child.
     *
     * @param parent
     *            the new parent of this panel. If this panel is being removed, the parent should be set to null.
     */
    public void setParent( final WorkbenchPanelPresenter parent );

    /**
     * Returns a {@code @Portable} description of the current state of this panel.
     */
    public PanelDefinition getDefinition();

    /**
     * Called by the framework when the panel instance is first created. Application code should not call this method
     * directly.
     *
     * @param definition
     *            description of the state this panel should put itself in. This panel is also responsible for keeping
     *            the definition up to date with the panel's current state.
     */
    public void setDefinition( final PanelDefinition definition );

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
     *
     * @param part
     *            the part to add. Must not be null, and must not currently belong to any panel.
     * @throws UnsupportedOperationException if this panel does not support parts
     */
    public void addPart( final WorkbenchPartPresenter part );

    /**
     * Adds the given part to this panel with the given context ID, updating this panel's definition and the part's
     * definition to reflect the new part ownership.
     *
     * @param part
     *            the part to add. Must not be null, and must not currently belong to any panel.
     * @see #addPart(WorkbenchPartPresenter)
     * @throws UnsupportedOperationException if this panel does not support parts
     */
    public void addPart( final WorkbenchPartPresenter part,
                         final String contextId );

    /**
     * Removes the given part from this panel, updating this panel's definition and the part's definition to reflect
     * that the part no longer belongs to this panel.
     *
     * @return true if the given part was found and removed; false if this call had no effect
     * @see #addPart(WorkbenchPartPresenter)
     */
    public boolean removePart( final PartDefinition part );

    /**
     * Adds the given panel as a subpanel of this one in the given position. Panels typically only allow one child panel
     * in each position, and may throw an exception or make alternative arrangements (for example, forward the request
     * to a child panel) when you try to add a child panel to an already-occupied slot.
     * <p>
     * Subpanels are typically always visible, and take up space within the bounds of their parent panel.
     *
     * @param child
     *            the panel to add. The presenter, its view, and its definition must not belong to any parent. As a side
     *            effect of this call (if the call is successful), the given presenter, its view, and its definition
     *            will get attached to their new parents.
     * @param position
     *            the position to add the child at. Different panel implementations support different position types.
     */
    public void addPanel( final WorkbenchPanelPresenter child,
                          final Position position );

    /**
     * Removes the given panel presenter and its view from this panel, freeing all resources associated with them.
     *
     * @param child
     *            The child panel to remove. Must be a direct child of this panel, and must be empty (contain no parts
     *            or child panels). Null is not permitted.
     * @return true if the child was found and removed from this panel; false if the child panel could not be found.
     */
    public boolean removePanel( WorkbenchPanelPresenter child );

    /**
     * Returns the immediate child panels of this panel. Note that panels and parts are not the same thing; this method
     * only returns the panels.
     *
     * @return an unmodifiable snapshot of the immediate child panels nested within this one. Never null, and will not
     *         update to reflect subsequent changes to this panel's children. Safe to iterate over when adding or
     *         removing child panels.
     */
    public Map<Position, WorkbenchPanelPresenter> getPanels();

    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration );

    public void setFocus( final boolean hasFocus );

    public boolean selectPart( final PartDefinition part );

    /**
     * Makes this panel's view take up most of the space on the workbench. The exact meaning of "maximize" is left to
     * the implementation of {@link WorkbenchLayout}.
     */
    public void maximize();

    /**
     * Restores this panel's view to its original unmaximized size and position.
     */
    public void unmaximize();

    /**
     * Returns the view that was given to this panel when it was first created.
     */
    public WorkbenchPanelView getPanelView();

    public void onResize( final int width,
                          final int height );

    /**
     * Returns the panel type that should be used when adding child panels of type
     * {@link PanelDefinition#PARENT_CHOOSES_TYPE}.
     *
     * @return the fully-qualified class name of a WorkbenchPanelPresenter implementation. Returns null if
     *         this panel presenter does not allow child panels.
     */
    public String getDefaultChildType();

}
