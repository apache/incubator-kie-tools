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

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.events.SetInstanceHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetPropertyHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RightPanelPresenterTest extends AbstractRightPanelTest {

    @Mock
    private RightPanelView rightPanelViewMock;

    @Mock
    private DivElement dataObjectListContainerMock;

    @Mock
    private DivElement simpleJavaTypeListContainerMock;

    @Mock
    private DivElement instanceListContainerMock;

    @Mock
    private DivElement simpleJavaInstanceListContainerMock;

    @Mock
    private ListGroupItemPresenter listGroupItemPresenterMock;

    @Mock
    private  ListGroupItemView selectedListGroupItemViewMock;
    @Mock
    private  FieldItemView selectedFieldItemViewMock;

    @Mock
    private EventBus eventBusMock;

    private RightPanelPresenter rightPanelPresenter;

    @Before
    public void setup() {
        super.setup();
        final String firstKey = dataObjectFactTreeMap.firstKey();
        final FactModelTree factModelTree = dataObjectFactTreeMap.get(firstKey);
        final String firstPropertyKey = (String) new ArrayList(factModelTree.getSimpleProperties().keySet()).get(0);
        final String firstPropertyClass = factModelTree.getSimpleProperties().get(firstPropertyKey);

        when(selectedListGroupItemViewMock.getActualClassName()).thenReturn(firstKey);

        when(selectedFieldItemViewMock.getFullPath()).thenReturn(firstKey/* + "." + firstPropertyKey*/);
        when(selectedFieldItemViewMock.getFieldName()).thenReturn(firstPropertyKey);
        when(selectedFieldItemViewMock.getClassName()).thenReturn(firstPropertyClass);

        when(rightPanelViewMock.getDataObjectListContainer()).thenReturn(dataObjectListContainerMock);
        when(rightPanelViewMock.getSimpleJavaTypeListContainer()).thenReturn(simpleJavaTypeListContainerMock);
        when(rightPanelViewMock.getInstanceListContainer()).thenReturn(instanceListContainerMock);
        when(rightPanelViewMock.getSimpleJavaInstanceListContainer()).thenReturn(simpleJavaInstanceListContainerMock);
        when(listGroupItemPresenterMock.getDivElement(FACT_NAME, FACT_MODEL_TREE)).thenReturn(dataObjectListContainerMock);
        this.rightPanelPresenter = spy(new RightPanelPresenter(rightPanelViewMock, listGroupItemPresenterMock) {
            {
                this.dataObjectFieldsMap = dataObjectFactTreeMap;
                this.simpleJavaTypeFieldsMap = simpleJavaTypeTreeMap;
                this.instanceFieldsMap = instanceFactTreeMap;
                this.simpleJavaInstanceFieldsMap = simpleJavaInstanceFactTreeMap;
                this.eventBus = eventBusMock;
            }
        });
    }

    @Test
    public void onSetup() {
        rightPanelPresenter.setup();
        verify(rightPanelViewMock, times(1)).init(rightPanelPresenter);
    }

    @Test
    public void getTitle() {
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.testTools(), rightPanelPresenter.getTitle());
    }

    @Test
    public void onClearSearch() {
        rightPanelPresenter.onClearSearch();
        verify(rightPanelViewMock, times(1)).clearInputSearch();
        verify(rightPanelViewMock, times(1)).hideClearButton();
    }

    @Test
    public void onClearNameField() {
        rightPanelPresenter.onClearNameField();
        verify(rightPanelViewMock, times(1)).clearNameField();
    }

    @Test
    public void onClearStatus() {
        rightPanelPresenter.onClearStatus();
        verify(rightPanelPresenter, times(1)).onClearSearch();
        verify(rightPanelPresenter, times(1)).onClearNameField();
        verify(rightPanelPresenter, times(1)).clearDataObjectList();
    }

    @Test
    public void clearDataObjectList() {
        rightPanelPresenter.clearDataObjectList();
        verify(rightPanelViewMock, times(1)).getDataObjectListContainer();
        verify(dataObjectListContainerMock, times(1)).removeAllChildren();
    }

    @Test
    public void clearInstanceList() {
        rightPanelPresenter.clearInstanceList();
        verify(rightPanelViewMock, times(1)).getInstanceListContainer();
        verify(instanceListContainerMock, times(1)).removeAllChildren();
    }


    @Test
    public void getFactModelTree() {
        rightPanelPresenter.setDataObjectFieldsMap(dataObjectFactTreeMap);
        String factName = getRandomFactModelTree(dataObjectFactTreeMap, 0);
        Optional<FactModelTree> retrieved = rightPanelPresenter.getFactModelTreeFromFactTypeMap(factName);
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        assertEquals(dataObjectFactTreeMap.get(factName), retrieved.get());
    }

    @Test
    public void setFactTypeFieldsMap() {
        rightPanelPresenter.setDataObjectFieldsMap(dataObjectFactTreeMap);
        verify(rightPanelPresenter, times(dataObjectFactTreeMap.size())).addDataObjectListGroupItemView(anyString(), anyObject());
    }

    @Test
    public void onShowClearButton() {
        rightPanelPresenter.onShowClearButton();
        verify(rightPanelViewMock, times(1)).showClearButton();
    }

    @Test
    public void setEventBus() {
        rightPanelPresenter.setEventBus(eventBusMock);
        assertEquals(eventBusMock, rightPanelPresenter.eventBus);
    }

    @Test
    public void onSearchedEvent() {
        rightPanelPresenter.onSearchedEvent("");
        verify(rightPanelPresenter, times(1)).clearDataObjectList();
        verify(rightPanelPresenter, times(1)).clearInstanceList();
    }

    @Test
    public void addListGroupItemView() {
        rightPanelPresenter.addDataObjectListGroupItemView(FACT_NAME, FACT_MODEL_TREE);
        verify(rightPanelViewMock, times(1)).getDataObjectListContainer();
        verify(listGroupItemPresenterMock, times(1)).getDivElement(eq(FACT_NAME), eq(FACT_MODEL_TREE));
        verify(dataObjectListContainerMock, times(1)).appendChild(anyObject());
    }

    @Test
    public void onEnableEditorTabWithoutFactName() {
        rightPanelPresenter.onEnableEditorTab();
        verify(rightPanelPresenter, times(1)).onSearchedEvent(eq(""));
        verify(listGroupItemPresenterMock, times(1)).enable();
        verify(listGroupItemPresenterMock, never()).enable(anyString());
        verify(rightPanelViewMock, times(1)).enableEditorTab();
    }

    @Test
    public void onEnableEditorTabWithFactName() {
        rightPanelPresenter.onEnableEditorTab(FACT_NAME, null, false);
        verify(rightPanelPresenter, times(1)).onPerfectMatchSearchedEvent(eq(FACT_NAME), eq(false));
        verify(listGroupItemPresenterMock, times(1)).enable(eq(FACT_NAME));
        verify(listGroupItemPresenterMock, never()).enable();
        verify(rightPanelViewMock, times(1)).enableEditorTab();
    }

    @Test
    public void onDisableEditorTab() {
        rightPanelPresenter.onDisableEditorTab();
        verify(listGroupItemPresenterMock, times(1)).disable();
        verify(listGroupItemPresenterMock, never()).enable();
        verify(rightPanelViewMock, times(1)).disableEditorTab();
    }

    @Test
    public void onModifyColumn() {
        rightPanelPresenter.editingColumnEnabled = true;
        rightPanelPresenter.selectedFieldItemView = null;
        rightPanelPresenter.selectedListGroupItemView = null;
        rightPanelPresenter.onModifyColumn();
        verify(eventBusMock, never()).fireEvent(isA(SetInstanceHeaderEvent.class));
        verify(eventBusMock, never()).fireEvent(isA(SetPropertyHeaderEvent.class));

        reset(eventBusMock);
        rightPanelPresenter.selectedListGroupItemView = null;
        rightPanelPresenter.selectedFieldItemView = selectedFieldItemViewMock;
        rightPanelPresenter.onModifyColumn();
        verify(eventBusMock, times(1)).fireEvent(isA(SetPropertyHeaderEvent.class));
        verify(eventBusMock, never()).fireEvent(isA(SetInstanceHeaderEvent.class));

        reset(eventBusMock);
        rightPanelPresenter.selectedListGroupItemView = selectedListGroupItemViewMock;
        rightPanelPresenter.selectedFieldItemView = null;
        rightPanelPresenter.onModifyColumn();
        verify(eventBusMock, never()).fireEvent(isA(SetPropertyHeaderEvent.class));
        verify(eventBusMock, times(1)).fireEvent(isA(SetInstanceHeaderEvent.class));

    }

    @Test
    public void filterTerm() {
        String key = getRandomString();
        String search = String.join(";", IntStream.range(0, 4)
                .mapToObj(i -> getRandomString())
                .collect(Collectors.toSet()));
        assertTrue(rightPanelPresenter.filterTerm(key, key, false));
        assertFalse(rightPanelPresenter.filterTerm(key, key, true));

        assertFalse(rightPanelPresenter.filterTerm(key, search, false));
        assertTrue(rightPanelPresenter.filterTerm(key, search, true));

        search += ";" + key;
        assertTrue(rightPanelPresenter.filterTerm(key, search, false));
        assertFalse(rightPanelPresenter.filterTerm(key, search, true));

    }

}