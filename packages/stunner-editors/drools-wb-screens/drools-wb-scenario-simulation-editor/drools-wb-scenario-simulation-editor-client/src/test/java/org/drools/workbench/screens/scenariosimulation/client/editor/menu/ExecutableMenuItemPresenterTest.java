/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

import java.util.Map;

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ExecutableMenuItemPresenterTest {

    @Mock
    private ExecutableMenuItemViewImpl mockExecutableMenuItemViewImpl;

    @Mock
    private LIElement mockLIElement;

    @Mock
    private Map<LIElement, Event> mockMenuItemsEventMap;

    @Mock
    private Event mockEvent;

    @Mock
    private EventBus mockEventBus;

    @Mock
    private BaseMenu mockparent;

    private ExecutableMenuItemPresenter executableMenuItemPresenter;

    @Before
    public void setup() {
        when(mockExecutableMenuItemViewImpl.getLExecutableMenuElement()).thenReturn(mockLIElement);
        when(mockMenuItemsEventMap.containsKey(mockLIElement)).thenReturn(true);
        when(mockMenuItemsEventMap.get(mockLIElement)).thenReturn(mockEvent);

        this.executableMenuItemPresenter = spy(new ExecutableMenuItemPresenter() {
            {
                menuItemsEventMap = mockMenuItemsEventMap;
                eventBus = mockEventBus;
                parent = mockparent;
            }

            @Override
            protected ExecutableMenuItemView getMenuItemView() {
                return mockExecutableMenuItemViewImpl;
            }
        });
    }

    @Test
    public void onClickEvent() {
        ClickEvent mockClickEvent = mock(ClickEvent.class);
        executableMenuItemPresenter.onClickEvent(mockClickEvent, mockLIElement);
        verify(mockClickEvent, times(1)).preventDefault();
        verify(mockClickEvent, times(1)).stopPropagation();
        verify(mockparent, times(1)).hide();
        verify(executableMenuItemPresenter, times(1)).fireEvent(eq(mockLIElement));
    }

    @Test
    public void fireEvent() {
        executableMenuItemPresenter.fireEvent(mockLIElement);
        verify(mockMenuItemsEventMap, times(1)).containsKey(mockLIElement);
        verify(mockEventBus, times(1)).fireEvent(eq(mockEvent));
    }

    @Test
    public void getLIElement() {
        LIElement liElement = executableMenuItemPresenter.getLExecutableMenuElement("TEST-ID", "TEST-LABEL", mockEvent);
        assertNotNull(liElement);
        verify(mockMenuItemsEventMap, times(1)).put(liElement, mockEvent);
    }
}