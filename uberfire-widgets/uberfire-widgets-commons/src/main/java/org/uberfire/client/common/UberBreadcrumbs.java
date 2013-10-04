package org.uberfire.client.common;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavWidget;
import com.github.gwtbootstrap.client.ui.base.ComplexWidget;
import com.github.gwtbootstrap.client.ui.base.ListItem;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.github.gwtbootstrap.client.ui.constants.Constants;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;

public class UberBreadcrumbs extends UnorderedList {

    private WidgetCollection children = new WidgetCollection( this );

    private List<Divider> dividerList = new ArrayList<Divider>();

    private String divider = "/";

    private static class Divider extends ComplexWidget {

        public Divider( String divider ) {
            super( "span" );
            setStyleName( Constants.DIVIDER );
            setDivider( divider );
        }

        public void setDivider( String divider ) {
            getElement().setInnerText( divider );
        }
    }

    /**
     * Create a empty Breadcrumbs
     */
    public UberBreadcrumbs() {
        setStyleName( Constants.BREADCRUMB );
    }

    /**
     * Create Breadcrumbs with widgets
     * @param widgets widgets
     */
    public UberBreadcrumbs( Widget... widgets ) {
        this();

        for ( Widget widget : widgets ) {
            this.add( widget );
        }
    }

    /**
     * Create Breadcrumbs with setting divider character
     * @param divider separate char (ex : ">")
     */
    public UberBreadcrumbs( String divider ) {
        this();
        this.setDivider( divider );
    }

    /**
     * Set separater character
     * @param divider separater character
     */
    public void setDivider( String divider ) {
        if ( divider == null || divider.isEmpty() ) {
            this.divider = "/";
        } else {
            this.divider = divider;
        }

        for ( Divider dividerWidget : dividerList ) {
            dividerWidget.setDivider( this.divider );
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttach() {

        if ( !isOrWasAttached() && children.size() > 0 ) {

            Widget lastWidget = children.get( children.size() - 1 );

            for ( final Widget w : children ) {
                Widget item = lastWidget.equals( w ) ? w : getOrCreateListItem( w );
                if (item != null){
                    super.add( item );
                }
            }
        }

        super.onAttach();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add( Widget w ) {

        w.removeStyleName( Constants.ACTIVE );

        if ( !isOrWasAttached() ) {

            children.add( w );

            return;
        }

        if ( children.size() > 0 ) {
            //Change last widget 2 Link

            //pygical remove
            super.remove( getWidget( getWidgetCount() - 1 ) );

            ComplexWidget item = getOrCreateListItem( children.get( children.size() - 1 ) );

            if (item != null){
                super.add( item );
            }
        }

        super.add( w );

        children.add( w );
    }

    private ComplexWidget getOrCreateListItem( final Widget lastWidget ) {
        ListItem item = null;

        final Divider dividerWidget = new Divider( divider );
        if ( lastWidget instanceof NavWidget ) {
            NavWidget w = (NavWidget) lastWidget;

            if ( hasDivier( w ) ) {

                return w;
            } else {
                dividerList.add( dividerWidget );
                w.addWidget( dividerWidget );
                return w;
            }
        } else if ( lastWidget instanceof Dropdown ) {
            final Dropdown w = (Dropdown) lastWidget;

            if ( hasDivier( w ) ) {
                return w;
            } else {
                dividerList.add( dividerWidget );
                super.add( w );
                super.add( dividerWidget );
                return null;
            }
        } else if ( lastWidget instanceof ListItem ) {
            item = (ListItem) lastWidget;
        } else {
            item = new ListItem( lastWidget );
        }

        if ( hasDivier( item ) ) {
            return item;
        }

        item.add( dividerWidget );
        dividerList.add( dividerWidget );

        return item;
    }

    private boolean hasDivier( final ComplexWidget item ) {

        for ( Widget w : item ) {
            if ( w instanceof Divider ) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove( Widget w ) {

        if ( !isOrWasAttached() && children.contains( w ) ) {
            children.remove( w );
            return true;
        }

        if ( getWidgetIndex( w ) < 0 && !children.contains( w ) ) {
            return false;
        }

        if ( getWidgetIndex( w ) >= 0 && children.contains( w ) ) {
            children.remove( w );
            super.remove( w );
        } else if ( getWidgetIndex( w ) >= 0 && !children.contains( w ) ) {
            children.remove( getWidgetIndex( w ) );
            super.remove( w );
        } else if ( getWidgetIndex( w ) < 0 && children.contains( w ) ) {
            return remove( getWidget( children.indexOf( w ) ) );
        } else {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        super.clear();
        children = new WidgetCollection( this );
        dividerList.clear();
    }
}