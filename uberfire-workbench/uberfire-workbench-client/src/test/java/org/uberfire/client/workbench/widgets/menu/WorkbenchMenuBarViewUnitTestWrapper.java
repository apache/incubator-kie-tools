package org.uberfire.client.workbench.widgets.menu;

import com.github.gwtbootstrap.client.ui.Nav;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Mockito.*;

public class WorkbenchMenuBarViewUnitTestWrapper extends WorkbenchMenuBarView{

    public void setupMocks(Nav menuBarLeft, Nav menuBarCenter, Nav menuBarRight){
        this.menuBarLeft = menuBarLeft;
        this.menuBarCenter =  menuBarCenter;
        this.menuBarRight = menuBarRight;

    }

    boolean notHavePermissionToMakeThis( MenuItem item ) {
        return false;
    }

    Widget makeNavLink( final MenuItem item ) {
        return mock(Widget.class);
    }

    Widget makeMenuCustom( MenuCustom item ) {
        return mock(Widget.class);
    }

    Widget makeMenuGroup( MenuGroup item ) {
        return mock(Widget.class);
    }

    Widget makeMenuItemCommand( final MenuItem item ) {
        return mock(Widget.class);
    }

}
