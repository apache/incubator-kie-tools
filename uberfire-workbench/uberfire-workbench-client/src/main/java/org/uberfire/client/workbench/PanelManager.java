package org.uberfire.client.workbench;

import java.util.List;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Internal framework component that handles the creation, destruction, layout, and composition (parent-child nesting)
 * of all panels that make up the workbench layout. Also orchestrates adding and removing parts to/from panels.
 * <p>
 * <b>Application code should not invoke any of the methods of this class directly.</b> Doing so will corrupt the state
 * of the PlaceManager, ActivityManager, and potentially other stateful framework components. Applications should always
 * initiate Workbench actions through the public methods on {@link PlaceManager}.
 */
public interface PanelManager {

    /**
     * Returns the description of the currently-active perspective.
     * 
     * @param perspective
     *            description of the current perspective. Will be null until the first call to
     *            {@link #setPerspective(PerspectiveDefinition)}, which is typically done by the {@link PlaceManager}
     *            shortly after the {@link ApplicationReadyEvent} is fired. After this, the returned value will never
     *            revert to null.
     */
    PerspectiveDefinition getPerspective();

    /**
     * Switches to the given perspective, replacing the previously active perspective. The PanelManager is responsible
     * for creating all panels described in the PerspectiveDefinition and its constituent parts.
     * <p>
     * TODO (question) should it also issue PlaceRequests for the default place in each part?
     * If so, these PlaceRequests should have their UpdateHistory flag set to false so that the
     * perspective switch is an atomic history item! (can unit test this too)
     * 
     * @param perspective
     *            description of the perspective to switch to. Must not be null.
     */
    void setPerspective( final PerspectiveDefinition perspective );

    /**
     * TODO may not need this anymore (should always be the root of the current perspective?)
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
     * Removes the given panel from the workbench layout, preserving child panels by reparenting them to the removed
     * panel's parent. Application code should not call this method directly; it is called by PlaceManager as part of
     * the overall procedure in closing a place.
     * 
     * @param toRemove
     *            the place that is closing. Must not be null.
     * @return true if the associated panel was found and removed; false if no matching panel could be found.
     */
    boolean removePanelForPlace( final PlaceRequest toRemove );

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
     * Sets the given widgets as the contents of the header area, replacing any existing widgets that were already in
     * the header area. Each widget is meant to fill the width of the page, and the widgets are meant to stack on top of
     * each other with the first one in the list being highest up on the page. Headers should remain in place even when
     * the perspective switches.
     * 
     * @param headers the list of headers in top-to-bottom stacking order. Never null, but can be empty.
     */
    void setHeaderContents( List<Header> headers );

    /**
     * Sets the given widgets as the contents of the footer area, replacing any existing widgets that were already in
     * the footer area. Each widget is meant to fill the width of the page, and the widgets are meant to stack on top of
     * each other with the first one in the list being highest up on the page. Footers should remain in place even when
     * the perspective switches.
     * 
     * @param footer the list of footer in top-to-bottom stacking order. Never null, but can be empty.
     */
    void setFooterContents( List<Footer> footers );

    /**
     * Notifies the PanelManager that the space available to the UberFire workbench has changed. The PanelManager should
     * respond by ensuring that its panels fill the new amount of available space. Normally this would be in response to
     * the browser window being resized by the user, but in a situation where an UberFire workbench is embedded in a
     * larger context, this could be controlled by the host application.
     * 
     * @param width
     *            the width in pixels that the entire workbench should take up.
     * @param height
     *            the height in pixels that the entire workbench should take up (includes space used by headers and
     *            footers).
     */
    void setWorkbenchSize( int width, int height );

}
