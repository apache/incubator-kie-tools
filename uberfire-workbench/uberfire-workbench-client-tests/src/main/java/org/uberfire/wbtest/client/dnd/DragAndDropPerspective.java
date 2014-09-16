package org.uberfire.wbtest.client.dnd;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleDnDWorkbenchPanelPresenter;
import org.uberfire.wbtest.client.api.AbstractTestPerspectiveActivity;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@Dependent
@Named("org.uberfire.wbtest.client.dnd.DragAndDropPerspective")
public class DragAndDropPerspective extends AbstractTestPerspectiveActivity {

    @Inject
    public DragAndDropPerspective( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition pd = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        pd.getRoot().setElementId( "DragAndDropPerspective-list" );
        pd.getRoot().addPart( DragAndDropScreen.class.getName() + "?debugId=1" );
        pd.getRoot().addPart( DragAndDropScreen.class.getName() + "?debugId=2" );

        PanelDefinitionImpl tabPanel = new PanelDefinitionImpl( MultiTabWorkbenchPanelPresenter.class.getName() );
        tabPanel.setElementId( "DragAndDropPerspective-tab" );
        tabPanel.setWidth( 300 );
        tabPanel.addPart( DragAndDropScreen.class.getName() + "?debugId=3" );
        tabPanel.addPart( DragAndDropScreen.class.getName() + "?debugId=4" );
        pd.getRoot().appendChild( CompassPosition.WEST, tabPanel );

        PanelDefinitionImpl simplePanel = new PanelDefinitionImpl( SimpleDnDWorkbenchPanelPresenter.class.getName() );
        simplePanel.setWidth( 175 );
        simplePanel.setElementId( "DragAndDropPerspective-simple" );
        simplePanel.addPart( DragAndDropScreen.class.getName() + "?debugId=5" );
        pd.getRoot().appendChild( CompassPosition.EAST, simplePanel );

        return pd;
    }

}
