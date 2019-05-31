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
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private ButtonElement searchButtonMock;

    @Mock
    private LabelElement dataObjectListContainerSeparatorMock;

    @Mock
    private Style dataObjectListStyleMock;

    @Mock
    private DivElement dataObjectListContainerMock;

    @Mock
    private LabelElement simpleJavaTypeListContainerSeparatorMock;

    @Mock
    private Style simpleJavaTypeListStyleMock;

    @Mock
    private DivElement simpleJavaTypeListContainerMock;

    @Mock
    private LabelElement instanceListContainerSeparatorMock;

    @Mock
    private Style instanceListStyleMock;

    @Mock
    private DivElement instanceListContainerMock;

    @Mock
    private LabelElement simpleJavaInstanceListContainerSeparatorMock;

    @Mock
    private DivElement simpleJavaInstanceListContainerMock;

    @Mock
    private Style simpleJavaInstanceListStyleMock;

    @Mock
    private DivElement kieTestToolsContentMock;

    @Mock
    private ButtonElement conditionsButtonMock;

    @Before
    public void setup() {
        this.testToolsView = spy(new TestToolsViewImpl() {
            {
                this.inputSearch = inputSearchMock;
                this.clearSearchButton = clearSearchButtonMock;
                this.searchButton = searchButtonMock;
                this.conditionsButton = conditionsButtonMock;
                this.nameField = nameFieldMock;
                this.kieTestToolsContent = kieTestToolsContentMock;
                this.dataObjectListContainer = dataObjectListContainerMock;
                this.dataObjectListContainerSeparator = dataObjectListContainerSeparatorMock;
                this.simpleJavaTypeListContainer = simpleJavaTypeListContainerMock;
                this.simpleJavaTypeListContainerSeparator = simpleJavaTypeListContainerSeparatorMock;
                this.instanceListContainer = instanceListContainerMock;
                this.instanceListContainerSeparator = instanceListContainerSeparatorMock;
                this.simpleJavaInstanceListContainer = simpleJavaInstanceListContainerMock;
                this.simpleJavaInstanceListContainerSeparator = simpleJavaInstanceListContainerSeparatorMock;
            }
        });
        when(dataObjectListContainerSeparatorMock.getStyle()).thenReturn(dataObjectListStyleMock);
        when(simpleJavaTypeListContainerSeparatorMock.getStyle()).thenReturn(simpleJavaTypeListStyleMock);
        when(instanceListContainerSeparatorMock.getStyle()).thenReturn(instanceListStyleMock);
        when(simpleJavaInstanceListContainerSeparatorMock.getStyle()).thenReturn(simpleJavaInstanceListStyleMock);
    }

    @Test
    public void onClearSearchButtonClick() {
        testToolsView.init(testToolsPresenterMock);
        testToolsView.onClearSearchButtonClick(mock(ClickEvent.class));
        verify(testToolsPresenterMock, times(1)).onClearSearch();
    }

    @Test
    public void onInputSearchKeyUp() {
        testToolsView.init(testToolsPresenterMock);
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

    @Test
    public void testReset() {
        testToolsView.reset();
        verify(dataObjectListContainerSeparatorMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
        verify(dataObjectListContainerMock, times(1)).removeAllChildren();
        verify(simpleJavaTypeListContainerSeparatorMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
        verify(simpleJavaTypeListContainerMock, times(1)).removeAllChildren();
        verify(instanceListContainerSeparatorMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
        verify(instanceListContainerMock, times(1)).removeAllChildren();
        verify(simpleJavaInstanceListContainerSeparatorMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
        verify(simpleJavaInstanceListContainerMock, times(1)).removeAllChildren();
    }

    @Test
    public void setDisabledStatusTrue() {
        testToolsView.setDisabledStatus(true);
        verify(nameFieldMock, times(1)).setDisabled(eq(true));
        verify(conditionsButtonMock, times(1)).setDisabled(true);
        verify(testToolsView, times(1)).setContainersDisabledStatus(true);
        verify(kieTestToolsContentMock, times(1)).addClassName("disabled");
        verify(testToolsView, times(1)).disableSearch();
        verify(testToolsView, times(1)).disableAddButton();
        verify(kieTestToolsContentMock, never()).removeClassName(anyString());
    }

    @Test
    public void setDisabledStatusFalse() {
        testToolsView.setDisabledStatus(false);
        verify(nameFieldMock, times(1)).setDisabled(eq(false));
        verify(conditionsButtonMock, times(1)).setDisabled(false);
        verify(testToolsView, times(1)).setContainersDisabledStatus(false);
        verify(kieTestToolsContentMock, never()).addClassName(anyString());
        verify(testToolsView, never()).disableSearch();
        verify(testToolsView, never()).disableAddButton();
        verify(kieTestToolsContentMock, times(1)).removeClassName("disabled");
    }


    @Test
    public void enableSearch() {
        testToolsView.enableSearch();
        verify(clearSearchButtonMock, times(1)).setDisabled(eq(false));
        verify(searchButtonMock, times(1)).setDisabled(eq(false));
        verify(inputSearchMock, times(1)).setDisabled(eq(false));
    }

    @Test
    public void disableSearch() {
        testToolsView.disableSearch();
        verify(clearSearchButtonMock, times(1)).setDisabled(eq(true));
        verify(searchButtonMock, times(1)).setDisabled(eq(true));
        verify(inputSearchMock, times(1)).setDisabled(eq(true));
    }
}