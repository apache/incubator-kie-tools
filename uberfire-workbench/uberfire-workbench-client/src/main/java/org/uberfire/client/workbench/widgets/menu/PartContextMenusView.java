package org.uberfire.client.workbench.widgets.menu;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavPills;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import org.uberfire.security.Subject;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
public class PartContextMenusView
        extends Composite
        implements PartContextMenusPresenter.View {

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private Subject identity;

    NavPills menuBar = new NavPills();

    public PartContextMenusView() {
        initWidget( menuBar );
    }

    @Override
    public void buildMenu( final Menus menus ) {
        menuBar.clear();

        for ( final MenuItem menuItem : menus.getItems() ) {
            final Widget result = makeItem( menuItem );
            if ( result != null ) {
                menuBar.add( result );
            }
        }
    }

    Widget makeItem( final MenuItem item ) {
        if ( notHavePermissionToMakeThis( item ) ) {
            return null;
        }

        if ( item instanceof MenuItemCommand ) {
            return makeMenuItemCommand( item );

        } else if ( item instanceof MenuGroup ) {
            return makeMenuGroup( (MenuGroup) item );
        }

        return null;
    }

    Widget makeMenuGroup( MenuGroup item ) {
        final MenuGroup groups = (MenuGroup) item;
        final List<Widget> widgetList = new ArrayList<Widget>();
        for ( final MenuItem _item : groups.getItems() ) {
            final Widget result = makeItem( _item );
            if ( result != null ) {
                widgetList.add( result );
            }
        }

        if ( widgetList.isEmpty() ) {
            return null;
        }

        return new Dropdown( groups.getCaption() ) {{
            for ( final Widget widget : widgetList ) {
                add( widget );
            }
        }};
    }

    Widget makeMenuItemCommand( final MenuItem item ) {
        final MenuItemCommand cmdItem = (MenuItemCommand) item;
        final Widget gwtItem;

        gwtItem = new NavLink( cmdItem.getCaption() ) {{
            setDisabled( !item.isEnabled() );
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( final ClickEvent event ) {
                    cmdItem.getCommand().execute();
                }
            } );
        }};
        item.addEnabledStateChangeListener( new EnabledStateChangeListener() {
            @Override
            public void enabledStateChanged( final boolean enabled ) {
                ( (NavLink) gwtItem ).setDisabled( !enabled );
            }
        } );

        return gwtItem;
    }

    boolean notHavePermissionToMakeThis( MenuItem item ) {
        return !authzManager.authorize( item, identity );
    }

    @Override
    public void clear() {
        menuBar.clear();
    }

}
