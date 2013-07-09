package org.uberfire.client.workbench;

import com.google.gwt.user.client.ui.IsWidget;
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
                           final String title,
                           final IsWidget titleDecoration,
                           final IsWidget partWidget );

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

    void onBeforePartClose( final PartDefinition part );

    WorkbenchPanelView getPanelView( final PanelDefinition panel );
}
