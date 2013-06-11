package org.kie.workbench.common.screens.explorer.client;

import java.util.Collection;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.screens.explorer.model.Package;
import org.kie.workbench.common.screens.explorer.model.Project;
import org.kie.workbench.common.screens.explorer.model.ProjectPackage;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.context.WorkbenchContext;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.GroupChangeEvent;
import org.uberfire.workbench.events.PathChangeEvent;
import org.uberfire.workbench.events.RepositoryChangeEvent;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;

/**
 * Repository, Package, Folder and File explorer
 */
@WorkbenchScreen(identifier = "org.kie.guvnor.explorer")
public class ExplorerPresenter {

    @Inject
    private Caller<ExplorerService> explorerService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<GroupChangeEvent> groupChangeEvent;

    @Inject
    private Event<RepositoryChangeEvent> repositoryChangeEvent;

    @Inject
    private Event<PathChangeEvent> pathChangeEvent;

    @Inject
    private WorkbenchContext context;

    @Inject
    private ExplorerView view;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    private Group activeGroup;
    private Repository activeRepository;
    private Project activeProject;
    private Package activePackage;

    @OnStart
    public void onStart() {
        activeGroup = context.getActiveGroup();
        activeRepository = context.getActiveRepository();
        explorerService.call( new RemoteCallback<ProjectPackage>() {
            @Override
            public void callback( final ProjectPackage projectPackage ) {
                activeProject = projectPackage.getProject();
                activePackage = projectPackage.getPackage();
                load();
            }
        } ).resolveProjectPackage( context.getActivePath() );
    }

    @WorkbenchPartView
    public UberView<ExplorerPresenter> getView() {
        return this.view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.explorerTitle();
    }

    private void load() {
        explorerService.call( new RemoteCallback<Collection<Group>>() {
            @Override
            public void callback( final Collection<Group> groups ) {
                view.setGroups( groups,
                                activeGroup );
            }

        } ).getGroups();
    }

    public void groupSelected( final Group group ) {
        if ( !group.equals( activeGroup ) ) {
            groupChangeEvent.fire( new GroupChangeEvent( group ) );
        } else {
            groupChangeHandler( group );
        }
    }

    public void groupChangeHandler( final @Observes GroupChangeEvent event ) {
        final Group group = event.getGroup();
        activeGroup = group;
        groupChangeHandler( group );
    }

    private void groupChangeHandler( final Group group ) {
        if ( group == null ) {
            return;
        }
        explorerService.call( new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback( final Collection<Repository> repositories ) {
                view.setRepositories( repositories,
                                      activeRepository );
            }

        } ).getRepositories( group );
    }

    public void repositorySelected( final Repository repository ) {
        if ( !repository.equals( activeRepository ) ) {
            repositoryChangeEvent.fire( new RepositoryChangeEvent( repository ) );
        } else {
            repositoryChangeHandler( repository );
        }
    }

    public void repositoryChangeHandler( final @Observes RepositoryChangeEvent event ) {
        final Repository repository = event.getRepository();
        activeRepository = repository;
        repositoryChangeHandler( repository );
    }

    private void repositoryChangeHandler( final Repository repository ) {
        if ( repository == null ) {
            return;
        }
        explorerService.call( new RemoteCallback<Collection<Project>>() {
            @Override
            public void callback( final Collection<Project> projects ) {
                view.setProjects( projects,
                                  activeProject );
            }

        } ).getProjects( repository );
    }

    public void projectSelected( final Project project ) {
        activeProject = project;
        pathChangeEvent.fire( new PathChangeEvent( project.getPath() ) );
        explorerService.call( new RemoteCallback<Collection<Package>>() {
            @Override
            public void callback( final Collection<Package> packages ) {
                view.setPackages( packages,
                                  activePackage );
            }

        } ).getPackages( project );
    }

    public void packageSelected( final Package pkg ) {
        activePackage = pkg;
        explorerService.call( new RemoteCallback<Collection<Item>>() {
            @Override
            public void callback( final Collection<Item> items ) {
                view.setItems( items );
            }
        } ).getItems( pkg );
    }

    public void itemSelected( final Item item ) {
        final Path path = item.getPath();
        if ( path == null ) {
            return;
        }
        pathChangeEvent.fire( new PathChangeEvent( path ) );
        placeManager.goTo( item.getPath() );
    }

    // Refresh when a Resource has been added
    public void onResourceAdded( @Observes final ResourceAddedEvent event ) {
        //TODO Refresh only if required
        //loadItems( context.getActivePath() );
    }

    // Refresh when a Resource has been deleted
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        //TODO Refresh only if required
        //loadItems( context.getActivePath() );
    }

    // Refresh when a Resource has been copied
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        //TODO Refresh only if required
        //loadItems( context.getActivePath() );
    }

    // Refresh when a Resource has been renamed
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        //TODO Refresh only if required
        //loadItems( context.getActivePath() );
    }

    // Refresh when a batch Resource change has occurred
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        //TODO Refresh only if required
        //loadItems( context.getActivePath() );
    }

}
