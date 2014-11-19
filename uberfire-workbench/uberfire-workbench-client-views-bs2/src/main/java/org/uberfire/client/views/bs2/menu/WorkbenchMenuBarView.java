/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.views.bs2.menu;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.Nav;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

/**
 * The Menu Bar widget
 */
public class WorkbenchMenuBarView extends Composite
        implements
        WorkbenchMenuBarPresenter.View {

    interface WorkbenchMenuBarViewBinder
            extends
            UiBinder<Panel, WorkbenchMenuBarView> {

    }

    private static WorkbenchMenuBarViewBinder uiBinder = GWT.create( WorkbenchMenuBarViewBinder.class );

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;

    @UiField
    public Nav menuBarLeft;

    @UiField
    public Nav menuBarCenter;

    @UiField
    public Nav menuBarRight;

    public WorkbenchMenuBarView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    /**
     * Add a Presenter Menu item to the view. This simply converts Presenter
     * Menu items to GWT MenuItems. Filtering of menu items for permissions is
     * conducted by the Presenter.
     */
    @Override
    public void addMenuItems( final Menus menus ) {
        for ( final MenuItem activeMenu : menus.getItems() ) {
            final Widget result = makeItem( activeMenu );
            if ( result != null ) {
                final Widget gwtItem = makeItem( activeMenu );
                if ( activeMenu.getPosition().equals( MenuPosition.LEFT ) ) {
                    menuBarLeft.add( gwtItem );
                } else if ( activeMenu.getPosition().equals( MenuPosition.CENTER ) ) {
                    menuBarCenter.add( gwtItem );
                } else if ( activeMenu.getPosition().equals( MenuPosition.RIGHT ) ) {
                    menuBarRight.add( gwtItem );
                }
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

        } else if ( item instanceof MenuCustom ) {

            return makeMenuCustom( (MenuCustom) item );

        }
        return makeNavLink( item );
    }

    Widget makeNavLink( final MenuItem item ) {
        final NavLink gwtItem = new NavLink( item.getCaption() ) {{
            setDisabled( !item.isEnabled() );
        }};
        item.addEnabledStateChangeListener( new EnabledStateChangeListener() {
            @Override
            public void enabledStateChanged( final boolean enabled ) {
                gwtItem.setDisabled( !enabled );
            }
        } );

        return gwtItem;
    }

    Widget makeMenuCustom( MenuCustom item ) {
        final MenuCustom custom = item;

        return (Widget) custom.build();
    }

    Widget makeMenuGroup( MenuGroup item ) {
        final MenuGroup groups = item;
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
        menuBarLeft.clear();
        menuBarCenter.clear();
        menuBarRight.clear();
    }

}
