package org.uberfire.client.tree;

import java.util.Iterator;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.resources.NavigatorResources;

public class Tree extends Composite implements HasSelectionHandlers<TreeItem>,
                                               HasOpenHandlers<TreeItem>,
                                               HasCloseHandlers<TreeItem> {

    final FlowPanel container = new FlowPanel();

    private TreeItem curSelection = null;

    public Tree() {
        initWidget( container );
        container.setStylePrimaryName( NavigatorResources.INSTANCE.css().tree() );
    }

    @Override
    public HandlerRegistration addOpenHandler( final OpenHandler<TreeItem> handler ) {
        return addHandler( handler, OpenEvent.getType() );
    }

    @Override
    public HandlerRegistration addCloseHandler( final CloseHandler<TreeItem> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

    @Override
    public HandlerRegistration addSelectionHandler( final SelectionHandler<TreeItem> handler ) {
        return addHandler( handler, SelectionEvent.getType() );
    }

    public void clear() {
        container.clear();
    }

    void fireStateChanged( final TreeItem item,
                           final TreeItem.State state ) {
        if ( state.equals( TreeItem.State.OPEN ) ) {
            OpenEvent.fire( this, item );
        } else {
            CloseEvent.fire( this, item );
        }
        onSelection( item, true );
    }

    public void setSelectedItem( final TreeItem item ) {
        onSelection( item, true );
    }

    public void setSelectedItem( final TreeItem item,
                                 final boolean fireEvents ) {
        onSelection( item, fireEvents );
    }

    public TreeItem getSelectedItem() {
        return curSelection;
    }

    void onSelection( final TreeItem item,
                      final boolean fireEvents ) {
        if ( curSelection != null ) {
            curSelection.setSelected( false );
        }
        curSelection = item;

        if ( curSelection != null ) {
            // Select the item and fire the selection event.
            curSelection.setSelected( true );
            if ( fireEvents ) {
                SelectionEvent.fire( this, curSelection );
            }
        }
    }

    public TreeItem addItem( final TreeItem.Type type,
                             final String value ) {
        final TreeItem item = new TreeItem( type, value );
        return addItem( item );
    }

    public TreeItem addItem( final TreeItem item ) {
        container.add( item );
        item.setTree( this );
        return item;
    }

    public void removeItem( final TreeItem treeItem ) {
        container.remove( treeItem );
    }

    public Iterable<TreeItem> getItems() {
        return new Iterable<TreeItem>() {
            @Override
            public Iterator<TreeItem> iterator() {
                return new TreeItem.TreeItemIterator( container );
            }
        };
    }

    public boolean isEmpty() {
        return container.getWidgetCount() == 0;
    }
}
