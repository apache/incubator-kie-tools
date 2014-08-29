package org.uberfire.client.workbench.panels.support;

import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Helper class for docking panels.
 */
public interface PanelSupport {

    /**
     * Adds the given panel view to the given target view in the given position, inserting a new parent container into
     * the layout hierarchy if necessary.
     *
     * @param panel
     *            description of the new panel. Consulted for initial size and minimum size information.
     * @param newView
     *            the new panel to add as a child of the target
     * @param targetView
     *            the panel that gets the new child
     * @param position
     *            the position within targetView where the new view will be placed
     * @return the parent container that now holds both the target view and the newly added view.
     */
    IsWidget addPanel( final PanelDefinition panel,
                     final WorkbenchPanelView newView,
                     final WorkbenchPanelView targetView,
                     final CompassPosition position );

    /**
     * Removes the given panel from its container, restoring the containment hierarchy to what it was before
     * {@link #addPanel(PanelDefinition, WorkbenchPanelView, WorkbenchPanelView, Position)} modified it to accommodate
     * the new view.
     *
     * @param view
     *            the view to remove from its parent
     * @param parent
     *            the container that was returned from
     *            {@link #addPanel(PanelDefinition, WorkbenchPanelView, WorkbenchPanelView, Position)} when the given
     *            view was added as a child.
     */
    boolean remove( final WorkbenchPanelView<?> view,
                    final IsWidget parent );
}
