/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.views.pfly.menu;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorButton;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownHeader;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListDropDown;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.html.Text;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuPosition;

@ApplicationScoped
public class WorkbenchMenuCompactNavBarView extends WorkbenchMenuNavBarView {

    private final Map<String, String> menuLabelMap = Maps.newHashMap();
    private final SetMultimap<String, ComplexPanel> menuItemContextMenus = HashMultimap.create();
    private final AnchorButton anchor = GWT.create( AnchorButton.class );
    private final Text text = GWT.create( Text.class );
    private final DropDownMenu dropDownMenu = GWT.create( DropDownMenu.class );
    private final ListDropDown listDropDown = GWT.create( ListDropDown.class );

    @PostConstruct
    protected void setup() {
        super.setup();
        this.addStyleName( "uf-navbar-nav-compact" );
        anchor.setDataToggle( Toggle.DROPDOWN );
        anchor.add( text );
        listDropDown.add( anchor );
        listDropDown.add( dropDownMenu );
        this.navbarNav.add( listDropDown );
    }

    @Override
    public void addMenuItem( final String id,
                             final String label,
                             final String parentId,
                             final Command command,
                             final MenuPosition position ) {
        final AnchorListItem menuItem = GWT.create( AnchorListItem.class );
        menuItem.setText( label );
        if ( command != null ) {
            menuItem.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    command.execute();
                }
            } );
        }
        getMenuItemWidgetMap().put( id, menuItem );
        menuLabelMap.put( id, label );
        dropDownMenu.add( menuItem );
    }

    @Override
    public void addCustomMenuItem( final Widget menu,
                                   final MenuPosition position ) {
        //No support for adding custom menus when using compact mode.
    }

    @Override
    public void addGroupMenuItem( final String id,
                                  final String label,
                                  final MenuPosition position ) {
        final DropDownHeader group = GWT.create( DropDownHeader.class );
        group.setText( label );
        dropDownMenu.add( group );
    }

    @Override
    public void addContextMenuItem(
            final String menuItemId,
            final String id,
            final String label,
            final String parentId,
            final Command command,
            final MenuPosition position ) {
        final AnchorListItem menuItem = GWT.create( AnchorListItem.class );
        menuItem.setText( label );
        menuItem.addStyleName( UF_PERSPECTIVE_CONTEXT_MENU );
        if ( command != null ) {
            menuItem.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    command.execute();
                }
            } );
        }

        final ComplexPanel contextContainer = getMenuItemContextWidgetMap().get( parentId );
        if ( contextContainer != null ) {
            contextContainer.add( menuItem );
        } else {
            menuItemContextMenus.get( menuItemId ).add( menuItem );
        }
        getMenuItemContextWidgetMap().put( id, menuItem );
    }

    @Override
    public void addContextGroupMenuItem( final String menuItemId,
                                         final String id,
                                         final String label,
                                         final MenuPosition position ) {
        final ListDropDown listDropDown = GWT.create( ListDropDown.class );
        listDropDown.addStyleName( UF_PERSPECTIVE_CONTEXT_MENU );
        final AnchorButton anchor = GWT.create( AnchorButton.class );
        anchor.setDataToggle( Toggle.DROPDOWN );
        anchor.setText( label );
        final DropDownMenu dropDownMenu = GWT.create( DropDownMenu.class );
        listDropDown.add( anchor );
        listDropDown.add( dropDownMenu );
        menuItemContextMenus.get( menuItemId ).add( listDropDown );
        getMenuItemContextWidgetMap().put( id, dropDownMenu );
    }

    @Override
    public void clearContextMenu() {
        super.clearContextMenu();

        for( final ComplexPanel contextContainer : menuItemContextMenus.values() ){
            contextContainer.clear();
            contextContainer.removeFromParent();
        }

        menuItemContextMenus.clear();
    }

    @Override
    public void selectMenuItem( final String id ) {
        super.selectMenuItem( id );
        final String menuLabel = menuLabelMap.get( id );
        text.setText( menuLabel == null ? "" : menuLabel );
        for ( ComplexPanel context : menuItemContextMenus.get( id ) ) {
            navbarNav.add( context );
        }
    }

    @Override
    public void clear() {
        super.clear();
        menuLabelMap.clear();
        menuItemContextMenus.clear();
    }

    @Override
    protected void selectElement( final ComplexPanel item ) {
        for ( Widget widget : dropDownMenu ) {
            widget.removeStyleName( Styles.ACTIVE );
        }
        if ( item != null ) {
            item.addStyleName( Styles.ACTIVE );
        }
    }

    protected Multimap<String, ComplexPanel> getMenuItemContextMenus() {
        return menuItemContextMenus;
    }
}