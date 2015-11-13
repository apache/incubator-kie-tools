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

package org.uberfire.client.views.pfly.menu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.User.StandardUserProperties;
import org.uberfire.client.menu.AuthFilterMenuVisitor;
import org.uberfire.client.workbench.widgets.menu.HasMenus;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

/**
 * Provides the menu that appears in the top right corner of the screen. Shows the current user's name.
 */
@Dependent
public class UserMenu implements MenuFactory.CustomMenuBuilder, HasMenus {

    public interface UserMenuView extends HasMenuItems {

        void setUserName( String userName );

    }

    @Inject
    private UserMenuView userMenuView;

    @Inject
    private User user;

    @Inject
    private AuthorizationManager authzManager;

    @PostConstruct
    protected void setup() {
        userMenuView.setUserName( formattedUsername() );
    }

    @Override
    public void addMenus( final Menus menus ) {
        menus.accept( new AuthFilterMenuVisitor( authzManager, user, new DropdownMenuVisitor( userMenuView ) ) );
    }

    /**
     * Tries to return the user's first and/or last names. If neither is available, returns the user's ID instead.
     */
    private String formattedUsername() {
        final StringBuilder sb = new StringBuilder();
        if ( user.getProperty( StandardUserProperties.FIRST_NAME ) != null ) {
            sb.append( user.getProperty( StandardUserProperties.FIRST_NAME ) );
        }
        if ( user.getProperty( StandardUserProperties.LAST_NAME ) != null ) {
            if( sb.length() > 0 ) {
                sb.append( " " );
            }
            sb.append( user.getProperty( StandardUserProperties.LAST_NAME ) );
        }
        if ( sb.length() == 0 ) {
            sb.append( user.getIdentifier() );
        }
        return sb.toString();
    }

    public IsWidget getView() {
        return userMenuView;
    }

    @Override
    public void push( MenuFactory.CustomMenuBuilder element ) {

    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {

            @Override
            public IsWidget build() {
                return getView();
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.RIGHT;
            }
        };
    }

}
