package org.kie.workbench.common.widgets.client.menu;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.context.Project;
import org.kie.workbench.common.widgets.client.resources.i18n.ToolsMenuConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.PathChangeEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

@ApplicationScoped
public class ToolsMenu {

    @Inject
    private PlaceManager placeManager;

    @Inject
    protected Caller<ProjectService> projectService;

    private MenuItem projectScreen = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.ProjectEditor() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo( "projectScreen" );
                }
            } ).endMenu().build().getItems().get( 0 );

    private MenuItem dataModelerScreen = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.DataModeller() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo( "dataModelerScreen" );
                }
            } ).endMenu().build().getItems().get( 0 );

    public List<MenuItem> getMenuItems() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

        menuItems.add( projectScreen );
        menuItems.add( dataModelerScreen );

        return menuItems;
    }

    public void selectedPathChanged( @Observes final PathChangeEvent event ) {
        projectService.call( new RemoteCallback<Project>() {
            @Override
            public void callback( final Project project ) {
                projectScreen.setEnabled( project != null );
                dataModelerScreen.setEnabled( project != null );
            }
        } ).resolveProject( event.getPath() );
    }
}
