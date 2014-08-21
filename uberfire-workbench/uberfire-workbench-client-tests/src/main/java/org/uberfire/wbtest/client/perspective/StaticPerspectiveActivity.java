package org.uberfire.wbtest.client.perspective;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestPerspectiveActivity;
import org.uberfire.wbtest.client.resize.ResizeTestScreenActivity;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * A perspective with a root panel of type StaticWorkbenchPanelPresenter.
 */
@Dependent
@Named( "org.uberfire.wbtest.client.perspective.StaticPerspectiveActivity" )
public class StaticPerspectiveActivity extends AbstractTestPerspectiveActivity {

    @Inject
    public StaticPerspectiveActivity( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition pdef = new PerspectiveDefinitionImpl( StaticWorkbenchPanelPresenter.class.getName() );
        pdef.setName( "StaticPerspectiveActivity" );

        DefaultPlaceRequest destintationPlace = new DefaultPlaceRequest( ResizeTestScreenActivity.class.getName() );
        destintationPlace.addParameter( "debugId", "staticPerspectiveDefault" );
        pdef.getRoot().addPart( new PartDefinitionImpl( destintationPlace ) );
        return pdef;
    }
}
