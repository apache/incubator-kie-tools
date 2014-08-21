package org.uberfire.wbtest.client.perspective;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestPerspectiveActivity;
import org.uberfire.wbtest.client.resize.ResizeTestScreenActivity;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * A perspective with a root panel of type MultiTabWorkbenchPanelPresenter.
 */
@Dependent
@Named( "org.uberfire.wbtest.client.perspective.TabbedPerspectiveActivity" )
public class TabbedPerspectiveActivity extends AbstractTestPerspectiveActivity {

    @Inject
    public TabbedPerspectiveActivity( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition pdef = new PerspectiveDefinitionImpl( MultiTabWorkbenchPanelPresenter.class.getName() );
        pdef.setName( "TabbedPerspectiveActivity" );

        DefaultPlaceRequest destintationPlace = new DefaultPlaceRequest( ResizeTestScreenActivity.class.getName() );
        destintationPlace.addParameter( "debugId", "tabbedPerspectiveDefault" );
        pdef.getRoot().addPart( new PartDefinitionImpl( destintationPlace ) );
        return pdef;
    }
}
