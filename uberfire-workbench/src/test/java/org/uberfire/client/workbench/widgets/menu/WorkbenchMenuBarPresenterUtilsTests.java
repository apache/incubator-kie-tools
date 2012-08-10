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

import java.util.List;

import org.junit.Test;

/**
 * Tests for WorkbenchMenuBarPresenterUtils
 */
public class WorkbenchMenuBarPresenterUtilsTests {

    private final Command mockCommand = mock( Command.class );

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabled() {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        final CommandMenuItem m0i0 = new CommandMenuItem( "m0i0",
                                                          mockCommand );
        final CommandMenuItem m0i1 = new CommandMenuItem( "m0i1",
                                                          mockCommand );
        final CommandMenuItem m0i2 = new CommandMenuItem( "m0i2",
                                                          mockCommand );
        menuBar.addItem( m0i0 );
        menuBar.addItem( m0i1 );
        menuBar.addItem( m0i2 );

        List<AbstractMenuItem> items = WorkbenchMenuBarPresenterUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 3,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertEquals( "m0i1",
                      items.get( 1 ).getCaption() );
        assertEquals( "m0i2",
                      items.get( 2 ).getCaption() );
        assertTrue( items.get( 0 ).hasPermission() );
        assertTrue( items.get( 1 ).hasPermission() );
        assertTrue( items.get( 2 ).hasPermission() );
        assertTrue( items.get( 0 ).isEnabled() );
        assertTrue( items.get( 1 ).isEnabled() );
        assertTrue( items.get( 2 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllNotGrantedAllEnabled() {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        final CommandMenuItem m0i0 = new CommandMenuItem( "m0i0",
                                                          mockCommand );
        final CommandMenuItem m0i1 = new CommandMenuItem( "m0i1",
                                                          mockCommand );
        final CommandMenuItem m0i2 = new CommandMenuItem( "m0i2",
                                                          mockCommand );
        m0i0.setHasPermission( false );
        m0i1.setHasPermission( false );
        m0i2.setHasPermission( false );
        menuBar.addItem( m0i0 );
        menuBar.addItem( m0i1 );
        menuBar.addItem( m0i2 );

        List<AbstractMenuItem> items = WorkbenchMenuBarPresenterUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 0,
                      items.size() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllDisabled() {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        final CommandMenuItem m0i0 = new CommandMenuItem( "m0i0",
                                                          mockCommand );
        final CommandMenuItem m0i1 = new CommandMenuItem( "m0i1",
                                                          mockCommand );
        final CommandMenuItem m0i2 = new CommandMenuItem( "m0i2",
                                                          mockCommand );
        m0i0.setEnabled( false );
        m0i1.setEnabled( false );
        m0i2.setEnabled( false );
        menuBar.addItem( m0i0 );
        menuBar.addItem( m0i1 );
        menuBar.addItem( m0i2 );

        List<AbstractMenuItem> items = WorkbenchMenuBarPresenterUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 3,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertEquals( "m0i1",
                      items.get( 1 ).getCaption() );
        assertEquals( "m0i2",
                      items.get( 2 ).getCaption() );
        assertTrue( items.get( 0 ).hasPermission() );
        assertTrue( items.get( 1 ).hasPermission() );
        assertTrue( items.get( 2 ).hasPermission() );
        assertFalse( items.get( 0 ).isEnabled() );
        assertFalse( items.get( 1 ).isEnabled() );
        assertFalse( items.get( 2 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0FirstNotGrantedAllEnabled() {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        final CommandMenuItem m0i0 = new CommandMenuItem( "m0i0",
                                                          mockCommand );
        final CommandMenuItem m0i1 = new CommandMenuItem( "m0i1",
                                                          mockCommand );
        final CommandMenuItem m0i2 = new CommandMenuItem( "m0i2",
                                                          mockCommand );
        m0i0.setHasPermission( false );
        menuBar.addItem( m0i0 );
        menuBar.addItem( m0i1 );
        menuBar.addItem( m0i2 );

        List<AbstractMenuItem> items = WorkbenchMenuBarPresenterUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 2,
                      items.size() );
        assertEquals( "m0i1",
                      items.get( 0 ).getCaption() );
        assertEquals( "m0i2",
                      items.get( 1 ).getCaption() );
        assertTrue( items.get( 0 ).hasPermission() );
        assertTrue( items.get( 1 ).hasPermission() );
        assertTrue( items.get( 0 ).isEnabled() );
        assertTrue( items.get( 1 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllGrantedAllEnabled() {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        final WorkbenchMenuBar subMenuBar = new WorkbenchMenuBar();

        final SubMenuItem m0i0 = new SubMenuItem( "m0i0",
                                                  subMenuBar );
        menuBar.addItem( m0i0 );

        final CommandMenuItem m1i0 = new CommandMenuItem( "m1i0",
                                                          mockCommand );
        final CommandMenuItem m1i1 = new CommandMenuItem( "m1i1",
                                                          mockCommand );
        final CommandMenuItem m1i2 = new CommandMenuItem( "m1i2",
                                                          mockCommand );
        subMenuBar.addItem( m1i0 );
        subMenuBar.addItem( m1i1 );
        subMenuBar.addItem( m1i2 );

        List<AbstractMenuItem> items = WorkbenchMenuBarPresenterUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).hasPermission() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof SubMenuItem );
        final SubMenuItem m1i0Clone = (SubMenuItem) items.get( 0 );
        assertEquals( 3,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i0",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).hasPermission() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );
        assertEquals( "m1i1",
                      m1i0Clone.getSubMenu().getItems().get( 1 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 1 ).hasPermission() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 1 ).isEnabled() );
        assertEquals( "m1i2",
                      m1i0Clone.getSubMenu().getItems().get( 2 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 2 ).hasPermission() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 2 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllNotGrantedAllEnabled() {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        final WorkbenchMenuBar subMenuBar = new WorkbenchMenuBar();

        final SubMenuItem m0i0 = new SubMenuItem( "m0i0",
                                                  subMenuBar );
        menuBar.addItem( m0i0 );

        final CommandMenuItem m1i0 = new CommandMenuItem( "m1i0",
                                                          mockCommand );
        final CommandMenuItem m1i1 = new CommandMenuItem( "m1i1",
                                                          mockCommand );
        final CommandMenuItem m1i2 = new CommandMenuItem( "m1i2",
                                                          mockCommand );
        m1i0.setHasPermission( false );
        m1i1.setHasPermission( false );
        m1i2.setHasPermission( false );
        subMenuBar.addItem( m1i0 );
        subMenuBar.addItem( m1i1 );
        subMenuBar.addItem( m1i2 );

        List<AbstractMenuItem> items = WorkbenchMenuBarPresenterUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).hasPermission() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof SubMenuItem );
        final SubMenuItem m1i0Clone = (SubMenuItem) items.get( 0 );
        assertEquals( 0,
                      m1i0Clone.getSubMenu().getItems().size() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllGrantedAllDisabled() {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        final WorkbenchMenuBar subMenuBar = new WorkbenchMenuBar();

        final SubMenuItem m0i0 = new SubMenuItem( "m0i0",
                                                  subMenuBar );
        menuBar.addItem( m0i0 );

        final CommandMenuItem m1i0 = new CommandMenuItem( "m1i0",
                                                          mockCommand );
        final CommandMenuItem m1i1 = new CommandMenuItem( "m1i1",
                                                          mockCommand );
        final CommandMenuItem m1i2 = new CommandMenuItem( "m1i2",
                                                          mockCommand );
        m1i0.setEnabled( false );
        m1i1.setEnabled( false );
        m1i2.setEnabled( false );
        subMenuBar.addItem( m1i0 );
        subMenuBar.addItem( m1i1 );
        subMenuBar.addItem( m1i2 );

        List<AbstractMenuItem> items = WorkbenchMenuBarPresenterUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).hasPermission() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof SubMenuItem );
        final SubMenuItem m1i0Clone = (SubMenuItem) items.get( 0 );
        assertEquals( 3,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i0",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).hasPermission() );
        assertFalse( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );
        assertEquals( "m1i1",
                      m1i0Clone.getSubMenu().getItems().get( 1 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 1 ).hasPermission() );
        assertFalse( m1i0Clone.getSubMenu().getItems().get( 1 ).isEnabled() );
        assertEquals( "m1i2",
                      m1i0Clone.getSubMenu().getItems().get( 2 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 2 ).hasPermission() );
        assertFalse( m1i0Clone.getSubMenu().getItems().get( 2 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1FirstNotGrantedAllEnabled() {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        final WorkbenchMenuBar subMenuBar = new WorkbenchMenuBar();

        final SubMenuItem m0i0 = new SubMenuItem( "m0i0",
                                                  subMenuBar );
        menuBar.addItem( m0i0 );

        final CommandMenuItem m1i0 = new CommandMenuItem( "m1i0",
                                                          mockCommand );
        final CommandMenuItem m1i1 = new CommandMenuItem( "m1i1",
                                                          mockCommand );
        final CommandMenuItem m1i2 = new CommandMenuItem( "m1i2",
                                                          mockCommand );
        m1i0.setHasPermission( false );
        subMenuBar.addItem( m1i0 );
        subMenuBar.addItem( m1i1 );
        subMenuBar.addItem( m1i2 );

        List<AbstractMenuItem> items = WorkbenchMenuBarPresenterUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).hasPermission() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof SubMenuItem );
        final SubMenuItem m1i0Clone = (SubMenuItem) items.get( 0 );
        assertEquals( 2,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i1",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).hasPermission() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );
        assertEquals( "m1i2",
                      m1i0Clone.getSubMenu().getItems().get( 1 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 1 ).hasPermission() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 1 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllGrantedAllEnabledLevel2AllGrantedAllEnabled() {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        final WorkbenchMenuBar subMenuBar0 = new WorkbenchMenuBar();
        final WorkbenchMenuBar subMenuBar1 = new WorkbenchMenuBar();

        final SubMenuItem m0i0 = new SubMenuItem( "m0i0",
                                                  subMenuBar0 );
        menuBar.addItem( m0i0 );

        final SubMenuItem m1i0 = new SubMenuItem( "m1i0",
                                                  subMenuBar1 );
        subMenuBar0.addItem( m1i0 );
        final CommandMenuItem m2i0 = new CommandMenuItem( "m2i0",
                                                          mockCommand );
        final CommandMenuItem m2i1 = new CommandMenuItem( "m2i1",
                                                          mockCommand );
        final CommandMenuItem m2i2 = new CommandMenuItem( "m2i2",
                                                          mockCommand );
        subMenuBar1.addItem( m2i0 );
        subMenuBar1.addItem( m2i1 );
        subMenuBar1.addItem( m2i2 );

        List<AbstractMenuItem> items = WorkbenchMenuBarPresenterUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).hasPermission() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof SubMenuItem );
        final SubMenuItem m1i0Clone = (SubMenuItem) items.get( 0 );
        assertEquals( 1,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i0",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).hasPermission() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );

        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ) instanceof SubMenuItem );
        final SubMenuItem m2i0Clone = (SubMenuItem) m1i0Clone.getSubMenu().getItems().get( 0 );
        assertEquals( 3,
                      m2i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m2i0",
                      m2i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 0 ).hasPermission() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );
        assertEquals( "m2i1",
                      m2i0Clone.getSubMenu().getItems().get( 1 ).getCaption() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 1 ).hasPermission() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 1 ).isEnabled() );
        assertEquals( "m2i2",
                      m2i0Clone.getSubMenu().getItems().get( 2 ).getCaption() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 2 ).hasPermission() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 2 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllGrantedAllEnabledLevel2AllNotGrantedAllEnabled() {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        final WorkbenchMenuBar subMenuBar0 = new WorkbenchMenuBar();
        final WorkbenchMenuBar subMenuBar1 = new WorkbenchMenuBar();

        final SubMenuItem m0i0 = new SubMenuItem( "m0i0",
                                                  subMenuBar0 );
        menuBar.addItem( m0i0 );

        final SubMenuItem m1i0 = new SubMenuItem( "m1i0",
                                                  subMenuBar1 );
        subMenuBar0.addItem( m1i0 );
        final CommandMenuItem m2i0 = new CommandMenuItem( "m2i0",
                                                          mockCommand );
        final CommandMenuItem m2i1 = new CommandMenuItem( "m2i1",
                                                          mockCommand );
        final CommandMenuItem m2i2 = new CommandMenuItem( "m2i2",
                                                          mockCommand );
        m2i0.setHasPermission( false );
        m2i1.setHasPermission( false );
        m2i2.setHasPermission( false );
        subMenuBar1.addItem( m2i0 );
        subMenuBar1.addItem( m2i1 );
        subMenuBar1.addItem( m2i2 );

        List<AbstractMenuItem> items = WorkbenchMenuBarPresenterUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).hasPermission() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof SubMenuItem );
        final SubMenuItem m1i0Clone = (SubMenuItem) items.get( 0 );
        assertEquals( 1,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i0",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).hasPermission() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );

        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ) instanceof SubMenuItem );
        final SubMenuItem m2i0Clone = (SubMenuItem) m1i0Clone.getSubMenu().getItems().get( 0 );
        assertEquals( 0,
                      m2i0Clone.getSubMenu().getItems().size() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllGrantedAllEnabledLevel2AllGrantedAllDisabled() {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        final WorkbenchMenuBar subMenuBar0 = new WorkbenchMenuBar();
        final WorkbenchMenuBar subMenuBar1 = new WorkbenchMenuBar();

        final SubMenuItem m0i0 = new SubMenuItem( "m0i0",
                                                  subMenuBar0 );
        menuBar.addItem( m0i0 );

        final SubMenuItem m1i0 = new SubMenuItem( "m1i0",
                                                  subMenuBar1 );
        subMenuBar0.addItem( m1i0 );
        final CommandMenuItem m2i0 = new CommandMenuItem( "m2i0",
                                                          mockCommand );
        final CommandMenuItem m2i1 = new CommandMenuItem( "m2i1",
                                                          mockCommand );
        final CommandMenuItem m2i2 = new CommandMenuItem( "m2i2",
                                                          mockCommand );
        m2i0.setEnabled( false );
        m2i1.setEnabled( false );
        m2i2.setEnabled( false );
        subMenuBar1.addItem( m2i0 );
        subMenuBar1.addItem( m2i1 );
        subMenuBar1.addItem( m2i2 );

        List<AbstractMenuItem> items = WorkbenchMenuBarPresenterUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).hasPermission() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof SubMenuItem );
        final SubMenuItem m1i0Clone = (SubMenuItem) items.get( 0 );
        assertEquals( 1,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i0",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).hasPermission() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );

        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ) instanceof SubMenuItem );
        final SubMenuItem m2i0Clone = (SubMenuItem) m1i0Clone.getSubMenu().getItems().get( 0 );
        assertEquals( 3,
                      m2i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m2i0",
                      m2i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 0 ).hasPermission() );
        assertFalse( m2i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );
        assertEquals( "m2i1",
                      m2i0Clone.getSubMenu().getItems().get( 1 ).getCaption() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 1 ).hasPermission() );
        assertFalse( m2i0Clone.getSubMenu().getItems().get( 1 ).isEnabled() );
        assertEquals( "m2i2",
                      m2i0Clone.getSubMenu().getItems().get( 2 ).getCaption() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 2 ).hasPermission() );
        assertFalse( m2i0Clone.getSubMenu().getItems().get( 2 ).isEnabled() );
    }

    @Test
    public void testFilterPermissionsLevel0AllGrantedAllEnabledLevel1AllGrantedAllEnabledLevel2FirstNotGrantedAllEnabled() {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        final WorkbenchMenuBar subMenuBar0 = new WorkbenchMenuBar();
        final WorkbenchMenuBar subMenuBar1 = new WorkbenchMenuBar();

        final SubMenuItem m0i0 = new SubMenuItem( "m0i0",
                                                  subMenuBar0 );
        menuBar.addItem( m0i0 );

        final SubMenuItem m1i0 = new SubMenuItem( "m1i0",
                                                  subMenuBar1 );
        subMenuBar0.addItem( m1i0 );
        final CommandMenuItem m2i0 = new CommandMenuItem( "m2i0",
                                                          mockCommand );
        final CommandMenuItem m2i1 = new CommandMenuItem( "m2i1",
                                                          mockCommand );
        final CommandMenuItem m2i2 = new CommandMenuItem( "m2i2",
                                                          mockCommand );
        m2i0.setHasPermission( false );
        subMenuBar1.addItem( m2i0 );
        subMenuBar1.addItem( m2i1 );
        subMenuBar1.addItem( m2i2 );

        List<AbstractMenuItem> items = WorkbenchMenuBarPresenterUtils.filterMenuItemsByPermission( menuBar.getItems() );

        assertEquals( 1,
                      items.size() );
        assertEquals( "m0i0",
                      items.get( 0 ).getCaption() );
        assertTrue( items.get( 0 ).hasPermission() );
        assertTrue( items.get( 0 ).isEnabled() );

        assertTrue( items.get( 0 ) instanceof SubMenuItem );
        final SubMenuItem m1i0Clone = (SubMenuItem) items.get( 0 );
        assertEquals( 1,
                      m1i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m1i0",
                      m1i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).hasPermission() );
        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );

        assertTrue( m1i0Clone.getSubMenu().getItems().get( 0 ) instanceof SubMenuItem );
        final SubMenuItem m2i0Clone = (SubMenuItem) m1i0Clone.getSubMenu().getItems().get( 0 );
        assertEquals( 2,
                      m2i0Clone.getSubMenu().getItems().size() );
        assertEquals( "m2i1",
                      m2i0Clone.getSubMenu().getItems().get( 0 ).getCaption() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 0 ).hasPermission() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 0 ).isEnabled() );
        assertEquals( "m2i2",
                      m2i0Clone.getSubMenu().getItems().get( 1 ).getCaption() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 1 ).hasPermission() );
        assertTrue( m2i0Clone.getSubMenu().getItems().get( 1 ).isEnabled() );
    }

}
