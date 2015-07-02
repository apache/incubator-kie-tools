package org.uberfire.ext.apps.client;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = "AppsPerspective")
public class AppsPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( "org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter" );
        p.setName( "Apps Perspective" );
        p.getRoot().addPart(
                new PartDefinitionImpl(
                        new DefaultPlaceRequest( "AppsHomePresenter" ) ) );

        return p;
    }
}