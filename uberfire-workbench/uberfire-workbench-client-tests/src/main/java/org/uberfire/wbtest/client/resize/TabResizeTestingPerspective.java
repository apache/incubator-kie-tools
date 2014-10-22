package org.uberfire.wbtest.client.resize;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.wbtest.client.api.AbstractTestPerspectiveActivity;
import org.uberfire.wbtest.client.panels.docking.NestingScreen;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * A perspective with a root panel of type StaticWorkbenchPanelPresenter.
 */
@Dependent
@Named( "org.uberfire.wbtest.client.resize.TabResizeTestingPerspective" )
public class TabResizeTestingPerspective extends AbstractTestPerspectiveActivity {

    @Inject
    public TabResizeTestingPerspective( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition pdef = new PerspectiveDefinitionImpl( MultiTabWorkbenchPanelPresenter.class.getName() );
        pdef.setName( "TabResizeTestingPerspective" );
        pdef.getRoot().addPart( NestingScreen.class.getName() + "?place=One" );
        pdef.getRoot().addPart( NestingScreen.class.getName() + "?place=Two" );
        pdef.getRoot().addPart( NestingScreen.class.getName() + "?place=Three" );
        pdef.getRoot().addPart( NestingScreen.class.getName() + "?place=Four" );
        pdef.getRoot().addPart( NestingScreen.class.getName() + "?place=Five" );
        pdef.getRoot().addPart( NestingScreen.class.getName() + "?place=Six" );
        pdef.getRoot().addPart( NestingScreen.class.getName() + "?place=Seven" );
        pdef.getRoot().addPart( NestingScreen.class.getName() + "?place=Eight" );
        return pdef;
    }
}
