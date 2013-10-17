package org.uberfire.client.tree;

import java.util.Iterator;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.resources.NavigatorResources;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class TreeItem extends Composite {

    private Tree tree;
    private Object userObject;
    private TreeItem parent;

    public static enum Type {
        ROOT, FOLDER, ITEM, LOADING
    }

    public static enum State {
        NONE, OPEN, CLOSE
    }

    private State state = State.CLOSE;
    private boolean isSelected = false;
    private final Type type;

    private FlowPanel header;
    private Element icon;
    private FlowPanel content;
    private FlowPanel item;

    public TreeItem( final Type type,
                     final String value ) {
        this.type = checkNotNull( "type", type );
        if ( type.equals( Type.FOLDER ) || type.equals( Type.ROOT ) ) {
            final FlowPanel folder = new FlowPanel();
            folder.setStylePrimaryName( NavigatorResources.INSTANCE.css().treeFolder() );
            folder.getElement().getStyle().setDisplay( Style.Display.BLOCK );
            {
                this.header = new FlowPanel();
                this.icon = Document.get().createElement( "i" );
                this.content = new FlowPanel();
                final Anchor name = new Anchor();
                {
                    header.setStylePrimaryName( NavigatorResources.INSTANCE.css().treeFolderHeader() );
                    folder.add( header );
                    {
                        icon.setClassName( "icon-folder-close" );
                        header.getElement().appendChild( icon );
                    }
                    final FlowPanel folderName = new FlowPanel();
                    {
                        folderName.setStylePrimaryName( NavigatorResources.INSTANCE.css().treeFolderName() );
                        header.add( folderName );
                        {
                            name.setText( value );
                            folderName.add( name );
                        }
                    }
                    header.addDomHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            if ( state.equals( State.CLOSE ) ) {
                                setState( State.OPEN, true );
                            } else {
                                setState( State.CLOSE, true );
                            }
                        }
                    }, ClickEvent.getType() );
                }
                {
                    content.setStylePrimaryName( NavigatorResources.INSTANCE.css().treeFolderContent() );
                    content.getElement().getStyle().setDisplay( Style.Display.NONE );
                    folder.add( content );
                }
                initWidget( folder );
            }
        } else if ( type.equals( Type.ITEM ) ) {
            this.item = new FlowPanel();
            item.setStylePrimaryName( NavigatorResources.INSTANCE.css().treeItem() );
            {
                this.icon = Document.get().createElement( "i" );
                final FlowPanel itemName = new FlowPanel();
                final Anchor name = new Anchor();
                {
                    icon.setClassName( "icon-file-alt" );
                    item.getElement().appendChild( icon );
                }
                {
                    itemName.setStylePrimaryName( NavigatorResources.INSTANCE.css().treeItemName() );
                    item.add( itemName );
                    {
                        name.setText( value );
                        itemName.add( name );
                    }
                }
                item.addDomHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        tree.onSelection( TreeItem.this, true );
                    }
                }, ClickEvent.getType() );
            }
            initWidget( item );
        } else {
            final FlowPanel loader = new FlowPanel();
            {
                final SimplePanel loading = new SimplePanel();
                loading.getElement().setInnerText( value );
                loader.add( loading );
            }
            initWidget( loader );
        }
    }

    public State getState() {
        return state;
    }

    public void setState( final State state ) {
        setState( state, false, true );
    }

    public void setState( final State state,
                          boolean fireEvents ) {
        setState( state, false, fireEvents );
    }

    public void setState( final State state,
                          boolean propagateParent,
                          boolean fireEvents ) {
        if ( notFolder() ) {
            return;
        }

        if ( !this.state.equals( state ) ) {
            this.state = state;
            updateState( state );
            if ( propagateParent && parent != null ) {
                parent.setState( state, true, false );
            }

            if ( fireEvents && tree != null ) {
                tree.fireStateChanged( this, state );
            }
        }
    }

    private boolean notFolder() {
        return !type.equals( Type.FOLDER );
    }

    public Object getUserObject() {
        return userObject;
    }

    public void setUserObject( final Object userObject ) {
        this.userObject = userObject;
    }

    public TreeItem addItem( final Type type,
                             final String value ) {
        if ( notFolder() ) {
            return null;
        }

        final TreeItem child = new TreeItem( type, value );
        content.add( child );
        child.setTree( tree );
        child.setParent( this );

        return child;
    }

    public void removeItems() {
        content.clear();
    }

    public int getChildCount() {
        return content.getWidgetCount();
    }

    public TreeItem getChild( final int i ) {
        if ( i + 1 > content.getWidgetCount() ) {
            return null;
        }
        return (TreeItem) content.getWidget( i );
    }

    public Iterable<TreeItem> getChildren() {
        return new Iterable<TreeItem>() {
            @Override
            public Iterator<TreeItem> iterator() {
                return new TreeItemIterator( content );
            }
        };
    }

    void setTree( final Tree tree ) {
        this.tree = tree;
    }

    void setParent( final TreeItem parent ) {
        this.parent = parent;
    }

    void updateState( final State state ) {
        // If the tree hasn't been set, there is no visual state to update.
        // If the tree is not attached, then update will be called on attach.
        if ( tree == null || !tree.isAttached() ) {
            return;
        }

        switch ( state ) {
            case OPEN:
                content.getElement().getStyle().setDisplay( Style.Display.BLOCK );
                icon.setClassName( "icon-folder-open" );
                break;
            case CLOSE:
                icon.setClassName( "icon-folder-close" );
                content.getElement().getStyle().setDisplay( Style.Display.NONE );
        }
    }

    /**
     * Removes this item from its tree.
     */
    public void remove() {
        if ( parent != null ) {
            // If this item has a parent, remove self from it.
            parent.removeItem( this );
        } else if ( tree != null ) {
            // If the item has no parent, but is in the Tree, it must be a top-level
            // element.
            tree.removeItem( this );
        }
    }

    private void removeItem( final TreeItem treeItem ) {
        content.remove( treeItem );
    }

    public String getText() {
        return getElement().getInnerText();
    }

    public boolean isSelected() {
        return isSelected;
    }

    void setSelected( boolean selected ) {
        isSelected = selected;
        if ( selected ) {
            if ( header != null ) {
                header.addStyleName( NavigatorResources.INSTANCE.css().treeSelected() );
            } else {
                item.addStyleName( NavigatorResources.INSTANCE.css().treeSelected() );
            }
        } else {
            if ( header != null ) {
                header.removeStyleName( NavigatorResources.INSTANCE.css().treeSelected() );
            } else {
                item.removeStyleName( NavigatorResources.INSTANCE.css().treeSelected() );
            }
        }
    }

    public boolean isEmpty() {
        return content.getWidgetCount() == 0;
    }

    protected static class TreeItemIterator implements Iterator<TreeItem> {

        private final ComplexPanel container;
        private int index = 0;

        TreeItemIterator( ComplexPanel container ) {
            this.container = container;
        }

        @Override
        public boolean hasNext() {
            if ( container == null ) {
                return false;
            }
            return index < container.getWidgetCount();
        }

        @Override
        public TreeItem next() {
            return (TreeItem) container.getWidget( index++ );
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
