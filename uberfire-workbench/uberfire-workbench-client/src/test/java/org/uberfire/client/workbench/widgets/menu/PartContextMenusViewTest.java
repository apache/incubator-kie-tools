package org.uberfire.client.workbench.widgets.menu;

import com.github.gwtbootstrap.client.ui.NavPills;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class PartContextMenusViewTest {

    private PartContextMenusViewUnitTestWrapper partContextMenusViewUnitTestWrapper;

    private PartContextMenusView partContextMenusViewWithoutPermission;

    private NavPills menuBar;
    private Menus menuTopLevel;

    @Before
    public void setup() {

        menuBar = GWT.create( NavPills.class );

        menuTopLevel = MenusFixture.buildTopLevelMenu();

        partContextMenusViewUnitTestWrapper = new PartContextMenusViewUnitTestWrapper();
        partContextMenusViewUnitTestWrapper.setupMocks( menuBar );

        partContextMenusViewWithoutPermission = new PartContextMenusView() {

            boolean notHavePermissionToMakeThis( MenuItem item ) {
                return true;
            }
        };

    }

    @Test
    public void simpleAddMenuItems() {
        Menus item = MenusFixture.buildMenuGroup();
        partContextMenusViewUnitTestWrapper.buildMenu( item );
        verify( menuBar, Mockito.times( item.getItems().size() ) ).add( any( Widget.class ) );
    }

    @Test
    public void makeItemWithoutPermissionShouldReturnNull() {
        assertNull( partContextMenusViewWithoutPermission.makeItem( menuTopLevel.getItems().get( 0 ) ) );
    }

    @Test
    public void makeMenuItemCommand() {
        PartContextMenusViewUnitTestWrapper spy = spy( partContextMenusViewUnitTestWrapper );
        MenuItem item = MenusFixture.buildMenuItemCommand();
        Widget widget = spy.makeItem( item );
        verify( spy, Mockito.times( 1 ) ).makeMenuItemCommand( item );

    }

    @Test
    public void makeMenuGroup() {
        PartContextMenusViewUnitTestWrapper spy = spy( partContextMenusViewUnitTestWrapper );
        MenuItem item = MenusFixture.buildMenuGroupItem();
        Widget widget = spy.makeItem( item );
        verify( spy, Mockito.times( 1 ) ).makeMenuGroup( (MenuGroup) item );

    }
    @Test
    public void otherTypesOfMenusShouldReturnNull() {
        Menus item = MenusFixture.buildTopLevelMenu();
        partContextMenusViewUnitTestWrapper.buildMenu( item );
        verify( menuBar, never()).add( any( Widget.class ) );
    }

}