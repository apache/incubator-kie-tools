package org.uberfire.wbtest.client.resize;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.wbtest.client.api.AbstractTestPerspectiveActivity;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * A perspective with a root panel of type StaticWorkbenchPanelPresenter.
 */
@Dependent
@Named( "org.uberfire.wbtest.client.resize.OverflowStaticPerspective" )
public class OverflowStaticPerspective extends AbstractTestPerspectiveActivity {

    @Inject
    public OverflowStaticPerspective( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition pdef = new PerspectiveDefinitionImpl( StaticWorkbenchPanelPresenter.class.getName() );
        pdef.setName( "OverflowStaticPerspective" );
        pdef.getRoot().addPart( OverflowTestScreen.class.getName() );
        return pdef;
    }
}
