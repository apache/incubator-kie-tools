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

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertSame;
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

    private TestToolsViewImpl testToolsViewSpy;

    @Mock
    private AnchorElement clearSelectionElementMock;

    @Mock
    private ParagraphElement testToolsDescriptionElementMock;

    @Mock
    private LabelElement testToolObjectSelectionTitleElementMock;

    @Mock
    private TestToolsPresenter testToolsPresenterMock;

    @Mock
    private InputElement inputSearchMock;

    @Mock
    private ButtonElement clearSearchButtonMock;

    @Mock
    private ButtonElement searchButtonMock;

    @Mock
    private DivElement dataObjectListContainerMock;

    @Mock
    private DivElement simpleJavaTypeListContainerMock;

    @Mock
    private SpanElement instanceListContainerSeparatorMock;

    @Mock
    private Style instanceListStyleMock;

    @Mock
    private DivElement instanceListContainerMock;

    @Mock
    private DivElement simpleJavaInstanceListContainerMock;

    @Mock
    private DivElement kieTestToolsContentMock;

    @Mock
    private ButtonElement addButtonMock;

    @Mock
    private DivElement addButtonLabelMock;

    @Mock
    private SpanElement infoSelectDataObjectElementMock;

    @Mock
    private Style dataObjectListContainerStyleMock;

    @Mock
    private Style simpleJavaTypeListContainerStyleMock;

    @Mock
    private Style instanceListContainerStyleMock;

    @Mock
    private Style simpleJavaInstanceListStyleMock;

    @Mock
    private Style instanceListSeparatorStyleMock;

    @Before
    public void setup() {
        this.testToolsViewSpy = spy(new TestToolsViewImpl() {
            {
                this.inputSearch = inputSearchMock;
                this.clearSearchButton = clearSearchButtonMock;
                this.searchButton = searchButtonMock;
                this.addButton = addButtonMock;
                this.addButtonLabel = addButtonLabelMock;
                this.testToolsDescriptionElement = testToolsDescriptionElementMock;
                this.testToolObjectSelectionTitleElement = testToolObjectSelectionTitleElementMock;
                this.clearSelectionElement = clearSelectionElementMock;
                this.kieTestToolsContent = kieTestToolsContentMock;
                this.dataObjectListContainer = dataObjectListContainerMock;
                this.simpleJavaTypeListContainer = simpleJavaTypeListContainerMock;
                this.instanceListContainer = instanceListContainerMock;
                this.instanceListContainerSeparator = instanceListContainerSeparatorMock;
                this.simpleJavaInstanceListContainer = simpleJavaInstanceListContainerMock;
                this.infoSelectDataObjectElement = infoSelectDataObjectElementMock;
            }
        });
        when(instanceListContainerSeparatorMock.getStyle()).thenReturn(instanceListStyleMock);
        when(dataObjectListContainerMock.getStyle()).thenReturn(dataObjectListContainerStyleMock);
        when(simpleJavaTypeListContainerMock.getStyle()).thenReturn(simpleJavaTypeListContainerStyleMock);
        when(instanceListContainerMock.getStyle()).thenReturn(instanceListContainerStyleMock);
        when(simpleJavaInstanceListContainerMock.getStyle()).thenReturn(simpleJavaInstanceListStyleMock);
        when(instanceListContainerSeparatorMock.getStyle()).thenReturn(instanceListSeparatorStyleMock);
    }

    @Test
    public void init() {
        testToolsViewSpy.init(testToolsPresenterMock);
        assertSame(testToolsPresenterMock, testToolsViewSpy.presenter);
        verify(testToolsViewSpy, times(1)).disableEditorTab();
        verify(testToolsDescriptionElementMock, times(1)).setInnerText(eq(ScenarioSimulationEditorConstants.INSTANCE.testToolsDescription()));
        verify(testToolObjectSelectionTitleElementMock, times(1)).setInnerText(eq(ScenarioSimulationEditorConstants.INSTANCE.testToolObjectSelectionTitle()));
        verify(clearSelectionElementMock, times(1)).setInnerText(eq(ScenarioSimulationEditorConstants.INSTANCE.testToolClearSelection()));
        verify(addButtonMock, times(1)).setInnerText(eq(ScenarioSimulationEditorConstants.INSTANCE.testToolsAddButton()));
        verify(addButtonLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.testToolsAddButtonLabel());
        verify(instanceListContainerSeparatorMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.dataObjectInstances());
        verify(infoSelectDataObjectElementMock, times(1)).setAttribute(eq("title"), eq(ScenarioSimulationEditorConstants.INSTANCE.testToolObjectSelectionTooltip()));
    }

    @Test
    public void onClearSearchButtonClick() {
        testToolsViewSpy.init(testToolsPresenterMock);
        testToolsViewSpy.onClearSearchButtonClick(mock(ClickEvent.class));
        verify(testToolsPresenterMock, times(1)).onUndoSearch();
    }

    @Test
    public void onInputSearchKeyUp() {
        testToolsViewSpy.init(testToolsPresenterMock);
        testToolsViewSpy.onInputSearchKeyUp(mock(KeyUpEvent.class));
        verify(testToolsPresenterMock, times(1)).onShowClearButton();
    }

    @Test
    public void clearInputSearch() {
        testToolsViewSpy.clearInputSearch();
        verify(inputSearchMock, times(1)).setValue(eq(""));
    }

    @Test
    public void hideClearButton() {
        reset(clearSearchButtonMock);
        testToolsViewSpy.hideClearButton();
        verify(clearSearchButtonMock, times(1)).setDisabled(eq(true));
        verify(clearSearchButtonMock, times(1)).setAttribute(eq("style"), eq("display: none;"));
    }

    @Test
    public void showClearButton() {
        reset(clearSearchButtonMock);
        testToolsViewSpy.showClearButton();
        verify(clearSearchButtonMock, times(1)).setDisabled(eq(false));
        verify(clearSearchButtonMock, times(1)).removeAttribute(eq("style"));
    }

    @Test
    public void testReset() {
        testToolsViewSpy.reset();
        verify(testToolsViewSpy, times(1)).clearDataObjectList();
        verify(testToolsViewSpy, times(1)).clearSimpleJavaTypeList();
        verify(testToolsViewSpy, times(1)).showInstanceListContainerSeparator(eq(false));
        verify(testToolsViewSpy, times(1)).clearInstanceList();
        verify(testToolsViewSpy, times(1)).clearSimpleJavaInstanceFieldList();
    }

    @Test
    public void onClearSelectionElementClicked() {
        testToolsViewSpy.init(testToolsPresenterMock);
        testToolsViewSpy.onClearSelectionElementClicked(mock(ClickEvent.class));
        verify(testToolsPresenterMock, times(1)).clearSelection();
    }

    @Test
    public void clearDataObjectList() {
        testToolsViewSpy.clearDataObjectList();
        verify(dataObjectListContainerMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
        verify(dataObjectListContainerMock, times(1)).removeAllChildren();
    }

    @Test
    public void clearSimpleJavaTypeList() {
        testToolsViewSpy.clearSimpleJavaTypeList();
        verify(simpleJavaTypeListContainerMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
        verify(simpleJavaTypeListContainerMock, times(1)).removeAllChildren();
    }

    @Test
    public void clearInstanceList() {
        testToolsViewSpy.clearInstanceList();
        verify(instanceListContainerMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
        verify(instanceListContainerMock, times(1)).removeAllChildren();
    }

    @Test
    public void clearSimpleJavaInstanceFieldList() {
        testToolsViewSpy.clearSimpleJavaInstanceFieldList();
        verify(simpleJavaInstanceListContainerMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
        verify(simpleJavaInstanceListContainerMock, times(1)).removeAllChildren();
    }

    @Test
    public void addDataObjectListGroupItem() {
        DivElement divElementMock = mock(DivElement.class);
        testToolsViewSpy.addDataObjectListGroupItem(divElementMock);
        verify(dataObjectListContainerMock, times(1)).appendChild(eq(divElementMock));
        verify(dataObjectListContainerMock.getStyle(), times(1)).setDisplay(eq(Style.Display.BLOCK));
    }

    @Test
    public void addSimpleJavaTypeListGroupItem() {
        DivElement divElementMock = mock(DivElement.class);
        testToolsViewSpy.addSimpleJavaTypeListGroupItem(divElementMock);
        verify(simpleJavaTypeListContainerMock, times(1)).appendChild(eq(divElementMock));
        verify(simpleJavaTypeListContainerMock.getStyle(), times(1)).setDisplay(eq(Style.Display.BLOCK));
    }

    @Test
    public void addInstanceListGroupItem() {
        DivElement divElementMock = mock(DivElement.class);
        testToolsViewSpy.addInstanceListGroupItem(divElementMock);
        verify(instanceListContainerMock, times(1)).appendChild(eq(divElementMock));
        verify(instanceListContainerMock.getStyle(), times(1)).setDisplay(eq(Style.Display.BLOCK));
    }

    @Test
    public void addSimpleJavaInstanceListGroupItem() {
        DivElement divElementMock = mock(DivElement.class);
        testToolsViewSpy.addSimpleJavaInstanceListGroupItem(divElementMock);
        verify(simpleJavaInstanceListContainerMock, times(1)).appendChild(eq(divElementMock));
        verify(simpleJavaInstanceListContainerMock.getStyle(), times(1)).setDisplay(eq(Style.Display.BLOCK));
    }

    @Test
    public void updateInstanceListSeparatorNotEmptyTrueParameter() {
        when(instanceListContainerMock.getChildCount()).thenReturn(2);
        testToolsViewSpy.updateInstanceListSeparator(true);
        verify(instanceListContainerMock, times(1)).getChildCount();
        verify(instanceListContainerSeparatorMock.getStyle(), times(1)).setDisplay(eq(Style.Display.BLOCK));
    }

    @Test
    public void updateInstanceListSeparatorNotEmptiesTrueParameter() {
        when(instanceListContainerMock.getChildCount()).thenReturn(2);
        when(simpleJavaInstanceListContainerMock.getChildCount()).thenReturn(2);
        testToolsViewSpy.updateInstanceListSeparator(true);
        verify(instanceListContainerMock, times(1)).getChildCount();
        verify(instanceListContainerSeparatorMock.getStyle(), times(1)).setDisplay(eq(Style.Display.BLOCK));
    }

    @Test
    public void updateInstanceListSeparatorNotEmptyFalseParameter() {
        when(instanceListContainerMock.getChildCount()).thenReturn(2);
        testToolsViewSpy.updateInstanceListSeparator(false);
        verify(instanceListContainerMock, never()).getChildCount();
        verify(instanceListContainerSeparatorMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
    }

    @Test
    public void updateInstanceListSeparatorNotEmptiesFalseParameter() {
        when(instanceListContainerMock.getChildCount()).thenReturn(2);
        when(simpleJavaInstanceListContainerMock.getChildCount()).thenReturn(2);
        testToolsViewSpy.updateInstanceListSeparator(false);
        verify(instanceListContainerMock, never()).getChildCount();
        verify(instanceListContainerSeparatorMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
    }

    @Test
    public void updateInstanceListSeparatorEmptyFalseParameter() {
        when(instanceListContainerMock.getChildCount()).thenReturn(0);
        when(simpleJavaInstanceListContainerMock.getChildCount()).thenReturn(0);
        testToolsViewSpy.updateInstanceListSeparator(false);
        verify(instanceListContainerMock, never()).getChildCount();
        verify(instanceListContainerSeparatorMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
    }

    @Test
    public void updateInstanceListSeparatorEmptyTrueParameter() {
        when(instanceListContainerMock.getChildCount()).thenReturn(0);
        when(simpleJavaInstanceListContainerMock.getChildCount()).thenReturn(0);
        testToolsViewSpy.updateInstanceListSeparator(true);
        verify(instanceListContainerMock, times(1)).getChildCount();
        verify(instanceListContainerSeparatorMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
    }

    @Test
    public void showInstanceListContainerSeparator() {
        testToolsViewSpy.showInstanceListContainerSeparator(true);
        verify(instanceListContainerSeparatorMock.getStyle(), times(1)).setDisplay(eq(Style.Display.BLOCK));
        //
        testToolsViewSpy.showInstanceListContainerSeparator(false);
        verify(instanceListContainerSeparatorMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
    }

    @Test
    public void setClearSelectionAnchorDisabledStatus() {
        testToolsViewSpy.setClearSelectionAnchorDisabledStatus(true);
        verify(clearSelectionElementMock, times(1)).addClassName(eq(ConstantHolder.DISABLED));
        //
        testToolsViewSpy.setClearSelectionAnchorDisabledStatus(false);
        verify(clearSelectionElementMock, times(1)).removeClassName(eq(ConstantHolder.DISABLED));
    }

    @Test
    public void setInfoSelectDataObjectElementDisabledStatus() {
        testToolsViewSpy.setInfoSelectDataObjectElementDisabledStatus(true);
        verify(infoSelectDataObjectElementMock, times(1)).addClassName(eq(ConstantHolder.DISABLED));
        //
        testToolsViewSpy.setInfoSelectDataObjectElementDisabledStatus(false);
        verify(infoSelectDataObjectElementMock, times(1)).removeClassName(eq(ConstantHolder.DISABLED));
    }

    @Test
    public void setDisabledStatusTrue() {
        testToolsViewSpy.setDisabledStatus(true);
        verify(testToolsViewSpy, times(1)).setClearSelectionAnchorDisabledStatus(eq(true));
        verify(testToolsViewSpy, times(1)).setInfoSelectDataObjectElementDisabledStatus(eq(true));
        verify(testToolsViewSpy, times(1)).setContainersDisabledStatus(eq(true));
        verify(kieTestToolsContentMock, times(1)).addClassName(ConstantHolder.DISABLED);
        verify(testToolsViewSpy, times(1)).disableSearch();
        verify(testToolsViewSpy, times(1)).disableAddButton();
        verify(kieTestToolsContentMock, never()).removeClassName(anyString());
    }

    @Test
    public void setDisabledStatusFalse() {
        testToolsViewSpy.setDisabledStatus(false);
        verify(testToolsViewSpy, times(1)).setClearSelectionAnchorDisabledStatus(eq(false));
        verify(testToolsViewSpy, times(1)).setInfoSelectDataObjectElementDisabledStatus(eq(false));
        verify(testToolsViewSpy, times(1)).setContainersDisabledStatus(eq(false));
        verify(kieTestToolsContentMock, never()).addClassName(anyString());
        verify(testToolsViewSpy, never()).disableSearch();
        verify(testToolsViewSpy, never()).disableAddButton();
        verify(kieTestToolsContentMock, times(1)).removeClassName(ConstantHolder.DISABLED);
    }

    @Test
    public void enableSearch() {
        testToolsViewSpy.enableSearch();
        verify(clearSearchButtonMock, times(1)).setDisabled(eq(false));
        verify(searchButtonMock, times(1)).setDisabled(eq(false));
        verify(inputSearchMock, times(1)).setDisabled(eq(false));
    }

    @Test
    public void disableSearch() {
        testToolsViewSpy.disableSearch();
        verify(testToolsViewSpy, times(1)).hideClearButton();
        verify(searchButtonMock, times(1)).setDisabled(eq(true));
        verify(inputSearchMock, times(1)).setDisabled(eq(true));
        verify(inputSearchMock, times(1)).setValue((eq("")));
    }
}