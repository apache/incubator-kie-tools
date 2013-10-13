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

package org.drools.workbench.screens.categories.client.widget;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.screens.categories.client.resources.ImageResources;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.guvnor.common.services.shared.metadata.model.CategoryItem;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * This is a rule/resource navigator that uses the server side categories to
 * navigate the repository. Uses the the {@link com.google.gwt.user.client.ui.Tree} widget.
 */
public class CategoryTreeEditorWidget
        extends Composite {

    protected Tree navTreeWidget = new Tree();
    private Categories categories = null;

    /**
     * Create a new cat tree editor.
     */
    public CategoryTreeEditorWidget() {
        final VerticalPanel panel = new VerticalPanel();
        panel.add( navTreeWidget );

        initWidget( panel );

        setStyleName( "category-explorer-Tree" );
    }

    /**
     * This refreshes the view.
     */
    public void refresh() {
        navTreeWidget.removeItems();
        buildTree( categories );
    }

    protected String h( final String cat ) {
        return cat.replace( "<", "&lt;" ).replace( ">", "&gt;" );
    }

    public CategoryItem getSelectedCategory() {
        if ( navTreeWidget.getSelectedItem() == null ) {
            return null;
        }
        return ( (CategoryItem) navTreeWidget.getSelectedItem().getUserObject() );
    }

    public boolean isSelected() {
        return navTreeWidget.getSelectedItem() != null;
    }

    protected void buildTree( final Categories categories ) {
        final TreeItem root = new TreeItem();
        root.setHTML( AbstractImagePrototype.create( ImageResources.INSTANCE.desc() ).getHTML() );
        navTreeWidget.addItem( root );

        for ( final CategoryItem category : categories ) {
            final TreeItem it = buildTreeItem( category );
            root.addItem( it );

            if ( category.hasChild() ) {
                loadChildren( it, category );
            }
        }

        root.setState( true );
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
        it.setUserObject( category );

        return it;
    }

    public void setContent( final Categories categories ) {
        this.categories = checkNotNull( "categories", categories );
        refresh();
    }

    public void renameSelected( final String name ) {
        ( (CategoryItem) navTreeWidget.getSelectedItem().getUserObject() ).setName( name );
        navTreeWidget.getSelectedItem().setHTML( AbstractImagePrototype.create( ImageResources.INSTANCE.categorySmall() ).getHTML() + h( name ) );
    }

    public void removeSelected() {
        final CategoryItem parent = ( (CategoryItem) navTreeWidget.getSelectedItem().getUserObject() ).getParent();
        if ( parent != null ) {
            parent.removeChildren( getSelectedCategory().getName() );
        } else {
            categories.removeChildren( getSelectedCategory().getName() );
        }
        navTreeWidget.getSelectedItem().remove();
    }

    public void addChildren( final CategoryItem parent,
                             final String name,
                             final String description ) {
        final CategoryItem child = parent.addChildren( name, description );

        final TreeItem parentTree;
        if ( getSelectedCategory() == null ) {
            parentTree = navTreeWidget.getItem( 0 );
        } else {
            parentTree = navTreeWidget.getSelectedItem();
        }

        final TreeItem ct = buildTreeItem( child );
        parentTree.addItem( ct );
        parentTree.setState( true );
    }

    public Categories getCategories() {
        return categories;
    }

}