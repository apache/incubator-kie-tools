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
import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
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
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_NAME_2;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_PACKAGE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FILTER_TERM;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.PROPERTY_NAME;
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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TestToolsPresenterTest extends AbstractTestToolsTest {

    @Mock
    private TestToolsView testToolsViewMock;

    @Mock
    private DivElement divItemContainerMock;

    @Mock
    private ListGroupItemPresenter listGroupItemPresenterMock;

    @Mock
    private ListGroupItemView selectedListGroupItemViewMock;

    @Mock
    private FieldItemView selectedFieldItemViewMock;

    @Mock
    private EventBus eventBusMock;

    private TestToolsPresenter testToolsPresenterSpy;

    @Before
    public void setup() {
        super.setup();
        final String firstKey = dataObjectFactTreeMap.firstKey();
        final FactModelTree factModelTree = dataObjectFactTreeMap.get(firstKey);
        final String firstPropertyKey = (String) new ArrayList(factModelTree.getSimpleProperties().keySet()).get(0);
        final String firstPropertyClass = factModelTree.getSimpleProperties().get(firstPropertyKey);

        when(selectedListGroupItemViewMock.getActualClassName()).thenReturn(firstKey);

        when(selectedFieldItemViewMock.getFullPath()).thenReturn(firstKey);
        when(selectedFieldItemViewMock.getFieldName()).thenReturn(firstPropertyKey);
        when(selectedFieldItemViewMock.getClassName()).thenReturn(firstPropertyClass);

        when(listGroupItemPresenterMock.getDivElement(FACT_NAME, FACT_MODEL_TREE)).thenReturn(divItemContainerMock);
        this.testToolsPresenterSpy = spy(new TestToolsPresenter(testToolsViewMock, listGroupItemPresenterMock) {
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
        testToolsPresenterSpy.setup();
        verify(testToolsViewMock, times(1)).init(testToolsPresenterSpy);
    }

    @Test
    public void getTitle() {
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.testTools(), testToolsPresenterSpy.getTitle());
    }

    @Test
    public void onClearSearch() {
        testToolsPresenterSpy.onClearSearch();
        verify(testToolsViewMock, times(1)).clearInputSearch();
        verify(testToolsViewMock, times(1)).hideClearButton();
        verify(testToolsPresenterSpy, times(1)).onSearchedEvent(eq(""));
    }

    @Test
    public void onUndoSearch() {
        when(listGroupItemPresenterMock.getFilterTerm()).thenReturn(FILTER_TERM);
        testToolsPresenterSpy.onUndoSearch();
        verify(testToolsViewMock, times(1)).clearInputSearch();
        verify(testToolsViewMock, times(1)).hideClearButton();
        verify(listGroupItemPresenterMock, times(1)).getFilterTerm();
        verify(testToolsPresenterSpy, times(1)).onPerfectMatchSearchedEvent(eq(FILTER_TERM), eq(true));
    }

    @Test
    public void onClearStatus() {
        testToolsPresenterSpy.onClearStatus();
        verify(testToolsPresenterSpy, times(1)).onClearSearch();
        verify(testToolsPresenterSpy, times(1)).clearDataObjectList();
    }

    @Test
    public void clearDataObjectList() {
        testToolsPresenterSpy.clearDataObjectList();
        verify(testToolsViewMock, times(1)).clearDataObjectList();
    }

    @Test
    public void clearSimpleJavaTypeList() {
        testToolsPresenterSpy.clearSimpleJavaTypeList();
        verify(testToolsViewMock, times(1)).clearSimpleJavaTypeList();
    }

    @Test
    public void clearInstanceList() {
        testToolsPresenterSpy.clearInstanceList();
        verify(testToolsViewMock, times(1)).clearInstanceList();
    }

    @Test
    public void clearSimpleJavaInstanceFieldList() {
        testToolsPresenterSpy.clearSimpleJavaInstanceFieldList();
        verify(testToolsViewMock, times(1)).clearSimpleJavaInstanceFieldList();
    }

   @Test
    public void updateInstanceListSeparatorNotEmptySIMULATION() {
        testToolsPresenterSpy.gridWidget = GridWidget.SIMULATION;
        testToolsPresenterSpy.updateInstanceListSeparator();
        verify(testToolsViewMock, times(1)).updateInstanceListSeparator(eq(true));
    }

    @Test
    public void updateInstanceListSeparatorNotEmptyBACKGROUND() {
        testToolsPresenterSpy.gridWidget = GridWidget.BACKGROUND;
        testToolsPresenterSpy.updateInstanceListSeparator();
        verify(testToolsViewMock, times(1)).updateInstanceListSeparator(eq(false));
    }

    @Test
    public void updateInstanceListSeparatorEmpty() {
        testToolsPresenterSpy.updateInstanceListSeparator();
        verify(testToolsViewMock, times(1)).updateInstanceListSeparator(eq(false));
    }

    @Test
    public void showInstanceListContainerSeparator() {
        testToolsPresenterSpy.showInstanceListContainerSeparator(true);
        verify(testToolsViewMock, times(1)).showInstanceListContainerSeparator(eq(true));
    }

    @Test
    public void getFactModelTree() {
        testToolsPresenterSpy.setDataObjectFieldsMap(dataObjectFactTreeMap);
        String factName = getRandomFactModelTree(dataObjectFactTreeMap, 0);
        Optional<FactModelTree> retrieved = testToolsPresenterSpy.getFactModelTreeFromFactTypeMap(factName);
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        assertEquals(dataObjectFactTreeMap.get(factName), retrieved.get());
    }

    @Test
    public void setFactTypeFieldsMap() {
        testToolsPresenterSpy.setDataObjectFieldsMap(dataObjectFactTreeMap);
        verify(testToolsPresenterSpy, times(dataObjectFactTreeMap.size())).addDataObjectListGroupItemView(anyString(), anyObject());
    }

    @Test
    public void onShowClearButton() {
        testToolsPresenterSpy.onShowClearButton();
        verify(testToolsViewMock, times(1)).showClearButton();
    }

    @Test
    public void setEventBus() {
        testToolsPresenterSpy.setEventBus(eventBusMock);
        assertEquals(eventBusMock, testToolsPresenterSpy.eventBus);
    }

    @Test
    public void setGridWidgetSIMULATION() {
        testToolsPresenterSpy.setGridWidget(GridWidget.SIMULATION);
        verify(testToolsPresenterSpy, times(1)).showInstanceListContainerSeparator(eq(true));
        verify(testToolsPresenterSpy, never()).hideInstances();
    }

    @Test
    public void setGridWidgetBACKGROUND() {
        testToolsPresenterSpy.setGridWidget(GridWidget.BACKGROUND);
        verify(testToolsPresenterSpy, times(1)).hideInstances();
        verify(testToolsPresenterSpy, never()).showInstanceListContainerSeparator(eq(true));
    }

    @Test
    public void onSearchedEvent() {
        String searched = "";
        testToolsPresenterSpy.onSearchedEvent(searched);
        verify(testToolsPresenterSpy, times(1)).clearLists();
        testToolsPresenterSpy.dataObjectFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(searched))
                .forEach(filteredEntry -> verify(testToolsPresenterSpy, times(1)).addDataObjectListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenterSpy.simpleJavaTypeFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(searched))
                .forEach(filteredEntry -> verify(testToolsPresenterSpy, times(1)).addSimpleJavaTypeListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenterSpy.instanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(searched))
                .forEach(filteredEntry -> verify(testToolsPresenterSpy, times(1)).addInstanceListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenterSpy.simpleJavaInstanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(searched))
                .forEach(filteredEntry -> verify(testToolsPresenterSpy, times(1)).addSimpleJavaInstanceListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        verify(testToolsPresenterSpy, times(1)).updateInstanceListSeparator();
    }

    @Test
    public void onPerfectMatchSearchedEventNotEquals() {
        String search = "";
        testToolsPresenterSpy.onPerfectMatchSearchedEvent(search, true);
        verify(testToolsPresenterSpy, times(1)).clearLists();
        testToolsPresenterSpy.dataObjectFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenterSpy.filterTerm(entry.getKey(), search, true))
                .forEach(filteredEntry -> verify(testToolsPresenterSpy, times(1)).addDataObjectListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenterSpy.simpleJavaTypeFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenterSpy.filterTerm(entry.getKey(), search, true))
                .forEach(filteredEntry -> verify(testToolsPresenterSpy, times(1)).addSimpleJavaTypeListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenterSpy.instanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenterSpy.filterTerm(entry.getKey(), search, true))
                .forEach(filteredEntry -> verify(testToolsPresenterSpy, times(1)).addInstanceListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenterSpy.simpleJavaInstanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenterSpy.filterTerm(entry.getKey(), search, true))
                .forEach(filteredEntry -> verify(testToolsPresenterSpy, times(1)).addSimpleJavaInstanceListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        verify(testToolsPresenterSpy, times(1)).updateInstanceListSeparator();
    }

    @Test
    public void onPerfectMatchSearchedEventEquals() {
        String search = "";
        testToolsPresenterSpy.onPerfectMatchSearchedEvent(search, false);
        verify(testToolsPresenterSpy, times(1)).clearLists();
        testToolsPresenterSpy.dataObjectFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenterSpy.filterTerm(entry.getKey(), search, false))
                .forEach(filteredEntry -> verify(testToolsPresenterSpy, times(1)).addDataObjectListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenterSpy.simpleJavaTypeFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenterSpy.filterTerm(entry.getKey(), search, false))
                .forEach(filteredEntry -> verify(testToolsPresenterSpy, times(1)).addSimpleJavaTypeListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenterSpy.instanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenterSpy.filterTerm(entry.getKey(), search, false))
                .forEach(filteredEntry -> verify(testToolsPresenterSpy, times(1)).addInstanceListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        testToolsPresenterSpy.simpleJavaInstanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> testToolsPresenterSpy.filterTerm(entry.getKey(), search, false))
                .forEach(filteredEntry -> verify(testToolsPresenterSpy, times(1)).addSimpleJavaInstanceListGroupItemView(eq(filteredEntry.getKey()), eq(filteredEntry.getValue())));
        verify(testToolsPresenterSpy, times(1)).updateInstanceListSeparator();
    }

    @Test
    public void addListGroupItemView() {
        testToolsPresenterSpy.addDataObjectListGroupItemView(FACT_NAME, FACT_MODEL_TREE);
        verify(testToolsViewMock, times(1)).addDataObjectListGroupItem(eq(divItemContainerMock));
    }

    @Test
    public void addInstanceListGroupItemView() {
        testToolsPresenterSpy.addInstanceListGroupItemView(FACT_NAME, FACT_MODEL_TREE);
        verify(testToolsViewMock, times(1)).addInstanceListGroupItem(eq(divItemContainerMock));
    }

    @Test
    public void addSimpleJavaTypeListGroupItemView() {
        testToolsPresenterSpy.addSimpleJavaTypeListGroupItemView(FACT_NAME, FACT_MODEL_TREE);
        verify(testToolsViewMock, times(1)).addSimpleJavaTypeListGroupItem(eq(divItemContainerMock));
    }

    @Test
    public void addSimpleJavaInstanceListGroupItemView() {
        testToolsPresenterSpy.addSimpleJavaInstanceListGroupItemView(FACT_NAME, FACT_MODEL_TREE);
        verify(testToolsViewMock, times(1)).addSimpleJavaInstanceListGroupItem(eq(divItemContainerMock));
    }

    @Test
    public void onEnableEditorTabWithoutFactName() {
        testToolsPresenterSpy.onEnableEditorTab();
        verify(testToolsPresenterSpy, times(1)).onDisableEditorTab();
        verify(testToolsPresenterSpy, times(1)).onSearchedEvent(eq(""));
        verify(listGroupItemPresenterMock, times(1)).enable();
        verify(listGroupItemPresenterMock, never()).enable(anyString());
        verify(testToolsViewMock, times(1)).enableEditorTab();
        verify(testToolsViewMock, times(1)).enableSearch();
    }

    @Test
    public void onEnableEditorTabWithFactName_NotEqualsSearch() {
        testToolsPresenterSpy.onEnableEditorTab(FACT_NAME, null, false);
        verify(testToolsPresenterSpy, times(1)).onDisableEditorTab();
        verify(testToolsPresenterSpy, times(1)).onPerfectMatchSearchedEvent(eq(FACT_NAME), eq(false));
        verify(testToolsPresenterSpy, times(1)).updateInstanceIsAssignedStatus(eq(FACT_NAME));
        verify(testToolsViewMock, never()).enableSearch();
        verify(listGroupItemPresenterMock, times(1)).enable(eq(FACT_NAME));
        verify(listGroupItemPresenterMock, never()).enable();
        verify(testToolsViewMock, times(1)).enableEditorTab();
        verify(listGroupItemPresenterMock, never()).selectProperty(anyString(), any());
    }

    @Test
    public void onEnableEditorTabWithFactName_EqualSearch() {
        testToolsPresenterSpy.onEnableEditorTab(FACT_NAME, null, true);
        verify(testToolsPresenterSpy, times(1)).onDisableEditorTab();
        verify(testToolsPresenterSpy, times(1)).onPerfectMatchSearchedEvent(eq(FACT_NAME), eq(true));
        verify(testToolsPresenterSpy, never()).updateInstanceIsAssignedStatus(anyString());
        verify(testToolsViewMock, times(1)).enableSearch();
        verify(listGroupItemPresenterMock, times(1)).enable(eq(FACT_NAME));
        verify(listGroupItemPresenterMock, never()).enable();
        verify(testToolsViewMock, times(1)).enableEditorTab();
        verify(listGroupItemPresenterMock, never()).selectProperty(anyString(), any());
    }

    @Test
    public void onEnableEditorTabWithProperties() {
        List<String> propertiesName = Arrays.asList("property1", "property2");
        testToolsPresenterSpy.onEnableEditorTab(FACT_NAME, propertiesName, false);
        verify(testToolsPresenterSpy, times(1)).onDisableEditorTab();
        verify(testToolsPresenterSpy, times(1)).onPerfectMatchSearchedEvent(eq(FACT_NAME), eq(false));
        verify(listGroupItemPresenterMock, times(1)).enable(eq(FACT_NAME));
        verify(listGroupItemPresenterMock, never()).enable();
        verify(testToolsViewMock, never()).disableSearch();
        verify(testToolsViewMock, times(1)).enableEditorTab();
        verify(listGroupItemPresenterMock, times(1)).selectProperty(eq(FACT_NAME), eq(propertiesName));
    }

    @Test
    public void onDisableEditorTab() {
        testToolsPresenterSpy.onDisableEditorTab();
        verify(listGroupItemPresenterMock, times(1)).disable();
        verify(listGroupItemPresenterMock, never()).enable();
        verify(testToolsViewMock, times(1)).disableEditorTab();
    }

    @Test
    public void setSelectedElement_InstanceAssigned() {
        when(selectedListGroupItemViewMock.getFactName()).thenReturn(FACT_NAME);
        when(listGroupItemPresenterMock.getFilterTerm()).thenReturn(FILTER_TERM);
        testToolsPresenterSpy.setSelectedElement(selectedListGroupItemViewMock);
        verify(testToolsPresenterSpy, times(1)).filterTerm(eq(FACT_NAME), eq(FILTER_TERM), eq(false));
        verify(testToolsViewMock, times(1)).disableAddButton();
        assertNull(testToolsPresenterSpy.selectedFieldItemView);
        assertEquals(selectedListGroupItemViewMock, testToolsPresenterSpy.selectedListGroupItemView);
    }

    @Test
    public void setSelectedElement_InstanceNotAssigned() {
        when(selectedListGroupItemViewMock.getFactName()).thenReturn(FACT_NAME_2);
        when(listGroupItemPresenterMock.getFilterTerm()).thenReturn(FILTER_TERM);
        testToolsPresenterSpy.setSelectedElement(selectedListGroupItemViewMock);
        verify(testToolsPresenterSpy, times(1)).filterTerm(eq(FACT_NAME_2), eq(FILTER_TERM), eq(false));
        verify(testToolsViewMock, times(1)).enableAddButton();
        assertNull(testToolsPresenterSpy.selectedFieldItemView);
        assertEquals(selectedListGroupItemViewMock, testToolsPresenterSpy.selectedListGroupItemView);
    }

    @Test
    public void setSelectedElementProperty_InstanceAssigned() {
        when(selectedFieldItemViewMock.isCheckShown()).thenReturn(true);
        when(listGroupItemPresenterMock.isInstanceAssigned(FACT_NAME)).thenReturn(true);
        when(selectedFieldItemViewMock.getFullPath()).thenReturn(FACT_NAME);
        when(listGroupItemPresenterMock.getFilterTerm()).thenReturn(FILTER_TERM);
        testToolsPresenterSpy.setSelectedElement(selectedFieldItemViewMock);
        verify(listGroupItemPresenterMock, times(1)).isInstanceAssigned(eq(FACT_NAME));
        verify(testToolsPresenterSpy, times(1)).filterTerm(eq(FACT_NAME), eq(FILTER_TERM), eq(true));
        verify(testToolsViewMock, times(1)).enableAddButton();
        assertNull(testToolsPresenterSpy.selectedListGroupItemView);
        assertEquals(selectedFieldItemViewMock, testToolsPresenterSpy.selectedFieldItemView);
    }

    @Test
    public void setSelectedElementProperty_CheckNotShown() {
        when(selectedFieldItemViewMock.isCheckShown()).thenReturn(false);
        when(listGroupItemPresenterMock.isInstanceAssigned(FACT_NAME)).thenReturn(true);
        when(selectedFieldItemViewMock.getFullPath()).thenReturn(FACT_NAME);
        when(listGroupItemPresenterMock.getFilterTerm()).thenReturn(FILTER_TERM);
        testToolsPresenterSpy.setSelectedElement(selectedFieldItemViewMock);
        verify(listGroupItemPresenterMock, times(1)).isInstanceAssigned(eq(FACT_NAME));
        verify(testToolsPresenterSpy, never()).filterTerm(anyString(), anyString(), anyBoolean());
        verify(testToolsViewMock, times(1)).disableAddButton();
        assertNull(testToolsPresenterSpy.selectedListGroupItemView);
        assertEquals(selectedFieldItemViewMock, testToolsPresenterSpy.selectedFieldItemView);
    }

    @Test
    public void setSelectedElementProperty_InstanceNotAssignedFactNameAlreadyAssigned() {
        when(selectedFieldItemViewMock.isCheckShown()).thenReturn(true);
        when(listGroupItemPresenterMock.isInstanceAssigned(FACT_NAME)).thenReturn(false);
        when(selectedFieldItemViewMock.getFullPath()).thenReturn(FACT_NAME);
        when(listGroupItemPresenterMock.getFilterTerm()).thenReturn(FILTER_TERM);
        testToolsPresenterSpy.setSelectedElement(selectedFieldItemViewMock);
        verify(listGroupItemPresenterMock, times(1)).isInstanceAssigned(eq(FACT_NAME));
        verify(testToolsPresenterSpy, times(1)).filterTerm(eq(FACT_NAME), eq(FILTER_TERM), eq(false));
        verify(testToolsViewMock, times(1)).disableAddButton();
        assertNull(testToolsPresenterSpy.selectedListGroupItemView);
        assertEquals(selectedFieldItemViewMock, testToolsPresenterSpy.selectedFieldItemView);
    }

    @Test
    public void setSelectedElementProperty_InstanceNotAssignedFactNameAlreadyAssigned_NestedProperties() {
        when(selectedFieldItemViewMock.isCheckShown()).thenReturn(true);
        when(listGroupItemPresenterMock.isInstanceAssigned(FACT_NAME)).thenReturn(false);
        when(selectedFieldItemViewMock.getFullPath()).thenReturn(FACT_NAME + "." + PROPERTY_NAME);
        when(listGroupItemPresenterMock.getFilterTerm()).thenReturn(FILTER_TERM);
        testToolsPresenterSpy.setSelectedElement(selectedFieldItemViewMock);
        verify(listGroupItemPresenterMock, times(1)).isInstanceAssigned(eq(FACT_NAME));
        verify(testToolsPresenterSpy, times(1)).filterTerm(eq(FACT_NAME), eq(FILTER_TERM), eq(false));
        verify(testToolsViewMock, times(1)).disableAddButton();
        assertNull(testToolsPresenterSpy.selectedListGroupItemView);
        assertEquals(selectedFieldItemViewMock, testToolsPresenterSpy.selectedFieldItemView);
    }

    @Test
    public void setSelectedElementProperty_InstanceNotAssignedFactNameNotAssigned() {
        when(selectedFieldItemViewMock.isCheckShown()).thenReturn(true);
        when(listGroupItemPresenterMock.isInstanceAssigned(FACT_NAME_2)).thenReturn(false);
        when(selectedFieldItemViewMock.getFullPath()).thenReturn(FACT_NAME_2);
        when(listGroupItemPresenterMock.getFilterTerm()).thenReturn(FILTER_TERM);
        testToolsPresenterSpy.setSelectedElement(selectedFieldItemViewMock);
        verify(listGroupItemPresenterMock, times(1)).isInstanceAssigned(eq(FACT_NAME_2));
        verify(testToolsPresenterSpy, times(1)).filterTerm(eq(FACT_NAME_2), eq(FILTER_TERM), eq(false));
        verify(testToolsViewMock, times(1)).enableAddButton();
        assertNull(testToolsPresenterSpy.selectedListGroupItemView);
        assertEquals(selectedFieldItemViewMock, testToolsPresenterSpy.selectedFieldItemView);
    }

    @Test
    public void onModifyColumn_NoSelection() {
        testToolsPresenterSpy.editingColumnEnabled = true;
        testToolsPresenterSpy.selectedFieldItemView = null;
        testToolsPresenterSpy.selectedListGroupItemView = null;
        testToolsPresenterSpy.onModifyColumn();
        verify(selectedListGroupItemViewMock, never()).getActualClassName();
        verify(selectedFieldItemViewMock, never()).getFullPath();
        verify(selectedFieldItemViewMock, never()).getFieldName();
        verify(selectedFieldItemViewMock, never()).getClassName();
        verify(eventBusMock, never()).fireEvent(isA(SetInstanceHeaderEvent.class));
        verify(eventBusMock, never()).fireEvent(isA(SetPropertyHeaderEvent.class));
    }

    @Test
    public void onModifyColumn_FieldItemSelected() {
        testToolsPresenterSpy.editingColumnEnabled = true;
        testToolsPresenterSpy.selectedListGroupItemView = null;
        testToolsPresenterSpy.selectedFieldItemView = selectedFieldItemViewMock;
        testToolsPresenterSpy.onModifyColumn();
        verify(selectedListGroupItemViewMock, never()).getActualClassName();
        verify(selectedFieldItemViewMock, times(2)).getFullPath();
        verify(selectedFieldItemViewMock, times(1)).getFieldName();
        verify(selectedFieldItemViewMock, times(1)).getClassName();
        verify(eventBusMock, times(1)).fireEvent(isA(SetPropertyHeaderEvent.class));
        verify(eventBusMock, never()).fireEvent(isA(SetInstanceHeaderEvent.class));
    }

    @Test
    public void onModifyColumn_ListGroupSelected() {
        testToolsPresenterSpy.editingColumnEnabled = true;
        testToolsPresenterSpy.selectedListGroupItemView = selectedListGroupItemViewMock;
        testToolsPresenterSpy.selectedFieldItemView = null;
        testToolsPresenterSpy.onModifyColumn();
        verify(selectedListGroupItemViewMock, times(1)).getActualClassName();
        verify(selectedFieldItemViewMock, never()).getFullPath();
        verify(selectedFieldItemViewMock, never()).getFieldName();
        verify(selectedFieldItemViewMock, never()).getClassName();
        verify(eventBusMock, times(1)).fireEvent(isA(SetPropertyHeaderEvent.class));
        verify(eventBusMock, never()).fireEvent(isA(SetInstanceHeaderEvent.class));
    }

    @Test
    public void clearLists() {
        testToolsPresenterSpy.clearLists();
        verify(testToolsPresenterSpy, times(1)).clearDataObjectList();
        verify(testToolsPresenterSpy, times(1)).clearSimpleJavaTypeList();
        verify(testToolsPresenterSpy, times(1)).clearInstanceList();
        verify(testToolsPresenterSpy, times(1)).clearSimpleJavaInstanceFieldList();
    }

    @Test
    public void filterTerm() {
        String key = getRandomString();
        String search = String.join(";", IntStream.range(0, 4)
                .mapToObj(i -> getRandomString())
                .collect(Collectors.toSet()));
        assertTrue(testToolsPresenterSpy.filterTerm(key, key, false));
        assertFalse(testToolsPresenterSpy.filterTerm(key, key, true));

        assertFalse(testToolsPresenterSpy.filterTerm(key, search, false));
        assertTrue(testToolsPresenterSpy.filterTerm(key, search, true));

        search += ";" + key;
        assertTrue(testToolsPresenterSpy.filterTerm(key, search, false));
        assertFalse(testToolsPresenterSpy.filterTerm(key, search, true));
    }

    @Test
    public void resetTest() {
        testToolsPresenterSpy.reset();
        verify(testToolsViewMock, times(1)).reset();
        verify(listGroupItemPresenterMock, times(1)).reset();
    }

    @Test
    public void updateInstanceIsAssignedStatus_NotPresent() {
        String instance = "CHECK_INSTANCE";
        testToolsPresenterSpy.updateInstanceIsAssignedStatus(instance);
        verify(listGroupItemPresenterMock, times(1)).setInstanceAssigned(eq(instance), eq(false));
    }

    @Test
    public void updateInstanceIsAssignedStatus_Present() {
        String instance = "CHECK_INSTANCE";
        FactModelTree factModel = new FactModelTree(instance, FACT_PACKAGE, getMockSimpleProperties(), new HashMap<>());
        dataObjectFactTreeMap.put(instance, factModel);
        testToolsPresenterSpy.updateInstanceIsAssignedStatus(instance);
        verify(listGroupItemPresenterMock, times(1)).setInstanceAssigned(eq(instance), eq(true));
    }

    @Test
    public void updateInstanceIsAssignedStatus_EmptyString() {
        String instance = "";
        testToolsPresenterSpy.updateInstanceIsAssignedStatus(instance);
        verify(listGroupItemPresenterMock, never()).setInstanceAssigned(anyString(), anyBoolean());
    }

    @Test
    public void hideInstances() {
        testToolsPresenterSpy.hideInstances();
        verify(testToolsPresenterSpy, times(1)).clearInstanceList();
        verify(testToolsPresenterSpy, times(1)).clearSimpleJavaInstanceFieldList();
        verify(testToolsPresenterSpy, times(1)).showInstanceListContainerSeparator(eq(false));
    }

    @Test
    public void clearSelection_ListGroup() {
        testToolsPresenterSpy.selectedListGroupItemView = selectedListGroupItemViewMock;
        testToolsPresenterSpy.selectedFieldItemView = null;
        testToolsPresenterSpy.clearSelection();
        verify(selectedListGroupItemViewMock, times(1)).showCheck(eq(false));
        verify(testToolsViewMock, times(1)).disableAddButton();
    }

    @Test
    public void clearSelection_FieldItem() {
        testToolsPresenterSpy.selectedListGroupItemView = null;
        testToolsPresenterSpy.selectedFieldItemView = selectedFieldItemViewMock;
        testToolsPresenterSpy.clearSelection();
        verify(selectedFieldItemViewMock, times(1)).showCheck(eq(false));
        verify(testToolsViewMock, times(1)).disableAddButton();
    }
}