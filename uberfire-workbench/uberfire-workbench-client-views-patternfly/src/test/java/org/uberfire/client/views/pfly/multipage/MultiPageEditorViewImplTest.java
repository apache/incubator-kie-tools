/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.multipage;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabPane;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.tab.TabPanelEntry;
import org.uberfire.client.workbench.widgets.multipage.Page;
import org.uberfire.client.workbench.widgets.multipage.PageView;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MultiPageEditorViewImplTest {

    @Mock
    private TabPanelEntry.DropDownTabListItem dropDownTabListItem;

    @Mock
    private TabPane contentPane;

    @Mock
    private Page page;

    @Mock
    private TabPanelEntry tab;

    private MultiPageEditorViewImpl view;

    @Before
    public void setup() {
        view = spy(new MultiPageEditorViewImpl());
        view.init();
    }

    @Test
    public void testAddPage() {

        doReturn(tab).when(view).makeTabPanelEntry(page);
        doNothing().when(view).addItem(any());
        doNothing().when(view).setAsActive(any());

        view.addPage(page);

        verify(view).addItem(tab);
        verify(view).setAsActive(tab);
    }

    @Test
    public void testAddPageWithIndex() {

        final int index = 1;

        doReturn(tab).when(view).makeTabPanelEntry(page);
        doNothing().when(view).insertItem(any(), anyInt());
        doNothing().when(view).setAsActive(any());

        view.addPage(index, page);

        verify(view).insertItem(tab, index);
        verify(view).setAsActive(tab);
    }

    @Test
    public void testMakeTabPanelEntry() {

        final String title = "";
        final PageView pageView = mock(PageView.class);
        final Widget widget = mock(Widget.class);

        doReturn(title).when(page).getLabel();
        doReturn(pageView).when(page).getView();
        doReturn(widget).when(pageView).asWidget();

        final TabPanelEntry tabPanelEntry = view.makeTabPanelEntry(page);

        assertEquals(title, tabPanelEntry.getTitle());
        assertEquals(widget, tabPanelEntry.getContents());
    }

    @Test
    public void testSetAsActiveWhenActiveTabIsNull() {

        doReturn(null).when(view).getActiveTab();

        view.setAsActive(tab);

        verify(tab).showTab();
        verify(tab).setActive(true);
    }

    @Test
    public void testSetAsActiveWhenActiveTabIsNotNull() {

        doReturn(mock(TabPanelEntry.class)).when(view).getActiveTab();

        view.setAsActive(tab);

        verify(tab, never()).showTab();
        verify(tab, never()).setActive(true);
    }

    @Test
    public void testDisablePage() {

        final int index = 1;
        final Widget widget = mock(Widget.class);
        final NavTabs navTabs = mock(NavTabs.class);
        final Element element = mock(Element.class);
        final Style style = mock(Style.class);

        doReturn(navTabs).when(view).getTabBar();
        doReturn(widget).when(navTabs).getWidget(index);
        doReturn(element).when(widget).getElement();
        doReturn(style).when(element).getStyle();

        view.disablePage(index);

        verify(widget).addStyleName("disabled");
        verify(style).setProperty("pointerEvents", "none");
    }

    @Test
    public void testEnablePage() {

        final int index = 1;
        final Widget widget = mock(Widget.class);
        final NavTabs navTabs = mock(NavTabs.class);
        final Element element = mock(Element.class);
        final Style style = mock(Style.class);

        doReturn(navTabs).when(view).getTabBar();
        doReturn(widget).when(navTabs).getWidget(index);
        doReturn(element).when(widget).getElement();
        doReturn(style).when(element).getStyle();

        view.enablePage(index);

        verify(widget).removeStyleName("disabled");
        verify(style).clearProperty("pointerEvents");
    }
}
