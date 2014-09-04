package org.uberfire.wbtest.client.main;

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

/**
 * The starting perspective. Displays a screen with a well-known debug ID so tests can detect when the workbench is done
 * bootstrapping.
 */
@Dependent
@Named( "org.uberfire.wbtest.client.main.DefaultPerspectiveActivity" )
public class DefaultPerspectiveActivity extends AbstractTestPerspectiveActivity {

    @Inject
    public DefaultPerspectiveActivity( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition pdef = new PerspectiveDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        pdef.setName( "DefaultPerspectiveActivity" );

        DefaultPlaceRequest destintationPlace = new DefaultPlaceRequest( DefaultScreenActivity.class.getName() );
        pdef.getRoot().addPart( new PartDefinitionImpl( destintationPlace ) );

        return pdef;
    }

    @Override
    public boolean isDefault() {
        return true;
    }
}
