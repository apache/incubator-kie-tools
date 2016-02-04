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

package org.uberfire.workbench.model.menu;

import java.util.Collection;
import java.util.List;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.impl.MenuBuilderImpl;

import static org.uberfire.workbench.model.menu.impl.MenuBuilderImpl.MenuType.*;

public final class MenuFactory {

    private MenuFactory() {
    }

    public static MenuBuilder<TopLevelMenusBuilder<MenuBuilder>> newTopLevelMenu( final String caption ) {
        return new MenuBuilderImpl( TOP_LEVEL, caption );
    }

    public static ContributedMenuBuilder<TopLevelMenusBuilder<MenuBuilder>> newContributedMenu( final String caption ) {
        return new MenuBuilderImpl( CONTRIBUTED, caption );
    }

    public static MenuBuilder<Builder> newSimpleItem( final String caption ) {
        return new MenuBuilderImpl( REGULAR, caption );
    }

    public static MenuBuilder<TopLevelMenusBuilder<MenuBuilder>> newTopLevelCustomMenu( final CustomMenuBuilder builder ) {
        return new MenuBuilderImpl( CUSTOM, builder );
    }

    public interface TopLevelMenusBuilder<T>
            extends Builder {

        ContributedMenuBuilder<TopLevelMenusBuilder<T>> newContributedMenu( final String caption );

        TerminalMenu<TopLevelMenusBuilder<T>> newTopLevelMenu( final MenuItem menu );

        MenuBuilder<TopLevelMenusBuilder<T>> newTopLevelMenu( final String caption );

        TerminalCustomMenu<TopLevelMenusBuilder<T>> newTopLevelCustomMenu( final CustomMenuBuilder builder );
    }

    public interface Builder {

        Menus build();
    }

    public interface ContributedMenuBuilder<T> extends MenuBuilder<T> {

        ContributedMenuBuilder<T> contributeTo( final String contributionPoint );

    }

    public interface MenuBuilder<T>
            extends SimpleMenuBuilder<MenuBuilder<T>>,
                    SecurityInfos<MenuBuilder<T>>,
                    CommandMenu<MenuBuilder<T>>,
                    PerspectiveMenu<MenuBuilder<T>>,
                    OrderedMenu<MenuBuilder<T>> {

        TerminalMenu<T> custom( final CustomMenuBuilder builder );

        SubMenusBuilder<SubMenuBuilder<T>> submenu( final String caption );

        SubMenusBuilder<SubMenuBuilder<T>> menus();

        TerminalMenu<T> withItems( final List<? extends MenuItem> items );

        T endMenu();
    }

    public interface SubMenuBuilder<T>
            extends SimpleMenuBuilder<MenuBuilder<T>>,
                    SecurityInfos<MenuBuilder<T>> {

        SubMenusBuilder<SubMenuBuilder<T>> submenu( final String caption );

        T endMenu();
    }

    public interface SimpleMenuBuilder<T> {

        T order( final int order );

        T position( final MenuPosition position );
    }

    public interface SubMenusBuilder<T> {

        MenuBuilder<SubMenusBuilder<T>> menu( final String caption );

        T endMenus();
    }

    public interface TerminalMenu<T> {

        T endMenu();
    }

    public interface TerminalCustomMenu<T> {

        T endMenu();
    }

    public interface SecurityInfos<T> {

        T withRole( final String role );

        T withRoles( final String... roles );

        T withRoles( final Collection<String> roles );
    }

    public interface CustomMenuBuilder {

        void push( final CustomMenuBuilder element );

        MenuItem build();
    }

    public interface CommandMenu<T> {

        T respondsWith( final Command command );
    }

    public interface PerspectiveMenu<T> {

        T perspective( final String identifier );

        T place( final PlaceRequest placeRequest );
    }

    public interface OrderedMenu<T> {

        T orderAll( final int order );
    }

}
