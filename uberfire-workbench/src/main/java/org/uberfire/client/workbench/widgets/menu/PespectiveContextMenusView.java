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
import org.uberfire.security.Identity;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
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
    private RuntimeAuthorizationManager authzManager;

    @Inject
    private Identity identity;

    private NavPills menuBar = new NavPills();

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

    private Widget makeItem( final MenuItem item ) {
        if ( !authzManager.authorize( item, identity ) ) {
            return null;
        }

        if ( item instanceof MenuItemCommand ) {
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

        } else if ( item instanceof MenuGroup ) {
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

        return null;
    }

    @Override
    public void clear() {
        menuBar.clear();
    }

}
