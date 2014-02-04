package org.uberfire.client.workbench.widgets.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Divider;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.common.ContextDropdownButton;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.MINI;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.mock;

public class MenusFixture {


    public static Menus buildTopLevelMenu() {

        return MenuFactory.newTopLevelMenu( "RIGHT" ).position( MenuPosition.RIGHT )
                .menus()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( "CENTER" ).position( MenuPosition.CENTER )
                .menus()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( "LEFT" ).position( MenuPosition.LEFT )
                .menus()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( "RIGHT" ).position( MenuPosition.RIGHT )
                .menus()
                .endMenus()
                .endMenu()
                .build();

    }

    public static MenuItem buildCustomMenu() {
        Menus menu =  MenuFactory
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new MenuCustom<Widget>() {

                            @Override
                            public Widget build() {
                                return new ContextDropdownButton() {
                                    {
                                        displayCaret( false );
                                        setRightDropdown( true );
                                        setIcon( IconType.COG );
                                        setSize( MINI );
                                        add( new NavLink( "Business view" ) {{
                                            setIconSize( IconSize.SMALL );
                                        }} );
                                        add( new NavLink( "Tech view" ) {{
                                            setIcon( IconType.ASTERISK );
                                            setIconSize( IconSize.SMALL );
                                        }} );
                                        add( new Divider() );
                                        add( new NavLink( "Breadbrumb Explorer" ) {{
                                            setIcon( IconType.OK );
                                            setIconSize( IconSize.SMALL );
                                        }} );
                                        add( new NavLink( "Tree explorer" ) {{
                                        }} );
                                        add( new Divider() );
                                        add( new NavLink( "Flatten folders" ) {{
                                        }} );
                                        add( new NavLink( "Compact empty folders" ) {{
                                        }} );
                                        add( new NavLink( "Display hidden files" ) {{
                                        }} );
                                    }
                                };
                            }

                            @Override
                            public boolean isEnabled() {
                                return false;
                            }

                            @Override
                            public void setEnabled( boolean enabled ) {

                            }

                            @Override
                            public String getContributionPoint() {
                                return null;
                            }

                            @Override
                            public String getCaption() {
                                return null;
                            }

                            @Override
                            public MenuPosition getPosition() {
                                return null;
                            }

                            @Override
                            public int getOrder() {
                                return 0;
                            }

                            @Override
                            public void addEnabledStateChangeListener( EnabledStateChangeListener listener ) {

                            }

                            @Override
                            public String getSignatureId() {
                                return null;
                            }

                            @Override
                            public Collection<String> getRoles() {
                                return null;
                            }

                            @Override
                            public Collection<String> getTraits() {
                                return null;
                            }
                        };
                    }
                } ).endMenu().build();
        return menu.getItems().get( 0 );
    }

    public static MenuItem buildMenuGroupItem() {

        Menus menu = buildMenuGroup();
        return  menu.getItems().get( 0 );
    }

    public static Menus buildMenuGroup() {
        return MenuFactory.newTopLevelMenu( "Screens" )
                    .menus()
                    .menu( "Hello Screen" ).endMenu()
                    .menu( "Mood Screen" ).endMenu()
                    .endMenus()
                    .endMenu()
                    .newTopLevelMenu( "Perspectives" )
                    .menus()
                    .menu( "Home Perspective" ).endMenu()
                    .menu( "Horizontal Perspective" ).endMenu()
                    .endMenus()
                    .endMenu()
                    .newTopLevelMenu( "Other" )
                    .menus()
                    .menu( "Alert Box" ).endMenu()
                    .endMenus()
                    .endMenu()
                    .build();
    }

    public static MenuItemCommand buildMenuItemCommand(){
        return new MenuItemCommand() {

            private final List<EnabledStateChangeListener> enabledStateChangeListeners = new ArrayList<EnabledStateChangeListener>();
            private boolean isEnabled = true;

            @Override
            public Command getCommand() {
                return mock(Command.class);  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public String getContributionPoint() {
                return "";
            }

            @Override
            public String getCaption() {
                return "";
            }

            @Override
            public MenuPosition getPosition() {
                return mock(MenuPosition.class);
            }

            @Override
            public int getOrder() {
                return 1;
            }

            @Override
            public boolean isEnabled() {
                return isEnabled;
            }

            @Override
            public void setEnabled( final boolean enabled ) {
                this.isEnabled = enabled;
                notifyListeners( enabled );
            }

            @Override
            public void addEnabledStateChangeListener( final EnabledStateChangeListener listener ) {
                enabledStateChangeListeners.add( listener );
            }

            @Override
            public String getSignatureId() {
                return "";
            }

            @Override
            public Collection<String> getRoles() {
                return mock(Collection.class);
            }

            @Override
            public Collection<String> getTraits() {
                return emptyList();
            }

            private void notifyListeners( final boolean enabled ) {
                for ( final EnabledStateChangeListener listener : enabledStateChangeListeners ) {
                    listener.enabledStateChanged( enabled );
                }
            }
        };
    }

}
