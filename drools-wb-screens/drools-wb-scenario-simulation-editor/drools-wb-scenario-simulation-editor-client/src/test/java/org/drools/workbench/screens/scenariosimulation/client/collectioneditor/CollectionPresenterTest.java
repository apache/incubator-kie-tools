/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.collectioneditor;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.editingbox.ItemEditingBoxPresenter;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.editingbox.KeyValueEditingBoxPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.ConfirmPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.ScenarioConfirmationPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CHILD_COUNT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ITEM_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.JSON_ARRAY_SIZE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.KEY_SET;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_JSON_STRING;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_KEY;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_PROPERTYNAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.UPDATED_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
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
public class CollectionPresenterTest extends AbstractCollectionEditorTest {

    @Mock
    private ItemElementPresenter listElementPresenterMock;

    @Mock
    private KeyValueElementPresenter mapElementPresenterMock;

    @Mock
    private ItemEditingBoxPresenter listEditingBoxPresenterMock;

    @Mock
    private KeyValueEditingBoxPresenter mapEditingBoxPresenterMock;

    @Mock
    private CollectionView collectionViewMock;

    private Map<String, Map<String, String>> instancePropertiesMapLocal = new HashMap<>();

    private Map<String, String> keyPropertyMapLocal = new HashMap<>();
    private Map<String, String> propertyMapLocal = new HashMap<>();

    @Mock
    private UListElement elementsContainerMock;

    @Mock
    private LIElement listEditingBoxMock;

    @Mock
    private LIElement mapEditingBoxMock;

    @Mock
    private LIElement itemElementMock;

    @Mock
    private JSONString jsonStringMock;

    @Mock
    private JSONValue jsonValueMock;

    @Mock
    private JSONArray jsonArrayMock;

    @Mock
    private JSONValue jsonValueChildMock;

    @Mock
    private JSONObject jsonObjectMock;

    @Mock
    private JSONValue jsonValueNeph1Mock;

    @Mock
    private JSONValue jsonValueNeph2Mock;

    @Mock
    private JSONString jsonStringProp1Mock;

    @Mock
    private JSONString jsonStringProp2Mock;

    @Mock
    private JSONObject nestedValue1Mock;

    @Mock
    private JSONObject nestedValue2Mock;

    @Mock
    private HeadingElement editorTitleMock;

    @Mock
    private SpanElement propertyTitleMock;

    @Mock
    private ScenarioConfirmationPopupPresenter scenarioConfirmationPopupPresenterMock;

    @Mock
    private ConfirmPopupPresenter confirmPopupPresenterMock;

    private CollectionPresenter collectionEditorPresenterSpy;

    @Before
    public void setup() {
        when(elementsContainerMock.getChildCount()).thenReturn(CHILD_COUNT);
        when(editorTitleMock.getInnerText()).thenReturn(TEST_KEY);
        when(collectionViewMock.getElementsContainer()).thenReturn(elementsContainerMock);
        when(collectionViewMock.getEditorTitle()).thenReturn(editorTitleMock);
        when(collectionViewMock.getPropertyTitle()).thenReturn(propertyTitleMock);

        when(nestedValue1Mock.keySet()).thenReturn(KEY_SET);
        when(nestedValue1Mock.get(eq("prop1"))).thenReturn(jsonValueNeph1Mock);
        when(nestedValue1Mock.get(eq("prop2"))).thenReturn(jsonValueNeph2Mock);

        when(nestedValue2Mock.keySet()).thenReturn(KEY_SET);
        when(nestedValue2Mock.get(eq("prop1"))).thenReturn(jsonValueNeph1Mock);
        when(nestedValue2Mock.get(eq("prop2"))).thenReturn(jsonValueNeph2Mock);

        when(jsonValueNeph1Mock.isString()).thenReturn(jsonStringProp1Mock);
        when(jsonValueNeph2Mock.isString()).thenReturn(jsonStringProp2Mock);
        when(jsonValueNeph1Mock.isObject()).thenReturn(nestedValue1Mock);
        when(jsonValueNeph2Mock.isObject()).thenReturn(nestedValue2Mock);

        when(jsonObjectMock.keySet()).thenReturn(KEY_SET);
        when(jsonObjectMock.get(eq("prop1"))).thenReturn(jsonValueNeph1Mock);
        when(jsonObjectMock.get(eq("prop2"))).thenReturn(jsonValueNeph2Mock);

        when(jsonValueChildMock.isObject()).thenReturn(jsonObjectMock);
        when(jsonArrayMock.size()).thenReturn(JSON_ARRAY_SIZE);
        when(jsonArrayMock.get(anyInt())).thenReturn(jsonValueChildMock);
        when(jsonValueMock.isArray()).thenReturn(jsonArrayMock);
        when(jsonValueMock.isObject()).thenReturn(jsonObjectMock);
        when(jsonValueMock.isString()).thenReturn(jsonStringMock);
        this.collectionEditorPresenterSpy = spy(new CollectionPresenter() {
            {
                this.viewsProvider = viewsProviderMock;
                this.listElementPresenter = listElementPresenterMock;
                this.mapElementPresenter = mapElementPresenterMock;
                this.listEditingBoxPresenter = listEditingBoxPresenterMock;
                this.mapEditingBoxPresenter = mapEditingBoxPresenterMock;
                this.simplePropertiesMap = instancePropertiesMapLocal;
                this.collectionView = collectionViewMock;
                this.scenarioConfirmationPopupPresenter = scenarioConfirmationPopupPresenterMock;
                this.confirmPopupPresenter = confirmPopupPresenterMock;
            }

            @Override
            protected JSONValue getJSONValue(String jsonString) {
                return jsonValueMock;
            }

            @Override
            protected String getListValue() {
                return UPDATED_VALUE;
            }

            @Override
            protected String getMapValue() {
                return UPDATED_VALUE;
            }

            @Override
            protected String getExpressionValue() {
                return UPDATED_VALUE;
            }
        });
        instancePropertiesMapLocal.clear();
        when(listElementPresenterMock.getItemContainer(anyString(), anyMap(), anyMap())).thenReturn(itemElementMock);
        when(mapElementPresenterMock.getKeyValueContainer(anyString(), anyMap(), anyMap())).thenReturn(itemElementMock);
        when(listEditingBoxPresenterMock.getEditingBox(anyString(), anyMap(), anyMap())).thenReturn(listEditingBoxMock);
        when(mapEditingBoxPresenterMock.getEditingBox(anyString(), anyMap(), anyMap())).thenReturn(mapEditingBoxMock);
    }

    @Test
    public void initListStructure() {
        collectionEditorPresenterSpy.initListStructure(TEST_KEY, propertyMapLocal, new HashMap<>(), collectionViewMock);
        verify(collectionEditorPresenterSpy, times(1)).commonInit(eq(TEST_KEY), eq(collectionViewMock));
        assertTrue(instancePropertiesMapLocal.containsKey(TEST_KEY));
        assertEquals(instancePropertiesMapLocal.get(TEST_KEY), propertyMapLocal);
        verify(listEditingBoxPresenterMock, times(1)).setCollectionEditorPresenter(eq(collectionEditorPresenterSpy));
        verify(listElementPresenterMock, times(1)).setCollectionEditorPresenter(eq(collectionEditorPresenterSpy));
    }

    @Test
    public void initMapStructure() {
        collectionEditorPresenterSpy.initMapStructure(TEST_KEY, keyPropertyMapLocal, propertyMapLocal, collectionViewMock);
        verify(collectionEditorPresenterSpy, times(1)).commonInit(eq(TEST_KEY), eq(collectionViewMock));
        assertTrue(instancePropertiesMapLocal.containsKey(TEST_KEY + "#key"));
        assertEquals(instancePropertiesMapLocal.get(TEST_KEY + "#key"), keyPropertyMapLocal);
        assertTrue(instancePropertiesMapLocal.containsKey(TEST_KEY + "#value"));
        assertEquals(instancePropertiesMapLocal.get(TEST_KEY + "#value"), propertyMapLocal);
        verify(mapEditingBoxPresenterMock, times(1)).setCollectionEditorPresenter(eq(collectionEditorPresenterSpy));
        verify(mapElementPresenterMock, times(1)).setCollectionEditorPresenter(eq(collectionEditorPresenterSpy));
    }

    @Test
    public void setValueIsListWidgetTrue() {
        commonSetValue(true, false);
    }

    @Test
    public void setValueIsListWidgetFalse() {
        commonSetValue(false, false);
    }

    @Test
    public void setValueIsExpressionWidgetList() {
        commonSetValue(true, true);
    }

    @Test
    public void setValueIsExpressionWidgetMap() {
        commonSetValue(false, true);
    }

    @Test
    public void showEditingBoxIsListWidgetTrue() {
        when(collectionViewMock.isListWidget()).thenReturn(true);
        collectionEditorPresenterSpy.showEditingBox();
        verify(collectionViewMock, times(1)).getElementsContainer();
        verify(listEditingBoxPresenterMock, times(1)).getEditingBox(eq(TEST_KEY), anyMap(), anyMap());
        verify(elementsContainerMock, times(1)).appendChild(eq(listEditingBoxMock));
    }

    @Test
    public void showEditingBoxIsListWidgetFalse() {
        when(collectionViewMock.isListWidget()).thenReturn(false);
        collectionEditorPresenterSpy.showEditingBox();
        verify(collectionViewMock, times(1)).getElementsContainer();
        verify(mapEditingBoxPresenterMock, times(1)).getEditingBox(eq(TEST_KEY), anyMap(), anyMap());
        verify(elementsContainerMock, times(1)).appendChild(eq(mapEditingBoxMock));
        verify(collectionEditorPresenterSpy, times(1)).toggleEditingStatus(eq(true));
        verify(listElementPresenterMock, times(1)).toggleEditingStatus(eq(true));
        verify(mapElementPresenterMock, times(1)).toggleEditingStatus(eq(true));
    }

    @Test
    public void onToggleRowExpansionIsShownTrue() {
        commonOnToggleRowExpansionIsShown(true, true);
        commonOnToggleRowExpansionIsShown(true, false);
    }

    @Test
    public void onToggleRowExpansionIsShownFalse() {
        commonOnToggleRowExpansionIsShown(false, true);
        commonOnToggleRowExpansionIsShown(false, false);
    }

    @Test
    public void addListItem() {
        collectionEditorPresenterSpy.addListItem(propertyMapLocal, new HashMap<>());
        verify(collectionViewMock, times(1)).getElementsContainer();
        verify(elementsContainerMock, times(1)).getChildCount();
        verify(listElementPresenterMock, times(1)).getItemContainer(eq(ITEM_ID), eq(propertyMapLocal), anyMap());
        verify(elementsContainerMock, times(1)).appendChild(eq(itemElementMock));
        verify(collectionEditorPresenterSpy, times(1)).toggleEditingStatus(eq(false));
    }

    @Test
    public void addMapItem() {
        collectionEditorPresenterSpy.addMapItem(keyPropertyMapLocal, propertyMapLocal);
        verify(collectionViewMock, times(1)).getElementsContainer();
        verify(elementsContainerMock, times(1)).getChildCount();
        verify(mapElementPresenterMock, times(1)).getKeyValueContainer(eq(ITEM_ID), eq(keyPropertyMapLocal), eq(propertyMapLocal));
        verify(elementsContainerMock, times(1)).appendChild(eq(itemElementMock));
        verify(collectionEditorPresenterSpy, times(1)).toggleEditingStatus(eq(false));
    }

    @Test
    public void saveIsListWidgetTrue() {
        commonSave(true, false,false);
        commonSave(true, false,true);
    }

    @Test
    public void saveIsListWidgetFalse() {
        commonSave(false, false,false);
        commonSave(false, false,true);
    }

    @Test
    public void saveIsExpressionWidget() {
        commonSave(false, true,false);
        commonSave(false, true,true);
        commonSave(true, true,false);
        commonSave(true, true,true);
    }

    @Test
    public void removeIsListWidgetTrue() {
        commonRemove(true);
    }

    @Test
    public void removeIsListWidgetFalse() {
        commonRemove(false);
    }

    @Test
    public void okRemoveCommandMethodIsListWidgetTrue() {
        commonOkRemoveCommandMethod(true);
    }

    @Test
    public void okRemoveCommandMethodIsListWidgetFalse() {
        commonOkRemoveCommandMethod(false);
    }

    @Test
    public void commonInit() {
        collectionEditorPresenterSpy.collectionView = null;
        collectionEditorPresenterSpy.commonInit(TEST_KEY, collectionViewMock);
        assertEquals(collectionEditorPresenterSpy.collectionView, collectionViewMock);
        verify(editorTitleMock, times(1)).setInnerText(TEST_KEY);
        verify(propertyTitleMock, times(1)).setInnerText(TEST_PROPERTYNAME);
    }

    @Test
    public void populateList() {
        collectionEditorPresenterSpy.populateList(jsonValueMock);
        for (int i = 0; i < JSON_ARRAY_SIZE; i++) {
            verify(jsonArrayMock, times(1)).get(eq(i));
        }
        verify(collectionEditorPresenterSpy, times(JSON_ARRAY_SIZE * 3)).getSimplePropertiesMap(any()); // Multiply x 3 because getSimplePropertiesMap is called by getExpandablePropertiesValues
        verify(collectionEditorPresenterSpy, times(JSON_ARRAY_SIZE)).getExpandablePropertiesValues(any());
        verify(collectionEditorPresenterSpy, times(JSON_ARRAY_SIZE)).addListItem(anyMap(), anyMap());
    }

    @Test
    public void populateMap() {
        collectionEditorPresenterSpy.populateMap(jsonValueMock);
        verify(collectionEditorPresenterSpy, times(JSON_ARRAY_SIZE)).addMapItem(anyMap(), anyMap());
    }

    @Test
    public void toggleEditingStatusToDisableTrue() {
        collectionEditorPresenterSpy.toggleEditingStatus(true);
        verify(collectionViewMock, times(1)).enableEditingMode(eq(true));
        verify(listElementPresenterMock, times(1)).toggleEditingStatus(eq(true));
        verify(mapElementPresenterMock, times(1)).toggleEditingStatus(eq(true));
    }

    @Test
    public void toggleEditingStatusToDisableFalse() {
        collectionEditorPresenterSpy.toggleEditingStatus(false);
        verify(collectionViewMock, times(1)).enableEditingMode(eq(false));
        verify(listElementPresenterMock, times(1)).toggleEditingStatus(eq(false));
        verify(mapElementPresenterMock, times(1)).toggleEditingStatus(eq(false));
    }

    private void commonRemove(boolean isWidget) {
        when(collectionViewMock.isListWidget()).thenReturn(isWidget);
        collectionEditorPresenterSpy.remove();
        verify(scenarioConfirmationPopupPresenterMock, times(1)).show(
                eq(ScenarioSimulationEditorConstants.INSTANCE.removeCollectionMainTitle()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.removeCollectionMainQuestion()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.removeCollectionText1()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.removeCollectionQuestion()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.removeCollectionWarningText()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.remove()),
                isA(Command.class));
        verify(mapElementPresenterMock, never()).remove();
        verify(listElementPresenterMock, never()).remove();
    }

    private void commonOkRemoveCommandMethod(boolean isListWidget) {
        when(collectionViewMock.isListWidget()).thenReturn(isListWidget);
        collectionEditorPresenterSpy.okRemoveCommandMethod();
        if (isListWidget) {
            verify(listElementPresenterMock, times(1)).remove();
            verify(mapElementPresenterMock, never()).remove();
        } else {
            verify(mapElementPresenterMock, times(1)).remove();
            verify(listElementPresenterMock, never()).remove();
        }
        verify(collectionViewMock, times(1)).updateValue(eq(null));
        verify(collectionViewMock, times(1)).close();
    }

    private void commonSetValue(boolean isListWidget, boolean isExpressionList) {
        collectionEditorPresenterSpy.setValue(null);
        verify(collectionEditorPresenterSpy, never()).getJSONValue(anyString());
        reset(collectionEditorPresenterSpy);
        collectionEditorPresenterSpy.setValue("");
        verify(collectionEditorPresenterSpy, never()).getJSONValue(anyString());
        reset(collectionEditorPresenterSpy);
        when(collectionViewMock.isListWidget()).thenReturn(isListWidget);
        when(collectionViewMock.isExpressionWidget()).thenReturn(isExpressionList);
        if (isExpressionList) {
           when(collectionEditorPresenterSpy.getJSONValue(TEST_JSON_STRING)).thenReturn(new JSONString(TEST_JSON_STRING));
        }
        collectionEditorPresenterSpy.setValue(TEST_JSON_STRING);
        if (isExpressionList) {
            verify(collectionEditorPresenterSpy, times(1)).populateExpression(isA(JSONValue.class));
            verify(collectionEditorPresenterSpy, never()).populateCreateCollection(any());
        } else {
            if (isListWidget) {
                verify(collectionEditorPresenterSpy, times(1)).populateCreateCollection(isA(JSONValue.class));
                verify(collectionEditorPresenterSpy, never()).populateExpression(any());
            } else {
                verify(collectionEditorPresenterSpy, times(1)).populateCreateCollection(isA(JSONValue.class));
                verify(collectionEditorPresenterSpy, never()).populateExpression(any());
            }
        }
    }

    private void commonOnToggleRowExpansionIsShown(boolean isShown, boolean isListWidget) {
        when(collectionViewMock.isListWidget()).thenReturn(isListWidget);
        collectionEditorPresenterSpy.onToggleRowExpansion(isShown);
        verify(collectionViewMock, times(1)).toggleRowExpansion();
        if (isListWidget) {
            verify(listElementPresenterMock, times(1)).onToggleRowExpansion(eq(isShown));
        } else {
            verify(mapElementPresenterMock, times(1)).onToggleRowExpansion(eq(isShown));
        }
        reset(collectionViewMock);
        reset(listElementPresenterMock);
        reset(mapElementPresenterMock);
    }

    private void commonSave(boolean isListWidget, boolean isExpressionWidget, boolean throwException) {
        when(collectionViewMock.isListWidget()).thenReturn(isListWidget);
        when(collectionViewMock.isExpressionWidget()).thenReturn(isExpressionWidget);
        if (throwException) {
            if(isExpressionWidget) {
                when(collectionEditorPresenterSpy.getExpressionValue()).thenThrow(IllegalStateException.class);
            } else {
                if (isListWidget) {
                    when(collectionEditorPresenterSpy.getListValue()).thenThrow(IllegalStateException.class);
                } else {
                    when(collectionEditorPresenterSpy.getMapValue()).thenThrow(IllegalStateException.class);
                }
            }
        }
        collectionEditorPresenterSpy.save();
        if (isExpressionWidget) {
            verify(collectionEditorPresenterSpy, times(1)).getExpressionValue();
            verify(collectionEditorPresenterSpy, never()).getValueFromCreateCollection();
        } else {
            if (isListWidget) {
                verify(collectionEditorPresenterSpy, times(1)).getValueFromCreateCollection();
                verify(collectionEditorPresenterSpy, never()).getExpressionValue();
            } else {
                verify(collectionEditorPresenterSpy, times(1)).getValueFromCreateCollection();
                verify(collectionEditorPresenterSpy, never()).getExpressionValue();
            }
        }
        if (throwException) {
            verify(confirmPopupPresenterMock, times(1)).show(eq(ScenarioSimulationEditorConstants.INSTANCE.collectionError()), anyString());
            verify(collectionViewMock, never()).updateValue(anyString());
        } else {
            verify(confirmPopupPresenterMock, never()).show(eq(ScenarioSimulationEditorConstants.INSTANCE.collectionError()), anyString());
            verify(collectionViewMock, times(1)).updateValue(eq(UPDATED_VALUE));
        }
        reset(confirmPopupPresenterMock);
        reset(collectionViewMock);
        reset(collectionEditorPresenterSpy);
    }

    @Test
    public void populateExpression() {
        when(jsonStringMock.stringValue()).thenReturn("test-string");
        collectionEditorPresenterSpy.populateExpression(jsonValueMock);
        verify(collectionViewMock, times(1)).setExpression("test-string");
    }
}
