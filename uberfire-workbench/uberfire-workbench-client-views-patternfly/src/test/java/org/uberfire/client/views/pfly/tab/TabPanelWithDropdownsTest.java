/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.views.pfly.tab;

import org.gwtbootstrap3.client.GwtBootstrap3EntryPoint;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.uberfire.client.views.pfly.mock.CountingTabShowHandler;
import org.uberfire.client.views.pfly.mock.CountingTabShownHandler;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class TabPanelWithDropdownsTest extends AbstractErraiCDITest {

    private TabPanelWithDropdowns tabPanel;

    @Override
    public String getModuleName() {
        return "org.uberfire.client.views.pfly.PatternFlyTabTests";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        tabPanel = new TabPanelWithDropdowns();
        tabPanel.init();
        
        new GwtBootstrap3EntryPoint().onModuleLoad();
    }

    public void testAddTabByTitleAndContent() throws Exception {
        Label content = new Label( "First tab's content" );
        TabPanelEntry item = tabPanel.addItem( "First Tab", content );

        assertNotNull( item.getTabWidget() );
        assertNotNull( item.getContents() );
        assertEquals( item.getTitle(), "First Tab" );

        // the content should be attached
        assertNotNull( content.getParent() );
    }

    public void testShowTab() throws Exception {
        RootPanel.get().add( tabPanel );

        TabPanelEntry item1 = tabPanel.addItem( "First Tab", new Label( "First tab's content" ) );
        TabPanelEntry item2 = tabPanel.addItem( "Second Tab", new Label( "Second tab's content" ) );

        item2.showTab();
        item1.showTab();

        assertTrue( item1.getContentPane().isActive() );
        assertTrue( item1.getTabWidget().isActive() );
        assertEquals( item1, tabPanel.getActiveTab() );

        assertFalse( item2.getContentPane().isActive() );
        assertFalse( item2.getTabWidget().isActive() );
    }

    public void testRemoveActiveTab() throws Exception {
        RootPanel.get().add( tabPanel );

        TabPanelEntry item1 = tabPanel.addItem( "First Tab", new Label( "First tab's content" ) );
        TabPanelEntry item2 = tabPanel.addItem( "Second Tab", new Label( "Second tab's content" ) );

        item2.showTab();
        item1.showTab();
        tabPanel.remove( item1 );

        // must not upset the active state when removing an item (UberTabPanel.updateDisplayedTabs relies on this)
        assertTrue( item1.getTabWidget().isActive() );

        // but the tab panel itself should no longer consider the removed item as active
        assertNull( tabPanel.getActiveTab() );

        // checking that the content _pane_ was removed, and the content itself is still parented to the content pane.
        // this rule could be changed if the tab panel would always reconnect the tab item's content to its content pane
        // when adding an entry back to the panel. Feel free to change this if necessary/convenient.
        assertEquals( item1.getContentPane(), item1.getContents().getParent() );
        assertNull( item1.getContentPane().getParent() );

        assertFalse( item2.getContentPane().isActive() );
        assertFalse( item2.getTabWidget().isActive() );
    }

    public void testRebroadcastShowEvents() throws Exception {
        RootPanel.get().add( tabPanel );

        CountingTabShowHandler showHandler = new CountingTabShowHandler();
        CountingTabShownHandler shownHandler = new CountingTabShownHandler();
        tabPanel.addShowHandler( showHandler );
        tabPanel.addShownHandler( shownHandler );

        // this test leaves it intentionally ambiguous if the show[n] events come from adding the tab or from showing it later
        TabPanelEntry item1 = tabPanel.addItem( "First Tab", new Label( "First tab's content" ) );
        assertNull( tabPanel.getActiveTab() );
        item1.showTab();
        assertNotNull( tabPanel.getActiveTab() );

        assertEquals( 1, showHandler.getEventCount() );
        assertEquals( 1, shownHandler.getEventCount() );
    }

}
