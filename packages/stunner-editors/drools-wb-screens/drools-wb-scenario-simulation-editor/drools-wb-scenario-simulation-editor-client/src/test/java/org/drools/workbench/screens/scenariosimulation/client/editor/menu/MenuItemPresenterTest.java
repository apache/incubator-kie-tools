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

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MenuItemPresenterTest {

    @Mock
    private MenuItemViewImpl mockMenuItemViewImpl;

    @Mock
    private LIElement mockLIElement;

    @Mock
    private ViewsProvider mockViewsProvider;

    private MenuItemPresenter menuItemPresenter;

    @Before
    public void setup() {
        when(mockViewsProvider.getMenuItemView()).thenReturn(mockMenuItemViewImpl);
        when(mockMenuItemViewImpl.getLabelMenuElement()).thenReturn(mockLIElement);

        this.menuItemPresenter = spy(new MenuItemPresenter() {
            {
                this.viewsProvider = mockViewsProvider;
            }
        });
    }

    @Test
    public void onClickEvent() {
        ClickEvent mockClickEvent = mock(ClickEvent.class);
        menuItemPresenter.onClickEvent(mockClickEvent);
        verify(mockClickEvent, times(1)).preventDefault();
        verify(mockClickEvent, times(1)).stopPropagation();
    }

    @Test
    public void getLIElement() {
        LIElement liElement = menuItemPresenter.getLabelMenuElement("TEST-ID", "TEST-LABEL");
        assertNotNull(liElement);
    }
}