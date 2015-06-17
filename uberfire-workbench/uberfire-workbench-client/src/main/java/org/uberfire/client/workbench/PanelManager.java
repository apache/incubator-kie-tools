package org.uberfire.client.workbench;

import com.google.gwt.user.client.ui.HasWidgets;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;

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
     * Returns the description of the entire panel + part tree that makes up the UI in its current state.
     */
    PanelDefinition getRoot();

    /**
     * Adds the given part to the given panel, which must already be part of the visible workbench layout. Fires a
     * {@link SelectPlaceEvent} with the given {@link PlaceRequest} once the part has been added.
     *
     * @param place
     *            The PlaceRequest that the part was resolved from. Not null.
     * @param part
     *            The description of the part to add. Not null.
     * @param panel
     *            definition of the panel to add the part to (must describe a panel that is already present in the
     *            layout). Not null.
     * @param menus
     *            The menus to display for the given part. Null means no menus.
     * @param uiPart
     *            The part's title and physical view. Not null.
     * @param contextId
     *            part of a removed framework feature (TODO: remove this?)
     * @param minInitialWidth
     *            minimum pixel width of the part's activity, or null if there is no known minimum width. The target
     *            panel will expand to the this width if the panel is not already at least as wide, and only if it
     *            supports resizing on the horizontal axis.
     * @param minInitialHeight
     *            minimum pixel height of the part's activity, or null if there is no known minimum height. The target
     *            panel will expand to this height if the panel is not already at least as tall, and only if it supports
     *            resizing on the vertical axis.
     */
    void addWorkbenchPart( final PlaceRequest place,
                           final PartDefinition part,
                           final PanelDefinition panel,
                           final Menus menus,
                           final UIPart uiPart,
                           final String contextId,
                           final Integer minInitialWidth,
                           final Integer minInitialHeight );

    /**
     * Adds an empty child panel of the target panel's default child type at the given position within the target panel.
     * The new child panel will have the given dimensions and minimum sizes set on it.
     * <p>
     * TODO: the usefulness of this method to callers is questionable (versus creating a new child panel definition and
     * calling {@link #addWorkbenchPanel(PanelDefinition, PanelDefinition, Position)}). candidate for deletion.
     */
    PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                       final Position position,
                                       final Integer height,
                                       final Integer width,
                                       final Integer minHeight,
                                       final Integer minWidth );

    /**
     * Adds the given child panel to the given target panel at the given position within the target. Upon successful
     * completion of this method, the child panel will have a new parent panel. Its {@link PanelDefinition},
     * {@link WorkbenchPanelPresenter}, and {@link WorkbenchPanelView} and those of its new parent will be updated to
     * reflect the new relationship. Note that the given target panel will not necessarily be the new parent: panel
     * implementations may choose to avoid collisions (more than one child panel in the same position) by redirecting
     * requests to add children.
     *
     * @throws IllegalStateException
     *             if {@code targetPanel} already has a child at {@code position} and it doesn't have any special
     *             collision avoidance logic
     * @throws UnsupportedOperationException
     *             if {@code targetPanel} doesn't support child panels.
     * @throws IllegalArgumentException
     *             if {@code targetPanel} doesn't understand the given {@code position} value.
     */
    PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                       final PanelDefinition childPanel,
                                       final Position position );

    /**
     * Creates an UberFire panel and installs its view in the given widget container.
     * <p>
     * <h3>Custom Panel Lifecycle</h3>
     * <p>
     * Custom panels can be disposed like any other panel: by calling {@link #removeWorkbenchPanel(PanelDefinition)}.
     * Additionally, custom panels are monitored for DOM detachment. When a custom panel's view is removed from the DOM
     * (whether directly removed from its parent or some ancestor is removed,) all the panel's parts are closed and then
     * the associated panel is disposed.
     *
     * @param container
     *            the widget container to install the new panel in. The new panel will fill the container.
     * @return the definition for the newly constructed panel. Never null. The panel's type will be {@code panelType};
     *         its parent will be null; {@code isRoot()} will return false.
     */
    PanelDefinition addCustomPanel( HasWidgets container, String panelType );

    /**
     * Removes the panel associated with the given definition, removing the panel's presenter and view from the
     * workbench, and freeing any resources associated with them. The panel must have no parts and no child panels.
     *
     * @param toRemove
     *            the panel to remove from the workbench layout. Must not be null.
     * @throws IllegalStateException
     *             if the panel contains parts or child panels
     * @throws IllegalArgumentException
     *             if no panel presenter is currently associated with the given definition
     */
    void removeWorkbenchPanel( final PanelDefinition toRemove ) throws IllegalStateException;

    /**
     * Removes the part associated with the given PlaceRequest from the panel that contains it. If this operation
     * removes the last part from the panel, and the panel is not the root panel, it will be removed from the workbench
     * layout. Child panels are preserved by reparenting them to the removed panel's parent. Application code should not
     * call this method directly; it is called by PlaceManager as part of the overall procedure in closing a place.
     *
     * @param toRemove
     *            the place that is closing. Must not be null.
     * @return true if the associated part was found and removed; false if no matching part could be found.
     */
    boolean removePartForPlace( final PlaceRequest toRemove );

    void onPartFocus( final PartDefinition part );

    void onPartLostFocus();

    /**
     * Gives focus to the given panel, if it is known to this PanelManager. Also removes focus from all other panels
     * associated with this PanelManager.
     *
     * @param panel
     *            the panel to give focus to. May be null, in which case all panels will lose focus.
     */
    void onPanelFocus( final PanelDefinition panel );

    /**
     * Closes the given part. This is a convenience method for <tt>placeManager.closePlace(part.getPlace())</tt>.
     *
     * @param part
     *            the part to close (remove from the GUI). Must not be null.
     */
    void closePart( final PartDefinition part );

    /**
     * Clears all existing panel structure from the user interface, then installs a new root panel according to the
     * specifications in the given {@link PanelDefinition}. Only installs the root panel; does not build the child
     * panel/part structure recursively.
     *
     * @param root
     *            description of the new root panel to install. Must not be null.
     */
    void setRoot( PerspectiveActivity activity, PanelDefinition root );

    /**
     * Returns the first PanelDefinition whose place matches the given one.
     *
     * @return the definition for the panel servicing the given place, or null if no such part can be found.
     */
    PanelDefinition getPanelForPlace( PlaceRequest place );

    /**
     * @param part the part that has been maximized
     */
    void onPartMaximized( PartDefinition part );

    /**
     * @param part the part that has been minimized
     */
    void onPartMinimized( PartDefinition part );
}