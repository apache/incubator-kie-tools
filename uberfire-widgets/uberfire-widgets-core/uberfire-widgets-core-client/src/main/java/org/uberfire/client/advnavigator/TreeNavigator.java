package org.uberfire.client.advnavigator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.resources.NavigatorResources;
import org.uberfire.client.tree.Tree;
import org.uberfire.client.tree.TreeItem;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.navigator.DataContent;
import org.uberfire.navigator.FileNavigatorService;
import org.uberfire.navigator.NavigatorContent;
import org.uberfire.workbench.type.DotResourceTypeDefinition;

import static org.kie.commons.validation.PortablePreconditions.*;

@Dependent
@Named("TreeNav")
public class TreeNavigator extends Composite implements Navigator {

    private static final String LAZY_LOAD = "Loading...";

    @Inject
    private DotResourceTypeDefinition hiddenTypeDef;

    @Inject
    private Caller<FileNavigatorService> navigatorService;

    private NavigatorOptions options = NavigatorOptions.DEFAULT;

    private final Tree tree = new Tree() {{
        addStyleName( NavigatorResources.INSTANCE.css().treeNav() );
    }};
    private ParameterizedCommand<Path> fileActionCommand = null;

    @PostConstruct
    public void init() {
        initWidget( tree );

        tree.addOpenHandler( new OpenHandler<TreeItem>() {
            @Override
            public void onOpen( final OpenEvent<TreeItem> event ) {
                if ( needsLoading( event.getTarget() ) && event.getTarget().getUserObject() instanceof Path ) {
                    loadContent( new TreeNavigatorItemImpl( event.getTarget() ), (Path) event.getTarget().getUserObject() );
                }
            }
        } );

        tree.addSelectionHandler( new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection( SelectionEvent<TreeItem> event ) {
                if ( fileActionCommand != null ) {
                    final Object userObject = event.getSelectedItem().getUserObject();
                    if ( userObject != null && userObject instanceof Path ) {
                        fileActionCommand.execute( (Path) userObject );
                    }
                }
            }
        } );
    }

    private void loadContent( final NavigatorItem parent,
                              final Path path ) {
        if ( path != null ) {
            navigatorService.call( new RemoteCallback<NavigatorContent>() {
                @Override
                public void callback( final NavigatorContent response ) {
                    for ( final DataContent dataContent : response.getContent() ) {
                        if ( dataContent.isDirectory() ) {
                            if ( options.showDirectories() ) {
                                parent.addDirectory( dataContent.getPath() );
                            }
                        } else {
                            if ( options.showFiles() ) {
                                if ( !options.showHiddenFiles() && !hiddenTypeDef.accept( dataContent.getPath() ) ) {
                                    parent.addFile( dataContent.getPath() );
                                } else if ( options.showHiddenFiles() ) {
                                    parent.addFile( dataContent.getPath() );
                                }
                            }
                        }
                    }
                }
            } ).listContent( path );
        }
    }

    @Override
    public void loadContent( final Path path ) {
        final NavigatorItem parent = new TreeNavigatorItemImpl( new TreeItem( TreeItem.Type.FOLDER, path.getFileName() ) );
        tree.addItem( ( (TreeNavigatorItemImpl) parent ).parent );

        loadContent( parent, path );
    }

    private boolean needsLoading( final TreeItem item ) {
        return item.getChildCount() == 1 && LAZY_LOAD.equals( item.getChild( 0 ).getText() );
    }

    private class TreeNavigatorItemImpl implements NavigatorItem {

        private final TreeItem parent;

        TreeNavigatorItemImpl( final TreeItem treeItem ) {
            this.parent = checkNotNull( "parent", treeItem );
        }

        public void addDirectory( final Path child ) {
            checkCleanupLoading();

            //Util.getHeaderSafeHtml( images.openedFolder(), child.getFileName() )
            final TreeItem newDirectory = parent.addItem( TreeItem.Type.FOLDER, child.getFileName() );
            newDirectory.addItem( TreeItem.Type.LOADING, LAZY_LOAD );
            newDirectory.setUserObject( child );
        }

        public void addFile( final Path child ) {
            checkCleanupLoading();

            //Util.getHeaderSafeHtml( images.file(), child.getFileName() )
            final TreeItem newFile = parent.addItem( TreeItem.Type.ITEM, child.getFileName() );
            newFile.setUserObject( child );
        }

        private void checkCleanupLoading() {
            if ( parent.getChild( 0 ) != null && parent.getChild( 0 ).getUserObject() == null ) {
                parent.getChild( 0 ).remove();
            }
        }

    }
}