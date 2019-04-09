/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class TestToolsViewImplTest {

    private TestToolsViewImpl testToolsView;

    @Mock
    private TestToolsPresenter testToolsPresenterMock;

    @Mock
    private InputElement inputSearchMock;

    @Mock
    private InputElement nameFieldMock;

    @Mock
    private ButtonElement clearSearchButtonMock;

    @Before
    public void setup() {
        this.testToolsView = spy(new TestToolsViewImpl() {
            {
                this.inputSearch = inputSearchMock;
                this.clearSearchButton = clearSearchButtonMock;
                this.nameField = nameFieldMock;
            }
        });
        testToolsView.init(testToolsPresenterMock);
    }

    @Test
    public void onClearSearchButtonClick() {
        reset(testToolsPresenterMock);
        testToolsView.onClearSearchButtonClick(mock(ClickEvent.class));
        verify(testToolsPresenterMock, times(1)).onClearSearch();
    }

    @Test
    public void onInputSearchKeyUp() {
        testToolsView.onInputSearchKeyUp(mock(KeyUpEvent.class));
        verify(testToolsPresenterMock, times(1)).onShowClearButton();
    }

    @Test
    public void clearInputSearch() {
        testToolsView.clearInputSearch();
        verify(inputSearchMock, times(1)).setValue(eq(""));
    }

    @Test
    public void clearNameField() {
        testToolsView.clearNameField();
        verify(nameFieldMock, times(1)).setValue(eq(""));
    }

    @Test
    public void hideClearButton() {
        reset(clearSearchButtonMock);
        testToolsView.hideClearButton();
        verify(clearSearchButtonMock, times(1)).setDisabled(eq(true));
        verify(clearSearchButtonMock, times(1)).setAttribute(eq("style"), eq("display: none;"));
    }

    @Test
    public void showClearButton() {
        reset(clearSearchButtonMock);
        testToolsView.showClearButton();
        verify(clearSearchButtonMock, times(1)).setDisabled(eq(false));
        verify(clearSearchButtonMock, times(1)).removeAttribute(eq("style"));
    }
}