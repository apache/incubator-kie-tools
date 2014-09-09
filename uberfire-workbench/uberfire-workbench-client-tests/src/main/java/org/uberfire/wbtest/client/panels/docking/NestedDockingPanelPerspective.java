package org.uberfire.wbtest.client.panels.docking;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestPerspectiveActivity;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@Dependent
@Named("org.uberfire.wbtest.client.panels.docking.NestedDockingPanelPerspective")
public class NestedDockingPanelPerspective extends AbstractTestPerspectiveActivity {

    @Inject
    public NestedDockingPanelPerspective( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition pd = new PerspectiveDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        pd.getRoot().setElementId( "root" );
        pd.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( NestingScreen.class.getName() ) ) );
        return pd;
    }

}
