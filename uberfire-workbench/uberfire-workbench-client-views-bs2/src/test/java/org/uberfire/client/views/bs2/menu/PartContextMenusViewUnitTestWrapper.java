package org.uberfire.client.views.bs2.menu;

import com.github.gwtbootstrap.client.ui.NavPills;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Mockito.*;

public class PartContextMenusViewUnitTestWrapper extends PartContextMenusView{

    public void setupMocks(NavPills menuBar){
        this.menuBar = menuBar;
    }

    @Override
    Widget makeMenuGroup( MenuGroup item ) {
        return mock(Widget.class);
    }
    @Override
    Widget makeMenuItemCommand( final MenuItem item ) {
        return mock(Widget.class);
    }

    boolean notHavePermissionToMakeThis( MenuItem item ) {
        return false;
    }

}
