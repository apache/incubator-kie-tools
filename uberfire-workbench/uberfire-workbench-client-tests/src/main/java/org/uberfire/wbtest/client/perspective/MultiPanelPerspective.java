package org.uberfire.wbtest.client.perspective;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.wbtest.client.api.AbstractTestPerspectiveActivity;
import org.uberfire.wbtest.client.dnd.DragAndDropScreen;
import org.uberfire.wbtest.client.panels.docking.NestingScreen;
import org.uberfire.wbtest.client.resize.ResizeTestScreenActivity;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@Dependent
@Named("org.uberfire.wbtest.client.perspective.MultiPanelPerspective")
public class MultiPanelPerspective extends AbstractTestPerspectiveActivity {

    @Inject
    public MultiPanelPerspective( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition def = new PerspectiveDefinitionImpl( MultiTabWorkbenchPanelPresenter.class.getName() );
        def.getRoot().addPart( ResizeTestScreenActivity.class.getName() );

        PanelDefinition southPanel = new PanelDefinitionImpl( MultiTabWorkbenchPanelPresenter.class.getName() );
        southPanel.addPart( DragAndDropScreen.class.getName() );
        def.getRoot().appendChild( CompassPosition.SOUTH, southPanel );

        PanelDefinition eastPanel = new PanelDefinitionImpl( MultiTabWorkbenchPanelPresenter.class.getName() );
        eastPanel.addPart( NestingScreen.class.getName() );
        def.getRoot().appendChild( CompassPosition.EAST, eastPanel );

        return def;
    }

}
