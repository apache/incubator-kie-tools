package org.kie.workbench.common.screens.server.management.client;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = "ServerManagementPerspective")
public class ServerManagementPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        perspective.setName( "ServerManagementPerspective" );

        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "ServerManagementBrowser" ) ) );

        return perspective;
    }

}
