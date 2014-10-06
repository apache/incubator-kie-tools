package org.kie.uberfire.perspective.editor.client;

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
@WorkbenchPerspective(identifier = "PerspectiveEditorPerspective", isDefault = true)
public class PerspectiveEditorPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( PanelType.ROOT_LIST );
        p.setTransient( true );
        p.setName( "Perspective Editor" );
        final PanelDefinition west = new PanelDefinitionImpl( PanelType.SIMPLE );
        west.setWidth( 250 );
        west.setMinWidth( 250 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "PerspectiveEditorSidePresenter" ) ) );
        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "PerspectiveEditorMainPresenter" ) ) );
        p.getRoot().insertChild( Position.WEST,
                                 west );
        return p;
    }
}