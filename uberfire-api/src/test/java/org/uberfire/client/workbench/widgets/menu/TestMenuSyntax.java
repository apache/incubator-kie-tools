package org.uberfire.client.workbench.widgets.menu;

/**
 *
 */
public class TestMenuSyntax {

    public static Object main( final String... args ) {
        return MenuFactory
                .newContributedMenu( "x" )
                    .contributeTo( "xx" )
                    .withRole( "" )
                    .submenu( "xx" )
                        .menu( "cc" )
                            .respondsWith( null )
                        .endMenu()
                    .endMenus()
                .endMenu().
                newTopLevelMenu( "")
                    .withRole( "xxx" )
                        .submenu("")
                            .menu("xx")
                                .withRole( "xxx" )
                                .respondsWith( null )
                            .endMenu()
                            .menu("x")
                                .submenu("xxx")
                                    .menu("xx")
                                        .respondsWith( null )
                                    .endMenu()
                                .endMenus()
                                .submenu("xxx")
                                    .menu("xx")
                                        .respondsWith( null )
                                    .endMenu()
                                .endMenus()
                            .endMenu()
                        .endMenus()
                .endMenu()
                .newTopLevelMenu("x")
                    .withRole( "" )
                    .respondsWith( null )
                .endMenu()
                .newTopLevelMenu("x")
                    .submenu("x")
                        .menu("x")
                            .respondsWith( null )
                        .endMenu()
                    .endMenus()
                .endMenu()
                .newTopLevelMenu("xx")
                    .respondsWith( null )
                .endMenu()
                .newSearchItem( "search" )
                    .respondsWith( new MenuSearchItem.SearchCommand() {
                        @Override
                        public void execute( final String term ) {
                        }
                    } )
                .endMenu()
                .newTopLevelMenu( "xx" )
                    .withItems( null )
                .endMenu()
                .newContributedMenu( "x" )
                    .contributeTo( "xx" )
                    .withRole( "" )
                    .submenu( "xx" )
                        .menu( "cc" )
                            .respondsWith( null )
                        .endMenu()
                    .endMenus()
                .endMenu()
            .build();
    }
}
