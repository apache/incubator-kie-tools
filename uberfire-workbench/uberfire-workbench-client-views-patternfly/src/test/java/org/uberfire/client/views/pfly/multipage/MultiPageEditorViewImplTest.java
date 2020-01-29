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
import org.assertj.core.api.Assertions;
import org.gwtbootstrap3.client.shared.event.TabShownEvent;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.tab.TabPanelEntry;
import org.uberfire.client.workbench.widgets.multipage.Page;
import org.uberfire.client.workbench.widgets.multipage.PageView;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MultiPageEditorViewImplTest {

    @Mock
    private Page page;

    @Mock
    private TabPanelEntry tab;

    @Mock
    private EventSourceMock<MultiPageEditorSelectedPageEvent> selectedPageEvent;

    @Captor
    private ArgumentCaptor<MultiPageEditorSelectedPageEvent> pageEvent;

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
        doReturn(true).when(view).isValid(anyInt());

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
        doReturn(true).when(view).isValid(anyInt());

        view.enablePage(index);

        verify(widget).removeStyleName("disabled");
        verify(style).clearProperty("pointerEvents");
    }

    @Test
    public void testGetTabShownHandler() {

        final TabListItem tab = mock(TabListItem.class);
        final TabShownEvent event = mock(TabShownEvent.class);
        final TabPanelEntry tabPanelEntry = mock(TabPanelEntry.class);
        final TabPane tabPane = mock(TabPane.class);
        final PageViewImpl page = mock(PageViewImpl.class);
        final int pageIndex = 42;

        when(event.getTab()).thenReturn(tab);
        when(tabPanelEntry.getContentPane()).thenReturn(tabPane);
        when(tabPane.getWidget(0)).thenReturn(page);
        when(tab.getTabIndex()).thenReturn(pageIndex);
        doReturn(tabPanelEntry).when(view).findEntryForTabWidget(tab);

        view.enableSelectedPageEvent(selectedPageEvent);

        view.getTabShownHandler().onShown(event);

        verify(view).onResize();
        verify(selectedPageEvent).fire(pageEvent.capture());
        verify(page).onLostFocus();
        assertEquals(pageIndex, pageEvent.getValue().getSelectedPage());
    }

    @Test
    public void testGetPageIndex() {
        final NavTabs navTabs = mock(NavTabs.class);
        doReturn(navTabs).when(view).getTabBar();

        final TabListItem pageOne = mock(TabListItem.class);
        final TabListItem pageTwo = mock(TabListItem.class);
        final TabListItem pageThree = mock(TabListItem.class);

        doReturn(3).when(navTabs).getWidgetCount();
        doReturn(pageOne).when(navTabs).getWidget(0);
        doReturn(pageTwo).when(navTabs).getWidget(1);
        doReturn(pageThree).when(navTabs).getWidget(2);

        doReturn("page 1").when(pageOne).getText();
        doReturn("page 2").when(pageTwo).getText();
        doReturn("page 3").when(pageThree).getText();

        Assertions.assertThat(view.getPageIndex("page 3")).isEqualTo(2);
        Assertions.assertThat(view.getPageIndex("page 2")).isEqualTo(1);
        Assertions.assertThat(view.getPageIndex("page 1")).isEqualTo(0);

        Assertions.assertThatThrownBy(() -> view.getPageIndex("xyz"))
                .hasMessage("Page with title: 'xyz' doesn't exist.");
    }
}
