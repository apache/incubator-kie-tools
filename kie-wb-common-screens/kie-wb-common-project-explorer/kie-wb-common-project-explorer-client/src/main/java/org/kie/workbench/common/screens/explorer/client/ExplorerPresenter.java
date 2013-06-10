package org.kie.workbench.common.screens.explorer.client;

import java.util.Collection;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.project.service.model.Package;
import org.kie.workbench.common.services.project.service.model.Project;
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
import org.uberfire.workbench.events.PathChangeEvent;
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
    private Event<PathChangeEvent> pathChangeEvent;

    @Inject
    private WorkbenchContext context;

    @Inject
    private ExplorerView view;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    private Path activePath;

    @OnStart
    public void onStart() {
        load();
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
                view.setGroups( groups );
            }
        } ).getGroups();
    }

    public void groupSelected( final Group group ) {
        explorerService.call( new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback( final Collection<Repository> repositories ) {
                view.setRepositories( repositories );
            }
        } ).getRepositories( group );
    }

    public void repositorySelected( final Repository repository ) {
        explorerService.call( new RemoteCallback<Collection<Project>>() {
            @Override
            public void callback( final Collection<Project> projects ) {
                view.setProjects( projects );
            }
        } ).getProjects( repository );
    }

    public void projectSelected( final Project project ) {
        explorerService.call( new RemoteCallback<Collection<Package>>() {
            @Override
            public void callback( final Collection<Package> packages ) {
                view.setPackages( packages );
            }
        } ).getPackages( project );
    }

    public void packageSelected( final Package pkg ) {
        //TODO Show content of package
    }

    public void openResource( final Path path ) {
        placeManager.goTo( path );
    }

    public void pathChangeHandler( @Observes PathChangeEvent event ) {
        final Path path = event.getPath();
    }

    // Refresh when a Resource has been added
    public void onResourceAdded( @Observes final ResourceAddedEvent event ) {
        activePath = null;
        //TODO Refresh only if required
        //loadItems( context.getActivePath() );
    }

    // Refresh when a Resource has been deleted
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        activePath = null;
        //TODO Refresh only if required
        //loadItems( context.getActivePath() );
    }

    // Refresh when a Resource has been copied
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        activePath = null;
        //TODO Refresh only if required
        //loadItems( context.getActivePath() );
    }

    // Refresh when a Resource has been renamed
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        activePath = null;
        //TODO Refresh only if required
        //loadItems( context.getActivePath() );
    }

    // Refresh when a batch Resource change has occurred
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        activePath = null;
        //TODO Refresh only if required
        //loadItems( context.getActivePath() );
    }

    private class RootItemsSuccessCallback implements RemoteCallback<Collection<Repository>> {

        protected final Path path;

        private RootItemsSuccessCallback( final Path path ) {
            this.path = path;
        }

        @Override
        public void callback( final Collection<Repository> repositories ) {
        }

    }

}
