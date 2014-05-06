/*
 * Copyright 2012 JBoss Inc
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

import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Manages the Widget and DOM interaction of a panel. Part of the UberFire MVC system for panels. For a full explanation
 * of what a panel is in UberFire, see the class-level documentation for {@link WorkbenchPanelPresenter}.
 */
public interface WorkbenchPanelView<P extends WorkbenchPanelPresenter> extends UberView<P>, RequiresResize {

    P getPresenter();

    void clear();

    /**
     * Adds the given view to this panel if this panel does not already contain a view that handles the same
     * {@link PlaceRequest} as the given one. If this panel does already contain such a part, the existing one is
     * {@link #selectPart(PartDefinition) selected} and the given one is not added.
     * 
     * @param view the view to add as long as it is not a duplicate. Must not be null.
     */
    void addPart( final WorkbenchPartPresenter.View view );

    /**
     * Nests the given WorkbenchPanelView inside this one at the given position. If there is already a nested panel view
     * in the target position, it will be replaced with the given panel.
     * 
     * @param panel
     *            specifies the size that should be imposed on the nested view. Must not be null.
     *            FIXME: is this sensible/correct?
     * @param view
     *            the panel to nest inside this one. Must not be null.
     * @param position
     *            specifies which edge of this panel will be shared with the nested panel. Must not be null.
     */
    void addPanel( final PanelDefinition panel,
                   final WorkbenchPanelView view,
                   final Position position );

    /**
     * Assigns the given title to the given part, if the part belongs to this panel.
     * 
     * @param part
     *            the part whose title to change. Must not be null.
     * @param title
     *            the new title. Must not be null.
     * @param titleDecoration
     *            An optional widget to display beside the title. Note that some implementations do not support title
     *            decorations, and they will ignore this. Null is permitted, and means no decoration.
     */
    void changeTitle( final PartDefinition part,
                      final String title,
                      final IsWidget titleDecoration );

    /**
     * Makes the given part visible and focused, if it belongs to this view.
     * 
     * @param part
     *            the part to reveal and give focus to.
     * @return true if the part was found, made visible, and given focus. False if not.
     */
    boolean selectPart( final PartDefinition part );

    /**
     * Removes the given part from this view, if it belonged to this view.
     * 
     * @param part
     *            the part to remove.
     * @return true if the part was found and removed. False if not.
     */
    boolean removePart( final PartDefinition part );

    /**
     * Removes this panel from its parent, freeing all view resources that were associated with it.
     */
    void removePanel();

    /**
     * Requests or releases keyboard focus for the currently-visible part within this panel.
     * 
     * @param hasFocus
     *            if true, this panel will attempt to give keyboard focus to its currently-visible part. If false, this
     *            panel will attempt to release keyboard focus.
     */
    void setFocus( boolean hasFocus );

}