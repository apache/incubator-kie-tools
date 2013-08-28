/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client.widget;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.guvnor.common.services.shared.metadata.CategoriesService;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.guvnor.common.services.shared.metadata.model.CategoryItem;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.kie.workbench.common.widgets.metadata.client.resources.ImageResources;
import org.kie.workbench.common.widgets.metadata.client.resources.i18n.MetadataConstants;
import org.uberfire.backend.vfs.Path;

import static org.kie.commons.validation.PortablePreconditions.*;

/**
 * This is a rule/resource navigator that uses the server side categories to
 * navigate the repository. Uses the the
 * {@link com.google.gwt.user.client.ui.Tree} widget.
 */
public class CategoryExplorerWidget
        extends Composite
        implements SelectionHandler<TreeItem> {

    private final Tree navTreeWidget = new Tree();
    private final VerticalPanel panel = new VerticalPanel();
    private final Path resource;
    private final CategorySelectHandler categorySelectHandler;

    private Panel emptyCategories = null;

    private String selectedPath;

    /**
     * Create a new cat explorer.
     * @param resource active resource path, based on it will display related categories
     * @param handler category select handler
     */
    public CategoryExplorerWidget( final Path resource,
                                   final CategorySelectHandler handler ) {
        this.resource = checkNotNull( "resource", resource );
        this.categorySelectHandler = handler;

        panel.add( navTreeWidget );

        loadInitialTree();

        initWidget( panel );
        navTreeWidget.addSelectionHandler( this );
        this.setStyleName( "category-explorer-Tree" );
    }

    /**
     * This refreshes the view.
     */
    public void refresh() {
        navTreeWidget.removeItems();
        selectedPath = null;
        loadInitialTree();
    }

    public void showEmptyTree() {
        if ( this.emptyCategories == null ) {
            final AbsolutePanel p = new AbsolutePanel();
            p.add( new HTML( MetadataConstants.INSTANCE.NoCategoriesCreatedYetTip() ) );
            final Button b = new Button( MetadataConstants.INSTANCE.Refresh() );
            b.addClickHandler( new ClickHandler() {
                public void onClick( final ClickEvent event ) {
                    refresh();
                }
            } );
            p.add( b );
            p.setStyleName( "small-Text" );
            this.emptyCategories = p;
            this.panel.add( this.emptyCategories );
        }

        this.navTreeWidget.setVisible( false );
        emptyCategories.setVisible( true );
    }

    /**
     * This will refresh the tree and restore it back to the original state
     */
    private void loadInitialTree() {
        navTreeWidget.addItem( MetadataConstants.INSTANCE.PleaseWait() );
        Scheduler scheduler = Scheduler.get();
        scheduler.scheduleDeferred( new Command() {
            public void execute() {
                MessageBuilder.createCall( new RemoteCallback<Categories>() {
                    public void callback( final Categories categories ) {
                        selectedPath = null;
                        navTreeWidget.removeItems();

                        TreeItem root = new TreeItem();
                        root.setHTML( AbstractImagePrototype.create( ImageResources.INSTANCE.desc() ).getHTML() );
                        navTreeWidget.addItem( root );

                        if ( categories.size() == 0 ) {
                            showEmptyTree();
                        } else {
                            hideEmptyTree();
                        }
                        for ( final CategoryItem category : categories ) {
                            final TreeItem it = buildTreeItem( category );
                            root.addItem( it );
                            if ( category.hasChild() ) {
                                loadChildren( it, category );
                            }
                        }

                        root.setState( true );
                    }
                }, CategoriesService.class ).getCategoriesFromResource( resource );
            }
        } );

    }

    protected void loadChildren( final TreeItem it,
                                 final CategoryItem category ) {
        for ( final CategoryItem child : category.getChildren() ) {
            final TreeItem ct = buildTreeItem( child );

            it.addItem( ct );

            if ( child.hasChild() ) {
                loadChildren( ct, child );
            }
        }
    }

    public TreeItem buildTreeItem( final CategoryItem category ) {
        final TreeItem it = new TreeItem();
        it.setHTML( AbstractImagePrototype.create( ImageResources.INSTANCE.categorySmall() ).getHTML() + h( category.getName() ) );
        it.setUserObject( category.getFullPath() );

        return it;
    }

    private String h( String cat ) {
        return cat.replace( "<", "&lt;" ).replace( ">", "&gt;" );
    }

    private void hideEmptyTree() {
        if ( this.emptyCategories != null ) {
            this.emptyCategories.setVisible( false );
        }
        this.navTreeWidget.setVisible( true );
    }

    public void onSelection( final SelectionEvent<TreeItem> event ) {
        this.selectedPath = getPath( event.getSelectedItem() );
        this.categorySelectHandler.selected( selectedPath );
    }

    private String getPath( final TreeItem item ) {
        return (String) item.getUserObject();
    }
}