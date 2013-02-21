package org.uberfire.client.workbench.widgets.menu.impl;

import org.junit.Test;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.MenuFactory;
import org.uberfire.client.workbench.widgets.menu.MenuGroup;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.MenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.MenuPosition;
import org.uberfire.client.workbench.widgets.menu.Menus;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.uberfire.client.workbench.widgets.menu.MenuFactory.newTopLevelMenu;

/**
 *
 */
public class TestBehavior {

    private static Command DUMMY = new Command(){
        @Override
        public void execute(){

        }
    };

    @Test
    public void testSimpleMenu() {
        final Menus menus = newTopLevelMenu( "File" )
                        .position( MenuPosition.RIGHT )
                        .menus()
                            .menu( "Save" )
                               .order( 2 )
                               .respondsWith( DUMMY )
                            .endMenu()
                            .menu( "Close" )
                                .respondsWith( DUMMY )
                            .endMenu()
                        .endMenus()
                    .endMenu().build();

        assertThat( menus ).isNotNull();
        assertThat( menus.getItems() ).isNotNull().hasSize( 1 );
        assertThat( menus.getItems().get( 0 ) ).isNotNull();
        assertThat( menus.getItems().get( 0 ) ).isInstanceOf( MenuGroup.class );
        assertThat( menus.getItems().get( 0 ).getPosition() ).isEqualTo( MenuPosition.RIGHT );
        assertThat( menus.getItems().get( 0 ).getCaption() ).isEqualTo( "File" );
        assertThat( ((MenuGroup) menus.getItems().get( 0 )).getItems() ).hasSize( 2 );

        {
            final MenuItem menuItem = ((MenuGroup) menus.getItems().get( 0 )).getItems().get( 0 );
            assertThat( menuItem ).isNotNull();
            assertThat( menuItem.getCaption() ).isEqualTo( "Save" );
            assertThat( menuItem.getOrder() ).isEqualTo( 2 );
        }
        {
            final MenuItem menuItem = ((MenuGroup) menus.getItems().get( 0 )).getItems().get( 1 );
            assertThat( menuItem ).isNotNull();
            assertThat( menuItem.getCaption() ).isEqualTo( "Close" );
        }
    }

    @Test
    public void testSimpleNestedMenu() {
        final Menus menus = newTopLevelMenu( "File" )
                        .menus()
                            .menu( "Operations" )
                                .menus()
                                    .menu( "Save" )
                                        .respondsWith( DUMMY )
                                    .endMenu()
                                    .menu( "Close" )
                                        .respondsWith( DUMMY )
                                    .endMenu()
                                .endMenus()
                            .endMenu()
                        .endMenus()
                    .endMenu()
                    .newTopLevelMenu( "Explore" )
                        .respondsWith( DUMMY )
                    .endMenu()
                    .build();

        assertThat( menus ).isNotNull();
        assertThat( menus.getItems() ).isNotNull().hasSize( 2 );
        {
            assertThat( menus.getItems().get( 0 ) ).isNotNull();
            assertThat( menus.getItems().get( 0 ) ).isInstanceOf( MenuGroup.class );
            assertThat( menus.getItems().get( 0 ).getCaption() ).isEqualTo( "File" );
            assertThat( ((MenuGroup) menus.getItems().get( 0 )).getItems() ).hasSize( 1 );

            {
                final MenuItem menuItem = ((MenuGroup) menus.getItems().get( 0 )).getItems().get( 0 );
                assertThat( menuItem ).isNotNull();
                assertThat( menuItem.getCaption() ).isEqualTo( "Operations" );
                assertThat( menuItem ).isInstanceOf( MenuGroup.class );

                {
                    final MenuItem subMenuItem = ((MenuGroup) menuItem).getItems().get( 0 );
                    assertThat( subMenuItem ).isNotNull();
                    assertThat( subMenuItem.getCaption() ).isEqualTo( "Save" );
                }

                {
                    final MenuItem subMenuItem = ((MenuGroup) menuItem).getItems().get( 1 );
                    assertThat( subMenuItem ).isNotNull();
                    assertThat( subMenuItem.getCaption() ).isEqualTo( "Close" );
                }
            }

            assertThat( menus.getItems().get( 1 ) ).isNotNull();
            assertThat( menus.getItems().get( 1 ) ).isInstanceOf( MenuItemCommand.class );
            assertThat( menus.getItems().get( 1 ).getCaption() ).isEqualTo( "Explore" );
        }
    }
}
