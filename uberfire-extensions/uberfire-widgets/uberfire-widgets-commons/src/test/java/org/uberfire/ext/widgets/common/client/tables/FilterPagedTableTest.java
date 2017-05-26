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

package org.uberfire.ext.widgets.common.client.tables;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(Modal.class)
public class FilterPagedTableTest {

    @InjectMocks
    @Spy
    FilterPagedTable filterPagedTable;

    @GwtMock
    Button button;

    @Mock
    protected MultiGridPreferencesStore multiGridPreferencesStoreMock;

    @GwtMock
    protected PagedTable pagedTableMock;

    @GwtMock
    protected YesNoCancelPopup yesNoCancelPopupMock;

    @GwtMock
    TabListItem tabListItem;

    @GwtMock
    NavTabs navTabs;

    ClickHandler clickHandler;

    @Before
    public void setup(){
        filterPagedTable.makeWidget();
    }

    @Test
    public void testAddTab() throws Exception {
        when(navTabs.getWidget(anyInt())).thenReturn(tabListItem);

        filterPagedTable.addTab(pagedTableMock, "base", mock(Command.class));

        verify(tabListItem, times(1)).showTab();

        filterPagedTable.addTab(pagedTableMock, "base", mock(Command.class), true);

        verify(tabListItem, times(2)).showTab();

        filterPagedTable.addTab(pagedTableMock, "base", mock(Command.class), false);

        verify(tabListItem, times(2)).showTab();
    }

    @Test
    public void testRemoveTab() throws Exception {
        final Element element = mock(Element.class);
        when(element.getStyle()).thenReturn(mock(Style.class));
        when(button.getElement()).thenReturn(element);
        when(button.addClickHandler(any(ClickHandler.class))).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock aInvocation) throws Throwable {
                clickHandler = (ClickHandler) aInvocation.getArguments()[0];
                return null;
            }
        });

        final YesNoCancelPopup yesNoCancelPopup = mock(YesNoCancelPopup.class);
        doReturn(yesNoCancelPopup).when(filterPagedTable).getYesNoCancelPopup(anyString(), anyString());

        filterPagedTable.addTab(mock(PagedTable.class), "", mock(Command.class));

        clickHandler.onClick(new ClickEvent() {
        });

        verify(yesNoCancelPopup).show();
        verify(filterPagedTable, never()).removeTab(anyInt());
        verify(filterPagedTable, never()).removeTab(anyString());
    }

    @Test
    public void testScapeHtmlCodeInDescriptionRemoveTab() throws Exception {
        String key = "test";
        String header = "*<h1>test</h1>*";
        String title = "*<h1>test</h1>*";

        final Element element = mock(Element.class);
        when(element.getStyle()).thenReturn(mock(Style.class));
        when(button.getElement()).thenReturn(element);
        when(button.addClickHandler(any(ClickHandler.class))).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock aInvocation) throws Throwable {
                clickHandler = (ClickHandler) aInvocation.getArguments()[0];
                return null;
            }
        });

        doReturn(yesNoCancelPopupMock).when(filterPagedTable).getYesNoCancelPopup(eq(SafeHtmlUtils.htmlEscape(header)), anyString());

        when(multiGridPreferencesStoreMock.getGridSettingParam(eq(key),eq(NewTabFilterPopup.FILTER_TAB_NAME_PARAM))).thenReturn(header);
        when(multiGridPreferencesStoreMock.getGridSettingParam(eq(key),eq(NewTabFilterPopup.FILTER_TAB_DESC_PARAM))).thenReturn(title);

        filterPagedTable.addTab(pagedTableMock, key, mock(Command.class));

        clickHandler.onClick(new ClickEvent() {
        });

        verify(pagedTableMock).addTableTitle(eq(SafeHtmlUtils.htmlEscape(title)));
        verify(yesNoCancelPopupMock).show();
    }
}