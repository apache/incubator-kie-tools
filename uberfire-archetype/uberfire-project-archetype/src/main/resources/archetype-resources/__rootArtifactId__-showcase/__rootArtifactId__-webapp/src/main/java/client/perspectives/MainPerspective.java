#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;

@ApplicationScoped
@WorkbenchPerspective(identifier = "MainPerspective", isDefault = true)
public class MainPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( StaticWorkbenchPanelPresenter.class.getName() );
        p.setName( "MainPerspective" );

        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "HelloWorldScreen" ) ) );
//        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "ComponentPresenter" ) ) );

        return p;
    }
}
