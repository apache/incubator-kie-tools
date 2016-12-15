/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.explorer.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

public class TreeExplorerView extends Composite implements TreeExplorer.View {

    interface ViewBinder extends UiBinder<Widget, TreeExplorerView> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    Tree tree;

    private TreeExplorer presenter;

    @Override
    public void init( final TreeExplorer presenter ) {
        this.presenter = presenter;
        initWidget( uiBinder.createAndBindUi( this ) );
        tree.addSelectionHandler( selectionEvent -> {
            final TreeItem item = selectionEvent.getSelectedItem();
            final String uuid = ( String ) item.getUserObject();
            presenter.onSelect( uuid );
        } );

    }

    public TreeExplorer.View addItem( final String uuid,
                                      final IsWidget itemView,
                                      final boolean state ) {
        final TreeItem item = buildItem( uuid, itemView );
        tree.addItem( item );
        item.setState( state );
        return this;
    }

    public TreeExplorer.View addItem( final String uuid,
                                      final IsWidget itemView,
                                      final boolean state,
                                      final int... parentsIds ) {
        final TreeItem item = buildItem( uuid, itemView );
        final TreeItem parent = getParent( parentsIds );
        parent.addItem( item );
        parent.setState( state );
        item.setState( state );
        return this;
    }

    private TreeItem buildItem( final String uuid, final IsWidget isWidget ) {
        final TreeItem item = new TreeItem();
        item.setWidget( ( Widget ) isWidget );
        item.setUserObject( uuid );
        item.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        return item;
    }

    public TreeExplorer.View removeItem( final int index ) {
        final TreeItem item = tree.getItem( index );
        tree.removeItem( item );
        return this;
    }

    public TreeExplorer.View removeItem( final int index, final int... parentsIds ) {
        final TreeItem parent = getParent( parentsIds );
        final TreeItem item = parent.getChild( index );
        parent.removeItem( item );
        return this;
    }

    @Override
    public TreeExplorer.View clear() {
        tree.clear();
        return this;
    }

    private TreeItem getParent( final int... parentsIds ) {
        TreeItem parent = null;
        for ( int x = 0; x < ( parentsIds.length - 1 ); x++ ) {
            final int parentIdx = parentsIds[ x ];
            if ( null == parent ) {
                parent = tree.getItem( parentIdx );
            } else {
                parent = parent.getChild( parentIdx );
            }
        }
        return parent;
    }

}
