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

/**
 * A perspective that gets its layout state saved when leaving and restored when coming back.
 */
@Dependent
@Named("org.uberfire.wbtest.client.perspective.NonTransientMultiPanelPerspective")
public class NonTransientMultiPanelPerspective extends AbstractTestPerspectiveActivity {

    @Inject
    public NonTransientMultiPanelPerspective(PlaceManager placeManager) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition def = new PerspectiveDefinitionImpl( MultiTabWorkbenchPanelPresenter.class.getName() );
        def.getRoot().addPart( ResizeTestScreenActivity.class.getName() );

        PanelDefinition southPanel = new PanelDefinitionImpl( MultiTabWorkbenchPanelPresenter.class.getName() );
        southPanel.addPart( ResizeTestScreenActivity.class.getName() + "?debugId=south" );
        def.getRoot().appendChild( CompassPosition.SOUTH, southPanel );

        PanelDefinition westPanel = new PanelDefinitionImpl( MultiTabWorkbenchPanelPresenter.class.getName() );
        westPanel.addPart( ResizeTestScreenActivity.class.getName() + "?debugId=west" );
        westPanel.setMinWidth( 250 );
        westPanel.setWidth( 300 );
        def.getRoot().appendChild( CompassPosition.WEST, westPanel );

        return def;
    }

    @Override
    public boolean isTransient() {
        return false;
    }
    
}
