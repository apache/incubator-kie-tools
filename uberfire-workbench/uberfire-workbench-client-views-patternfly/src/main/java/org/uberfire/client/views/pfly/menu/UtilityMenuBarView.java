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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.html.UnorderedList;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.menu.AuthFilterMenuVisitor;
import org.uberfire.client.workbench.widgets.menu.UtilityMenuBarPresenter;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Goes inside the collapsible navbar container, and can contain a status menu (not implemented yet) and the {@link UserMenu}.
 */
@ApplicationScoped
public class UtilityMenuBarView extends UnorderedList implements UtilityMenuBarPresenter.View,
                                                                 HasMenuItems {

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;

    @PostConstruct
    public void setup() {
        addStyleName( Styles.NAV );
        addStyleName( Styles.NAVBAR_NAV );
        addStyleName( "navbar-utility" );
    }

    @Override
    public void addMenus( final Menus menus ) {
        menus.accept( new AuthFilterMenuVisitor( authzManager, identity, new DropdownMenuVisitor( this ) ) );
    }

    @Override
    public void addMenuItem( final MenuPosition position,
                             final Widget menuContent ) {
        switch ( position ) {
            case LEFT:
                this.insert( menuContent, 0 );
                break;
            case RIGHT:
                this.add( menuContent );
                break;
        }
    }

}
