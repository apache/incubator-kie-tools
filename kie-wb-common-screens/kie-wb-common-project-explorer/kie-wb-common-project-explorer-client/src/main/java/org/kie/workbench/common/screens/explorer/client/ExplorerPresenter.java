package org.kie.workbench.common.screens.explorer.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.explorer.model.ExplorerContent;
import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.screens.explorer.model.RepositoryItem;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.widgets.client.callbacks.DefaultErrorCallback;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
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
    private Caller<RepositoryService> repositoryService;

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
        final Path p = context.getActivePath();
        loadItems( p );
    }

    @WorkbenchPartView
    public UberView<ExplorerPresenter> getView() {
        return this.view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.explorerTitle();
    }

    public void pathChangeHandler( @Observes PathChangeEvent event ) {
        final Path path = event.getPath();
        loadItemsWithBusyIndicator( path );
    }

    public void openResource( final Path path ) {
        placeManager.goTo( path );
    }

    public void setContext( final Path path ) {
        pathChangeEvent.fire( new PathChangeEvent( path ) );
    }

    private void loadItems( final Path path ) {
        if ( path == null ) {
            repositoryService.call( new RootItemsSuccessCallback( path ),
                                    new DefaultErrorCallback() ).getRepositories();
        } else {
            if ( !path.equals( activePath ) ) {
                explorerService.call( new ContentInScopeSuccessCallback( path ),
                                      new DefaultErrorCallback() ).getContentInScope( path );
            }
        }
    }

    private void loadItemsWithBusyIndicator( final Path path ) {
        if ( path == null ) {
            busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            repositoryService.call( new RootItemsSuccessCallbackWithBusyIndicator( path ),
                                    new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).getRepositories();
        } else {
            if ( !path.equals( activePath ) ) {
                busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
                explorerService.call( new ContentInScopeSuccessCallbackWithBusyIndicator( path ),
                                      new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).getContentInScope( path );
            }
        }
    }

    // Refresh when a Resource has been added
    public void onResourceAdded( @Observes final ResourceAddedEvent event ) {
        activePath = null;
        //TODO Refresh only if required
        loadItems( context.getActivePath() );
    }

    // Refresh when a Resource has been deleted
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        activePath = null;
        //TODO Refresh only if required
        loadItems( context.getActivePath() );
    }

    // Refresh when a Resource has been copied
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        activePath = null;
        //TODO Refresh only if required
        loadItems( context.getActivePath() );
    }

    // Refresh when a Resource has been renamed
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        activePath = null;
        //TODO Refresh only if required
        loadItems( context.getActivePath() );
    }

    // Refresh when a batch Resource change has occurred
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        activePath = null;
        //TODO Refresh only if required
        loadItems( context.getActivePath() );
    }

    private class RootItemsSuccessCallback implements RemoteCallback<Collection<Repository>> {

        protected final Path path;

        private RootItemsSuccessCallback( final Path path ) {
            this.path = path;
        }

        @Override
        public void callback( final Collection<Repository> repositories ) {
            final List<Item> items = new ArrayList<Item>();
            for ( final Repository repository : repositories ) {
                items.add( wrapRepository( repository ) );
            }
            final ExplorerContent content = new ExplorerContent( items );
            activePath = path;
            view.setContent( content );
        }

        private RepositoryItem wrapRepository( final Repository repository ) {
            final RepositoryItem repositoryItem = new RepositoryItem( repository.getRoot() );
            return repositoryItem;
        }

    }

    private class ContentInScopeSuccessCallback implements RemoteCallback<ExplorerContent> {

        protected final Path path;

        private ContentInScopeSuccessCallback( final Path path ) {
            this.path = path;
        }

        @Override
        public void callback( final ExplorerContent content ) {
            activePath = path;
            view.setContent( content );
        }

    }

    private class RootItemsSuccessCallbackWithBusyIndicator extends RootItemsSuccessCallback {

        private RootItemsSuccessCallbackWithBusyIndicator( final Path path ) {
            super( path );
        }

        @Override
        public void callback( final Collection<Repository> roots ) {
            super.callback( roots );
            busyIndicatorView.hideBusyIndicator();
        }

    }

    private class ContentInScopeSuccessCallbackWithBusyIndicator extends ContentInScopeSuccessCallback {

        private ContentInScopeSuccessCallbackWithBusyIndicator( final Path path ) {
            super( path );
        }

        @Override
        public void callback( final ExplorerContent content ) {
            super.callback( content );
            busyIndicatorView.hideBusyIndicator();
        }

    }

}
