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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.Style;
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

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
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
public class TestToolsPresenterTest extends AbstractTestToolsTest {

    @Mock
    private TestToolsView testToolsViewMock;

    @Mock
    private LabelElement dataObjectListContainerSeparatorMock;

    @Mock
    private Style dataObjectListContainerSeparatorStyleMock;

    @Mock
    private DivElement dataObjectListContainerMock;

    @Mock
    private LabelElement simpleJavaTypeListContainerSeparatorMock;

    @Mock
    private Style simpleJavaTypeListContainerSeparatorStyleMock;

    @Mock
    private DivElement simpleJavaTypeListContainerMock;

    @Mock
    private LabelElement instanceListContainerSeparatorMock;

    @Mock
    private Style instanceListContainerSeparatorStyleMock;

    @Mock
    private DivElement instanceListContainerMock;

    @Mock
    private LabelElement simpleJavaInstanceListContainerSeparatorMock;

    @Mock
    private Style simpleJavaInstanceListContainerSeparatorStyleMock;

    @Mock
    private DivElement simpleJavaInstanceListContainerMock;

    @Mock
    private ListGroupItemPresenter listGroupItemPresenterMock;

    @Mock
    private ListGroupItemView selectedListGroupItemViewMock;

    @Mock
    private FieldItemView selectedFieldItemViewMock;

    @Mock
    private EventBus eventBusMock;

    private TestToolsPresenter testToolsPresenter;

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

        when(dataObjectListContainerSeparatorMock.getStyle()).thenReturn(dataObjectListContainerSeparatorStyleMock);
        when(simpleJavaTypeListContainerSeparatorMock.getStyle()).thenReturn(simpleJavaTypeListContainerSeparatorStyleMock);
        when(instanceListContainerSeparatorMock.getStyle()).thenReturn(instanceListContainerSeparatorStyleMock);
        when(simpleJavaInstanceListContainerSeparatorMock.getStyle()).thenReturn(simpleJavaInstanceListContainerSeparatorStyleMock);

        when(testToolsViewMock.getDataObjectListContainerSeparator()).thenReturn(dataObjectListContainerSeparatorMock);
        when(testToolsViewMock.getDataObjectListContainer()).thenReturn(dataObjectListContainerMock);
        when(testToolsViewMock.getSimpleJavaTypeListContainerSeparator()).thenReturn(simpleJavaTypeListContainerSeparatorMock);
        when(testToolsViewMock.getSimpleJavaTypeListContainer()).thenReturn(simpleJavaTypeListContainerMock);
        when(testToolsViewMock.getInstanceListContainerSeparator()).thenReturn(instanceListContainerSeparatorMock);
        when(testToolsViewMock.getInstanceListContainer()).thenReturn(instanceListContainerMock);
        when(testToolsViewMock.getSimpleJavaInstanceListContainerSeparator()).thenReturn(simpleJavaInstanceListContainerSeparatorMock);
        when(testToolsViewMock.getSimpleJavaInstanceListContainer()).thenReturn(simpleJavaInstanceListContainerMock);
        when(listGroupItemPresenterMock.getDivElement(FACT_NAME, FACT_MODEL_TREE)).thenReturn(dataObjectListContainerMock);
        this.testToolsPresenter = spy(new TestToolsPresenter(testToolsViewMock, listGroupItemPresenterMock) {
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
        testToolsPresenter.setup();
        verify(testToolsViewMock, times(1)).init(testToolsPresenter);
    }

    @Test
    public void getTitle() {
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.testTools(), testToolsPresenter.getTitle());
    }

    @Test
    public void onClearSearch() {
        testToolsPresenter.onClearSearch();
        verify(testToolsViewMock, times(1)).clearInputSearch();
        verify(testToolsViewMock, times(1)).hideClearButton();
    }

    @Test
    public void onClearNameField() {
        testToolsPresenter.onClearNameField();
        verify(testToolsViewMock, times(1)).clearNameField();
    }

    @Test
    public void onClearStatus() {
        testToolsPresenter.onClearStatus();
        verify(testToolsPresenter, times(1)).onClearSearch();
        verify(testToolsPresenter, times(1)).onClearNameField();
        verify(testToolsPresenter, times(1)).clearDataObjectList();
    }

    @Test
    public void clearDataObjectList() {
        testToolsPresenter.clearDataObjectList();
        verify(testToolsViewMock, times(1)).getDataObjectListContainer();
        verify(dataObjectListContainerMock, times(1)).removeAllChildren();
    }

    @Test
    public void clearSimpleJavaTypeList() {
        testToolsPresenter.clearSimpleJavaTypeList();
        verify(testToolsViewMock, times(1)).getSimpleJavaTypeListContainer();
        verify(simpleJavaTypeListContainerMock, times(1)).removeAllChildren();
    }

    @Test
    public void clearInstanceList() {
        testToolsPresenter.clearInstanceList();
        verify(testToolsViewMock, times(1)).getInstanceListContainer();
        verify(instanceListContainerMock, times(1)).removeAllChildren();
    }

    @Test
    public void clearSimpleJavaInstanceFieldList() {
        testToolsPresenter.clearSimpleJavaInstanceFieldList();
        verify(testToolsViewMock, times(1)).getSimpleJavaInstanceListContainer();
        verify(simpleJavaInstanceListContainerMock, times(1)).removeAllChildren();
    }

    @Test
    public void updateDataObjectListSeparatorNotEmpty() {
        when(dataObjectListContainerMock.getChildCount()).thenReturn(2);
        testToolsPresenter.updateDataObjectListSeparator();
        verify(dataObjectListContainerMock, times(1)).getChildCount();
        verify(testToolsViewMock, times(1)).getDataObjectListContainerSeparator();
        verify(dataObjectListContainerSeparatorStyleMock, times(1)).setDisplay(eq(Style.Display.BLOCK));
    }

    @Test
    public void updateDataObjectListSeparatorEmpty() {
        when(dataObjectListContainerMock.getChildCount()).thenReturn(0);
        testToolsPresenter.updateDataObjectListSeparator();
        verify(dataObjectListContainerMock, times(1)).getChildCount();
        verify(testToolsViewMock, times(1)).getDataObjectListContainerSeparator();
        verify(dataObjectListContainerSeparatorStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
    }

    @Test
    public void updateSimpleJavaTypeListSeparatorNotEmpty() {
        when(simpleJavaTypeListContainerMock.getChildCount()).thenReturn(2);
        testToolsPresenter.updateSimpleJavaTypeListSeparator();
        verify(simpleJavaTypeListContainerMock, times(1)).getChildCount();
        verify(testToolsViewMock, times(1)).getSimpleJavaTypeListContainerSeparator();
        verify(simpleJavaTypeListContainerSeparatorStyleMock, times(1)).setDisplay(eq(Style.Display.BLOCK));
    }

    @Test
    public void updateSimpleJavaTypeListSeparatorEmpty() {
        when(simpleJavaTypeListContainerMock.getChildCount()).thenReturn(0);
        testToolsPresenter.updateSimpleJavaTypeListSeparator();
        verify(simpleJavaTypeListContainerMock, times(1)).getChildCount();
        verify(testToolsViewMock, times(1)).getSimpleJavaTypeListContainerSeparator();
        verify(simpleJavaTypeListContainerSeparatorStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
    }

    @Test
    public void updateInstanceListSeparatorNotEmpty() {
        when(instanceListContainerMock.getChildCount()).thenReturn(2);
        testToolsPresenter.updateInstanceListSeparator();
        verify(instanceListContainerMock, times(1)).getChildCount();
        verify(testToolsViewMock, times(1)).getInstanceListContainerSeparator();
        verify(instanceListContainerSeparatorStyleMock, times(1)).setDisplay(eq(Style.Display.BLOCK));
    }

    @Test
    public void updateInstanceListSeparatorEmpty() {
        when(instanceListContainerMock.getChildCount()).thenReturn(0);
        testToolsPresenter.updateInstanceListSeparator();
        verify(instanceListContainerMock, times(1)).getChildCount();
        verify(testToolsViewMock, times(1)).getInstanceListContainerSeparator();
        verify(instanceListContainerSeparatorStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
    }

    @Test
    public void updateSimpleJavaInstanceFieldListSeparatorNotEmpty() {
        when(simpleJavaInstanceListContainerMock.getChildCount()).thenReturn(2);
        testToolsPresenter.updateSimpleJavaInstanceFieldListSeparator();
        verify(testToolsViewMock, times(1)).getSimpleJavaInstanceListContainerSeparator();
        verify(simpleJavaInstanceListContainerSeparatorStyleMock, times(1)).setDisplay(eq(Style.Display.BLOCK));
    }

    @Test
    public void updateSimpleJavaInstanceFieldListSeparatorEmpty() {
        when(simpleJavaInstanceListContainerMock.getChildCount()).thenReturn(0);
        testToolsPresenter.updateSimpleJavaInstanceFieldListSeparator();
        verify(simpleJavaInstanceListContainerMock, times(1)).getChildCount();
        verify(testToolsViewMock, times(1)).getSimpleJavaInstanceListContainerSeparator();
        verify(simpleJavaInstanceListContainerSeparatorStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
    }

    @Test
    public void getFactModelTree() {
        testToolsPresenter.setDataObjectFieldsMap(dataObjectFactTreeMap);
        String factName = getRandomFactModelTree(dataObjectFactTreeMap, 0);
        Optional<FactModelTree> retrieved = testToolsPresenter.getFactModelTreeFromFactTypeMap(factName);
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        assertEquals(dataObjectFactTreeMap.get(factName), retrieved.get());
    }

    @Test
    public void setFactTypeFieldsMap() {
        testToolsPresenter.setDataObjectFieldsMap(dataObjectFactTreeMap);
        verify(testToolsPresenter, times(dataObjectFactTreeMap.size())).addDataObjectListGroupItemView(anyString(), anyObject());
    }

    @Test
    public void onShowClearButton() {
        testToolsPresenter.onShowClearButton();
        verify(testToolsViewMock, times(1)).showClearButton();
    }

    @Test
    public void setEventBus() {
        testToolsPresenter.setEventBus(eventBusMock);
        assertEquals(eventBusMock, testToolsPresenter.eventBus);
    }

    @Test
    public void onSearchedEvent() {
        String searched = "";
        testToolsPresenter.onSearchedEvent(searched);
        verify(testToolsPresenter, times(1)).clearLists();
        testToolsPresenter.dataObjectFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(searched))
                .forEach(filteredEntry -> verify(testToolsPresenter, times(1)).addDataObjectListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenter.simpleJavaTypeFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(searched))
                .forEach(filteredEntry -> verify(testToolsPresenter, times(1)).addSimpleJavaTypeListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenter.instanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(searched))
                .forEach(filteredEntry -> verify(testToolsPresenter, times(1)).addInstanceListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenter.simpleJavaInstanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(searched))
                .forEach(filteredEntry -> verify(testToolsPresenter, times(1)).addSimpleJavaInstanceListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        verify(testToolsPresenter, times(1)).updateSeparators();
    }

    @Test
    public void onPerfectMatchSearchedEventNotEquals() {
        String search = "";
        testToolsPresenter.onPerfectMatchSearchedEvent(search, true);
        verify(testToolsPresenter, times(1)).clearLists();
        testToolsPresenter.dataObjectFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenter.filterTerm(entry.getKey(), search, true))
                .forEach(filteredEntry -> verify(testToolsPresenter, times(1)).addDataObjectListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenter.simpleJavaTypeFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenter.filterTerm(entry.getKey(), search, true))
                .forEach(filteredEntry -> verify(testToolsPresenter, times(1)).addSimpleJavaTypeListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenter.instanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenter.filterTerm(entry.getKey(), search, true))
                .forEach(filteredEntry -> verify(testToolsPresenter, times(1)).addInstanceListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenter.simpleJavaInstanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenter.filterTerm(entry.getKey(), search, true))
                .forEach(filteredEntry -> verify(testToolsPresenter, times(1)).addSimpleJavaInstanceListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        verify(testToolsPresenter, times(1)).updateSeparators();
    }

    @Test
    public void onPerfectMatchSearchedEventEquals() {
        String search = "";
        testToolsPresenter.onPerfectMatchSearchedEvent(search, false);
        verify(testToolsPresenter, times(1)).clearLists();
        testToolsPresenter.dataObjectFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenter.filterTerm(entry.getKey(), search, false))
                .forEach(filteredEntry -> verify(testToolsPresenter, times(1)).addDataObjectListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenter.simpleJavaTypeFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenter.filterTerm(entry.getKey(), search, false))
                .forEach(filteredEntry -> verify(testToolsPresenter, times(1)).addSimpleJavaTypeListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenter.instanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenter.filterTerm(entry.getKey(), search, false))
                .forEach(filteredEntry -> verify(testToolsPresenter, times(1)).addInstanceListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenter.simpleJavaInstanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenter.filterTerm(entry.getKey(), search, false))
                .forEach(filteredEntry -> verify(testToolsPresenter, times(1)).addSimpleJavaInstanceListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        verify(testToolsPresenter, times(1)).updateSeparators();
    }

    @Test
    public void addListGroupItemView() {
        testToolsPresenter.addDataObjectListGroupItemView(FACT_NAME, FACT_MODEL_TREE);
        verify(testToolsViewMock, times(1)).getDataObjectListContainer();
        verify(listGroupItemPresenterMock, times(1)).getDivElement(eq(FACT_NAME), eq(FACT_MODEL_TREE));
        verify(dataObjectListContainerMock, times(1)).appendChild(anyObject());
    }

    @Test
    public void onEnableEditorTabWithoutFactName() {
        testToolsPresenter.onEnableEditorTab();
        verify(testToolsPresenter, times(1)).onDisableEditorTab();
        verify(testToolsPresenter, times(1)).onSearchedEvent(eq(""));
        verify(listGroupItemPresenterMock, times(1)).enable();
        verify(listGroupItemPresenterMock, never()).enable(anyString());
        verify(testToolsViewMock, times(1)).enableEditorTab();
        verify(testToolsViewMock, times(1)).enableSearch();
    }

    @Test
    public void onEnableEditorTabWithFactName_NotEqualsSearch() {
        testToolsPresenter.onEnableEditorTab(FACT_NAME, null, false);
        verify(testToolsPresenter, times(1)).onDisableEditorTab();
        verify(testToolsPresenter, times(1)).onPerfectMatchSearchedEvent(eq(FACT_NAME), eq(false));
        verify(testToolsPresenter, times(1)).updateInstanceIsAssignedStatus(eq(FACT_NAME));
        verify(testToolsViewMock, never()).enableSearch();
        verify(listGroupItemPresenterMock, times(1)).enable(eq(FACT_NAME));
        verify(listGroupItemPresenterMock, never()).enable();
        verify(testToolsViewMock, times(1)).enableEditorTab();
        verify(listGroupItemPresenterMock, never()).selectProperty(anyString(), any());
    }

    @Test
    public void onEnableEditorTabWithFactName_EqualSearch() {
        testToolsPresenter.onEnableEditorTab(FACT_NAME, null, true);
        verify(testToolsPresenter, times(1)).onDisableEditorTab();
        verify(testToolsPresenter, times(1)).onPerfectMatchSearchedEvent(eq(FACT_NAME), eq(true));
        verify(testToolsPresenter, never()).updateInstanceIsAssignedStatus(anyString());
        verify(testToolsViewMock, times(1)).enableSearch();
        verify(listGroupItemPresenterMock, times(1)).enable(eq(FACT_NAME));
        verify(listGroupItemPresenterMock, never()).enable();
        verify(testToolsViewMock, times(1)).enableEditorTab();
        verify(listGroupItemPresenterMock, never()).selectProperty(anyString(), any());
    }

    @Test
    public void onEnableEditorTabWithProperties() {
        List<String> propertiesName = Arrays.asList("property1", "property2");
        testToolsPresenter.onEnableEditorTab(FACT_NAME, propertiesName, false);
        verify(testToolsPresenter, times(1)).onDisableEditorTab();
        verify(testToolsPresenter, times(1)).onPerfectMatchSearchedEvent(eq(FACT_NAME), eq(false));
        verify(listGroupItemPresenterMock, times(1)).enable(eq(FACT_NAME));
        verify(listGroupItemPresenterMock, never()).enable();
        verify(testToolsViewMock, never()).disableSearch();
        verify(testToolsViewMock, times(1)).enableEditorTab();
        verify(listGroupItemPresenterMock, times(1)).selectProperty(eq(FACT_NAME), eq(propertiesName));
    }

    @Test
    public void onDisableEditorTab() {
        testToolsPresenter.onDisableEditorTab();
        verify(listGroupItemPresenterMock, times(1)).disable();
        verify(listGroupItemPresenterMock, never()).enable();
        verify(testToolsViewMock, times(1)).disableEditorTab();
    }

    @Test
    public void setSelectedElement_WithInstanceAssigned() {
        when(selectedListGroupItemViewMock.isInstanceAssigned()).thenReturn(true);
        testToolsPresenter.setSelectedElement(selectedListGroupItemViewMock);
        verify(selectedListGroupItemViewMock, times(1)).isInstanceAssigned();
        verify(testToolsViewMock, times(1)).disableAddButton();
        assertNull(testToolsPresenter.selectedFieldItemView);
        assertEquals(selectedListGroupItemViewMock, testToolsPresenter.selectedListGroupItemView);
    }

    @Test
    public void setSelectedElement_WithoutInstanceAssigned() {
        when(selectedListGroupItemViewMock.isInstanceAssigned()).thenReturn(false);
        testToolsPresenter.setSelectedElement(selectedListGroupItemViewMock);
        verify(selectedListGroupItemViewMock, times(1)).isInstanceAssigned();
        verify(testToolsViewMock, times(1)).enableAddButton();
        assertNull(testToolsPresenter.selectedFieldItemView);
        assertEquals(selectedListGroupItemViewMock, testToolsPresenter.selectedListGroupItemView);
    }

    @Test
    public void onModifyColumn() {
        testToolsPresenter.editingColumnEnabled = true;
        testToolsPresenter.selectedFieldItemView = null;
        testToolsPresenter.selectedListGroupItemView = null;
        testToolsPresenter.onModifyColumn();
        verify(eventBusMock, never()).fireEvent(isA(SetInstanceHeaderEvent.class));
        verify(eventBusMock, never()).fireEvent(isA(SetPropertyHeaderEvent.class));

        reset(eventBusMock);
        testToolsPresenter.selectedListGroupItemView = null;
        testToolsPresenter.selectedFieldItemView = selectedFieldItemViewMock;
        testToolsPresenter.onModifyColumn();
        verify(eventBusMock, times(1)).fireEvent(isA(SetPropertyHeaderEvent.class));
        verify(eventBusMock, never()).fireEvent(isA(SetInstanceHeaderEvent.class));

        reset(eventBusMock);
        testToolsPresenter.selectedListGroupItemView = selectedListGroupItemViewMock;
        testToolsPresenter.selectedFieldItemView = null;
        testToolsPresenter.onModifyColumn();
        verify(eventBusMock, never()).fireEvent(isA(SetPropertyHeaderEvent.class));
        verify(eventBusMock, times(1)).fireEvent(isA(SetInstanceHeaderEvent.class));
    }

    @Test
    public void clearLists() {
        testToolsPresenter.clearLists();
        verify(testToolsPresenter, times(1)).clearDataObjectList();
        verify(testToolsPresenter, times(1)).clearSimpleJavaTypeList();
        verify(testToolsPresenter, times(1)).clearInstanceList();
        verify(testToolsPresenter, times(1)).clearSimpleJavaInstanceFieldList();
    }

    @Test
    public void updateSeparators() {
        testToolsPresenter.updateSeparators();
        verify(testToolsPresenter, times(1)).updateDataObjectListSeparator();
        verify(testToolsPresenter, times(1)).updateSimpleJavaTypeListSeparator();
        verify(testToolsPresenter, times(1)).updateInstanceListSeparator();
        verify(testToolsPresenter, times(1)).updateSimpleJavaInstanceFieldListSeparator();
    }

    @Test
    public void filterTerm() {
        String key = getRandomString();
        String search = String.join(";", IntStream.range(0, 4)
                .mapToObj(i -> getRandomString())
                .collect(Collectors.toSet()));
        assertTrue(testToolsPresenter.filterTerm(key, key, false));
        assertFalse(testToolsPresenter.filterTerm(key, key, true));

        assertFalse(testToolsPresenter.filterTerm(key, search, false));
        assertTrue(testToolsPresenter.filterTerm(key, search, true));

        search += ";" + key;
        assertTrue(testToolsPresenter.filterTerm(key, search, false));
        assertFalse(testToolsPresenter.filterTerm(key, search, true));
    }

    @Test
    public void resetTest() {
        testToolsPresenter.reset();
        verify(testToolsViewMock, times(1)).reset();
        verify(listGroupItemPresenterMock, times(1)).reset();
    }

    @Test
    public void checkInstanceIsAssigned_NotPresent() {
        String instance = "CHECK_INSTANCE";
        testToolsPresenter.updateInstanceIsAssignedStatus(instance);
        verify(listGroupItemPresenterMock, times(1)).setInstanceAssigned(eq(instance), eq(false));
    }

    @Test
    public void checkInstanceIsAssigned_Present() {
        String instance = "CHECK_INSTANCE";
        FactModelTree factModel = new FactModelTree(instance, FACT_PACKAGE, getMockSimpleProperties(), new HashMap<>());
        dataObjectFactTreeMap.put(instance, factModel);
        testToolsPresenter.updateInstanceIsAssignedStatus(instance);
        verify(listGroupItemPresenterMock, times(1)).setInstanceAssigned(eq(instance), eq(true));
    }

    @Test
    public void checkInstanceIsAssigned_EmptyString() {
        String instance = "";
        testToolsPresenter.updateInstanceIsAssignedStatus(instance);
        verify(listGroupItemPresenterMock, never()).setInstanceAssigned(anyString(), anyBoolean());
    }
}