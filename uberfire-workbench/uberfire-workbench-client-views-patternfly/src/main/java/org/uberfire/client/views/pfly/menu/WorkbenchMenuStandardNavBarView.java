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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorButton;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListDropDown;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.html.UnorderedList;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuPosition;

@ApplicationScoped
public class WorkbenchMenuStandardNavBarView extends WorkbenchMenuNavBarView {

    @PostConstruct
    protected void setup() {
        super.setup();
        this.addStyleName( "navbar-primary persistent-secondary" );
    }

    @Override
    public void addMenuItem( final String id, final String label, final String parentId, final Command command ) {
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
        if ( parentId == null ) {
            navbarNav.add( menuItem );
        } else {
            final ComplexPanel parent = getMenuItemWidgetMap().get( parentId );
            if ( parent != null ) {
                parent.add( menuItem );
            }
        }
    }

    @Override
    public void addCustomMenuItem( final Widget menu ) {
        navbarNav.add( menu );
    }

    @Override
    public void addGroupMenuItem( final String id, final String label ) {
        final ListDropDown listDropDown = GWT.create( ListDropDown.class );
        final AnchorButton anchor = GWT.create( AnchorButton.class );
        anchor.setDataToggle( Toggle.DROPDOWN );
        anchor.setText( label );
        final DropDownMenu dropDownMenu = GWT.create( DropDownMenu.class );
        listDropDown.add( anchor );
        listDropDown.add( dropDownMenu );
        navbarNav.add( listDropDown );
        getMenuItemWidgetMap().put( id, dropDownMenu );
    }

    @Override
    public void addContextMenuItem(
            final String menuItemId,
            final String id,
            final String label,
            final String parentId,
            final Command command,
            final MenuPosition position ) {
        final ComplexPanel menuItemWidget = getMenuItemWidgetMap().get( menuItemId );
        if ( menuItemWidget == null ) {
            return;
        }

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

        if( position == MenuPosition.RIGHT ){
            menuItem.setPull( Pull.RIGHT );
        }

        ComplexPanel contextContainer = getMenuItemContextWidgetMap().get( parentId );
        if ( contextContainer == null ) {
            contextContainer = getContextContainer( menuItemId, menuItemWidget );
        }

        contextContainer.add( menuItem );
        getMenuItemContextWidgetMap().put( id, menuItem );
    }

    @Override
    public void addContextGroupMenuItem( final String menuItemId, final String id, final String label ) {
        final ComplexPanel menuItemWidget = getMenuItemWidgetMap().get( menuItemId );
        if ( menuItemWidget == null ) {
            return;
        }

        final ComplexPanel contextContainer = getContextContainer( menuItemId, menuItemWidget );

        final ListDropDown listDropDown = GWT.create( ListDropDown.class );
        listDropDown.setStyleName( "dropdown-submenu" );
        final Anchor anchor = GWT.create( Anchor.class );
        anchor.addStyleName( Styles.DROPDOWN_TOGGLE );
        anchor.setDataToggle( Toggle.DROPDOWN );
        anchor.setText( label );
        final DropDownMenu dropDownMenu = GWT.create( DropDownMenu.class );
        listDropDown.add( anchor );
        listDropDown.add( dropDownMenu );

        contextContainer.add( listDropDown );
        getMenuItemContextWidgetMap().put( id, dropDownMenu );
    }

    private ComplexPanel getContextContainer( final String menuItemId, final ComplexPanel menuItemWidget ) {
        final ComplexPanel container = menuItemWidget.getParent().getParent() instanceof ListDropDown ? (ListDropDown) menuItemWidget.getParent().getParent() : menuItemWidget;
        ComplexPanel contextContainer = getContextContainerWidgetMap().get( menuItemId );
        if ( contextContainer == null ) {
            contextContainer = GWT.create( UnorderedList.class );
            contextContainer.addStyleName( "nav navbar-nav navbar-persistent" );
            contextContainer.addStyleName( UF_PERSPECTIVE_CONTEXT_MENU );
            contextContainer.setVisible( false );
            container.add( contextContainer );
            getContextContainerWidgetMap().put( menuItemId, contextContainer );
        }
        return contextContainer;
    }

    @Override
    public void selectMenuItem( final String id ) {
        super.selectMenuItem( id );
        for ( Map.Entry<String, ComplexPanel> context : getContextContainerWidgetMap().entrySet() ) {
            if ( context.getKey().equals( id ) ) {
                context.getValue().setVisible( true );
                context.getValue().getParent().addStyleName( UF_PERSPECTIVE_CONTEXT_MENU_CONTAINER );
            } else {
                context.getValue().setVisible( false );
            }
        }
    }

    @Override
    protected void selectElement( final ComplexPanel item ) {
        iterateWidgets( navbarNav );

        if ( item == null ) {
            return;
        }

        item.addStyleName( Styles.ACTIVE );

        if ( item.getParent() != null && item.getParent().getParent() instanceof ListDropDown ) {
            item.getParent().getParent().addStyleName( Styles.ACTIVE );
        }
    }

    private void iterateWidgets( final ComplexPanel widget ) {
        widget.removeStyleName( Styles.ACTIVE );
        widget.removeStyleName( UF_PERSPECTIVE_CONTEXT_MENU_CONTAINER );
        for ( Widget next : widget ) {
            if ( next instanceof ComplexPanel ) {
                iterateWidgets( (ComplexPanel) next );
            }
        }
    }

}