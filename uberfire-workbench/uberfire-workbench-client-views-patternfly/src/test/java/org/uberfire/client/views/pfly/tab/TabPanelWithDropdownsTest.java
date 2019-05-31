/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.shared.event.TabShowHandler;
import org.gwtbootstrap3.client.shared.event.TabShownHandler;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabPane;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static com.google.gwt.i18n.client.HasDirection.Direction.LTR;
import static com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant.endOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class TabPanelWithDropdownsTest {

    @Mock
    private TabPanelEntry tab;

    @Mock
    private Multimap<TabPanelEntry, HandlerRegistration> registrations;

    @Mock
    private TabPanelEntry.DropDownTabListItem tabWidget;

    @Mock
    private TabShowHandler tabShowHandler;

    @Mock
    private TabShownHandler tabShownHandler;

    @Mock
    private HandlerRegistration showHandlerRegistration;

    @Mock
    private HandlerRegistration shownHandlerRegistration;

    @Mock
    private TabPanelEntry tabPanelEntry;

    @Mock
    private HorizontalPanel tabBarWidgetsPanel;

    private Set<TabPanelEntry> tabPanelEntries;

    private TabPanelWithDropdowns tabPanel;

    @Before
    public void setUp() {
        tabPanel = spy(new TabPanelWithDropdowns());

        tabPanelEntries = new HashSet<TabPanelEntry>() {{
            add(tabPanelEntry);
        }};
    }

    @Test
    public void testAddItem() {

        final int index = 1;

        doNothing().when(tabPanel).insertItem(any(), anyInt());
        doReturn(tabPanelEntries).when(tabPanel).getAllContentTabs();

        tabPanel.addItem(tab);

        verify(tabPanel).insertItem(tab, index);
    }

    @Test
    public void testInsertItem() {

        final int index = 1;

        doNothing().when(tabPanel).registerTabHandlers(any());
        doNothing().when(tabPanel).insertTabAndContent(any(), anyInt());
        doNothing().when(tabPanel).resizeTabContent();

        tabPanel.insertItem(tabPanelEntry, index);

        verify(tabPanel).registerTabHandlers(tabPanelEntry);
        verify(tabPanel).insertTabAndContent(tabPanelEntry, index);
        verify(tabPanel).resizeTabContent();
    }

    @Test
    public void testInsertTabAndContent() {

        final int index = 1;
        final TabPane tabContentPane = mock(TabPane.class);
        final NavTabs tabBar = mock(NavTabs.class);
        final TabContent tabContent = mock(TabContent.class);
        final Set<TabPanelEntry> allContentTabs = spy(new HashSet<>());
        final Set<Widget> activatableWidgets = spy(new HashSet<>());

        doReturn(tabWidget).when(tab).getTabWidget();
        doReturn(tabContentPane).when(tab).getContentPane();
        doReturn(tabBar).when(tabPanel).getTabBar();
        doReturn(tabContent).when(tabPanel).getTabContent();
        doReturn(allContentTabs).when(tabPanel).getAllContentTabs();
        doReturn(activatableWidgets).when(tabPanel).getActivatableWidgets();

        tabPanel.insertTabAndContent(tab, index);

        verify(tabBar).insert(tab.getTabWidget(), index);
        verify(allContentTabs).add(tab);
        verify(tabContent).add(tabContentPane);
        verify(activatableWidgets).add(tabWidget);
    }

    @Test
    public void testRegisterTabHandlers() {

        doReturn(registrations).when(tabPanel).getTabHandlerRegistrations();
        doReturn(tabWidget).when(tab).getTabWidget();
        doReturn(tabShowHandler).when(tabPanel).getIndividualTabShowHandler();
        doReturn(tabShownHandler).when(tabPanel).getIndividualTabShownHandler();
        doReturn(showHandlerRegistration).when(tabWidget).addShowHandler(tabShowHandler);
        doReturn(shownHandlerRegistration).when(tabWidget).addShownHandler(tabShownHandler);

        tabPanel.registerTabHandlers(tab);

        verify(registrations).put(tab, shownHandlerRegistration);
        verify(registrations).put(tab, showHandlerRegistration);
    }

    @Test
    public void testAddTabBarWidget() throws Exception {
        doReturn(tabBarWidgetsPanel).when(tabPanel).getWidgetsPanel();

        final IsWidget widget = mock(IsWidget.class);

        tabPanel.addTabBarWidget(widget);

        verify(tabBarWidgetsPanel).add(eq(widget));
        verify(tabBarWidgetsPanel).setCellHorizontalAlignment(eq(widget), eq(endOf(LTR)));
    }
}
