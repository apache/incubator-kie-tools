package org.uberfire.client.workbench;

import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Manager responsible for adding or removing WorkbenchParts to WorkbenchPanels;
 * either as a consequence of explicitly opening or closing WorkbenchParts or
 * implicitly as part of a drag operation.
 */
public interface PanelManager {

    PerspectiveDefinition getPerspective();

    void setPerspective( final PerspectiveDefinition perspective );

    PanelDefinition getRoot();

    void setRoot( final PanelDefinition panel );

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

    PerspectiveActivity getDefaultPerspectiveActivity();

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

    WorkbenchPanelView getPanelView( final PanelDefinition panel );

}
