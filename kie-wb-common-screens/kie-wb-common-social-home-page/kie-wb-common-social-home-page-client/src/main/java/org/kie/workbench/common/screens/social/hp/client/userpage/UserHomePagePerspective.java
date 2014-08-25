package org.kie.workbench.common.screens.social.hp.client.userpage;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = "UserHomePagePerspective")
public class UserHomePagePerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( PanelType.ROOT_LIST );
        p.setTransient( true );
        p.setName( "People Perspective" );
        final PanelDefinition west = new PanelDefinitionImpl( PanelType.SIMPLE );
        west.setWidth( 400 );
        west.setMinWidth( 350 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "UserHomePageSidePresenter" ) ) );
        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "UserHomePageMainPresenter" ) ) );
        p.getRoot().insertChild( Position.WEST,
                                 west );
        return p;
    }
}

