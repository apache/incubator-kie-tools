package org.kie.workbench.common.screens.projecteditor.client.menu;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.shared.file.CopyService;
import org.guvnor.common.services.shared.file.DeleteService;
import org.guvnor.common.services.shared.file.RenameService;
import org.guvnor.structure.client.file.*;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.widgets.client.resources.i18n.ToolsMenuConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

@ApplicationScoped
public class ProjectMenu {

    @Inject
    private PlaceManager placeManager;

    @Inject
    protected Caller<KieProjectService> projectService;

    @Inject
    protected Caller<RenameService> renameService;

    @Inject
    protected Caller<DeleteService> deleteService;

    @Inject
    protected Caller<CopyService> copyService;

    @Inject
    protected ProjectContext context;

    @Inject
    protected ProjectNameValidator projectNameValidator;

    private MenuItem projectScreen = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.ProjectEditor() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo( "projectScreen" );
                }
            } ).endMenu().build().getItems().get( 0 );
    
    private MenuItem projectStructureScreen = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.RepositoryStructure() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo( "repositoryStructureScreen" );
                }
            } ).endMenu().build().getItems().get( 0 );

    private MenuItem copyProject = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.CopyProject() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    final Path path = context.getActiveProject().getRootPath();
                    new CopyPopup( path,
                                   projectNameValidator,
                                   new CommandWithFileNameAndCommitMessage() {
                                       @Override
                                       public void execute( FileNameAndCommitMessage payload ) {
                                           copyService.call().copy( path,
                                                                    payload.getNewFileName(),
                                                                    payload.getCommitMessage() );
                                       }
                                   } ).show();
                }
            } ).endMenu().build().getItems().get( 0 );

    private MenuItem renameProject = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.RenameProject() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    final Path path = context.getActiveProject().getRootPath();
                    new RenamePopup( path,
                                     projectNameValidator,
                                     new CommandWithFileNameAndCommitMessage() {
                                         @Override
                                         public void execute( FileNameAndCommitMessage payload ) {
                                             renameService.call().rename( path,
                                                                          payload.getNewFileName(),
                                                                          payload.getCommitMessage() );
                                         }
                                     } ).show();

                }
            } ).endMenu().build().getItems().get( 0 );

    private MenuItem removeProject = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.RemoveProject() ).respondsWith(
            new Command() {
                @Override
                public void execute() {

                    new DeletePopup( new CommandWithCommitMessage() {
                        @Override
                        public void execute( String payload ) {
                            deleteService.call().delete(
                                    context.getActiveProject().getRootPath(),
                                    payload );
                        }
                    } ).show();

                }
            } ).endMenu().build().getItems().get( 0 );

    public List<MenuItem> getMenuItems() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

        menuItems.add( projectScreen );
        
        menuItems.add( projectStructureScreen );

//        menuItems.add(removeProject);
//        menuItems.add(renameProject);
//        menuItems.add(copyProject);

        return menuItems;
    }

    public void onProjectContextChanged( @Observes final ProjectContextChangeEvent event ) {
        enableToolsMenuItems( (KieProject) event.getProject() );
    }

    private void enableToolsMenuItems( final KieProject project ) {
        final boolean enabled = ( project != null );
        projectScreen.setEnabled( enabled );
    }

}
