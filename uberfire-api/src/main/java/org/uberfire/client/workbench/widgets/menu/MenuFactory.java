package org.uberfire.client.workbench.widgets.menu;

import java.util.List;

import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.impl.MenuBuilderImpl;

import static org.uberfire.client.workbench.widgets.menu.impl.MenuBuilderImpl.MenuType.*;

public final class MenuFactory {

    private MenuFactory() {
    }

    public static MenuBuilder<TopLevelMenusBuilder<MenuBuilder>> newTopLevelMenu( final String caption ) {
        return new MenuBuilderImpl( TOP_LEVEL, caption );
    }

    public static ContributedMenuBuilder<TopLevelMenusBuilder<MenuBuilder>> newContributedMenu( final String caption ) {
        return new MenuBuilderImpl( CONTRIBUTED, caption );
    }

    public static SearchMenuBuilder<TopLevelMenusBuilder<MenuBuilder>> newSearchItem( final String caption ) {
        return new MenuBuilderImpl( SEARCH, caption );
    }

    public static MenuBuilder<Builder> newSimpleItem( final String caption ) {
        return new MenuBuilderImpl( REGULAR, caption );
    }

    public interface TopLevelMenusBuilder<T> extends Builder {

        ContributedMenuBuilder<TopLevelMenusBuilder<T>> newContributedMenu( final String caption );

        TerminalMenu<TopLevelMenusBuilder<T>> newTopLevelMenu( final MenuItem menu );

        MenuBuilder<TopLevelMenusBuilder<T>> newTopLevelMenu( final String caption );

        SearchMenuBuilder<TopLevelMenusBuilder<T>> newSearchItem( final String caption );

    }

    public interface Builder {

        Menus build();
    }

    public interface ContributedMenuBuilder<T> extends MenuBuilder<T> {

        ContributedMenuBuilder<T> contributeTo( final String contributionPoint );

    }

    public interface MenuBuilder<T>
            extends SimpleMenuBuilder<MenuBuilder<T>>,
                    SecurityInfos<MenuBuilder<T>> {

        SubMenusBuilder<SubMenuBuilder<T>> submenu( final String caption );

        SubMenusBuilder<SubMenuBuilder<T>> menus();

        TerminalMenu<T> withItems( final List<? extends MenuItem> items );

        TerminalMenu<T> respondsWith( final Command command );

        T endMenu();
    }

    public interface SearchMenuBuilder<T> {

        TerminalMenu<T> respondsWith( final MenuSearchItem.SearchCommand command );

        SearchMenuBuilder<T> withRole( final String role );

        SearchMenuBuilder<T> withRoles( final String... roles );

        SearchMenuBuilder<T> order( final int order );

        SearchMenuBuilder<T> position( final MenuPosition position );
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

    public interface SecurityInfos<T> {

        T withRole( final String role );

        T withRoles( final String... roles );
    }

}
