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

import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.WorkbenchLayout;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Manages the Widget and DOM interaction of a panel. Part of the UberFire MVC system for panels. For a full explanation
 * of what a panel is in UberFire, see the class-level documentation for {@link WorkbenchPanelPresenter}.
 * <p>
 * <h2>View Lifecycle</h2>
 * <p>
 * UberFire Panel Views are Dependent-scoped beans managed by the Errai IOC container. Views are always created by
 * injection into their presenter, which ties their bean lifecycle to that of the presenter: they are created when the
 * presenter is created, and they are destroyed when the presenter is destroyed.
 * <p>
 * This is the lifecycle of a WorkbenchPanelView:
 * <ol>
 *  <li>The view's constructor is invoked by Errai IOC. At this point, it is not yet safe to access injected
 *      members (they may be uninitialized proxies).
 *  <li>The view's {@code @PostConstruct} method (if it has one) is invoked by Errai IOC. Safe to access injected members.
 *  <li>The view's {@link #init(Object)} method is invoked by the presenter. The argument is a reference to the presenter itself.
 *  <li>The view's widget (obtained from {@link #asWidget()}) is added to the parent panel's widget.
 *  <li>The view is now in service, and any of its public methods can be called.
 *  <li>When the panel is no longer needed, Errai IOC is told to destroy the presenter bean. This results in the view bean
 *      being destroyed too. At this point, the view's {@code @PreDestroy} method is invoked by Errai IOC.
 * </ol>
 */
public interface WorkbenchPanelView<P extends WorkbenchPanelPresenter> extends UberView<P>, RequiresResize {

    /**
     * Returns this view's presenter.
     *
     * @return the presenter that this view is bound to. Will return null if invoked before the presenter calls
     *         {@link #init(Object)}; afterward, the return value is never null.
     */
    P getPresenter();

    /**
     * Adds the given part view to this panel if this panel does not already contain a view that handles the same
     * {@link PlaceRequest} as the given one. If this panel does already contain such a part, the existing one is
     * {@link #selectPart(PartDefinition) selected} and the given one is not added.
     *
     * @param view the view to add as long as it is not a duplicate. Must not be null.
     */
    void addPart( final WorkbenchPartPresenter.View view );

    /**
     * Nests the given WorkbenchPanelView inside this one at the given position, which must be unoccupied. This is an
     * optional feature of WorkbenchPanelView: not all implementations support nested child panels. Additionally,
     * different panels support different {@link Position} types. Implementations should document whether or not they
     * support child panels, and if so, what types of Positions they understand.
     *
     * @param panel
     *            specifies the size that should be imposed on the nested view. Must not be null. FIXME: is this
     *            sensible/correct?
     * @param view
     *            the panel to nest inside this one. Must not be null.
     * @param position
     *            specifies which edge of this panel will be shared with the nested panel. Must not be null.
     * @throws IllegalStateException
     *             if the given position is already occupied by a child panel.
     * @throws IllegalArgumentException
     *             if the given child position is not understood by this type of panel.
     * @throws UnsupportedOperationException
     *             if this panel does not support child panels at all.
     */
    void addPanel( final PanelDefinition panel,
                   final WorkbenchPanelView<?> view,
                   final Position position );

    /**
     * Removes the view widget associated with the given child from this panel, freeing any resources that were
     * allocated by this panel when the child was added.
     */
    boolean removePanel( WorkbenchPanelView<?> child );

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
     * Informs this view that it has gained or lost keyboard focus. Focused views may respond by updating their style to
     * look more prominent than unfocused views.
     *
     * @param hasFocus
     *            if true, this panel now has focus. If false, this panel does not have focus.
     */
    void setFocus( boolean hasFocus );

    /**
     * Sets the ID of the physical root element of this view. For HTML-based views, this is the {@code id} attribute of
     * the view's top-level DOM element. Implementations for other view technologies should map this to whatever the
     * underlying widget/component system uses for unique identifiers.
     *
     * @param elementId
     *            the element ID to set. If null, the ID value will be cleared.
     */
    void setElementId( String elementId );

    /**
     * Returns the widget that defines the boundaries of this panel view for purposes of drag-and-drop.
     *
     * @return the widget whose boundaries define the region where workbench parts can be dropped into this panel. For
     *         simple panel types that do not support child panels, this will typically be the same widget returned by
     *         {@link #asWidget()}. For fancier panels, this will typically be some child panel within the view's
     *         internal structure.
     *         <p>
     *         If the return value is null, parts will not be droppable on this view.
     */
    Widget getPartDropRegion();

    /**
     * Maximizes this view using {@link WorkbenchLayout#maximize(Widget)}.
     */
    void maximize();

    /**
     * Restores this view to its original unmaximized size and position using {@link WorkbenchLayout#unmaximize(Widget)}.
     */
    void unmaximize();
}
