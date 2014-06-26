package org.uberfire.client.workbench;

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
 * Handles the creation, layout, and composition (parent-child nesting) of all panels that make up a perspective.
 * The outer most workbench panels (header, footer, perspective container)
 * are managed by the {@link org.uberfire.client.workbench.WorkbenchLayout}.
 */
public interface PanelManager {

    /**
     * Returns the description of the currently-active perspective.
     * Will be null until the first call to
     * {@link #setPerspective(PerspectiveDefinition)}, which is typically done by the {@link PlaceManager}
     * shortly after the {@link ApplicationReadyEvent} is fired. After this, the returned value will never
     * revert to null.
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

    PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
            final Position position );

    PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
            final Position position,
            final Integer height,
            final Integer width,
            final Integer minHeight,
            final Integer minWidth );

    PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
            final PanelDefinition childPanel,
            final Position position );

    void onPartFocus( final PartDefinition part );

    void onPartLostFocus();

    void onPanelFocus( final PanelDefinition panel );

    /**
     * Closes the given part. This is a convenience method for <tt>placeManager.closePlace(part.getPlace())</tt>.
     *
     * @param part
     *            the part to close (remove from the GUI). Must not be null.
     */
    void closePart( final PartDefinition part );

}
