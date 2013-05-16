/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.editors.fileexplorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.Util;
import org.uberfire.client.resources.CoreImages;

import static org.kie.commons.validation.PortablePreconditions.*;

public class FileExplorerView
        extends Composite
        implements FileExplorerPresenter.View {

    TreeItem rootTreeItem = null;

    final Tree tree = new Tree();

    private static CoreImages images = GWT.create( CoreImages.class );

    private static final String REPOSITORY_ID = "repositories";
    private static final String LAZY_LOAD = "Loading...";

    private FileExplorerPresenter presenter = null;

    @Override
    public void init( final FileExplorerPresenter presenter ) {
        this.presenter = presenter;
        rootTreeItem = tree.addItem( Util.getHeaderSafeHtml( images.packageIcon(), "Repositories" ) );
        rootTreeItem.setState( true );
        initWidget( tree );

        tree.addOpenHandler( new OpenHandler<TreeItem>() {
            @Override
            public void onOpen( final OpenEvent<TreeItem> event ) {
                if ( needsLoading( event.getTarget() ) && event.getTarget().getUserObject() instanceof Path ) {
                    presenter.loadDirectoryContent( new FileExplorerItemImpl( event.getTarget() ), (Path) event.getTarget().getUserObject() );
                }
            }
        } );

        tree.addSelectionHandler( new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection( SelectionEvent<TreeItem> event ) {
                final Object userObject = event.getSelectedItem().getUserObject();
                if ( userObject != null && userObject instanceof Path ) {
                    final Path path = (Path) userObject;
                    presenter.redirect( path );
                } else if ( userObject != null && userObject instanceof Repository ) {
                    final Repository root = (Repository) userObject;
                    presenter.redirect( root );
                } else if ( event.getSelectedItem().getUserObject() instanceof String &&
                        ( event.getSelectedItem().getUserObject() ).equals( REPOSITORY_ID ) ) {
                    presenter.redirectRepositoryList();
                }
            }
        } );

    }

    @Override
    public void setFocus() {
        tree.setFocus( true );
    }

    @Override
    public void reset() {
        rootTreeItem.setUserObject( REPOSITORY_ID );
        rootTreeItem.addItem( Util.toSafeHtml( LAZY_LOAD ) );

        rootTreeItem.removeItems();
    }

    @Override
    public void removeIfExists( final Repository repo ) {
        //TODO {porcelli} implement!
    }

    @Override
    public void addNewRepository( final Repository repo ) {
        final TreeItem repositoryRootItem = rootTreeItem.addItem( Util.getHeaderSafeHtml( images.packageIcon(), repo.getAlias() ) );
        repositoryRootItem.setState( true );
        repositoryRootItem.setUserObject( repo );

        presenter.loadDirectoryContent( new FileExplorerItemImpl( repositoryRootItem ), repo.getRoot() );
    }

    private boolean needsLoading( TreeItem item ) {
        return item.getChildCount() == 1 && LAZY_LOAD.equals( item.getChild( 0 ).getText() );
    }

    private static class FileExplorerItemImpl implements FileExplorerPresenter.FileExplorerItem {

        private final TreeItem parent;

        FileExplorerItemImpl( final TreeItem treeItem ) {
            this.parent = checkNotNull( "parent", treeItem );
        }

        @Override
        public void addDirectory( final Path child ) {
            checkCleanupLoading();

            final TreeItem newDirectory = parent.addItem( Util.getHeaderSafeHtml( images.openedFolder(), child.getFileName() ) );
            newDirectory.addItem( LAZY_LOAD );
            newDirectory.setUserObject( child );
        }

        @Override
        public void addFile( final Path child ) {
            checkCleanupLoading();

            final TreeItem newFile = parent.addItem( Util.getHeaderSafeHtml( images.file(), child.getFileName() ) );
            newFile.setUserObject( child );
        }

        private void checkCleanupLoading() {
            if ( parent.getChild( 0 ) != null && parent.getChild( 0 ).getUserObject() == null ) {
                parent.getChild( 0 ).remove();
            }
        }

    }

}