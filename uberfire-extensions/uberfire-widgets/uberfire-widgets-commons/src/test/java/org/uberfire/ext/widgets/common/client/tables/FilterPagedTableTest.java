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
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(Modal.class)
public class FilterPagedTableTest {

    FilterPagedTable filterPagedTable;

    @GwtMock
    Button button;

    @GwtMock
    NavTabs navTabs;

    ClickHandler clickHandler;

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
        filterPagedTable = spy(new FilterPagedTable(mock(MultiGridPreferencesStore.class)));
        doReturn(yesNoCancelPopup).when(filterPagedTable).getYesNoCancelPopup(anyString(), anyString());
        filterPagedTable.makeWidget();

        filterPagedTable.addTab(mock(PagedTable.class), "", mock(Command.class));

        clickHandler.onClick(new ClickEvent() {
        });

        verify(yesNoCancelPopup).show();
        verify(filterPagedTable, never()).removeTab(anyInt());
        verify(filterPagedTable, never()).removeTab(anyString());
    }


}