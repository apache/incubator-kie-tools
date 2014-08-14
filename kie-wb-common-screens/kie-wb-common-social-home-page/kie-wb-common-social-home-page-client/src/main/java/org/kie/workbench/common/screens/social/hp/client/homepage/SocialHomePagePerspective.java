package org.kie.workbench.common.screens.social.hp.client.homepage;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = "SocialHomePagePerspective")
public class SocialHomePagePerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        p.setName( "Social Home Page Perspective" );
        final PanelDefinition west = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        west.setWidth( 450 );
        west.setMinWidth( 450 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "SocialHomePageSidePresenter" ) ) );
        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "SocialHomePageMainPresenter" ) ) );
        p.getRoot().insertChild( CompassPosition.WEST,
                                                west );
        return p;
    }
}

