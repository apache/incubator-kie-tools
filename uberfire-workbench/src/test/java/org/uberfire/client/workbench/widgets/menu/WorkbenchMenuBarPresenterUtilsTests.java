/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.widgets.menu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.kie.commons.validation.PortablePreconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemSubMenu;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

/**
 * Tests for WorkbenchMenuBarPresenterUtils
 */
public class WorkbenchMenuBarPresenterUtilsTests {

    private Command                        mockCommand       = mock( Command.class );

    private WorkbenchMenuBarPresenterUtils menuBarUtils;

    private String[]                       PERMISSIONS_ADMIN = new String[]{"ADMIN"};

    @Before
    public void setupMenuBarPresenterUtils() {
        final Identity principle = new Identity() {

            private static final long serialVersionUID = 1L;

            private String            NAME             = "user";

            private List<Role>        ROLES;
            {
                ROLES = new ArrayList<Role>();
                ROLES.add( new Role() {

                    @Override
                    public String getName() {
                        return "user-permissions";
                    }

                } );
            }

            @Override
            public String getName() {
                return NAME;
            }

            @Override
            public List<Role> getRoles() {
                return ROLES;
            }

            @Override
            public boolean hasRole(final Role role) {
                checkNotNull("role", role);
                for (final Role activeRole : ROLES) {
                    if (activeRole.getName().equals(role.getName())){
                        return true;
                    }
                }
                return false;
            }

        };
        menuBarUtils = new WorkbenchMenuBarPresenterUtils( new RuntimeAuthorizationManager(),
                                                           principle );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabled() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuItemCommand m0i0 = new DefaultMenuItemCommand( "m0i0",
                                                                 mockCommand );
        final MenuItemCommand m0i1 = new DefaultMenuItemCommand( "m0i1",
                                                                 mockCommand );
        final MenuItemCommand m0i2 = new DefaultMenuItemCommand( "m0i2",
                                                                 mockCommand );
        menuBar.addItem( m0i0 );
        menuBar.addItem( m0i1 );
        menuBar.addItem( m0i2 );

        List<MenuItem> items = menuBarUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 3,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertEquals( "m0i1",
                      items.get( 1 ).getCaption() );
        assertEquals( "m0i2",
                      items.get( 2 ).getCaption() );
        assertTrue( items.get( 0 ).isEnabled() );
        assertTrue( items.get( 1 ).isEnabled() );
        assertTrue( items.get( 2 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllNotGrantedAllEnabled() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuItemCommand m0i0 = new DefaultMenuItemCommand( "m0i0",
                                                                 mockCommand );
        final MenuItemCommand m0i1 = new DefaultMenuItemCommand( "m0i1",
                                                                 mockCommand );
        final MenuItemCommand m0i2 = new DefaultMenuItemCommand( "m0i2",
                                                                 mockCommand );
        m0i0.setRoles( PERMISSIONS_ADMIN );
        m0i1.setRoles( PERMISSIONS_ADMIN );
        m0i2.setRoles( PERMISSIONS_ADMIN );
        menuBar.addItem( m0i0 );
        menuBar.addItem( m0i1 );
        menuBar.addItem( m0i2 );

        List<MenuItem> items = menuBarUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 0,
                      items.size() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllDisabled() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuItemCommand m0i0 = new DefaultMenuItemCommand( "m0i0",
                                                                 mockCommand );
        final MenuItemCommand m0i1 = new DefaultMenuItemCommand( "m0i1",
                                                                 mockCommand );
        final MenuItemCommand m0i2 = new DefaultMenuItemCommand( "m0i2",
                                                                 mockCommand );
        m0i0.setEnabled( false );
        m0i1.setEnabled( false );
        m0i2.setEnabled( false );
        menuBar.addItem( m0i0 );
        menuBar.addItem( m0i1 );
        menuBar.addItem( m0i2 );

        List<MenuItem> items = menuBarUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 3,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertEquals( "m0i1",
                      items.get( 1 ).getCaption() );
        assertEquals( "m0i2",
                      items.get( 2 ).getCaption() );
        assertFalse( items.get( 0 ).isEnabled() );
        assertFalse( items.get( 1 ).isEnabled() );
        assertFalse( items.get( 2 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0FirstNotGrantedAllEnabled() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuItemCommand m0i0 = new DefaultMenuItemCommand( "m0i0",
                                                                 mockCommand );
        final MenuItemCommand m0i1 = new DefaultMenuItemCommand( "m0i1",
                                                                 mockCommand );
        final MenuItemCommand m0i2 = new DefaultMenuItemCommand( "m0i2",
                                                                 mockCommand );
        m0i0.setRoles( PERMISSIONS_ADMIN );
        menuBar.addItem( m0i0 );
        menuBar.addItem( m0i1 );
        menuBar.addItem( m0i2 );

        List<MenuItem> items = menuBarUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 2,
                      items.size() );
        assertEquals( "m0i1",
                      items.get( 0 ).getCaption() );
        assertEquals( "m0i2",
                      items.get( 1 ).getCaption() );
        assertTrue( items.get( 0 ).isEnabled() );
        assertTrue( items.get( 1 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllGrantedAllEnabled() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuBar subMenuBar = new DefaultMenuBar();

        final MenuItemSubMenu m0i0 = new DefaultMenuItemSubMenu( "m0i0",
                                                                 subMenuBar );
        menuBar.addItem( m0i0 );

        final MenuItemCommand m1i0 = new DefaultMenuItemCommand( "m1i0",
                                                                 mockCommand );
        final MenuItemCommand m1i1 = new DefaultMenuItemCommand( "m1i1",
                                                                 mockCommand );
        final MenuItemCommand m1i2 = new DefaultMenuItemCommand( "m1i2",
                                                                 mockCommand );
        subMenuBar.addItem( m1i0 );
        subMenuBar.addItem( m1i1 );
        subMenuBar.addItem( m1i2 );

        List<MenuItem> items = menuBarUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof MenuItemSubMenu );
        final MenuItemSubMenu m1i0Clone = (MenuItemSubMenu) items.get( 0 );
        assertEquals( 3,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i0",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );
        assertEquals( "m1i1",
                      m1i0Clone.getSubMenu().getItems().get( 1 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 1 ).isEnabled() );
        assertEquals( "m1i2",
                      m1i0Clone.getSubMenu().getItems().get( 2 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 2 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllNotGrantedAllEnabled() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuBar subMenuBar = new DefaultMenuBar();

        final MenuItemSubMenu m0i0 = new DefaultMenuItemSubMenu( "m0i0",
                                                                 subMenuBar );
        menuBar.addItem( m0i0 );

        final MenuItemCommand m1i0 = new DefaultMenuItemCommand( "m1i0",
                                                                 mockCommand );
        final MenuItemCommand m1i1 = new DefaultMenuItemCommand( "m1i1",
                                                                 mockCommand );
        final MenuItemCommand m1i2 = new DefaultMenuItemCommand( "m1i2",
                                                                 mockCommand );
        m1i0.setRoles( PERMISSIONS_ADMIN );
        m1i1.setRoles( PERMISSIONS_ADMIN );
        m1i2.setRoles( PERMISSIONS_ADMIN );
        subMenuBar.addItem( m1i0 );
        subMenuBar.addItem( m1i1 );
        subMenuBar.addItem( m1i2 );

        List<MenuItem> items = menuBarUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof MenuItemSubMenu );
        final MenuItemSubMenu m1i0Clone = (MenuItemSubMenu) items.get( 0 );
        assertEquals( 0,
                      m1i0Clone.getSubMenu().getItems().size() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllGrantedAllDisabled() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuBar subMenuBar = new DefaultMenuBar();

        final MenuItemSubMenu m0i0 = new DefaultMenuItemSubMenu( "m0i0",
                                                                 subMenuBar );
        menuBar.addItem( m0i0 );

        final MenuItemCommand m1i0 = new DefaultMenuItemCommand( "m1i0",
                                                                 mockCommand );
        final MenuItemCommand m1i1 = new DefaultMenuItemCommand( "m1i1",
                                                                 mockCommand );
        final MenuItemCommand m1i2 = new DefaultMenuItemCommand( "m1i2",
                                                                 mockCommand );
        m1i0.setEnabled( false );
        m1i1.setEnabled( false );
        m1i2.setEnabled( false );
        subMenuBar.addItem( m1i0 );
        subMenuBar.addItem( m1i1 );
        subMenuBar.addItem( m1i2 );

        List<MenuItem> items = menuBarUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof MenuItemSubMenu );
        final MenuItemSubMenu m1i0Clone = (MenuItemSubMenu) items.get( 0 );
        assertEquals( 3,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i0",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertFalse( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );
        assertEquals( "m1i1",
                      m1i0Clone.getSubMenu().getItems().get( 1 ).getCaption() );
        assertFalse( m1i0Clone.getSubMenu().getItems().get( 1 ).isEnabled() );
        assertEquals( "m1i2",
                      m1i0Clone.getSubMenu().getItems().get( 2 ).getCaption() );
        assertFalse( m1i0Clone.getSubMenu().getItems().get( 2 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1FirstNotGrantedAllEnabled() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuBar subMenuBar = new DefaultMenuBar();

        final MenuItemSubMenu m0i0 = new DefaultMenuItemSubMenu( "m0i0",
                                                                 subMenuBar );
        menuBar.addItem( m0i0 );

        final MenuItemCommand m1i0 = new DefaultMenuItemCommand( "m1i0",
                                                                 mockCommand );
        final MenuItemCommand m1i1 = new DefaultMenuItemCommand( "m1i1",
                                                                 mockCommand );
        final MenuItemCommand m1i2 = new DefaultMenuItemCommand( "m1i2",
                                                                 mockCommand );
        m1i0.setRoles( PERMISSIONS_ADMIN );
        subMenuBar.addItem( m1i0 );
        subMenuBar.addItem( m1i1 );
        subMenuBar.addItem( m1i2 );

        List<MenuItem> items = menuBarUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof MenuItemSubMenu );
        final MenuItemSubMenu m1i0Clone = (MenuItemSubMenu) items.get( 0 );
        assertEquals( 2,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i1",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );
        assertEquals( "m1i2",
                      m1i0Clone.getSubMenu().getItems().get( 1 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 1 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllGrantedAllEnabledLevel2AllGrantedAllEnabled() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuBar subMenuBar0 = new DefaultMenuBar();
        final MenuBar subMenuBar1 = new DefaultMenuBar();

        final MenuItemSubMenu m0i0 = new DefaultMenuItemSubMenu( "m0i0",
                                                                 subMenuBar0 );
        menuBar.addItem( m0i0 );

        final MenuItemSubMenu m1i0 = new DefaultMenuItemSubMenu( "m1i0",
                                                                 subMenuBar1 );
        subMenuBar0.addItem( m1i0 );
        final MenuItemCommand m2i0 = new DefaultMenuItemCommand( "m2i0",
                                                                 mockCommand );
        final MenuItemCommand m2i1 = new DefaultMenuItemCommand( "m2i1",
                                                                 mockCommand );
        final MenuItemCommand m2i2 = new DefaultMenuItemCommand( "m2i2",
                                                                 mockCommand );
        subMenuBar1.addItem( m2i0 );
        subMenuBar1.addItem( m2i1 );
        subMenuBar1.addItem( m2i2 );

        List<MenuItem> items = menuBarUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof MenuItemSubMenu );
        final MenuItemSubMenu m1i0Clone = (MenuItemSubMenu) items.get( 0 );
        assertEquals( 1,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i0",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );

        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ) instanceof MenuItemSubMenu );
        final MenuItemSubMenu m2i0Clone = (MenuItemSubMenu) m1i0Clone.getSubMenu().getItems().get( 0 );
        assertEquals( 3,
                      m2i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m2i0",
                      m2i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );
        assertEquals( "m2i1",
                      m2i0Clone.getSubMenu().getItems().get( 1 ).getCaption() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 1 ).isEnabled() );
        assertEquals( "m2i2",
                      m2i0Clone.getSubMenu().getItems().get( 2 ).getCaption() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 2 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllGrantedAllEnabledLevel2AllNotGrantedAllEnabled() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuBar subMenuBar0 = new DefaultMenuBar();
        final MenuBar subMenuBar1 = new DefaultMenuBar();

        final MenuItemSubMenu m0i0 = new DefaultMenuItemSubMenu( "m0i0",
                                                                 subMenuBar0 );
        menuBar.addItem( m0i0 );

        final MenuItemSubMenu m1i0 = new DefaultMenuItemSubMenu( "m1i0",
                                                                 subMenuBar1 );
        subMenuBar0.addItem( m1i0 );
        final MenuItemCommand m2i0 = new DefaultMenuItemCommand( "m2i0",
                                                                 mockCommand );
        final MenuItemCommand m2i1 = new DefaultMenuItemCommand( "m2i1",
                                                                 mockCommand );
        final MenuItemCommand m2i2 = new DefaultMenuItemCommand( "m2i2",
                                                                 mockCommand );
        m2i0.setRoles( PERMISSIONS_ADMIN );
        m2i1.setRoles( PERMISSIONS_ADMIN );
        m2i2.setRoles( PERMISSIONS_ADMIN );
        subMenuBar1.addItem( m2i0 );
        subMenuBar1.addItem( m2i1 );
        subMenuBar1.addItem( m2i2 );

        List<MenuItem> items = menuBarUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof MenuItemSubMenu );
        final MenuItemSubMenu m1i0Clone = (MenuItemSubMenu) items.get( 0 );
        assertEquals( 1,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i0",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );

        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ) instanceof MenuItemSubMenu );
        final MenuItemSubMenu m2i0Clone = (MenuItemSubMenu) m1i0Clone.getSubMenu().getItems().get( 0 );
        assertEquals( 0,
                      m2i0Clone.getSubMenu().getItems().size() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllGrantedAllEnabledLevel2AllGrantedAllDisabled() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuBar subMenuBar0 = new DefaultMenuBar();
        final MenuBar subMenuBar1 = new DefaultMenuBar();

        final MenuItemSubMenu m0i0 = new DefaultMenuItemSubMenu( "m0i0",
                                                                 subMenuBar0 );
        menuBar.addItem( m0i0 );

        final MenuItemSubMenu m1i0 = new DefaultMenuItemSubMenu( "m1i0",
                                                                 subMenuBar1 );
        subMenuBar0.addItem( m1i0 );
        final MenuItemCommand m2i0 = new DefaultMenuItemCommand( "m2i0",
                                                                 mockCommand );
        final MenuItemCommand m2i1 = new DefaultMenuItemCommand( "m2i1",
                                                                 mockCommand );
        final MenuItemCommand m2i2 = new DefaultMenuItemCommand( "m2i2",
                                                                 mockCommand );
        m2i0.setEnabled( false );
        m2i1.setEnabled( false );
        m2i2.setEnabled( false );
        subMenuBar1.addItem( m2i0 );
        subMenuBar1.addItem( m2i1 );
        subMenuBar1.addItem( m2i2 );

        List<MenuItem> items = menuBarUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof MenuItemSubMenu );
        final MenuItemSubMenu m1i0Clone = (MenuItemSubMenu) items.get( 0 );
        assertEquals( 1,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i0",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );

        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ) instanceof MenuItemSubMenu );
        final MenuItemSubMenu m2i0Clone = (MenuItemSubMenu) m1i0Clone.getSubMenu().getItems().get( 0 );
        assertEquals( 3,
                      m2i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m2i0",
                      m2i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertFalse( m2i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );
        assertEquals( "m2i1",
                      m2i0Clone.getSubMenu().getItems().get( 1 ).getCaption() );
        assertFalse( m2i0Clone.getSubMenu().getItems().get( 1 ).isEnabled() );
        assertEquals( "m2i2",
                      m2i0Clone.getSubMenu().getItems().get( 2 ).getCaption() );
        assertFalse( m2i0Clone.getSubMenu().getItems().get( 2 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllGrantedAllEnabledLevel2FirstNotGrantedAllEnabled() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuBar subMenuBar0 = new DefaultMenuBar();
        final MenuBar subMenuBar1 = new DefaultMenuBar();

        final MenuItemSubMenu m0i0 = new DefaultMenuItemSubMenu( "m0i0",
                                                                 subMenuBar0 );
        menuBar.addItem( m0i0 );

        final MenuItemSubMenu m1i0 = new DefaultMenuItemSubMenu( "m1i0",
                                                                 subMenuBar1 );
        subMenuBar0.addItem( m1i0 );
        final MenuItemCommand m2i0 = new DefaultMenuItemCommand( "m2i0",
                                                                 mockCommand );
        final MenuItemCommand m2i1 = new DefaultMenuItemCommand( "m2i1",
                                                                 mockCommand );
        final MenuItemCommand m2i2 = new DefaultMenuItemCommand( "m2i2",
                                                                 mockCommand );
        m2i0.setRoles( PERMISSIONS_ADMIN );
        subMenuBar1.addItem( m2i0 );
        subMenuBar1.addItem( m2i1 );
        subMenuBar1.addItem( m2i2 );

        List<MenuItem> items = menuBarUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof MenuItemSubMenu );
        final MenuItemSubMenu m1i0Clone = (MenuItemSubMenu) items.get( 0 );
        assertEquals( 1,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i0",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );

        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ) instanceof MenuItemSubMenu );
        final MenuItemSubMenu m2i0Clone = (MenuItemSubMenu) m1i0Clone.getSubMenu().getItems().get( 0 );
        assertEquals( 2,
                      m2i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m2i1",
                      m2i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );
        assertEquals( "m2i2",
                      m2i0Clone.getSubMenu().getItems().get( 1 ).getCaption() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 1 ).isEnabled() );
    }

}
