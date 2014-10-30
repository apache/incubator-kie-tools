package org.kie.workbench.common.widgets.client.menu;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.widgets.client.resources.i18n.ToolsMenuConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

@ApplicationScoped
public class RepositoryMenu {

    @Inject
    private PlaceManager placeManager;

    @Inject
    protected Caller<KieProjectService> projectService;



    @Inject
    protected ProjectContext context;
    private MenuItem projectScreen = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.ProjectEditor() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo( "projectScreen" );
                }
            } ).endMenu().build().getItems().get( 0 );
    
    private MenuItem repositoryStructureScreen = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.RepositoryStructure() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo( "repositoryStructureScreen" );
                }
            } ).endMenu().build().getItems().get( 0 );

    private MenuItem categoriesEditor = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.CategoriesEditor() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo( "CategoryManager" );
                }
            } ).endMenu().build().getItems().get( 0 );
    
    

    public List<MenuItem> getMenuItems() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
        
        //@TODO: the idea is to remove this one when we add the option to the project explorer
        menuItems.add( projectScreen );
        
        menuItems.add( repositoryStructureScreen );
        
        
        //menuItems.add( categoriesEditor );

        return menuItems;
    }
    
    //@TODO: we need to remove these two when we remove the projectScreen from here
    public void onProjectContextChanged( @Observes final ProjectContextChangeEvent event ) {
        enableToolsMenuItems( (KieProject) event.getProject() );
    }

    private void enableToolsMenuItems( final KieProject project ) {
        final boolean enabled = ( project != null );
        projectScreen.setEnabled( enabled );
    }

}
