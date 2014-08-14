package org.kie.workbench.common.screens.social.hp.client.userpage;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.screens.social.hp.client.resources.i18n.Constants;
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
@WorkbenchPerspective(identifier = "UserHomePagePerspective")
public class UserHomePagePerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        p.setName(Constants.INSTANCE.PeoplePerspective() );
        final PanelDefinition west = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        west.setWidth( 350 );
        west.setMinWidth( 350 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "UserHomePageSidePresenter" ) ) );
        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "UserHomePageMainPresenter" ) ) );
        p.getRoot().insertChild( CompassPosition.WEST,
                                 west );
        return p;
    }
}

