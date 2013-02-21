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
package org.uberfire.client.workbench.widgets.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Brand;
import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.Nav;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavSearch;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

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

    @UiField
    public Brand brand;

    @UiField
    public Nav menuBarLeft;

    @UiField
    public Nav menuBarCenter;

    @UiField
    public Nav menuBarRight;

    //Map of UberFire's AbstractMenuItems to GWT MenuItems
    private final Map<MenuItem, Widget> leftMenuItemsMap   = new HashMap<MenuItem, Widget>();
    private final Map<MenuItem, Widget> rightMenuItemsMap  = new HashMap<MenuItem, Widget>();
    private final Map<MenuItem, Widget> centerMenuItemsMap = new HashMap<MenuItem, Widget>();

    public WorkbenchMenuBarView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setBrandMenu( final BrandMenuItem brand ) {
        this.brand.setText( brand.getCaption() );
    }

    /**
     * Add a Presenter Menu item to the view. This simply converts Presenter
     * Menu items to GWT MenuItems. Filtering of menu items for permissions is
     * conducted by the Presenter.
     */
    @Override
    public void addMenuItem( final MenuItem item ) {

        final Widget gwtItem = makeMenuItem( item );
        if ( item.getPosition().equals( MenuPosition.LEFT ) ) {
            leftMenuItemsMap.put( item, gwtItem );
            menuBarLeft.add( gwtItem );
        } else if ( item.getPosition().equals( MenuPosition.CENTER ) ) {
            rightMenuItemsMap.put( item, gwtItem );
            menuBarRight.add( gwtItem );
        } else if ( item.getPosition().equals( MenuPosition.RIGHT ) ) {
            centerMenuItemsMap.put( item, gwtItem );
            menuBarCenter.add( gwtItem );
        }
    }

    /**
     * Remove a Presenter Menu item from the view.
     */
    @Override
    public void removeMenuItem( final MenuItem item ) {
        if ( leftMenuItemsMap.containsKey( item ) ) {
            final Widget gwtItem = leftMenuItemsMap.remove( item );
            if ( gwtItem != null ) {
                menuBarLeft.remove( gwtItem );
            }
        } else if ( rightMenuItemsMap.containsKey( item ) ) {
            final Widget gwtItem = rightMenuItemsMap.remove( item );
            if ( gwtItem != null ) {
                menuBarRight.remove( gwtItem );
            }
        } else if ( centerMenuItemsMap.containsKey( item ) ) {
            final Widget gwtItem = centerMenuItemsMap.remove( item );
            if ( gwtItem != null ) {
                menuBarCenter.remove( gwtItem );
            }
        }

    }

    //Recursively converts a Presenter Menu item to a GWT MenuItem
    private Widget makeMenuItem( final MenuItem item ) {
        if ( item instanceof MenuItemCommand ) {
            final MenuItemCommand cmdItem = (MenuItemCommand) item;
            final NavLink gwtItem = new NavLink( cmdItem.getCaption() ) {{
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
                    gwtItem.setDisabled( !enabled );
                }
            } );

            return gwtItem;

        }
        if ( item instanceof MenuSearchItem ) {
            final MenuSearchItem searchItem = (MenuSearchItem) item;
            final NavSearch gwtItem = new NavSearch() {{
                setPlaceholder( searchItem.getCaption() );
                addSubmitHandler( new SubmitHandler() {
                    @Override
                    public void onSubmit( final SubmitEvent event ) {
                        event.cancel();
                    }
                } );
            }};

            return gwtItem;

        } else if ( item instanceof MenuGroup ) {
            final MenuGroup groups = (MenuGroup) item;
            final Dropdown gwtItem = new Dropdown( groups.getCaption() );
            for ( final Widget _item : makeMenuItems( groups.getItems() ) ) {
                gwtItem.add( _item );
            }
            return gwtItem;
        }
        throw new IllegalArgumentException( "item type [" + item.getClass().getName() + "] is not recognised." );
    }

    private List<Widget> makeMenuItems( final List<MenuItem> items ) {
        final List<Widget> gwtItems = new ArrayList<Widget>();
        for ( final MenuItem item : items ) {
            final Widget gwtItem = makeMenuItem( item );
            gwtItems.add( gwtItem );
        }
        return gwtItems;
    }
}
