package org.uberfire.client.workbench;

import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UIPart;
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

    void addWorkbenchPart( final PlaceRequest place,
                           final PartDefinition part,
                           final PanelDefinition panel,
                           final Menus menus,
                           final UIPart uiPart );

    void addWorkbenchPart( final PlaceRequest place,
                           final PartDefinition part,
                           final PanelDefinition panel,
                           final Menus menus,
                           final UIPart uiPart,
                           final String contextId );

    /**
     * Adds an empty child panel of the target panel's default child type at the given position within the target panel.
     * Only the presenters and views are manipulated; it is assumed the caller will add the new child panel definition
     * to the target after this method returns.
     * <p>
     * TODO: the usefulness of this method to callers is questionable. candidate for deletion.
     */
    PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                       final Position position );

    /**
     * Adds an empty child panel of the target panel's default child type at the given position within the target panel.
     * The new child panel will have the given dimensions and minimum sizes set on it. Only the presenters and views are
     * manipulated; it is assumed the caller will add the new child panel definition to the target after this method
     * returns.
     * <p>
     * TODO: the usefulness of this method to callers is questionable. candidate for deletion.
     */
    PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                       final Position position,
                                       final Integer height,
                                       final Integer width,
                                       final Integer minHeight,
                                       final Integer minWidth );

    /**
     * Adds the given child panel to the given target panel at the given position within the target. Only the presenters
     * and views are manipulated; it is assumed the child panel definition is already attached to the parent panel
     * definition (or the caller will attach it later).
     */
    PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                       final PanelDefinition childPanel,
                                       final Position position );

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

}
