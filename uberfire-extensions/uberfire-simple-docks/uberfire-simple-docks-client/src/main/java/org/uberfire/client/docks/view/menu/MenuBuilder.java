/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.docks.view.menu;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.NavbarLink;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;

@ApplicationScoped
public class MenuBuilder {

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;

    public Widget makeItem( final MenuItem item,
                            boolean isRoot ) {
        if ( !authzManager.authorize( item, identity ) ) {
            return null;
        }

        if ( item instanceof MenuItemCommand ) {
            final MenuItemCommand cmdItem = (MenuItemCommand) item;
            if ( isRoot ) {
                final Button button = new Button( cmdItem.getCaption() );
                button.setSize( ButtonSize.SMALL );
                button.setEnabled( item.isEnabled() );
                button.addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                        cmdItem.getCommand().execute();
                    }
                } );
                item.addEnabledStateChangeListener( new EnabledStateChangeListener() {
                    @Override
                    public void enabledStateChanged( final boolean enabled ) {
                        button.setEnabled( enabled );
                    }
                } );
                return button;
            } else {
                final NavbarLink navbarLink = new NavbarLink();
                navbarLink.setText( cmdItem.getCaption() );
                if ( !item.isEnabled() ) {
                    navbarLink.addStyleName( "disabled" );
                }
                navbarLink.addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                        cmdItem.getCommand().execute();
                    }
                } );
                item.addEnabledStateChangeListener( new EnabledStateChangeListener() {
                    @Override
                    public void enabledStateChanged( final boolean enabled ) {
                        if ( enabled ) {
                            navbarLink.removeStyleName( "disabled" );
                        } else {
                            navbarLink.addStyleName( "disabled" );
                        }
                    }
                } );
                return navbarLink;
            }

        } else if ( item instanceof MenuGroup ) {
            final MenuGroup groups = (MenuGroup) item;
            if ( isRoot ) {
                final List<Widget> widgetList = new ArrayList<Widget>();
                for ( final MenuItem _item : groups.getItems() ) {
                    final Widget widget = makeItem( _item, false );
                    if ( widget != null ) {
                        widgetList.add( widget );
                    }
                }

                if ( widgetList.isEmpty() ) {
                    return null;
                }

                return makeDropDownMenuButton( groups.getCaption(),
                                               widgetList );

            } else {
                final List<Widget> widgetList = new ArrayList<Widget>();
                for ( final MenuItem _item : groups.getItems() ) {
                    final Widget result = makeItem( _item, false );
                    if ( result != null ) {
                        widgetList.add( result );
                    }
                }

                if ( widgetList.isEmpty() ) {
                    return null;
                }

                return makeDropDownMenuButton( groups.getCaption(),
                                               widgetList );
            }

        } else if ( item instanceof MenuCustom ) {
            final Object result = ( (MenuCustom) item ).build();
            if ( result instanceof Widget ) {
                return (Widget) result;
            }
        }

        return null;
    }

    private Widget makeDropDownMenuButton( final String caption,
                                           final List<Widget> widgetList ) {
        final ButtonGroup buttonGroup = new ButtonGroup();
        final Button dropdownButton = new Button( caption );
        dropdownButton.setDataToggle( Toggle.DROPDOWN );
        dropdownButton.setSize( ButtonSize.SMALL );
        final DropDownMenu dropDownMenu = new DropDownMenu();
        for ( final Widget _item : widgetList ) {
            dropDownMenu.add( _item );
        }
        buttonGroup.add( dropdownButton );
        buttonGroup.add( dropDownMenu );
        return buttonGroup;
    }

}
