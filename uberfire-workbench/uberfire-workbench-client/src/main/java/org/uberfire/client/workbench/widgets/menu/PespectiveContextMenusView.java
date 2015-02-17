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

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
public class PespectiveContextMenusView
        extends Composite
        implements PespectiveContextMenusPresenter.View {

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;

    NavPills menuBar = new NavPills();

    public PespectiveContextMenusView() {
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
        if ( isInstanceOfMenuItemCommand(item ) ) {
            return makeMenuItemCommand( item );

        } else if ( isInstanceOfMenuGroup( item ) ) {
            return makeMenuGroup( (MenuGroup) item );
        }

        return null;
    }

    private boolean isInstanceOfMenuGroup( MenuItem item ) {
        //Workaround af a dev mode bug that instanceOf doesn't seems to work with static inner classes
        boolean isInstanceOfMenuGroup = false;
        try {
            final MenuGroup _x = (MenuGroup) item;
            isInstanceOfMenuGroup = true;
        } catch ( Exception ex ) {
            isInstanceOfMenuGroup = false;
        }
        return isInstanceOfMenuGroup;
    }

    private boolean isInstanceOfMenuItemCommand( MenuItem item ) {
        //Workaround af a dev mode bug that instanceOf doesn't seems to work with static inner classes
        boolean isInstanceOfMenuItemCommand = false;
        try {
            final MenuItemCommand _x = (MenuItemCommand) item;
            isInstanceOfMenuItemCommand = true;
        } catch ( Exception ex ) {
            isInstanceOfMenuItemCommand = false;
        }
        return isInstanceOfMenuItemCommand;
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

        gwtItem.addStyleName( WorkbenchResources.INSTANCE.CSS().perspectiveContextMenus() );

        return gwtItem;
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

        Dropdown dropdown = new Dropdown( groups.getCaption() ) {{
            for ( final Widget widget : widgetList ) {
                add( widget );
            }
        }};
        dropdown.addStyleName( WorkbenchResources.INSTANCE.CSS().perspectiveContextMenus() );
        return dropdown;
    }

    boolean notHavePermissionToMakeThis( MenuItem item ) {
        return !authzManager.authorize( item, identity );
    }

    @Override
    public void clear() {
        menuBar.clear();
    }

}
