package org.kie.workbench.screens.explorer.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.callbacks.DefaultErrorCallback;
import org.kie.guvnor.commons.ui.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.commons.ui.client.widget.BusyIndicatorView;
import org.kie.workbench.screens.explorer.client.resources.i18n.Constants;
import org.kie.workbench.screens.explorer.model.ExplorerContent;
import org.kie.workbench.screens.explorer.model.Item;
import org.kie.workbench.screens.explorer.model.RepositoryItem;
import org.kie.workbench.screens.explorer.service.ExplorerService;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.context.WorkbenchContext;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.PathChangeEvent;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceBatchChangesEvent;
import org.uberfire.client.workbench.widgets.events.ResourceCopiedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceDeletedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceRenamedEvent;

/**
 * Repository, Package, Folder and File explorer
 */
@WorkbenchScreen(identifier = "org.kie.guvnor.explorer")
public class ExplorerPresenter {

    @Inject
    private Caller<FileExplorerRootService> rootService;

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
            rootService.call( new RootItemsSuccessCallback( path ),
                              new DefaultErrorCallback() ).listRoots();
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
            rootService.call( new RootItemsSuccessCallbackWithBusyIndicator( path ),
                              new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).listRoots();
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

    private class RootItemsSuccessCallback implements RemoteCallback<Collection<Root>> {

        protected final Path path;

        private RootItemsSuccessCallback( final Path path ) {
            this.path = path;
        }

        @Override
        public void callback( final Collection<Root> roots ) {
            final List<Item> items = new ArrayList<Item>();
            for ( final Root root : roots ) {
                items.add( wrapRoot( root ) );
            }
            final ExplorerContent content = new ExplorerContent( items );
            activePath = path;
            view.setContent( content );
        }

        private RepositoryItem wrapRoot( final Root root ) {
            final RepositoryItem repositoryItem = new RepositoryItem( root.getPath() );
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
        public void callback( final Collection<Root> roots ) {
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
