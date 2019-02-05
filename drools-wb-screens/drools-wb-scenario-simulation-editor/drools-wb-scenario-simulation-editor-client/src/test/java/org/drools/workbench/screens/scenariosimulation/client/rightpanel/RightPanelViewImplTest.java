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
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style;
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
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RightPanelViewImplTest {

    private RightPanelViewImpl rightPanelView;

    @Mock
    private RightPanelPresenter rightPanelPresenterMock;

    @Mock
    private InputElement inputSearchMock;

    @Mock
    private InputElement nameFieldMock;

    @Mock
    private ButtonElement clearSearchButtonMock;

    @Mock
    private DivElement ruleCheatSheetMock;

    @Mock
    private DivElement dmnCheatSheetMock;

    @Mock
    private Style ruleCheatSheetStyleMock;

    @Mock
    private Style dmnCheatSheetStyleMock;

    @Before
    public void setup() {
        this.rightPanelView = spy(new RightPanelViewImpl() {
            {
                this.inputSearch = inputSearchMock;
                this.clearSearchButton = clearSearchButtonMock;
                this.nameField = nameFieldMock;
                this.ruleCheatSheet = ruleCheatSheetMock;
                this.dmnCheatSheet = dmnCheatSheetMock;
            }
        });
        rightPanelView.init(rightPanelPresenterMock);
        when(ruleCheatSheetMock.getStyle()).thenReturn(ruleCheatSheetStyleMock);
        when(dmnCheatSheetMock.getStyle()).thenReturn(dmnCheatSheetStyleMock);
    }

    @Test
    public void onClearSearchButtonClick() {
        reset(rightPanelPresenterMock);
        rightPanelView.onClearSearchButtonClick(mock(ClickEvent.class));
        verify(rightPanelPresenterMock, times(1)).onClearSearch();
    }

    @Test
    public void onInputSearchKeyUp() {
        rightPanelView.onInputSearchKeyUp(mock(KeyUpEvent.class));
        verify(rightPanelPresenterMock, times(1)).onShowClearButton();
    }

    @Test
    public void clearInputSearch() {
        rightPanelView.clearInputSearch();
        verify(inputSearchMock, times(1)).setValue(eq(""));
    }

    @Test
    public void clearNameField() {
        rightPanelView.clearNameField();
        verify(nameFieldMock, times(1)).setValue(eq(""));
    }

    @Test
    public void hideClearButton() {
        reset(clearSearchButtonMock);
        rightPanelView.hideClearButton();
        verify(clearSearchButtonMock, times(1)).setDisabled(eq(true));
        verify(clearSearchButtonMock, times(1)).setAttribute(eq("style"), eq("display: none;"));
    }

    @Test
    public void showClearButton() {
        reset(clearSearchButtonMock);
        rightPanelView.showClearButton();
        verify(clearSearchButtonMock, times(1)).setDisabled(eq(false));
        verify(clearSearchButtonMock, times(1)).removeAttribute(eq("style"));
    }

    @Test
    public void setRuleCheatSheetContent() {
        rightPanelView.setRuleCheatSheetContent();
        verify(ruleCheatSheetStyleMock, times(1)).setDisplay(Style.Display.BLOCK);
        verify(dmnCheatSheetStyleMock, times(1)).setDisplay(Style.Display.NONE);
    }

    @Test
    public void setDMNCheatSheetContent() {
        rightPanelView.setDMNCheatSheetContent();
        verify(ruleCheatSheetStyleMock, times(1)).setDisplay(Style.Display.NONE);
        verify(dmnCheatSheetStyleMock, times(1)).setDisplay(Style.Display.BLOCK);
    }
}